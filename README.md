# BookMyShow

Full-stack movie ticket booking: React frontend, Spring Boot API, MySQL, JWT auth, and optional SMTP notifications.

Source: https://github.com/nishakumari26/BookMyShow-Ticket-Booking

Local URLs (when running on this machine):

- Frontend: http://localhost:3000
- Backend: http://localhost:8080
- Swagger: http://localhost:8080/swagger-ui/index.html

Public production URLs will be added here after Railway and Vercel deployments are verified. Do not treat placeholder text as a live demo.

## Features

- Registration, login, profile, JWT (`USER` / `ADMIN`)
- Movie / theater / show catalog and seat maps
- Multi-seat booking with pessimistic locking (duplicate seat → 409)
- Booking history and cancellation
- Admin dashboard and CRUD
- Optional booking/cancellation email
- Swagger UI
- Docker Compose (API + MySQL + frontend)

## Tech stack

| Layer | Choice |
| --- | --- |
| Frontend | React 18, Vite, Axios |
| Backend | Java 21, Spring Boot 3.3 |
| Database | MySQL 8, Spring Data JPA |
| Security | Spring Security, JWT, BCrypt |
| Docs | springdoc-openapi |
| Hosting (intended) | Vercel (frontend), Railway (API + MySQL) |

## Project layout

```
src/main/java/com/nisha/bookmyshow/   Spring Boot API
src/main/resources/                   application.yml + profiles (dev, docker, prod)
frontend/                             Vite React app
Dockerfile                            Backend image
frontend/Dockerfile                   Frontend image (nginx)
docker-compose.yml                    Local containers
```

## Environment variables

Copy `.env.example` to `.env`. Never commit `.env`.

| Variable | Purpose |
| --- | --- |
| `DB_HOST` `DB_PORT` `DB_NAME` | MySQL location |
| `DB_USERNAME` `DB_PASSWORD` | MySQL credentials |
| `JWT_SECRET` | HMAC signing key, **≥ 32 characters**. Required in `prod` / `docker`. |
| `JWT_EXPIRATION` | Token lifetime in milliseconds (alias: `JWT_EXPIRATION_MS`) |
| `MAIL_ENABLED` | `true` to send mail |
| `MAIL_HOST` `MAIL_PORT` `MAIL_USERNAME` `MAIL_PASSWORD` | SMTP |
| `FRONTEND_URL` | Production frontend origin for CORS |
| `CORS_ALLOWED_ORIGINS` | Optional comma-separated origins (overrides if set) |
| `SPRING_PROFILES_ACTIVE` | `dev` (local), `docker`, or `prod` |
| `PORT` | HTTP port (Railway sets this) |
| `APP_SEED_DATA` | `true` only for local/demo catalog seed |
| `APP_ADMIN_EMAIL` `APP_ADMIN_PASSWORD` | Optional production admin bootstrap (Railway secrets) |

Frontend build:

| Variable | Purpose |
| --- | --- |
| `VITE_API_BASE_URL` | Absolute API root, e.g. `https://<railway>/api` |

## Local development

Prerequisites: JDK 21, Maven 3.9+, Node 20+, MySQL 8.

```bash
mvn spring-boot:run
cd frontend && npm install && npm run dev
```

Development seed accounts (never use in production):

| Role | Email | Password |
| --- | --- | --- |
| ADMIN | `admin@bookmyshow.local` | `Admin@123` |
| USER | `user@bookmyshow.local` | `User@123` |

Production profile does **not** seed those users. Set `APP_ADMIN_EMAIL` and `APP_ADMIN_PASSWORD` on the host instead.

## Docker

```bash
cp .env.example .env
# set JWT_SECRET, MYSQL_ROOT_PASSWORD, MYSQL_PASSWORD
docker compose up --build
```

Compose publishes MySQL on **3307**, API on **18080**, and the UI on **13000** by default so it does not collide with a local MySQL on 3306 or a local app on 8080/3000.

Verified on this machine after `docker compose up --build`:

- Frontend: http://localhost:13000
- Backend health: http://localhost:18080/api/health
- Movies: http://localhost:18080/api/movies
- Swagger: http://localhost:18080/swagger-ui/index.html

`GET /api/health` reports API + MySQL status.

GitHub Actions (`.github/workflows/ci.yml`) runs `mvn test` and `npm run build` on push to `main`.

## Tests

```bash
mvn test
cd frontend && npm run build
```

## Deployment

### Backend (Railway)

1. Create a Railway project from this repository.
2. Add a MySQL plugin. Map `MYSQLHOST` / `MYSQLPORT` / `MYSQLDATABASE` / `MYSQLUSER` / `MYSQLPASSWORD` or set `DB_*` explicitly.
3. Set `SPRING_PROFILES_ACTIVE=prod`.
4. Generate a unique `JWT_SECRET` (do not reuse the local placeholder).
5. Set `FRONTEND_URL` to the Vercel origin (no `*`).
6. Leave `MAIL_ENABLED=false` until SMTP credentials are configured.
7. Optional: `APP_ADMIN_EMAIL` and `APP_ADMIN_PASSWORD`.

The API Dockerfile uses Java 21. Railway `PORT` is bound via `server.port`.

### Frontend (Vercel)

1. Root directory: `frontend`
2. Build command: `npm run build`
3. Output: `dist`
4. `VITE_API_BASE_URL=https://<your-railway-host>/api`

Then set backend `FRONTEND_URL` to the Vercel URL and redeploy the API so CORS allows only that origin.

## License

Learning and portfolio use.
