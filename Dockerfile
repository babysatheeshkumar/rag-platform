FROM eclipse-temurin:21-jdk

# Install Tesseract OCR
#RUN apt-get update && \
#    apt-get install -y \
#        tesseract-ocr \
#        tesseract-ocr-eng && \
#    rm -rf /var/lib/apt/lists/*

WORKDIR /app

COPY rag-app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","app.jar"]