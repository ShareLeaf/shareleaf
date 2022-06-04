#FROM node:14-alpine AS builder
#
#WORKDIR /opt/web
#COPY package.json ./
#
#RUN npm install
#RUN npm audit fix
#
#ENV PATH="./node_modules/.bin:$PATH"
#
#EXPOSE 80
#
#COPY . ./
#RUN echo "NODE_ENV=production" > .env
#
#RUN npm run build
#
#FROM nginx:1.17-alpine
#RUN apk --no-cache add curl
#RUN curl -L https://github.com/a8m/envsubst/releases/download/v1.1.0/envsubst-`uname -s`-`uname -m` -o envsubst && \
#    chmod +x envsubst && \
#    mv envsubst /usr/local/bin
#COPY ./nginx.config /etc/nginx/nginx.template
#CMD ["/bin/sh", "-c", "envsubst < /etc/nginx/nginx.template > /etc/nginx/conf.d/default.conf && nginx -g 'daemon off;'"]
#COPY --from=builder /opt/web/build /usr/share/nginx/html
#



FROM node:alpine

# Set working directory
WORKDIR /usr/app

# Install PM2 globally
RUN npm install --global pm2

COPY package*.json ./

RUN npm install

COPY ./ ./

RUN npm run build

EXPOSE 3000

USER node
CMD [ "pm2-runtime", "start", "npm", "--", "start" ]