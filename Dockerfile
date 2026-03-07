FROM eclipse-temurin:17 AS builder
WORKDIR /builder
ARG JAR_FILE=target/*jar
COPY ${JAR_FILE} catalog-service.jar

RUN java -Djarmode=tools -jar catalog-service.jar extract --layers --destination extracted

FROM eclipse-temurin:17
WORKDIR /application

COPY --from=builder /builder/extracted/dependencies/ ./
COPY --from=builder /builder/extracted/spring-boot-loader/ ./
COPY --from=builder /builder/extracted/snapshot-dependencies/ ./
COPY --from=builder /builder/extracted/application/ ./
ENTRYPOINT ["java", "-jar", "catalog-service.jar"]