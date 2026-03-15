package com.acme.acme_order_api.Pedidos.model.mapperXML;


import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

/** Envelope SOAP */
public class SoapEnvelopeResponse {
    @JacksonXmlProperty(localName = "Body")
    private SoapBody body;

    public SoapBody getBody() { return body; }
    public void setBody(SoapBody body) { this.body = body; }
}
