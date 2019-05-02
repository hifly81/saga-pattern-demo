#!/bin/bash
#title           :deploy-docker.sh
#description     :This script will create the SAGA Choreography env with docker containers.
#author		     :hifly81
#date            :20190410
#version         :0.1
#usage		     :bash deploy-docker.sh
#notes           :requires: docker, curl
#==============================================================================

DOCKER_HOST=$(ip -4 addr show docker0 | grep -Po 'inet \K[\d.]+')
printf "\nDocker host: ${DOCKER_HOST}"

############################ Docker prune
echo -e "\nDeleting Docker containers running for Saga...."

docker stop $(docker ps -a | grep connect | cut -d ' ' -f 1)
docker stop $(docker ps -a | grep kafka | cut -d ' ' -f 1)
docker stop $(docker ps -a | grep zookeeper | cut -d ' ' -f 1)
docker stop $(docker ps -a | grep elk | cut -d ' ' -f 1)
docker stop $(docker ps -a | grep postgres | cut -d ' ' -f 1)
docker stop $(docker ps -a | grep ticket | cut -d ' ' -f 1)
docker stop $(docker ps -a | grep insurance | cut -d ' ' -f 1)
docker stop $(docker ps -a | grep payment| cut -d ' ' -f 1)
docker rm $(docker ps -a | grep connect | cut -d ' ' -f 1)
docker rm $(docker ps -a | grep kafka | cut -d ' ' -f 1)
docker rm $(docker ps -a | grep zookeeper | cut -d ' ' -f 1)
docker rm $(docker ps -a | grep elk | cut -d ' ' -f 1)
docker rm $(docker ps -a | grep postgres | cut -d ' ' -f 1)
docker rm $(docker ps -a | grep ticket | cut -d ' ' -f 1)
docker rm $(docker ps -a | grep insurance | cut -d ' ' -f 1)
docker rm $(docker ps -a | grep payment | cut -d ' ' -f 1)


echo -e "\nPruning done. Starting application..."

############################ Postgres

echo -e "\nStart Postgresql container...."
docker run -d --name postgres -p 5432:5432 debezium/postgres
sleep 5
echo -e "\nCREATE tickets database...."
docker exec -it postgres psql -h localhost -p 5432 -U postgres -c 'CREATE DATABASE tickets;'
sleep 5
echo -e "\nCREATE payments database...."
docker exec -it postgres psql -h localhost -p 5432 -U postgres -c 'CREATE DATABASE payments;'
sleep 5
echo -e "\nCREATE insurances database...."
docker exec -it postgres psql -h localhost -p 5432 -U postgres -c 'CREATE DATABASE insurances;'
echo -e "\nPostgresql started."

############################ Elastic Search + Kibana
sleep 5
echo -e "\nStart Elastic Search + Kibana container...."
docker run -d --name elk -p 9200:9200 -p 9300:9300 -p 5601:5601 nshou/elasticsearch-kibana
sleep 15
echo -e "\nCreate index orders..."
curl -X PUT http://localhost:9200/orders
curl -X GET http://localhost:9200/_cat/indices?v
echo -e "\nElastic Search + Kibana started."

############################ Zookeeper
sleep 5
echo -e "\nStart Zookeeper container...."
docker run -d --name zookeeper -p 2181:2181 -p 2888:2888 -p 3888:3888 debezium/zookeeper
echo -e "\nZookeeper started."

############################ Kafka
sleep 5
echo -e "\nStart Kafka container...."
docker run -d --name my-cluster-kafka-bootstrap -p 9092:9092 --link zookeeper:zookeeper debezium/kafka
echo -e "\nKafka started."

############################ Debezium - Kafka Connect with transformation
sleep 5
echo -e "\nStart Debezium Kafka connect container...."
docker run -d --name connect -p 8083:8083 -e es-host=http://elk:9200 -e BOOTSTRAP_SERVERS=my-cluster-kafka-bootstrap:9092 -e GROUP_ID=1 -e CONNECT_KEY_CONVERTER_SCHEMAS_ENABLE=false -e CONNECT_VALUE_CONVERTER_SCHEMAS_ENABLE=false -e CONFIG_STORAGE_TOPIC=my-connect-configs -e OFFSET_STORAGE_TOPIC=my-connect-offsets -e ADVERTISED_HOST_NAME=${DOCKER_HOST} --link zookeeper:zookeeper --link postgres:postgres --link my-cluster-kafka-bootstrap:my-cluster-kafka-bootstrap --link elk:elk quay.io/bridlos/outbox-connect
sleep 10
echo -e "\nCREATE kafka connector ticket-connector...."
curl -X POST -H "Accept:application/json" -H "Content-Type:application/json" localhost:8083/connectors/ -d @debezium/connector/ticket-connector.json
sleep 5
echo -e "\nCREATE kafka connector order-connector...."
curl -X POST -H "Accept:application/json" -H "Content-Type:application/json" localhost:8083/connectors/ -d @debezium/connector/order-connector.json
sleep 5
echo -e "\nCREATE kafka connector payment-connector...."
curl -X POST -H "Accept:application/json" -H "Content-Type:application/json" localhost:8083/connectors/ -d @debezium/connector/payment-connector.json

echo -e "\nKafka Connect started."

########################### Ticket Application
sleep 5
echo -e "\nStart Ticket Application container..."
docker run -d --name ticket -p 8080:8080 --link postgres:postgres --link zookeeper:zookeeper --link my-cluster-kafka-bootstrap:my-cluster-kafka-bootstrap quay.io/bridlos/ticket-service-quarkus
echo -e "\nTicket Application started."

########################### Insurance Application
sleep 5
echo -e "\nStart Insurance Application container..."
docker run -d --name insurance -p 8090:8080 --link postgres:postgres --link zookeeper:zookeeper --link my-cluster-kafka-bootstrap:my-cluster-kafka-bootstrap quay.io/bridlos/insurance-service-quarkus
echo -e "\nInsurance Application started."

########################### Payment Application
sleep 5
echo -e "\nStart Payment Application container..."
docker run -d --name payment -p 8100:8080 --link postgres:postgres --link zookeeper:zookeeper --link my-cluster-kafka-bootstrap:my-cluster-kafka-bootstrap quay.io/bridlos/payment-service-quarkus
echo -e "\nPayment Application started."

