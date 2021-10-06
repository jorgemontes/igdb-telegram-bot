FROM java:openjdk-8u111-alpine
CMD java ${JAVA_OPTS} -jar igdb-telegram-bot.jar
COPY build/libs/*-all.jar igdb-telegram-bot.jar