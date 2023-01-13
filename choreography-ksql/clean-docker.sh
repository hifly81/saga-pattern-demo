#!/bin/bash

DOCKER_HOST=$(ip -4 addr show docker0 | grep -Po 'inet \K[\d.]+')
printf "\nDocker host: ${DOCKER_HOST}"

############################ Docker prune
echo -e "\nDeleting Docker containers and images for Saga...."

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
docker rmi confluentinc/cp-zookeeper:6.1.0
docker rmi debezium/postgres
docker rmi confluentinc/cp-kafka:6.1.0
docker rmi hifly81/debezium-connect
docker rmi hifly81/quarkus-insurance-service
docker rmi hifly81/quarkus-payment-service
docker rmi hifly81/quarkus-ticket-service


echo -e "\nPruning done"
