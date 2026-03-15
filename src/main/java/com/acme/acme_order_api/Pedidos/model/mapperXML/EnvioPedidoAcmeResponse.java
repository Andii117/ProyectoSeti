package com.acme.acme_order_api.Pedidos.model.mapperXML;


import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

/** Contenedor intermedio de la respuesta SOAP */
public class EnvioPedidoAcmeResponse {
    @JacksonXmlProperty(localName = "EnvioPedidoResponse")
    private EnvioPedidoResponse envioPedidoResponse;

    public EnvioPedidoResponse getEnvioPedidoResponse() { return envioPedidoResponse; }
    public void setEnvioPedidoResponse(EnvioPedidoResponse envioPedidoResponse) { this.envioPedidoResponse = envioPedidoResponse; }
}
