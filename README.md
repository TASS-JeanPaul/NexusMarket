# NexusMarket

Marketplace that connects buyers and sellers, built as a decision support
system (DSS). The code is organised into the three subsystems a DSS is made
of: data management, model management and dialog management.

## Requirements

- Java 17

Maven does not need to be installed: the project ships the wrapper (`mvnw.cmd`).

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

```
docker compose up -d
mvnw.cmd spring-boot:run
```

## Structure

Each first level folder is one subsystem of the DSS.

```
src/main/java/application/
├── data/                         data management subsystem
│   ├── config/
│   │   └── DatabaseConnection.java   JDBC connection (Singleton)
│   ├── entities/                     business classes
│   ├── enums/                        status and type catalogues
│   ├── contracts/                    repository contracts
│   └── repositories/
│       ├── BaseRepository.java       operations common to every table
│       └── ...                       one repository per contract
├── models/                       model management subsystem
│   └── services/
│       ├── BaseService.java          validation and error handling shared by services
│       └── ...                       one service per functional domain
├── shared/
│   └── exceptions/               business, not found and data access errors
└── NexusMarketApplication.java
```

| Subsystem          | Folder    | Holds                                                     |
|--------------------|-----------|-----------------------------------------------------------|
| Data management    | `data/`   | the entities, the contracts and the JDBC access to them   |
| Model management   | `models/` | the services that process the data and apply the rules    |
| Dialog management  | `dialog/` | the entry points that answer a query (not written yet)    |

Entities live in the data subsystem because they describe data, not processing.
The enums belong there for the same reason: they state which values a datum may
take.

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

| Exception                   | Meaning                             |
|-----------------------------|-------------------------------------|
| `BusinessRuleException`     | a business rule was violated        |
| `ResourceNotFoundException` | the requested identifier does not exist |
| `DataAccessException`       | the database operation failed        |

## Pending

In the order they need to be built:

1. The decision models of the DSS, inside `models/indicators/`: inventory
   restocking, seller performance, catalogue profitability and after sales
   quality. They read aggregated queries over the transactional tables.
2. The dialog subsystem, inside `dialog/`, which exposes those indicators.

Separately, the service operations that touch several tables, such as the
checkout, are not atomic yet: `BaseRepository` opens one connection per
operation, so a failure halfway through leaves the previous steps applied.
Sharing the connection of the operation in progress is the step that closes it.
The same gap affects saving a buyer or a seller, which writes two rows: if the
second insert fails, the first one stays.

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
