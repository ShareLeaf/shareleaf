FROM python:3.9
ENV LD_LIBRARY_PATH=/usr/local/lib
COPY --from=jrottenberg/ffmpeg /usr/local /usr/local/
RUN wget http://security.ubuntu.com/ubuntu/pool/main/o/openssl1.0/libssl1.0.0_1.0.2n-1ubuntu5.9_amd64.deb
RUN apt install ./libssl1.0.0_1.0.2n-1ubuntu5.9_amd64.deb
WORKDIR /usr/app/server
ENV PYTHONUNBUFFERED=true
ENV FLASK_APP=app.y
ENV FLASK_RUN_HOST=0.0.0.0
ENV SHARELEAF_AWS_BUCKET_NAME=shareleaf
ENV SHARELEAF_AWS_CDN=https://d1x2gijf3bj73j.cloudfront.net
#ENV SHARELEAF_AWS_ACCESS_KEY_ID=${{ env.AWS_ACCESS_KEY_ID }}
#ENV SHARELEAF_AWS_SECRET_KEY_ID=${{ env.AWS_SECRET_ACCESS_KEY }}
ENV SHARELEAF_DATASOURCE_USERNAME=shareleaf
ENV SHARELEAF_DATASOURCE_PASSWORD=shareleaf
ENV SHARELEAF_DATASOURCE_URL=host.docker.internal:7432/content
COPY ./ ./
#RUN pip install --upgrade setuptools --user python
RUN pip install --upgrade pip
RUN pip install -r requirements.txt
EXPOSE 5000
CMD ["gunicorn", "--preload", "--bind", "0.0.0.0:5000", "-w", "3", "app:app"]