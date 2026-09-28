# Alérgenos (Allergens)

Catálogo de alérgenos reutilizable, asociado a los platos mediante una relación muchos-a-muchos. Base path: `/api/allergens`.

## Entidad: `Allergen`

| Campo | Tipo | Descripción |
|---|---|---|
| `id` | Long | Identificador |
| `translations` | List\<AllergenTranslation\> | Nombre traducido por idioma |
| `dishes` | List\<Dish\> | Platos que contienen este alérgeno |

## Endpoints

| Método | Path | Descripción | Acceso |
|---|---|---|---|
| GET | `/api/allergens` | Lista todos los alérgenos | Público |
| GET | `/api/allergens/{id}` | Detalle de un alérgeno | Público |
| POST | `/api/allergens` | Crea un alérgeno | `ADMIN`, `CHEF` |
| PUT | `/api/allergens/{id}` | Renombra un alérgeno | `ADMIN`, `CHEF` |
| DELETE | `/api/allergens/{id}` | Elimina un alérgeno | `ADMIN`, `CHEF` |

## DTOs

**`CreateAllergenRequest` / `UpdateAllergenRequest`**
```json
{ "name": "string (3-100, requerido)" }
```

**`AllergenResponse`**
```json
{ "id": 1, "name": "Gluten" }
```

## Reglas de negocio

- No se pueden crear dos alérgenos con el mismo nombre (mismo idioma).
- Al eliminar un alérgeno se desvincula automáticamente de todos los platos que lo tenían (no se bloquea el borrado).
