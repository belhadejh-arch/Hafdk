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
const frontendOrigins = (process.env.FRONTEND_ORIGIN || '')
  .split(',')
  .map((origin) => origin.trim())
  .filter(Boolean);
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
  const origin = req.get('origin');
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
    receiptFileName: row.receipt_file_name || null
  };
}

function requireUser(req, res, next) {
  if (!req.session.userId) return res.status(401).json({ error: 'يجب تسجيل الدخول أولاً.' });
  next();
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
    const passwordHash = await bcrypt.hash(password, 12);
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
      'SELECT * FROM users WHERE lower(username) = $1 OR lower(email) = $1 LIMIT 1',
      [login]
    );
    const row = result.rows[0];
    if (!row || !(await bcrypt.compare(password, row.password_hash))) {
      return res.status(401).json({ error: 'اسم المستخدم أو كلمة المرور غير صحيحة.' });
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
    const result = await pool.query('SELECT * FROM users WHERE id = $1', [req.session.userId]);
    if (!result.rows[0]) {
      req.session.destroy(() => {});
      return res.status(401).json({ error: 'انتهت صلاحية الجلسة.' });
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
      !['MONTHLY 6', 'YEAR'].includes(plan)) {
    return res.status(400).json({ error: 'تحقق من الاسم والهاتف والخطة.' });
  }
  try {
    const result = await pool.query(
      `UPDATE users SET full_name = $1, phone = $2, beneficiary_type = $3,
       subscription_plan = $4, receipt_file_name = $5, status = 'PENDING',
       updated_at = NOW()
       WHERE id = $6 RETURNING *`,
      [fullName, phone, beneficiaryType, plan, receiptFileName, req.session.userId]
    );
    if (!result.rows[0]) return res.status(404).json({ error: 'الحساب غير موجود.' });
    res.json({ user: publicUser(result.rows[0]) });
  } catch (error) {
    next(error);
  }
});

app.get('/api/admin/users', requireAdmin, async (_req, res, next) => {
  try {
    const result = await pool.query("SELECT * FROM users WHERE role = 'USER' ORDER BY created_at DESC");
    res.json({ users: result.rows.map(publicUser) });
  } catch (error) {
    next(error);
  }
});

app.patch('/api/admin/users/:id', requireAdmin, async (req, res, next) => {
  const { action } = req.body || {};
  try {
    let result;
    if (action === 'suspend') {
      result = await pool.query(
        `UPDATE users SET status = 'SUSPENDED', updated_at = NOW()
         WHERE id = $1 AND role = 'USER' RETURNING *`,
        [req.params.id]
      );
    } else if (action === 'activate' || action === 'renew') {
      const months = Number(req.body?.months);
      if (![6, 12].includes(months)) {
        return res.status(400).json({ error: 'اختر 6 أو 12 شهراً.' });
      }
      const days = months === 6 ? 180 : 365;
      const now = new Date();
      const endDate = new Date(now);
      endDate.setUTCDate(endDate.getUTCDate() + days);
      const licenseKey = `HFD-${crypto.randomBytes(6).toString('hex').toUpperCase()}`;
      result = await pool.query(
        `UPDATE users SET status = 'ACTIVE', subscription_plan = $1,
         start_date = $2, end_date = $3, license_key = $4, updated_at = NOW()
         WHERE id = $5 AND role = 'USER' RETURNING *`,
        [
          months === 6 ? 'MONTHLY 6' : 'YEAR',
          now.toISOString().slice(0, 10),
          endDate.toISOString().slice(0, 10),
          licenseKey,
          req.params.id
        ]
      );
    } else {
      return res.status(400).json({ error: 'الإجراء غير معروف.' });
    }
    if (!result.rows[0]) return res.status(404).json({ error: 'المستخدم غير موجود.' });
    res.json({ user: publicUser(result.rows[0]) });
  } catch (error) {
    next(error);
  }
});

app.use('/api', (_req, res) => res.status(404).json({ error: 'المسار غير موجود.' }));
app.get('/devicesData.js', (_req, res) => {
  res.sendFile(path.join(projectDir, 'devicesData.js'));
});
app.use(express.static(path.join(projectDir, 'public')));
app.get(/.*/, (_req, res) => res.sendFile(path.join(projectDir, 'public', 'index.html')));

app.use((error, _req, res, _next) => {
  if (error.code === '23505') {
    return res.status(409).json({ error: 'السجل موجود مسبقاً.' });
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