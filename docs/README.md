# menuFlow — Documentación de la API

API REST para la gestión de un restaurante: mesas con acceso por QR, sesiones de mesa, pedidos y menú (categorías, platos, ingredientes y alérgenos), con autenticación JWT y control de acceso por roles.

## Módulos

| Módulo | Descripción | Doc |
|---|---|---|
| Auth | Login de personal, login de mesa por QR, registro de usuarios | [auth.md](auth.md) |
| Mesas | Gestión de mesas del restaurante y su código QR | [tables.md](tables.md) |
| Sesiones | Apertura/cierre de sesión de una mesa y su detalle de consumo | [sessions.md](sessions.md) |
| Categorías | Categorías del menú (entrantes, principales, postres...) | [categories.md](categories.md) |
| Ingredientes | Catálogo de ingredientes usados en los platos | [ingredients.md](ingredients.md) |
| Alérgenos | Catálogo de alérgenos asociados a los platos | [allergens.md](allergens.md) |
| Platos | Platos del menú, con precio, ingredientes y alérgenos | [dishes.md](dishes.md) |
| Pedidos | Pedidos de una sesión de mesa y su flujo de estados en cocina | [orders.md](orders.md) |
| Cocina | Cola de pedidos pendientes de cocinar, ordenada por antigüedad | [kitchen.md](kitchen.md) |
| Facturación | Cobro y cierre de un pedido servido | [invoices.md](invoices.md) |

## Cómo levantar el proyecto

**Con Docker (recomendado):**
```bash
cp .env.example .env   # y rellena tus propios valores
docker compose up --build
```
Esto levanta la base de datos MySQL y la aplicación juntas. La API queda disponible en `http://localhost:8080` (o el puerto que pongas en `SERVER_PORT`).

**En local (IDE / Maven):**
```bash
cp .env.example .env          # y rellena tus propios valores
docker compose up db          # solo la base de datos
./mvnw spring-boot:run
```
`application.properties` importa automáticamente el `.env` de la raíz (`spring.config.import=optional:file:.env[.properties]`), así que nunca hay que tocar el `.properties` para cambiar credenciales: todo lo sensible vive en `.env`, que está en `.gitignore` y nunca se sube al repositorio.

## Convenciones comunes

- **Base URL**: `/api`
- **Autenticación**: Bearer JWT (`Authorization: Bearer <token>`), obtenido en `POST /api/auth/login` o `POST /api/auth/table`.
- **Formato de respuesta**: todas las respuestas usan el envoltorio `ApiResponse`:
  ```json
  {
    "success": true,
    "message": "success",
    "data": { }
  }
  ```
  En caso de error, `success` es `false` y se devuelve un `ErrorResponse` con `status`, `error`, `message`, `path` y, si aplica, `validationErrors`.
- **Idioma**: las entidades del menú (categoría, plato, ingrediente, alérgeno) están preparadas para traducción por idioma (`lang`), aunque de momento la API solo expone el idioma por defecto (`es`).
- **Roles disponibles**: `ADMIN`, `CASHIER`, `CHEF`, `KITCHEN`, `USER` (este último es el que recibe un cliente al escanear el QR de su mesa).
- **Documentación interactiva**: Swagger UI disponible en `/swagger-ui.html` (o `/swagger-ui/index.html`) una vez levantada la aplicación.

## CI

GitHub Actions (`.github/workflows/ci.yml`) en cada push/PR a `main`:

1. **build-and-test**: levanta un contenedor MySQL de servicio, compila con Java 21 y ejecuta `./mvnw test` (incluye el `contextLoads` de `@SpringBootTest`, que arranca el contexto completo contra esa base de datos).
2. **docker-build**: construye la imagen con el `Dockerfile` del repo para asegurar que el build de producción no se rompe.

## Módulos pendientes de implementar

- **Frontend**: no hay interfaz de usuario, solo la API (documentada aquí + Swagger).
