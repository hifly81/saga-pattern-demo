package com.redhat.demo.saga.payment.consumer;

import com.redhat.demo.saga.payment.service.PaymentService;
import io.smallrye.reactive.messaging.kafka.KafkaMessage;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

@ApplicationScoped
public class OrdersEventConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrdersEventConsumer.class);

    @Inject
    PaymentService paymentService;

    @Incoming("orders")
    public CompletionStage<Void> onMessage(KafkaMessage<String, String> message) {
        final Optional<String> orderId = message.getHeaders().getOneAsString("correlationid");

        if(orderId.isPresent()) {
            String payload = message.getPayload();
            paymentService.onEventReceived(orderId.get(), payload);
        }

        return message.ack();
    }
}
