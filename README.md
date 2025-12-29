# Digital Wallet Platform

## Overview

This project is a backend-focused **digital wallet platform** inspired by real-world payment systems. It is designed to handle user onboarding, wallet management, and money transfers in a reliable and predictable way, even when multiple services are involved.
The goal of this project is not just to move money from one wallet to another, but to do it **safely**, **consistently**, and **in a way that reflects production-grade system design**.

---

## What This Project Does

At a high level, the system allows users to:
* Create an account and authenticate
* Create and manage wallets
* Credit money into a wallet
* Perform transactions between wallets
* Track transaction status reliably
All services communicate through a central API Gateway and run together using Docker.

---

## Architecture Overview

The platform follows a **microservices architecture**, where each service has a clear responsibility:

* **API Gateway** – Single entry point for all client requests
* **User Service** – User registration and authentication
* **Wallet Service** – Wallet creation, balance management, hold/credit/release operations
* **Transaction Service** – Orchestrates money transfers between wallets
* **Notification Service** – Sends async notifications
* **Reward Service** – Handles reward-related logic
* **Kafka** – Event streaming for async communication
* **Redis** – Rate limiting and caching

All services are containerized and run together using `docker-compose`.

---

## Reliability & Consistency Design

Transactions are designed with real-world failure scenarios in mind.

The system uses:
* **Idempotency** to avoid duplicate transactions
* **Saga pattern** to manage multi-step transactions across services
* **Circuit breaker** to prevent cascading failures
  
This ensures:
* No duplicate transactions
* No inconsistent wallet states
* Automatic rollback when a step fails
* A resilient and fault-tolerant payment flow

---

## Technology Stack

* **Java 17**
* **Spring Boot**
* **Spring Cloud (OpenFeign, Gateway)**
* **Apache Kafka**
* **Redis**
* **Docker & Docker Compose**
* **PostgreSQL / JPA (Hibernate)**

---

## Running the Project Locally

### Prerequisites
* Docker
* Docker Compose

### Steps
1. Clone the repository
2. Build all services
3. Start the system:

   ```bash
   docker compose up --build
   ```

Once running, all services will be available through the API Gateway.

---

## Testing the System
Instead of listing every API endpoint in this README, the project provides a **ready-to-use Postman collection** that covers:
* User signup and login
* Wallet creation
* Wallet credit
* Transactions
---

## Future Improvements

* Centralized observability (logs and metrics)
* Distributed tracing
* Role-based access control
* Improved failure simulations

---

## Author

Built as a hands-on backend engineering project with a focus on correctness, clarity, and real-world system behavior.
