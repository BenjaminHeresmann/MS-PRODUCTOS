# MS-PRODUCTOS - Microservicio de Gestión de Productos

## Descripción
Microservicio REST para la gestión de productos con integración de información de stock. Implementa HATEOAS, documentación OpenAPI/Swagger y pruebas unitarias completas.

## Características
- ✅ **CRUD completo** de productos
- ✅ **Integración con microservicio de inventario** para obtener stock
- ✅ **HATEOAS** - Enlaces hipermedia navegables
- ✅ **OpenAPI/Swagger** - Documentación automática de API
- ✅ **Validaciones** con Bean Validation
- ✅ **Pruebas unitarias** con JUnit 5 y Mockito
- ✅ **Health checks** con Spring Boot Actuator
- ✅ **Configuraciones por ambiente** (dev/prod)

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

## Ejecución Local

### Prerrequisitos
- Java 17+
- Maven 3.6+
- Oracle Database con wallet configurado

### Comandos
```bash
# Compilar
./mvnw clean compile

# Ejecutar pruebas
./mvnw test

# Ejecutar aplicación
./mvnw spring-boot:run

# Generar JAR
./mvnw clean package
```

## Despliegue con Docker

### Construir imagen
```bash
# Generar JAR primero
./mvnw clean package -DskipTests

# Construir imagen Docker
docker build -t ms-productos .
```

### Ejecutar contenedor
```bash
docker run -p 8089:8089 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e DATABASE_URL=your_database_url \
  -e DATABASE_USERNAME=your_username \
  -e DATABASE_PASSWORD=your_password \
  -e INVENTARIO_SERVICE_URL=http://inventario-service:8085 \
  ms-productos
```

## Despliegue en la Nube

### Opción 1: Railway (Recomendado - Sin Docker)

#### Pasos para Railway:
1. **Crear cuenta en Railway**: https://railway.app
2. **Conectar repositorio**:
   - Click "New Project" → "Deploy from GitHub repo"
   - Seleccionar tu repositorio
3. **Configurar variables de entorno** en Railway:
   ```
   DATABASE_URL=jdbc:oracle:thin:@bdfullstack_high?TNS_ADMIN=Wallet_BDFULLSTACK
   DATABASE_USERNAME=BDCODEMONKEYS
   DATABASE_PASSWORD=BD_codemonkeys2025
   INVENTARIO_SERVICE_URL=http://localhost:8085
   SPRING_PROFILES_ACTIVE=prod
   ```
4. **Deploy automático**: Railway construirá y desplegará automáticamente

#### Archivos de configuración incluidos:
- `railway.json` - Configuración específica para Railway
- `system.properties` - Especifica Java 17

### Opción 2: Heroku
1. Crear `Procfile`: `web: java -jar target/prueba-0.0.1-SNAPSHOT.jar`
2. Configurar variables de entorno
3. Deploy con Git

### Opción 3: Docker + Plataformas Cloud
Para plataformas que soportan Docker (Google Cloud Run, AWS ECS, etc.):
- **Google Cloud Run**
- **AWS ECS**
- **Azure Container Instances**

## Testing

### Ejecutar todas las pruebas
```bash
./mvnw test
```

### Pruebas incluidas
- **ProductoControllerTest**: Pruebas de endpoints REST
- **ProductoServiceTest**: Pruebas de lógica de negocio
- Cobertura completa de los 5 endpoints principales

## Monitoreo

### Health Checks
- **URL**: `/actuator/health`
- **Respuesta**: Estado de la aplicación y dependencias

### Métricas
- **URL**: `/actuator/metrics`
- **Info**: `/actuator/info`

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

## Contribución
1. Fork del repositorio
2. Crear rama feature
3. Realizar cambios
4. Ejecutar pruebas
5. Crear Pull Request
