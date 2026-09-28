# Platos (Dishes)

Los platos del menú: pertenecen a una categoría, tienen precio, disponibilidad, ingredientes y alérgenos. Base path: `/api/dishes`.

## Entidad: `Dish`

| Campo | Tipo | Descripción |
|---|---|---|
| `id` | Long | Identificador (hereda de `BaseEntity`: incluye `createdAt`/`updatedAt`) |
| `category` | Category | Categoría a la que pertenece |
| `translations` | List\<DishTranslation\> | Nombre + descripción traducidos por idioma |
| `price` | BigDecimal | Precio, debe ser > 0 |
| `available` | boolean | Si el plato se puede pedir actualmente |
| `imagePath` | String | Ruta de la imagen del plato (aún sin subida de ficheros) |
| `createdBy` | User | Usuario (normalmente `CHEF`) que creó el plato |
| `ingredients` | List\<Ingredient\> | Ingredientes del plato (many-to-many) |
| `allergens` | List\<Allergen\> | Alérgenos del plato (many-to-many) |
| `orderItems` | List\<OrderItem\> | Líneas de pedido que referencian este plato |

## Endpoints

| Método | Path | Descripción | Acceso |
|---|---|---|---|
| GET | `/api/dishes` | Lista todos los platos | Público |
| GET | `/api/dishes/available` | Lista solo los platos disponibles | Público |
| GET | `/api/dishes/category/{categoryId}` | Lista los platos de una categoría | Público |
| GET | `/api/dishes/{id}` | Detalle de un plato | Público |
| POST | `/api/dishes` | Crea un plato | `ADMIN`, `CHEF` |
| PUT | `/api/dishes/{id}` | Actualiza un plato | `ADMIN`, `CHEF` |
| DELETE | `/api/dishes/{id}` | Elimina un plato | `ADMIN`, `CHEF` |

## DTOs

**`CreateDishRequest`**
```json
{
  "categoryId": "long (requerido)",
  "name": "string (3-150, requerido)",
  "description": "string (10-500, requerido)",
  "price": "decimal (>=0.01, requerido)",
  "available": "bool (opcional, default true)",
  "ingredientsIds": [1, 2],
  "allergensIds": [3]
}
```

**`UpdateDishRequest`**: igual que `CreateDishRequest`, pero `available` es obligatorio.

**`DishResponse`**
```json
{
  "id": 1,
  "categoryId": 2,
  "categoryName": "Principales",
  "name": "Pizza margarita",
  "description": "Tomate, mozzarella y albahaca",
  "price": 9.50,
  "available": true,
  "imagePath": null,
  "imageUrl": null,
  "createdAt": "2026-01-01T12:00:00",
  "createdBy": "chef1",
  "ingredients": [ { "id": 1, "name": "Tomate" } ],
  "allergens": [ { "id": 2, "name": "Lactosa" } ]
}
```

## Reglas de negocio

- La categoría del plato debe existir; si no, `404`.
- El precio debe ser mayor que 0 (validado en el DTO y en la entidad).
- `createdBy` se toma automáticamente del usuario autenticado (JWT), no se envía en el request.
- Al actualizar, la lista de `ingredientsIds`/`allergensIds` sustituye por completo a la anterior (no es un "añadir", es un "reemplazar").
- Al eliminar un plato, se desvinculan automáticamente sus ingredientes y alérgenos.

## Pendiente

- Subida real de imágenes (`imagePath`/`imageUrl` están en el DTO pero no hay endpoint de subida de ficheros todavía).
