# Retail CRUD API

<p align="center">
  <img src="https://img.shields.io/badge/Java-17-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java 17"/>
  <img src="https://img.shields.io/badge/Spring_Boot-3.3.5-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white" alt="Spring Boot"/>
  <img src="https://img.shields.io/badge/H2-In--Memory_DB-003545?style=for-the-badge&logo=h2&logoColor=white" alt="H2"/>
  <img src="https://img.shields.io/badge/JPA-Hibernate-59666C?style=for-the-badge&logo=hibernate&logoColor=white" alt="JPA"/>
  <img src="https://img.shields.io/badge/Docker-Not_Required-lightgrey?style=for-the-badge&logo=docker&logoColor=white" alt="No Docker"/>
</p>

---

## Overview

A Spring Boot REST API for retail operations backed by an **H2 in-memory SQL database**.
Data is managed through **Spring Data JPA** and seeded automatically on startup.
A **configurable latency proxy** wraps the datasource to simulate realistic database response times.

No Docker, no external database server, and no environment variables are required to run this project.

---

## Architecture

```
HTTP Request
    |
    v
+--------------------+
|  RetailController  |   Spring MVC REST layer
+--------+-----------+
         |
         v
+--------------------+
|   RetailService    |   Business logic, @Transactional
+--------+-----------+
         |
         v
+--------------------+
| JPA Repositories   |   CustomerRepository
|                    |   CustomerOrderRepository
|                    |   OfferRepository
+--------+-----------+
         |
         v
+--------------------+
| DataSource Proxy   |   datasource-proxy — query logging + latency simulation
+--------+-----------+
         |
         v
+--------------------+
|   H2 In-Memory DB  |   SQL, PostgreSQL-compatible mode
|   (retaildb)       |   Schema created by Hibernate on startup
|                    |   Seed data loaded from data.sql
+--------------------+
```

---

## Project Structure

```
retail-crud-no-docker-no-db/
├── build.gradle
├── settings.gradle
└── src/
    └── main/
        ├── resources/
        │   ├── application.yml          # datasource, JPA, H2 console, latency config
        │   └── data.sql                 # seed data — runs after Hibernate creates schema
        └── java/com/example/retail/
            ├── RetailCrudApplication.java
            ├── config/
            │   └── DataSourceConfig.java    # H2 datasource wrapped with latency proxy
            ├── controller/
            │   └── RetailController.java
            ├── dto/
            │   ├── CustomerProfileResponse.java
            │   ├── ErrorResponse.java
            │   ├── LoyaltyUpdateRequest.java
            │   ├── LoyaltyUpdateResponse.java
            │   ├── OfferResponse.java
            │   └── OrderResponse.java
            ├── exception/
            │   ├── BusinessRuleException.java
            │   ├── GlobalExceptionHandler.java
            │   └── ResourceNotFoundException.java
            ├── model/
            │   ├── Customer.java            # @Entity
            │   ├── CustomerOrder.java       # @Entity, table = customer_order
            │   └── Offer.java               # @Entity, code is primary key
            ├── repository/
            │   ├── CustomerRepository.java
            │   ├── CustomerOrderRepository.java
            │   └── OfferRepository.java
            └── service/
                └── RetailService.java
```

---

## API Endpoints

Base URL: `http://localhost:8080`

| Method  | Endpoint                         | Description                              |
|---------|----------------------------------|------------------------------------------|
| GET     | /api/v1/customers                | List all customers                       |
| GET     | /api/v1/customers/{id}           | Get customer profile with order history  |
| GET     | /api/v1/offers/{code}            | Get offer details by promo code          |
| PATCH   | /api/v1/customers/{id}/loyalty   | Update a customer's loyalty points       |
| GET     | /actuator/health                 | Application health check                 |
| GET     | /h2-console                      | H2 database web console (browser)        |

---

## Database

### Connection details

| Property      | Value                                                           |
|---------------|-----------------------------------------------------------------|
| JDBC URL      | jdbc:h2:mem:retaildb;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE     |
| Driver        | org.h2.Driver                                                   |
| Username      | sa                                                              |
| Password      | (empty)                                                         |
| DDL           | create-drop (schema created by Hibernate, dropped on shutdown)  |

### Tables

| Table          | Primary Key     | Description                    |
|----------------|-----------------|--------------------------------|
| customer       | id BIGINT       | Customer accounts              |
| customer_order | id BIGINT       | Orders linked to customers     |
| offer          | code VARCHAR    | Promotional discount codes     |

### Seed data

Seed data is loaded from `src/main/resources/data.sql` after Hibernate creates the schema.
The file contains:

- 10 customers
- 50 orders across multiple customers
- 3 offer codes: WELCOME10, EXPIRED5, SUMMER25

Data resets to the seed state on every application restart.

### H2 web console

While the application is running, open `http://localhost:8080/h2-console` in a browser.
Use the JDBC URL `jdbc:h2:mem:retaildb` with username `sa` and an empty password.

---

## Latency Simulation

The H2 datasource is wrapped with a `datasource-proxy` interceptor that adds a configurable
random delay after every query execution. This allows the application to behave as if it were
talking to a real network-connected database.

Configuration in `application.yml`:

```yaml
app:
  db:
    simulate-latency: true   # set to false to use raw H2 speed
    min-delay-ms: 20
    max-delay-ms: 80
```

When enabled, each query incurs a random delay in the configured range.
The proxy also logs every SQL statement at DEBUG level through SLF4J under the logger name
`retail-h2-proxy`. To see query logs, add the following to `application.yml`:

```yaml
logging:
  level:
    retail-h2-proxy: DEBUG
```

---

## Prerequisites

| Tool  | Required Version | Check Command   |
|-------|-----------------|-----------------|
| Java  | 17 or higher    | java -version   |

No Docker, no database server, and no additional tooling is required.

---

## Running the Application

### Option 1 — Gradle wrapper

```bash
./gradlew bootRun
```

### Option 2 — Build and run the JAR

```bash
./gradlew clean bootJar
java -jar build/libs/retail-crud-no-docker-no-db.jar
```

The application starts on `http://localhost:8080`.

---

## Testing the API

### Health check

```bash
curl http://localhost:8080/actuator/health
```

### List all customers

```bash
curl http://localhost:8080/api/v1/customers
```

### Get customer profile with order history

```bash
curl http://localhost:8080/api/v1/customers/1
```

### Get an offer by code

```bash
curl http://localhost:8080/api/v1/offers/WELCOME10
```

### Update loyalty points

```bash
curl -X PATCH http://localhost:8080/api/v1/customers/1/loyalty \
  -H "Content-Type: application/json" \
  -d '{
    "pointsDelta": 50,
    "reason": "PURCHASE_REWARD"
  }'
```

---

## Technology Stack

| Layer              | Technology                                       |
|--------------------|--------------------------------------------------|
| Runtime            | Java 17                                          |
| Framework          | Spring Boot 3.3.5                                |
| REST               | Spring MVC                                       |
| Persistence        | Spring Data JPA, Hibernate                       |
| Database           | H2 in-memory (PostgreSQL-compatible mode)        |
| Query interception | datasource-proxy 1.10                            |
| Build              | Gradle 9                                         |
| Validation         | Jakarta Validation (Bean Validation 3.0)         |

---

<p align="center">
  <sub>Built with Java · Spring Boot · H2 · Spring Data JPA</sub>
</p>
