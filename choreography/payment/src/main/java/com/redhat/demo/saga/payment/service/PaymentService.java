package com.redhat.demo.saga.payment.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.redhat.demo.saga.payment.constant.OrderEventType;
import com.redhat.demo.saga.payment.event.PaymentEvent;
import com.redhat.demo.saga.payment.constant.PaymentEventType;
import com.redhat.demo.saga.payment.model.Account;
import com.redhat.demo.saga.payment.model.Payment;
import com.redhat.demo.saga.payment.model.PaymentState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.transaction.Transactional;
import java.time.Instant;

@ApplicationScoped
public class PaymentService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentService.class);

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Inject
    EntityManager entityManager;

    @Inject
    EventService eventService;

    @Inject
    AccountService accountService;


    @Transactional
    public void onEventReceived(String orderId, String payload) {

        LOGGER.info("Received event: orderId {} - payload {}", orderId, payload);

        JsonNode json;
        String accountId;
        String itemEventType;
        Double itemCost;

        try {
            json = objectMapper.readTree(payload);
            accountId = json.get("accountid").asText();
            itemEventType = json.get("itemeventtype").asText();
            itemCost = json.get("totalcost").asDouble();
        } catch (Exception e) {
            LOGGER.error("Can't create JsonNode {}", e);
            return;
        }

        //verify if item is already processed
        if (eventService.isEventProcessed(orderId, itemEventType)) {
            LOGGER.error("An event with same order id {}, itemEventType {} already processed, discard!", orderId, itemEventType);
            return;
        }

        LOGGER.info("Received eventtype {} for order id {}", itemEventType, orderId);

        if (itemEventType.equals(OrderEventType.ORDER_COMPLETED.name())) {
            //verify there is already a orderId for payment
            Payment payment = findPaymentByOrderId(orderId);

            if (payment != null) {
                LOGGER.error("A payment with same order id already processed, discard!", orderId);
                return;
            }

            //get Account
            Account account = accountService.findById(accountId);
            if (account == null) {
                LOGGER.error("No account existing {}, discard!", accountId);
                return;
            }

            payment = new Payment();
            payment.setOrderId(orderId);
            payment.setAccount(account);
            payment.setOrderCost(itemCost);
            if(itemCost > account.getFunds())
                payment.setState(PaymentState.PAYMENT_REFUSED);
            else {
                payment.setState(PaymentState.PAYMENT_ACCEPTED);
                //update account
                account.setFunds(account.getFunds() - itemCost);

                entityManager.merge(account);
                entityManager.flush();
            }

            createPayment(payment);
            createPaymentEvent(payment);

            eventService.processEvent(orderId, OrderEventType.ORDER_COMPLETED.name());
        } else {
            LOGGER.error("Event type not recognized!");
            return;
        }

    }

    @Transactional
    public void createPayment(Payment payment) {
        try {
            entityManager.persist(payment);
            entityManager.flush();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Transactional
    public void createPaymentEvent(Payment payment) {
        PaymentEvent paymentEvent = new PaymentEvent();
        paymentEvent.setCorrelationId(payment.getOrderId());
        paymentEvent.setAccountId(String.valueOf(payment.getAccount().getId()));
        paymentEvent.setCreatedOn(Instant.now());
        paymentEvent.setItemeventtype(PaymentEventType.valueOf(payment.getState().name()));

        entityManager.persist(paymentEvent);
        entityManager.flush();
    }


    public Payment findPaymentByOrderId(String orderId) {
        Payment payment;
        try {
            payment = (Payment) entityManager.createNamedQuery("Payment.findByOrder")
                    .setParameter("orderId", orderId)
                    .getSingleResult();
        } catch (NoResultException nre) {
            LOGGER.info("Payment linked to order not found {}", orderId);
            return null;
        }
        return payment;
    }

}
