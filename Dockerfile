FROM gradle:8.5-jdk17-alpine
WORKDIR /app
COPY . .
RUN gradle build -x test --no-daemon
EXPOSE 8080
CMD ["gradle", "run", "--no-daemon"]
