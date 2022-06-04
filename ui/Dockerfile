FROM node:alpine

# Set working directory
WORKDIR /usr/app

# Install PM2 globally
RUN #npm install --global pm2

#COPY ./package*.json ./

RUN #npm install

COPY ./ ./
RUN ls src
RUN ls src/content
RUN ls src/content/Overview
RUN ls src/content/Overview/Highlights

RUN npm run build
EXPOSE 3000
USER node
CMD [ "pm2-runtime", "start", "npm", "--", "start" ]