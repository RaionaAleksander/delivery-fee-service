# Delivery Fee Service Backend (Spring)

## Overview

This project is a Spring Boot backend service for calculating delivery fees for food couriers based on:

* City
* Vehicle type
* Weather conditions

---

## How to Run the Application

### 1. Prerequisites

Make sure you have installed:

* Java 21 or higher

---

### 2. Clone the Repository

```bash
git clone https://github.com/RaionaAleksander/delivery-fee-service-backend-spring.git
cd delivery-fee-service-backend-spring
```

---

### 3. Run the Application

Use Maven Wrapper (no need to install Maven):

```bash
./mvnw spring-boot:run
```

On Windows:

```bash
mvnw.cmd spring-boot:run
```

---

Or run the main class:

```
DeliveryFeeServiceApplication.java
```

---

### 4. Access the Application

* Application runs on:

  ```
  http://localhost:8080
  ```

* H2 Database Console:

  ```
  http://localhost:8080/h2-console
  ```

#### H2 Connection Settings:

* JDBC URL: `jdbc:h2:file:./data/weather-db`
* Username: `sa`
* Password: `password`

---

## Tech Stack

* Java
* Spring Boot
* Spring Data JPA
* H2 Database
* Maven

---

## Project Structure

```
controller   - REST endpoints  
service      - business logic  
repository   - data access layer  
model        - entities and DTOs  
exception    - custom exceptions  
```

---

## Notes

* Weather data is stored in H2 database
* Database is file-based and persists between runs
* Scheduled jobs will periodically fetch weather data
