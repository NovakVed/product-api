FROM eclipse-temurin:17-jre

RUN apt-get update && apt-get install -y --no-install-recommends \
    adduser \
    && addgroup --system product-api && adduser --system --ingroup product-api product-api \
    && rm -rf /var/lib/apt/lists/* /var/cache/apt/archives/*

USER product-api:product-api

ARG DEPENDENCY=build/dependency

COPY ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY ${DEPENDENCY}/META-INF /app/META-INF
COPY ${DEPENDENCY}/BOOT-INF/classes /app

ENTRYPOINT ["java", "-cp", "app:app/lib/*", "com.vednovak.product"]