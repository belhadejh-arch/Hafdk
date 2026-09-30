import fs from 'node:fs/promises';
import path from 'node:path';
import { fileURLToPath } from 'node:url';

const projectDir = path.dirname(path.dirname(fileURLToPath(import.meta.url)));
const outputDir = path.join(projectDir, 'dist');
await fs.rm(outputDir, { recursive: true, force: true });
await fs.mkdir(outputDir, { recursive: true });
await fs.cp(path.join(projectDir, 'public'), outputDir, { recursive: true });
await fs.copyFile(path.join(projectDir, 'devicesData.js'), path.join(outputDir, 'devicesData.js'));
console.log('Vercel static frontend built in dist/.');