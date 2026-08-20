# 🛒 E-Commerce REST API Application

A robust, scalable, and secure backend RESTful API built with **Java 21** and **Spring Boot 3**. This application models a full-fledged E-Commerce system featuring JWT-based authentication, entity relationships, PostgreSQL integration, comprehensive unit testing with Mockito, and full Dockerization.

---

## 🚀 Features

* **User Authentication & Authorization**: Secure signup, login, and Role-Based Access Control (RBAC) using **JWT (JSON Web Tokens)** and **Spring Security**.
* **Product & Category Management**: Full CRUD operations for managing e-commerce catalog items.
* **Shopping Cart System**: Dynamic item management allowing users to add, update, and remove items from their active cart.
* **Order Processing**: Automatic order generation from shopping cart contents with status updates.
* **API Documentation**: Interactive REST API documentation powered by **Swagger UI / OpenApi 3**.
* **Isolated Testing**: Comprehensive unit tests for service layers using **JUnit 5** & **Mockito**, with **H2 in-memory database** for context testing.
* **Containerization**: Single-command execution via **Docker** and **Docker Compose**.

---

## 🛠️ Tech Stack

* **Language**: Java 21
* **Framework**: Spring Boot 3
* **Database**: PostgreSQL (Production/Docker), H2 (Testing)
* **ORM / Data Access**: Spring Data JPA / Hibernate
* **Security**: Spring Security, JJWT (JSON Web Token)
* **Build Tool**: Maven
* **Testing**: JUnit 5, Mockito
* **DevOps**: Docker, Docker Compose
* **Documentation**: Springdoc OpenAPI / Swagger UI

---

## 📁 Project Structure

```text
src
├── main
│   ├── java/com/rufat/onlineshopping
│   │   ├── config       # Security & App Configurations
│   │   ├── controller   # REST Endpoints
│   │   ├── dto          # Data Transfer Objects (Requests/Responses)
│   │   ├── entity       # JPA Database Entities
│   │   ├── repository   # Spring Data JPA Repositories
│   │   ├── security     # JWT Filters & UserDetailsService
│   │   └── service      # Core Business Logic & Interfaces
│   └── resources
│       └── application.properties
└── test
    ├── java/com/rufat/onlineshopping
    │   └── service      # Mockito Unit Tests (Cart, Order, Auth, etc.)
    └── resources
        └── application-test.properties
```

---

## ⚙️ Getting Started

### Prerequisites
* **Java 21** installed
* **Maven 3.x** installed
* **Docker Desktop** installed and running

---

### 🔑 Environment Configuration

1. Create a `.env` file in the root directory (you can copy from `.env.example`):
   ```env
   SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/online_shopping_db
   SPRING_DATASOURCE_USERNAME=postgres
   SPRING_DATASOURCE_PASSWORD=your_password

   APP_JWT_SECRET=YourSuperSecretKeyForJWTAuthSystemWhichIsAtLeast256BitsLong
   APP_JWT_EXPIRATIONMS=86400000
   ```

---

### 🐳 Running with Docker (Recommended)

To run the full stack (Spring Boot Backend + PostgreSQL Database) in containers:

```bash
# Build and start containers
docker-compose up --build

# Stop containers
docker-compose down
```

The API will be available at `http://localhost:8080`.

---

### 💻 Running Locally (Without Docker)

1. Ensure PostgreSQL is running locally and the database `online_shopping_db` is created.
2. Run the application via Maven:
   ```bash
   mvn clean spring-boot:run
   ```

---

## 🧪 Running Unit Tests

Execute service layer unit tests using the H2 in-memory database profile:

```bash
mvn clean test
```

---

## 📖 API Documentation

Once the application is running, access Swagger UI to explore and test the endpoints interactively:

* **Swagger UI**: `http://localhost:8080/swagger-ui/index.html`
* **OpenAPI Docs**: `http://localhost:8080/v3/api-docs`

---

## 🔒 Main API Endpoints

| Method | Endpoint | Description | Access |
| :--- | :--- | :--- | :--- |
| **POST** | `/api/auth/register` | Register a new user | Public |
| **POST** | `/api/auth/login` | Authenticate & retrieve JWT token | Public |
| **GET** | `/api/products` | Get list of all products | Public / User |
| **POST** | `/api/products` | Create a new product | Admin |
| **GET** | `/api/cart` | Retrieve current user's cart | Authenticated |
| **POST** | `/api/cart/items` | Add product to cart | Authenticated |
| **POST** | `/api/orders` | Place order from active cart | Authenticated |

---

## 👨‍💻 Author

**Rüfət Məmmədov**
