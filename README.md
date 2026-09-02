# NexusMarket

Marketplace that connects buyers and sellers.
This project contains the business model and the data access layer.

## Requirements

- Java 17

Maven does not need to be installed: the project ships the wrapper (`mvnw.cmd`).

## Run

```
mvnw.cmd spring-boot:run
```

## Structure

```
src/main/java/application/
├── config/
│   └── DatabaseConnection.java   JDBC connection (Singleton)
├── domain/
│   ├── enums/                    status and type catalogues
│   ├── interfaces/
│   │   └── IRepository.java      repository contract
│   └── models/                   business classes
├── repositories/
│   └── BaseRepository.java       operations common to every table
└── NexusMarketApplication.java
```

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
