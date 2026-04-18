FROM node:alpine AS builder

WORKDIR /opt
COPY package.json package-lock.json ./
RUN npm ci

COPY . .

RUN npm install -g @angular/cli
RUN ng build

FROM nginx:alpine

COPY nginx.conf /etc/nginx/conf.d/default.conf

COPY --from=builder /opt/dist/frontend /usr/share/nginx/html/frontend
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]