package com.acme.acme_order_api.Pedidos.dto;

import lombok.Data;

@Data
public class EnviarPedido {

    private String numPedido;
    private String cantidadPedido;
    private String codigoEAN;
    private String nombreProducto;
    private String numDocumento;
    private String direccion;

}
