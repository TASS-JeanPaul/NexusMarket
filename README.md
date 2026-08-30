# NexusMarket

Marketplace que conecta compradores y vendedores.
Este proyecto contiene el modelo del negocio y la capa de acceso a datos.

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
├── config/
│   └── ConexionBD.java          conexion JDBC (Singleton)
├── domain/
│   ├── enums/                   catalogos de estados y tipos
│   ├── interfaces/
│   │   └── IRepositorio.java    contrato de los repositorios
│   └── models/                  clases del negocio
├── repositories/
│   └── RepositorioBase.java     operaciones comunes a toda tabla
└── NexusMarketApplication.java
```

## Acceso a datos

No se usa Jakarta Persistence (JPA). El acceso a la base se hace con JDBC:

- `ConexionBD` administra una unica conexion (patron Singleton) y la vuelve a
  abrir si se cerro o se perdio.
- `IRepositorio<T>` define las operaciones que todo repositorio debe cumplir:
  `listarTodos`, `buscarPorId`, `guardar`, `actualizar` y `eliminar`.
- `RepositorioBase<T>` es una clase abstracta que ya resuelve las consultas que
  son iguales para cualquier tabla. Cada repositorio concreto solo indica sobre
  que tabla trabaja y como convertir una fila en un objeto.

Los modelos guardan las relaciones con el identificador de la otra tabla
(`Long pedidoId`, `Long compradorId`), igual que las columnas de la base.

## Herencia

| Clase padre | Clases hijas                       | Motivo                                                          |
|-------------|------------------------------------|-----------------------------------------------------------------|
| `Usuario`   | `Comprador`, `Vendedor`            | Ambos son usuarios del sistema y cada uno cumple un unico rol    |
| `Producto`  | `ProductoFisico`, `ProductoDigital`| El fisico requiere inventario y despacho; el digital se entrega al pagar |

`Producto` es abstracta y obliga a sus hijas a responder `getTipo()`,
`requiereEnvio()` y `manejaInventario()`.

## Modelos

| Dominio      | Clases                                                             |
|--------------|--------------------------------------------------------------------|
| Usuarios     | Usuario, Rol, EstadoUsuario                                        |
| Compradores  | Comprador, Direccion, EstadoComercial                              |
| Vendedores   | Vendedor                                                           |
| Bodegas      | Bodega, TipoBodega                                                 |
| Catalogo     | Producto, ProductoFisico, ProductoDigital, Variante, Categoria, TipoProducto, EstadoProducto |
| Inventario   | Inventario, MovimientoInventario, TipoMovimiento, EstadoInventario |
| Carrito      | Carrito, ItemCarrito                                               |
| Pedidos      | Pedido, DetallePedido, EstadoPedido                                |
| Facturacion  | Factura, EstadoFactura                                             |
| Envios       | Envio, EstadoEnvio                                                 |
| Devoluciones | Devolucion, DetalleDevolucion, EstadoDevolucion                    |
| Reembolsos   | Reembolso, EstadoReembolso, MetodoReembolso                        |
