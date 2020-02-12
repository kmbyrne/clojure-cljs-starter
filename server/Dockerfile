# syntax=docker/dockerfile:experimental
FROM clojure AS builder
RUN mkdir -p /usr/src/app
WORKDIR /usr/src/app
ENV foo bar
COPY project.clj /usr/src/app/
RUN --mount=type=cache,target=/root/.m2 lein deps
COPY src /usr/src/app
EXPOSE 8080
EXPOSE 3001

RUN --mount=type=cache,target=/root/.m2 mv "$(lein uberjar | sed -n 's/^Created \(.*standalone\.jar\)/\1/p')" /app-standalone.jar

FROM clojure
COPY --from=builder /app-standalone.jar app-standalone.jar
CMD ["java", "-jar", "app-standalone.jar"]
