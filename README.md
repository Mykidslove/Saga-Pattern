# Spring Boot Microservices -- Jenkins CI/CD Deployment on AWS EC2

## Project Overview

This project demonstrates a Spring Boot microservices application and a
practical CI/CD deployment flow using GitHub, Jenkins, and AWS EC2. The
deployment exercise builds and runs the **Eureka Server** and
**OrderService** on an EC2 Jenkins agent, with OrderService registering
successfully with Eureka.

## CI/CD Architecture

``` text
Developer Code
      ↓
GitHub Repository
      ↓
Jenkins
      ↓
AWS EC2 Jenkins Agent
      ↓
Maven Build
      ↓
Build Eureka Server + OrderService
      ↓
Deploy Spring Boot JARs
      ↓
Eureka Server : 8761
OrderService  : 8088
      ↓
OrderService registers with Eureka
```

## Technologies Used

-   Java 17
-   Spring Boot
-   Spring Cloud Netflix Eureka
-   Maven
-   Jenkins
-   Git / GitHub
-   AWS EC2
-   MySQL
-   Linux
-   REST API
-   Postman

## Microservices

The repository contains components such as Eureka Server, OrderService,
InventoryService, PaymentService, ProductService, and API Gateway. The
current CI/CD deployment exercise focuses on **Eureka Server +
OrderService**.

## CI/CD Flow

1.  Developer changes the application code and pushes it to GitHub.
2.  Jenkins checks out the latest code from the `main` branch.
3.  The Jenkins EC2 agent executes the build.
4.  Maven creates executable Spring Boot JAR files.
5.  Jenkins stops old Eureka and OrderService processes if they exist.
6.  Jenkins starts Eureka Server on port `8761`.
7.  Jenkins starts OrderService on port `8088`.
8.  OrderService connects to MySQL using credentials supplied by
    Jenkins.
9.  OrderService registers with Eureka.
10. Deployment is verified using process, port, Eureka, and API checks.

## Spring Boot Database Configuration

Do not commit real database credentials to GitHub.

``` properties
spring.datasource.url=jdbc:mysql://localhost:3306/order_db_m2
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
```

Jenkins Credentials Binding supplies `DB_USERNAME` and `DB_PASSWORD`,
keeping the real password outside the source repository.

## Eureka Configuration

``` properties
eureka.client.service-url.defaultZone=http://localhost:8761/eureka/
eureka.client.register-with-eureka=true
eureka.client.fetch-registry=true
eureka.instance.prefer-ip-address=true
```

After deployment, OrderService appears in Eureka with status **UP**.

## Jenkins Deployment Script

``` bash
export BUILD_ID=dontKillMe

cd "$WORKSPACE/EurekaPro (2)/EurekaPro/EurekaPro"
echo "Building Eureka Server..."
mvn clean package -DskipTests
pkill -f 'EurekaPro-0.0.1-SNAPSHOT.jar' || true
nohup java -jar target/EurekaPro-0.0.1-SNAPSHOT.jar \
  > /home/ubuntu/eureka.log 2>&1 &
sleep 15

cd "$WORKSPACE/OrderService (4)/OrderService/OrderService"
echo "Building OrderService..."
mvn clean package -DskipTests
pkill -f 'OrderService-0.0.1-SNAPSHOT.jar' || true
nohup java -jar target/OrderService-0.0.1-SNAPSHOT.jar \
  > /home/ubuntu/orderservice.log 2>&1 &
sleep 15

ps -ef | grep EurekaPro | grep -v grep || true
ps -ef | grep OrderService | grep -v grep || true
ss -lnt | grep -E '8761|8088' || true
```

The Jenkins job must bind the database credential to `DB_USERNAME` and
`DB_PASSWORD`.

## AWS EC2 Security Group

Security Groups act as virtual firewalls for EC2 instances. Only
required ports should be allowed.

  Port   Purpose                 Recommended Source
  ------ ----------------------- ----------------------
  22     SSH administration      My IP
  8080   Jenkins web interface   My IP while learning
  8761   Eureka dashboard        My IP while testing
  8088   OrderService API        My IP while testing

## Deployment Verification

``` bash
ps -ef | grep -E 'EurekaPro|OrderService' | grep -v grep
sudo ss -lntp | grep -E '8761|8088'
tail -50 /home/ubuntu/eureka.log
tail -50 /home/ubuntu/orderservice.log
```

Expected result:

``` text
Eureka Server : 8761
OrderService  : 8088
ORDERSERVICE  : UP
```

## API Verification

``` text
GET http://<EC2-PUBLIC-IP>:8088/orders
```

A successful test returned HTTP `200 OK`.

Avoid committing a fixed EC2 public IP because it can change unless an
Elastic IP is used.

## Security Practices

-   Never commit passwords, AWS keys, SSH private keys, or `.pem` files.
-   Store sensitive values in Jenkins Credentials or a dedicated secrets
    service.
-   Restrict EC2 Security Group rules to the minimum required ports and
    sources.
-   Avoid exposing Jenkins and Eureka publicly in production.
-   Rotate credentials that have previously been exposed.

## Current Result

``` text
GitHub checkout             ✓
Jenkins build               ✓
EC2 Jenkins agent           ✓
Maven JAR packaging         ✓
Jenkins credential binding  ✓
Eureka deployment           ✓
OrderService deployment     ✓
Eureka registration         ✓
GET /orders                 ✓
```

## Next Improvements

-   Jenkins Pipeline using a `Jenkinsfile`
-   GitHub webhook for automatic builds
-   Automated tests instead of `-DskipTests`
-   Docker containerization
-   Deploy InventoryService and PaymentService
-   Spring Boot Actuator health checks
-   HTTPS and reverse proxy/load balancer
-   Terraform
-   AWS Secrets Manager or Systems Manager Parameter Store

## Interview Explanation

> I implemented a CI/CD deployment flow for a Spring Boot microservices
> project using GitHub, Jenkins, and AWS EC2. Jenkins checks out the
> source code and executes the build on an EC2 agent. Maven packages the
> applications as executable JARs. Jenkins deploys Eureka Server and
> OrderService, while database credentials are injected through Jenkins
> Credentials instead of being hard-coded in GitHub. AWS Security Groups
> control access to the required ports. After deployment, I verified the
> processes and ports, confirmed that OrderService registered with
> Eureka, and tested the REST endpoint from Postman.
