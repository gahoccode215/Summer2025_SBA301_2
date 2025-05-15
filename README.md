# How to start

## Prerequisites

### Install MySQL on Docker

`docker pull mysql:latest`

Run MySQL on port 3306

`docker run --name mysql -e MYSQL_ROOT_PASSWORD=12345 -d mysql:latest`


### Install KeyCloak on Docker

`docker pull quay.io/keycloak/keycloak:25.0.0`

Run keyCloak on port 8180

`docker run -d --name keycloak-25.0.0 -p 8180:8080 -e KEYCLOAK_ADMIN=admin -e KEYCLOAK_ADMIN_PASSWORD=admin quay.io/keycloak/keycloak:25.0.0 start-dev`

### Swagger

`http://localhost:8080/swagger-ui/index.html`
