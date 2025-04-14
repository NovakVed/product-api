IMAGE_NAME = product-api

GRADLE = ./gradlew
BUILD = build -x test
TEST = test
RUN = bootRun

DEVELOPMENT_PROFILE = dev
PRODUCTION_PROFILE = prod
SPRING_PROFILE ?= $(DEVELOPMENT_PROFILE)

DOCKER_COMPOSE = docker-compose
DOCKER_COMPOSE_DB = database
DOCKER_BUILD_WITH_ARG = docker build --build-arg

.DEFAULT_GOAL := help

help:
	@echo
	@echo "Product API Makefile"
	@echo
	@echo "Available commands:"
	@echo "  make build                   - Build the project"
	@echo "  make test                    - Run tests"
	@echo "  make unpack                  - Extract the JAR file"
	@echo "  make containerize            - Build the Docker image"
	@echo "  make infra-up                - Set up project infrastructure using Docker Compose"
	@echo "  make infra-down              - Clean up project infrastructure using Docker Compose"
	@echo "  make dev-db                  - Run only the database service from Docker Compose"
	@echo "  make dev                     - Run in development mode (default profile: dev)."
	@echo "                                 Use SPRING_PROFILE to override and SPRING_PROPS for extra properties"
	@echo

.PHONY: all build run test unpack containerize infra-up infra-down dev-db dev

all: build

build:
	@echo "Building the application"; \
	$(GRADLE) $(BUILD)
	$(MAKE) unpack

test:
	@echo "Running tests for product-api"; \
	$(GRADLE) $(TEST)

unpack:
	@if [ -f build/libs/*.jar ]; then \
		echo "Unpacking the JAR file"; \
		mkdir -p build/dependency && (cd build/dependency; jar -xf ../libs/*.jar); \
	else \
		echo "No JAR file found to unpack"; \
	fi

containerize: build unpack
	docker build -t $(IMAGE_NAME) .

infra-up: build
	@echo "Setting up the development environment"; \
	$(DOCKER_COMPOSE) up -d

infra-down:
	@echo "Cleaning up the development environment"; \
	$(DOCKER_COMPOSE) down

dev-db:
	$(DOCKER_COMPOSE) up $(DOCKER_COMPOSE_DB)

dev:
	@echo "Running in development mode with profile: $(SPRING_PROFILE)"; \
	$(GRADLE) $(RUN) -Dspring.profiles.active=$(SPRING_PROFILE) $(SPRING_PROPS)