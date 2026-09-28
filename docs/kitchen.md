# Cocina (Kitchen)

Vista de solo lectura para la pantalla de cocina. No introduce entidades nuevas: reutiliza `Order`/`OrderStatus` (ver [orders.md](orders.md)). Base path: `/api/kitchen`.

## Por qué existe, además de `GET /api/orders/active`

`GET /api/orders/active` devuelve todo lo "en curso" (`PENDING`, `IN_PROGRESS` y `READY`) sin orden garantizado — pensado para una vista general de admin/caja. Cocina necesita otra cosa: solo lo que todavía tiene que cocinar, en orden de llegada (FIFO), para saber qué preparar primero. Una vez un pedido pasa a `READY`, ya no es responsabilidad de cocina (queda a la espera de que sala lo sirva), así que se excluye de esta cola.

## Endpoints

| Método | Path | Descripción | Acceso |
|---|---|---|---|
| GET | `/api/kitchen/queue` | Pedidos `PENDING`/`IN_PROGRESS`, ordenados por `createdAt` ascendente (el más antiguo primero) | `ADMIN`, `KITCHEN` |

El avance de estado (`PENDING → IN_PROGRESS → READY`) se hace con `PATCH /api/orders/{id}/status` (ver [orders.md](orders.md)); no se duplica aquí.

## Respuesta

Misma forma que el resto de endpoints de pedidos: `ApiResponse<List<OrderResponse>>` (ver [orders.md](orders.md#dtos)).
