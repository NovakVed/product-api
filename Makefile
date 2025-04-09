GRADLE = ./gradlew
BUILD = build -x test
DOCKER_COMPOSE = docker-compose
IMAGE_NAME = product-api

.DEFAULT_GOAL := help

# TODO - adjust

help:
	@echo "Makefile for Product API"
	@echo
	@echo "Available commands:"
	@echo "  make build                         - Build the entire project"
	@echo "  make unpack                        - Unpack the JAR file"
	@echo "  make containerize                  - Build the project and create the latest Docker image"
	@echo "  make infra-up                      - Set up the project infrastructure using Docker Compose"
	@echo "  make infra-down                    - Clean up the project infrastructure using Docker Compose"
	@echo

.PHONY: all build unpack containerize infra-up infra-down

all: build

build:
	@echo "Building the product-api service"; \
	$(GRADLE) $(BUILD)
	@echo "Unpacking the JAR file"; \
    mkdir -p build/dependency && (cd build/dependency; jar -xf ../libs/*.jar)

unpack:
	@echo "Unpacking the JAR file"; \
	mkdir -p build/dependency && (cd build/dependency; jar -xf ../libs/*.jar)

containerize: build unpack
	docker build -t $(IMAGE_NAME) .

infra-up: build
	@echo "Setting up the development environment"; \
	$(DOCKER_COMPOSE) up -d

infra-down:
	@echo "Cleaning up the development environment"; \
	$(DOCKER_COMPOSE) down