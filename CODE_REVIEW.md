# Code Review — solar-sync-broker-api

Branch: `feat/fundatie` · Vertical-slice Spring Boot 3.5 (~1700 LOC)
Slices: `auth`, `house`, `battery`, `users`, `system`
Teste: **32 verzi** (unit + IT). Problemele de mai jos nu sunt prinse de teste.

## Legendă severitate

- 🔴 Corectitudine / securitate — de reparat
- 🟡 Design / consistență
- ⚪ Cosmetic (dar vizibil la evaluarea de licență)

---

## 🔴 Trebuie reparate

### 1. Autorizare bazată pe input de la client — `HouseController.getHouseById`

```java
@GetMapping("/{id}")
@PreAuthorize("hasAuthority('house:view')")
public ResponseEntity<HouseResponse> getHouseById(@PathVariable Long id, String email) {
    return ResponseEntity.ok(houseQueryService.getHouseForCaller(id, email));
}
```

`email` nu e legat de nimic (fără `@RequestParam` / `@AuthenticationPrincipal`), deci
Spring îl tratează ca query param. `getHouseForCaller` decide accesul owner/admin pe baza
acestui email — adică apelantul își trimite singur identitatea pe care se face verificarea.

Un `USER` cu `house:view` poate citi casa altcuiva punând alt email în query.
`@PreAuthorize` verifică doar că *ai permisiunea*, nu *a cui* casă o vezi — verificarea de
ownership trebuie legată de principal, nu de un parametru.

**Fix:** ia identitatea din token.
```java
public ResponseEntity<HouseResponse> getHouseById(@PathVariable Long id, Principal principal) {
    return ResponseEntity.ok(houseQueryService.getHouseForCaller(id, principal.getName()));
}
```

### 2. `BatteryQueryServiceImpl.getBatteryByHouseId` — logică inversată + slice mort

```java
if (user.getUserType() != UserType.ADMIN) {
    throw new HouseAccessDeniedHandler();
}
```

- Doar adminul poate citi vreodată o baterie — proprietarul casei nu-și poate vedea propria
  baterie.
- Nu există `BatteryController` în proiect → tot slice-ul `battery` e neatins prin HTTP
  (incomplet).
- „Not found" pentru baterie aruncă `HouseNotFoundException` (tip greșit).

**Fix:** aliniază regula la cea din `getHouseForCaller` (admin SAU owner) și adaugă
controllerul + o `BatteryNotFoundException` corectă.

### 3. `GlobalExceptionHandler` — status greșit + excepții nemapate

- `handleNotFoundExceptions` întoarce **409 CONFLICT** pentru „not found" — ar trebui **404**.
- Nu prinde deloc: `HouseNotFoundException`, `HouseAlreadyExistsExcption`,
  `HouseAccessDeniedHandler`, `MethodArgumentNotValidException` (validare).
  Toate devin **500** în loc de 404 / 403 / 400.
- Body-ul e `apiErrorResponse.toString()` — trimiți `toString()`-ul unui record, nu JSON;
  `timestamp` / `error` / `path` rămân `null` (builder-ul setează doar `message` + `status`).

**Fix:** handlere separate cu statusul corect (404/409/403/400), întoarce obiectul
`ApiErrorResponse` (nu `.toString()`), populează toate câmpurile.

### 4. `ddl-auto: create` în `application.yml`

Recreează schema (pierzi datele) la fiecare pornire. Există folder `db/migration` dar
`flyway.enabled: false`.

**Fix:** `ddl-auto: validate` (sau `update`) + `flyway.enabled: true` cu migrări.

---

## 🟡 Design / consistență

| # | Problemă | Unde |
|---|---|---|
| 5 | `PUBLIC_URLS` conține rute `v2` inexistente (controllerele sunt `v1`) + string invalid `" http://localhost:8080/login#/"` (spațiu în față) → `publicAwareBearerTokenResolver` devine cod mort | `SecurityConstants` |
| 6 | `ISSUER` / `AUDIENCE` = `controllerPractice-*` — rămas dintr-un proiect vechi | `SecurityConstants` |
| 7 | `JWTTokenProvider`: `getAuthorities` / `getAuthentication` / `isTokenValid` / `getSubject` sunt cod mort — validarea o face resource server-ul (`NimbusJwtDecoder`) | `JWTTokenProvider` |
| 8 | Mapper-e `@Component` cu metode `static` apelate static → adnotarea e inutilă | `HouseMapper`, `BatteryMapper` |
| 9 | Inconsistență stereotip: command = `@Service`, query = `@Component` | `*QueryServiceImpl` |
| 10 | `AbstractAuditable` definit dar niciun entity nu-l extinde → auditing nefolosit | `system/model` |
| 11 | `updateHouse` nu verifică unicitatea numelui (doar `createHouse` o face) → redenumire în coliziune | `HouseCommandServiceImpl` |
| 12 | `equals` / `hashCode` pe entități includ toate câmpurile (inclusiv `password`, colecții mutabile) — anti-pattern JPA; recomandat doar `id` | `User`, `Battery` |

---

## ⚪ Cosmetice

Typos în nume de clase / câmpuri / tabele — un examinator le observă:

`AuthReqisterRequest`, `AuthLoginrequest`, `HouseAlreadyExistsExcption`,
`UserAlreadyexistsException`, `JWTAuthentificationEntryPoint`, `efficientyPercent`,
`@Table("bateries")`.

`HouseAccessDeniedHandler` e de fapt o `RuntimeException`, nu un handler — numele induce în
eroare.

---

## Puncte forte

- Structură vertical-slice curată și consecventă; separare command / query.
- JWT stateless + method security cu `hasAuthority`; fără prefix `ROLE_`.
- DTO-uri ca `record`; validare pe entități.
- `open-in-view: false`; secret prin env var; `createDatabaseIfNotExist` doar pentru dev.

---

## Ordine recomandată de reparare

1. #1 și #3 (securitate + erori HTTP corecte) — cele mai vizibile la o demonstrație.
2. #4 (ddl-auto) înainte de a avea date reale.
3. #2 + `BatteryController` pentru a completa slice-ul `battery`.
4. Restul 🟡 / ⚪ ca pas de curățenie.
