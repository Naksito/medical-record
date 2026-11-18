FROM node:22.14.0-alpine3.21 AS builder

ARG SERVICE_VERSION=unknown

WORKDIR /opt
COPY package.json package-lock.json ./
RUN npm ci

COPY . .

RUN printf '{"version":"%s"}\n' "$SERVICE_VERSION" > public/version.json

RUN npm install -g @angular/cli
RUN ng build

FROM nginx:1.27.4-alpine3.21

COPY nginx.conf /etc/nginx/conf.d/default.conf

COPY --from=builder /opt/dist/frontend /usr/share/nginx/html/frontend
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]