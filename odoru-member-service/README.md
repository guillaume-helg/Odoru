# Odoru - Member Management Service (First Movement)

This service is the first component of the Odoru platform, focusing on member management for a rhythmic dance club.

## API Documentation
- **Swagger UI**: [http://localhost:8081/swagger-ui/index.html](http://localhost:8081/swagger-ui/index.html)
- **OpenAPI JSON**: [http://localhost:8081/v3/api-docs](http://localhost:8081/v3/api-docs)

### Members
- `GET /api/members`: List all members.
- `GET /api/members/{id}`: Get member by ID.
- `POST /api/members/signup`: Register a new member.
- `PUT /api/members/{id}`: Update member details.
- `PATCH /api/members/{id}/expertise?level={level}`: Update member expertise level (Secretary).
- `PATCH /api/members/{id}/registration-status`: Update member registration components (feePaid, medicalCertificateProvided, registrationValidated) (Secretary).
- `DELETE /api/members/{id}`: Remove a member.

## How to Run
Since Maven is required, ensure you have Maven installed and run:
```bash
cd odoru-member-service
mvn spring-boot:run
```
