package org.example.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import org.example.model.Account;
import org.example.model.Currency;
import org.example.model.Transaction;
import org.example.repository.AccountRepository;
import org.example.repository.TransactionRepository;

import java.math.BigDecimal;

public class TransferService {
    private final EntityManager em;
    private final AccountRepository accountRepo;
    private final TransactionRepository transactionRepo;

    public TransferService(EntityManager em, AccountRepository accountRepo, TransactionRepository transactionRepo) {
        this.em = em;
        this.accountRepo = accountRepo;
        this.transactionRepo = transactionRepo;
    }

    public void transfer(Long senderId, Long recipientId, BigDecimal amount, Currency currency) {
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            Account sender = accountRepo.findById(senderId);
            Account recipient = accountRepo.findById(recipientId);
            if (amount.compareTo(BigDecimal.ZERO) <= 0)
                throw new RuntimeException("Amount must be positive");
            if (sender == null) throw new RuntimeException("Invalid sender id");
            if (recipient == null) throw new RuntimeException("Invalid recipient id");
            if (!sender.getCurrency().equals(currency)) {
                throw new RuntimeException("Sender account currency mismatch");
            }
            if (!recipient.getCurrency().equals(currency)) {
                throw new RuntimeException("Recipient account currency mismatch");
            }
            BigDecimal senderBalance = sender.getBalance();
            BigDecimal recipientBalance = recipient.getBalance();
            if (senderBalance.compareTo(amount) >= 0) {
                sender.setBalance(senderBalance.subtract(amount));
                recipient.setBalance(recipientBalance.add(amount));
            } else {
                throw new RuntimeException("Insufficient amount");
            }
            Transaction t = new Transaction(sender, recipient, amount, currency);
            transactionRepo.save(t);
            transaction.commit();
        } catch (RuntimeException e) {
            transaction.rollback();
            throw e;
        }
    }
}
