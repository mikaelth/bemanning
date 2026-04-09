#!/bin/bash

# Run Spring Boot application with custom security policy to enable required ciphers
# This allows the AES256-GCM-SHA384 cipher that the LDAP server requires

mvn spring-boot:run -Pdev,bemanning-5 \
  -Dspring-boot.run.jvmArguments="\
    -Djava.security.properties=java.security.override \
    -Djdk.tls.client.protocols=TLSv1.2" 
