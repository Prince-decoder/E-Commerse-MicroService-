# 🛒 E-Commerce Microservices Backend

A production-ready, fully containerized **E-Commerce Backend** built with a **Spring Boot Microservices** architecture. Each business domain runs as an independent service, communicating over REST via **OpenFeign**, registered and discovered through a central **Eureka Service Registry**.

> **Project Duration:** Sep 25, 2026 → Oct 2, 2026 &nbsp;|&nbsp; **Total Commits:** 95

---

## 📑 Table of Contents

- [Architecture Overview](#architecture-overview)
- [Tech Stack](#tech-stack)
- [Services](#services)
  - [ServerService – Eureka Registry](#1-serverservice--eureka-registry)
  - [UserService](#2-userservice)
  - [ProductService](#3-productservice)
  - [CartService](#4-cartservice)
  - [OrderService](#5-orderservice)
- [Inter-Service Communication](#inter-service-communication)
- [Database Design](#database-design)
- [Docker & Deployment](#docker--deployment)
- [Project Structure](#project-structure)
- [Getting Started](#getting-started)
- [API Reference](#api-reference)
- [Git Commit History](#git-commit-history)
- [Notes](#notes)

---

## Architecture Overview

```
                        ┌─────────────────────────┐
                        │   Eureka Server          │
                        │   (ServerService :8761)  │
                        └────────────┬────────────┘
                                     │  Service Discovery
           ┌─────────────────────────┼──────────────────────────┐
           │                         │                          │
    ┌──────▼──────┐          ┌───────▼──────┐         ┌────────▼──────┐
    │ UserService │          │ProductService│         │  CartService  │
    │   :8081     │          │    :8082     │         │    :8083      │
    └──────┬──────┘          └───────┬──────┘         └────────┬──────┘
           │                         │                          │
           └─────────────────────────┼──────────────────────────┘
                                     │  OpenFeign Clients
                              ┌──────▼──────┐
                              │OrderService │
                              │   :8084     │
                              └──────┬──────┘
                                     │
                            ┌────────▼────────┐
                            │   PostgreSQL     │
                            │   :5432          │
                            │  (4 Databases)   │
                            └─────────────────┘
```

---

## Tech Stack

| Layer               | Technology               |
|---------------------|--------------------------|
| Language            | Java 25                  |
| Framework           | Spring Boot 4.1.1        |
| Service Registry    | Spring Cloud Netflix Eureka |
| Inter-Service RPC   | Spring Cloud OpenFeign   |
| Persistence         | Spring Data JPA + Hibernate |
| Database            | PostgreSQL 16            |
| Build Tool          | Apache Maven             |
| Containerization    | Docker + Docker Compose  |
| Boilerplate         | Lombok                   |
| Spring Cloud BOM    | 2025.1.3                 |

---

## Services

### 1. ServerService – Eureka Registry

**Port:** `8761`

The central **Service Discovery** hub. All microservices register themselves here on startup and use it to resolve each other's network addresses dynamically — eliminating hard-coded URLs in production.

- Annotated with `@EnableEurekaServer`
- Acts as the **first service** that must be healthy before any other service starts

---

### 2. UserService

**Port:** `8081` | **Database:** `EcomUser`

Manages all user-related operations including registration, profile management, and address handling.

#### Key Classes

| Layer       | Class              | Responsibility                               |
|-------------|--------------------|----------------------------------------------|
| Controller  | `UserController`   | Exposes REST endpoints                       |
| Service     | `UserService`      | Business logic, DTO mapping                  |
| Repository  | `UserRepository`   | JPA queries against `EcomUser` DB            |
| Model       | `UserDetails`      | JPA Entity — users table                     |
| Model       | `Address`          | Embedded address (OneToOne, CascadeAll)      |
| DTO         | `UserRequest`      | Incoming client payload                      |
| DTO         | `UserResponse`     | Safe outgoing response (hides password)      |

#### Data Model – `UserDetails`

| Field       | Type            | Notes                            |
|-------------|-----------------|----------------------------------|
| `id`        | `Long`          | Auto-generated primary key       |
| `name`      | `String`        | Concatenated first+mid+last name |
| `email`     | `String`        | Used for update lookups          |
| `password`  | `String`        | Stored as plain text             |
| `role`      | `UserAuthority` | Default: `CUSTOMER`              |
| `address`   | `Address`       | OneToOne, cascades all ops       |
| `createdAt` | `LocalDateTime` | Auto-set on insert               |
| `updatedAt` | `LocalDateTime` | Auto-set on update               |

#### application.properties (UserService)

```properties
spring.application.name=UserService
server.port=8081

# Local development
spring.datasource.url=jdbc:postgresql://localhost:5432/EcomUser
spring.datasource.username=postgres
spring.datasource.password=Ashu
spring.datasource.driver-class-name=org.postgresql.Driver

spring.jpa.show-sql=true
spring.jpa.hibernate.ddl-auto=update

# Docker (uncomment when running via docker-compose)
# spring.datasource.url=jdbc:postgresql://postgres:5432/EcomUser
# eureka.client.service-url.defaultZone=http://eureka-server:8761/eureka/
```

---

### 3. ProductService

**Port:** `8082` | **Database:** `EcomProduct`

Manages the product catalog — adding products, keyword searching, and fetching product details.

#### Key Classes

| Layer       | Class               | Responsibility                       |
|-------------|---------------------|--------------------------------------|
| Controller  | `ProductController` | Exposes REST endpoints               |
| Service     | `ProductService`    | Business logic, DTO mapping          |
| Repository  | `ProductRepository` | JPA queries against `EcomProduct` DB |
| Model       | `ProductDetails`    | JPA Entity — products table          |
| Model       | `ProductStatus`     | Enum: `AVAILABLE`, etc.              |
| DTO         | `ProductRequest`    | Incoming client payload              |
| DTO         | `ProductResponse`   | Safe outgoing response               |

#### Data Model – `ProductDetails`

| Field           | Type            | Notes                      |
|-----------------|-----------------|----------------------------|
| `id`            | `Long`          | Auto-generated primary key |
| `name`          | `String`        | Product name               |
| `description`   | `String`        | Product description        |
| `stockQuantity` | `Integer`       | Units in stock             |
| `category`      | `String`        | Product category           |
| `imageUrl`      | `String`        | URL to product image       |
| `price`         | `BigDecimal`    | Product price              |
| `status`        | `ProductStatus` | Default: `AVAILABLE`       |
| `createdAt`     | `LocalDateTime` | Auto-set on insert         |
| `updatedAt`     | `LocalDateTime` | Auto-set on update         |

---

### 4. CartService

**Port:** `8083` | **Database:** `EcomCart`

Manages the shopping cart. Calls **UserService** and **ProductService** via Feign clients to validate users and fetch pricing before persisting cart items.

#### Key Classes

| Layer       | Class            | Responsibility                                       |
|-------------|------------------|------------------------------------------------------|
| Controller  | `CarController`  | Exposes REST endpoints                               |
| Service     | `CartService`    | Business logic, cross-service validation, price calc |
| Repository  | `CartRepository` | JPA queries against `EcomCart` DB                    |
| Feign       | `UserFeing`      | Calls `UserService` to validate users                |
| Feign       | `ProductFeing`   | Calls `ProductService` to fetch product + price      |
| Model       | `CartDetails`    | JPA Entity — cart table                              |
| Model       | `CartRequest`    | Incoming add-to-cart payload                         |

#### Data Model – `CartDetails`

| Field       | Type            | Notes                                         |
|-------------|-----------------|-----------------------------------------------|
| `id`        | `Long`          | Auto-generated primary key                    |
| `productId` | `Long`          | Soft-reference to ProductService (no FK join) |
| `userId`    | `Long`          | Soft-reference to UserService (no FK join)    |
| `quantity`  | `Integer`       | Number of units                               |
| `price`     | `BigDecimal`    | productPrice x quantity (computed at add)     |
| `createdAt` | `LocalDateTime` | Auto-set on insert                            |
| `updatedAt` | `LocalDateTime` | Auto-set on update                            |

#### Cart Logic

When a user adds an item, `CartService` will:
1. Verify the `userId` exists via `UserFeing`
2. Verify the `productId` exists via `ProductFeing`
3. Fetch the product price and compute `price = unitPrice x quantity`
4. Persist the `CartDetails` to `EcomCart` DB

#### application.properties (CartService)

```properties
spring.application.name=CartService
server.port=8083

# Local development
spring.datasource.url=jdbc:postgresql://localhost:5432/EcomCart
spring.datasource.username=postgres
spring.datasource.password=Ashu
spring.datasource.driver-class-name=org.postgresql.Driver

spring.jpa.show-sql=true
spring.jpa.hibernate.ddl-auto=update

# Docker (uncomment when running via docker-compose)
# spring.datasource.url=jdbc:postgresql://postgres:5432/EcomCart
# eureka.client.service-url.defaultZone=http://eureka-server:8761/eureka/
```

---

### 5. OrderService

**Port:** `8084` | **Database:** `EcomOrder`

Handles order placement and retrieval. Calls **CartService** and **ProductService** via Feign clients to compile order details.

#### Key Classes

| Layer       | Class             | Responsibility                                  |
|-------------|-------------------|-------------------------------------------------|
| Controller  | `OrderController` | Exposes REST endpoints                          |
| Service     | `OrderService`    | Business logic, order assembly                  |
| Repository  | `OderRepository`  | JPA queries against `EcomOrder` DB              |
| Feign       | `CartFeing`       | Calls `CartService` to fetch cart items         |
| Feign       | `ProductFeing`    | Calls `ProductService` to fetch product details |
| Model       | `Order`           | JPA Entity — orders table                       |
| Model       | `OrderStatus`     | Enum: `PROCESSING`, etc.                        |
| DTO         | `OrderResponse`   | Safe order summary for the client               |
| DTO         | `ProductResponse` | Sanitized product info in order response        |

#### Data Model – `Order`

| Field         | Type            | Notes                           |
|---------------|-----------------|---------------------------------|
| `id`          | `Long`          | Auto-generated primary key      |
| `userId`      | `String`        | The placing user's ID           |
| `orderStatus` | `OrderStatus`   | Default: `PROCESSING`           |
| `orderPrice`  | `BigDecimal`    | Sum of all cart item prices     |
| `itemId`      | `List<Long>`    | Cart item IDs at time of order  |
| `orderDate`   | `LocalDateTime` | Auto-set on insert              |
| `orderUpdate` | `LocalDateTime` | Auto-set on update              |

#### Order Placement Flow

```
POST /order/place?userId={id}
    1. Fetch all CartItems for userId  --> CartService
    2. Sum item prices --> set orderPrice
    3. Persist Order to EcomOrder DB
    4. Return "Order Placed"
```

#### Order Retrieval Flow

```
GET /order/user?userId={id}
    1. Find Order by userId in EcomOrder
    2. For each itemId --> fetch ProductDetails  --> ProductService
    3. For each itemId --> fetch quantity        --> CartService
    4. Build OrderResponse with products + total price
```

#### application.properties (OrderService)

```properties
spring.application.name=OrderService
server.port=8084

# Local development
spring.datasource.url=jdbc:postgresql://localhost:5432/EcomOrder
spring.datasource.username=postgres
spring.datasource.password=Ashu
spring.datasource.driver-class-name=org.postgresql.Driver

spring.jpa.show-sql=true
spring.jpa.hibernate.ddl-auto=update

# Docker (uncomment when running via docker-compose)
# spring.datasource.url=jdbc:postgresql://postgres:5432/EcomOrder
# eureka.client.service-url.defaultZone=http://eureka-server:8761/eureka/
```

---

## Inter-Service Communication

All services communicate synchronously over HTTP using **Spring Cloud OpenFeign**. Services discover each other's addresses through the **Eureka registry** — no hard-coded URLs needed in production.

| Consumer      | Feign Client  | Target Service   | Methods Used                         |
|---------------|---------------|------------------|--------------------------------------|
| `CartService` | `UserFeing`   | `UserService`    | Get user by ID, Get user by username |
| `CartService` | `ProductFeing`| `ProductService` | Get product details by ID            |
| `OrderService`| `CartFeing`   | `CartService`    | Get cart items by userId, by IDs     |
| `OrderService`| `ProductFeing`| `ProductService` | Get full product details by ID       |

> **Design Note:** Services use **soft references** (storing IDs only, not JPA foreign keys) across database boundaries. This preserves database isolation — each service fully owns its own database and schema.

---

## Database Design

The `init.sql` script is automatically executed when the PostgreSQL container first starts, creating all four isolated databases:

```sql
CREATE DATABASE "EcomUser";
CREATE DATABASE "EcomCart";
CREATE DATABASE "EcomOrder";
CREATE DATABASE "EcomProduct";
```

| Database      | Owned By         | Key Tables                  |
|---------------|------------------|-----------------------------|
| `EcomUser`    | `UserService`    | `user_details`, `address`   |
| `EcomProduct` | `ProductService` | `products`                  |
| `EcomCart`    | `CartService`    | `cart`                      |
| `EcomOrder`   | `OrderService`   | `orders`                    |

> **PostgreSQL credentials (Docker):** User: `postgres` | Password: `Ashu` | Host: `postgres:5432`

---

## Docker & Deployment

### Startup Order

Docker Compose enforces a strict **dependency chain** with health checks:

```
1. eureka-server   (must be HEALTHY)
        |
2. postgres        (must be HEALTHY)
        |
3. user-service    (must be HEALTHY)
        |
4. product-service (must be HEALTHY)
        |
5. cart-service    (must be HEALTHY)
        |
6. order-service
```

### Dockerfile Pattern

Each service uses the same two-step Dockerfile:

```dockerfile
FROM openjdk:28-ea-trixie
LABEL authors="ashutosh"
COPY target/<ServiceName>.jar <ServiceName>.jar
ENTRYPOINT ["java", "-jar", "<ServiceName>.jar"]
```

> **Important:** Each service JAR must be built with Maven before running Docker Compose — the Dockerfiles copy pre-built `.jar` files from `target/`.

### Port Mapping

| Service          | Host Port | Container Port |
|------------------|-----------|----------------|
| `ServerService`  | 8761      | 8761           |
| `UserService`    | 8081      | 8081           |
| `ProductService` | 8082      | 8082           |
| `CartService`    | 8083      | 8083           |
| `OrderService`   | 8084      | 8084           |
| `PostgreSQL`     | 5432      | 5432           |

All containers communicate over a custom Docker bridge network: `my-network`.

---

## Project Structure

```
MicroService(Ecommerse Website Backend)/
│
├── docker-compose.yaml          # Orchestrates all services + PostgreSQL
├── init.sql                     # Creates 4 PostgreSQL databases on first run
│
├── ServerService/               # Eureka Service Registry (Port: 8761)
│   ├── Dockerfile
│   └── src/main/java/com/example/ServerService/
│       └── ServerServiceApplication.java
│
├── UserService/                 # User Management (Port: 8081)
│   ├── Dockerfile
│   ├── pom.xml
│   └── src/main/java/com/ashu/UserService/
│       ├── Controller/UserController.java
│       ├── Service/UserService.java
│       ├── Repository/UserRepository.java
│       ├── Model/User/UserDetails.java
│       ├── Model/User/UserAuthority.java
│       ├── Model/Address/Address.java
│       └── Config/WebConfig.java
│
├── ProductService/              # Product Catalog (Port: 8082)
│   ├── Dockerfile
│   ├── pom.xml
│   └── src/main/java/com/ashu/ProductService/
│       ├── Controller/ProductController.java
│       ├── Server/ProductService.java
│       ├── Repository/ProductRepository.java
│       ├── Model/ProductDetails.java
│       ├── Model/ProductStatus.java
│       └── Config/WebConfig.java
│
├── CartService/                 # Shopping Cart (Port: 8083)
│   ├── Dockerfile
│   ├── pom.xml
│   └── src/main/java/com/ashu/CartService/
│       ├── Controller/CarController.java
│       ├── Service/CartService.java
│       ├── Repository/CartRepository.java
│       ├── Feing/UserFeing.java
│       ├── Feing/ProductFeing.java
│       ├── Model/CartDetails.java
│       ├── Model/CartRequest.java
│       └── Config/WebConfig.java
│
└── OrderService/                # Order Management (Port: 8084)
    ├── Dockerfile
    ├── pom.xml
    └── src/main/java/com/ashu/OrderService/
        ├── Controller/OrderController.java
        ├── Service/OrderService.java
        ├── Repository/OderRepository.java
        ├── Feing/CartFeing.java
        ├── Feing/ProductFeing.java
        ├── Model/Order.java
        ├── Model/OrderStatus.java
        └── Config/WebConfig.java
```

---

## Getting Started

### Prerequisites

- Java 25+
- Apache Maven 3.8+
- Docker & Docker Compose

### Run with Docker Compose (Recommended)

**Step 1 — Build all service JARs:**

```bash
cd ServerService  && mvn package -DskipTests && cd ..
cd UserService    && mvn package -DskipTests && cd ..
cd ProductService && mvn package -DskipTests && cd ..
cd CartService    && mvn package -DskipTests && cd ..
cd OrderService   && mvn package -DskipTests && cd ..
```

**Step 2 — Start all services:**

```bash
docker-compose up --build
```

**Step 3 — Verify services are registered:**

Open Eureka Dashboard → http://localhost:8761

---

### Run Locally (Development)

In each service's `application.properties`, the Docker URLs are already commented out and localhost URLs are active. Simply ensure PostgreSQL is running locally, then start services in order:

```
ServerService → UserService → ProductService → CartService → OrderService
```

---

## API Reference

### UserService — `http://localhost:8081`

| Method | Endpoint        | Description          | Params / Body                        |
|--------|-----------------|----------------------|--------------------------------------|
| `POST` | `/api/add`      | Register a new user  | Body: `UserRequest`                  |
| `GET`  | `/api/users`    | Get all users        | —                                    |
| `GET`  | `/api/find`     | Get user by ID       | `?userId={id}`                       |
| `POST` | `/api/update`   | Update user details  | `?userId={id}` + Body: `UserRequest` |
| `GET`  | `/api/username` | Get user by username | `?username={name}`                   |

### ProductService — `http://localhost:8082`

| Method | Endpoint           | Description                    | Params / Body          |
|--------|--------------------|--------------------------------|------------------------|
| `GET`  | `/product/all`     | Get all products               | —                      |
| `GET`  | `/product/search`  | Search products by keyword     | `?keyword={word}`      |
| `GET`  | `/product/find`    | Get product response by ID     | `?id={id}`             |
| `GET`  | `/product/details` | Get full product details by ID | `?id={id}`             |
| `POST` | `/product/add`     | Add a new product              | Body: `ProductRequest` |

### CartService — `http://localhost:8083`

| Method | Endpoint           | Description                      | Params / Body                                     |
|--------|--------------------|----------------------------------|---------------------------------------------------|
| `GET`  | `/cart/all`        | Get all cart items               | —                                                 |
| `POST` | `/cart/add`        | Add item to cart                 | Body: `CartRequest` (productId, userId, quantity) |
| `GET`  | `/cart/user`       | Get cart items by user ID        | `?id={userId}`                                    |
| `GET`  | `/cart/name`       | Get cart items by username       | `?name={username}`                                |
| `GET`  | `/cart/cartsbyids` | Get specific cart item by IDs    | `?productid={id}&userid={id}`                     |
| `GET`  | `/cart/user/{id}`  | Get cart items by user ID (path) | Path: `{id}`                                      |

### OrderService — `http://localhost:8084`

| Method | Endpoint       | Description                  | Params         |
|--------|----------------|------------------------------|----------------|
| `POST` | `/order/place` | Place an order from cart     | `?userId={id}` |
| `GET`  | `/order/user`  | Get order details for a user | `?userId={id}` |

---

## Git Commit History

> **95 commits** across **8 days** — Sep 25, 2026 to Oct 2, 2026

### Phase 1 — Project Bootstrapping (Sep 25, 2026)

| Commit    | Description                                         |
|-----------|-----------------------------------------------------|
| `c4e3862` | Default User MicroService — initial Spring Boot project |
| `465a3b4` | Default Product Service — initial setup             |
| `f30d61c` | Default Cart Service — initial setup                |
| `6aee38f` | Default Order Service — initial setup               |
| `e668e22` | Default Cart Service with any config                |
| `c46ecb4` | Structure of the Address entity for database storage |

---

### Phase 2 — User & Product Service Core (Sep 26, 2026)

| Commit    | Description                                                            |
|-----------|------------------------------------------------------------------------|
| `6ffb86f` | `UserDetails` entity — structure for storing user data in DB           |
| `2bff38e` | `Address` model — structure for user address                           |
| `1aa9318` | `AddressRequest` DTO — client-to-server structure                      |
| `2637eb5` | `UserResponse` DTO — hides sensitive user data from response           |
| `d3d0413` | `UserRequest` DTO — structure for client-side user data                |
| `0d1c135` | `UserAuthority` enum — role/authority of the user (e.g. CUSTOMER)     |
| `9baaeb5` | `UserRepository` — JPA repository layer for DB interaction             |
| `2e19472` | `UserService` — service layer with DTO converters and CRUD methods     |
| `aaf4b8c` | `UserController` — REST controller using service layer                 |
| `607e409` | PostgreSQL database connection config (local server)                   |
| `5e65d97` | Removed properties file (switched to YAML)                             |
| `4c7b25d` | `ProductStatus` enum — product listing states on the website           |
| `d9fc7bf` | `ProductStatus` — different states of products available on website    |
| `a4dc656` | `ProductDetails` entity — product data structure for DB storage        |
| `5fa0977` | `ProductRepository` — custom query to find products by keywords        |
| `501ce7e` | `ProductRequest` DTO — structured data client sends to server          |
| `08cc4d2` | `ProductResponse` DTO — response model sent to user                    |
| `b6ecc19` | `ProductService` — business logic: add product, search, DTO mapping    |
| `86fcc18` | `ProductController` — REST controller layer                            |
| `f20c058` | Converted properties to YAML; connected to local PostgreSQL            |

---

### Phase 3 — Eureka Server Setup (Sep 29, 2026)

| Commit    | Description                                                              |
|-----------|--------------------------------------------------------------------------|
| `b30f746` | Default Eureka Server — initial Spring Boot project                      |
| `231575c` | `@EnableEurekaServer` annotation added                                   |
| `2d18383` | Declared port (8761) and instance hostname                               |
| `0b6e550` | Added Eureka Server + OpenFeign dependencies to ServerService            |
| `06e7bb4` | Reformatted configuration file                                           |
| `c501c0d` | Changed port for ProductService                                          |
| `42209ce` | Added OpenFeign + Eureka Client dependency to ProductService             |
| `affdabb` | New endpoint to get full product details from ProductService             |
| `aaad434` | New `getDetailsById` function in ProductService service layer            |
| `0affd27` | `CartDetails` entity — cart item structure for DB storage                |
| `2d3138d` | `CartRepository` — JPA repository layer for CartService DB              |
| `284a4ab` | `@EnableFeignClients` — enabled Feign Client on CartService             |
| `3a3f735` | `UserFeing` — Feign client to call UserService                           |
| `9fc1425` | `ProductFeing` — Feign client to call ProductService                     |
| `be0c559` | Added DTOs needed by Feign clients (User + Product structures)           |
| `a19b7aa` | Removed redundant methods already handled by ProductService              |
| `09db11c` | Removed redundant methods already handled by UserService                 |
| `7088f72` | Changed default port and database connection in CartService              |
| `131b5fc` | Added Product Feign-related DTOs to CartService                          |
| `39dc1a8` | `CartService` — business logic interacting with UserFeing + ProductFeing |
| `c08f5e4` | `CarController` — REST controller for CartService                        |
| `831f0b8` | `CartRequest` — request structure from client (productId, userId, qty)   |
| `112765a` | `findByName` — JPA function to get user by username                      |
| `3b7bc81` | Added `getUserByName` function to UserService service layer              |
| `1d1f802` | New endpoint in UserController to get user by username                   |
| `66085b4` | `getUserByUsername` added to `UserFeing` Feign client                    |
| `41aba30` | `findAllByUserName` — new Cart function using username via Feign         |
| `2347415` | Two new endpoints: get cart by userId, get cart by username              |
| `2fb4502` | Added Eureka Client + OpenFeign to OrderService; named service           |
| `796bfb7` | `Order` entity — order data structure for DB storage                     |
| `b65048e` | `OrderStatus` enum — status of the order (PROCESSING, etc.)             |
| `1a235bf` | `OderRepository` — JPA repository for order DB interaction              |
| `3e9c7d8` | `CartFeing` — Feign client to call CartService from OrderService         |
| `72f3e41` | `CartDetails` DTO in OrderService — receives cart item data from Feign  |
| `f902d0b` | `OrderService` — service layer interacting with Feign + repository       |
| `5ef152e` | `OrderController` — REST controller to place order                       |
| `3d9d83a` | `@EnableFeignClients` enabled on OrderService                            |
| `e834636` | Named UserService (`spring.application.name=UserService`)               |
| `5f72429` | Named ProductService (`spring.application.name=ProductService`)         |
| `3a972e3` | Named ServerService (`spring.application.name=ServerService`)           |
| `98a32c6` | Named CartService (`spring.application.name=CartService`)               |

---

### Phase 4 — Docker Containerization (Sep 30, 2026)

| Commit    | Description                                                             |
|-----------|-------------------------------------------------------------------------|
| `72d5628` | Updated DB URL + Eureka URL in UserService for Docker container         |
| `93d0c5e` | Updated DB URL + Eureka URL in ProductService for Docker container      |
| `3e5456a` | Updated DB URL + Eureka URL in CartService for Docker container         |
| `141b8ee` | Updated DB URL + Eureka URL in CartService (retry/fix)                  |
| `4049428` | Changed Eureka hostname from `localhost` to `eureka-server`             |
| `6168d47` | Dockerfile for UserService — base image, jar copy, entrypoint           |
| `bf39e75` | Dockerfile for ProductService                                           |
| `423f4d1` | Dockerfile for CartService                                              |
| `04a16f6` | Dockerfile for OrderService                                             |
| `f6fe6bc` | Dockerfile for ServerService                                            |
| `c7b4df7` | `docker-compose.yaml` — all services, PostgreSQL image, bridge network  |
| `643918a` | `init.sql` — auto-creates all 4 databases when PostgreSQL starts        |
| `97629a5` | IntelliJ `.idea` IDE files added                                        |

---

### Phase 5 — Order Retrieval & Local Dev Fixes (Oct 1–2, 2026)

| Commit    | Date       | Description                                                             |
|-----------|------------|-------------------------------------------------------------------------|
| `75df430` | 2026-10-01 | Commented out Docker URLs; restored localhost URLs for local dev        |
| `1a5db35` | 2026-10-01 | Same localhost URL fix for CartService                                  |
| `5909d6b` | 2026-10-01 | Same localhost URL fix for OrderService                                 |
| `24428ad` | 2026-10-01 | `ProductFeing` added to OrderService to fetch product data              |
| `fd858d0` | 2026-10-01 | `ProductDetails` DTO structure in OrderService (fetch from ProductSvc)  |
| `f9137e8` | 2026-10-01 | `ProductResponse` DTO — converts product details; hides extra data      |
| `a1d9011` | 2026-10-01 | `findByProductIdAndUserId` — new CartRepository JPA function            |
| `26648b8` | 2026-10-01 | `findByUserId` — new JPA function to get cart items from DB             |
| `15c0c1a` | 2026-10-01 | New CartService endpoint exposing the new DB function                   |
| `3f34fb7` | 2026-10-01 | `findByUserId` in OrderRepository — JPA query to find orders by user    |
| `ce294a6` | 2026-10-01 | `getCartByIds` added to `CartFeing` — retrieves cart item by product+user IDs |
| `1a858d0` | 2026-10-01 | Temporary `CartDetails` structure in OrderService for Feign response    |
| `c00bbe8` | 2026-10-01 | `getOrders(userId)` + `detailsToResponse()` in OrderService             |
| `bfdc05c` | 2026-10-01 | `OrderResponse` DTO — hides sensitive order data from API response      |
| `a33e121` | 2026-10-01 | New `GET /order/user` endpoint to retrieve orders by userId             |
| `dd19fe7` | 2026-10-02 | Compiler configuration fix in Maven `pom.xml`                           |

---

## Notes

- **Passwords** are currently stored as plain text. For production, integrate Spring Security with `BCryptPasswordEncoder`.
- Each service has a `WebConfig.java` for CORS configuration, enabling the frontend to connect during development.
- All Dockerfiles rely on pre-built `.jar` files. Always run `mvn package -DskipTests` before `docker-compose up`.
- Each `application.properties` has Docker URLs **commented out** by default — uncomment them when deploying via Docker Compose.
