FROM tomcat:9.0-jdk11

# Remove default Tomcat apps to keep the image clean
RUN rm -rf /usr/local/tomcat/webapps/*

# Deploy your app - Tomcat auto-extracts and serves it at /OnlineExaminationSystem/
COPY OnlineExaminationSystem.war /usr/local/tomcat/webapps/OnlineExaminationSystem.war

# Create a tiny ROOT app so visiting the bare domain (/) auto-redirects
# to /OnlineExaminationSystem/ instead of showing "Not Found"
RUN mkdir -p /usr/local/tomcat/webapps/ROOT && \
    echo '<!DOCTYPE html><html><head><meta http-equiv="refresh" content="0; url=/OnlineExaminationSystem/"></head><body>Redirecting...</body></html>' \
    > /usr/local/tomcat/webapps/ROOT/index.html

EXPOSE 8080

CMD ["catalina.sh", "run"]