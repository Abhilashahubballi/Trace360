# 🚀 Trace360 — Real-Time GPS Package Delivery Tracking

> End-to-end package delivery platform with live GPS tracking, OTP delivery confirmation, automated status updates, and WebSocket-powered real-time map updates.

---

## ✨ Key Features

- 📍 **Live GPS Tracking** — WebSocket-powered real-time map pin updates, no page refresh needed
- 🤖 **Auto Status Updates** — GPS proximity triggers automatic status transitions (Picked Up → In Transit → Out for Delivery → Delivered)
- 🔐 **OTP Delivery Confirmation** — 4-digit OTP sent to customer via Email + SMS; agent enters to confirm delivery
- 📧 **Email Notifications** — Status change emails via Gmail SMTP
- 📱 **SMS Alerts** — Twilio integration for SMS on every status change (optional)
- ⏱️ **Live ETA** — Haversine formula fallback; real traffic ETA with Google Maps API (optional)
- 🔒 **JWT Auth** — Secure role-based access (Admin / Agent / Customer)
- 🌐 **API-first Backend** — All agent endpoints designed for React Native / mobile app integration

---

## 🛠 Tech Stack

| Layer | Technology |
|-------|-----------|
| **Backend** | Java 17, Spring Boot 3.2, Spring Security, Spring WebSocket |
| **Database** | PostgreSQL 16 |
| **ORM** | Spring Data JPA / Hibernate |
| **Auth** | JWT (jjwt 0.11.5) |
| **Real-time** | STOMP over WebSocket (SockJS) |
| **Email** | Spring Mail — Gmail SMTP |
| **SMS** | Twilio (optional) |
| **ETA** | Haversine formula + Google Maps API (optional) |
| **Frontend** | Vanilla HTML5, CSS3, JavaScript, Leaflet.js maps |
| **Build** | Maven 3.9 |
| **Containerisation** | Docker, Docker Compose |
| **Hosting** | Render (backend + DB), Netlify (frontend) |

---

## 📁 Project Structure

```
trace360/
├── pom.xml                        # Root parent POM
├── docker-compose.yml             # Local full-stack dev environment
├── README.md
│
├── backend/                       # Spring Boot application
│   ├── Dockerfile
│   ├── pom.xml
│   └── src/main/java/com/trace360/
│       ├── controller/            # REST endpoints
│       ├── service/               # Business logic
│       ├── entity/                # JPA entities
│       ├── repository/            # Spring Data repos
│       ├── security/              # JWT filter, SecurityConfig
│       ├── websocket/             # Live location push
│       ├── dto/                   # Request / Response DTOs
│       └── exception/             # Global error handling
│
├── frontend/                      # Static HTML pages
│   ├── trace360-home.html         # Landing / public tracking page
│   ├── trace360-auth-v2.html      # Login / Register
│   ├── trace360-admin.html        # Admin dashboard
│   ├── trace360-v3.html           # Live map tracking page
│   └── trace360-404.html          # 404 error page
│
└── frontend-api/
    └── trace360-axios.js          # Centralised API client (Axios)
```

---

## ⚙️ Environment Variables

### Required

| Variable | Description |
|----------|-------------|
| `SPRING_DATASOURCE_URL` | PostgreSQL JDBC URL |
| `SPRING_DATASOURCE_USERNAME` | Database username |
| `SPRING_DATASOURCE_PASSWORD` | Database password |
| `SPRING_MAIL_USERNAME` | Gmail address for sending emails |
| `SPRING_MAIL_PASSWORD` | Gmail App Password (not your login password) |
| `APP_JWT_SECRET` | Secret key ≥ 256 bits for JWT signing |

### Optional

| Variable | Description |
|----------|-------------|
| `TWILIO_ACCOUNT_SID` | Twilio account SID (SMS) |
| `TWILIO_AUTH_TOKEN` | Twilio auth token |
| `TWILIO_PHONE_NUMBER` | Your Twilio number e.g. `+1234567890` |
| `GOOGLE_MAPS_API_KEY` | Real traffic-aware ETA (falls back to Haversine if absent) |
| `APP_FRONTEND_URL` | Frontend URL for CORS |
| `APP_CORS_ALLOWED_ORIGINS` | Same as above |

---

## 🐳 Local Development with Docker

### Prerequisites
- Docker Desktop installed and running
- Git

### Steps

```bash
# 1. Clone the repo
git clone https://github.com/your-username/trace360.git
cd trace360

# 2. (Optional) Fill in email credentials in docker-compose.yml
#    SPRING_MAIL_USERNAME and SPRING_MAIL_PASSWORD

# 3. Start everything (PostgreSQL + Backend + Nginx frontend)
docker compose up --build

# Backend API  →  http://localhost:8080
# Frontend     →  http://localhost:5500
# Database     →  localhost:5432
```

To stop:
```bash
docker compose down          # keep DB volume
docker compose down -v       # also delete DB data
```

---

## ☁️ Deploy on Render — Step by Step

### Step 1 — Push to GitHub

```bash
git init
git add .
git commit -m "initial commit"
git branch -M main
git remote add origin https://github.com/your-username/trace360.git
git push -u origin main
```

### Step 2 — Create a PostgreSQL Database on Render

1. Log in at [render.com](https://render.com) → **New → PostgreSQL**
2. Fill in:
   - **Name:** `trace360-db`
   - **Database:** `trace360`
   - **User:** `trace360user`
   - **Region:** choose closest to you
   - **Plan:** Free
3. Click **Create Database**
4. Copy the **Internal Database URL** — you'll need it in Step 3

### Step 3 — Deploy the Backend Web Service

1. Render dashboard → **New → Web Service**
2. Connect your GitHub repository
3. Configure:

   | Field | Value |
   |-------|-------|
   | **Name** | `trace360-backend` |
   | **Root Directory** | `backend` |
   | **Runtime** | `Docker` |
   | **Dockerfile Path** | `./Dockerfile` |
   | **Instance Type** | Free |

4. Add **Environment Variables**:

   | Key | Value |
   |-----|-------|
   | `SPRING_DATASOURCE_URL` | Internal DB URL from Step 2 |
   | `SPRING_DATASOURCE_USERNAME` | `trace360user` |
   | `SPRING_DATASOURCE_PASSWORD` | your DB password |
   | `APP_JWT_SECRET` | any long random string (min 32 chars) |
   | `SPRING_MAIL_USERNAME` | your Gmail address |
   | `SPRING_MAIL_PASSWORD` | your Gmail App Password |
   | `APP_FRONTEND_URL` | your frontend URL (set after Step 4) |
   | `APP_CORS_ALLOWED_ORIGINS` | same as above |

5. Click **Create Web Service** — build takes ~5 minutes
6. Your API will be live at: `https://trace360-backend.onrender.com`

> **Note:** On the free plan, the service sleeps after 15 min of inactivity. First request after sleep takes ~30 seconds.

### Step 4 — Deploy the Frontend (Netlify)

1. Go to [netlify.com](https://netlify.com) → **Add new site → Import from Git**
2. Select your `trace360` repository
3. Set **Publish directory** to `frontend`, leave build command empty
4. Click **Deploy Site**
5. Copy your Netlify URL (e.g. `https://trace360.netlify.app`)
6. Back in Render → update `APP_FRONTEND_URL` and `APP_CORS_ALLOWED_ORIGINS` to your Netlify URL

### Step 5 — Point Frontend to Backend

Open `frontend-api/trace360-axios.js` and update:

```js
const BASE_URL = "https://trace360-backend.onrender.com";
```

Commit and push — Netlify auto-redeploys.

### Step 6 — Gmail App Password Setup

1. Google Account → **Security → 2-Step Verification** → enable
2. **App Passwords** → generate one for "Mail"
3. Use the 16-character password as `SPRING_MAIL_PASSWORD`

### Step 7 — Verify

```bash
# Health check
curl https://trace360-backend.onrender.com/api/auth/health

# Test registration
curl -X POST https://trace360-backend.onrender.com/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"name":"Test","email":"test@example.com","password":"Pass@123","role":"CUSTOMER"}'
```

---

## 📡 API Reference

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| POST | `/api/auth/register` | Public | Register user |
| POST | `/api/auth/login` | Public | Login, get JWT |
| POST | `/api/auth/send-otp` | Public | Send OTP to email |
| GET | `/api/packages` | Admin | List all packages |
| POST | `/api/packages` | Admin | Create package |
| GET | `/api/packages/track/{id}` | Public | Track a package |
| POST | `/api/agent/location` | Agent | Push GPS update |
| POST | `/api/agent/verify-otp` | Agent | Confirm delivery via OTP |

**WebSocket:**
- Endpoint: `wss://trace360-backend.onrender.com/ws`
- Subscribe: `/topic/track/{trackingId}`

---

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/your-feature`
3. Commit: `git commit -m "feat: describe your change"`
4. Push: `git push origin feature/your-feature`
5. Open a Pull Request

---

## 📄 License

MIT © 2024 Trace360
