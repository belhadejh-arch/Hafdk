import 'dotenv/config';
import fs from 'node:fs/promises';
import path from 'node:path';
import { fileURLToPath } from 'node:url';
import { Pool } from 'pg';

if (!process.env.DATABASE_URL) {
  throw new Error('DATABASE_URL is required to run database migrations.');
}

const projectDir = path.dirname(path.dirname(fileURLToPath(import.meta.url)));
const poolOptions = { connectionString: process.env.DATABASE_URL };
if (process.env.DB_SSL === 'true') poolOptions.ssl = { rejectUnauthorized: false };
const pool = new Pool(poolOptions);

try {
  const schema = await fs.readFile(path.join(projectDir, 'db', 'schema.sql'), 'utf8');
  await pool.query(schema);
  console.log('Database schema is up to date.');
} finally {
  await pool.end();
}