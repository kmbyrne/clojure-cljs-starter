# syntax=docker/dockerfile:experimental
FROM theasp/clojurescript-nodejs:shadow-cljs-alpine as build
WORKDIR /app
COPY package.json shadow-cljs.edn /app/
RUN --mount=type=cache,target=/app/node_modules npm install && shadow-cljs npm-deps
COPY public /app/public
COPY src /app/src

RUN --mount=type=cache,target=/app/node_modules npm run release
FROM nginx
COPY --from=build /app/public /usr/share/nginx/html

