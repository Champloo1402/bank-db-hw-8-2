package org.example.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import org.example.model.Account;
import org.example.model.ExchangeRate;
import org.example.model.Transaction;
import org.example.repository.AccountRepository;
import org.example.repository.ExchangeRateRepository;
import org.example.repository.TransactionRepository;

import java.math.BigDecimal;

public class CurrencyConversionService {
    private final EntityManager em;
    private final AccountRepository accountRepo;
    private final TransactionRepository transactionRepo;
    private final ExchangeRateRepository exchangeRateRepository;

    public CurrencyConversionService(EntityManager em, AccountRepository accountRepo, TransactionRepository transactionRepo, ExchangeRateRepository exchangeRateRepository) {
        this.em = em;
        this.accountRepo = accountRepo;
        this.transactionRepo = transactionRepo;
        this.exchangeRateRepository = exchangeRateRepository;
    }

    public void convert(Long sourceAccountId, Long targetAccountId, BigDecimal amount) {
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            Account source = accountRepo.findById(sourceAccountId);
            Account target = accountRepo.findById(targetAccountId);
            if (amount.compareTo(BigDecimal.ZERO) <= 0)
                throw new RuntimeException("Amount must be positive");
            if (source == null) throw new RuntimeException("Invalid source id");
            if (target == null) throw new RuntimeException("Invalid target id");
            if (source.getCurrency().equals(target.getCurrency()))
                throw new RuntimeException("Currency must be different");
            if (!source.getUser().equals(target.getUser())) throw new RuntimeException("Wrong user");
            ExchangeRate rate = exchangeRateRepository.findByFromAndToCurrency(source.getCurrency(), target.getCurrency());
            if (rate == null) {
                throw new RuntimeException("No exchange rate defined for "
                        + source.getCurrency() + " -> " + target.getCurrency());
            }
            if(source.getBalance().compareTo(amount) < 0) {
                throw new RuntimeException("Insufficient balance");
            }
            source.setBalance(source.getBalance().subtract(amount));
            BigDecimal convertedAmount = amount.multiply(rate.getRate());
            target.setBalance(target.getBalance().add(convertedAmount));
            Transaction t = new Transaction(
                    source,
                    target,
                    amount,
                    source.getCurrency()
            );
            transactionRepo.save(t);
            transaction.commit();
        } catch (RuntimeException e) {
            transaction.rollback();
            throw e;
        }
    }
}
