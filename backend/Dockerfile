FROM azul/zulu-openjdk:16
ENV LD_LIBRARY_PATH=/usr/local/lib
COPY --from=jrottenberg/ffmpeg /usr/local /usr/local/
RUN apt update && \
    apt -y install wget libgomp1
RUN wget http://archive.ubuntu.com/ubuntu/pool/main/o/openssl1.0/libssl1.0.0_1.0.2n-1ubuntu5.10_amd64.deb
RUN apt install ./libssl1.0.0_1.0.2n-1ubuntu5.10_amd64.deb
ENV SPRING_APPLICATION_NAME=shareleaf
ENV SPRING_PROFILES_ACTIVE=dev
ENV SL_GH_USERNAME=bmelesse
ENV SL_GH_TOKEN=
EXPOSE 8080
COPY shareleaf-api/build/libs/app.jar /app.jar
ENTRYPOINT ["java","-jar","/app.jar"]