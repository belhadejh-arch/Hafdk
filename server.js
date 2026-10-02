import 'dotenv/config';
import crypto from 'node:crypto';
import path from 'node:path';
import { fileURLToPath } from 'node:url';
import bcrypt from 'bcryptjs';
import connectPgSimple from 'connect-pg-simple';
import express from 'express';
import { rateLimit } from 'express-rate-limit';
import session from 'express-session';
import helmet from 'helmet';
import { Pool } from 'pg';

const app = express();
const port = Number(process.env.PORT || 5000);
const isProduction = process.env.NODE_ENV === 'production';
const subscriptionPlanDays = new Map([
  ['MONTHLY 6', 180],
  ['YEAR', 365],
  ['TWO YEARS', 730],
  ['LIFETIME', null]
]);
const allowedReceiptContentTypes = new Set([
  'image/jpeg',
  'image/png',
  'image/webp',
  'application/pdf'
]);
const publicUserColumns = `
  id, username, email, role, phone, full_name, beneficiary_type, subscription_plan,
  status, registration_date, start_date, end_date, license_key, receipt_file_name,
  receipt_content_type, (receipt_content IS NOT NULL) AS has_receipt, created_at
`;
function normalizeOrigin(value) {
  try {
    return new URL(value).origin;
  } catch {
    return '';
  }
}

const frontendOrigins = (process.env.FRONTEND_ORIGIN || '')
  .split(',')
  .map((origin) => normalizeOrigin(origin.trim()))
  .filter(Boolean);
if (isProduction) {
  frontendOrigins.push(normalizeOrigin('https://hafdk.vercel.app'));
}
const projectDir = path.dirname(fileURLToPath(import.meta.url));
const poolOptions = { connectionString: process.env.DATABASE_URL };
if (process.env.DB_SSL === 'true') {
  poolOptions.ssl = { rejectUnauthorized: false };
}
const pool = new Pool(poolOptions);
const PgSession = connectPgSimple(session);

if (!process.env.DATABASE_URL) {
  throw new Error('DATABASE_URL is required.');
}
if (!process.env.SESSION_SECRET || process.env.SESSION_SECRET.length < 32) {
  throw new Error('SESSION_SECRET is required and must be at least 32 characters.');
}

app.set('trust proxy', 1);
app.disable('x-powered-by');
app.use(helmet({ contentSecurityPolicy: false, crossOriginEmbedderPolicy: false }));
app.use((req, res, next) => {
  const origin = normalizeOrigin(req.get('origin') || '');
  if (origin && ((isProduction && !frontendOrigins.includes(origin)) ||
      (!isProduction && frontendOrigins.length && !frontendOrigins.includes(origin)))) {
    return res.status(403).json({ error: 'Origin not allowed.' });
  }
  if (origin) {
    res.setHeader('Access-Control-Allow-Origin', origin);
    res.setHeader('Vary', 'Origin');
    res.setHeader('Access-Control-Allow-Credentials', 'true');
    res.setHeader('Access-Control-Allow-Headers', 'Content-Type');
    res.setHeader('Access-Control-Allow-Methods', 'GET, POST, PATCH, OPTIONS');
  }
  if (req.method === 'OPTIONS') return res.sendStatus(204);
  next();
});
app.use(express.json({ limit: '32kb' }));
app.use(session({
  name: 'haafedk.sid',
  secret: process.env.SESSION_SECRET,
  resave: false,
  saveUninitialized: false,
  store: new PgSession({
    pool,
    tableName: 'user_sessions',
    createTableIfMissing: true
  }),
  cookie: {
    httpOnly: true,
    secure: isProduction,
    sameSite: 'lax',
    maxAge: 1000 * 60 * 60 * 24 * 14
  }
}));

const authLimiter = rateLimit({
  windowMs: 15 * 60 * 1000,
  limit: 20,
  standardHeaders: 'draft-8',
  legacyHeaders: false
});

function publicUser(row) {
  if (!row) return null;
  return {
    id: row.id,
    username: row.username,
    email: row.email,
    role: row.role,
    phone: row.phone || '',
    fullName: row.full_name || '',
    beneficiaryType: row.beneficiary_type || 'لنفسي',
    subscriptionPlan: row.subscription_plan || 'MONTHLY 6',
    status: row.status,
    registrationDate: row.registration_date instanceof Date
      ? row.registration_date.toISOString().slice(0, 10)
      : String(row.registration_date).slice(0, 10),
    startDate: row.start_date ? String(row.start_date).slice(0, 10) : null,
    endDate: row.end_date ? String(row.end_date).slice(0, 10) : null,
    licenseKey: row.license_key || null,
    receiptFileName: row.receipt_file_name || null,
    receiptMimeType: row.receipt_content_type || null,
    hasReceipt: Boolean(row.has_receipt ?? row.receipt_content),
    createdAt: row.created_at ? new Date(row.created_at).toISOString() : null
  };
}

async function requireUser(req, res, next) {
  if (!req.session.userId) return res.status(401).json({ error: 'يجب تسجيل الدخول أولاً.' });
  try {
    const result = await pool.query('SELECT status FROM users WHERE id = $1', [req.session.userId]);
    const user = result.rows[0];
    if (!user) {
      req.session.destroy(() => {});
      res.clearCookie('haafedk.sid', { httpOnly: true, secure: isProduction, sameSite: 'lax' });
      return res.status(401).json({ error: 'انتهت صلاحية الجلسة.' });
    }
    if (user.status === 'SUSPENDED' || user.status === 'BANNED') {
      req.session.destroy(() => {});
      res.clearCookie('haafedk.sid', { httpOnly: true, secure: isProduction, sameSite: 'lax' });
      return res.status(403).json({
        error: user.status === 'BANNED' ? 'تم حظر الحساب نهائياً.' : 'الحساب موقوف مؤقتاً.'
      });
    }
    next();
  } catch (error) {
    next(error);
  }
}

async function requireAdmin(req, res, next) {
  if (!req.session.userId) return res.status(401).json({ error: 'يجب تسجيل الدخول أولاً.' });
  try {
    const result = await pool.query('SELECT role FROM users WHERE id = $1', [req.session.userId]);
    if (result.rows[0]?.role !== 'ADMIN') {
      return res.status(403).json({ error: 'هذه العملية متاحة للمدير فقط.' });
    }
    next();
  } catch (error) {
    next(error);
  }
}

function regenerateSession(req) {
  return new Promise((resolve, reject) => {
    req.session.regenerate((error) => error ? reject(error) : resolve());
  });
}

app.get('/api/health', async (_req, res, next) => {
  try {
    await pool.query('SELECT 1');
    res.json({ status: 'ok', database: 'connected' });
  } catch (error) {
    next(error);
  }
});

app.post('/api/auth/register', authLimiter, async (req, res, next) => {
  const username = String(req.body?.username || '').trim().toLowerCase();
  const email = String(req.body?.email || '').trim().toLowerCase();
  const password = String(req.body?.password || '');
  if (!/^[a-z0-9_]{3,32}$/.test(username)) {
    return res.status(400).json({ error: 'اسم المستخدم يجب أن يكون من 3 إلى 32 حرفاً أو رقماً أو _.' });
  }
  if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email) || email.length > 254) {
    return res.status(400).json({ error: 'البريد الإلكتروني غير صالح.' });
  }
  if (password.length < 12 || password.length > 128) {
    return res.status(400).json({ error: 'كلمة المرور يجب أن تكون بين 12 و128 حرفاً.' });
  }

  try {
    const passwordHash = await bcrypt.hash(password, 11);
    const result = await pool.query(
      `INSERT INTO users (id, username, email, password_hash, role, status)
       VALUES ($1, $2, $3, $4, 'USER', 'INACTIVE')
       RETURNING *`,
      [crypto.randomUUID(), username, email, passwordHash]
    );
    await regenerateSession(req);
    req.session.userId = result.rows[0].id;
    res.status(201).json({ user: publicUser(result.rows[0]) });
  } catch (error) {
    if (error.code === '23505') {
      return res.status(409).json({ error: 'اسم المستخدم أو البريد الإلكتروني مستخدم بالفعل.' });
    }
    next(error);
  }
});

app.post('/api/auth/login', authLimiter, async (req, res, next) => {
  const login = String(req.body?.username || '').trim().toLowerCase();
  const password = String(req.body?.password || '');
  if (!login || !password || login.length > 254 || password.length > 128) {
    return res.status(400).json({ error: 'أدخل اسم المستخدم وكلمة المرور.' });
  }

  try {
    const result = await pool.query(
      `SELECT ${publicUserColumns}, password_hash
       FROM users WHERE lower(username) = $1 OR lower(email) = $1 LIMIT 1`,
      [login]
    );
    const row = result.rows[0];
    if (!row || !(await bcrypt.compare(password, row.password_hash))) {
      return res.status(401).json({ error: 'اسم المستخدم أو كلمة المرور غير صحيحة.' });
    }
    if (row.status === 'BANNED') {
      return res.status(403).json({ error: 'تم حظر الحساب نهائياً.' });
    }
    if (row.status === 'SUSPENDED') {
      return res.status(403).json({ error: 'الحساب موقوف مؤقتاً. تواصل مع الدعم.' });
    }
    await regenerateSession(req);
    req.session.userId = row.id;
    res.json({ user: publicUser(row) });
  } catch (error) {
    next(error);
  }
});

app.get('/api/auth/me', async (req, res, next) => {
  if (!req.session.userId) return res.json({ user: null });
  try {
    const result = await pool.query(
      `SELECT ${publicUserColumns} FROM users WHERE id = $1`,
      [req.session.userId]
    );
    if (!result.rows[0]) {
      req.session.destroy(() => {});
      return res.status(401).json({ error: 'انتهت صلاحية الجلسة.' });
    }
    if (result.rows[0].status === 'BANNED' || result.rows[0].status === 'SUSPENDED') {
      const status = result.rows[0].status;
      req.session.destroy(() => {});
      res.clearCookie('haafedk.sid', { httpOnly: true, secure: isProduction, sameSite: 'lax' });
      return res.status(403).json({
        error: status === 'BANNED' ? 'تم حظر الحساب نهائياً.' : 'الحساب موقوف مؤقتاً.'
      });
    }
    res.json({ user: publicUser(result.rows[0]) });
  } catch (error) {
    next(error);
  }
});

app.post('/api/auth/logout', requireUser, (req, res, next) => {
  req.session.destroy((error) => {
    if (error) return next(error);
    res.clearCookie('haafedk.sid', { httpOnly: true, secure: isProduction, sameSite: 'lax' });
    res.json({ ok: true });
  });
});

app.patch('/api/account/activation', requireUser, async (req, res, next) => {
  const fullName = String(req.body?.fullName || '').trim().slice(0, 120);
  const phone = String(req.body?.phone || '').trim().slice(0, 40);
  const beneficiaryType = String(req.body?.beneficiaryType || '');
  const plan = String(req.body?.subscriptionPlan || '');
  const receiptFileName = String(req.body?.receiptFileName || '').replace(/[\\/\u0000-\u001f]/g, '').slice(0, 180) || null;
  if (!fullName || !phone || !['لنفسي', 'شخص آخر'].includes(beneficiaryType) ||
      !subscriptionPlanDays.has(plan)) {
    return res.status(400).json({ error: 'تحقق من الاسم والهاتف والخطة.' });
  }
  try {
    const result = await pool.query(
      `UPDATE users SET full_name = $1, phone = $2, beneficiary_type = $3,
       subscription_plan = $4, receipt_file_name = $5, status = 'PENDING',
       updated_at = NOW()
       WHERE id = $6 RETURNING ${publicUserColumns}`,
      [fullName, phone, beneficiaryType, plan, receiptFileName, req.session.userId]
    );
    if (!result.rows[0]) return res.status(404).json({ error: 'الحساب غير موجود.' });
    res.json({ user: publicUser(result.rows[0]) });
  } catch (error) {
    next(error);
  }
});

app.put('/api/account/receipt', requireUser, express.raw({
  type: [...allowedReceiptContentTypes],
  limit: '5mb'
}), async (req, res, next) => {
  const contentType = String(req.get('content-type') || '').split(';')[0].trim().toLowerCase();
  if (!allowedReceiptContentTypes.has(contentType)) {
    return res.status(415).json({ error: 'ارفع صورة JPG أو PNG أو WebP أو ملف PDF.' });
  }
  if (!Buffer.isBuffer(req.body) || req.body.length === 0) {
    return res.status(400).json({ error: 'الملف المرفوع فارغ.' });
  }

  let receiptFileName = String(req.get('x-receipt-filename') || '');
  try {
    receiptFileName = decodeURIComponent(receiptFileName);
  } catch {
    return res.status(400).json({ error: 'اسم الملف غير صالح.' });
  }
  receiptFileName = receiptFileName.replace(/[\\/\u0000-\u001f\u007f]/g, '').slice(0, 180) || 'receipt';

  try {
    const result = await pool.query(
      `UPDATE users
       SET receipt_file_name = $1, receipt_content = $2, receipt_content_type = $3, updated_at = NOW()
       WHERE id = $4 AND status NOT IN ('SUSPENDED', 'BANNED')
       RETURNING ${publicUserColumns}`,
      [receiptFileName, req.body, contentType, req.session.userId]
    );
    if (!result.rows[0]) return res.status(404).json({ error: 'الحساب غير موجود.' });
    res.json({ user: publicUser(result.rows[0]) });
  } catch (error) {
    next(error);
  }
});

app.get('/api/admin/users', requireAdmin, async (_req, res, next) => {
  try {
    const result = await pool.query(
      `SELECT ${publicUserColumns}
       FROM users WHERE role = 'USER' ORDER BY created_at DESC`
    );
    res.json({ users: result.rows.map(publicUser) });
  } catch (error) {
    next(error);
  }
});

app.get('/api/admin/users/:id/receipt', requireAdmin, async (req, res, next) => {
  try {
    const result = await pool.query(
      `SELECT receipt_content, receipt_content_type, receipt_file_name
       FROM users WHERE id = $1 AND role = 'USER'`,
      [req.params.id]
    );
    const receipt = result.rows[0];
    if (!receipt?.receipt_content || !allowedReceiptContentTypes.has(receipt.receipt_content_type)) {
      return res.status(404).json({ error: 'إيصال المستخدم غير متوفر.' });
    }
    res.setHeader('Content-Type', receipt.receipt_content_type);
    res.setHeader('Content-Disposition', `inline; filename*=UTF-8''${encodeURIComponent(receipt.receipt_file_name || 'receipt')}`);
    res.setHeader('Cache-Control', 'private, no-store');
    res.setHeader('X-Content-Type-Options', 'nosniff');
    res.send(receipt.receipt_content);
  } catch (error) {
    next(error);
  }
});

app.patch('/api/admin/users/:id', requireAdmin, async (req, res, next) => {
  const { action } = req.body || {};
  try {
    let result;
    if (action === 'ban') {
      result = await pool.query(
        `UPDATE users SET status = 'BANNED', updated_at = NOW()
         WHERE id = $1 AND role = 'USER' RETURNING ${publicUserColumns}`,
        [req.params.id]
      );
    } else if (action === 'suspend') {
      result = await pool.query(
        `UPDATE users SET status = 'SUSPENDED', updated_at = NOW()
         WHERE id = $1 AND role = 'USER' AND status <> 'BANNED'
         RETURNING ${publicUserColumns}`,
        [req.params.id]
      );
    } else if (action === 'resume') {
      result = await pool.query(
        `UPDATE users SET status = 'ACTIVE', updated_at = NOW()
         WHERE id = $1 AND role = 'USER' AND status = 'SUSPENDED'
           AND (subscription_plan = 'LIFETIME' OR end_date >= CURRENT_DATE)
         RETURNING ${publicUserColumns}`,
        [req.params.id]
      );
    } else if (action === 'activate' || action === 'renew') {
      const plan = String(req.body?.plan || '');
      if (!subscriptionPlanDays.has(plan)) {
        return res.status(400).json({ error: 'اختر خطة اشتراك صحيحة.' });
      }
      const now = new Date();
      const durationDays = subscriptionPlanDays.get(plan);
      const endDate = durationDays === null ? null : new Date(now);
      if (endDate) endDate.setUTCDate(endDate.getUTCDate() + durationDays);
      const licenseKey = `HFD-${crypto.randomBytes(6).toString('hex').toUpperCase()}`;
      result = await pool.query(
        `UPDATE users SET status = 'ACTIVE', subscription_plan = $1,
         start_date = $2, end_date = $3, license_key = $4, updated_at = NOW()
         WHERE id = $5 AND role = 'USER' AND status <> 'BANNED'
         RETURNING ${publicUserColumns}`,
        [
          plan,
          now.toISOString().slice(0, 10),
          endDate ? endDate.toISOString().slice(0, 10) : null,
          licenseKey,
          req.params.id
        ]
      );
    } else {
      return res.status(400).json({ error: 'الإجراء غير معروف.' });
    }
    if (!result.rows[0]) {
      return res.status(404).json({
        error: action === 'resume'
          ? 'تعذر استئناف الاشتراك؛ تحقق من حالة الحساب وتاريخ انتهائه.'
          : 'المستخدم غير موجود أو محظور نهائياً.'
      });
    }
    res.json({ user: publicUser(result.rows[0]) });
  } catch (error) {
    next(error);
  }
});

app.use('/api', (_req, res) => res.status(404).json({ error: 'المسار غير موجود.' }));
app.get('/devicesData.js', (_req, res) => {
  res.sendFile(path.join(projectDir, 'devicesData.js'));
});
app.get('/data/supportedDevices.json', (_req, res) => {
  res.sendFile(path.join(projectDir, 'data', 'supportedDevices.json'));
});
app.use(express.static(path.join(projectDir, 'public')));
app.get(/.*/, (_req, res) => res.sendFile(path.join(projectDir, 'public', 'index.html')));

app.use((error, _req, res, _next) => {
  if (error.code === '23505') {
    return res.status(409).json({ error: 'السجل موجود مسبقاً.' });
  }
  if (error.status === 413 || error.type === 'entity.too.large') {
    return res.status(413).json({ error: 'الحد الأقصى لحجم الملف 5 ميغابايت.' });
  }
  console.error('Request failed:', error.message);
  res.status(500).json({ error: 'حدث خطأ داخلي. حاول مرة أخرى.' });
});

const server = app.listen(port, '0.0.0.0', () => {
  console.log(`Haafedk API listening on port ${port}`);
});

async function shutdown() {
  server.close(async () => {
    await pool.end();
    process.exit(0);
  });
}
process.on('SIGTERM', shutdown);
process.on('SIGINT', shutdown);