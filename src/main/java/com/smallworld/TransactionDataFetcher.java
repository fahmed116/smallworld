package com.smallworld;

import com.smallworld.data.Transaction;
import com.smallworld.exception.OperationException;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static java.util.Comparator.comparing;
import static java.util.Comparator.comparingDouble;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.trim;

public class TransactionDataFetcher {

    /**
     * Returns the sum of the amounts of all successful transactions
     */
    double getTotalTransactionAmount(final List<Transaction> transactions) {
        final List<Transaction> completeTransactions = transactions.stream()
                .filter(Transaction::isIssueSolved)
                .collect(toList());

        return sumTransactionAmounts(completeTransactions);
    }

    /**
     * Returns the sum of the amounts of all successful transactions sent by the specified client
     */
    double getTotalTransactionAmountSentBy(final String senderFullName, final List<Transaction> transactions) {
        if (isBlank(senderFullName)) {
            throw new OperationException("Operation is not allowed with null or empty sender name");
        }

        final String sanitizedSenderName = trim(senderFullName);
        final List<Transaction> senderTransactions = transactions.stream()
                .filter(transaction -> transaction.getSenderFullName().equalsIgnoreCase(sanitizedSenderName) &&
                        transaction.isIssueSolved())
                .collect(toList());

        return sumTransactionAmounts(senderTransactions);
    }

    /**
     * Returns the highest successful transaction amount
     */
    double getMaxTransactionAmount(final List<Transaction> transactions) {
        Optional<Transaction> optionalTransaction = transactions.stream()
                .filter(Transaction::isIssueSolved)
                .max(comparingDouble(Transaction::getAmount));

        return optionalTransaction.isEmpty()
                ? 0
                : optionalTransaction.get().getAmount();
    }

    /**
     * Counts the number of unique clients that sent or received a successful transaction
     */
    int countUniqueClients(final List<Transaction> transactions) {
        final Set<String> clients = new HashSet<>();
        transactions.stream()
                .filter(Transaction::isIssueSolved)
                .forEach(transaction -> {
                    clients.add(transaction.getBeneficiaryFullName());
                    clients.add(transaction.getSenderFullName());
                });

        return clients.size();
    }

    /**
     * Returns whether a client (sender or beneficiary) has at least one transaction with a compliance
     * issue that has not been solved
     */
    boolean hasOpenComplianceIssues(final String clientFullName, final List<Transaction> transactions) {
        if (isBlank(clientFullName)) {
            throw new OperationException("Operation is not allowed with null or empty client name");
        }

        final String sanitizedClientName = trim(clientFullName);
        return transactions.stream()
                .anyMatch(transaction -> !transaction.isIssueSolved() &&
                        (transaction.getSenderFullName().equalsIgnoreCase(sanitizedClientName)
                                || transaction.getBeneficiaryFullName().equalsIgnoreCase(sanitizedClientName))
                );

    }

    /**
     * Returns all transactions indexed by beneficiary name
     */
    Map<String, List<Transaction>> getTransactionsByBeneficiaryName(final List<Transaction> transactions) {
        final List<Transaction> sortedTransactionsByName = transactions.stream()
                .sorted(comparing(Transaction::getBeneficiaryFullName))
                .collect(toList());
        final Map<String, List<Transaction>> beneficiaryMap = new HashMap<>();

        if (!sortedTransactionsByName.isEmpty()) {
            int lastIndex = 0;
            String lastBeneficiaryName = sortedTransactionsByName.get(lastIndex).getBeneficiaryFullName();
            for (int i = 1; i < sortedTransactionsByName.size(); i++) {
                if (!lastBeneficiaryName.equalsIgnoreCase(sortedTransactionsByName.get(i).getBeneficiaryFullName())) {
                    beneficiaryMap.put(lastBeneficiaryName, sortedTransactionsByName.subList(lastIndex, i));
                    lastBeneficiaryName = sortedTransactionsByName.get(i).getBeneficiaryFullName();
                    lastIndex = i;
                }
            }
            beneficiaryMap.put(lastBeneficiaryName, sortedTransactionsByName.subList(lastIndex, sortedTransactionsByName.size()));
        }
        return beneficiaryMap;
    }

    /**
     * Returns the identifiers of all open compliance issues
     */
    Set<Long> getUnsolvedIssueIds(final List<Transaction> transactions) {
        return transactions.stream()
                .filter(transaction -> !transaction.isIssueSolved())
                .map(Transaction::getIssueId)
                .collect(toSet());
    }

    /**
     * Returns a list of all solved issue messages
     */
    List<String> getAllSolvedIssueMessages(final List<Transaction> transactions) {
        return transactions.stream()
                .filter(transaction -> !isEmpty(transaction.getIssueMessage()) && transaction.isIssueSolved())
                .map(Transaction::getIssueMessage)
                .collect(toList());
    }

    /**
     * Returns the 3 transactions with the highest amount sorted by amount descending
     */
    List<Transaction> getTop3TransactionsByAmount(final List<Transaction> transactions) {
        final List<Transaction> sortedTransaction = transactions
                .stream()
                .distinct()
                .sorted(comparingDouble(Transaction::getAmount).reversed())
                .collect(toList());

        return sortedTransaction.size() > 3
                ? sortedTransaction.subList(0, 3)
                : sortedTransaction.subList(0, sortedTransaction.size());
    }

    /**
     * Returns the senderFullName of the sender with the most total sent amount
     */
    Optional<String> getTopSender(final List<Transaction> transactions) {
        final Map<String, Double> senderTransactionAmountMap = new HashMap<>();
        final List<Transaction> successfulTransactions = transactions.stream()
                .filter(Transaction::isIssueSolved)
                .collect(toList());

        double transactionsTotalAmount;
        for (final Transaction transaction : successfulTransactions) {
            transactionsTotalAmount = senderTransactionAmountMap.compute(transaction.getSenderFullName(),
                    (k, v) -> v == null ? transaction.getAmount() : transaction.getAmount() + v);
            senderTransactionAmountMap.put(transaction.getSenderFullName(), transactionsTotalAmount);
        }

        Optional<Map.Entry<String, Double>> optionalEntry = senderTransactionAmountMap.entrySet()
                .stream()
                .max(comparingDouble(Map.Entry::getValue));

        return optionalEntry.isEmpty()
                ? Optional.empty()
                : Optional.of(optionalEntry.get().getKey());
    }

    private double sumTransactionAmounts(final List<Transaction> transactions) {
        double transactionsTotalAmount = 0;
        for (Transaction transaction : transactions) {
            transactionsTotalAmount += transaction.getAmount();
        }

        return transactionsTotalAmount;
    }
}
