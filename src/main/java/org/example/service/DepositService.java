package org.example.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import org.example.model.Account;
import org.example.model.Currency;
import org.example.model.Transaction;
import org.example.repository.AccountRepository;
import org.example.repository.TransactionRepository;

import java.math.BigDecimal;

public class DepositService {
    private final EntityManager em;
    private final AccountRepository accountRepo;
    private final TransactionRepository transactionRepo;

    public DepositService(EntityManager em, AccountRepository accountRepo, TransactionRepository transactionRepo) {
        this.em = em;
        this.accountRepo = accountRepo;
        this.transactionRepo = transactionRepo;
    }

    public void deposit(Long accountId, BigDecimal amount, Currency currency) {
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            Account account = accountRepo.findById(accountId);
            if (account == null) throw new RuntimeException("No account found");
            if (amount.compareTo(BigDecimal.ZERO) <= 0)
                throw new RuntimeException("Amount must be positive");
            if (account.getCurrency() != currency) throw new RuntimeException("Wrong currency");
            BigDecimal currentBalance = account.getBalance();
            account.setBalance(currentBalance.add(amount));
            Transaction t = new Transaction(null, account, amount, currency);
            transactionRepo.save(t);
            transaction.commit();
        } catch (RuntimeException e) {
            transaction.rollback();
            throw e;
        }
    }
}
