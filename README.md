# NexusMarket

Marketplace that connects buyers and sellers, built as a decision support
system (DSS). The code is organised into the three subsystems a DSS is made
of: data management, model management and dialog management.

## Requirements

- Java 17

Maven does not need to be installed: the project ships the wrapper (`mvnw.cmd`).

The commands below are written for PowerShell, which does not look for programs
in the current folder, so the wrapper is always called as `.\mvnw.cmd`. The same
spelling works in `cmd.exe`.

## Database

The project ships its own PostgreSQL in Docker, on port 5433 so it does not
collide with any other database on the machine.

```
docker compose up -d
```

The first time the container is created it runs `01-schema.sql` and then
`02-seed.sql` from `src/main/resources/db/`, so the tables and a small coherent
set of data are ready without any extra step.

Those two scripts only run when the volume is empty. After changing the schema
the database has to be recreated:

```
docker compose down -v
docker compose up -d
```

## Run

Everything in containers, the database and the application:

```
docker compose up -d
```

The API then answers on port 8081. To run the application on the host instead,
against the database in Docker, start only the database and use the wrapper:

```
docker compose up -d postgres
.\mvnw.cmd spring-boot:run
```

The API answers on `http://localhost:8080` when it runs on the host, and on
`http://localhost:8081` when it runs inside Docker. Every route asks for a token
except the four listed under Authentication.

To check that it is alive:

```
curl http://localhost:8080/api/health
```

```json
{ "api": "UP", "database": "UP", "checkedAt": "2026-10-27T18:46:06" }
```

It answers 200 when both answer, and 503 with `"database": "DOWN"` when the API
is up but cannot reach PostgreSQL. The check opens a real connection instead of
reporting the state of a pool, so it cannot claim the database is healthy while
the container is stopped.

## Swagger

Once the API is running, the whole surface is browsable and executable at:

```
http://localhost:8080/swagger-ui.html
```

The sections are numbered in the order of the business flow, from `00 Autenticacion`
and `00 Salud` down to `13 Reportes`, so the page can be read top to bottom: register the
participants, publish the catalogue, load stock, fill the cart, check out, pay,
dispatch, and return. Every operation has a `Try it out` button, and fields such
as the product `type` or an order `status` are offered as a list of the values
the enum allows, so a request can be sent without typing anything by hand.

The raw contract lives at `/v3/api-docs`.

Swagger is the fastest way to explore and to run one call. What it cannot do is
carry an identifier from one call into the next, so a whole purchase still has
to be walked by hand. `NexusMarket.postman_collection.json` does that part: it
runs the full flow in order and chains every identifier on its own.

## Debug

In VS Code, press F5 and pick `NexusMarket API`: breakpoints inside the
controllers, the services and the repositories are hit directly.

To attach a debugger to a process started from the command line:

```
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.jvmArguments=-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005"
```

and then choose `NexusMarket API (attach on 5005)`.

## Structure

Each first level folder is one subsystem of the DSS.

```
src/main/java/application/
├── data/                         data management subsystem
│   ├── config/                       JDBC connection and transactions
│   ├── entities/                     business classes
│   ├── enums/                        status and type catalogues
│   ├── views/                        aggregated rows the reports return
│   ├── ports/out/                    the contracts the model asks the data for
│   └── repositories/                 the adapters that answer them in SQL
├── models/                       model management subsystem
│   ├── ports/in/                     the operations the outside world may ask for
│   ├── ports/out/                    what the model needs from the outside
│   ├── usecases/                     the adapters that answer each input port
│   ├── services/                     the domain rules
│   └── reports/                      the classifications the reports carry
├── dialog/                       dialog management subsystem
│   ├── config/                       security and OpenAPI
│   ├── security/                     token issuing and the authentication filter
│   ├── requests/                     bodies that are not entities
│   ├── responses/                    uniform error body and token
│   ├── controllers/                  one controller per functional domain
│   └── ApiExceptionHandler.java      turns exceptions into HTTP codes
├── shared/
│   └── exceptions/               business, authentication, not found, data access
└── NexusMarketApplication.java
```

| Subsystem          | Folder    | Holds                                                     |
|--------------------|-----------|-----------------------------------------------------------|
| Data management    | `data/`   | the entities, the output ports and the JDBC that answers them |
| Model management   | `models/` | the input ports, the use cases and the services that apply the rules |
| Dialog management  | `dialog/` | the REST entry points, the security and the error translation |

Entities live in the data subsystem because they describe data, not processing.
The enums belong there for the same reason: they state which values a datum may
take.

### The path of a request

A request always travels the same way, and never backwards:

```
RestController → PortIn → UseCase → DomainService → PortOut → Persistence Adapter
```

A controller depends on an input port, never on a service, so the dialog
subsystem does not know which class ends up doing the work. A service depends on
an output port, never on a repository, so the model subsystem does not know that
SQL is what answers it. Both ends of the model are interfaces it owns; the
adapters on either side are replaceable without touching a single rule.

## Data access

Jakarta Persistence (JPA) is not used. The database is accessed through JDBC:

- `DatabaseConnection` reads the configuration only once (Singleton pattern) and
  hands out a new connection for every operation, so several people can use the
  application at the same time without interfering with each other.
- `IRepository<T>` defines the operations every repository must fulfil:
  `findAll`, `findById`, `save`, `update` and `delete`.
- `BaseRepository<T>` is an abstract class that already solves the queries that
  are the same for any table. Each concrete repository only states which table
  it works on and how to turn a row into an object.

Models hold relations through the identifier of the other table
(`Long orderId`, `Long buyerId`), just like the database columns do.

## Inheritance

| Parent class | Child classes                     | Reason                                                              |
|--------------|-----------------------------------|---------------------------------------------------------------------|
| `User`       | `Buyer`, `Seller`                 | Both are system users and each one holds a single role               |
| `Product`    | `PhysicalProduct`, `DigitalProduct` | Physical products need stock and shipping; digital ones are delivered once paid |

`Product` is abstract and forces its children to answer `getType()`,
`requiresShipping()` and `managesInventory()`.

### How each hierarchy reaches the database

The two hierarchies are stored differently, following the same criterion that
justified the inheritance in the first place.

`Buyer` and `Seller` add data of their own, so each one gets its own table whose
primary key is also a foreign key to `app_user`:

```
app_user (id, full_name, identity_document, email, ...)
buyer    (id -> app_user.id, commercial_status)
seller   (id -> app_user.id, business_name, tax_id, registered_by_id)
```

That single column states "a buyer is a user": there cannot be a buyer without
its user, and two buyers cannot share one user. `BuyerRepository` and
`SellerRepository` are the only ones that write to two tables, and they override
`findById` and `findAll` because the inherited query reads a single table.

`PhysicalProduct` and `DigitalProduct` add no data at all, only behaviour, so
they share the `product` table and a `type` column tells them apart.
`ProductRepository.mapRow` reads that column and builds the right subclass,
which is the one place where the polymorphism is rebuilt out of the database.
Everything above it keeps talking to `Product`.

## Services

Each service covers one functional domain and concentrates the rules of that
domain. They depend on repository interfaces, never on JDBC, and receive those
dependencies through their constructor.

| Service            | Rules it enforces                                                                                  |
|--------------------|----------------------------------------------------------------------------------------------------|
| `UserService`      | unique email and identity document, only active users operate, the role cannot be changed          |
| `BuyerService`     | a single primary address per buyer, a suspended buyer cannot place orders                          |
| `SellerService`    | sellers cannot register themselves: an administrator must do it, unique tax identification number   |
| `WarehouseService` | a seller warehouse requires its owner, a marketplace warehouse cannot have one                     |
| `CatalogService`   | unique product code and SKU, existing category, a discontinued product is not modified again       |
| `InventoryService` | no negative stock, available and reserved quantities kept apart, every change records a movement    |
| `CartService`      | one active cart per buyer, published products only, positive quantities                            |
| `OrderService`     | checkout freezes name and price, reserves stock, and a finalized order is never modified            |
| `InvoiceService`   | one invoice per order with its own numbering, a paid invoice cannot be voided                       |
| `ShipmentService`  | only paid orders that contain shippable products, the operator must hold the logistics role         |
| `ReturnService`    | only delivered orders, and no more units than the ones bought and not yet returned                  |
| `RefundService`    | the amount never exceeds the value of the returned products, an administrator authorizes it          |

`BaseService` is an abstract class that solves what every service repeats:
translating `SQLException`, demanding that an entity exists, validating
arguments and generating the document numbers.

The services are where the inheritance of `Product` pays off. Neither
`InventoryService` nor `ShipmentService` asks `getType()`: they ask
`managesInventory()` and `requiresShipping()`, so a third kind of product would
not force a single change in them.

## Errors

No service exposes `SQLException`, so the layers above never learn that JDBC
sits underneath.

| Exception                   | Meaning                                 |
|-----------------------------|-----------------------------------------|
| `BusinessRuleException`     | a business rule was violated            |
| `AuthenticationException`   | nobody is identified behind the request |
| `AuthorizationException`    | the identified user may not do this     |
| `ResourceNotFoundException` | the requested identifier does not exist |
| `DataAccessException`       | the database operation failed           |

## API

One controller per functional domain, each one delegating to its input port. A
purchase travels through these requests:

```
GET  /api/health                        is the API alive, and can it reach the database
POST /api/auth/login                    obtain the token every other route asks for
POST /api/users                         register an administrator or an operator
POST /api/sellers?administratorId=1     register a seller, never self registered
POST /api/buyers                        register a buyer
POST /api/buyers/{id}/addresses         add a delivery address
POST /api/categories                    create a category
POST /api/products                      publish, with type PHYSICAL or DIGITAL
POST /api/inventory                     register stock in a warehouse
POST /api/carts/{buyerId}/items         add to the cart
POST /api/orders/checkout               turn the cart into an order
POST /api/orders/{id}/payment           pay, which issues the invoice
POST /api/shipments                     dispatch
PUT  /api/shipments/{id}/delivery       deliver, which closes the order
POST /api/returns                       request a return
PUT  /api/returns/{id}/reception        receive it, which puts the stock back
POST /api/refunds                       refund the money
GET  /api/reports/...                   consolidated administrative information
```

Each domain also answers `GET` requests for its own data.

`Product` is abstract, so Jackson cannot deserialise it. `POST /api/products`
takes a body carrying a `type` field, and the controller builds the matching
subclass. It is the only place where the API has to name the type explicitly.

### Validation

Request bodies carry their own rules as annotations, so a malformed request
never reaches a service: a price must be positive, a quantity greater than zero,
an email well formed. Spring checks them before the controller runs and the
handler turns the failure into a 400 that names every field that was wrong.
Swagger reads the same annotations, so the documentation states the rules
without anyone writing them twice.

The services keep validating anyway. The annotations guard the shape of the
request; the services guard the business, and only they know that a product must
be published or that a buyer must not be suspended.

### Errors

A single `@RestControllerAdvice` turns the exceptions into HTTP codes, so
no controller holds a `try/catch`:

| Exception                   | Code | Example                                      |
|-----------------------------|------|----------------------------------------------|
| `BusinessRuleException`     | 400  | insufficient stock, a seller self registering |
| `AuthenticationException`   | 401  | no token, wrong credentials, expired token    |
| `AuthorizationException`    | 403  | the wrong role, or another user's information |
| `ResourceNotFoundException` | 404  | an identifier that does not exist             |
| `DataAccessException`       | 500  | the database failed                           |

Every error answers with the same JSON shape, carrying the message the service
wrote:

```json
{
  "status": 400,
  "error": "BUSINESS_RULE_VIOLATED",
  "message": "The order 1 is finalized and cannot be modified under any circumstance"
}
```

## Authentication

Constraint RG-01 of the specification says every operation must be performed by
an authenticated user, so the API hands out a token and asks for it on every
route except the ones listed below.

```
POST /api/auth/login     { "email": "...", "password": "..." }
```

It answers with a signed token, the role it carries and the moment it expires.
From then on each request sends it:

```
Authorization: Bearer <token>
```

Passwords are never stored in the clear: they are hashed with BCrypt, and the
field is write only, so a user can be created with a password but no response
ever returns one.

Four things answer without a token: the login, the health check, the buyer
self-registration and the Swagger pages. A seller cannot self-register
because the specification says an administrator incorporates them.

### Who may do what

Constraint RG-03 says no participant administers information outside their role,
and the specification carries a responsibility matrix. The rules live in
`SecurityConfiguration` and read as that matrix:

| Area                     | Roles allowed                                      |
|--------------------------|----------------------------------------------------|
| Users                    | administrator                                      |
| Seller registration      | administrator                                      |
| Reports                  | administrator, supervisor                          |
| Catalogue changes        | seller, administrator                              |
| Inventory                | seller, logistics operator                         |
| Warehouses               | seller, logistics operator, administrator          |
| Shipments                | logistics operator, administrator                  |
| Cart and checkout        | buyer                                              |
| Orders                   | buyer, seller, logistics operator, administrator   |
| Returns                  | buyer requests, administrator or supervisor reviews, operator receives |
| Refunds                  | buyer, administrator                               |

A request with no token is refused with 401; a token for the wrong role is
refused with 403. An administrator cannot open a cart, and a buyer cannot read
the reports.

Belonging to the right role is not enough on its own. A buyer operates on their
own cart and nobody else's: the cart routes compare the identifier in the path
against the user the token identifies, so a buyer asking for another buyer's
cart is refused even though the role is right.

## Reports

Objective OBJ-12 asks to consolidate administrative information for consultation.
Six reports answer it, each one adding the figures and then classifying them, so
the answer supports a decision instead of only describing the past:

| Route                               | Answers                                              |
|-------------------------------------|------------------------------------------------------|
| `/api/reports/sales-by-seller`      | revenue and return rate per seller, with a standing   |
| `/api/reports/sales-by-category`    | revenue per category and its share of the total       |
| `/api/reports/sales-by-product`     | units and revenue per product, with a suggested action |
| `/api/reports/orders-by-status`     | how many orders sit in each state                     |
| `/api/reports/inventory`            | stock per warehouse, flagged by how little is left    |
| `/api/reports/after-sales`          | returns per product and failure rate per carrier      |

A seller is rated `EXCELLENT`, `SOLID`, `WATCH` or `AT_RISK` by its return rate;
a product `STAR`, `STEADY`, `SLOW` or `DEAD` by what it sold; stock
`OUT_OF_STOCK`, `CRITICAL`, `LOW` or `HEALTHY` by what is left. All of them take
`?days=` to move the window, 90 days by default.

The aggregation lives in the data subsystem, in a repository of its own.
It cannot extend `BaseRepository`, which implements the contract with `save`,
`update` and `delete`: an aggregate query neither saves nor deletes anything.

## Transactions

A business operation that writes more than one row runs inside a single
database transaction, so it either happens whole or not at all.

`TransactionManager` keeps the connection of the operation in progress in a
`ThreadLocal`. `BaseRepository` asks it for a connection instead of opening one
of its own: inside a transaction every repository call reuses the same
connection and none of them closes it, and outside a transaction each call still
gets a fresh connection that it closes when it is done.

Nested calls join the transaction already in progress rather than starting
another one. This matters because the checkout reserves stock through
`InventoryService`, which is transactional on its own: only the outermost
operation commits, so a reservation cannot be committed before the order that
justifies it exists.

The 27 operations that write several rows use it, among them the checkout, the
payment, the cancellation, every inventory movement, and saving a buyer or a
seller, which writes to two tables.

A failed checkout leaves nothing behind: with 50 units in stock, a cart whose
second line exceeds the available stock answers 400 and the first line's
reservation is rolled back, so the stock stays at 50 and no order row is
written.

## Pending

Nothing from the functional specification. The twelve objectives and the three
general constraints are covered. What is left is optional: a document database
beside PostgreSQL, which the specification does not ask for since it puts
storage decisions out of its scope.

## Models

| Domain     | Classes                                                                    |
|------------|----------------------------------------------------------------------------|
| Users      | User, Role, UserStatus                                                     |
| Buyers     | Buyer, Address, CommercialStatus                                           |
| Sellers    | Seller                                                                     |
| Warehouses | Warehouse, WarehouseType                                                   |
| Catalogue  | Product, PhysicalProduct, DigitalProduct, Variant, Category, ProductType, ProductStatus |
| Inventory  | Inventory, InventoryMovement, MovementType, InventoryStatus                |
| Cart       | Cart, CartItem                                                             |
| Orders     | Order, OrderDetail, OrderStatus                                            |
| Invoicing  | Invoice, InvoiceStatus                                                     |
| Shipping   | Shipment, ShipmentStatus                                                   |
| Returns    | ProductReturn, ProductReturnDetail, ReturnStatus                           |
| Refunds    | Refund, RefundStatus, RefundMethod                                         |
