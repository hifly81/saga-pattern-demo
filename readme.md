Saga Choreography with Quarkus
=============================

### Create Native Image

Launch the script to create the native images:

```bash
cd quarkus/
./build-image.sh
```

### Launch on local env - linux and mac

Launch the bootstrap script to create the docker containers.<br>
Images are downloaded from docker hub.

Containers:
 - Postgres (image debezium/postgres) on local port 5432
 - Elastic Search + Kibana (image nshou/elasticsearch-kibana) on local port 9200 and 5601 (kibana)
 - Zookeeper (image debezium/zookeeper) on local port 2181
 - Kafka (image debezium/kafka) on local port 9092
 - Kafka Connect + Debezium (image hifly81/debezium-connect) on local port 8083
 - Ticket Service (image hifly81/quarkus-ticket-service) on local port 8080
 - Insurance Service (image hifly81/quarkus-insurance-service) on local port 8090
 - Payment Service (image hifly81/quarkus-payment-service) on local port 8100

```bash
cd quarkus/
./deploy-docker.sh
./test-saga.sh
./test-saga-failed.sh
```

2 tickets will be created.

2 insurances will be created.

### Deploy on OpenShift

Launch the bootstrap script to create your namespace.<br>
Images are downloaded from docker hub.

Containers:
 - Postgres (image debezium/postgres)
 - Zookeeper (image debezium/zookeeper)
 - Elastic + Kibana (image nshou/elasticsearch-kibana)
 - Kafka (image debezium/kafka)
 - Kafka Connect + Debezium (image hifly81/debezium-connect)
 - Ticket Service (image hifly81/quarkus-ticket-service)
 - Insurance Service (image hifly81/quarkus-insurance-service)
 - Payment Service (image hifly81/quarkus-payment-service)

```bash
cd quarkus/
./deploy-ocp.sh
```

### Verification

This is the final state inside the microservices databases at the end of the 2 sagas:

![ScreenShot 1](quarkus/images/ticketevent.png)

![ScreenShot 2](quarkus/images/orderevent.png)

![ScreenShot 3](quarkus/images/paymentevent.png)

![ScreenShot 4](quarkus/images/tickettable.png)

![ScreenShot 5](quarkus/images/insurancetable.png)

![ScreenShot 6](quarkus/images/accounttable.png)

Events as stored in Elastic Search (Kibana view):

![ScreenShot 7](quarkus/images/kibana.png)
