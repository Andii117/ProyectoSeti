package com.acme.acme_order_api.Pedidos.dto;


import lombok.Data;

@Data
public class EnviarPedidoRespuesta {
    private String codigoEnvio;
    private String estado;
}