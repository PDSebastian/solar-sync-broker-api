# Backlog — API SolarSyncBroker

Implementarea REST API a backend-ului `solar-sync-broker-api`, în ordinea din `../../PLAN_PROIECT.md` (secțiunea 18). Recapitulare Spring pe parcurs: `../../RECAP_SPRING.md`.

**Convenție:**
- fiecare **Story = un commit separat**; task-urile = pași în ordine. Bifează cu `[x]` ce ai terminat.
- structură **vertical-slice** pe domeniu: `controller / dtos / exceptions / mapper / model / repository / service/{commandService,queryService}` (folderele există deja, goale).
- entitățile NU se expun direct — request/response prin DTO-uri.
- erori în engleză; fără comentarii în cod.
- DB: **MySQL** (schema doar prin Flyway, `ddl-auto: validate`); bani cu `BigDecimal`.
- la finalul fiecărui Story: `./mvnw clean compile` verde + commit.

**Roluri** (vezi PLAN 6.1): `ADMIN` (toată comunitatea + control), `OPERATOR` (analiză + aprobare acțiuni), `USER` (doar casa proprie). `AGENT` = identitate internă, fără HTTP.

---

## Harta EPIC-urilor (ordinea de implementare)

| EPIC | Temă | Depinde de | Etapă PLAN |
|------|------|-----------|------------|
| A | Convenții, erori, OpenAPI | — | 1 |
| B | Autentificare & Utilizatori | A | 2 |
| C | Case & Baterii (CRUD) | B | 2 |
| D | Lifecycle simulare + telemetrie | C | 3 |
| E | Energie, baterii (dispatch), piață | D | 4 |
| F | Protecția transformatorului (grid) | E | 5 |
| G | Contabilitate financiară | E | 4 |
| H | Agent: status & runs (ADVISORY) | E, F | 6 |
| I | Planuri: simulare, validare, aprobare, execuție | H | 6-7 |
| J | Audit & rapoarte | toate | 10 |
| K | WebSocket (dashboard) | D, F, H | 9 |

Oprește-te la EPIC I în mod **ADVISORY** pentru licență (vezi RECAP, Modul 9). Execuția reală (SUPERVISED/AUTONOMOUS) e „dezvoltare viitoare".

---

# EPIC A — Convenții, erori, OpenAPI

**Goal:** fundația peste care se construiește orice endpoint — format de eroare unitar, OpenAPI, auditare automată a entităților, parametrii fizici externalizați.

## Story A1 — Problem Details + handler global

**Acceptance:** orice eroare iese în format `application/problem+json` cu `errorCode`, status HTTP corect.

- [ ] **A1.1** În `system/exceptions/` creează `ApiException` (abstractă, `extends RuntimeException`) cu câmpuri `HttpStatus status`, `String errorCode`.
- [ ] **A1.2** `system/exceptions/GlobalExceptionHandler.java` cu `@RestControllerAdvice`:
  - handler pentru `ApiException` → construiește `ProblemDetail` (`ProblemDetail.forStatusAndDetail(...)`) + proprietatea `errorCode`.
  - handler pentru `MethodArgumentNotValidException` → 422 `VALIDATION_ERROR`, cu lista de câmpuri invalide (altfel primești 500).
  - handler fallback `Exception` → 500 `INTERNAL_ERROR` (fără stack trace în body).
- [ ] **A1.3** Verify: un controller dummy care aruncă o `ApiException` întoarce body conform exemplului din PLAN secțiunea 10.

## Story A2 — OpenAPI / Swagger

**Acceptance:** Swagger UI accesibil, cu titlu și schema de securitate Bearer.

- [ ] **A2.1** `system/config/OpenApiConfig.java` cu `@Bean OpenAPI` — titlu „SolarSyncBroker API", versiune, security scheme `bearerAuth` (HTTP, JWT).
- [ ] **A2.2** Verify: `http://localhost:8080/swagger-ui.html` se încarcă (springdoc deja în pom).

## Story A3 — Auditing & ConfigurationProperties

**Acceptance:** entitățile au `createdAt` automat; parametrii fizici sunt tipizați, nu împrăștiați.

- [ ] **A3.1** `@EnableJpaAuditing` (în config) + o clasă bază `AbstractAuditable` (`@MappedSuperclass`) cu `@CreatedDate Instant createdAt`. Domeniile o pot extinde.
- [ ] **A3.2** `system/config/SimulationProperties.java` cu `@ConfigurationProperties(prefix="solarsync.simulation")` — `seed`, `tickSeconds`, `realToVirtualRatio`.
- [ ] **A3.3** `system/config/GridProperties.java` — `transformerNominalKw`, `safetyMarginPercent`.
- [ ] **A3.4** `system/config/AgentProperties.java` — `mode` (enum DISABLED/ADVISORY/SUPERVISED/AUTONOMOUS), `planningIntervalSeconds`. Default `ADVISORY`.
- [ ] **A3.5** Adaugă cheile în `application.yml` cu valori implicite. NU pune secrete.

## Story A4 — Clock injectabil

**Acceptance:** nicăieri `Instant.now()` direct; timpul vine dintr-un bean.

- [ ] **A4.1** `system/config/ClockConfig.java` cu `@Bean Clock` (real). În teste injectezi un `Clock.fixed(...)`.

---

# EPIC B — Autentificare & Utilizatori

**Goal:** JWT login/register, roluri, baza pentru „un user vede doar casa lui".

## Story B1 — Entități User & Role

- [ ] **B1.1** Migrație Flyway `V1__users.sql`: tabele `users` (id, email unique, password_hash, enabled, created_at) și `user_roles` (user_id, role). (PLAN 9.1)
- [ ] **B1.2** `users/model/User.java` (`@Entity @Table(name="users")`), `users/model/Role.java` (enum ADMIN/OPERATOR/USER/AGENT).
- [ ] **B1.3** `users/repository/UserRepository.java`: `Optional<User> findByEmail(String)`, `boolean existsByEmail(String)`.
- [ ] **B1.4** Verify: `./mvnw flyway:migrate` (sau boot) creează tabelele; `ddl-auto: validate` trece.

## Story B2 — Security config + JWT

- [ ] **B2.1** `users/jwt/JwtService.java` — emitere (`generateToken(User)`) și parsare cu jjwt; cheia din `${JWT_SECRET_KEY}` (env, fallback doar dev).
- [ ] **B2.2** `users/jwt/JwtAuthFilter.java` (`OncePerRequestFilter`) — extrage Bearer, validează, setează `SecurityContext`.
- [ ] **B2.3** `system/config/SecurityConfiguration.java`: `SecurityFilterChain` stateless, `@EnableMethodSecurity`, `BCryptPasswordEncoder`, filtrul JWT înainte de `UsernamePasswordAuthenticationFilter`. CSRF off (conștient, API stateless).
- [ ] **B2.4** Reguli `requestMatchers`: `/api/v1/auth/**`, `/swagger-ui/**`, `/v3/api-docs/**`, `/actuator/health` → permitAll; restul → authenticated.

## Story B3 — Auth endpoints

**Acceptance:** register + login funcționează, întorc JWT.

- [ ] **B3.1** DTO-uri în `auth/dtos/`: `RegisterRequest` (email, password, opțional role), `LoginRequest`, `AuthResponse` (accessToken, expiresAt).
- [ ] **B3.2** `auth/service/...` — `register` (hash parolă, save, rol implicit USER), `login` (autentifică, emite token).
- [ ] **B3.3** `auth/controller/AuthController.java`:

  | Method | Path | Acces | Notă |
  |---|---|---|---|
  | POST | `/api/v1/auth/register` | public | 201 |
  | POST | `/api/v1/auth/login` | public | 200 + token |
  | GET | `/api/v1/auth/me` | authenticated | userul curent |
  | POST | `/api/v1/auth/logout` | authenticated | (stateless: client drop token) |

- [ ] **B3.4** `auth/exceptions/`: `EmailAlreadyUsedException` (409), `InvalidCredentialsException` (401). Înregistrează-le în handler-ul global.
- [ ] **B3.5** Verify (curl/Postman): register → login → `GET /me` cu Bearer întoarce 200; fără token → 401.

> `refresh` token: opțional în MVP (PLAN 19). Sari peste dacă nu îți trebuie.

---

# EPIC C — Case & Baterii

**Goal:** CRUD case + baterii, cu regula de izolare „USER vede doar casa lui".

## Story C1 — Entitatea House

- [ ] **C1.1** `V2__houses.sql`: `houses` (id, name, owner_id FK users, enabled, pv_peak_power_kw, max_import_power_kw, max_export_power_kw, created_at). (PLAN 9 House)
- [ ] **C1.2** `house/model/House.java` + `house/repository/HouseRepository.java` (`findByOwnerId`, `findByIdAndOwnerId`).
- [ ] **C1.3** DTO-uri `house/dtos/`: `HouseRequest`, `HousePatchRequest`, `HouseResponse`, `HouseStateResponse`. Validare `@Positive` pe puteri.
- [ ] **C1.4** `house/mapper/HouseMapper.java` (entity → response).

## Story C2 — House services + controller

**Acceptance:** ADMIN vede toate casele; USER doar pe a lui (403 altfel), verificat **în service**.

- [ ] **C2.1** `house/service/queryService/` — `getAllHouses()` (ADMIN), `getHouseForCaller(id, caller)` (verifică owner).
- [ ] **C2.2** `house/service/commandService/` — `createHouse`, `updateHouse` (PUT), `patchHouse` (PATCH), `deleteHouse`.
- [ ] **C2.3** `house/controller/HouseController.java`:

  | Method | Path | Permission |
  |---|---|---|
  | GET | `/api/v1/houses` | `hasRole('ADMIN')` |
  | POST | `/api/v1/houses` | `hasRole('ADMIN')` |
  | GET | `/api/v1/houses/{id}` | ADMIN sau owner (check în service) |
  | PATCH | `/api/v1/houses/{id}` | `hasRole('ADMIN')` |
  | GET | `/api/v1/houses/{id}/state` | ADMIN sau owner |

- [ ] **C2.4** `house/exceptions/`: `HouseNotFoundException` (404), `HouseAccessDeniedException` (403).
- [ ] **C2.5** Verify: 2 useri — owner primește 200 pe casa lui, alt USER primește 403 pe `/houses/{altId}`.

## Story C3 — Battery

- [ ] **C3.1** `V3__batteries.sql`: `batteries` (id, house_id unique FK, capacity_kwh, soc_percent, min_soc_percent, max_soc_percent, max_charge_power_kw, max_discharge_power_kw, charge_efficiency, discharge_efficiency, status, **version**). (PLAN 9 Battery)
- [ ] **C3.2** `battery/model/Battery.java` cu `@Version` (optimistic locking — race tick vs comandă). `battery/repository/BatteryRepository.java` (`findByHouseId`).
- [ ] **C3.3** DTO-uri `battery/dtos/`: `BatteryResponse`, `BatteryConfigurationRequest` (PATCH config).
- [ ] **C3.4** `battery/controller/BatteryController.java`:

  | Method | Path | Permission |
  |---|---|---|
  | GET | `/api/v1/houses/{houseId}/battery` | ADMIN sau owner |
  | PATCH | `/api/v1/houses/{houseId}/battery/configuration` | `hasRole('ADMIN')` |

- [ ] **C3.5** Verify: GET battery întoarce SoC + limite; PATCH config schimbă `maxChargePowerKw`.

> Endpoint-ul de **comenzi** baterie (`POST .../battery/commands`) vine în EPIC E, după ce există validatorul fizic.

---

# EPIC D — Lifecycle simulare + telemetrie

**Goal:** ceas virtual + tick central reproductibil care generează PV/consum și persistă telemetrie. (PLAN 6.3)

## Story D1 — Telemetry entity & queries

- [ ] **D1.1** `V4__telemetry.sql`: `telemetry` (id, house_id, recorded_at, simulation_time, pv_power_kw, load_power_kw, battery_power_kw, net_power_kw, soc_percent, market_price) + **index `(house_id, recorded_at)`**. (PLAN 9 Telemetry)
- [ ] **D1.2** `telemetry/model` + `telemetry/repository` (`findByHouseIdOrderByRecordedAtDesc`, paginat).
- [ ] **D1.3** DTO `telemetry/dtos/TelemetryResponse` + `house/dtos/HouseTelemetryPageResponse` (paginare).

## Story D2 — Generatoare PV & consum

**Acceptance:** funcții deterministe (seed) — același seed ⇒ aceleași valori.

- [ ] **D2.1** `simulation/service/` — `PvGenerator` și `LoadGenerator`, semnătură `double powerKw(House house, SimulationTime t, long seed)`.
- [ ] **D2.2** Teste unitare: aceeași intrare + seed ⇒ output identic (reproductibilitate).

## Story D3 — Tick central + lifecycle

**Acceptance:** un singur tick actualizează toate casele; start/pause/resume/stop.

- [ ] **D3.1** `simulation/service/SimulationClock.java` — timp virtual avansat cu `tickSeconds`.
- [ ] **D3.2** `simulation/service/SimulationEngine.java` — `tick()`: pentru fiecare casă generează PV+consum, calculează `netPower` (provizoriu, fără baterie încă), persistă telemetrie. **Un singur tick central**, nu 10 fire.
- [ ] **D3.3** Status machine: `STOPPED → RUNNING → PAUSED`. `simulation/controller/SimulationController.java`:

  | Method | Path | Permission |
  |---|---|---|
  | GET | `/api/v1/simulation/status` | authenticated |
  | POST | `/api/v1/simulation/start` `/pause` `/resume` `/stop` `/reset` | `hasRole('ADMIN')` |
  | PATCH | `/api/v1/simulation/configuration` | `hasRole('ADMIN')` |

- [ ] **D3.4** Telemetrie + stare:

  | Method | Path | Permission |
  |---|---|---|
  | GET | `/api/v1/houses/{id}/telemetry` | ADMIN sau owner |
  | GET | `/api/v1/community/telemetry` | `hasRole('ADMIN')` |
  | GET | `/api/v1/community/state` | `hasRole('ADMIN')` |

- [ ] **D3.5** Verify: `start` → după câteva tick-uri, `GET /houses/{id}/telemetry` întoarce rânduri; `reset` golește/reia cu același seed.

---

# EPIC E — Energie, baterii (dispatch) & piață

**Goal:** bilanțul energetic, actualizarea SoC cu limite fizice, simulatorul de preț, comenzile manuale de baterie validate. **Inima deterministă** (PLAN 7).

## Story E1 — Energy engine

**Acceptance:** `Pnet = Ppv - Pload + Pbatt` cu convenția de semn din PLAN 7.1; bilanțul se închide în toleranță.

- [ ] **E1.1** `energy/service/EnergyEngine.java` — `EnergyResult compute(houseState, batteryCommand, deltaHours)`: import/export, energie pe tick.
- [ ] **E1.2** Teste unitare (PLAN 15.1): bilanț energetic, conversie putere→energie, semn corect export/import.

## Story E2 — Battery SoC update + constrângeri

**Acceptance:** formulele de SoC (PLAN 7.2) pe ambele ramuri; SoC niciodată în afara `[minSoC, maxSoC]`.

- [ ] **E2.1** `battery/service/BatteryService.java` — `applyCommand`: încărcare/descărcare cu randament, limitare putere, clamp SoC.
- [ ] **E2.2** Teste unitare: charge, discharge, comandă peste `maxChargePowerKw` respinsă, descărcare care ar coborî sub `minSoC` respinsă, randamente. (PLAN 15.1)

## Story E3 — Validator de comandă (determinist)

**Acceptance:** orice comandă (manuală sau de agent) trece prin ACELAȘI validator înainte de aplicare.

- [ ] **E3.1** `battery/service/BatteryCommandValidator.java` — verifică putere ≤ limite, fezabilitate SoC pe durata tick-ului, returnează motiv structurat la respingere.
- [ ] **E3.2** `battery/exceptions/BatterySocLimitException`, `BatteryPowerLimitException` (422, `errorCode` ca în PLAN 10).

## Story E4 — Comenzi manuale de baterie

- [ ] **E4.1** DTO `BatteryCommandRequest` (action CHARGE/DISCHARGE/IDLE, powerKw, durationSeconds), `CommandExecutionResponse`.
- [ ] **E4.2** `V5__command_execution.sql` + `agent/model/CommandExecution.java` (PLAN 9) — reutilizat și de agent.
- [ ] **E4.3** Endpoint:

  | Method | Path | Permission |
  |---|---|---|
  | GET | `/api/v1/houses/{houseId}/battery/commands` | ADMIN/OPERATOR |
  | POST | `/api/v1/houses/{houseId}/battery/commands` | `hasAnyRole('ADMIN','OPERATOR')` |

  Comanda trece prin `BatteryCommandValidator` → aplicată → persistată ca `CommandExecution`.
- [ ] **E4.4** Verify: comandă validă → 201 + SoC actualizat; comandă peste limită → 422 cu `errorCode`.

## Story E5 — Market simulator

**Acceptance:** preț pe intervale virtuale, cu istoric. (PLAN 6.4)

- [ ] **E5.1** `V6__market_price.sql` + `market/model/MarketPrice.java` (id, valid_from, valid_until, price_per_kwh, source, scenario).
- [ ] **E5.2** `market/service/MarketSimulator.java` — preț pentru intervalul curent; scenarii (preț mic/negativ/ridicat).
- [ ] **E5.3** Endpoint:

  | Method | Path | Permission |
  |---|---|---|
  | GET | `/api/v1/market/current-price` | authenticated |
  | GET | `/api/v1/market/prices` | authenticated (paginat) |
  | POST | `/api/v1/market/scenarios` | `hasRole('ADMIN')` |

- [ ] **E5.4** Integrează prețul în tick (telemetria primește `market_price`).

---

# EPIC F — Protecția transformatorului (grid)

**Goal:** agregă fluxurile, detectează suprasarcina, aplică curtailment determinist. **Prioritate față de agent**, nu poate fi dezactivat de el. (PLAN 6.7)

## Story F1 — Grid snapshot & detecție

- [ ] **F1.1** `V7__grid.sql`: `grid_snapshot` (recorded_at, total_import_kw, total_export_kw, transformer_load_kw, operational_limit_kw, status) + `grid_alert` (type, severity, message, created_at, resolved_at, related_house_id, related_plan_id). (PLAN 9)
- [ ] **F1.2** `grid/service/GridProtectionService.java` — `operationalLimit = nominal * (1 - safetyMargin)` din `GridProperties`; detectează depășire la import și export.

## Story F2 — Curtailment determinist

**Acceptance:** scenariul din PLAN 15.4 — 60 kW export cerut, limită 45 kW ⇒ rezultat ≤ 45.

- [ ] **F2.1** Strategie de reducere **proporțională** a comenzilor caselor care contribuie la depășire.
- [ ] **F2.2** Test unitar dedicat (curtailment 60→45). Alertă creată + auditată.
- [ ] **F2.3** Integrează în tick: după calculul comenzilor, grid protection rulează ÎNAINTE de aplicare.

## Story F3 — Grid endpoints

- [ ] **F3.1**:

  | Method | Path | Permission |
  |---|---|---|
  | GET | `/api/v1/grid/state` | authenticated |
  | GET | `/api/v1/grid/alerts` | ADMIN/OPERATOR |
  | GET | `/api/v1/grid/alerts/{id}` | ADMIN/OPERATOR |

---

# EPIC G — Contabilitate financiară

**Goal:** cost/venit/profit per casă și comunitate. (PLAN 6.8)

## Story G1 — FinancialTransaction

- [ ] **G1.1** `V8__financial.sql` + `financial/model/FinancialTransaction.java` (house_id, recorded_at, type BUY/SELL, energy_kwh, unit_price, amount — `BigDecimal`).
- [ ] **G1.2** `financial/service/` — la fiecare tick: import ⇒ BUY, export ⇒ SELL, cu prețul curent.

## Story G2 — Summaries

- [ ] **G2.1**:

  | Method | Path | Permission |
  |---|---|---|
  | GET | `/api/v1/houses/{id}/financial-summary` | ADMIN sau owner |
  | GET | `/api/v1/community/financial-summary` | `hasRole('ADMIN')` |

- [ ] **G2.2** Test: scenariul „preț ridicat seara" (PLAN 15.4) — profit calculat corect.

---

# EPIC H — Agent: status & runs (ADVISORY)

**Goal:** scheletul agentului autonom + auditul, **fără** apel LLM încă (mock). (PLAN 8, RECAP Modul 8-9)

## Story H1 — Entități audit agent

- [ ] **H1.1** `V9__agent.sql`: `agent_run` (trigger_type, mode, provider, model, status, started_at, finished_at, failure_reason, token_usage, estimated_cost), `agent_plan` (agent_run_id, status, objective, plan_json, expected_profit, expected_transformer_peak_kw, confidence, approved_by, approved_at), `agent_tool_call` (agent_run_id, tool_name, arguments_json, result_json, status, started_at, finished_at). (PLAN 9)
- [ ] **H1.2** `agent/model/` + `agent/repository/` pentru cele 3 entități.

## Story H2 — Model gateway (abstracție LLM)

**Acceptance:** tot codul Spring AI izolat într-un singur loc; în teste un gateway fals, fără rețea.

- [ ] **H2.1** `agent/service/AgentModelGateway.java` (interfață) — `DispatchPlan proposePlan(AgentContext ctx)`.
- [ ] **H2.2** Implementare `mock` (returnează un plan determinist) — folosită până la Modul 8 și în teste. **Adaugă dependența Spring AI reală abia când integrezi providerul** (vezi RECAP Modul 8: BOM e deja pin-uit).
- [ ] **H2.3** Provider real (OpenAI) — `AI_PROVIDER`, chei din env. (poate fi un Story separat la Modul 8)

## Story H3 — Agent status & runs endpoints

- [ ] **H3.1**:

  | Method | Path | Permission |
  |---|---|---|
  | GET | `/api/v1/agent/status` | ADMIN/OPERATOR |
  | GET | `/api/v1/agent/runs` | ADMIN/OPERATOR |
  | GET | `/api/v1/agent/runs/{id}` | ADMIN/OPERATOR |
  | PATCH | `/api/v1/agent/configuration` | `hasRole('ADMIN')` (mode, interval) |
  | POST | `/api/v1/agent/run` | ADMIN/OPERATOR (trigger manual) |

- [ ] **H3.2** Un `agent/run` pornit creează un `AgentRun` cu status, persistă fiecare `AgentToolCall`.

---

# EPIC I — Planuri: simulare, validare, aprobare, execuție

**Goal:** ciclul `OBSERVE → PLAN → SIMULATE → VALIDATE` (ACT abia la SUPERVISED). (PLAN 8.3)

## Story I1 — DispatchPlan + structured output

- [ ] **I1.1** `agent/dtos/DispatchPlan` + `BatteryCommand` exact ca PLAN 8.9 (planId, validFrom/Until, objective, commands[], expected*, assumptions[], confidence). Bean Validation pe câmpuri.
- [ ] **I1.2** Validare: răspuns invalid de la model ⇒ plan **respins**, nu excepție necontrolată (PLAN 8.10).

## Story I2 — Simulate & validate (dry-run)

**Acceptance:** planul e simulat fără efecte și validat determinist înainte de a fi marcat acceptabil.

- [ ] **I2.1** `agent/service/` — `simulateDispatchPlan(plan)` (rulează prin EnergyEngine + GridProtection, fără persistare reală).
- [ ] **I2.2** `validateDispatchPlan(plan)` — fiecare comandă prin `BatteryCommandValidator` + check transformator. Plan malițios (peste limite) ⇒ respins + auditat (PLAN 15.4).

## Story I3 — Plans endpoints (aprobare/execuție)

- [ ] **I3.1**:

  | Method | Path | Permission |
  |---|---|---|
  | GET | `/api/v1/agent/plans` `/{id}` `/{id}/tool-calls` | ADMIN/OPERATOR |
  | POST | `/api/v1/agent/plans/{id}/approve` `/reject` | `hasRole('OPERATOR')` |
  | POST | `/api/v1/agent/plans/{id}/execute` | `hasRole('OPERATOR')` (SUPERVISED) |
  | POST | `/api/v1/agent/emergency-stop` | `hasAnyRole('ADMIN','OPERATOR')` |

- [ ] **I3.2** În ADVISORY: `execute` întoarce 409/`AGENT_MODE_ADVISORY` (nu execută). Comutarea pe SUPERVISED activează execuția prin același validator + `CommandExecution`.
- [ ] **I3.3** Verify (scenariile PLAN 15.4): preț mic la prânz ⇒ plan de încărcare valid; plan invalid ⇒ respins; provider indisponibil ⇒ simularea continuă, niciun plan nou (degradare sigură).

---

# EPIC J — Audit & rapoarte

- [ ] **J1.1** `audit/` (sub `system` sau domeniu propriu) — endpoint citire evenimente:

  | Method | Path | Permission |
  |---|---|---|
  | GET | `/api/v1/audit/events` | `hasRole('ADMIN')` |
  | GET | `/api/v1/reports/energy` | ADMIN/OPERATOR |
  | GET | `/api/v1/reports/financial` | ADMIN/OPERATOR |
  | GET | `/api/v1/reports/agent-performance` | ADMIN/OPERATOR |

- [ ] **J1.2** Rapoartele agregă pe intervale (import/export, profit, planuri acceptate/respinse, token usage).

---

# EPIC K — WebSocket (dashboard)

**Goal:** evenimente live pentru dashboard. **Niciodată comenzi critice** pe WebSocket. (PLAN 12)

- [ ] **K1.1** `system/config/WebSocketConfig.java` — STOMP, endpoint `/ws`, broker `/topic`.
- [ ] **K1.2** Publică din tick / din servicii (via `SimpMessagingTemplate`) — DTO-uri, nu entități:
  `/topic/community/state`, `/topic/houses/{id}/state`, `/topic/grid/alerts`, `/topic/market/price`, `/topic/agent/status`, `/topic/agent/plans`, `/topic/agent/executions`.
- [ ] **K1.3** Verify: un client STOMP primește `CommunityStateUpdated` la fiecare tick. Oprește WebSocket ⇒ protecția și comenzile rulează în continuare.

---

## Definition of Done (pe FIECARE Story)

- [ ] `./mvnw clean compile` → BUILD SUCCESS, fără warning-uri noi
- [ ] schema schimbată DOAR prin migrație Flyway nouă (`ddl-auto: validate` trece)
- [ ] DTO request/response (nu entitate expusă), validare pe input
- [ ] `@PreAuthorize` pe FIECARE endpoint care citește/modifică date sensibile
- [ ] erori prin `GlobalExceptionHandler` (Problem Details + `errorCode`), texte în engleză
- [ ] teste: unit pentru logica deterministă (energie/SoC/curtailment/validator), integrare pentru endpoint-uri securizate
- [ ] commit separat: `git commit -m "<EPIC><Story>: <descriere>"`

---

## Ordine recomandată

1. **EPIC A, B, C** — fundație + auth + CRUD case/baterii. Aici intri în ritm, commit-uri scurte.
2. **EPIC D** — simularea (tick reproductibil + telemetrie). Prima parte „vie" a sistemului.
3. **EPIC E, F, G** — inima deterministă (energie, SoC, curtailment, financiar). Aici stau testele unitare care contează la comisie. **Nu trece mai departe fără ele verzi.**
4. **EPIC H, I** — agentul în ADVISORY (mock gateway întâi, provider real la Modul 8). Aici e contribuția nouă a lucrării.
5. **EPIC J, K** — audit/rapoarte + WebSocket pentru dashboard.

**Sfat:** ține mereu sistemul funcțional fără LLM (degradare sigură). Modelul **propune**, codul determinist **dispune** — asta aperi în fața comisiei.
