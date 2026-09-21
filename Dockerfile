FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY Task04.java .
RUN javac Task04.java
CMD java Task04 & python3 -m http.server $PORT
