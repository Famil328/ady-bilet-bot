FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY Task04.java .
RUN javac Task04.java
CMD java Task04 & java -net-properties=1 -cp . Task04
