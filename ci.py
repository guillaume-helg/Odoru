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

        # 3. Define base Maven container using JDK 26
        m2_cache = dag.cache_volume("maven-m2-cache")
        maven_base = (
            dag.container()
            .from_("maven:3-eclipse-temurin-26")
            .with_mounted_directory("/src", source)
            .with_workdir("/src/odoru-member-service")
            .with_mounted_cache("/root/.m2", m2_cache)
        )

        # 4. Checkstyle Linting
        print("Running Checkstyle linting...")
        lint = maven_base.with_exec(["mvn", "checkstyle:check"])
        await lint.sync()
        print("Checkstyle check passed!")

        # 5. Unit Tests
        print("Running unit tests...")
        test = (
            maven_base
            .with_service_binding("mongodb", mongodb)
            .with_env_variable("MONGODB_HOST", "mongodb")
            .with_exec(["mvn", "test", "jacoco:report"])
        )
        await test.sync()
        print("Unit tests passed!")

        # 6. OpenAPI Generation
        print("Generating OpenAPI spec...")
        build = (
            maven_base
            .with_service_binding("mongodb", mongodb)
            .with_env_variable("MONGODB_HOST", "mongodb")
            .with_exec([
                "mvn", "clean", "verify", "-DskipTests",
                "spring-boot:start", "springdoc-openapi:generate", "spring-boot:stop"
            ])
        )
        # Export openapi.json to host filesystem
        await build.file("target/openapi.json").export("odoru-member-service/target/openapi.json")
        print("OpenAPI spec generated and exported successfully!")

if __name__ == "__main__":
    try:
        anyio.run(main)
    except Exception as e:
        print(f"CI Pipeline Failed: {e}", file=sys.stderr)
        sys.exit(1)
