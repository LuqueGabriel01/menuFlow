# MenuFlow

API REST para la gestión integral de un restaurante: mesas con acceso por código QR, sesiones de consumo, menú multi-idioma (categorías, platos, ingredientes y alérgenos), pedidos con flujo de estados en cocina y facturación — con autenticación JWT y control de acceso por roles.

Proyecto personal construido con Spring Boot para practicar y mostrar un diseño de API REST completo de principio a fin: modelado de dominio, seguridad, testing y despliegue con Docker.

## Stack técnico

- **Java 21** + **Spring Boot**
- **Spring Data JPA** / Hibernate + **MySQL 8**
- **Spring Security** con JWT (autenticación stateless, control de acceso por roles)
- **springdoc-openapi** (Swagger UI)
- **JUnit 5 + Mockito** para tests de servicios y controladores
- **Docker** / **Docker Compose** para despliegue
- **ZXing** para generación de códigos QR de mesa

## Funcionalidades

- **Autenticación dual**: login de personal (usuario/contraseña → JWT con rol real) y login de cliente por QR de mesa (→ JWT con `ROLE_USER`), sin necesidad de cuenta.
- **Gestión de mesas y sesiones**: cada mesa tiene un QR único; abrir una sesión habilita a esa mesa a pedir hasta que se cierra.
- **Menú multi-idioma**: categorías, platos, ingredientes y alérgenos con traducciones por idioma (`lang`), preparado para exponer varios idiomas en el futuro.
- **Pedidos**: alta de pedidos y líneas por sesión, con flujo de estados (`PENDING → IN_PROGRESS → READY → SERVED → CLOSED`, con cancelación).
- **Cocina**: cola de pedidos pendientes de cocinar (`/api/kitchen/queue`), ordenada por antigüedad, separada de la vista general de pedidos activos.
- **Facturación**: cierre y cobro de un pedido servido (`CASH`, `CARD`, `TRANSFER`), con relación 1 a 1 pedido-factura.
- **Roles**: `ADMIN`, `CASHIER`, `CHEF`, `KITCHEN`, `USER`, cada endpoint protegido según corresponda.

## Documentación

- **[docs/](docs/README.md)** — un README por módulo con sus entidades, reglas de negocio y endpoints (auth, mesas, sesiones, menú, pedidos, facturación).
- **[database/README.md](database/README.md)** — diseño de la base de datos y diagrama entidad-relación.
- **Swagger UI** — `/swagger-ui.html` una vez levantada la aplicación.

### Modelo de datos

![Diagrama entidad-relación](database/assets/restaurant_erd.png)

## Cómo levantar el proyecto

**Con Docker (recomendado):**
```bash
cp .env.example .env   # y rellena tus propios valores
docker compose up --build
```
Levanta MySQL y la aplicación juntas. La API queda disponible en `http://localhost:8080` (o el puerto configurado en `SERVER_PORT`).

**En local (IDE / Maven):**
```bash
cp .env.example .env          # y rellena tus propios valores
docker compose up db          # solo la base de datos
./mvnw spring-boot:run
```

`application.properties` no contiene ningún dato sensible: solo referencias `${VAR}` que se resuelven contra el `.env` de la raíz (`spring.config.import=optional:file:.env[.properties]`). El `.env` nunca se sube al repositorio.

## Tests

```bash
./mvnw test
```
Cobertura con JUnit 5 + Mockito a nivel de servicio (reglas de negocio) y controlador (`@WebMvcTest` + `MockMvc`).

## Convenciones de la API

- **Base URL**: `/api`
- **Autenticación**: Bearer JWT (`Authorization: Bearer <token>`), obtenido en `POST /api/auth/login` o `POST /api/auth/table`.
- **Formato de respuesta**: todas las respuestas usan el envoltorio `ApiResponse` (`success`, `message`, `data`); los errores devuelven un `ErrorResponse` con `status`, `error`, `message`, `path` y, si aplica, `validationErrors`.

Detalle completo endpoint por endpoint en **[docs/](docs/README.md)**.

## CI

GitHub Actions compila y ejecuta los tests (con un MySQL de servicio) en cada push/PR a `main`, y valida que la imagen Docker construye correctamente. Ver [`.github/workflows/ci.yml`](.github/workflows/ci.yml).

## Roadmap

- [ ] Frontend de demostración
