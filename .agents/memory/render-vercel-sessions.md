---
name: Render and Vercel session auth
description: Deployment tradeoff for browser sessions when the static site and API use separate hosts.
---

Route browser `/api` requests through the frontend host to the Render API instead of calling Render directly from browser code. Keep the session cookie `HttpOnly`, `Secure` in production, and `SameSite=Lax`; set the API's allowed frontend origin to the deployed Vercel origin.

**Why:** A direct Vercel-to-Render browser request is cross-site and can lose its session cookie under third-party cookie restrictions. A frontend-host rewrite keeps the browser request first-party while the server-side proxy reaches Render.

**How to apply:** Use this arrangement for browser-based authentication across Vercel and Render. If changing to direct cross-origin API calls or custom domains, revisit cookie policy, CORS, and CSRF protections together.