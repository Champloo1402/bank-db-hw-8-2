package org.example.service;

import jakarta.persistence.EntityManager;
import org.example.model.Account;
import org.example.model.Currency;
import org.example.model.ExchangeRate;
import org.example.repository.AccountRepository;
import org.example.repository.ExchangeRateRepository;

import java.math.BigDecimal;
import java.util.List;

public class UserFundsService {
    private final AccountRepository accountRepo;
    private final ExchangeRateRepository exchangeRateRepo;

    public UserFundsService(AccountRepository accountRepo, ExchangeRateRepository exchangeRateRepo) {
        this.accountRepo = accountRepo;
        this.exchangeRateRepo = exchangeRateRepo;
    }

    public BigDecimal getTotalFundsInUAH(Long userId) {
        List<Account> accounts = accountRepo.findByUserId(userId);
        BigDecimal total = BigDecimal.ZERO;

        for(Account a : accounts){
            if (a.getCurrency().equals(Currency.UAH)){
                total = total.add(a.getBalance());
            } else {
                ExchangeRate er = exchangeRateRepo.findByFromAndToCurrency(a.getCurrency(), Currency.UAH);
                if (er == null) throw new RuntimeException("Exchange rate not found");
                total = total.add(a.getBalance().multiply(er.getRate()));
            }
        }
        return total;
    }
}
