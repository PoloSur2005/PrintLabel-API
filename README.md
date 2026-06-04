# PrintLabel Manager API REST

API REST para generar y administrar etiquetas de calzado en formato CSV. El proyecto está preparado para ejecutarse con Docker Compose y mantener una configuración consistente entre desarrollo, pruebas y producción.

## Stack técnico

| Tecnología | Versión |
|---|---|
| Java | 17 |
| Spring Boot | 3.2.5 |
| Spring Security + JWT | jjwt 0.11.5 |
| Spring Data JPA + Hibernate | Incluido por Spring Boot |
| MySQL | 8.4 en Docker Compose |
| Maven | 3.9+ |
| OpenAPI / Swagger UI | springdoc 2.5.0 |

## Inicio rápido con Docker Compose

La forma recomendada de ejecutar el proyecto es Docker Compose. Este flujo levanta la API y MySQL con la misma configuración de red y variables que usará el despliegue.

### Requisitos

- Docker Engine con Docker Compose v2.
- Puerto `8080` libre para la API.
- Puerto `3306` libre si deseas acceder a MySQL desde el host.

### Configuración de variables

Copia el archivo de ejemplo y ajusta los secretos antes de desplegar:

```bash
cp .env.example .env
```

Variables principales:

| Variable | Uso | Valor local por defecto |
|---|---|---|
| `MYSQL_DATABASE` | Base de datos creada por MySQL | `printlabel_db` |
| `MYSQL_USER` | Usuario de aplicación | `printlabel` |
| `MYSQL_PASSWORD` | Contraseña del usuario de aplicación | `printlabel` |
| `MYSQL_ROOT_PASSWORD` | Contraseña del usuario root de MySQL | `root` |
| `API_PORT` | Puerto expuesto por la API | `8080` |
| `JWT_SECRET` | Llave de firma HS256 para tokens JWT | Solo para desarrollo local |
| `JWT_EXPIRATION_MS` | Duración del token en milisegundos | `86400000` |
| `SPRING_JPA_HIBERNATE_DDL_AUTO` | Validación del esquema JPA | `validate` |

En producción cambia siempre `MYSQL_ROOT_PASSWORD`, `MYSQL_PASSWORD` y `JWT_SECRET`. El `JWT_SECRET` debe tener al menos 32 bytes para HS256.

### Levantar servicios

```bash
docker compose up --build -d
```

Verifica el estado:

```bash
docker compose ps
docker compose logs -f api
```

La API queda disponible en:

```text
http://localhost:8080/api/v1
```

Swagger UI queda disponible en:

```text
http://localhost:8080/swagger-ui/index.html
```

### Reiniciar con base de datos limpia

MySQL usa el volumen `mysql_data`. Si necesitas volver a cargar `schema.sql` desde cero:

```bash
docker compose down -v
docker compose up --build -d
```

## Ejecución local sin Docker

Usa esta opción solo si necesitas ejecutar la API directamente con Maven.

### Requisitos

- Java 17.
- Maven 3.9+.
- MySQL 8.x en ejecución.

### Crear base de datos, usuario y cargar esquema

```bash
mysql -u root -p < src/main/resources/schema.sql
mysql -u root -p -e "CREATE USER IF NOT EXISTS 'printlabel'@'%' IDENTIFIED BY 'printlabel'; GRANT ALL PRIVILEGES ON printlabel_db.* TO 'printlabel'@'%'; FLUSH PRIVILEGES;"
```

Si prefieres usar otro usuario, ajusta `SPRING_DATASOURCE_USERNAME` y `SPRING_DATASOURCE_PASSWORD` con las mismas credenciales.

### Exportar variables de entorno

```bash
export SPRING_DATASOURCE_URL='jdbc:mysql://localhost:3306/printlabel_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true'
export SPRING_DATASOURCE_USERNAME='printlabel'
export SPRING_DATASOURCE_PASSWORD='printlabel'
export JWT_SECRET='change-this-secret-to-at-least-32-characters-for-hs256'
export JWT_EXPIRATION_MS='86400000'
```

### Compilar y ejecutar

```bash
mvn clean package -DskipTests
java -jar target/printlabel-api-1.0.0.jar
```

## Autenticación

Todas las peticiones protegidas requieren el encabezado HTTP:

```text
Authorization: Bearer <JWT>
```

El esquema inicial crea un usuario administrador para desarrollo:

| Campo | Valor |
|---|---|
| Email | `admin@printlabel.com` |
| Password | `Admin2026!` |
| Rol | `admin` |

Ejemplo de login:

```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@printlabel.com","password":"Admin2026!"}'
```

Respuesta esperada:

```json
{
  "token": "eyJ...",
  "tipo": "Bearer",
  "idUsuario": 1,
  "nombre": "Administrador",
  "email": "admin@printlabel.com",
  "rol": "admin"
}
```

Ejemplo usando el token:

```bash
TOKEN=$(curl -s -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@printlabel.com","password":"Admin2026!"}' \
  | jq -r '.token')

curl -H "Authorization: Bearer $TOKEN" http://localhost:8080/api/v1/auth/me
```

Notas importantes sobre tokens:

- El prefijo recomendado es `Bearer` seguido de un espacio y el token.
- Si el token falta, es inválido o expiró, la API responde `401 Unauthorized`.
- El cierre de sesión invalida el token activo en la instancia actual de la API. Si se usan varias réplicas, reemplaza la lista en memoria por Redis u otro almacén compartido.

## Endpoints

Base URL:

```text
http://localhost:8080/api/v1
```

### Auth

| Método | Endpoint | Descripción | Autenticación |
|---|---|---|---|
| POST | `/auth/login` | Inicia sesión y devuelve JWT | No |
| POST | `/auth/logout` | Invalida el token activo | Sí |
| GET | `/auth/me` | Devuelve el perfil autenticado | Sí |

### Usuarios (`/usuarios`, solo admin)

| Método | Endpoint | Descripción |
|---|---|---|
| GET | `/usuarios` | Lista todos los usuarios |
| POST | `/usuarios` | Crea un usuario |
| GET | `/usuarios/{id}` | Consulta usuario por ID |
| PUT | `/usuarios/{id}` | Actualiza usuario |
| DELETE | `/usuarios/{id}` | Desactiva usuario |

Body para crear usuario:

```json
{
  "nombre": "Juan Pérez",
  "email": "juan@empresa.com",
  "password": "segura123",
  "rol": "operario"
}
```

### Fábricas (`/fabricas`)

| Método | Endpoint | Descripción |
|---|---|---|
| GET | `/fabricas` | Lista fábricas activas |
| POST | `/fabricas` | Registra una fábrica |
| GET | `/fabricas/{id}` | Consulta fábrica por ID |
| PUT | `/fabricas/{id}` | Actualiza fábrica |
| DELETE | `/fabricas/{id}` | Desactiva fábrica |

Body de ejemplo:

```json
{
  "nombre": "Planta Sur",
  "ciudad": "Guadalajara",
  "contacto": "María López"
}
```

### Programas (`/programas`)

| Método | Endpoint | Descripción |
|---|---|---|
| GET | `/programas` | Lista programas. Soporta `?nombre=` |
| POST | `/programas` | Crea un programa |
| GET | `/programas/{id}` | Consulta programa por ID |
| PUT | `/programas/{id}` | Actualiza programa |
| DELETE | `/programas/{id}` | Desactiva programa |

Body de ejemplo:

```json
{
  "clave": "ANG-019",
  "nombre": "Angulo 019 Niño",
  "descripcion": "Zapato escolar"
}
```

### Tallas (`/tallas`)

| Método | Endpoint | Descripción |
|---|---|---|
| GET | `/tallas` | Lista tallas activas ordenadas |
| POST | `/tallas` | Agrega una talla |
| GET | `/tallas/{id}` | Consulta talla por ID |
| PUT | `/tallas/{id}` | Actualiza talla |
| DELETE | `/tallas/{id}` | Desactiva talla |

Body de ejemplo:

```json
{
  "numeroTalla": 6.5,
  "centimetros": 24.5
}
```

### Órdenes (`/ordenes`)

| Método | Endpoint | Descripción |
|---|---|---|
| GET | `/ordenes` | Lista órdenes. Soporta `?idFabrica=1&estatus=borrador` |
| POST | `/ordenes` | Crea una orden con folio automático |
| GET | `/ordenes/{id}` | Consulta orden completa con estilos y tallas |
| PUT | `/ordenes/{id}` | Actualiza encabezado si está en borrador |
| DELETE | `/ordenes/{id}` | Elimina orden si está en borrador |
| PATCH | `/ordenes/{id}/cerrar` | Cambia estatus a `cerrada` |
| GET | `/ordenes/{id}/csv` | Descarga CSV para impresora |

Body de ejemplo:

```json
{
  "idFabrica": 1,
  "fechaProgramacion": "2026-06-17",
  "observaciones": "Producción semana 25"
}
```

CSV generado:

```csv
PROGRAMA,TALLA,CM
ANG-017,6.0,24.0
ANG-017,6.0,24.0
ANG-017,7.0,25.0
ANG-017,7.0,25.0
```

### Estilos de orden (`/ordenes/{id}/estilos`)

| Método | Endpoint | Descripción |
|---|---|---|
| GET | `/ordenes/{id}/estilos` | Lista estilos de la orden |
| POST | `/ordenes/{id}/estilos` | Agrega programa a la orden |
| PUT | `/ordenes/{id}/estilos/{eid}` | Actualiza posición del estilo |
| DELETE | `/ordenes/{id}/estilos/{eid}` | Elimina estilo |
| PUT | `/ordenes/{id}/estilos/{eid}/tallas` | Actualiza cantidades por talla en lote |

Agregar estilo:

```json
{
  "idPrograma": 1,
  "ordenFila": 1
}
```

Actualizar tallas:

```json
{
  "tallas": [
    { "idTalla": 1, "cantidadPares": 6 },
    { "idTalla": 2, "cantidadPares": 12 },
    { "idTalla": 3, "cantidadPares": 0 }
  ]
}
```

Las tallas con `cantidadPares: 0` se eliminan si ya existían; no se persisten filas con cero pares.

## Códigos de respuesta

| Código | Descripción |
|---|---|
| 200 | Operación exitosa |
| 201 | Recurso creado |
| 204 | Operación exitosa sin contenido |
| 400 | Error de validación o solicitud incorrecta |
| 401 | Token ausente, inválido o expirado |
| 403 | Usuario autenticado sin permisos suficientes |
| 404 | Recurso no encontrado |
| 409 | Conflicto por registro duplicado u otra regla de negocio |
| 500 | Error interno del servidor |

## Estructura del proyecto

```text
src/main/java/com/printlabel/
├── PrintLabelApplication.java
├── config/
│   ├── OpenApiConfig.java
│   └── SecurityConfig.java
├── controller/
├── dto/
├── exception/
├── model/
├── repository/
├── security/
└── service/
src/main/resources/
├── application.properties
└── schema.sql
```

## Flujo de prueba rápida

```bash
TOKEN=$(curl -s -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@printlabel.com","password":"Admin2026!"}' \
  | jq -r '.token')

curl -X POST http://localhost:8080/api/v1/fabricas \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"nombre":"Planta Sur","ciudad":"Guadalajara","contacto":"María López"}'

curl -X POST http://localhost:8080/api/v1/ordenes \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"idFabrica":1,"fechaProgramacion":"2026-06-17","observaciones":"Prueba"}'

curl -H "Authorization: Bearer $TOKEN" \
  http://localhost:8080/api/v1/ordenes/1/csv \
  -o etiquetas.csv
```

## Recomendaciones para producción

- Usa un `.env` administrado por tu plataforma de despliegue o un gestor de secretos.
- No uses los valores locales por defecto en producción.
- Mantén `spring.jpa.hibernate.ddl-auto=validate` para evitar cambios automáticos en la base de datos.
- Usa volúmenes persistentes y respaldos programados para MySQL.
- Si escalas la API a más de una réplica, mueve la lista de tokens invalidados a un almacén compartido como Redis.
