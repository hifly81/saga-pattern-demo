FROM debezium/connect:0.9
ENV DEBEZIUM_DIR=$KAFKA_CONNECT_PLUGINS_DIR/debezium-connect

RUN mkdir $DEBEZIUM_DIR
COPY target/debezium-1.0.jar $DEBEZIUM_DIR