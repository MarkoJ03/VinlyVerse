# VinylVerse

![Java](https://img.shields.io/badge/Java-23-orange?style=flat-square&logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.7-brightgreen?style=flat-square&logo=springboot)
![Angular](https://img.shields.io/badge/Angular-19-red?style=flat-square&logo=angular)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue?style=flat-square&logo=mysql&logoColor=white)
![JWT](https://img.shields.io/badge/JWT-Authentication-purple?style=flat-square&logo=jsonwebtokens)

**VinylVerse** is a full-stack web application for browsing, ordering, and managing a collection of vinyl records. The project demonstrates modern software development practices, including REST API architecture, JWT authentication, role-based access control, PayPal checkout integration, email receipt delivery, and modern Angular standalone components.

---

## Table of Contents

- [Project Overview](#project-overview)
- [Features](#features)
- [Technologies](#technologies)
- [Architecture](#architecture)
- [Installation and Setup](#installation-and-setup)
- [API Documentation](#api-documentation)
- [Database Model](#database-model)
- [Security](#security)
- [Payments and Receipts](#payments-and-receipts)
- [Skills Demonstrated](#skills-demonstrated)
- [Author](#author)

---

## Project Overview

VinylVerse allows users to browse a collection of vinyl records with detailed information about each record, including track list, genre, record label, release year, and price. Users can add records to a cart, place guest orders, choose cash-on-delivery or PayPal sandbox payment, and receive a digital receipt by email. Administrators have access to a CRUD panel for managing records, genres, and order status.

### Key Features:
- **Public Section**: Record browsing, genre filtering, server-side search, sorting, pagination
- **Shopping Cart and Checkout**: Guest checkout with delivery address, order tokens, and digital receipt view
- **Payments**: Cash-on-delivery and PayPal sandbox flow with automatic capture after PayPal approval
- **Email Receipts**: Automatic digital receipt delivery after confirmed cash-on-delivery orders or successful PayPal payments
- **Admin Panel**: Complete management of records, genres, and orders
- **Authentication**: JWT-based authentication with BCrypt password encryption
- **Authorization**: Role-based access (Admin/User)

---

## Features

### Public Users
- Browse all records with server-side pagination
- Homepage carousel populated with random visible records from the backend
- Filter records by music genre
- Debounced server-side search by record name
- Sorting (by name, price ascending/descending)
- Detailed record view with track list
- Add records to a persistent shopping cart stored in browser local storage
- Guest checkout with email, delivery address, and payment method selection
- Cash-on-delivery order confirmation
- PayPal sandbox checkout with automatic payment capture after approval
- Digital receipt page with order items, delivery address, status, and payment method
- Responsive design for all devices

### Administrators
- System login (JWT authentication)
- CRUD operations for records:
  - Add new record with image upload
  - Edit existing records
  - Delete records (soft delete)
- CRUD operations for genres
- Order overview with detailed receipt-style order page
- Order status management, including marking orders as shipped
- Image cropper for optimizing images before upload

### Checkout and Payments
- Guest orders are protected with a generated `gostPristupniToken`
- Ordered records are hidden from the public catalog after checkout
- PayPal orders store the external provider order ID and payment capture timestamp
- RSD prices remain the main application currency
- PayPal payments are converted from RSD to a supported payment currency using a live exchange-rate service
- Email receipts are sent through Spring Mail when mail delivery is enabled

---

## Technologies

### Backend
| Technology | Version | Purpose |
|------------|---------|--------|
| **Java** | 23 | Programming language |
| **Spring Boot** | 3.4.7 | Backend framework |
| **Spring Security** | 6.x | Authentication and authorization |
| **Spring Data JPA** | 3.x | ORM and database access |
| **MySQL** | 8.0 | Relational database |
| **JWT (JJWT)** | 0.12.6 | Token-based authentication |
| **Lombok** | Latest | Boilerplate code reduction |
| **Jackson** | Latest | JSON/XML serialization |
| **Spring Mail** | 3.x | Digital receipt email delivery |
| **Bucket4j** | 8.10.1 | Login rate limiting |
| **PayPal REST API** | Sandbox | Online payment flow |
| **ExchangeRate API** | External REST API | RSD to payment-currency conversion |

### Frontend
| Technology | Version | Purpose |
|------------|---------|--------|
| **Angular** | 19.1 | Frontend framework |
| **TypeScript** | 5.7 | Programming language |
| **RxJS** | 7.8 | Reactive programming |
| **SweetAlert2** | 11.x | Modal dialogs and notifications |
| **ngx-image-cropper** | 9.x | Image cropping |
| **LocalStorage / SessionStorage** | Browser API | Cart persistence and guest order access token storage |

---

## Architecture

### Backend Architecture

```
vinylverse-backend/
├── src/main/java/server/
│   ├── config/              # Security, CORS, Web configuration
│   ├── controller/          # REST controllers
│   │   ├── BaseController   # Generic CRUD controller
│   │   ├── PlocaController
│   │   ├── ZanrController
│   │   ├── NarudzbinaController
│   │   ├── SlikaController
│   │   └── LoginController
│   ├── DTOs/                # Data Transfer Objects
│   ├── model/               # JPA entities
│   │   ├── Ploca
│   │   ├── Proizvod
│   │   ├── Zanr
│   │   ├── Narudzbina
│   │   ├── StavkaNarudzbine
│   │   ├── Korisnik
│   │   └── PravoPristupa
│   ├── repository/          # Spring Data repositories
│   ├── service/             # Business logic
│   │   ├── BaseService      # Generic service pattern
│   │   ├── PayPalService
│   │   ├── ExchangeRateService
│   │   └── RacunEmailService
│   └── utils/               # JWT Token utilities
```

**Key Design Patterns:**
- **Generic BaseService/BaseController** - Reducing code duplication for CRUD operations
- **DTO Pattern** - Separation of internal entities from API responses
- **Soft Delete** - Logical deletion with `vidljiv` field
- **Repository Pattern** - Spring Data JPA abstraction
- **Payment Service Layer** - PayPal order creation/capture is isolated from order business logic
- **Exchange Rate Service** - Live RSD conversion with caching and fallback configuration
- **Guest Access Token** - Guest orders are protected without requiring user accounts

### Frontend Architecture

```
vinylverse-frontend/
├── src/app/
│   ├── components/
│   │   ├── core/            # Header, Footer
│   │   ├── features/        # Feature components
│   │   │   ├── home-page/
│   │   │   ├── ploce-page/
│   │   │   ├── ploca-details/
│   │   │   ├── cart-page/
│   │   │   ├── checkout-success/
│   │   │   ├── checkout-cancel/
│   │   │   ├── login-page/
│   │   │   └── CRUD/        # Admin components
│   │   └── shared/          # Reusable components
│   │       ├── base-table/
│   │       ├── base-form/
│   │       └── ploca-card/
│   ├── models/              # TypeScript interfaces
│   ├── services/            # HTTP services
│   │   └── base.service.ts  # Generic CRUD service
│   ├── authGuard.ts         # Route protection
│   └── authInterceptor.ts   # JWT token injection
```

**Key Features:**
- **Standalone Components** - Modern Angular 19 architecture without NgModules
- **Generic BaseService** - Reusable CRUD operations
- **HTTP Interceptor** - Automatic JWT token injection on every request
- **Route Guards** - Admin route protection
- **Cart Service** - Local cart state with browser storage persistence
- **Checkout Success Flow** - Automatic PayPal capture after sandbox approval

---

## Installation and Setup

### Prerequisites
- Java 23+
- Node.js 20+
- MySQL 8.0+
- Maven 3.9+

### Backend

1. **Clone the repository**
```bash
git clone https://github.com/MarkoJ03/VinylVerse.git
cd VinylVerse/vinylverse-backend
```

2. **Set up the database**
```sql
CREATE DATABASE vinylverse;
```

3. **Configure environment variables or application.properties**
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/vinylverse
spring.datasource.username=your_username
spring.datasource.password=your_password

jwt.secret=your-long-random-jwt-secret

paypal.client-id=your-paypal-client-id
paypal.client-secret=your-paypal-client-secret
paypal.base-url=https://api-m.sandbox.paypal.com
paypal.return-url=http://localhost:4200/checkout/success
paypal.cancel-url=http://localhost:4200/checkout/cancel
paypal.payment-currency=EUR
paypal.rsd-to-payment-rate=0.0085

exchange-rate.rsd-api-url=https://open.er-api.com/v6/latest/RSD
exchange-rate.cache-minutes=720

app.mail.enabled=false
app.mail.from=no-reply@vinylverse.local
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password
```

The backend also supports a `.env` file loaded through `spring.config.import`, using variables such as `PAYPAL_CLIENT_ID`, `PAYPAL_CLIENT_SECRET`, `MAIL_ENABLED`, and `EXCHANGE_RATE_CACHE_MINUTES`.

4. **Run the application**
```bash
mvn spring-boot:run
```

Backend will be available at `http://localhost:8080`

### Frontend

1. **Install dependencies**
```bash
cd vinylverse-frontend
npm install
```

2. **Run development server**
```bash
ng serve
```

Frontend will be available at `http://localhost:4200`

---

## API Documentation

### Authentication

| Endpoint | Method | Description | Authentication |
|----------|--------|-------------|---------------|
| `/api/auth/login` | POST | User login | No |
| `/api/auth/register-admin` | POST | Admin registration | No |

### Records (Ploče)

| Endpoint | Method | Description | Authentication |
|----------|--------|-------------|---------------|
| `/api/ploca` | GET | All records | No |
| `/api/ploca/{id}` | GET | Single record | No |
| `/api/ploca/zanr/{id}` | GET | Records by genre | No |
| `/api/ploca/paginacija` | GET | Paginated records | No |
| `/api/ploca/search` | GET | Server-side search by record name with pagination | No |
| `/api/ploca/nasumicno` | GET | Random visible records for homepage carousel | No |
| `/api/ploca` | POST | New record | Admin |
| `/api/ploca/{id}` | PUT | Update record | Admin |
| `/api/ploca/{id}` | DELETE | Delete record | Admin |

### Genres (Žanrovi)

| Endpoint | Method | Description | Authentication |
|----------|--------|-------------|---------------|
| `/api/zanr` | GET | All genres | No |
| `/api/zanr/{id}` | GET | Single genre | No |
| `/api/zanr` | POST | New genre | Admin |
| `/api/zanr/{id}` | PUT | Update genre | Admin |
| `/api/zanr/{id}` | DELETE | Delete genre | Admin |

### Images

| Endpoint | Method | Description | Authentication |
|----------|--------|-------------|---------------|
| `/api/slike/upload` | POST | Upload image | Admin |
| `/api/slike/{filename}` | GET | Get image | No |

### Orders (Narudžbine)

| Endpoint | Method | Description | Authentication |
|----------|--------|-------------|---------------|
| `/api/narudzbina/guest` | POST | Create guest order from cart | No |
| `/api/narudzbina/guest/{id}` | GET | Guest order preview with access token query parameter | Guest token |
| `/api/narudzbina/moje` | GET | Current user's visible orders | User/Admin |
| `/api/narudzbina` | GET | All orders | Admin |
| `/api/narudzbina/{id}` | GET | Single order | Admin |
| `/api/narudzbina` | POST | Create order manually | Admin |
| `/api/narudzbina/{id}` | PUT | Update order/status | Admin |
| `/api/narudzbina/{id}` | DELETE | Soft delete order | Admin |

### Payments

| Endpoint | Method | Description | Authentication |
|----------|--------|-------------|---------------|
| `/api/narudzbina/{id}/pay-mock` | POST | Local mock payment for testing | Guest token/User/Admin |
| `/api/narudzbina/{id}/paypal/create-order` | POST | Create PayPal sandbox order and return approval URL | Guest token/User/Admin |
| `/api/narudzbina/{id}/paypal/capture` | POST | Capture approved PayPal order and mark order as paid | Guest token/User/Admin |

### Checkout Flow

1. User adds records to the cart on the frontend.
2. Frontend sends a guest order to `/api/narudzbina/guest`.
3. Backend validates the cart, delivery address, email, and payment method.
4. Backend generates a `gostPristupniToken` for secure guest access.
5. Ordered records are marked as not visible in the public catalog.
6. Cash-on-delivery orders are immediately confirmed and can trigger an email receipt.
7. PayPal orders are created in `PENDING_PAYMENT` status.
8. Frontend redirects the user to PayPal sandbox approval.
9. After PayPal approval, the success page automatically calls the capture endpoint.
10. Backend marks the order as `PAID`, stores `paymentCapturedAt`, and sends a digital receipt email.

---

## Database Model

```
┌─────────────────┐     ┌─────────────────┐     ┌─────────────────┐
│    Proizvod     │     │      Ploca      │     │      Zanr       │
├─────────────────┤     ├─────────────────┤     ├─────────────────┤
│ id (PK)         │◄────│ id (PK, FK)     │     │ id (PK)         │
│ naziv           │     │ listaPesama     │────►│ naziv           │
│ cena            │     │ brend           │     │ vidljiv         │
│ opis            │     │ izdavackaKuca   │     └─────────────────┘
│ slikaPutanja    │     │ godinaIzdanja   │
│ vidljiv         │     │ zanr_id (FK)    │
└─────────────────┘     │ vidljiv         │
                        └─────────────────┘

┌─────────────────┐     ┌─────────────────────────┐     ┌─────────────────┐
│    Korisnik     │     │ DodeljenoPravoPristupa  │     │  PravoPristupa  │
├─────────────────┤     ├─────────────────────────┤     ├─────────────────┤
│ id (PK)         │◄────│ id (PK)                 │     │ id (PK)         │
│ email           │     │ korisnik_id (FK)        │────►│ naziv           │
│ korisnickoIme   │     │ pravoPristupa_id (FK)   │     │ vidljiv         │
│ lozinka         │     │ vidljiv                 │     └─────────────────┘
│ vidljiv         │     └─────────────────────────┘
└─────────────────┘

┌───────────────────────────┐     ┌───────────────────────────┐     ┌─────────────────┐
│        Narudzbina         │     │     StavkaNarudzbine      │     │      Ploca      │
├───────────────────────────┤     ├───────────────────────────┤     ├─────────────────┤
│ id (PK)                   │◄────│ narudzbina_id (FK)        │     │ id (PK)         │
│ korisnik_id (nullable FK) │     │ ploca_id (FK)             │────►│ proizvod_id     │
│ gostEmail                 │     │ jedinicnaCena             │     │ vidljiv         │
│ gostPristupniToken        │     │ ukupno                    │     └─────────────────┘
│ status                    │     │ vidljiv                   │
│ ukupanIznos               │     └───────────────────────────┘
│ nacinPlacanja             │
│ paymentProvider           │
│ providerOrderId           │
│ paymentCapturedAt         │
│ delivery address fields   │
│ vidljiv                   │
└───────────────────────────┘
```

---

## Security

- **JWT Authentication** - Stateless token-based authentication
- **BCrypt** - Password encryption with salt
- **CORS** - Configured for frontend origin
- **Role-Based Access Control** - Admin/User distinction
- **HTTP Interceptor** - Automatic Authorization header injection
- **Route Guards** - Frontend protection for admin routes
- **Image Upload Validation** - File extension and MIME type validation
- **Login Rate Limiting** - Bucket4j limits repeated login attempts per IP
- **Guest Order Token** - Guest checkout uses `X-Order-Token` / query token protection
- **PayPal Credentials** - Client ID and secret are read from environment configuration
- **Path Traversal Protection** - Uploaded image filenames and paths are normalized and validated

---

## Payments and Receipts

- **Cash on Delivery (`POUZEC`)** - Orders are confirmed immediately and can trigger a digital receipt email.
- **PayPal (`PAYPAL`)** - Backend creates a PayPal order, redirects the user to sandbox approval, captures the approved order, and marks the order as paid.
- **RSD-first Pricing** - Product prices and receipts remain in RSD.
- **Currency Conversion** - Since PayPal does not support RSD directly, the backend converts RSD to a supported payment currency such as EUR.
- **Live Exchange Rate** - `ExchangeRateService` retrieves live rates from `https://open.er-api.com/v6/latest/RSD`, caches them, and falls back to `PAYPAL_RSD_TO_PAYMENT_RATE` if the API is unavailable.
- **Email Receipt** - `RacunEmailService` sends a plain-text digital receipt with order details, items, total amount, and delivery address.

---

## Skills Demonstrated

This project demonstrates the following skills:

### Backend Development
- REST API design and implementation
- Spring Boot configuration and dependency injection
- JPA/Hibernate ORM mapping
- JWT implementation with Spring Security
- Generic patterns for reducing code duplication
- File upload handling
- Server-side pagination, search, and random record retrieval
- PayPal REST integration and payment capture
- Email delivery with Spring Mail
- Exchange-rate API integration with caching and fallback logic
- Rate limiting with Bucket4j
- Error handling and validation

### Frontend Development  
- Angular 19 with standalone components
- Reactive programming with RxJS
- HTTP interceptors and route guards
- Responsive CSS design
- State management
- Form handling and validation
- Shopping cart state persistence
- Checkout, digital receipt, and PayPal return handling

### General Skills
- Full-stack integration
- Git version control
- Database design
- API documentation
- Clean code principles

---

## Author

**Marko Jeremić** - Software Engineering Student

- GitHub: [github.com/MarkoJ03](https://github.com/MarkoJ03)
- LinkedIn: [linkedin.com/in/marko-jeremić](https://www.linkedin.com/in/marko-jeremi%C4%87-04408229b/)
- Email: markojeremic03@gmail.com

---



<p align="center">
  <b>Thank you for viewing!</b><br>
  Feel free to contact me with any questions.
</p>