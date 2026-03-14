# ACME Pedido API

API REST desarrollada con **Java 17** y **Spring Boot** para el ciclo de abastecimiento de la compañía ACME.
El servicio recibe pedidos en **JSON**, los transforma a **XML (SOAP)** para enviarlos a un servicio externo, y luego transforma la respuesta **XML a JSON**.

---

# Arquitectura de la solución

El flujo de la aplicación es el siguiente:

```
Cliente (JSON)
       │
       ▼
API REST Spring Boot
       │
       ▼
Transformación JSON → XML
       │
       ▼
Consumo de servicio externo
       │
       ▼
Respuesta XML
       │
       ▼
Transformación XML → JSON
       │
       ▼
Respuesta al cliente
```

---

# Tecnologías utilizadas

* Java 17
* Spring Boot
* Maven
* Docker
* REST API

---

# Estructura del proyecto

```
src/main/java/com/acme/orderapi

controller
 └── PedidoController

service
 └── PedidoService

dto
 ├── PedidoRequest
 ├── PedidoResponse
 ├── EnviarPedido
 └── EnviarPedidoRespuesta

util
 └── SSLUtil

config
 └── RestTemplateConfig
```

---

# Endpoint expuesto

### Crear pedido

```
POST /api/pedidos
```

URL local:

```
http://localhost:8080/api/pedidos
```

---

# Request JSON

```json
{
  "enviarPedido": {
    "numPedido": "75630275",
    "cantidadPedido": "1",
    "codigoEAN": "00110000765191002104587",
    "nombreProducto": "Armario INVAL",
    "numDocumento": "1113987400",
    "direccion": "CR 72B 45 12 APT 301"
  }
}
```

---

# Transformación JSON → XML

```xml
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/">
   <soapenv:Body>
      <EnvioPedidoRequest>
         <pedido>75630275</pedido>
         <Cantidad>1</Cantidad>
         <EAN>00110000765191002104587</EAN>
         <Producto>Armario INVAL</Producto>
         <Cedula>1113987400</Cedula>
         <Direccion>CR 72B 45 12 APT 301</Direccion>
      </EnvioPedidoRequest>
   </soapenv:Body>
</soapenv:Envelope>
```

---

# Response XML

```xml
<soapenv:Envelope>
   <soapenv:Body>
      <EnvioPedidoResponse>
         <Codigo>80375472</Codigo>
         <Mensaje>Entregado exitosamente al cliente</Mensaje>
      </EnvioPedidoResponse>
   </soapenv:Body>
</soapenv:Envelope>
```

---

# Response JSON

```json
{
  "enviarPedidoRespuesta": {
    "codigoEnvio": "80375472",
    "estado": "Entregado exitosamente al cliente"
  }
}
```

---

# Ejecutar el proyecto

## 1. Clonar repositorio

```
git clone https://github.com/TU_USUARIO/acme-pedidos-api.git
```

---

## 2. Compilar proyecto

```
mvn clean package
```

Esto generará el archivo:

```
target/acme-order-api-0.0.1-SNAPSHOT.jar
```

---

## 3. Ejecutar aplicación

```
mvn spring-boot:run
```

La API quedará disponible en:

```
http://localhost:8080
```

---

# Ejecutar con Docker

## Construir imagen

```
docker build -t acme-pedidos-api .
```

## Ejecutar contenedor

```
docker run -p 8080:8080 acme-pedidos-api
```

La aplicación quedará disponible en:

```
http://localhost:8080/api/pedidos
```

---

# Prueba con Postman

### Endpoint

```
POST http://localhost:8080/api/pedidos
```

### Body

```json
{
  "enviarPedido": {
    "numPedido": "75630275",
    "cantidadPedido": "1",
    "codigoEAN": "00110000765191002104587",
    "nombreProducto": "Armario INVAL",
    "numDocumento": "1113987400",
    "direccion": "CR 72B 45 12 APT 301"
  }
}
```

### cURL

```
curl --location 'http://localhost:8080/api/pedidos' \
--header 'Content-Type: application/json' \
--data '{
  "enviarPedido": {
    "numPedido": "75630275",
    "cantidadPedido": "1",
    "codigoEAN": "00110000765191002104587",
    "nombreProducto": "Armario INVAL",
    "numDocumento": "1113987400",
    "direccion": "CR 72B 45 12 APT 301"
  }
}
'
```


---

# Notas técnicas

* Se implementó una utilidad `SSLUtil` para deshabilitar validación SSL en entornos de desarrollo.
* La transformación XML se realiza utilizando **Jackson XML**.
* La aplicación está preparada para ejecutarse dentro de **contenedores Docker**.

---

# Autor
Harold Andres Jara Granados
3182673318
andresjara630@gmail.com
Ingeniero de sistemas
Desarrollado como prueba técnica para el proceso de selección de ACME.
