# Guide

## Prerequisites

### Install MySQL on Docker

`docker pull mysql:latest`

Run MySQL on port 3306

`docker run --name mysql -e MYSQL_ROOT_PASSWORD=<your-root-password> -d mysql:latest`

### Swagger

`http://localhost:8080/swagger-ui/index.html`

### Kibana

`http://localhost:5601/app/management/kibana/indexPatterns/`

### Prometheus

`http://localhost:9090/targets`

### RabbitMQ - Run before starting the app
docker run --rm -it -p 15672:15672 -p 5672:5672 --name SBA_Cinema -d rabbitmq:3-management

