
# Forex Service API

A Spring Boot based microservice for real-time currency conversion and exchange rate retrieval.  
This project is designed with extensibility in mind and showcases common patterns like retry mechanisms, message queue integration, and clean exception handling.

## Features

- Currency conversion (single request)
- Bulk currency conversion (via CSV upload)
- Real-time exchange rate retrieval
- Retry mechanism using RabbitMQ for failed conversions
- Scheduled processing of retry queue
- Global exception handling
- API documentation available via Swagger UI
- Dockerized setup for local development

## How to Run

### Prerequisites

- Java 21
- Docker

### Start with Docker Compose

```bash
docker-compose up
```

This will start:
- The application (accessible on `http://localhost:8080`)
- RabbitMQ (management console at `http://localhost:15672`, default username/password: guest/guest)

### API Documentation

Once the app is running, Swagger UI is available at:

```
http://localhost:8080/swagger-ui/index.html
```

## Notes and Design Decisions

### Why use Scheduled Retry?

This project uses a **retry mechanism with in-memory queue + scheduled processor** for failed currency conversions.

**Reason:**  
I am using the free plan of [CurrencyLayer](https://currencylayer.com/), which has strict rate limits and occasionally causes API exceptions due to limited access.  
To handle this, failed conversions are sent to a RabbitMQ queue and then processed using a scheduled processor.

This is a simple and effective approach for this project scope.  
In a production-grade system (with enterprise subscription or unlimited API access), this could be further improved with:

- Backoff strategies
- Circuit breakers and rate limiters

### Why are API keys in `application.yml`?

For simplicity, in this project I have directly added API keys and settings to `application.yml`.  
In a real-world scenario, sensitive information such as API keys should be handled securely:

- Environment variables
- Secrets management tools (Vault, AWS Secrets Manager, etc.)
- CI/CD pipelines with secure injection

This is an area for improvement and can easily be refactored.

### Current Limitations / Improvement Ideas

- Use a persistent queue (RabbitMQ + DLQ + persistence)
- Add more integration tests
- Implement user authentication (OAuth2 / JWT)

## Technologies Used

- Java 21
- Spring Boot
- Spring Web
- Spring Scheduling
- Spring Cache
- Spring AMQP (RabbitMQ)
- Feign Client
- Lombok
- Swagger / OpenAPI
- Docker
- Docker Compose


The architecture is intentionally kept **clean and extensible**, and it is open for further development.
