package application.domain.models;

import application.domain.enums.TipoProducto;

/**
 * Producto fisico: necesita inventario en bodega y despacho al comprador.
 */
public class ProductoFisico extends Producto {

    @Override
    public TipoProducto getTipo() {
        return TipoProducto.FISICO;
    }

    @Override
    public boolean requiereEnvio() {
        return true;
    }

    @Override
    public boolean manejaInventario() {
        return true;
    }
}
