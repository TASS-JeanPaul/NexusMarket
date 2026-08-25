# NexusMarket

Modelo de datos del marketplace NexusMarket.

## Requisitos

- Java 17

No hace falta instalar Maven: el proyecto trae el wrapper (`mvnw.cmd`).

## Ejecutar

```
mvnw.cmd spring-boot:run
```

## Estructura

```
src/main/java/application/
├── domain/models/               entidades y enums del negocio
└── NexusMarketApplication.java  clase principal
```

## Modelos

| Dominio      | Clases                                                             |
|--------------|--------------------------------------------------------------------|
| Usuarios     | Usuario, Rol, EstadoUsuario                                        |
| Compradores  | Comprador, Direccion, EstadoComercial                              |
| Vendedores   | Vendedor, EstadoVendedor                                           |
| Bodegas      | Bodega, TipoBodega                                                 |
| Catalogo     | Producto, Variante, Categoria, TipoProducto, EstadoProducto        |
| Inventario   | Inventario, MovimientoInventario, TipoMovimiento, EstadoInventario |
| Carrito      | Carrito, ItemCarrito                                               |
| Pedidos      | Pedido, DetallePedido, EstadoPedido                                |
| Facturacion  | Factura, EstadoFactura                                             |
| Envios       | Envio, EstadoEnvio                                                 |
| Devoluciones | Devolucion, DetalleDevolucion, EstadoDevolucion                    |
| Reembolsos   | Reembolso, EstadoReembolso, MetodoReembolso                        |
