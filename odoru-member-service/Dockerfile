FROM maven:3-eclipse-temurin-26 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B -Dcheckstyle.skip -Dsonar.skip
COPY src ./src
RUN mvn package -DskipTests -Dcheckstyle.skip -Dsonar.skip

FROM eclipse-temurin:26-jre-jammy
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
