FROM python:3.7
ENV LD_LIBRARY_PATH=/usr/local/lib
COPY --from=jrottenberg/ffmpeg /usr/local /usr/local/
COPY --from=jrottenberg/ffmpeg /usr/lib/x86_64-linux-gnu /usr/lib/x86_64-linux-gnu
RUN ln -s libssl.so.1.0.0 /usr/lib/libssl.so.10
WORKDIR /usr/app/server
COPY ./ ./
RUN #apt-get install libpython3.7
RUN pip install --upgrade pip
RUN pip install -r requirements.txt
EXPOSE 5000
CMD ["gunicorn", "--preload", "--bind", "0.0.0.0:5000", "-w", "3", "app:app"]