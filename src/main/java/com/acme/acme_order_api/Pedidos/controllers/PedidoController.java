package com.acme.acme_order_api.Pedidos.controllers;

import com.acme.acme_order_api.Pedidos.dto.EnviarPedidoRespuesta;
import com.acme.acme_order_api.orderapi.service.PedidoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.acme.acme_order_api.Pedidos.dto.PedidoRequest;
import com.acme.acme_order_api.Pedidos.dto.PedidoResponse;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }


    /**
     * Endpoint REST encargado de recibir la solicitud de envío de un pedido.
     *
     * Este servicio recibe la información del pedido en formato JSON, la cual
     * es enviada al servicio de negocio para ser procesada. Internamente el
     * servicio realiza la transformación de JSON a XML para consumir un servicio
     * externo y posteriormente transforma la respuesta XML a JSON.
     *
     * En caso de presentarse un error al consumir el servicio externo (por ejemplo
     * cuando el endpoint proporcionado no se encuentra disponible), el sistema
     * genera una respuesta controlada para evitar que la aplicación falle y
     * permitir continuar con la ejecución de la API.
     *
     * Flujo del método:
     * 1. Recibe el request con la información del pedido.
     * 2. Invoca el servicio {@link PedidoService#procesarPedido(PedidoRequest)}.
     * 3. Retorna la respuesta procesada al cliente.
     * 4. Si ocurre un error, retorna una respuesta alternativa controlada.
     *
     * @param request Objeto {@link PedidoRequest} que contiene la información
     *                del pedido enviada por el cliente:
     *                - numPedido
     *                - cantidadPedido
     *                - codigoEAN
     *                - nombreProducto
     *                - numDocumento
     *                - direccion
     *
     * @return {@link ResponseEntity} que contiene un objeto {@link PedidoResponse}
     *         con la información del resultado del procesamiento del pedido:
     *
     *         codigoEnvio : código generado por el sistema de envíos
     *         estado      : estado del envío del pedido
     *
     * Ejemplo de respuesta:
     *
     * {
     *   "enviarPedidoRespuesta": {
     *     "codigoEnvio": "80375472",
     *     "estado": "Entregado exitosamente al cliente"
     *   }
     * }
     */
    @PostMapping
    public ResponseEntity<PedidoResponse> enviarPedido(@RequestBody PedidoRequest request) {

        //Variable que controla la respuesta de la aplicación.
        //true: muestra error generado por la petición HTTPs.
        //false: Simula el resultado esperado.
        boolean respuestaServicioExterno = true;
        PedidoResponse response= new PedidoResponse();

        try {
            response = pedidoService.procesarPedido(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            //Debido a que el cURL facilitado en el documento tecnico no existe, se genera la respuesta esperada para que no se bloquee el programa.
            //Una vez solucionado el endpoint externo se debe quitar el bloque de codigo if.
            //y todo el codigo del cath adicionando para registro en un log
            if (respuestaServicioExterno){
                EnviarPedidoRespuesta respuesta = new EnviarPedidoRespuesta();
                respuesta.setCodigoEnvio("-99");
                respuesta.setEstado("Error: "+e.getMessage());
                response.setEnviarPedidoRespuesta(respuesta);
                return ResponseEntity.ok(response);
            }

            EnviarPedidoRespuesta respuesta = new EnviarPedidoRespuesta();
            respuesta.setCodigoEnvio("80375472");
            respuesta.setEstado("Entregado exitosamente al cliente");

            response.setEnviarPedidoRespuesta(respuesta);

            return ResponseEntity.ok(response);
        }
    }
}
