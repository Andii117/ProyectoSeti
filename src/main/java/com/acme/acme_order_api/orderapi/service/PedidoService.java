package com.acme.acme_order_api.orderapi.service;


import com.acme.acme_order_api.Pedidos.dto.EnviarPedido;
import com.acme.acme_order_api.Pedidos.dto.EnviarPedidoRespuesta;
import com.acme.acme_order_api.Pedidos.dto.PedidoRequest;
import com.acme.acme_order_api.Pedidos.dto.PedidoResponse;
import com.acme.acme_order_api.SSLUtil.SSLUtil;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class PedidoService {

    private final String URL =
            "https://run.mocky.io/v3/19217075-6d4e-4818-98bc-416d1feb7b84";

    public PedidoResponse procesarPedido(PedidoRequest request) {

        RestTemplate restTemplate = new RestTemplate();

        String xmlRequest = construirXml(request);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_XML);

        HttpEntity<String> entity = new HttpEntity<>(xmlRequest, headers);

        // Desactiva validación SSL
        SSLUtil.disableSSLVerification();
        ResponseEntity<String> response = restTemplate.exchange(
                URL,
                HttpMethod.POST,
                entity,
                String.class
        );

        String xmlResponse = response.getBody();

        return convertirRespuesta(xmlResponse);
    }

    private String construirXml(PedidoRequest request) {

        EnviarPedido informacionPedido = request.getEnviarPedido();

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
                informacionPedido.getNumPedido(),
                informacionPedido.getCantidadPedido(),
                informacionPedido.getCodigoEAN(),
                informacionPedido.getNombreProducto(),
                informacionPedido.getNumDocumento(),
                informacionPedido.getDireccion()
        );
    }

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

    private String extraerValor(String xml, String tag) {

        int inicio = xml.indexOf("<" + tag + ">") + tag.length() + 2;
        int fin = xml.indexOf("</" + tag + ">");

        return xml.substring(inicio, fin);
    }
}
