# Makefile for User Service

# Variables
MVN = mvn
JAR_NAME = target/user-service-0.0.1-SNAPSHOT.jar

# Default target
all: clean build test it package

# Clean previous builds
clean:
	@echo "Cleaning previous builds..."
	$(MVN) clean

# Compile and build
build:
	@echo "Compiling and building project..."
	$(MVN) compile

# Run unit tests
test:
	@echo "Running unit tests..."
	$(MVN) test -Dspring.profiles.active=unit

# Run integration tests
it:
	@echo "Running integration tests..."
	$(MVN) verify -Dspring.profiles.active=integration

# Package jar
package:
	@echo "Building executable jar..."
	$(MVN) package -DskipTests

# Run application locally
run:
	@echo "Running Spring Boot application..."
	$(MVN) spring-boot:run -Dspring-boot.run.profiles=dev

# Show help
help:
	@echo "Makefile commands:"
	@echo "  make            -> clean, build, test, package (default)"
	@echo "  make clean      -> clean previous builds"
	@echo "  make build      -> compile project"
	@echo "  make test       -> run unit tests"
	@echo "  make it         -> run integration tests"
	@echo "  make package    -> build executable jar"
	@echo "  make run        -> run Spring Boot application"
