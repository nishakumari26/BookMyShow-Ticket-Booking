# Frontend

React UI for the BookMyShow backend.

```bash
cd frontend
npm install
npm run dev
```

Opens http://localhost:3000 and proxies `/api` to http://localhost:8080.

Production builds read `VITE_API_BASE_URL` (see `.env.example`). Do not commit real API URLs with secrets; the backend origin is public.

Local seed accounts (development backend only):

- Admin: `admin@bookmyshow.local` / `Admin@123`
- User: `user@bookmyshow.local` / `User@123`
