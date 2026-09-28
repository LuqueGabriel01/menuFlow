# Categorías (Categories)

Categorías en las que se agrupan los platos del menú (p. ej. Entrantes, Principales, Postres). Base path: `/api/categories`.

## Entidad: `Category`

| Campo | Tipo | Descripción |
|---|---|---|
| `id` | Long | Identificador |
| `translations` | List\<CategoryTranslation\> | Nombre traducido por idioma (`lang` + `name`) |
| `dishes` | List\<Dish\> | Platos asociados a la categoría |

La API actual solo lee/escribe la traducción en el idioma por defecto (`es`).

## Endpoints

| Método | Path | Descripción | Acceso |
|---|---|---|---|
| GET | `/api/categories` | Lista todas las categorías | Público |
| GET | `/api/categories/{id}` | Detalle de una categoría | Público |
| POST | `/api/categories` | Crea una categoría | `ADMIN`, `CHEF` |
| PUT | `/api/categories/{id}` | Renombra una categoría | `ADMIN`, `CHEF` |
| DELETE | `/api/categories/{id}` | Elimina una categoría | `ADMIN`, `CHEF` |

## DTOs

**`CreateCategoryRequest` / `UpdateCategoryRequest`**
```json
{ "name": "string (3-100, requerido)" }
```

**`CategoryResponse`**
```json
{ "id": 1, "name": "Entrantes", "dishCount": 4 }
```

## Reglas de negocio

- No se pueden crear dos categorías con el mismo nombre (mismo idioma).
- No se puede eliminar una categoría que tenga platos asociados.
