# Haafedk project setup

## Project layout

- The web app is the static site in `public/`.
- `server.js` is the Node.js API and serves the same site for local preview.
- PostgreSQL holds web accounts and subscriptions. Passwords are stored as bcrypt hashes.
- The Android app remains a separate native app and has not yet been connected to this API.

## Run and build commands

Use Node.js 20 or newer.

```bash
npm install
npm run dev
```

`npm run dev` applies the idempotent database schema, optionally creates the first administrator if the `ADMIN_*` variables are set, then starts the local server on port 5000. Set `DATABASE_URL` and `SESSION_SECRET` using Replit Secrets for local preview; do not commit a `.env` file.

Other commands:

```bash
npm run build       # Build the Vercel static output in dist/
npm run db:migrate  # Apply db/schema.sql to DATABASE_URL
npm run admin:create
npm start           # Migrate, bootstrap admin if configured, and start the API
```

## Deploy the API and database on Render

1. Create a PostgreSQL database in Render. In the API service, set `DATABASE_URL` to that database's **internal** connection string.
2. Create a Render Web Service from this repository using:
   - Runtime: Node
   - Build command: `npm install`
   - Start command: `npm start`
   - Health-check path: `/api/health`
3. Add these environment variables to the Render service:
   - `NODE_ENV=production`
   - `SESSION_SECRET`: generate a new random value, for example with `openssl rand -base64 48`
   - `ADMIN_USERNAME`, `ADMIN_EMAIL`, and `ADMIN_PASSWORD`: set the administrator account you want. The password must be at least 12 characters.
   - `FRONTEND_ORIGIN`: set this to the exact Vercel site origin after the Vercel deployment exists, without a trailing slash.
   - `DB_SSL=true` only if the PostgreSQL provider requires TLS.
4. On the first start, `npm start` creates the schema and bootstraps the admin account. The password is hashed before it reaches the database. After confirming the first successful start, remove `ADMIN_PASSWORD` from Render's environment settings and redeploy. The startup bootstrap is idempotent and will not reset an existing admin password.

Do not put database URLs, session secrets, or admin passwords in source files or chat. Keep the Render database's internal connection string private.

## Deploy the web frontend on Vercel

1. Import this repository into Vercel with the project root as the Root Directory and the “Other” framework preset.
2. Set the install command to `npm install`, the build command to `npm run build`, and the output directory to `dist`.
3. Before deploying, replace `YOUR-RENDER-SERVICE` in `vercel.json` with the Render service hostname. For example, if Render gives the API hostname `haafedk-api.onrender.com`, the rewrite destination should be `https://haafedk-api.onrender.com/api/:path*`.
4. Deploy the frontend, then copy its exact origin (for example, `https://your-site.vercel.app`) into Render's `FRONTEND_ORIGIN` and redeploy the API.

The Vercel rewrite keeps browser requests on the Vercel origin, so the API's secure, HTTP-only session cookie works without exposing database credentials to the browser.

## Current limits

- The receipt selector currently saves only the chosen filename; it does not upload the receipt contents to persistent storage.
- The Android app still uses its own local account state and does not use this web API or PostgreSQL.