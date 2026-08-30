package application.domain.models;

import application.domain.enums.TipoProducto;

/**
 * Producto digital: se entrega de una vez cuando se confirma el pago,
 * por eso no pasa por bodega ni por envio.
 */
public class ProductoDigital extends Producto {

    @Override
    public TipoProducto getTipo() {
        return TipoProducto.DIGITAL;
    }

    @Override
    public boolean requiereEnvio() {
        return false;
    }

    @Override
    public boolean manejaInventario() {
        return false;
    }
}
