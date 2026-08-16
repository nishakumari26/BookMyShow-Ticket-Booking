# BookMyShow Ticket Booking

React + Spring Boot + MySQL movie ticket booking. JWT auth, seat locking, admin CRUD, optional SMTP.

**Live demo**

| | URL |
| --- | --- |
| Frontend | https://frontend-nine-psi-53.vercel.app |
| Backend | https://backend-production-57ef3.up.railway.app |
| Health | https://backend-production-57ef3.up.railway.app/api/health |
| Swagger | https://backend-production-57ef3.up.railway.app/swagger-ui/index.html |
| Source | https://github.com/nishakumari26/BookMyShow-Ticket-Booking |

**Local (this machine)**

- Frontend: http://localhost:3000
- Backend: http://localhost:8080
- Swagger: http://localhost:8080/swagger-ui/index.html

Docker Compose uses **13000 / 18080 / 3307** so it does not clash with those ports.

## Status (verified 16 Aug 2026)

| Check | Result |
| --- | --- |
| GitHub | Live |
| Docker Compose | Pass |
| Railway API + MySQL | Pass (`/api/health` → API UP, DB UP) |
| Vercel frontend | Pass, wired to Railway `/api` |
| CORS | Pass (Vercel origin only, no `*`) |
| Auth / JWT | Pass |
| Booking + duplicate seat `409` | Pass |
| Cancel booking | Pass |
| Admin movie CRUD | Pass |
| GitHub Actions CI | `mvn test` + frontend build on `main` |
| SMTP email | **Not verified** — `MAIL_HOST` / `MAIL_USERNAME` / `MAIL_PASSWORD` are not set on Railway |

Do not treat email as working until those variables exist and a real inbox is checked.

## Features

- Register, login, profile (`USER` / `ADMIN`)
- Movies, theaters, shows, seat map
- Multi-seat booking with pessimistic locking (taken seat → `409`)
- Booking history and cancellation
- Admin dashboard and CRUD
- Optional booking/cancellation email (when SMTP is configured)
- Public `GET /api/health`
- Swagger UI

## Tech stack

| Layer | Choice |
| --- | --- |
| Frontend | React 18, Vite, Axios — Vercel |
| Backend | Java 21, Spring Boot 3.3 — Railway |
| Database | MySQL — Railway |
| Security | Spring Security, JWT, BCrypt |
| Docs | springdoc-openapi |

## Project layout

```
src/main/java/com/nisha/bookmyshow/   Spring Boot API
src/main/resources/                   application.yml + profiles (dev, docker, prod)
frontend/                             Vite React app
Dockerfile                            Backend image
frontend/Dockerfile                   Frontend image (nginx)
docker-compose.yml                    Local containers
.github/workflows/ci.yml              Maven tests + frontend build
```

## Environment variables

Copy `.env.example` to `.env`. **Never commit `.env` or real passwords.**

| Variable | Purpose |
| --- | --- |
| `DB_HOST` `DB_PORT` `DB_NAME` | MySQL location |
| `DB_USERNAME` `DB_PASSWORD` | MySQL credentials |
| `JWT_SECRET` | HMAC key, **≥ 32 characters** (required in `prod` / `docker`) |
| `JWT_EXPIRATION` | Token lifetime in ms |
| `MAIL_ENABLED` | `true` to send mail |
| `MAIL_HOST` `MAIL_PORT` `MAIL_USERNAME` `MAIL_PASSWORD` | SMTP |
| `FRONTEND_URL` | Production frontend origin for CORS |
| `CORS_ALLOWED_ORIGINS` | Comma-separated origins (no `*`) |
| `SPRING_PROFILES_ACTIVE` | `dev`, `docker`, or `prod` |
| `PORT` | HTTP port (Railway sets this) |
| `APP_SEED_DATA` | `true` seeds demo catalog/accounts |
| `APP_ADMIN_EMAIL` `APP_ADMIN_PASSWORD` | Optional admin bootstrap on Railway |

Frontend build: `VITE_API_BASE_URL` = absolute API root, e.g. `https://backend-production-57ef3.up.railway.app/api`.

## Local development

JDK 21, Maven 3.9+, Node 20+, MySQL 8.

```bash
mvn spring-boot:run
cd frontend && npm install && npm run dev
```

Demo seed accounts (present when `APP_SEED_DATA=true`):

| Role | Email | Password |
| --- | --- | --- |
| ADMIN | `admin@bookmyshow.local` | `Admin@123` |
| USER | `user@bookmyshow.local` | `User@123` |

The live Railway service currently has seed data **on** so those accounts work there too. Turn `APP_SEED_DATA` off and rotate passwords when you no longer want a public demo login.

## Docker

```bash
cp .env.example .env
# set JWT_SECRET, MYSQL_ROOT_PASSWORD, MYSQL_PASSWORD
docker compose up --build
```

- UI: http://localhost:13000
- Health: http://localhost:18080/api/health
- Movies: http://localhost:18080/api/movies
- Swagger: http://localhost:18080/swagger-ui/index.html

## Tests

```bash
mvn test
cd frontend && npm run build
```

CI: `.github/workflows/ci.yml`.

## Production hosting

**Backend (Railway)** — Java 21 Docker image, `SPRING_PROFILES_ACTIVE=prod`, `PORT` from Railway, MySQL via `DB_*` (referenced from the MySQL plugin). CORS is set to https://frontend-nine-psi-53.vercel.app.

**Frontend (Vercel)** — root directory `frontend`, build `npm run build`, output `dist`, env `VITE_API_BASE_URL=https://backend-production-57ef3.up.railway.app/api`.

**SMTP (optional)** — on the Railway **backend** service add `MAIL_ENABLED=true`, `MAIL_HOST`, `MAIL_PORT` (587), `MAIL_USERNAME`, `MAIL_PASSWORD`. Gmail needs an App Password. Do not put SMTP secrets in git.

## License

Learning and portfolio use.
