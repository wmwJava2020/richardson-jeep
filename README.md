# Richardson-Jeep Microservices Platform

A multi-module Spring Boot 3 / Spring Cloud microservices reference
project for Richardson-Jeep (base package `com.xpo.dfw`), covering order
placement, inventory management, customer management, and notification
dispatch behind a single API gateway, with centralized configuration,
service discovery, observability, and resilience.

## Architecture

```
                         ┌──────────────────┐
                         │   API Gateway     │  :1921
                         │ (Spring Cloud GW) │
                         └─────────┬─────────┘
                                    │
        ┌───────────────┬──────────┼──────────────┬────────────────┐
        │                │          │              │                │
 ┌──────▼─────┐   ┌──────▼─────┐ ┌──▼──────────┐ ┌─▼──────────────┐
 │ Inventory  │   │ Customer   │ │  Order       │ │ Notification    │
 │ Service    │   │ Service    │ │  Service     │ │ Service          │
 │  :8081     │   │  :8084     │ │  :8082       │ │  :8083           │
 └──────┬─────┘   └──────┬─────┘ └──┬─────┬─────┘ └─────────┬────────┘
        │                │          │     │                 │
        │                │   Feign  │     │ Feign           │
        │                └──────────┼─────┴─────────────────┘
        │                           │
        └───────────────────────────┴── MySQL (one DB per service)

 Cross-cutting: Eureka (discovery-server :8761), Config Server
 (config-server :8888, native profile), Zipkin (:9411), Prometheus
 (:9090), Grafana (:3000).
```

Order Service orchestrates order placement by calling Customer Service
(validate the customer), Inventory Service (check availability, price,
and reserve stock), and Notification Service (send an outcome
notification), each via a Feign client wrapped with a Resilience4j
circuit breaker and retry.

## Services & Ports

| Service                | Port | Database          |
|------------------------|------|--------------------|
| discovery-server       | 8761 | -                  |
| config-server          | 8888 | -                  |
| api-gateway             | 1921 | -                  |
| inventory-service       | 8081 | `inventory_db`     |
| order-service           | 8082 | `order_db`         |
| notification-service    | 8083 | `notification_db`  |
| customer-service        | 8084 | `customer_db`      |
| MySQL                   | 3306 | -                  |
| Zipkin                  | 9411 | -                  |
| Prometheus              | 9090 | -                  |
| Grafana                 | 3000 | -                  |

Each business service exposes:
- `GET /actuator/health`, `/actuator/prometheus`, `/actuator/metrics`
- `GET /swagger-ui.html` and `/v3/api-docs` (OpenAPI / Swagger UI)

## Running locally with Docker Compose

Requires Docker and Docker Compose.

```bash
docker compose up -d --build
```

This starts MySQL (with the four service databases created
automatically via `mysql-init/init.sql`), the observability stack
(Zipkin, Prometheus, Grafana), `discovery-server`, `config-server`, all
four business services, and `api-gateway`. Give services a minute or two
to register with Eureka before sending requests.

Eureka dashboard: http://localhost:8761
Zipkin UI: http://localhost:9411
Prometheus: http://localhost:9090
Grafana: http://localhost:3000 (admin / admin)

To stop and remove containers:

```bash
docker compose down
```

Add `-v` to also remove the MySQL data volume.

## Running locally with Maven

Each module can also be run standalone with `mvn spring-boot:run`
(falling back to local `application.yml` defaults if Config Server /
Eureka / MySQL aren't running, though a MySQL instance is still required
for the JPA-backed services). Recommended startup order:

1. `discovery-server`
2. `config-server`
3. `inventory-service`, `customer-service`, `notification-service`
4. `order-service`
5. `api-gateway`

Build the whole reactor:

```bash
mvn clean verify
```

## API Examples (via the gateway, port 1921)

Create a customer:

```bash
curl -X POST http://localhost:1921/api/v1/customers \
  -H "Content-Type: application/json" \
  -d '{
        "firstName": "Jane",
        "lastName": "Doe",
        "email": "jane.doe@example.com",
        "phone": "214-555-0100",
        "addressLine": "123 Main St",
        "city": "Garland",
        "state": "TX",
        "postalCode": "75040"
      }'
```

Create an inventory item:

```bash
curl -X POST http://localhost:1921/api/v1/inventory \
  -H "Content-Type: application/json" \
  -d '{
        "sku": "JEEP-WRANGLER-DOOR-01",
        "productName": "Wrangler Front Door Assembly",
        "description": "OEM front door assembly",
        "quantityAvailable": 25,
        "reorderLevel": 5,
        "unitPrice": 450.00,
        "warehouseLocation": "DFW-A1"
      }'
```

Place an order (orchestrates customer validation, inventory check &
reservation, and a notification):

```bash
curl -X POST http://localhost:1921/api/v1/orders \
  -H "Content-Type: application/json" \
  -d '{
        "customerId": 1,
        "items": [
          { "sku": "JEEP-WRANGLER-DOOR-01", "quantity": 2 }
        ]
      }'
```

Check an order's status:

```bash
curl http://localhost:1921/api/v1/orders/1
```

Cancel an order:

```bash
curl -X POST http://localhost:1921/api/v1/orders/1/cancel
```

## Resilience

Order Service's calls to Customer, Inventory, and Notification Services
are protected by Resilience4j circuit breakers and retries (instance
names `customerService`, `inventoryService`, `notificationService`),
configured centrally in Config Server's `order-service.yml` (with local
fallback defaults in each service's `application.yml`). If a downstream
service is unavailable:

- Customer Service down → order creation fails with `503 Service
  Unavailable`.
- Inventory Service down → affected items are marked unavailable and the
  order is `REJECTED`.
- Notification Service down → the order still succeeds; the notification
  is logged and skipped.

The API Gateway also wraps each routed service with its own circuit
breaker and `fallbackUri` (see `api-gateway/src/main/resources/application.yml`),
returning a `503` from `/fallback/*` if a service is completely
unreachable.

## CI/CD

- `.github/workflows/ci-cd.yml` - GitHub Actions pipeline: build & test
  the Maven reactor, build & push a Docker image per service, then a
  deploy placeholder step.
- `Jenkinsfile` - equivalent declarative Jenkins pipeline.
- `k8s/richardson-jeep.yaml` - combined Kubernetes manifests
  (Deployments + Services + ConfigMap) for all components.

## Project Structure

```
richardson-jeep/
├── pom.xml                       # parent POM (dependency management)
├── discovery-server/             # Eureka server
├── config-server/                # Spring Cloud Config (native profile)
│   └── src/main/resources/config/  # shared + per-service config files
├── api-gateway/                  # Spring Cloud Gateway
├── inventory-service/            # inventory_db
├── customer-service/             # customer_db
├── notification-service/         # notification_db
├── order-service/                # order_db (Feign + Resilience4j)
├── mysql-init/init.sql
├── observability/prometheus.yml
├── docker-compose.yml
├── k8s/richardson-jeep.yaml
├── Jenkinsfile
└── .github/workflows/ci-cd.yml
```
