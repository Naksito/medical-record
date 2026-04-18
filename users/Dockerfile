# This step takes the built .jar file of the service and creates a custom JRE using jlink

# We need an Alpine based image to build the JRE
FROM amazoncorretto:17-alpine AS packager

WORKDIR /opt

COPY /target/users-1.0.0.jar app.jar

# Show all needed modules for the .jar file and pipe them into a text file
RUN jdeps --print-module-deps --ignore-missing-deps app.jar > deps.txt

# Create custom JRE using the modules from the text file
RUN jlink \
--compress 2 \
--strip-java-debug-attributes \
--no-header-files \
--no-man-pages \
--output jre \
--add-modules $(cat deps.txt)

# Alpine is by far the best option to make the image size as small as possible
FROM alpine:latest

COPY --from=packager /opt/jre /opt/jre
COPY --from=packager /opt/app.jar /opt/app.jar

EXPOSE 9090

CMD ["/opt/jre/bin/java", "-jar", "/opt/app.jar"]