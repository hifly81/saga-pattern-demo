package org.hifly.saga.choreography.payment.service;

import org.hifly.saga.choreography.payment.model.Account;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

@ApplicationScoped
public class AccountService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccountService.class);

    @Inject
    EntityManager entityManager;

    public Account findById(String accountId) {
        return entityManager.find(Account.class, accountId);
    }
}
