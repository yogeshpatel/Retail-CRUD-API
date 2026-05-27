# Retail CRUD API — Baseline (No Docker · No Database)

<p align="center">
  <img src="https://img.shields.io/badge/Java-17-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java 17"/>
  <img src="https://img.shields.io/badge/Spring_Boot-3.3.5-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white" alt="Spring Boot"/>
  <img src="https://img.shields.io/badge/Gradle-8.x-02303A?style=for-the-badge&logo=gradle&logoColor=white" alt="Gradle"/>
  <img src="https://img.shields.io/badge/Storage-In--Memory-blueviolet?style=for-the-badge&logo=databricks&logoColor=white" alt="In-Memory"/>
  <img src="https://img.shields.io/badge/Docker-Not_Required-lightgrey?style=for-the-badge&logo=docker&logoColor=white" alt="No Docker"/>
</p>

---

## Overview

This is the **baseline version** of the Retail API — intentionally stripped of all infrastructure complexity.  
Its sole purpose is to demonstrate what a Spring Boot application looks like when it relies entirely on the **local developer environment**.

> **Teaching Goal:** Show why Docker is useful by first showing what life is like *without* it.

**This project intentionally has NO:**

| Excluded          | Reason                                      |
|-------------------|---------------------------------------------|
| Dockerfile        | Demonstrates environment dependency         |
| docker-compose    | No container orchestration                  |
| Database          | No PostgreSQL, H2, or JPA setup             |
| External services | Fully self-contained in a single JVM process|

All data lives in **Java `HashMap` collections** inside `RetailService` — it resets on every restart.

---

## Architecture

```
┌─────────────────────────────────────────────────────────┐
│                    Developer Machine                    │
│                                                         │
│   HTTP Request                                          │
│       │                                                 │
│       ▼                                                 │
│  ┌──────────────────┐                                   │
│  │  RetailController│  <- Spring MVC REST layer         │
│  └────────┬─────────┘                                   │
│           │                                             │
│           ▼                                             │
│  ┌──────────────────┐                                   │
│  │   RetailService  │  <- Business logic + seed data    │
│  └────────┬─────────┘                                   │
│           │                                             │
│           ▼                                             │
│  ┌──────────────────┐                                   │
│  │  In-Memory Maps  │  <- HashMap<Long, Customer> etc.  │
│  │  (no database)   │                                   │
│  └──────────────────┘                                   │
└─────────────────────────────────────────────────────────┘
```

---

## Project Structure

```text
retail-crud-no-docker-no-db/
├── build.gradle                          # Gradle build config (Java 17, Spring Boot 3.3.5)
├── settings.gradle
└── src/
    └── main/
        ├── resources/
        │   └── application.yml
        └── java/com/example/retail/
            ├── RetailCrudApplication.java        # Entry point
            ├── controller/
            │   └── RetailController.java         # REST endpoints
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
            │   ├── Customer.java
            │   ├── CustomerOrder.java
            │   └── Offer.java
            └── service/
                └── RetailService.java            # All data seeded here
```

---

## API Endpoints

Base URL: `http://localhost:8080`

| Method  | Endpoint                             | Description                            |
|---------|--------------------------------------|----------------------------------------|
| `GET`   | `/api/v1/customers`                  | List all customers                     |
| `GET`   | `/api/v1/customers/{id}`             | Get customer profile with order history|
| `GET`   | `/api/v1/offers/{code}`              | Get offer details by promo code        |
| `PATCH` | `/api/v1/customers/{id}/loyalty`     | Update a customer's loyalty points     |
| `GET`   | `/actuator/health`                   | Application health check               |

---

## Seed Data

There is no database. Data is hard-coded in `RetailService.seedData()` and resets on every restart.

```java
// src/main/java/com/example/retail/service/RetailService.java

private void seedData() {
    customers.put(1L, new Customer(1L, "Alice Smith",     "alice@example.com",   120));
    customers.put(2L, new Customer(2L, "Bob Jones",       "bob@example.com",      45));
    customers.put(3L, new Customer(3L, "Charlie Retail",  "charlie@example.com", 250));

    orders.put(1L, new CustomerOrder(1L, 1L, "ORD-1001", new BigDecimal("89.99"),  "DELIVERED"));
    orders.put(2L, new CustomerOrder(2L, 1L, "ORD-1002", new BigDecimal("15.50"),  "SHIPPED"));
    orders.put(3L, new CustomerOrder(3L, 2L, "ORD-1003", new BigDecimal("120.00"), "PROCESSING"));

    offers.put("WELCOME10", new Offer("WELCOME10", 10, true));
    offers.put("EXPIRED5",  new Offer("EXPIRED5",   5, false));
    offers.put("SUMMER25",  new Offer("SUMMER25",  25, true));
}
```

---

## Prerequisites

Before running this application, ensure the following are installed on your machine:

| Tool   | Required Version | Check Command     |
|--------|-----------------|-------------------|
| Java   | 17+             | `java -version`   |
| Gradle | 8.x             | `gradle -version` |

> **Note:** No Docker, no database, and no environment variables are required.

---

## Running the Application

### Option 1 — Run directly with Gradle

```bash
gradle bootRun
```

### Option 2 — Build and run the JAR

```bash
# Build
gradle clean bootJar

# Run
java -jar build/libs/retail-crud-no-docker-no-db.jar
```

The application starts at **`http://localhost:8080`**

---

## Testing the API

### Health Check

```bash
curl http://localhost:8080/actuator/health
```

### List All Customers

```bash
curl http://localhost:8080/api/v1/customers
```

### Get Customer Profile (with Orders)

```bash
curl http://localhost:8080/api/v1/customers/1
```

### Get an Offer by Code

```bash
curl http://localhost:8080/api/v1/offers/WELCOME10
```

### Update Loyalty Points

```bash
curl -X PATCH http://localhost:8080/api/v1/customers/1/loyalty \
  -H "Content-Type: application/json" \
  -d '{
    "pointsDelta": 50,
    "reason": "PURCHASE_REWARD"
  }'
```

---

## Why This Matters — The Docker Motivation

Running this app without Docker highlights a set of problems every team eventually faces:

| Problem                    | Without Docker              | With Docker                   |
|----------------------------|-----------------------------|-------------------------------|
| Java version mismatch      | App may fail silently       | Version pinned in `FROM` line |
| Gradle not installed       | `command not found`         | Bundled in build image        |
| Port already in use        | Manual conflict resolution  | Mapped via `-p` flag          |
| "Works on my machine"      | Very common                 | Container runs identically    |
| Onboarding a new developer | Hours of setup              | `docker run` and done         |

### Execution Model Comparison

**Before Docker:**
```
Source Code --> Local Gradle --> Local Java --> App on localhost
                (must exist)    (must be 17)   (port must be free)
```

**After Docker:**
```
Source Code --> docker build --> Image --> docker run --> Same result anywhere
```

> Docker does not make bad code good. It makes the **runtime packaging and execution repeatable**.

---

## Suggested Demo Flow

### Step 1 — Run the baseline

```bash
gradle bootRun
```

Show that this works *only* because Java and Gradle are installed locally.

---

### Step 2 — Build and run the JAR

```bash
gradle clean bootJar
java -jar build/libs/retail-crud-no-docker-no-db.jar
```

Point out: the JAR still requires a Java runtime on any target machine.

---

### Step 3 — Introduce a Dockerfile

```dockerfile
# Stage 1: Build
FROM gradle:8.10.2-jdk17 AS build
WORKDIR /app
COPY settings.gradle build.gradle ./
RUN gradle --no-daemon dependencies
COPY src ./src
RUN gradle --no-daemon clean bootJar -x test

# Stage 2: Run
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=build /app/build/libs/retail-crud-no-docker-no-db.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
```

---

### Step 4 — Build the Docker image

```bash
docker build -t retail-crud-demo .
```

---

### Step 5 — Run the container

```bash
docker run -p 8080:8080 retail-crud-demo
```

No local Gradle. No local Java. Just Docker.

---

## What Comes Next

After mastering this baseline, the next version of this project adds full infrastructure:

| Addition              | Purpose                                 |
|-----------------------|-----------------------------------------|
| Dockerfile            | Reproducible build and runtime          |
| docker-compose        | Service orchestration                   |
| PostgreSQL / H2       | Persistent data storage with JPA        |
| k6 load tests         | Performance and throughput benchmarking |
| Kubernetes manifests  | Production-grade deployment             |

That version becomes the **full benchmark lab**.

---

<p align="center">
  <sub>Built with Java · Spring Boot · Gradle</sub>
</p>
