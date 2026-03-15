package com.acme.acme_order_api.Pedidos.service;

import com.acme.acme_order_api.Pedidos.dto.EnviarPedido;
import com.acme.acme_order_api.Pedidos.dto.EnviarPedidoRespuesta;
import com.acme.acme_order_api.Pedidos.dto.PedidoRequest;
import com.acme.acme_order_api.Pedidos.dto.PedidoResponse;
import com.acme.acme_order_api.Pedidos.model.mapperXML.EnvioPedidoResponse;
import com.acme.acme_order_api.Pedidos.model.mapperXML.SoapEnvelopeResponse;
import com.acme.acme_order_api.SSLUtil.SSLUtil;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
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
    public PedidoResponse procesarPedido(PedidoRequest request) throws Exception {
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
     * Convierte la respuesta XML recibida desde el servicio SOAP externo
     * a un objeto {@link PedidoResponse} que será devuelto como JSON en la API REST.
     *
     * <p>Este método realiza las siguientes operaciones:</p>
     * <ol>
     *     <li>Deserializa el XML SOAP usando {@link com.fasterxml.jackson.dataformat.xml.XmlMapper}
     *         a objetos Java correspondientes al envelope, body y response.</li>
     *     <li>Extrae los valores de código y mensaje del pedido.</li>
     *     <li>Mapea esos valores a la clase {@link EnviarPedidoRespuesta} y luego a {@link PedidoResponse}.</li>
     * </ol>
     *
     * @param xml La cadena XML recibida desde el servicio externo.
     *            Debe cumplir con el formato SOAP esperado:
     *            <pre>
     *            &lt;soapenv:Envelope&gt;
     *                &lt;soapenv:Body&gt;
     *                    &lt;EnvioPedidoAcmeResponse&gt;
     *                        &lt;EnvioPedidoResponse&gt;
     *                            &lt;Codigo&gt;80375472&lt;/Codigo&gt;
     *                            &lt;Mensaje&gt;Entregado exitosamente al cliente&lt;/Mensaje&gt;
     *                        &lt;/EnvioPedidoResponse&gt;
     *                    &lt;/EnvioPedidoAcmeResponse&gt;
     *                &lt;/soapenv:Body&gt;
     *            &lt;/soapenv:Envelope&gt;
     *            </pre>
     *
     * @return {@link PedidoResponse} Objeto que representa la respuesta mapeada en JSON,
     *         incluyendo el código de envío y el estado del pedido.
     *
     * @throws Exception si ocurre un error durante la deserialización del XML
     *                   o si la estructura del XML no coincide con las clases de mapeo.
     */
    private PedidoResponse convertirRespuesta(String xml) throws Exception {
        XmlMapper xmlMapper = new XmlMapper();

        // Deserializamos el XML a objetos Java
        SoapEnvelopeResponse envelope = xmlMapper.readValue(xml, SoapEnvelopeResponse.class);

        EnvioPedidoResponse xmlResponse =
                envelope.getBody()
                        .getEnvioPedidoAcmeResponse()
                        .getEnvioPedidoResponse();

        // Mapeamos a nuestro objeto JSON
        EnviarPedidoRespuesta respuesta = new EnviarPedidoRespuesta();
        respuesta.setCodigoEnvio(xmlResponse.getCodigo());
        respuesta.setEstado(xmlResponse.getMensaje());

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
