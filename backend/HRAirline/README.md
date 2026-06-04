# HRAirline Backend

Spring Boot REST API for the HR Airline booking application.

## Security Configuration

HRAirline uses **stateless JWT Bearer authentication** with Spring Security. There is no server-side session; each request is validated through a custom `AuthFilter`.

### Security Components

| Component | File | Role |
|-----------|------|------|
| Main security config | `security/SecurityFilter.java` | `SecurityFilterChain`, CSRF, CORS, URL rules, session policy |
| JWT filter | `security/AuthFilter.java` | Reads `Authorization: Bearer <token>`, validates JWT, sets `SecurityContext` |
| JWT utility | `security/JwtUtils.java` | Sign, parse, and validate tokens (jjwt 0.12.6) |
| User loading | `security/CustomUserDetailsService.java` | `UserDetailsService` lookup by email |
| Principal | `security/AuthUser.java` | `UserDetails` wrapper around the `User` entity |
| CORS | `security/CorsConfig.java` | Global MVC CORS mappings |
| 401 handler | `exceptions/CustomAuthenticationEntryPoint.java` | JSON response for unauthenticated requests |
| 403 handler | `exceptions/CustomAccessDenialHandler.java` | JSON response for forbidden requests |

```mermaid
flowchart TB
    subgraph Config["Configuration Layer"]
        YAML["application.yaml<br/>JWT secret + expiration"]
        SF["SecurityFilter.java<br/>SecurityFilterChain"]
        CORS["CorsConfig.java<br/>Global CORS"]
    end

    subgraph Auth["Authentication Layer"]
        AF["AuthFilter<br/>(OncePerRequestFilter)"]
        JU["JwtUtils<br/>HmacSHA256 / jjwt"]
        CUDS["CustomUserDetailsService"]
        AU["AuthUser<br/>(UserDetails wrapper)"]
    end

    subgraph Data["Data Layer"]
        UR["UserRepo"]
        User["User entity<br/>email, password, roles, active"]
        Role["Role entity<br/>name → GrantedAuthority"]
    end

    subgraph Errors["Error Handlers"]
        EP401["CustomAuthenticationEntryPoint<br/>401 JSON"]
        EP403["CustomAccessDenialHandler<br/>403 JSON"]
    end

    subgraph Beans["Security Beans"]
        PE["BCryptPasswordEncoder"]
        AM["AuthenticationManager<br/>(not wired to login yet)"]
    end

    YAML --> JU
    SF --> AF
    SF --> EP401
    SF --> EP403
    SF --> CORS
    SF --> PE
    SF --> AM

    AF --> JU
    AF --> CUDS
    AF --> EP401
    CUDS --> UR
    CUDS --> AU
    AU --> User
    User --> Role
```

### Request Flow

```mermaid
sequenceDiagram
    autonumber
    participant Client
    participant CorsFilter
    participant AuthFilter
    participant JwtUtils
    participant UserDetailsService
    participant SecurityContext
    participant AuthzFilter as AuthorizationFilter
    participant EntryPoint as CustomAuthenticationEntryPoint
    participant Controller

    Client->>CorsFilter: HTTP Request<br/>(optional Authorization: Bearer JWT)
    CorsFilter->>AuthFilter: forward

    alt No Bearer token
        AuthFilter->>AuthzFilter: continue (no auth set)
    else Bearer token present
        AuthFilter->>JwtUtils: parse token → email
        alt Invalid/malformed JWT
            JwtUtils-->>AuthFilter: exception
            AuthFilter->>EntryPoint: commence()
            EntryPoint-->>Client: 401 JSON
        else Valid JWT structure
            AuthFilter->>UserDetailsService: loadUserByUsername(email)
            UserDetailsService-->>AuthFilter: AuthUser + roles
            AuthFilter->>JwtUtils: isTokenValid(token, user)?
            alt Token valid (email match + not expired)
                AuthFilter->>SecurityContext: setAuthentication
            else Token invalid/expired
                Note over AuthFilter: continues without auth
            end
            AuthFilter->>AuthzFilter: forward
        end
    end

    AuthzFilter->>AuthzFilter: check URL rules

    alt Public path (/api/auth/**, /api/airports/**, /api/flights/**)
        AuthzFilter->>Controller: allow
        Controller-->>Client: 200
    else Protected path + no Authentication
        AuthzFilter->>EntryPoint: commence()
        EntryPoint-->>Client: 401 JSON
    else Protected path + authenticated
        AuthzFilter->>Controller: allow
        Controller-->>Client: 200
    end
```

### Filter Chain

Configured in `SecurityFilter.java`:

```mermaid
flowchart LR
    subgraph Chain["SecurityFilterChain (in order)"]
        direction TB
        A["1. CSRF → DISABLED"]
        B["2. CORS → withDefaults()"]
        C["3. Exception Handling<br/>401 EntryPoint + 403 AccessDenied"]
        D["4. authorizeHttpRequests<br/>(URL rules)"]
        E["5. Session → STATELESS"]
        F["6. AuthFilter<br/>BEFORE UsernamePasswordAuthenticationFilter"]
    end

    A --> B --> C --> D --> E --> F
```

| Setting | Value |
|---------|-------|
| CSRF | Disabled (stateless JWT API) |
| Session | `STATELESS` — no server-side session |
| Custom filter | `AuthFilter` runs before `UsernamePasswordAuthenticationFilter` |
| Method security | `@EnableMethodSecurity` enabled (no `@PreAuthorize` usage yet) |

### URL Authorization Rules

```mermaid
flowchart TD
    REQ["Incoming Request"]

    REQ --> MATCH{URL matches?}

    MATCH -->|"/api/auth/**"| PUBLIC["permitAll() — no JWT needed"]
    MATCH -->|"/api/airports/**"| PUBLIC
    MATCH -->|"/api/flights/**"| PUBLIC
    MATCH -->|"Everything else<br/>(bookings, users, etc.)"| AUTH{Valid JWT in<br/>SecurityContext?}

    AUTH -->|Yes| OK["Proceed to controller"]
    AUTH -->|No| UNAUTH["401 via CustomAuthenticationEntryPoint"]

    PUBLIC --> OK
```

| Path pattern | Access |
|--------------|--------|
| `/api/auth/**` | Public |
| `/api/airports/**` | Public |
| `/api/flights/**` | Public |
| All other paths | Authenticated (valid JWT required) |

### JWT Configuration

JWT settings live in `src/main/resources/application.yaml`:

```yaml
app:
  jwt:
    secret-key: ${JWT_SECRET:<dev-fallback-key>}
    expiration-ms: 86400000  # 24 hours
```

| Setting | Value |
|---------|-------|
| Algorithm | HmacSHA256 |
| Subject | User email |
| Expiration | 24 hours (`86400000` ms) |
| Secret | `JWT_SECRET` environment variable (dev fallback in YAML) |
| Header format | `Authorization: Bearer <token>` |

```mermaid
flowchart LR
    subgraph Issue["Token creation (planned)"]
        Login["Login endpoint<br/>(not implemented yet)"]
        Login --> JU1["JwtUtils.generatedToken(email)"]
        JU1 --> Token["JWT<br/>subject = email<br/>exp = 24h"]
    end

    subgraph Validate["Token validation (AuthFilter)"]
        Header["Authorization: Bearer token"]
        Header --> Parse["getUsernameFromToken()"]
        Parse --> Load["loadUserByUsername(email)"]
        Load --> Check["isTokenValid()<br/>email match + not expired"]
        Check --> Context["SecurityContextHolder<br/>.setAuthentication()"]
    end

    Token --> Header
```

### Roles and Identity

```mermaid
flowchart LR
    User["User entity"] -->|"@ManyToMany"| Role["Role.name"]
    Role --> AuthUser["AuthUser.getAuthorities()"]
    AuthUser --> GA["SimpleGrantedAuthority(roleName)"]
    GA --> SC["SecurityContext<br/>(available for @PreAuthorize)"]

    User -->|"isActive()"| Enabled["isEnabled() check"]
    User -->|"email"| Username["getUsername() = email"]
    User -->|"BCrypt hash"| Password["getPassword()"]
```

- Roles are mapped as `SimpleGrantedAuthority(role.getName())` with **no automatic `ROLE_` prefix**.
- `AuthUser.isEnabled()` returns `user.isActive()`.
- `BCryptPasswordEncoder` is registered as a bean; login/register endpoints are not implemented yet.

### CORS Policy

```mermaid
flowchart LR
    MVC["CorsConfig<br/>/** → origins: *<br/>methods: GET, POST, PUT, DELETE"]
    SEC["SecurityFilter<br/>.cors(withDefaults())"]
    MVC --> SEC
    SEC --> Browser["Browser cross-origin requests allowed"]
```

| Setting | Value |
|---------|-------|
| Paths | `/**` |
| Origins | `*` (all) |
| Methods | GET, POST, PUT, DELETE |

### Environment Variables

| Variable | Purpose |
|----------|---------|
| `JWT_SECRET` | Overrides the default JWT signing key (required in production) |
| `EMAIL_PASSWORD` | SMTP password for email notifications |

### Implementation Status

| Ready | Not yet implemented |
|-------|---------------------|
| JWT validation filter | Login / register REST endpoints |
| URL-based auth rules | OAuth (Google/Facebook — data model only) |
| BCrypt password encoder bean | `AuthenticationManager` used for login |
| Role loading from DB | Method-level `@PreAuthorize` |
| 401/403 JSON error handlers | REST controllers (security rules are ahead of API surface) |

### Security File Index

```
src/main/java/com/hr/airline/
├── security/
│   ├── SecurityFilter.java      # SecurityFilterChain configuration
│   ├── AuthFilter.java          # JWT Bearer filter
│   ├── JwtUtils.java            # Token sign/parse/validate
│   ├── CorsConfig.java          # Global CORS
│   ├── CustomUserDetailsService.java
│   └── AuthUser.java
├── exceptions/
│   ├── CustomAuthenticationEntryPoint.java
│   └── CustomAccessDenialHandler.java
└── entities/
    ├── User.java
    └── Role.java
```
