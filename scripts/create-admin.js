import 'dotenv/config';
import crypto from 'node:crypto';
import bcrypt from 'bcryptjs';
import { Pool } from 'pg';

const username = String(process.env.ADMIN_USERNAME || '').trim().toLowerCase();
const email = String(process.env.ADMIN_EMAIL || '').trim().toLowerCase();
const password = String(process.env.ADMIN_PASSWORD || '');
if (!process.env.DATABASE_URL) throw new Error('DATABASE_URL is required.');
if (!username && !email && !password) {
  console.log('Admin bootstrap skipped; set ADMIN_USERNAME, ADMIN_EMAIL, and ADMIN_PASSWORD to create the initial admin.');
  process.exit(0);
}
if (!/^[a-z0-9_]{3,32}$/.test(username)) {
  throw new Error('Set ADMIN_USERNAME (3–32 letters, numbers, or underscores).');
}
if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
  throw new Error('Set a valid ADMIN_EMAIL.');
}
if (password.length < 12 || password.length > 128) {
  throw new Error('Set ADMIN_PASSWORD to a password between 12 and 128 characters.');
}

const poolOptions = { connectionString: process.env.DATABASE_URL };
if (process.env.DB_SSL === 'true') poolOptions.ssl = { rejectUnauthorized: false };
const pool = new Pool(poolOptions);

try {
  const existing = await pool.query(
    `SELECT username, email, role FROM users
     WHERE lower(username) = $1 OR lower(email) = $2 LIMIT 1`,
    [username, email]
  );
  if (existing.rows[0]) {
    const row = existing.rows[0];
    if (row.role === 'ADMIN' && row.username === username && row.email === email) {
      console.log(`Admin already exists: ${row.username} (${row.email}); no changes made.`);
    } else {
      throw new Error('The requested admin username or email is already used by another account.');
    }
  } else {
  const passwordHash = await bcrypt.hash(password, 12);
  const result = await pool.query(
    `INSERT INTO users (id, username, email, password_hash, role, status)
     VALUES ($1, $2, $3, $4, 'ADMIN', 'ACTIVE')
     RETURNING id, username, email, role`,
    [crypto.randomUUID(), username, email, passwordHash]
  );
  console.log(`Admin created: ${result.rows[0].username} (${result.rows[0].email}).`);
  }
} catch (error) {
  if (error.code === '23505') {
    console.error('Admin was not created: that username or email is already registered.');
    process.exitCode = 1;
  } else {
    throw error;
  }
} finally {
  await pool.end();
}