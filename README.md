# MS-PRODUCTOS - Microservicio de Gestión de Productos

## Descripción
Microservicio REST para la gestión de productos con integración de información de stock. Implementa HATEOAS, documentación OpenAPI/Swagger y pruebas unitarias completas.

## Características
-  **CRUD completo** de productos
-  **Integración con microservicio de inventario** para obtener stock
-  **HATEOAS** - Enlaces hipermedia navegables
-  **OpenAPI/Swagger** - Documentación automática de API
-  **Validaciones** con Bean Validation
-  **Pruebas unitarias** con JUnit 5 y Mockito
-  **Health checks** con Spring Boot Actuator
-  **Configuraciones por ambiente** (dev/prod)

## Tecnologías
- **Spring Boot 3.4.5**
- **Java 17**
- **Oracle Database** con Wallet
- **Spring Data JPA**
- **Spring HATEOAS**
- **SpringDoc OpenAPI 3**
- **JUnit 5 + Mockito**

## Endpoints Principales

### Productos
- `GET /api/productos` - Listar todos los productos con stock
- `POST /api/productos` - Crear nuevo producto
- `GET /api/productos/{id}` - Obtener producto por ID con stock
- `PUT /api/productos/{id}` - Actualizar producto
- `DELETE /api/productos/{id}` - Eliminar producto

### Documentación y Monitoreo
- `GET /swagger-ui.html` - Interfaz de Swagger UI
- `GET /api-docs` - Especificación OpenAPI en JSON
- `GET /actuator/health` - Health check del servicio

## Configuración

### Variables de Entorno
```bash
# Base de datos
DATABASE_URL=jdbc:oracle:thin:@bdfullstack_high?TNS_ADMIN=Wallet_BDFULLSTACK
DATABASE_USERNAME=BDCODEMONKEYS
DATABASE_PASSWORD=BD_codemonkeys2025

# Servicio externo
INVENTARIO_SERVICE_URL=http://localhost:8085

# Puerto (opcional)
PORT=8089

# Perfil de Spring (dev/prod)
SPRING_PROFILES_ACTIVE=prod
```

## Estructura del Proyecto
```
src/
├── main/
│   ├── java/
│   │   └── prueba/com/prueba/
│   │       ├── Config/           # Configuraciones
│   │       ├── Controller/       # Controladores REST
│   │       ├── DTO/             # Objetos de transferencia
│   │       ├── Model/           # Entidades JPA
│   │       ├── Repository/      # Repositorios de datos
│   │       └── Service/         # Lógica de negocio
│   └── resources/
│       ├── application.properties
│       ├── application-dev.properties
│       └── application-prod.properties
└── test/                        # Pruebas unitarias
```

