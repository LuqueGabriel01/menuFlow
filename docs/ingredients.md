# Ingredientes (Ingredients)

Catálogo de ingredientes reutilizable, asociado a los platos mediante una relación muchos-a-muchos. Base path: `/api/ingredients`.

## Entidad: `Ingredient`

| Campo | Tipo | Descripción |
|---|---|---|
| `id` | Long | Identificador |
| `translations` | List\<IngredientTranslation\> | Nombre traducido por idioma |
| `dishes` | List\<Dish\> | Platos que usan este ingrediente |

## Endpoints

| Método | Path | Descripción | Acceso |
|---|---|---|---|
| GET | `/api/ingredients` | Lista todos los ingredientes | Público |
| GET | `/api/ingredients/{id}` | Detalle de un ingrediente | Público |
| POST | `/api/ingredients` | Crea un ingrediente | `ADMIN`, `CHEF` |
| PUT | `/api/ingredients/{id}` | Renombra un ingrediente | `ADMIN`, `CHEF` |
| DELETE | `/api/ingredients/{id}` | Elimina un ingrediente | `ADMIN`, `CHEF` |

## DTOs

**`CreateIngredientRequest` / `UpdateIngredientRequest`**
```json
{ "name": "string (3-100, requerido)" }
```

**`IngredientResponse`**
```json
{ "id": 1, "name": "Tomate" }
```

## Reglas de negocio

- No se pueden crear dos ingredientes con el mismo nombre (mismo idioma).
- Al eliminar un ingrediente se desvincula automáticamente de todos los platos que lo usaban (no se bloquea el borrado).
