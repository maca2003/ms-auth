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

## Docker

El `Dockerfile` usa dos etapas:

1. **Compilación** (`maven:3.9.9-eclipse-temurin-21`): descarga las dependencias (en una capa separada para aprovechar la caché) y genera el `.jar`.
2. **Ejecución** (`eclipse-temurin:21-jre-alpine`): imagen liviana solo con el JRE, usuario sin privilegios (uid 1001), `HEALTHCHECK` y la JVM limitada al 75% de la memoria del contenedor.

```bash
docker build -t ms-auth:1.0.0 .
docker run -p 8081:8081 -e DB_HOST=host.docker.internal -e DB_PASSWORD=root --name ms-auth ms-auth:1.0.0
```

`DB_HOST=host.docker.internal` permite al contenedor usar el MySQL instalado en tu computador. Para levantar el sistema completo (todos los servicios + MySQL) usa el `docker-compose.yml` del repositorio [infraestructura-swarm](https://github.com/maca2003/infraestructura-swarm).

## CI/CD (GitHub Actions)

Archivo `.github/workflows/ci-cd.yml`. Se ejecuta en cada push o pull request a `main` y `develop`:

| Etapa | Ramas | Qué hace |
|---|---|---|
| Build y test | todas | `mvn clean verify` (compila, ejecuta pruebas y genera el `.jar`) |
| Provisionar Amazon ECR | `main` | Crea el repositorio de imágenes `ms-auth` en ECR si no existe |
| Construir y publicar imagen | `main` | `docker build` y `docker push` a ECR con etiquetas `<commit>` y `latest` |
| Desplegar en Docker Swarm | `main` | Por SSH al manager: `docker service update --image ... sistema_ms-auth` y verifica que `/api/usuarios` responda 200 a través del API Gateway |

Secrets requeridos (Settings → Secrets and variables → Actions):

| Secret | Valor |
|---|---|
| `AWS_ACCESS_KEY_ID`, `AWS_SECRET_ACCESS_KEY`, `AWS_SESSION_TOKEN` | Learner Lab → AWS Details → AWS CLI (cambian en cada sesión) |
| `EC2_SSH_KEY` | Contenido de `labsuser.pem` |
| `EC2_HOST` | IP pública del manager (salida `IpPublicaManager` del stack `cluster-swarm`) |

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
