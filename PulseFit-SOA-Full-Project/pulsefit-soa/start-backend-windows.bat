@echo off
echo Start PostgreSQL first: docker compose up -d postgres
echo.
echo Then open six Spring Boot applications in Spring Tools or VS Code:
echo 1. eureka-server
echo 2. auth-service
echo 3. member-service
echo 4. subscription-service
echo 5. attendance-service
echo 6. api-gateway
pause
