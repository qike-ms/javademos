FROM java:8-jre

ARG heap_size=200m
ENV JAVA_OPTS="-Xmx${heap_size}"
# Add -XX:+PrintGC if want to log gc events.

ADD ./target/snat-test.jar /app/
# On JAVA 8, if Xmx is not set, it defaults to 1/4 of available memory or 1GB, whichever is smaller
ENTRYPOINT exec java $JAVA_OPTS  -jar /app/snat-test.jar

