# Facturación (Invoices)

Cobro y cierre de un pedido ya servido. Cada `Invoice` está ligada a un único `Order` (relación 1 a 1). Base path: `/api/invoices`.

## Entidad: `Invoice`

| Campo | Tipo | Descripción |
|---|---|---|
| `id` | Long | Identificador |
| `order` | Order | Pedido facturado (único, no se puede facturar dos veces el mismo pedido) |
| `totalAmount` | BigDecimal | Importe total, copiado de `order.totalAmount` en el momento de facturar |
| `paymentMethod` | PaymentMethod | `CASH`, `CARD` o `TRANSFER` |
| `paidAt` | LocalDateTime | Fecha/hora del cobro |
| `createdBy` | User | Usuario (normalmente `CASHIER`) que generó la factura |

## Endpoints

Todos los endpoints requieren rol `ADMIN` o `CASHIER`.

| Método | Path | Descripción |
|---|---|---|
| GET | `/api/invoices` | Lista todas las facturas |
| GET | `/api/invoices/session/{sessionId}` | Facturas de todos los pedidos de una sesión |
| GET | `/api/invoices/order/{orderId}` | Factura de un pedido concreto |
| GET | `/api/invoices/{id}` | Detalle de una factura |
| POST | `/api/invoices` | Factura un pedido servido y lo cierra |

## DTOs

**`CreateInvoiceRequest`**
```json
{ "orderId": "long (requerido)", "paymentMethod": "CASH | CARD | TRANSFER (requerido)" }
```

**`InvoiceResponse`**
```json
{
  "id": 1,
  "orderId": 3,
  "sessionId": 2,
  "tableId": 5,
  "tableNumber": 5,
  "totalAmount": 24.50,
  "paymentMethod": "CARD",
  "paidAt": "2026-01-01T13:15:00",
  "createdBy": "cashier1"
}
```

## Reglas de negocio

- Solo se puede facturar un pedido en estado `SERVED`.
- Un pedido no puede facturarse dos veces (`Invoice.order` es único).
- Al crear la factura, el pedido pasa automáticamente a `CLOSED` (lo hace la propia entidad `Invoice` al fijar el método de pago).
- `createdBy` se toma del usuario autenticado (JWT), no se envía en el request.

## Cómo cerrar la cuenta de una mesa, de principio a fin

1. El cliente pide (`POST /api/orders`) y la cocina avanza el pedido hasta `SERVED` (`PATCH /api/orders/{id}/status`).
2. Caja emite la factura del pedido (`POST /api/invoices`) → el pedido queda `CLOSED`.
3. Repetir el paso 2 por cada pedido abierto de la sesión (si hubo varias rondas).
4. Caja cierra la sesión de la mesa (`POST /api/sessions/close`), que la libera para el siguiente cliente.

## Nota técnica

Igual que en el módulo de Pedidos, `Invoice.addOrder` / `Order.assignInvoice` tenían el mismo bug de recursión infinita sin guarda de idempotencia (esta vez en una relación 1 a 1, no una lista). Se corrigió comparando identidad de referencia (`if (this.order == order) return;`) antes de reenviar la llamada al otro lado de la relación.
