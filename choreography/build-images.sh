#!/bin/bash
#title           :build-images.sh
#description     :This script will create docker images for the SAGA Choreography env
#author		     :hifly81
#date            :20190410
#version         :0.1
#usage		     :bash build-images.sh
#notes           :requires: mvn, docker
#==============================================================================

image_ticket_name=ticket
image_ticket_version=latest

image_insurance_name=insurance
image_insurance_version=latest

image_payment_name=payment
image_payment_version=latest

image_debezium_name=debezium-connect
image_debezium_version=latest

############################ Ticket Service

#create image
cd ticket/
mvn clean package -Pnative -Dnative-image.docker-build=true
docker build -f Dockerfile.native -t ${image_ticket_name}:${image_ticket_version} .

############################ Insurance Service

#create image
cd insurance/
mvn clean package -Pnative -Dnative-image.docker-build=true
docker build -f Dockerfile.native -t ${image_insurance_name}:${image_insurance_version} .

############################ Payment Service

#create image
cd payment/
mvn clean package -Pnative -Dnative-image.docker-build=true
docker build -f Dockerfile.native -t ${image_payment_name}:${image_payment_version} .

############################ Debezium Connect Service

#create image
cd debezium/
mvn clean install
docker build -f Dockerfile -t ${image_debezium_name}:${image_debezium_version} .