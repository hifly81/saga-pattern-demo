package com.redhat.demo.saga.insurance.consumer;

import com.redhat.demo.saga.insurance.service.InsuranceService;
import io.smallrye.reactive.messaging.kafka.KafkaMessage;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

@ApplicationScoped
public class TicketsEventConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(TicketsEventConsumer.class);

    @Inject
    InsuranceService insuranceService;

    @Incoming("tickets")
    public CompletionStage<Void> onMessage(KafkaMessage<String, String> message) {
        final Optional<String> orderId = message.getHeaders().getOneAsString("correlationid");

        if(orderId.isPresent()) {
            String payload = message.getPayload();
            insuranceService.onEventReceived(orderId.get(), payload);
        }

        return message.ack();
    }

}
