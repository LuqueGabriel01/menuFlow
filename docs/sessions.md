# Sesiones de mesa (Table Sessions)

Una sesión representa el intervalo de tiempo en que una mesa está "ocupada": se abre cuando el cliente escanea el QR (o el personal la abre manualmente) y se cierra al terminar el servicio. Base path: `/api/sessions`.

## Entidad: `TableSession`

| Campo | Tipo | Descripción |
|---|---|---|
| `id` | Long | Identificador |
| `diningTable` | DiningTable | Mesa asociada |
| `status` | SessionStatus | `OPEN` o `CLOSED` |
| `openedBy` | User | Usuario que abrió la sesión (puede ser null) |
| `openedAt` | LocalDateTime | Fecha/hora de apertura |
| `closedAt` | LocalDateTime | Fecha/hora de cierre |
| `orders` | List\<Order\> | Pedidos realizados durante la sesión |

## Endpoints

| Método | Path | Descripción | Acceso |
|---|---|---|---|
| POST | `/api/sessions/open?tableId=&openedBy=` | Abre una sesión para una mesa | `ADMIN`, `CASHIER` |
| POST | `/api/sessions/close` | Cierra una sesión | `ADMIN`, `CASHIER` |
| GET | `/api/sessions/active` | Lista todas las sesiones abiertas | `ADMIN`, `CASHIER`, `KITCHEN` |
| GET | `/api/sessions/{id}` | Detalle básico de una sesión | `ADMIN`, `CASHIER` |
| GET | `/api/sessions/{id}/details` | Sesión + pedidos + total consumido | `ADMIN`, `CASHIER` |
| GET | `/api/sessions/table/{tableId}` | Historial de sesiones de una mesa | `ADMIN` |
| GET | `/api/sessions/table/{tableId}/active` | Sesión abierta actual de una mesa | `ADMIN`, `CASHIER` |

## DTOs

**`CloseSessionRequest`**
```json
{ "sessionId": "long (requerido)", "closedBy": "long (opcional)" }
```

**`SessionResponse`**
```json
{
  "id": 1,
  "tableId": 5,
  "tableNumber": 5,
  "openedAt": "2026-01-01T12:00:00",
  "closedAt": null,
  "status": "OPEN",
  "openedBy": "cashier1",
  "duration": "45min"
}
```

**`SessionDetailResponse`**
```json
{
  "session": { "...": "SessionResponse" },
  "orders": [ { "id": 1, "status": "SERVED", "totalAmount": 24.50, "itemCount": 3, "createdAt": "..." } ],
  "totalAmount": 24.50,
  "orderCount": 1
}
```

## Reglas de negocio

- No se puede abrir una sesión en una mesa inactiva.
- Una mesa no puede tener dos sesiones `OPEN` simultáneamente.
- `duration` se calcula sobre `closedAt` si existe, o sobre el instante actual si la sesión sigue abierta.
