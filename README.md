# SBJ Function: CRUD en AWS Lambda con Spring Boot

Aplicación CRUD de usuarios con Spring Boot 4.0.1 y Java 21, pensada para desplegarse como **función AWS Lambda** (Spring Cloud Function) con base de datos MySQL 8.

## Características

- CRUD completo de usuarios (crear, consultar, actualizar, eliminar).
- Activar y desactivar usuarios (los usuarios inactivos no aparecen en las listas de activos).
- Validación de datos de entrada con Bean Validation.
- Códigos de estado propios en la respuesta: 200, 400 (datos o petición inválidos), 404 (no existe) y 409 (correo duplicado).
- Auditoría automática de fecha de creación y de modificación.
- Búsqueda paginada por nombre, apellido o correo.
- Pruebas unitarias y de repositorio (JUnit 5, Mockito, H2 y Testcontainers).

## Requisitos

- JDK 21 y Maven 3.9 o superior.
- MySQL 8 (en local o AWS RDS).
- AWS CLI (solo para desplegar).

## Compilar y ejecutar

```bash
mvn test
mvn clean package            # genera el JAR para Lambda
```

> **Nota:** con Lombok, el proyecto debe compilarse con JDK 21. Versiones de Lombok anteriores a la 1.18.40 no funcionan con JDK 25.

## Base de datos

```sql
CREATE DATABASE sbj_function_db;
CREATE USER 'sbj_user'@'localhost' IDENTIFIED BY 'una_clave_segura';
GRANT ALL PRIVILEGES ON sbj_function_db.* TO 'sbj_user'@'localhost';
```

Variables de entorno:

| Variable | Descripción | Valor por defecto |
|----------|-------------|-------------------|
| `DB_USERNAME` | Usuario de MySQL | `root` |
| `DB_PASSWORD` | Contraseña de MySQL | `password` (solo para desarrollo) |
| `SPRING_DATASOURCE_URL` | URL JDBC | MySQL local |

## Operaciones de la función

La función `userLambdaHandler` recibe un JSON con la operación y devuelve `{success, message, data, statusCode, timestamp}`.

| `operation` | Campos necesarios | Descripción |
|-------------|-------------------|-------------|
| `CREATE` | `userData` | Crea un usuario |
| `GET_BY_ID` | `userId` | Consulta un usuario |
| `GET_ALL_ACTIVE` | — | Lista los usuarios activos |
| `UPDATE` | `userId`, `userData` | Actualiza un usuario |
| `DELETE` | `userId` | Elimina un usuario |
| `ACTIVATE` / `DEACTIVATE` | `userId` | Activa o desactiva un usuario |
| `COUNT_ACTIVE` | — | Cuenta los usuarios activos |

Ejemplo de creación:

```json
{
  "operation": "CREATE",
  "userData": {
    "name": "Juan",
    "lastName": "Pérez",
    "email": "juan.perez@example.com",
    "phone": "5512345678"
  }
}
```

Ejemplo de consulta:

```json
{ "operation": "GET_BY_ID", "userId": 1 }
```

## Despliegue en AWS Lambda

1. Genera el paquete: `mvn clean package -DskipTests`.
2. Crea la función con el *runtime* **java21**:

```bash
aws lambda create-function \
  --function-name sbj-user-crud \
  --runtime java21 \
  --role arn:aws:iam::TU_CUENTA:role/lambda-execution-role \
  --handler org.springframework.cloud.function.adapter.aws.FunctionInvoker \
  --zip-file fileb://target/sbj-function-0.0.1-SNAPSHOT.jar \
  --timeout 30 --memory-size 1024 \
  --environment "Variables={DB_USERNAME=usuario,DB_PASSWORD=clave,SPRING_DATASOURCE_URL=jdbc:mysql://tu-rds:3306/sbj_function_db}"
```

3. Para actualizar el código: `aws lambda update-function-code --function-name sbj-user-crud --zip-file fileb://target/sbj-function-0.0.1-SNAPSHOT.jar`.
4. Prueba la función: `aws lambda invoke --function-name sbj-user-crud --payload file://evento.json respuesta.json`.

### Infraestructura necesaria

- **Rol IAM** con `AWSLambdaBasicExecutionRole` (y `AWSLambdaVPCAccessExecutionRole` si la base de datos está en una VPC).
- **Base de datos MySQL** en RDS, accesible desde la función (VPC, subredes y grupo de seguridad con el puerto 3306).
- Guarda las credenciales en *Secrets Manager* o en variables de entorno cifradas; nunca en el código.

### Rendimiento

- Reduce el *cold start* con *provisioned concurrency* o con SnapStart (disponible para Java).
- Usa un pool de conexiones pequeño: `spring.datasource.hikari.maximum-pool-size=5`.
- Monitoriza invocaciones, errores y duración en CloudWatch; el grupo de logs es `/aws/lambda/sbj-user-crud`.

## Estructura

```text
src/main/java/com/juniverse/function/
├── SbjFunctionApplication.java
├── dto/          # peticiones y respuestas (usuario y Lambda)
├── entity/       # entidad JPA User
├── exception/    # NotFoundException y ServiceException
├── lambda/       # UserLambdaHandler (la función)
├── repository/   # UserRepository (Spring Data JPA)
└── service/      # UserService y UserServiceImpl
```

## Notas

- Perfiles: `dev` (MySQL local), `test` (H2 en memoria) y `prod` (RDS).
- Antes el README mencionaba clases que no existen; esta versión describe la estructura real del proyecto.

## Licencia

Código de ejemplo de uso libre con fines educativos.
