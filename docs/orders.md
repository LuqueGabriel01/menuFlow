# Pedidos (Orders)

Un pedido agrupa uno o varios platos pedidos dentro de una sesión de mesa activa, y avanza por un flujo de estados hasta servirse. Base path: `/api/orders`.

## Entidades

### `Order`

| Campo | Tipo | Descripción |
|---|---|---|
| `id` | Long | Identificador (hereda `createdAt`/`updatedAt` de `BaseEntity`) |
| `tableSession` | TableSession | Sesión de mesa a la que pertenece el pedido |
| `status` | OrderStatus | Ver flujo de estados más abajo |
| `totalAmount` | BigDecimal | Suma de los importes de sus items |
| `closedAt` | LocalDateTime | Fecha de cierre/cancelación |
| `orderItems` | List\<OrderItem\> | Líneas del pedido |
| `invoice` | Invoice | Factura asociada (cuando exista el módulo de facturación) |

### `OrderItem`

| Campo | Tipo | Descripción |
|---|---|---|
| `id` | Long | Identificador |
| `dish` | Dish | Plato pedido |
| `quantity` | Integer | Cantidad |
| `price` | BigDecimal | `dish.price * quantity`, calculado al crear el item |
| `status` | OrderStatus | Estado individual del item |
| `order` | Order | Pedido al que pertenece |

### Flujo de estados (`OrderStatus`)

```
PENDING → IN_PROGRESS → READY → SERVED → CLOSED
                                   ↘
                                 CANCELLED
```

`COMPLETED` existe en el enum pero no forma parte del flujo estándar de cocina (queda disponible para casos especiales vía `PATCH /status`).

## Endpoints

| Método | Path | Descripción | Acceso |
|---|---|---|---|
| POST | `/api/orders` | Crea un pedido con uno o varios platos, en una sesión abierta | Autenticado (cliente o personal) |
| GET | `/api/orders/active` | Vista general de pedidos en curso: `PENDING`/`IN_PROGRESS`/`READY` (sin orden garantizado). Para la cola de cocina, ver [kitchen.md](kitchen.md) | `ADMIN`, `CHEF`, `KITCHEN` |
| GET | `/api/orders/session/{sessionId}` | Pedidos de una sesión | Autenticado |
| GET | `/api/orders/{id}` | Detalle de un pedido (con items y plato) | Autenticado |
| POST | `/api/orders/{id}/items` | Añade un plato al pedido | Autenticado |
| DELETE | `/api/orders/{id}/items/{itemId}` | Quita un item del pedido | `ADMIN`, `CASHIER`, `CHEF`, `KITCHEN` |
| PATCH | `/api/orders/{id}/status` | Avanza el estado del pedido | `ADMIN`, `CHEF`, `KITCHEN` |
| DELETE | `/api/orders/{id}/cancel` | Cancela el pedido | `ADMIN`, `CASHIER`, `CHEF`, `KITCHEN` |

> Los endpoints de lectura/creación quedan abiertos a cualquier usuario autenticado porque un cliente que ha escaneado el QR de su mesa (login de mesa, rol `USER`) necesita poder pedir y ver el estado de su propio pedido.

## DTOs

**`CreateOrderRequest`**
```json
{
  "sessionId": "long (requerido)",
  "items": [ { "dishId": 1, "quantity": 2 } ]
}
```

**`OrderItemRequest`** (también se usa para `POST /{id}/items`)
```json
{ "dishId": "long (requerido)", "quantity": "int (>=1, requerido)" }
```

**`UpdateOrderStatusRequest`**
```json
{ "status": "IN_PROGRESS | READY | SERVED | COMPLETED | CLOSED | CANCELLED" }
```

**`OrderResponse`**
```json
{
  "id": 1,
  "sessionId": 3,
  "tableId": 5,
  "tableNumber": 5,
  "status": "PENDING",
  "totalAmount": 24.50,
  "createdAt": "2026-01-01T12:00:00",
  "closedAt": null,
  "items": [
    { "id": 1, "dishId": 2, "dishName": "Pizza margarita", "quantity": 2, "price": 19.00, "status": "PENDING" }
  ]
}
```

## Reglas de negocio

- Solo se puede crear un pedido si la sesión de la mesa está `OPEN`.
- Solo se pueden pedir platos con `available = true`.
- Un pedido deja de ser modificable (añadir/quitar items) en cuanto está `SERVED`, `CLOSED`, `CANCELLED` o `COMPLETED`.
- No se puede cambiar el estado de un pedido ya `CLOSED` o `CANCELLED`.
- El total (`totalAmount`) se recalcula automáticamente cada vez que se añade o quita un item.

## Nota técnica

Al implementar este módulo se corrigió un bug existente en `Order.addOrderItem` / `Dish.addOrderItem`: la relación bidireccional con `OrderItem` no tenía guarda de idempotencia, lo que provocaba una recursión infinita (`StackOverflowError`) en cuanto se añadía un item a un pedido o a un plato. Se aplicó el mismo patrón de guarda (`if (!contains(...))`) que ya usaban `Category.addDish` y `Allergen.addDish`.
