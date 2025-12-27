package com.paypal.transaction_service.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.paypal.transaction_service.client.WalletClient;
import com.paypal.transaction_service.dto.dto.*;
import com.paypal.transaction_service.entity.Transaction;
import com.paypal.transaction_service.kafka.KafkaEventProducer;
import com.paypal.transaction_service.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository repository;
    private final ObjectMapper objectMapper;
    private final KafkaEventProducer kafkaEventProducer;

    @Autowired
    private WalletClient walletClient; // Feign client

    // @Autowired
    // private RestTemplate restTemplate; // old RestTemplate approach

    public TransactionServiceImpl(TransactionRepository repository,
                                  KafkaEventProducer kafkaEventProducer,
                                  ObjectMapper objectMapper) {
        this.repository = repository;
        this.objectMapper = objectMapper;
        this.kafkaEventProducer = kafkaEventProducer;
    }

    @Override
    public Transaction createTransaction(Transaction request) {
        System.out.println("🚀 Entered createTransaction()");

        Long senderId = request.getSenderId();
        Long receiverId = request.getReceiverId();
        Double amount = request.getAmount();

        // Step 0: Mark transaction as PENDING
        request.setStatus("PENDING");
        request.setTimestamp(LocalDateTime.now());
        Transaction savedTransaction = repository.save(request);
        System.out.println("📥 Transaction PENDING saved: " + savedTransaction);

        String holdReference = null;
        boolean captured = false; // whether capture (actual debit) completed

        try {
            // Step 1: Place hold on sender wallet
            HoldRequest holdRequest = new HoldRequest();
            holdRequest.setUserId(senderId);
            holdRequest.setCurrency("INR");
            holdRequest.setAmount(amount.longValue());
            holdReference = walletClient.placeHold(holdRequest).getHoldReference();
            System.out.println("🛑 Hold placed: " + holdReference);

            // Step 2: Capture hold → debit sender wallet
            CaptureRequest captureRequest = new CaptureRequest();
            captureRequest.setHoldReference(holdReference);
            walletClient.capture(captureRequest);
            captured = true;
            System.out.println("💸 Hold captured → sender debited");

            // Step 3: Credit receiver wallet
            CreditRequest creditRequest = new CreditRequest();
            creditRequest.setUserId(receiverId);
            creditRequest.setCurrency("INR");
            creditRequest.setAmount(amount.longValue());
            walletClient.credit(creditRequest);
            System.out.println("💰 Receiver credited successfully");

            // Step 4: Mark transaction as SUCCESS
            savedTransaction.setStatus("SUCCESS");
            savedTransaction = repository.save(savedTransaction);
            System.out.println("✅ Transaction SUCCESS: " + savedTransaction);

        } catch (Exception e) {
            System.err.println("❌ Transaction failed: " + e.getMessage());

            // If hold was placed but not captured, try to release
            if (holdReference != null && !captured) {
                try {
                    walletClient.release(holdReference);
                    System.out.println("🔄 Hold released after failure: " + holdReference);
                } catch (Exception ex) {
                    System.err.println("❌ Failed to release hold: " + ex.getMessage());
                }
            }

            savedTransaction.setStatus("FAILED");
            savedTransaction = repository.save(savedTransaction);
            System.out.println("❌ Transaction FAILED saved: " + savedTransaction);
            return savedTransaction;
        }

        // Step 5: Send Kafka Event
        try {
            String key = String.valueOf(savedTransaction.getId());
            kafkaEventProducer.sendTransactionEvent(key, savedTransaction);
            System.out.println("🚀 Kafka message sent");
        } catch (Exception e) {
            System.err.println("❌ Failed to send Kafka event: " + e.getMessage());
            e.printStackTrace();
        }

        return savedTransaction;
    }

    @Override
    public Transaction getTransactionById(Long id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public List<Transaction> getTransactionsByUser(Long userId) {
        return repository.findBySenderIdOrReceiverId(userId, userId);
    }
}
