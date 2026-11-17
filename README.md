# ms-courier

Courier Service owns profiles and FREE/BUSY availability. It does not own orders,
prices, earnings, balances, or payments. Availability is read from PostgreSQL.
RabbitMQ delivers events; it is not a cache. No Spring caching or Redis is used.

## API

Base path: `/api/v1/couriers` (matches the local Order Service Feign client).
The old `/orders/couriers` path remains an alias.

- POST: create courier (name, seven-character finCode, gender, optional dateOfBirth/address).
- GET `/available`: first FREE courier, or 404 when none exists.
- GET `/{id}`: courier profile.
- GET `?page=0&size=50`: list profiles (maximum size 100).
- PUT `/{id}`: update profile fields.
- DELETE `/{id}`: delete only when FREE.

Status updates are driven by RabbitMQ; the old PATCH status endpoint is removed.
CourierNotFoundException maps to 404. CourierConflictException maps to 409.
InvalidOrderEventException identifies invalid domain events. HTTP exception advice
does not catch listener exceptions: those are retried and dead-lettered.

## Events

One durable topic exchange (`order.exchange`) and one courier queue
(`courier.state.events`) replace the two conflicting configurations. Both existing
lowercase routing keys (`order.assigned`, `order.delivered`) and requirement names
(`ORDER_ASSIGNED`, `ORDER_DELIVERED`) are accepted. Publish each fact once.

JSON example: `{"orderId":"123","courierId":1}`. Numeric or string IDs are accepted;
courierId must be a positive Long. Order IDs are strings up to 100 characters.
Unknown additional fields are ignored and Java type headers are not needed.

Assignment marks BUSY; delivery frees only the matching active order. Local order
facts prevent duplicate or out-of-order messages from resurrecting assignments.
Database locks serialize changes to each courier. Transactions commit before
acknowledgement. Failures get up to three attempts, then go to
`courier.state.events.dlq` through its `.dlx` exchange. Inspect and resolve failed
messages before replaying them.

If old courier queues already exist, drain/reconcile their pending messages before
retiring them; this version does not consume the old separate queues. The shared
exchange and the new queue can be overridden with ORDER_EVENTS_EXCHANGE and
COURIER_EVENTS_QUEUE.

The available endpoint does not reserve a courier. Concurrent orders can select
the same courier before the assignment event arrives. Conflicting assignments are
rejected, not overwritten. Exclusive allocation needs a reservation protocol with
Order Service; RabbitMQ alone cannot make a prior HTTP lookup atomic.

## Database and run

Use Java 17 and PostgreSQL/RabbitMQ, then run `gradlew.bat bootRun`.
Configure DB_URL, DB_USERNAME, DB_PASSWORD, RABBITMQ_HOST, RABBITMQ_PORT,
RABBITMQ_USERNAME, RABBITMQ_PASSWORD, and SERVER_PORT as needed.
Liquibase now points at the existing vers1.yaml and an additive vers2.yaml.
Existing FIN codes remain nullable for legacy records; all new/updated profiles
require a FIN through request validation. Populate real FINs for legacy rows before
adding a database NOT NULL constraint. Existing BUSY rows need reconciliation with
their real order IDs before event-driven tracking can release them safely.

Run `gradlew.bat test` for H2-backed migration, service, and HTTP tests. These tests
disable broker startup; live PostgreSQL locking and RabbitMQ/DLQ delivery still need
infrastructure integration checks.
