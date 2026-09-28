# Mesas (Dining Tables)

Gestión de las mesas físicas del restaurante y su código QR de acceso. Base path: `/api/tables`.

## Entidad: `DiningTable`

| Campo | Tipo | Descripción |
|---|---|---|
| `id` | Long | Identificador |
| `number` | Integer | Número de mesa, único |
| `isActive` | boolean | Si la mesa está operativa |
| `qrCode` | String | Código único (UUID) usado en el login de mesa |
| `sessions` | List\<TableSession\> | Historial de sesiones de esa mesa |

## Endpoints

| Método | Path | Descripción | Acceso |
|---|---|---|---|
| GET | `/api/tables` | Lista todas las mesas | `ADMIN`, `CASHIER` |
| GET | `/api/tables/active` | Lista solo las mesas activas | `ADMIN`, `CASHIER` |
| GET | `/api/tables/{id}` | Detalle de una mesa | `ADMIN`, `CASHIER` |
| GET | `/api/tables/qr/{qrCode}` | Busca una mesa por su QR | `ADMIN`, `CASHIER` |
| GET | `/api/tables/{id}/qr` | Descarga la imagen PNG del QR de la mesa | `ADMIN` |
| POST | `/api/tables` | Crea una mesa | `ADMIN`, `CASHIER` |
| PUT | `/api/tables/{id}` | Actualiza número/estado de una mesa | `ADMIN`, `CASHIER` |
| PATCH | `/api/tables/{id}/toggle` | Alterna activa/inactiva | `ADMIN`, `CASHIER` |
| DELETE | `/api/tables/{id}` | Elimina una mesa (falla si tiene sesión activa) | `ADMIN`, `CASHIER` |

## DTOs

**`CreateTableRequest`**
```json
{ "number": "int (>=1, requerido)", "active": "bool (opcional, default true)" }
```

**`UpdateTableRequest`**
```json
{ "number": "int (requerido)", "active": "bool (requerido)" }
```

**`TableResponse`**
```json
{
  "id": 1,
  "number": 5,
  "active": true,
  "qrCode": "uuid",
  "createdAt": "2026-01-01T12:00:00",
  "hasActiveSession": true
}
```

## Reglas de negocio

- No se puede crear una mesa con un `number` ya existente.
- No se puede eliminar una mesa que tenga una sesión con estado `OPEN`.
- El código QR se genera automáticamente (UUID) al crear la mesa y es único.
