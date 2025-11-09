# API de Registro de Usuarios - BCI Evaluación

API REST de Spring Boot 3.5.7 para el registro de usuarios con base de datos H2 en memoria y persistencia JPA.

## Descripción General

Esta aplicación proporciona un único endpoint REST para el registro de usuarios que:
- Valida los datos del usuario (formatos de correo y contraseña)
- Previene el registro duplicado de correos
- Genera tokens JWT para autenticación de cada usuario
- Almacena información del usuario con timestamps
- Asocia múltiples números de teléfono con cada usuario

## Stack Tecnológico

- **Java 21** con Spring Boot 3.5.7
- **Spring Data JPA** para persistencia
- **Base de Datos H2** (en memoria)
- **JWT (JSON Web Tokens)** para generación de tokens de autenticación
- **Gradle** como sistema de construcción
- **Lombok** para reducir código repetitivo
- **JUnit 5** para pruebas
- **SpringDoc OpenAPI** para documentación de API (Swagger)

## Estructura del Proyecto

```
src/main/java/cl/bci/evaluacion/
├── controller/
│   └── UserController.java              # Endpoints REST para registro de usuarios
├── service/
│   └── UserService.java                 # Lógica de negocio de usuarios
├── repository/
│   └── UserRepository.java              # Interfaz JPA para acceso a datos
├── model/
│   ├── entity/
│   │   ├── User.java                    # Entidad JPA para usuarios
│   │   └── Phone.java                   # Entidad JPA para teléfonos
│   └── dto/
│       ├── UserRequestDTO.java          # DTO para solicitud de registro
│       ├── UserResponseDTO.java         # DTO para respuesta de usuario
│       └── PhoneDTO.java                # DTO para teléfonos
├── validation/
│   ├── ValidEmail.java                  # Anotación para validación de email
│   ├── EmailValidator.java              # Implementación de validador de email
│   ├── ValidPassword.java               # Anotación para validación de contraseña
│   └── PasswordValidator.java           # Implementación de validador de contraseña
├── util/
│   └── JwtUtil.java                     # Utilidad para generación de tokens JWT
├── config/
│   └── OpenApiConfiguration.java        # Configuración de Swagger/OpenAPI
├── exception/
│   ├── DuplicateEmailException.java     # Excepción para correos duplicados
│   └── GlobalExceptionHandler.java      # Manejo global de excepciones
└── EvaluacionApplication.java           # Clase principal de Spring Boot

src/main/resources/
├── application.properties                # Configuración de la aplicación
└── schema.sql                            # Esquema de base de datos

docs/
├── arquitectura.drawio                   # Diagrama de arquitectura general
└── capas-arquitectura.drawio             # Diagrama de capas arquitectónicas
```

## Configuración

Los patrones de personalización están definidos en application.properties:

```properties
# Patrón de correo - por defecto: validación genérica de email
validation.email.pattern=^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$

# Patrón de contraseña - por defecto: 8+ caracteres con mayúscula y número
validation.password.pattern=^(?=.*[A-Z])(?=.*[0-9]).{8,}$

# JWT - Configuración de tokens
jwt.secret=mi_clave_secreta_super_segura_para_jwt_que_debe_ser_larga_y_compleja_123456789
jwt.expiration=86400000

# Consola H2 (para desarrollo)
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

```

## Endpoint de API

### Registrar Usuario

**POST** `/api/users`

#### Solicitud

```json
{
  "name": "Juan Rodriguez",
  "email": "juan@rodriguez.org",
  "password": "hunter2",
  "phones": [
    {
      "number": "1234567",
      "citycode": "1",
      "contrycode": "57"
    }
  ]
}
```

#### Respuesta Exitosa (201 Created)

```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "name": "Juan Rodriguez",
  "email": "juan@rodriguez.org",
  "created": "2025-11-07T23:30:00",
  "modified": "2025-11-07T23:30:00",
  "last_login": "2025-11-07T23:30:00",
  "token": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJqdWFuQHJvZHJpZ3Vlei5vcmciLCJyb2wiOiJ1c3VhcmlvIiwiaWF0IjoxNzMxMTM0MjAwLCJleHAiOjE3MzEyMjA2MDB9.xyz...",
  "isactive": true,
  "phones": [
    {
      "number": "1234567",
      "citycode": "1",
      "contrycode": "57"
    }
  ]
}
```

**Nota**: El campo `token` contiene un JWT (JSON Web Token) con:
- **Subject**: Email del usuario
- **Claim "rol"**: "usuario"
- **Algoritmo**: HS512
- **Expiración**: 24 horas (configurable)

#### Respuesta de Error (400 Bad Request)

```json
{
  "mensaje": "El correo ya registrado"
}
```

### Mensajes de Error

- **Correo Duplicado**: `"El correo ya registrado"`
- **Formato de Correo Inválido**: `"Formato de correo inválido"`
- **Formato de Contraseña Inválido**: `"Formato de contraseña inválido"`
- **Campo Faltante**: `"[Campo] es requerido"`
- **Lista de Teléfonos Vacía**: `"Al menos un teléfono es requerido"`

## Construcción y Ejecución

### Construcción

```bash
# Construcción completa con pruebas
./gradlew clean build

# Construcción sin pruebas
./gradlew clean build -x test
```

### Ejecutar Aplicación

```bash
# Inicia la aplicación (se ejecuta en http://localhost:8080)
./gradlew bootRun
```

La aplicación:
- Se inicia en `http://localhost:8080`
- Inicializa las tablas para `users` y `phones` en H2 (en memoria)
- Consola H2 disponible en `http://localhost:8080/h2-console`
- Documentación Swagger disponible en `http://localhost:8080/swagger-ui.html`
- OpenAPI Spec en `http://localhost:8080/v3/api-docs`

### Ejecutar Pruebas

```bash
./gradlew test
```

## Pruebas de la API

### Pruebas con SwaggerUI

SwaggerUI disponible en `http://localhost:8080/swagger-ui.html`.

### CURL

```bash
# Registro exitoso
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Juan Rodriguez",
    "email": "juan@rodriguez.com",
    "password": "SecurePass123",
    "phones": [
      {
        "number": "1234567",
        "citycode": "1",
        "contrycode": "57"
      }
    ]
  }'

# Formato de correo inválido
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Jane Doe",
    "email": "jane@invalid",
    "password": "SecurePass123",
    "phones": [{"number": "9876543", "citycode": "2", "contrycode": "57"}]
  }'

# Correo duplicado
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Duplicate",
    "email": "juan@rodriguez.com",
    "password": "SecurePass456",
    "phones": [{"number": "5555555", "citycode": "3", "contrycode": "57"}]
  }'
```

### Postman, Bruno o similar

👉 Importar la especificación OpenAPI V3 desde: `http://localhost:8080/v3/api-docs`

## Base de Datos

H2 en memoria con creación automática de tablas:

**Tabla users**
- id (UUID, PK)
- name (varchar)
- email (varchar, único)
- password (varchar)
- created (timestamp)
- modified (timestamp)
- last_login (timestamp)
- token (varchar) - almacena JWT
- is_active (boolean)

**Tabla phones**
- id (UUID, PK)
- number (varchar)
- citycode (varchar)
- countrycode (varchar)
- user_id (UUID, FK)

## Reglas de Validación

### Correo
- Debe coincidir con el patrón: formato email válido con TLD de cualquier país
- Acepta caracteres: alfanuméricos, puntos, guiones bajos, símbolos + y %
- TLD mínimo de 2 caracteres
- Campo requerido

### Contraseña
- Mínimo 8 caracteres
- Debe contener al menos una letra mayúscula
- Debe contener al menos un dígito
- Campo requerido

### Nombre
- String no vacío
- Campo requerido

### Teléfonos
- Al menos uno requerido
- Cada teléfono requiere: number, citycode, contrycode

## Notas

- La falta de ortografía "contrycode" y el mensaje de error "El correo ya registrado" se usan acorde al documento de especificaciones entregado para evitar inconsistencias, pero internamente el campo es llamado correctamente countryCode.
- Los tokens JWT se generan con el email del usuario como subject y un claim "rol" con valor "usuario".
- La clave secreta JWT (`jwt.secret`) debe cambiarse en producción por una clave más robusta y segura.

## Desarrollo

Para modificar patrones de validación:

1. Actualice `validation.email.pattern` o `validation.password.pattern` en `application.properties`
2. Recompile con `./gradlew build`
3. Reinicie la aplicación
**⚠️ Advertencia: Cambiar estos patrones podría hacer fallar los test unitarios.**

Para depurar con Consola H2:

1. Inicie la aplicación
2. Visite `http://localhost:8080/h2-console`
3. JDBC URL: `jdbc:h2:mem:testdb`
4. Usuario: `sa`
5. Sin contraseña requerida

Para explorar la API con documentación interactiva:

1. Inicie la aplicación
2. Visite `http://localhost:8080/swagger-ui.html`
3. Vea todos los endpoints y esquemas de solicitud/respuesta
4. Pruebe endpoints directamente desde la interfaz

## Licencia

Este es un proyecto de evaluación técnica para BCI. Todos los derechos reservados

