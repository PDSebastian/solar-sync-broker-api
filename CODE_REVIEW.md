# Code Review — solar-sync-broker-api

Branch: `feat/fundatie` · Vertical-slice Spring Boot 3.5 · Java 21
Slices implementate: `auth`, `house`, `battery`, `users`, `system`
Teste: **32 verzi** (unit + IT) — dar problemele 🔴 de mai jos **nu sunt prinse de teste**.

## Legendă severitate

- 🔴 Corectitudine / securitate — de reparat
- 🟡 Design / consistență
- ⚪ Cosmetic (dar vizibil la evaluarea de licență)

## Reparat de la ultimul pass ✅

- `House.equals`/`hashCode` reduse la `id + name` (nu mai includ toate câmpurile).
- 401/403 ies acum ca `ApiErrorResponse` **JSON real** prin `JWTAuthentificationEntryPoint` + `SecurityAccessDeniedHandler`, populate cu `ApiErrorResponse.of(...)` (timestamp/error/path complete).

---

## 🔴 Trebuie reparate

### 1. Ownership decis din query param, nu din token — `HouseController.getHouseById`

```java
@GetMapping("/{id}")
@PreAuthorize("hasAuthority('house:view')")
public ResponseEntity<HouseResponse> getHouseById(@PathVariable Long id, String email) {
    return ResponseEntity.ok(houseQueryService.getHouseForCaller(id, email));
}
```

`email` e `String` fără adnotare → Spring îl rezolvă ca **request param opțional** (`?email=`).
`getHouseForCaller` decide accesul pe baza acelui email:

```java
User caller = userRepository.findByEmail(email)...;   // identitate de la client
if (caller.getUserType() != ADMIN && !h.getOwner().getId().equals(caller.getId()))
    throw new HouseAccessDeniedHandler();
```

Deci **orice user autentificat citește orice casă**: `?email=<mailul owner-ului>` (check trece) sau
`?email=<mail de admin>` (bypass total). `@PreAuthorize` verifică *că ai permisiunea*, nu *a cui*
casă o vezi — ownership-ul trebuie legat de principal. Fără `?email` → `findByEmail(null)` →
`IllegalArgumentException` → 500.

**Fix:** ia identitatea din token.
```java
public ResponseEntity<HouseResponse> getHouseById(@PathVariable Long id, Principal principal) {
    return ResponseEntity.ok(houseQueryService.getHouseForCaller(id, principal.getName()));
}
```

### 2. `UserController` — dependență neinjectată + creare user nesecurizată

```java
@RestController @RequestMapping("/api/v1/user")
public class UserController {
    private UserCommandService userCommandService;   // niciodată injectat (fără constructor / @Autowired)

    @PostMapping("/add")
    public ResponseEntity<UserResponse> addUser(@RequestBody UserRequest userRequest){
        UserResponse userResponse = userCommandService.addUser(userRequest);   // NPE: null
        return ResponseEntity.status(HttpStatus.CREATED).build();              // și aruncă rezultatul
    }
}
```

- `userCommandService` e `null` → **NPE / 500** la orice apel.
- Chiar reparat, `addUser` creează user fără parolă și fără `userType` (`UserRequest` nu le are) →
  user invalid; `save` apelat de două ori.
- Fără `@PreAuthorize`. `UserRequest` importă greșit `org.hibernate.usertype.UserType`.

**Fix:** scoate controllerul (crearea de user se face deja prin `/auth/register`) sau injectează prin
constructor + securizează.

### 3. `GlobalExceptionHandler` — status greșit, body ne-JSON, excepții nemapate

```java
public ResponseEntity<String> handleNotFoundExceptions(RuntimeException e) {
    ApiErrorResponse r = ApiErrorResponse.builder()
            .message(e.getMessage()).status(HttpStatus.CONFLICT.value()).build();
    return new ResponseEntity<>(r.toString(), HttpStatus.CONFLICT);   // 409 pt "not found" + toString()
}
```

- „Not found" → **409** în loc de **404**.
- Trimiți `r.toString()` (nu JSON); `timestamp`/`error`/`path` rămân `null` (builder-ul setează doar 2
  câmpuri). Ai deja `ApiErrorResponse.of(...)` care le populează pe toate — folosește-l.
- **Nemapate → devin 500:** `HouseNotFoundException`, `HouseAlreadyExistsExcption`,
  `HouseAccessDeniedHandler` (ar fi 403), `MethodArgumentNotValidException` (validare → 400/422),
  `IllegalArgumentException` din #1.

**Fix:** handlere separate cu statusul corect (404/409/403/400-422), întoarce obiectul
`ApiErrorResponse` (nu `.toString()`).

### 4. Strategia de schemă contrazice backlog-ul

```yaml
jpa.hibernate.ddl-auto: create      # recreează schema (pierzi datele) la fiecare pornire
flyway.enabled: false               # deși db/migration/ există (gol)
```

Backlog-ul cere **Flyway + `ddl-auto: validate`**. În plus, numele DB nu se potrivește între surse:
- `application.yml` → `solarSyncBroker-api`
- `docker-compose.yml` + README → `solarsync`

→ `docker compose up` + app nu se leagă azi.

**Fix:** `ddl-auto: validate` + `flyway.enabled: true` cu migrări; un singur nume de DB peste tot.

---

## 🟡 Design / consistență

| # | Problemă | Unde |
|---|---|---|
| 5 | `getBatteryByHouseId`: doar `ADMIN` poate citi (owner-ul nu-și vede bateria); „not found" aruncă `HouseNotFoundException` (tip greșit); **nu există `BatteryController`** → tot slice-ul `battery` e neatins prin HTTP | `BatteryQueryServiceImpl` |
| 6 | `PUBLIC_URLS` = rute `v2` inexistente + string invalid `" http://localhost:8080/login#/"` (spațiu în față); merge doar pentru că `/api/v1/auth/**` e permis separat → `publicAwareBearerTokenResolver` e cod mort | `SecurityConstants` |
| 7 | `ISSUER`/`AUDIENCE` = `controllerPractice-*` (proiect vechi); oricum `NimbusJwtDecoder` nu validează issuer/audience aici | `SecurityConstants` |
| 8 | `JWTTokenProvider`: `getAuthorities`/`getAuthentication`/`isTokenValid`/`getSubject`/`getClaimsFromToken` = cod mort (validarea o face resource server-ul) | `JWTTokenProvider` |
| 9 | `permissionsForType`: `OPERATOR` și `AGENT` cad pe ramura `else` → primesc exact ce primește `USER` | `AuthServiceImpl` |
| 10 | Mapper-e `@Component` cu metode `static` → adnotarea nu face nimic | `HouseMapper`, `BatteryMapper`, `UserMapper` |
| 11 | Stereotipuri inconsistente: `HouseCommandServiceImpl`=`@Service`, `UserCommandServiceImpl`=`@Component`, query=`@Component` | services |
| 12 | `@EnableJpaAuditing` + `AbstractAuditable` există, dar niciun entity nu-l extinde → auditing nefolosit | `system/model` |
| 13 | `updateHouse` nu verifică unicitatea numelui (doar `createHouse` o face) → redenumire în coliziune | `HouseCommandServiceImpl` |
| 14 | `User`/`Battery`: `equals`/`hashCode` includ toate câmpurile (inclusiv `password`, colecția `permissions`) — anti-pattern JPA; recomandat doar `id` (ca la `House`, deja reparat) | `User`, `Battery` |
| 15 | `AuthLoginResponse` definit dar nefolosit; `HouseQueryService.getHouseById(Long)` = metodă moartă | dtos / query |

---

## ⚪ Cosmetice

Typos în nume de clase / câmpuri / tabele — un examinator le observă:

`AuthReqisterRequest`, `AuthLoginrequest`, `HouseAlreadyExistsExcption`, `UserAlreadyexistsException`,
`JWTAuthentificationEntryPoint`, `efficientyPercent`, `@Table("bateries")`.

`HouseAccessDeniedHandler` e de fapt un `RuntimeException`, nu un handler — numele induce în eroare.
`UserResponse.now` e un nume ciudat de câmp. `@NoArgsConstructor` + `@NotBlank` nefolosit pe
`SecurityConstants`.

---

## Puncte forte

- Vertical-slice curat, command/query separate; DTO-uri ca `record`; validare pe input.
- JWT stateless + `@EnableMethodSecurity` cu `hasAuthority`, fără prefix `ROLE_`.
- 401/403 corecte, JSON prin `ApiErrorResponse.of(...)` — modelul de urmat și în `GlobalExceptionHandler`.
- `open-in-view: false`; secret prin env var.

---

## Ordine recomandată de reparare

1. **#1 + #3** — securitatea pe owner + erorile HTTP corecte (cel mai vizibil la demo).
2. **#2** — scoate/repară `UserController` (gaură + NPE).
3. **#4** — Flyway + `validate` + un singur nume de DB, înainte de date reale.
4. **#5 + `BatteryController`** — completează slice-ul `battery`.
5. Restul 🟡 / ⚪ ca pas de curățenie.

> Un singur IT „user străin → 403" pe `/houses/{id}` ar fi prins #1 (gaura principală).
