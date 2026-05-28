# Odoru - Rhythmic Dance Club Platform

Odoru is a distributed platform designed to manage member registrations, course planning, competition scheduling, attendance swiping, and statistics reporting for a rhythmic dance club.

---

## 1. System Architecture

The application is structured as a collection of microservices behind a high-performance **KrakenD API Gateway**, authenticated using **Keycloak** (OAuth2/OIDC), and using **MongoDB** for persistence.

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
    
    LS -.->|REST Client| MS
    CS -.->|REST Client| MS
    BS -.->|REST Client| MS
    BS -.->|REST Client| LS
    
    SS -.->|REST Client| MS
    SS -.->|REST Client| LS
    SS -.->|REST Client| CS
    SS -.->|REST Client| BS
    
    MS & LS & CS & BS -->|Persistence| MongoDB[(MongoDB:27017)]
```

---

## 2. Microservices Data Schemas

Each microservice manages its own domain boundary in MongoDB. The class diagram below illustrates the entity properties and logical relationships across the databases:

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

### Prerequisites
- Docker & Docker Compose installed.

### Start the entire platform
Build and start Keycloak, MongoDB, the API Gateway, and the five Spring Boot microservices:
```bash
docker-compose up --build -d
```

### Access Points
- **API Gateway (Single Entry Point):** `http://localhost:8000`
- **Keycloak Admin Panel:** `http://localhost:9090` (Admin Credentials: `admin` / `admin`)
- **Microservices Endpoints (Internal / Gateway Routed):**
  - Member Service: `http://localhost:8000/api/members`
  - Lesson Service: `http://localhost:8000/api/lessons`
  - Competition Service: `http://localhost:8000/api/competitions`
  - Badge Service: `http://localhost:8000/api/badges`
  - Stats Service: `http://localhost:8000/api/stats`

---

## 4. API Testing with Bruno

A complete testing scenario collection is available in the [bruno/](file:///c:/Users/guill/Documents/Project/Odoru/bruno) folder.

1. Install the [Bruno API Client](https://www.usebruno.com/).
2. Open Bruno, click **Open Collection**, and select the [bruno/](file:///c:/Users/guill/Documents/Project/Odoru/bruno) directory.
3. Select the **Local** environment.
4. Run the requests sequentially from `01_Authentication` onwards. Access tokens will automatically extract and populate the environment variables.
