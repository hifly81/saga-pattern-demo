package com.redhat.demo.saga.insurance.consumer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.redhat.demo.saga.insurance.service.InsuranceService;
import io.smallrye.reactive.messaging.kafka.KafkaMessage;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

@ApplicationScoped
public class PaymentsEventConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentsEventConsumer.class);

    @Inject
    InsuranceService insuranceService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Incoming("payments")
    public CompletionStage<Void> onMessage(KafkaMessage<String, String> message) throws IOException {
        try {
            JsonNode json = objectMapper.readTree(message.getPayload());
            final Optional<String> orderId = message.getHeaders().getOneAsString("correlationid");

            if (orderId.isPresent())
                insuranceService.onPaymentReceived(orderId.get(), json);

        }
        catch (Throwable t) {
            t.printStackTrace();
        }
        return message.ack();
    }

}
