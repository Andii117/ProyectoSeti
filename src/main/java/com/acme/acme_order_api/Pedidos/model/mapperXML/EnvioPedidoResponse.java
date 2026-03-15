package com.acme.acme_order_api.Pedidos.model.mapperXML;


import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

/** Contenedor de la respuesta SOAP */
public class EnvioPedidoResponse {
    @JacksonXmlProperty(localName = "Codigo")
    private String codigo;

    @JacksonXmlProperty(localName = "Mensaje")
    private String mensaje;

    // Getters y Setters
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }
}
