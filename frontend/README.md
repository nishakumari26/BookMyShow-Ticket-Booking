# Frontend

React UI for the BookMyShow API.

**Live:** https://frontend-nine-psi-53.vercel.app  
API: `https://backend-production-57ef3.up.railway.app/api`

```bash
cd frontend
npm install
npm run dev
```

Local app: http://localhost:3000 (Vite proxies `/api` to http://localhost:8080).

Production builds use `VITE_API_BASE_URL` (see `.env.example`). Do not commit `.env` files.

Demo accounts (only when the backend has `APP_SEED_DATA=true`):

- Admin: `admin@bookmyshow.local` / `Admin@123`
- User: `user@bookmyshow.local` / `User@123`
