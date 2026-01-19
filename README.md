# VinylVerse

![Java](https://img.shields.io/badge/Java-23-orange?style=flat-square&logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.7-brightgreen?style=flat-square&logo=springboot)
![Angular](https://img.shields.io/badge/Angular-19-red?style=flat-square&logo=angular)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue?style=flat-square&logo=mysql&logoColor=white)
![JWT](https://img.shields.io/badge/JWT-Authentication-purple?style=flat-square&logo=jsonwebtokens)

**VinylVerse** is a full-stack web application for browsing and managing a collection of vinyl records. The project demonstrates modern software development practices, including REST API architecture, JWT authentication, role-based access control, and modern Angular standalone components.

---

## Table of Contents

- [Project Overview](#project-overview)
- [Features](#features)
- [Technologies](#technologies)
- [Architecture](#architecture)
- [Installation and Setup](#installation-and-setup)
- [API Documentation](#api-documentation)
- [Author](#author)

---

## Project Overview

VinylVerse allows users to browse a collection of vinyl records with detailed information about each record, including track list, genre, record label, release year, and price. Administrators have access to a CRUD panel for managing records and genres.

### Key Features:
- **Public Section**: Record browsing, genre filtering, search, sorting
- **Admin Panel**: Complete management of records and genres (CRUD operations)
- **Authentication**: JWT-based authentication with BCrypt password encryption
- **Authorization**: Role-based access (Admin/User)

---

## Features

### Public Users
- Browse all records with carousel display on the homepage
- Filter records by music genre
- Search records by name
- Sorting (by name, price ascending/descending)
- Detailed record view with track list
- Responsive design for all devices

### Administrators
- System login (JWT authentication)
- CRUD operations for records:
  - Add new record with image upload
  - Edit existing records
  - Delete records (soft delete)
- CRUD operations for genres
- Image cropper for optimizing images before upload

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

### Frontend
| Technology | Version | Purpose |
|------------|---------|--------|
| **Angular** | 19.1 | Frontend framework |
| **TypeScript** | 5.7 | Programming language |
| **RxJS** | 7.8 | Reactive programming |
| **SweetAlert2** | 11.x | Modal dialogs and notifications |
| **ngx-image-cropper** | 9.x | Image cropping |

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
│   │   └── LoginController
│   ├── DTOs/                # Data Transfer Objects
│   ├── model/               # JPA entities
│   │   ├── Ploca
│   │   ├── Proizvod
│   │   ├── Zanr
│   │   ├── Korisnik
│   │   └── PravoPristupa
│   ├── repository/          # Spring Data repositories
│   ├── service/             # Business logic
│   │   └── BaseService      # Generic service pattern
│   └── utils/               # JWT Token utilities
```

**Key Design Patterns:**
- **Generic BaseService/BaseController** - Reducing code duplication for CRUD operations
- **DTO Pattern** - Separation of internal entities from API responses
- **Soft Delete** - Logical deletion with `vidljiv` field
- **Repository Pattern** - Spring Data JPA abstraction

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

3. **Configure application.properties**
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/vinylverse
spring.datasource.username=your_username
spring.datasource.password=your_password
```

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
- Error handling and validation

### Frontend Development  
- Angular 19 with standalone components
- Reactive programming with RxJS
- HTTP interceptors and route guards
- Responsive CSS design
- State management
- Form handling and validation

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
