#!/bin/bash
#title           :test-saga-failed.sh
#description     :This script will test the SAGA Choreography env
#author		     :hifly81
#date            :20190410
#version         :0.1
#usage		     :bash test-saga-failed.sh
#notes           :requires: curl, jq, docker
#==============================================================================


########################### Test Saga
echo -e "\nAdd 1 ticket..."
echo -e "\nResponse:"
response=$(curl -s -X POST -H "Accept:application/json" -H "Content-Type:application/json" localhost:8080/tickets -d @json/tickets-test2.json)
echo $response
echo -e "\nExtract ticketId..."
ticketId=$(echo $response | jq '.id')
echo -e "\nticketId:$ticketId"
sleep 5
echo -e "\n\nVerify TicketEvent Table..."
docker exec -it postgres psql -h localhost -p 5432 -U postgres -d tickets -c 'select * from ticketevent;'
sleep 5
echo -e "\nAdd 1 insurance for ticket:$ticketId"
cp -f json/insurances-test2.json json/insurances-test2.json_bck
sed -i -e "s/ticketplaceholder/$ticketId/g" json/insurances-test2.json
cat json/insurances-test2.json
sleep 5
echo -e "\nResponse:"
curl -X POST -H "Accept:application/json" -H "Content-Type:application/json" localhost:8090/insurances -d @json/insurances-test2.json
sleep 5
cp -f json/insurances-test2.json_bck json/insurances-test2.json
rm -f json/insurances-test2.json_bck
echo -e "\n\nVerify Insurance Table..."
docker exec -it postgres psql -h localhost -p 5432 -U postgres -d insurances -c 'select * from insurance;'
echo -e "\n\nVerify OrderEvent Table..."
docker exec -it postgres psql -h localhost -p 5432 -U postgres -d insurances -c 'select * from orderevent;'
echo -e "\n\nVerify PaymentEvent Table..."
docker exec -it postgres psql -h localhost -p 5432 -U postgres -d payments -c 'select * from paymentevent;'
echo -e "\n\nVerify Ticket Table..."
docker exec -it postgres psql -h localhost -p 5432 -U postgres -d tickets -c 'select * from ticket;'
echo -e "\n\nVerify Insurance Table..."
docker exec -it postgres psql -h localhost -p 5432 -U postgres -d insurances -c 'select * from insurance;'
echo -e "\n\nVerify Account Table..."
docker exec -it postgres psql -h localhost -p 5432 -U postgres -d payments -c 'select * from account;'