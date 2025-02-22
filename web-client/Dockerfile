FROM node:18 AS builder
RUN apt-get update
WORKDIR /usr/src/
COPY . .
RUN npm install
RUN npm run build

FROM nginx
COPY --from=builder /usr/src/dist /usr/share/nginx/html
EXPOSE 80