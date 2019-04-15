package com.redhat.demo.saga.payment.service;

import com.redhat.demo.saga.payment.model.Account;
import com.redhat.demo.saga.payment.model.Payment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

@ApplicationScoped
public class AccountService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccountService.class);

    @Inject
    EntityManager entityManager;

    public Account findById(String accountId) {
        return entityManager.find(Account.class, accountId);
    }
}
