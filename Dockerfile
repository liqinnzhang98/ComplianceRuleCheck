# ************
# Build
# ************
FROM --platform=linux/amd64 eclipse-temurin:17-jdk-jammy AS build
WORKDIR /app

COPY . .
RUN ./gradlew bootWar


# ************
# Deployment
# ************
FROM tomcat:11.0.0-jre17-temurin-jammy AS deploy

COPY --from=build /app/backend/build/libs/ROOT.war /usr/local/tomcat/webapps/ROOT.war

ENTRYPOINT [ "catalina.sh", "run" ]
