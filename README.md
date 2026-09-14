# cbbackend — API REST de Créditos y Pagos

Backend en **Spring Boot 4.1.1 + Java 21 + Maven** para un sistema bancario simple: gestión de **usuarios, créditos y pagos**, con autenticación por **JWT (JJWT + HS256)**, persistencia con **JPA / Hibernate** y base de datos en memoria **H2** (con alternativa PostgreSQL comentada en la configuración).

> Stack: `spring-boot-starter-webmvc` · `spring-boot-starter-data-jpa` · `spring-boot-starter-validation` · `H2` · `JJWT 0.13.0` · `Lombok` · `springdoc-openapi` · `spring-boot-devtools`.

---

## 1. Puesta en marcha rápida

### Requisitos
- Java 21
- Maven 3.9+ (o usar el wrapper incluido `mvnw` / `mvnw.cmd`)
- Puerto `8080` libre

### Comandos esenciales
```bash
# Compilar (usa clean si el comportamiento parece "viejo" por target/)
mvn -q clean compile

# Ejecutar la aplicación
mvn spring-boot:run
# → http://localhost:8080
# → Consola H2: http://localhost:8080/h2-console (JDBC URL: jdbc:h2:mem:testdb, usuario: sa)

# Ejecutar tests (un solo test de carga de contexto, ~10s)
mvn -q test
```

### Configuración clave (`src/main/resources/application.properties`)

| Clave | Valor / efecto |
|---|---|
| `jwt.secret` | **Obligatoria al arrancar.** Clave Base64 de ≥32 bytes para firmar HS256. Sin ella falla el contexto con `UnsatisfiedDependencyException`. |
| `spring.datasource.url` | `jdbc:h2:mem:testdb` (en memoria: **los datos se reinician en cada arranque**). |
| `spring.jpa.hibernate.ddl-auto` | `update`: Hibernate crea/actualiza el esquema automáticamente. No hay migraciones (Flyway/Liquibase). |
| `spring.jpa.defer-datasource-initialization` | `true`: hace que `data.sql` se ejecute **después** de que Hibernate cree las tablas. Sin esto el seed fallaría. |
| `spring.h2.console.enabled` | `true` en `/h2-console` (solo para desarrollo). |

### Datos de prueba (`data.sql`)
Se insertan 3 usuarios y 7 créditos en cada arranque:
- `john@example.com` / `john123` (3 créditos: CR-001, CR-002, CR-007)
- `jane@example.com` / `jane123` (2 créditos: CR-003, CR-004)
- `mike@example.com` / `mike123` (2 créditos: CR-005, CR-006)

---

## 2. Endpoints de la API

| Método | Ruta | Auth | Descripción |
|---|---|---|---|
| `POST` | `/login` | Pública | Recibe `{email, password}`, devuelve `{token, message}`. |
| `GET` | `/credits` | `Bearer <token>` | Lista los créditos del usuario dueño del token. |
| `GET` | `/credits/{id}` | `Bearer <token>` | Detalle de un crédito por id numérico. |
| `POST` | `/payments` | `Bearer <token>` | Registra un pago. Body: `{title, identifier}`. Cobra la `monthlyFee` del crédito, resta el `balance` y extiende `expiration` +1 mes. |
| `GET` | `/payments/{id}` | `Bearer <token>` | Detalle de un pago por id numérico. |

**Formato de respuesta típico:**
```json
// Éxito
{ "credits": [ ... ] }
{ "credit": { "title": "...", "identifier": "CR-001", ... } }
{ "message": "Pago registrado correctamente", "payment": { ... } }

// Error
{ "message": "No se encontró el credito" }
```

Documentación OpenAPI (springdoc) disponible si se levanta la app, típicamente en `/swagger-ui.html` y `/v3/api-docs`.

---

## 3. Arquitectura: Capas Clásicas de Spring (Layered Architecture)

El proyecto usa una **arquitectura en capas**, la forma estándar de organizar un monolito Spring MVC:

```
                    HTTP / JSON
                        │
                        ▼
┌─────────────────────────────────────┐
│ controller/  (capa web)             │  @RestController. Fina: valida token,
│  UserController                     │  delega al servicio, devuelve ResponseEntity.
│  CreditController                   │
│  PaymentController                  │
└────────────────┬────────────────────┘
                 │ DTOs de entrada (Login, Pay)
                 ▼
┌─────────────────────────────────────┐
│ service/  (capa de negocio)         │  @Service / @Component. Aquí vive TODA
│  UserService, CreditService,        │  la lógica: consultas, reglas de pago,
│  PaymentService, TokenService,      │  construcción de respuestas y mapeo a DTOs.
│  Service (validador)                │
└────────────────┬────────────────────┘
                 │
                 ▼
┌─────────────────────────────────────┐
│ repository/ (capa de acceso)        │  Interfaces Spring Data JPA. Sin SQL manual:
│  UserRepository,                    │  métodos derivados (findByEmail, findByUser…).
│  CreditRepository,                  │
│  PaymentRepository                  │
└────────────────┬────────────────────┘
                 │ Entidades JPA
                 ▼
┌─────────────────────────────────────┐
│ model/ + DB (H2 / PostgreSQL)       │  @Entity: User, Credit, Payment.
└─────────────────────────────────────┘
```

**DTOs atraviesan controller ↔ service:**
- **Entrada:** `Login`, `Pay`
- **Salida:** `CreditResponse`, `PayResponse`

### Reglas arquitectónicas del proyecto
1. **Controladores delgados, servicios gruesos.** El controlador solo extrae el header `Authorization`, quita el prefijo `Bearer `, llama a `Service.validateToken()` y delega. Si el token no es `200 OK`, retorna el error sin tocar el servicio de negocio.
2. **Los servicios devuelven `ResponseEntity<Map<String, Object>>`.** La clave del mapa es el contrato (`credits`, `credit`, `payment`, `message`). Es simple pero acopla HTTP a la capa de negocio.
3. **Nunca se devuelven entidades JPA al cliente.** Se mapean a DTOs (`CreditResponse`, `PayResponse`). Motivo técnico: las asociaciones son `LAZY` y Jackson serializaría proxies de Hibernate sin inicializar como campos nulos/ceros (esto ya se observó en `GET /credits/{id}`).

### Estructura de paquetes (`com.example.cbbackend`)
```
CbbackendApplication.java        # @SpringBootApplication, punto de arranque
controller/  UserController.java, CreditController.java, PaymentController.java
service/     UserService.java, CreditService.java, PaymentService.java,
             TokenService.java, Service.java
repository/  UserRepository.java, CreditRepository.java, PaymentRepository.java
model/       User.java, Credit.java, Payment.java
dto/         Login.java, Pay.java, CreditResponse.java, PayResponse.java
resources/   application.properties, data.sql
```

---

## 4. Patrones y Decisiones de Diseño Usados

### 4.1 MVC REST (Model-View-Controller sin vista)
Spring WebMVC con `@RestController` + `@GetMapping/@PostMapping`. No hay plantillas: el "View" es JSON serializado por Jackson. Cada controlador tiene un `@RequestMapping` base (`/credits`, `/payments`) y métodos por operación.

### 4.2 Service Layer (Capa de Servicios)
`UserService`, `CreditService`, `PaymentService` concentran las reglas de negocio (buscar usuario por email del token, validar existencia del crédito, actualizar saldo/vencimiento al pagar). Son singletons de Spring (`@Service`) inyectados con `@Autowired` por campo.

> ⚠️ **Peculiaridad:** Existe un Bean **llamado** `Service` (`service/Service.java`, anotado con `@org.springframework.stereotype.Service` con nombre totalmente cualificado). **No es la anotación**, es una clase de utilidad cuya única misión es `validateToken()`. No confundirla con el estereotipo `@Service` del framework.

### 4.3 Repository Pattern (Spring Data JPA)
Los repositorios son interfaces que extienden `JpaRepository<Entidad, Long>`. Spring genera la implementación en tiempo de ejecución. No hay SQL escrito a mano; se usan **derived query methods**:

```java
User findByEmailAndPassword(String email, String password);
User findByEmail(String email);
List<Credit> findByUser(User user);
Credit findByIdentifier(String identifier);
Payment findByIdentifier(String identifier);
// + findById(Long) heredado → devuelve Optional, se usa .orElse(null)
```

Esto es el patrón **Repository**: la capa de negocio no sabe si hay H2 o PostgreSQL debajo.

### 4.4 DTO (Data Transfer Object)
`dto/` separa el contrato externo del modelo interno:

- **Entrada:** `Login(email, password)`, `Pay(title, identifier)`.
- **Salida:** `CreditResponse` (9 campos del crédito **sin** el `User` asociado), `PayResponse(title, identifier, monthlyFee, paidDate, operation)`.

Ventajas aquí:
- Evita exponer `password` (además `User.password` es `WRITE_ONLY` para Jackson)
- Evita recursión infinita `Credit → User → …`
- Evita el problema de los proxies `LAZY` (ver §3.3)

### 4.5 Dependency Injection (IoC / Inversión de Control)
Todo el cableado es por inyección de Spring (`@Autowired`, `@Component`, `@Service`, `@Repository`, `@Value("${jwt.secret}")`). La clase `CbbackendApplication` con `@SpringBootApplication` activa el auto-escaneo y el contenedor crea un único bean por servicio.

### 4.6 Autenticación Stateless con JWT (Manual, sin Spring Security)
No se usa `spring-boot-starter-security` ni filtros. El flujo es artesanal:

1. `POST /login` → `UserService.validateUser()` busca por email+password en claro y si existe llama a `TokenService.generateToken(email)`.
2. `TokenService` firma con JJWT HS256: `subject = email`, `issuedAt = ahora`, `expiration = +7 días`, clave de `jwt.secret`.
3. En cada ruta protegida el controlador hace:
   `authorization.replace("Bearer ", "")` → `Service.validateToken(token)` → `TokenService.validateToken()` (parsea firma + busca que el email exista en BD) → si OK, `TokenService.extractIdentifier(token)` para saber quién llama (p. ej. `CreditService.findUserCredits` filtra por ese usuario).

Es un esquema **stateless** (el servidor no guarda sesiones), pero simplificado: sin refresh tokens, sin roles/authorities, sin `SecurityFilterChain`.

### 4.7 Active Record vía Lombok + JPA
Las entidades usan `@Data` (getters/setters/`toString` generados) y `@Entity/@Table/@Id/@GeneratedValue(IDENTITY)`. `Payment` además lleva `@NoArgsConstructor + @AllArgsConstructor` — el `@NoArgsConstructor` es obligatorio porque los servicios hacen `new Payment()` y JPA lo exige.

### 4.8 Open/Closed Principle en Repositorios
Los repositorios están cerrados a modificación pero abiertos a extensión: para añadir una consulta nueva se declara un método en la interfaz (`findByXxx`) y Spring Data lo implementa automáticamente.

---

## 5. Modelo de Dominio y JPA (Detalle Técnico)

```
User (users) 1 ─── * Credit (credits)
  id (PK)             id (PK)
  email               title, identifier, status, balance, monthlyFee,
  password            expiration, rate, initDate, totalTerm
                      user_id (FK → users.id, ManyToOne LAZY)

User 1 ── * Payment (payments) * ── 1 Credit
              id (PK), title, identifier, paidAmount,
              payDate, operationNumber,
              credit_id (FK → credits.id, ManyToOne LAZY)
              user_id   (FK → users.id,   ManyToOne LAZY)
```

- `Payment` es `@ManyToOne` hacia `Credit` y `User` (un crédito/usuario tiene muchos pagos). **No debe cambiarse a `@OneToOne`**.
- `FetchType.LAZY` en todas las asociaciones: las entidades relacionadas solo se cargan si se accede a ellas dentro de una transacción. Por eso el punto 3 de §3 (mapear a DTO) es crítico.
- Hibernate mapea camelCase → snake_case automáticamente (`monthlyFee` → `monthly_fee`, `user` → `user_id`), que es lo que usa `data.sql`.
- Validación con Bean Validation (`@NotNull` con mensajes en español) en entidades.
- `@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)` en `User.password` para que nunca se serialice en respuestas.

---

## 6. Flujo de Datos Detallado (Ejemplo: Registrar un Pago)

```
POST /payments  (Bearer <token>)
     │
     ▼
PaymentController.savePayment()
     │  1. Extrae token del header
     │  2. Service.validateToken(token) → 200 OK o 401
     │  3. TokenService.extractIdentifier(token) → email
     ▼
PaymentService.savePayment(Pay pay, String email)
     │  1. Credit credit = creditRepository.findByIdentifier(pay.getIdentifier())
     │  2. User user = userRepository.findByEmail(email)
     │  3. Crea Payment (new Payment()) copiando title/identifier del crédito
     │     - paidAmount = credit.monthlyFee (regla de negocio: siempre paga la cuota)
     │     - payDate = LocalDate.now()
     │     - operationNumber = "96341" (hardcodeado - deuda técnica)
     │     - Relaciona payment.credit = credit, payment.user = user
     │  4. paymentRepository.save(payment)
     │  5. Actualiza crédito: balance -= monthlyFee, expiration += 1 mes
     │  6. creditRepository.save(credit)
     │  7. Construye PayResponse (DTO de salida)
     ▼
ResponseEntity<Map<String, Object>> con { message, payment }
```

---

## 7. Limitaciones Conocidas / Deuda Técnica (Honestidad Arquitectónica)

| Área | Problema | Mejora Sugerida |
|---|---|---|
| **Seguridad** | Contraseñas en claro (`findByEmailAndPassword`, seed con `john123`) | `PasswordEncoder` (BCrypt) + Spring Security |
| **Auth** | Sin `SecurityFilterChain`. Validación manual repetida en cada controlador | Filtro `OncePerRequestFilter` + `@ControllerAdvice` para 401 consistentes |
| **Pagos** | Lógica simplificada: `operationNumber` fijo, sin validar saldo, sin idempotencia | Validaciones de dominio, idempotency keys, reglas de mora |
| **Errores** | `try/catch → 500` repetido + `orElse(null)` + `if == null` | Excepciones de dominio + `@ControllerAdvice` global |
| **TokenService** | Mensaje confuso: devuelve 200 con `"Token no es válido"` cuando SÍ es válido | Renombrar método / corregir mensaje |
| **Testing** | Solo `@SpringBootTest` de carga de contexto; sin tests de negocio ni CI | Tests unitarios (Mockito) + tests de integración (@DataJpaTest, @WebMvcTest) + pipeline CI |
| **Migraciones** | `ddl-auto=update` en producción es peligroso | Flyway / Liquibase |
| **Observabilidad** | Sin logging estructurado, métricas, tracing | Micrometer + Prometheus/Grafana, Logback JSON |

---

## 8. Cómo Explicarlo en 1 Minuto (Guion para Equipo)

> "Es un monolito Spring Boot organizado en **capas**: controllers finos que solo validan el JWT y delegan; services con toda la lógica que devuelven mapas listos para JSON; repositories Spring Data que generan las consultas por nombre de método; y entidades JPA User-Credit-Payment relacionadas con **ManyToOne lazy**. Para no filtrar proxies de Hibernate al exterior, todo lo que sale por la API se mapea a **DTOs**. La auth es **JWT stateless de 7 días** firmado con HS256, pero **sin Spring Security**: cada controlador valida el Bearer a mano. La base es **H2 en memoria** creada por Hibernate con seed en `data.sql`, así que cada reinicio vuelve al estado inicial."

---

## 9. Decisiones de Diseño Clave (Para Discutir en Code Review)

1. **¿Por qué `ResponseEntity<Map<String, Object>>` en servicios?**
   - Simplicidad inicial, pero acopla la capa de servicio al protocolo HTTP. Alternativa: servicios devuelven objetos de dominio/DTOs y un `@ControllerAdvice` o `ResponseEntityExceptionHandler` construye la respuesta HTTP.

2. **¿Por qué validación manual del token en cada controlador?**
   - Evita la complejidad de Spring Security para un MVP. Coste: duplicación y riesgo de inconsistencia. Migración natural → `SecurityFilterChain` + `JwtAuthenticationFilter`.

3. **¿Por qué `Service` (clase) se llama igual que la anotación `@Service`?**
   - Decisión histórica/accidental. Confunde. Renombrar a `TokenValidatorService` o `AuthValidator` eliminaría la ambigüedad.

4. **¿Por qué `Payment` tiene `@NoArgsConstructor` obligatorio?**
   - JPA exige constructor sin args para instanciar entidades al leer de BD. Los servicios también usan `new Payment()` para crear pagos nuevos. Lombok `@AllArgsConstructor` es solo conveniencia.

5. **¿Por qué `CreditService.getCredit(Long id)` NO valida que el crédito pertenezca al usuario del token?**
   - Bug de autorización a nivel de objeto (IDOR). Cualquier usuario autenticado puede ver cualquier crédito por ID. Debería filtrar por `credit.getUser().getEmail() == tokenEmail`.

---

## 10. Referencias Rápidas

- **JWT Secret**: Mínimo 32 bytes (256 bits) para HS256. La actual en `application.properties` es válida.
- **H2 Console**: `http://localhost:8080/h2-console` → JDBC URL: `jdbc:h2:mem:testdb`, User: `sa`, Password: *(vacío)*
- **Swagger UI**: `http://localhost:8080/swagger-ui.html` (si springdoc está activo)
- **Compilación limpia**: `mvn -q clean compile` (evita clases stale en `target/`)
- **Test**: `mvn -q test` (levanta contexto completo con H2 + data.sql)