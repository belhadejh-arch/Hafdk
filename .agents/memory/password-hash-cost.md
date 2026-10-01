---
name: Password hashing cost
description: The deliberate bcrypt work-factor choice for account registration.
---

Keep new account passwords at bcrypt cost 11 unless production latency measurements and a security review justify a different value. Existing hashes remain valid; changing the cost affects only newly hashed passwords.

**Why:** Local measurements motivated reducing registration work from cost 12, but production latency has not been measured and weakening the work factor further would trade away password-guessing resistance.

**How to apply:** Re-measure signup latency in the deployed environment before changing this value again; consider database, session-store, and cold-start time separately from bcrypt time.