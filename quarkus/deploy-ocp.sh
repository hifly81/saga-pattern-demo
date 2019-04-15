#!/bin/bash

ocp_admin_url=127.0.0.1:8443
ocp_user_token=$(oc whoami -t)
ocp_namespace=saga-summit-2019

#create ocp project
oc login ${ocp_admin_url} --token ${ocp_user_token}
oc delete project ${ocp_namespace}
oc new-project ${ocp_namespace}

echo -e "\nPruning done. Starting application..."

############################ Postgres
echo -e "\nStart Postgresql container...."

oc import-image rhscl/postgresql-96-rhel7 --from=registry.access.redhat.com/rhscl/postgresql-96-rhel7 -n ${ocp_namespace} --confirm
oc new-app -e POSTGRESQL_USER=postgres -e POSTGRESQL_PASSWORD=postgres -e POSTGRESQL_DATABASE=test registry.access.redhat.com/rhscl/mysql-57-rhel7 --name=postgres -n ${ocp_namespace}
#oc port-forward <name-of-postgres-pod> 5432:5432
#sleep 5
#echo -e "\nCREATE tickets database...."
#docker exec -it postgres psql -h localhost -p 5432 -U postgres -c 'CREATE DATABASE tickets;'
#sleep 5
#echo -e "\nCREATE payments database...."
#docker exec -it postgres psql -h localhost -p 5432 -U postgres -c 'CREATE DATABASE payments;'
#sleep 5
#echo -e "\nCREATE insurances database...."
#docker exec -it postgres psql -h localhost -p 5432 -U postgres -c 'CREATE DATABASE insurances;'

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
