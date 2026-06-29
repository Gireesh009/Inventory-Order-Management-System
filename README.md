# Inventory & Order Management System (Microservices)

A backend-only microservices-based system built using Java 11+, Spring Boot, and Spring Security. 
The system manages products, orders, authentication, and inventory with JWT-based security and role-based access control.

---

## 📌 Overview

This project is designed as a modular microservices architecture to simulate a real-world e-commerce backend system. 
It includes secure authentication, product catalog management, inventory tracking, and order processing.

### Key Features
- JWT-based authentication and authorization
- Role-based access control (ADMIN, CUSTOMER)
- Product and inventory management
- Order placement with stock validation
- Order lifecycle management (CREATED, CANCELLED)
- Layered architecture with clean separation of concerns
- Global exception handling
- Bean validation
- Swagger API documentation
- Unit testing with JUnit & Mockito

---

## 🏗️ Architecture

### Microservices
1. **Auth Service**
   - User registration & login
   - JWT token generation & validation
   - Role management

2. **Product Service**
   - Product CRUD operations (ADMIN)
   - View products (CUSTOMER/ADMIN)
   - Stock-related operations

3. **Order Service**
   - Place and cancel orders
   - Validate stock before order creation
   - Update inventory after successful order

4. **Inventory Module (Logical Service / Part of Product Service)**
   - Stock tracking
   - Low stock reporting
   - Stock updates

### Communication
- REST APIs between services
- JWT token passed via Authorization header

---

## 🔐 Security

- Spring Security integration
- JWT-based authentication
- Password encryption using BCrypt
- Role-based access:
  - `ROLE_ADMIN`
  - `ROLE_CUSTOMER`

---

## 🧰 Technology Stack

- Java 11+
- Spring Boot
- Spring Security
- Spring Data JPA
- Microservices Architecture
- Maven
- PostgreSQL / MySQL / H2 (for development)
- JUnit 5
- Mockito
- Swagger / OpenAPI
- Postman
