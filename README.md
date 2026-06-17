# Odoru - Rhythmic Dance Club Platform

Odoru is a distributed platform designed to manage member registrations, course planning, competition scheduling, attendance swiping, and statistics reporting for a rhythmic dance club.

## Modifications apportées depuis ce midi

- **MongoDB par service :** Séparation de la base de données en 4 instances indépendantes (Database-per-Service).
- **Optimisation des microservices :** Passage sous Alpine Linux (images plus légères), limitation de la RAM et démarrages beaucoup plus rapides.
- **Correction de `/api/badges/scan` :** Fix du JWT, de l'envoi des paramètres et des rôles d'accès.
- **Test Bruno 100% auto :** Mise à jour des variables dynamiques et automatisation complète (les 36 tests RBAC passent au vert).
- **Ajout d'outillage Mise :** Nouvelles commandes (`start`, `destroy`, `reset`, `test-e2e`) pour piloter tout le cycle de vie facilement.

---

## 1. System Architecture

The application is structured as a collection of microservices behind a high-performance **KrakenD API Gateway**, authenticated using **Keycloak** (OAuth2/OIDC), and using **MongoDB** for persistence and **RabbitMQ** for messaging.

```mermaid
graph TD
    Client[Client / Bruno Collection] -->|Port 8000| Gateway[KrakenD API Gateway]
    
    subgraph Core [Ecosystem]
        Gateway -->|Route /api/members/**| MS[member-service:8080]
        Gateway -->|Route /api/lessons/**| LS[lesson-service:8081]
        Gateway -->|Route /api/competitions/**| CS[competition-service:8082]
        Gateway -->|Route /api/badges/**| BS[badge-service:8083]
        Gateway -->|Route /api/stats/**| SS[stats-service:8084]
    end
    
    Core -->|JWT JWKS Signature Check| Keycloak[Keycloak:9090]
    
    BS -.->|Async Message| RabbitMQ[RabbitMQ:5672]
    
    LS -.->|REST Client| MS
    CS -.->|REST Client| MS
    BS -.->|REST Client| MS
    BS -.->|REST Client| LS
    
    SS -.->|REST Client| MS
    SS -.->|REST Client| LS
    SS -.->|REST Client| CS
    SS -.->|REST Client| BS
    
    MS --> MDB[(member-mongo:27017)]
    LS --> LDB[(lesson-mongo:27018)]
    CS --> CDB[(competition-mongo:27019)]
    BS --> BDB[(badge-mongo:27020)]
```

---

## 2. Microservices Data Schemas

Each microservice manages its own domain boundary in its dedicated MongoDB instance. The class diagram below illustrates the entity properties and logical relationships:

```mermaid
classDiagram
    class Member {
        +String id
        +String username
        +String email
        +String firstName
        +String lastName
        +MemberRole role
        +int expertiseLevel
        +Address address
        +boolean registrationValidated
        +boolean feePaid
        +boolean medicalCertificateProvided
    }
    class Address {
        +String street
        +String city
        +String postalCode
    }
    class Lesson {
        +String id
        +String title
        +int targetLevel
        +DayOfWeek dayOfWeek
        +String timeSlot
        +int durationMinutes
        +String teacherId
        +String location
        +LocalDateTime dateTime
    }
    class Competition {
        +String id
        +String title
        +int targetLevel
        +LocalDateTime dateTime
        +String location
        +String teacherId
    }
    class CompetitionResult {
        +String id
        +String competitionId
        +String studentId
        +Double score
        +String teacherId
    }
    class BadgeAssociation {
        +String id
        +String memberId
        +String badgeNumber
    }
    class AttendanceLog {
        +String id
        +String memberId
        +String lessonId
        +LocalDateTime timestamp
    }
    
    Member "1" *-- "1" Address : embeds
    Lesson "*" --> "1" Member : taught by (teacherId)
    Competition "*" --> "1" Member : scheduled by (teacherId)
    CompetitionResult "*" --> "1" Competition : scores (competitionId)
    CompetitionResult "*" --> "1" Member : student (studentId)
    CompetitionResult "*" --> "1" Member : graded by (teacherId)
    BadgeAssociation "1" --> "1" Member : associates (memberId)
    AttendanceLog "*" --> "1" Member : attendee (memberId)
    AttendanceLog "*" --> "1" Lesson : course (lessonId)
```

---

## 3. How to Run the Platform

### Using Mise (Recommended)
This project uses [Mise](https://mise.jdx.dev/) for task orchestration and tool management.

**Installation:**
- Windows: `winget install jdx.mise`
- macOS/Linux: `curl https://mise.run | sh`

| Command | Description |
|---|---|
| `mise run up` | **Build** and start the entire Docker Compose stack. |
| `mise run start` | Start the stack **without** rebuilding images (`docker compose up -d`). |
| `mise run down` | Stop the Docker Compose stack. |
| `mise run destroy` | ⚠️ Stop and **completely wipe** containers, databases (volumes), and local images. |
| `mise run reset` | ⚠️ Destroy everything and recreate the environment from scratch (clean slate). |
| `mise run test-e2e` | Run the automated Bruno E2E API tests against the running stack. |
| `mise run test` | Run Java unit tests across all microservices using Maven. |
| `mise run lint` | Run Checkstyle verification across all microservices. |

*If you do not have `mise` installed, you can look at the `mise.toml` file for the exact raw commands.*

### Access Points
- **API Gateway (Single Entry Point):** `http://localhost:8000`
- **Keycloak Admin Panel:** `http://localhost:9090` (Admin Credentials: `admin` / `admin`)
- **RabbitMQ Management UI:** `http://localhost:15672` (Credentials: `guest` / `guest`)
- **Microservices Endpoints (Internal / Gateway Routed):**
  - Member Service: `http://localhost:8000/api/members`
  - Lesson Service: `http://localhost:8000/api/lessons`
  - Competition Service: `http://localhost:8000/api/competitions`
  - Badge Service: `http://localhost:8000/api/badges`
  - Stats Service: `http://localhost:8000/api/stats`

---

## 4. API Testing with Bruno

A complete, **fully automated** End-to-End testing scenario collection is available in the `bruno/` folder. It tests 100% of the routes, verifying success states and Role-Based Access Control (RBAC) rejection layers.

### Automated Testing
To run the full suite automatically on a fresh database:
```bash
mise run test-e2e
```

### Manual Testing
1. Install the [Bruno API Client](https://www.usebruno.com/).
2. Open Bruno, click **Open Collection**, and select the `bruno/` directory.
3. Select the **Local** environment.
4. Run the requests. The collection automatically handles dynamic variable injection (tokens, generated IDs) between requests.
