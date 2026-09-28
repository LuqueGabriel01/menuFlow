# Auth

Autenticación de personal (usuario/contraseña) y de clientes (QR de mesa), más registro de nuevos usuarios del personal. Base path: `/api/auth`.

## Entidades relacionadas

### `User`
| Campo | Tipo | Descripción |
|---|---|---|
| `id` | Long | Identificador |
| `username` | String | Único |
| `email` | String | Único |
| `password` | String | Hash BCrypt |
| `enabled` | boolean | Usuario activo/inactivo |
| `roles` | Set\<Role\> | Roles asignados (many-to-many) |

### `Role`
| Campo | Tipo | Descripción |
|---|---|---|
| `id` | Long | Identificador |
| `name` | RoleName | `ROLE_USER`, `ROLE_ADMIN`, `ROLE_CHEF`, `ROLE_CASHIER`, `ROLE_KITCHEN` |

## Endpoints

### `POST /api/auth/login`
Login de personal con usuario y contraseña. Público.

**Request** (`LoginRequest`)
```json
{ "username": "string (requerido)", "password": "string (min 6, requerido)" }
```

**Response** (`AuthResponse`) — 200 OK
```json
{ "token": "jwt", "type": "Bearer", "username": "string", "roles": ["ADMIN"] }
```

### `POST /api/auth/table`
Login de mesa mediante su código QR. Abre o reutiliza la sesión de esa mesa. Público.

**Request** (`TableLoginRequest`)
```json
{ "qrCode": "string (requerido)" }
```

**Response**: `AuthResponse` (token con permisos limitados a la mesa) — 200 OK

### `POST /api/auth/register`
Registro de un nuevo usuario del personal. **Solo ADMIN.**

**Request** (`RegisterRequest`)
```json
{ "username": "string (6-20)", "email": "string (email válido)", "password": "string (min 6)" }
```

**Response** — 201 Created
```json
{ "success": true, "message": "Registration successful under the name <username>", "data": { "message": "..." } }
```

## Seguridad

| Endpoint | Acceso |
|---|---|
| `POST /login` | Público |
| `POST /table` | Público |
| `POST /register` | `ADMIN` |
