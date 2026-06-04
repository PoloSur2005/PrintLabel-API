# 🖨️ PrintLabel Manager — API REST

API REST para la generación y gestión de etiquetas de calzado en formato CSV, orientada a plantas de producción.

---

## 📦 Stack Técnico

| Tecnología | Versión |
|---|---|
| Java | 17 |
| Spring Boot | 3.2 |
| Spring Security + JWT (jjwt) | 0.11.5 |
| Spring Data JPA + Hibernate | — |
| MySQL | 8.x |
| Lombok | — |
| Maven | 3.9+ |

---

## ⚙️ Requisitos Previos

Antes de ejecutar el proyecto asegúrate de tener instalado:

- **Java 17** — [Descargar](https://adoptium.net/)
- **Maven 3.9+** — [Descargar](https://maven.apache.org/download.cgi)
- **MySQL 8.x** — [Descargar](https://dev.mysql.com/downloads/)

### Verificar instalaciones

```bash
java -version
# java version "17.x.x"

mvn -version
# Apache Maven 3.9.x

mysql --version
# mysql  Ver 8.x.x
```

### Instalar Maven (si no lo tienes)

**macOS (Homebrew):**
```bash
brew install maven
```

**Linux (apt):**
```bash
sudo apt update && sudo apt install maven -y
```

**Windows:**
1. Descarga el binario ZIP desde https://maven.apache.org/download.cgi
2. Extrae en `C:\Program Files\Maven`
3. Agrega `C:\Program Files\Maven\bin` a la variable de entorno `PATH`
4. Verifica con `mvn -version` en una terminal nueva

---

## 🚀 Instalación y Configuración

### 1. Clonar el repositorio

```bash
git clone https://github.com/tu-usuario/printlabel-api.git
cd printlabel-api
```

### 2. Crear la base de datos

```sql
CREATE DATABASE printlabel_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 3. Ejecutar el schema

```bash
mysql -u root -p printlabel_db < src/main/resources/schema.sql
```

### 4. Configurar `application.properties`

Edita `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/printlabel_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=root

spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false

jwt.secret=TuClaveSecretaSuperLargaDeAlMenos256BitsParaHS256
jwt.expiration-ms=86400000
```

> ⚠️ Nunca subas credenciales reales al repositorio. Usa variables de entorno o un archivo `.env` en producción.

### 5. Compilar el proyecto

```bash
mvn clean package -DskipTests
```

### 6. Ejecutar

```bash
java -jar target/printlabel-api-1.0.0.jar
```

La API estará disponible en: `http://localhost:8080/api/v1`

---

## 🔐 Autenticación

Todas las peticiones (excepto `/api/v1/auth/login`) requieren el header:

```
Authorization: Bearer <JWT>
```

**Credenciales iniciales:**

| Campo | Valor |
|---|---|
| Email | `admin@printlabel.com` |
| Password | `Admin2026!` |

**Ejemplo de login:**

```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{ "email": "admin@printlabel.com", "password": "Admin2026!" }'
```

**Respuesta:**

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

---

## 📡 Endpoints

### Base URL: `http://localhost:8080/api/v1`

---

### 🔐 Auth

| Método | Endpoint | Descripción | Auth |
|---|---|---|---|
| POST | `/auth/login` | Inicia sesión, devuelve JWT | No |
| POST | `/auth/logout` | Invalida el token activo | Sí |
| GET | `/auth/me` | Perfil del usuario autenticado | Sí |

---

### 👤 Usuarios — `/usuarios` _(Solo admin)_

| Método | Endpoint | Descripción |
|---|---|---|
| GET | `/usuarios` | Lista todos los usuarios |
| POST | `/usuarios` | Crea un nuevo usuario |
| GET | `/usuarios/{id}` | Consulta usuario por ID |
| PUT | `/usuarios/{id}` | Edita datos del usuario |
| DELETE | `/usuarios/{id}` | Soft delete del usuario |

**Body POST/PUT:**
```json
{
  "nombre": "Juan Pérez",
  "email": "juan@empresa.com",
  "password": "segura123",
  "rol": "operario"
}
```

---

### 🏭 Fábricas — `/fabricas`

| Método | Endpoint | Descripción |
|---|---|---|
| GET | `/fabricas` | Lista fábricas activas |
| POST | `/fabricas` | Registra nueva fábrica |
| GET | `/fabricas/{id}` | Consulta fábrica por ID |
| PUT | `/fabricas/{id}` | Edita fábrica |
| DELETE | `/fabricas/{id}` | Soft delete de fábrica |

**Body POST:**
```json
{ "nombre": "Planta Sur", "ciudad": "Guadalajara", "contacto": "María López" }
```

---

### 👟 Programas — `/programas`

| Método | Endpoint | Descripción |
|---|---|---|
| GET | `/programas` | Lista modelos (soporta `?nombre=`) |
| POST | `/programas` | Crea nuevo modelo |
| GET | `/programas/{id}` | Consulta modelo por ID |
| PUT | `/programas/{id}` | Edita modelo |
| DELETE | `/programas/{id}` | Soft delete de modelo |

**Body POST:**
```json
{ "clave": "ANG-019", "nombre": "Angulo 019 Niño", "descripcion": "Zapato escolar" }
```

---

### 📏 Tallas — `/tallas`

| Método | Endpoint | Descripción |
|---|---|---|
| GET | `/tallas` | Lista tallas con CM, ordenadas |
| POST | `/tallas` | Agrega talla al catálogo |
| GET | `/tallas/{id}` | Consulta talla por ID |
| PUT | `/tallas/{id}` | Edita número o centimetraje |
| DELETE | `/tallas/{id}` | Soft delete de talla |

**Body POST:**
```json
{ "numeroTalla": 6.5, "centimetros": 24.5 }
```

---

### 📋 Órdenes — `/ordenes`

| Método | Endpoint | Descripción |
|---|---|---|
| GET | `/ordenes` | Lista órdenes (`?idFabrica=1&estatus=borrador`) |
| POST | `/ordenes` | Crea nueva orden (folio auto-generado) |
| GET | `/ordenes/{id}` | Orden completa con estilos y tallas |
| PUT | `/ordenes/{id}` | Edita encabezado (solo en borrador) |
| DELETE | `/ordenes/{id}` | Elimina orden en borrador |
| PATCH | `/ordenes/{id}/cerrar` | Cambia estatus a `cerrada` |
| GET | `/ordenes/{id}/csv` | ⬇️ Descarga el CSV para la impresora |

**Body POST:**
```json
{
  "idFabrica": 1,
  "fechaProgramacion": "2026-06-17",
  "observaciones": "Producción semana 25"
}
```

**Ejemplo de CSV generado (`GET /ordenes/1/csv`):**
```
PROGRAMA,TALLA,CM
ANG-017,6.0,24.0
ANG-017,6.0,24.0
ANG-017,7.0,25.0
ANG-017,7.0,25.0
```

---

### 🧩 Estilos de Orden — `/ordenes/{id}/estilos`

| Método | Endpoint | Descripción |
|---|---|---|
| GET | `/ordenes/{id}/estilos` | Lista estilos de la orden |
| POST | `/ordenes/{id}/estilos` | Agrega programa a la orden |
| PUT | `/ordenes/{id}/estilos/{eid}` | Edita posición del estilo en grilla |
| DELETE | `/ordenes/{id}/estilos/{eid}` | Elimina estilo de la orden |
| PUT | `/ordenes/{id}/estilos/{eid}/tallas` | Batch upsert de cantidades por talla |

**Agregar estilo:**
```json
{ "idPrograma": 1, "ordenFila": 1 }
```

**Upsert de tallas (batch):**
```json
{
  "tallas": [
    { "idTalla": 1, "cantidadPares": 6 },
    { "idTalla": 2, "cantidadPares": 12 },
    { "idTalla": 3, "cantidadPares": 0 }
  ]
}
```

> Celdas con `cantidadPares: 0` se eliminan si existían (no se persisten ceros).

---

## 📊 Códigos de Respuesta

| Código | Descripción |
|---|---|
| 200 | OK |
| 201 | Created |
| 204 | No Content (delete exitoso) |
| 400 | Bad Request (validación fallida) |
| 401 | Unauthorized (sin token o inválido) |
| 403 | Forbidden (sin permisos de rol) |
| 404 | Not Found |
| 409 | Conflict (registro duplicado) |
| 500 | Internal Server Error |

---

## 🗂️ Estructura del Proyecto

```
src/main/java/com/printlabel/
├── PrintLabelApplication.java
├── config/
│   ├── SecurityConfig.java
│   └── OpenApiConfig.java
├── controller/
│   ├── AuthController.java
│   ├── UsuarioController.java
│   ├── FabricaController.java
│   ├── ProgramaController.java
│   ├── TallaController.java
│   └── OrdenController.java
├── dto/
│   ├── AuthDto.java
│   ├── UsuarioDto.java
│   ├── CatalogoDto.java
│   └── OrdenDto.java
├── exception/
│   └── GlobalExceptionHandler.java
├── model/
│   ├── Usuario.java
│   ├── Fabrica.java
│   ├── Programa.java
│   ├── Talla.java
│   ├── Orden.java
│   ├── OrdenEstilo.java
│   └── EstiloTalla.java
├── repository/
│   ├── UsuarioRepository.java
│   ├── FabricaRepository.java
│   ├── ProgramaRepository.java
│   ├── TallaRepository.java
│   ├── OrdenRepository.java
│   ├── OrdenEstiloRepository.java
│   └── EstiloTallaRepository.java
├── security/
│   ├── JwtUtil.java
│   ├── JwtAuthFilter.java
│   └── UserDetailsServiceImpl.java
└── service/
    ├── AuthService.java
    ├── UsuarioService.java
    ├── FabricaService.java
    ├── ProgramaService.java
    ├── TallaService.java
    └── OrdenService.java
src/main/resources/
├── application.properties
└── schema.sql
```

---

## 🧪 Flujo de Prueba Rápida

```bash
# 1. Login
TOKEN=$(curl -s -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@printlabel.com","password":"Admin2026!"}' \
  | jq -r '.token')

# 2. Crear fábrica
curl -X POST http://localhost:8080/api/v1/fabricas \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"nombre":"Planta Sur","ciudad":"Guadalajara","contacto":"María López"}'

# 3. Crear orden
curl -X POST http://localhost:8080/api/v1/ordenes \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"idFabrica":1,"fechaProgramacion":"2026-06-17","observaciones":"Prueba"}'

# 4. Descargar CSV
curl -H "Authorization: Bearer $TOKEN" \
  http://localhost:8080/api/v1/ordenes/1/csv -o etiquetas.csv
```