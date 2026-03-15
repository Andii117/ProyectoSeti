package com.acme.acme_order_api.Pedidos.model.mapperXML;


import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

/** Body del SOAP */
public class SoapBody {
    @JacksonXmlProperty(localName = "EnvioPedidoAcmeResponse")
    private EnvioPedidoAcmeResponse envioPedidoAcmeResponse;

    public EnvioPedidoAcmeResponse getEnvioPedidoAcmeResponse() { return envioPedidoAcmeResponse; }
    public void setEnvioPedidoAcmeResponse(EnvioPedidoAcmeResponse envioPedidoAcmeResponse) { this.envioPedidoAcmeResponse = envioPedidoAcmeResponse; }
}
