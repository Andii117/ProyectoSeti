package com.acme.acme_order_api.orderapi.service;

import com.acme.acme_order_api.Pedidos.dto.EnviarPedido;
import com.acme.acme_order_api.Pedidos.dto.EnviarPedidoRespuesta;
import com.acme.acme_order_api.Pedidos.dto.PedidoRequest;
import com.acme.acme_order_api.Pedidos.dto.PedidoResponse;
import com.acme.acme_order_api.SSLUtil.SSLUtil;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

@Service
public class PedidoService {

    @Value("${acme.api.url}")
    private String url;

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Procesa un pedido recibido desde la API REST.
     *
     * Flujo:
     * 1. Convierte el JSON recibido a XML
     * 2. Envía la petición al servicio externo
     * 3. Convierte la respuesta XML a JSON
     *
     * @param request Información del pedido enviada por el cliente
     * @return PedidoResponse con el código de envío y estado del pedido
     */
    public PedidoResponse procesarPedido(PedidoRequest request) {
        desactivarSSL();

        String xmlRequest = construirXml(request);

        String xmlResponse = enviarPeticion(xmlRequest);

        return convertirRespuesta(xmlResponse);
    }

    /**
     * Desactiva la validación SSL para permitir consumir servicios HTTPS
     * en entornos de desarrollo.
     */
    private void desactivarSSL() {
        try {
            SSLUtil.disableSSLVerification();
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            throw new RuntimeException("Error desactivando SSL", e);
        }
    }

    /**
     * Construye el XML requerido por el servicio SOAP a partir del request JSON.
     *
     * @param request Pedido recibido desde la API
     * @return XML formateado listo para enviarse al servicio externo
     */
    private String construirXml(PedidoRequest request) {

        EnviarPedido pedido = request.getEnviarPedido();

        return """
        <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                          xmlns:env="http://WSDLs/EnvioPedidos/EnvioPedidosAcme">
           <soapenv:Header/>
           <soapenv:Body>
              <env:EnvioPedidoAcme>
                 <EnvioPedidoRequest>
                    <pedido>%s</pedido>
                    <Cantidad>%s</Cantidad>
                    <EAN>%s</EAN>
                    <Producto>%s</Producto>
                    <Cedula>%s</Cedula>
                    <Direccion>%s</Direccion>
                 </EnvioPedidoRequest>
              </env:EnvioPedidoAcme>
           </soapenv:Body>
        </soapenv:Envelope>
        """.formatted(
                pedido.getNumPedido(),
                pedido.getCantidadPedido(),
                pedido.getCodigoEAN(),
                pedido.getNombreProducto(),
                pedido.getNumDocumento(),
                pedido.getDireccion()
        );
    }

    /**
     * Envía la petición XML al servicio externo.
     *
     * @param xmlRequest XML construido con la información del pedido
     * @return XML de respuesta del servicio externo
     */
    private String enviarPeticion(String xmlRequest) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_XML);

        HttpEntity<String> entity = new HttpEntity<>(xmlRequest, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                entity,
                String.class
        );

        return response.getBody();
    }

    /**
     * Convierte la respuesta XML del servicio externo a un objeto JSON.
     *
     * @param xml XML recibido desde el servicio
     * @return PedidoResponse con los datos mapeados
     */
    private PedidoResponse convertirRespuesta(String xml) {

        String codigo = extraerValor(xml, "Codigo");
        String mensaje = extraerValor(xml, "Mensaje");

        EnviarPedidoRespuesta respuesta = new EnviarPedidoRespuesta();
        respuesta.setCodigoEnvio(codigo);
        respuesta.setEstado(mensaje);

        PedidoResponse response = new PedidoResponse();
        response.setEnviarPedidoRespuesta(respuesta);

        return response;
    }

    /**
     * Extrae el valor de una etiqueta XML específica.
     *
     * @param xml XML completo
     * @param tag etiqueta a buscar
     * @return valor contenido dentro de la etiqueta
     */
    private String extraerValor(String xml, String tag) {

        int inicio = xml.indexOf("<" + tag + ">") + tag.length() + 2;
        int fin = xml.indexOf("</" + tag + ">");

        return xml.substring(inicio, fin);
    }

}
