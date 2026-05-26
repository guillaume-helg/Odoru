import sys
import anyio
import dagger
from dagger import dag

async def main():
    print("Connecting to Dagger...")
    async with dagger.connection(dagger.Config(log_output=sys.stderr)):
        # 1. Get host source directory
        source = dag.host().directory(".")

        # 2. Define MongoDB service
        print("Starting MongoDB service...")
        mongodb = (
            dag.container()
            .from_("mongo:latest")
            .with_exposed_port(27017)
            .as_service()
        )

        # 3. Define base Maven containers using JDK 26
        m2_cache = dag.cache_volume("maven-m2-cache")
        maven_member = (
            dag.container()
            .from_("maven:3-eclipse-temurin-26")
            .with_mounted_directory("/src", source)
            .with_workdir("/src/odoru-member-service")
            .with_mounted_cache("/root/.m2", m2_cache)
        )
        maven_lesson = (
            dag.container()
            .from_("maven:3-eclipse-temurin-26")
            .with_mounted_directory("/src", source)
            .with_workdir("/src/odoru-lesson-service")
            .with_mounted_cache("/root/.m2", m2_cache)
        )

        # 4. Checkstyle Linting
        print("Running Checkstyle linting for Member Service...")
        lint_member = maven_member.with_exec(["mvn", "checkstyle:check"])
        await lint_member.sync()

        print("Running Checkstyle linting for Lesson Service...")
        lint_lesson = maven_lesson.with_exec(["mvn", "checkstyle:check"])
        await lint_lesson.sync()
        print("Checkstyle check passed!")

        # 5. Unit Tests
        print("Running unit tests for Member Service...")
        test_member = (
            maven_member
            .with_service_binding("mongodb", mongodb)
            .with_env_variable("MONGODB_HOST", "mongodb")
            .with_exec(["mvn", "test", "jacoco:report"])
        )
        await test_member.sync()

        print("Running unit tests for Lesson Service...")
        test_lesson = (
            maven_lesson
            .with_service_binding("mongodb", mongodb)
            .with_env_variable("MONGODB_HOST", "mongodb")
            .with_exec(["mvn", "test", "jacoco:report"])
        )
        await test_lesson.sync()
        print("Unit tests passed!")

        # 6. OpenAPI Generation
        print("Generating OpenAPI spec for Member Service...")
        build_member = (
            maven_member
            .with_service_binding("mongodb", mongodb)
            .with_env_variable("MONGODB_HOST", "mongodb")
            .with_exec([
                "mvn", "clean", "verify", "-DskipTests",
                "spring-boot:start", "springdoc-openapi:generate", "spring-boot:stop"
            ])
        )
        await build_member.file("target/openapi.json").export("odoru-member-service/target/openapi.json")

        print("Generating OpenAPI spec for Lesson Service...")
        build_lesson = (
            maven_lesson
            .with_service_binding("mongodb", mongodb)
            .with_env_variable("MONGODB_HOST", "mongodb")
            .with_exec([
                "mvn", "clean", "verify", "-DskipTests",
                "spring-boot:start", "springdoc-openapi:generate", "spring-boot:stop"
            ])
        )
        await build_lesson.file("target/openapi.json").export("odoru-lesson-service/target/openapi.json")
        print("OpenAPI specs generated and exported successfully!")

if __name__ == "__main__":
    try:
        anyio.run(main)
    except Exception as e:
        print(f"CI Pipeline Failed: {e}", file=sys.stderr)
        sys.exit(1)
