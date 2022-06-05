FROM python:3.7
COPY --from=jrottenberg/ffmpeg /usr/local /usr/local/
WORKDIR /usr/app/server
COPY ./ ./
RUN #apt-get install libpython3.7
RUN pip install --upgrade pip
RUN pip install -r requirements.txt
EXPOSE 5000
CMD ["\"gunicorn\"", "\"--preload\"", "\"--bind\"", "\"0.0.0.0:5000\"", "\"-w\"", "\"3\"", "\"app:app\""]