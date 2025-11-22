# BiblioDigit API 📚

API REST para gestión de biblioteca digital construida con Spring Boot 3.4.1 y PostgreSQL.

## 🚀 Tecnologías

- **Java 21**
- **Spring Boot 3.4.1**
  - Spring Web
  - Spring Data JPA
  - Hibernate
- **PostgreSQL 16**
- **Gradle 9.2.0**
- **Docker & Docker Compose**
- **Lombok**

## 📋 Requisitos Previos

- Docker 20.10+
- Docker Compose 2.0+
- Java 21 (solo para desarrollo local)
- Gradle 9.2+ (opcional, se usa wrapper)


## Diagramas 

### Diagrama de Caso de Uso

```mermaid
graph TB
    User((Usuario))
    Student((Estudiante))
    Teacher((Docente))
    External((Externo))
    Admin((Administrador))
    
    User -->|hereda| Student
    User -->|hereda| Teacher
    User -->|hereda| External
    
    Student --> UC1[Consultar Libros Disponibles]
    Student --> UC2[Solicitar Préstamo]
    Student --> UC3[Devolver Libro]
    Student --> UC4[Ver Mis Préstamos]
    
    Teacher --> UC1
    Teacher --> UC2
    Teacher --> UC3
    Teacher --> UC4
    
    External --> UC1
    External --> UC2
    External --> UC3
    External --> UC4
    
    Admin --> UC5[Registrar Libro]
    Admin --> UC6[Gestionar Usuarios]
    Admin --> UC7[Ver Todos los Préstamos]
    Admin --> UC8[Calcular Multas]
    
    UC2 --> |valida| System[Sistema]
    UC3 --> |actualiza| System
    System --> |verifica disponibilidad| UC1
    System --> |aplica reglas| UC2
```

### Diagrama de Clases

```mermaid
classDiagram
    %% ========== ENTIDADES DE DOMINIO ==========
    
    class Book {
        -Long id
        -String title
        -String isbn
        -Integer year
        -Author author
        -Category category
        +getId()
        +getTitle()
        +getIsbn()
    }
    
    class Author {
        -Long id
        -String name
        -String nationality
        -List~Book~ books
        +getId()
        +getName()
    }
    
    class Category {
        -Long id
        -String name
        -String description
        -List~Book~ books
        +getId()
        +getName()
    }
    
    class User {
        -Long id
        -String name
        -String email
        -String password
        -TypeUser typeUser
        -Boolean isActive
        +getId()
        +getName()
        +getTypeUser()
    }
    
    class TypeUser {
        -Long id
        -String type
        -String description
        -Integer maxBooks
        -Integer maxDays
        +getType()
        +getMaxBooks()
        +getMaxDays()
    }
    
    class Stock {
        -Long id
        -Book book
        -User user
        -Boolean availability
        -LocalDateTime departureDate
        -LocalDateTime deliveryDate
        -LoanStatus status
        +isAvailable()
        +getDaysOverdue()
        +calculateFine()
    }
    
    class LoanStatus {
        <<enumeration>>
        ACTIVE
        RETURNED
        OVERDUE
    }
    
    %% ========== RELACIONES DE DOMINIO ==========
    
    Book "N" --> "1" Author : belongsTo
    Book "N" --> "1" Category : belongsTo
    User "N" --> "1" TypeUser : hasType
    Stock "N" --> "1" Book : references
    Stock "N" --> "1" User : borrowedBy
    Stock --> LoanStatus : hasStatus
    
    %% ========== INTERFACES DE REPOSITORIO ==========
    
    class BookRepository {
        <<interface>>
        +findAll() List~Book~
        +findById(Long) Optional~Book~
        +save(Book) Book
        +findByIsbn(String) Optional~Book~
    }
    
    class UserRepository {
        <<interface>>
        +findAll() List~User~
        +findById(Long) Optional~User~
        +findByEmail(String) Optional~User~
        +save(User) User
    }
    
    class StockRepository {
        <<interface>>
        +findAll() List~Stock~
        +findById(Long) Optional~Stock~
        +save(Stock) Stock
        +findByUserIdAndStatus(Long, LoanStatus) List~Stock~
        +findByBookIdAndAvailability(Long, Boolean) Optional~Stock~
        +countActiveLoansForUser(Long) Long
    }
    
    %% ========== INTERFACES DE SERVICIO (PUERTOS) ==========
    
    class LoanService {
        <<interface>>
        +borrowBook(Long userId, Long bookId) Stock
        +returnBook(Long stockId) Stock
        +getActiveLoansForUser(Long userId) List~Stock~
        +getOverdueLoans() List~Stock~
        +calculateFine(Long stockId) BigDecimal
    }
    
    class BookService {
        <<interface>>
        +findAll() List~Book~
        +findById(Long id) Optional~Book~
        +findAvailableBooks() List~Book~
        +searchByTitle(String title) List~Book~
    }
    
    class UserService {
        <<interface>>
        +register(RegisterRequest) User
        +findById(Long id) Optional~User~
        +canUserBorrow(Long userId) boolean
    }
    
    %% ========== IMPLEMENTACIONES DE SERVICIO ==========
    
    class LoanServiceImpl {
        -StockRepository stockRepository
        -BookRepository bookRepository
        -UserRepository userRepository
        -LoanValidatorFactory validatorFactory
        +borrowBook(Long, Long) Stock
        +returnBook(Long) Stock
        +calculateFine(Long) BigDecimal
    }
    
    class BookServiceImpl {
        -BookRepository bookRepository
        -StockRepository stockRepository
        +findAll() List~Book~
        +findAvailableBooks() List~Book~
    }
    
    class UserServiceImpl {
        -UserRepository userRepository
        -TypeUserRepository typeUserRepository
        +register(RegisterRequest) User
        +canUserBorrow(Long) boolean
    }
    
    %% ========== ESTRATEGIA DE VALIDACIÓN (OCP) ==========
    
    class LoanValidationStrategy {
        <<interface>>
        +canBorrow(User, int) boolean
        +getMaxBooks() int
        +getMaxDays() int
    }
    
    class StudentLoanValidator {
        +canBorrow(User, int) boolean
        +getMaxBooks() int : 3
        +getMaxDays() int : 14
    }
    
    class TeacherLoanValidator {
        +canBorrow(User, int) boolean
        +getMaxBooks() int : 5
        +getMaxDays() int : 30
    }
    
    class ExternalLoanValidator {
        +canBorrow(User, int) boolean
        +getMaxBooks() int : 2
        +getMaxDays() int : 7
    }
    
    class LoanValidatorFactory {
        +getValidator(String) LoanValidationStrategy
    }
    
    %% ========== RELACIONES DE IMPLEMENTACIÓN ==========
    
    LoanService <|.. LoanServiceImpl : implements
    BookService <|.. BookServiceImpl : implements
    UserService <|.. UserServiceImpl : implements
    
    LoanValidationStrategy <|.. StudentLoanValidator : implements
    LoanValidationStrategy <|.. TeacherLoanValidator : implements
    LoanValidationStrategy <|.. ExternalLoanValidator : implements
    
    LoanServiceImpl --> StockRepository : uses
    LoanServiceImpl --> BookRepository : uses
    LoanServiceImpl --> UserRepository : uses
    LoanServiceImpl --> LoanValidatorFactory : uses
    
    BookServiceImpl --> BookRepository : uses
    BookServiceImpl --> StockRepository : uses
    
    UserServiceImpl --> UserRepository : uses
    
    LoanValidatorFactory --> LoanValidationStrategy : creates
```


## 🛠️ Instalación y Configuración

### 1. Clonar el Repositorio

```bash
git clone <url-del-repositorio>
cd bibliodigit
```

### 2. Configurar Variables de Entorno

Crea un archivo `.env` en la raíz del proyecto:

```env
# Base de Datos PostgreSQL
DB_NAME=bibliodigit
DB_USER=postgres
DB_PASSWORD=tu_password_seguro
DB_URL=jdbc:postgresql://db:5432/bibliodigit
```

### 3. Estructura del Proyecto

```
bibliodigit/
├── src/
│   ├── main/
│   │   ├── java/org/bibliodigit/
│   │   │   ├── App.java                      # Clase principal
│   │   │   ├── api/                          # Controladores REST
│   │   │   │   ├── dto/                      # Data Transfer Objects
│   │   │   │   │  ├── req/                   
│   │   │   │   │  └── res/
│   │   │   │   ├── exception/                # Manejo de excepciones
│   │   │   │   └── mapper/                   # Mappers
│   │   │   ├── application/                  # Casos de uso
│   │   │   │   └── service/
│   │   │   ├── domain/                       # Entidades de dominio
│   │   │   │   └── port/                     # Interfaces de servicios
│   │   │   └── repository/                   # Repositorios JPA
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       ├─── java/org/bibliodigit/
│       │    ├── api/ 
│       │    ├── application/
│       │    │   └── service/
│       │    ├── integration/
│       │    └── repository/
│       └── resources/
│           └── application-test.properties
├── docker-compose.yml
├── Dockerfile
├── build.gradle.kts
├── settings.gradle.kts
├── gradle/
│   └── libs.versions.toml
└── .env
```

## 🐳 Ejecución con Docker

### Opción 1: Modo Producción (Recomendado)

Ejecuta la aplicación completa en contenedores:

```bash
# Construir y ejecutar
docker-compose up --build

# Ejecutar en segundo plano
docker-compose up --build -d

# Ver logs
docker-compose logs -f app-service

# Detener
docker-compose down
```

La aplicación estará disponible en: **http://localhost:8080**

Y para los test el siguiente commando
```bash
docker build --target test --no-cache -t bibliodigit-test:latest .
```

### Opción 2: Reconstruir después de cambios

Cuando hagas cambios en el código:

```bash
# Detener, reconstruir y reiniciar
docker-compose down && docker-compose up --build
```

## 💻 Ejecución en Modo Desarrollo

Para desarrollo activo con **hot reload**:

### 1. Levantar solo PostgreSQL

```bash
docker-compose up db
```

### 2. Ejecutar la aplicación localmente

En otra terminal:

```bash
# Con Gradle Wrapper (recomendado)
./gradlew bootRun

# O con Gradle instalado
gradle bootRun
```

### 3. Ejecutar tests

```bash
# Todos los tests
./gradlew test

# Tests con reporte detallado
./gradlew test --info

# Limpiar y construir
./gradlew clean build
```

## 📡 Endpoints de la API

### Health Check

```http
GET http://localhost:8080/api/health
```

**Respuesta:**
```json
{
  "status": "UP",
  "service": "BiblioDigit API",
  "version": "1.0.2"
}
```


### Book API`s
#### CRUD:
```http
POST   /api/books                    # Crear libro
GET    /api/books                    # Listar todos
GET    /api/books/{id}               # Obtener por ID
PUT    /api/books/{id}               # Actualizar
DELETE /api/books/{id}               # Eliminar
```

#### Búsquedas
```http
GET /api/books/search/title?keyword=clean
GET /api/books/author/{authorId}
GET /api/books/search/author?name=tolkien
GET /api/books/category/{categoryId}
GET /api/books/search/category?name=Fiction
GET /api/books/year/2020
GET /api/books/year-range?start=2000&end=2020
```
### Estadísticas
```http
GET /api/books/count/author/{authorId}
GET /api/books/count/category/{categoryId}
GET /api/books/exists?title=Clean Code
```
## 🗄️ Base de Datos

### Conexión Directa a PostgreSQL

```bash
# Desde el contenedor
docker exec -it bibilio_db psql -U postgres -d bibliodigit

# Desde tu máquina (si tienes psql instalado)
psql -h localhost -p 5432 -U postgres -d bibliodigit
```

### Comandos SQL útiles

```sql
-- Listar tablas
\dt

-- Ver estructura de tabla books
\d books

-- Consultar libros
SELECT * FROM books;

-- Salir
\q
```

## 🧪 Testing

### Ejecutar Tests

```bash
# Todos los tests
./gradlew test

# Tests específicos
./gradlew test --tests "AppTest"

# Con coverage
./gradlew test jacocoTestReport
```

Los tests usan **H2 en memoria**, no afectan la base de datos de desarrollo.

## 📦 Construcción del JAR

```bash
# Construir JAR ejecutable
./gradlew bootJar

# El JAR se genera en:
# app/build/libs/app.jar
```

## 🔧 Troubleshooting

### Error: Puerto 8080 ya en uso

```bash
# Encontrar el proceso
lsof -i :8080

# Matar el proceso
kill -9 <PID>

# O cambiar el puerto en application.properties
server.port=8081
```

### Error: Puerto 5432 ya en uso (PostgreSQL)

```bash
# Detener PostgreSQL local
sudo systemctl stop postgresql

# O cambiar el puerto en docker-compose.yml
ports:
  - "5433:5432"  # Usar 5433 en lugar de 5432
```

### Error: Cannot connect to Docker daemon

```bash
# Iniciar Docker
sudo systemctl start docker

# Verificar estado
sudo systemctl status docker
```

### Limpiar todo Docker

```bash
# Detener y eliminar contenedores
docker-compose down

# Eliminar volúmenes (¡CUIDADO! Elimina datos)
docker-compose down -v

# Eliminar imágenes
docker rmi bibiliodigit:latest

# Reconstruir desde cero
docker-compose up --build
```

## 📊 Monitoreo

### Ver logs en tiempo real

```bash
# Todos los servicios
docker-compose logs -f

# Solo la aplicación
docker-compose logs -f app-service

# Solo la base de datos
docker-compose logs -f db
```

### Estado de los contenedores

```bash
# Ver contenedores corriendo
docker ps

# Ver todos los contenedores
docker ps -a

# Inspeccionar un contenedor
docker inspect bibilio_app
```

## 🚀 Despliegue

### Variables de Entorno de Producción

```env
# Producción
DB_NAME=bibliodigit_prod
DB_USER=bibliodigit_user
DB_PASSWORD=<c
