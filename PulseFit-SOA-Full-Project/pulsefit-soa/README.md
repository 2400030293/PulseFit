# PulseFit - Multi-Facility Fitness Membership & Attendance Tracking Platform

A complete starter SOA/microservices project using:
- Java 21
- Spring Boot 3.5.5
- Spring Cloud 2025.0.0
- Spring Data JPA
- PostgreSQL
- Eureka Service Discovery
- Spring Cloud Gateway
- JWT authentication
- React + Vite frontend
- Postman collection
- Unit tests
- Docker Compose for PostgreSQL

## Services

| Service | Port | Responsibility |
|---|---:|---|
| Eureka Server | 8761 | Service discovery |
| API Gateway | 8080 | Single entry point/routing |
| Auth Service | 8081 | Registration/login/JWT |
| Member Service | 8082 | Member profiles |
| Subscription Service | 8083 | Plans, subscriptions, renewals |
| Attendance Service | 8084 | Check-ins and attendance |

All business APIs should be called through the API Gateway.

## Architecture

React Frontend
        |
        v
API Gateway :8080
        |
        +--> Auth Service :8081
        +--> Member Service :8082
        +--> Subscription Service :8083
        +--> Attendance Service :8084
                    |
                    +--> Member Service
                    +--> Subscription Service

Eureka :8761 provides service discovery.

PostgreSQL stores data for all services in the `pulsefit` database. Tables are service-owned:
- auth_users
- members
- membership_plans
- subscriptions
- attendance

## 1. Start PostgreSQL

Option A - Docker:
    docker compose up -d postgres

Option B - local PostgreSQL:
Create a database:
    CREATE DATABASE pulsefit;

Then update each service's application.yml if your username/password differ.

Default:
username: postgres
password: postgres
database: pulsefit

## 2. Start backend

Open the root folder in Spring Tools / VS Code.

Run services in this order:
1. EurekaServerApplication
2. AuthServiceApplication
3. MemberServiceApplication
4. SubscriptionServiceApplication
5. AttendanceServiceApplication
6. ApiGatewayApplication

You can also use Maven from each service:
    mvn spring-boot:run

Check Eureka:
    http://localhost:8761

Gateway:
    http://localhost:8080

## 3. Frontend

Requirements:
- Node.js 20+

    cd frontend
    npm install
    npm run dev

Open:
    http://localhost:5173

## 4. Main API flow

Register:
POST http://localhost:8080/api/auth/register

Login:
POST http://localhost:8080/api/auth/login

Use the returned JWT as:
Authorization: Bearer <token>

Create member:
POST http://localhost:8080/api/members

Create plan:
POST http://localhost:8080/api/plans

Create subscription:
POST http://localhost:8080/api/subscriptions

Check in:
POST http://localhost:8080/api/attendance/checkin

Attendance history:
GET http://localhost:8080/api/attendance/member/{memberId}

Renew subscription:
PUT http://localhost:8080/api/subscriptions/{id}/renew

## 5. Important request bodies

Register:
{
  "username": "john",
  "password": "john123"
}

Login:
{
  "username": "john",
  "password": "john123"
}

Member:
{
  "name": "John Doe",
  "email": "john@example.com",
  "phone": "9876543210",
  "facility": "Vijayawada Central"
}

Plan:
{
  "name": "Premium",
  "durationMonths": 12,
  "price": 12000
}

Subscription:
{
  "memberId": 1,
  "planId": 1
}

Check-in:
{
  "memberId": 1,
  "facility": "Vijayawada Central"
}

## 6. Testing

Run:
    mvn test

inside each backend service.

The `postman/PulseFit.postman_collection.json` file can be imported into Postman.

## 7. Project submission structure

Recommended report chapters:
1. Abstract
2. Problem Statement
3. Objectives
4. Existing System
5. Proposed System
6. System Architecture
7. Microservice Description
8. Database Design
9. API Design
10. JWT Security
11. Eureka Service Discovery
12. API Gateway and Load Balancing
13. Frontend Screens
14. Testing
15. Results
16. Future Enhancement
17. Conclusion

## Notes

This project is intentionally kept student-friendly. It is a working academic baseline rather than a production banking-grade security system. Before production, add refresh tokens, secret management, HTTPS, centralized logging, database-per-service isolation, distributed tracing, rate limiting and stronger validation.
