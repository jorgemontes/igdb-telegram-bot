FROM java:openjdk-8u111-alpine
CMD java ${JAVA_OPTS} -jar pets-all.jar
COPY build/libs/*-all.jar pets-all.jar