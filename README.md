# ms-auth

Microservicio de **autenticación**: login, usuarios, roles y emisión de tokens **JWT**. Forma parte de la solución de microservicios (MS-AUTH, MS-PRODUCTOS, MS-VENTAS, MS-INVENTARIO y API Gateway).

Las contraseñas se guardan encriptadas con **BCrypt**. Al iniciar por primera vez se crean los roles `ADMIN` y `VENDEDOR` y el usuario:

| Usuario | Contraseña | Rol |
|---|---|---|
| `admin` | `admin123` | ADMIN |

## Tecnologías

- Java 21
- Spring Boot 3.5 (Web, Data JPA, Validation)
- Spring Security Crypto (BCrypt)
- JJWT 0.12
- MySQL 8
- Maven
- JUnit 5 + Mockito

## Estructura

```
src/main/java/cl/duoc/msauth
├── controller   -> Endpoints REST
├── service      -> Lógica de negocio (login, JWT, usuarios)
├── repository   -> Acceso a datos (Spring Data JPA)
├── model        -> Entidades JPA (Rol 1 ── * Usuario)
├── config       -> BCrypt y datos iniciales
├── dto          -> Objetos de entrada y salida
└── exception    -> Manejo global de errores
```

## Requisitos

- Java 21
- Maven 3.9+
- MySQL 8 corriendo en `localhost:3306`

## Cómo ejecutar

1. Clonar el repositorio:
   ```bash
   git clone https://github.com/maca2003/ms-auth.git
   cd ms-auth
   ```
2. Revisar el usuario y contraseña de MySQL en `src/main/resources/application.properties` (por defecto `root` / `root`). La base de datos `auth_db` se crea automáticamente.
3. Compilar, ejecutar pruebas y empaquetar:
   ```bash
   mvn clean install
   ```
4. Ejecutar:
   ```bash
   java -jar target/ms-auth-1.0.0.jar
   ```
   o bien `mvn spring-boot:run`.

El servicio queda disponible en `http://localhost:8081`.

## Endpoints

| Método | Ruta | Descripción |
|---|---|---|
| POST | `/api/auth/login` | Iniciar sesión y obtener token JWT |
| POST | `/api/auth/registro` | Registrar usuario (queda con rol VENDEDOR) |
| GET | `/api/usuarios` | Listar usuarios |
| GET | `/api/usuarios/{id}` | Obtener usuario |
| POST | `/api/usuarios` | Crear usuario con rol |
| PUT | `/api/usuarios/{id}` | Actualizar usuario |
| DELETE | `/api/usuarios/{id}` | Eliminar usuario |
| GET | `/api/roles` | Listar roles |

### Ejemplos de cuerpo

Login:
```json
{ "username": "admin", "password": "admin123" }
```

Respuesta:
```json
{ "token": "eyJhbGciOi...", "tipo": "Bearer", "username": "admin", "rol": "ADMIN" }
```

Crear usuario:
```json
{ "username": "vendedor1", "email": "vendedor1@duoc.cl", "password": "clave123", "rol": "VENDEDOR" }
```

## Respuestas de error

| Código | Caso |
|---|---|
| 400 | Datos inválidos (email mal formado, contraseña corta, etc.) |
| 401 | Usuario o contraseña incorrectos |
| 404 | Usuario o rol no encontrado |
| 409 | Usuario o email ya registrado |
