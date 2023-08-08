package com.smallworld;

import com.smallworld.data.Transaction;
import com.smallworld.exception.OperationException;
import com.smallworld.util.JsonParser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static java.text.MessageFormat.format;
import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TransactionDataFetcherTest {

    private final TransactionDataFetcher transactionDataFetcher = new TransactionDataFetcher();
    private final JsonParser jsonParser = new JsonParser();


    @Test
    void shouldReturnAvailableTopTransactionsOnMinTransactionSize() {
        List<Transaction> transactions = buildTransaction("test_transactions.json");
        List<Transaction> topTransactionsByAmount = transactionDataFetcher.getTop3TransactionsByAmount(transactions.subList(0, 2));
        assertEquals(1, topTransactionsByAmount.size());
        assertEquals(550.2, topTransactionsByAmount.get(0).getAmount());
    }

    @Test
    void shouldNotReturnTopTransactionOnEmptyList() {
        List<Transaction> transactions = buildTransaction("test_transactions.json");
        List<Transaction> topTransactionsByAmount = transactionDataFetcher.getTop3TransactionsByAmount(emptyList());
        assertEquals(0, topTransactionsByAmount.size());
    }

    @Test
    void shouldReturnTop3TransactionsByAmount() {
        List<Transaction> transactions = buildTransaction("test_transactions.json");
        List<Transaction> top3SortedTransactions = transactionDataFetcher.getTop3TransactionsByAmount(transactions);
        assertEquals(3, top3SortedTransactions.size());
        assertEquals(985.0, top3SortedTransactions.get(0).getAmount());
        assertEquals(550.2, top3SortedTransactions.get(1).getAmount());
        assertEquals(430.2, top3SortedTransactions.get(2).getAmount());

    }

    @Test
    void shouldReturnAllSolvedIssueMessages() {
        List<Transaction> transactions = buildTransaction("test_transactions.json");
        List<String> solvedIssueMessages = transactionDataFetcher.getAllSolvedIssueMessages(transactions);
        assertEquals(1, solvedIssueMessages.size());
        assertEquals("Never gonna give you up", solvedIssueMessages.get(0));
    }

    @Test
    void shouldReturnUnsolvedIssueIds() {
        List<Transaction> transactions = buildTransaction("test_transactions.json");
        Set<Long> openIssueIDs = transactionDataFetcher.getUnsolvedIssueIds(transactions);
        assertEquals(3, openIssueIDs.size());
        assertTrue(openIssueIDs.contains(1L));
        assertTrue(openIssueIDs.contains(3L));
        assertTrue(openIssueIDs.contains(15L));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   "})
    void shouldThrowExceptionOnEmptyClientName(final String clientName) {
        List<Transaction> transactions = buildTransaction("test_transactions.json");
        assertThrows(OperationException.class, () -> transactionDataFetcher.hasOpenComplianceIssues(clientName, transactions));
    }

    @Test
    void shouldThrowExceptionWithNullClientName() {
        List<Transaction> transactions = buildTransaction("test_transactions.json");
        assertThrows(OperationException.class, () -> transactionDataFetcher.hasOpenComplianceIssues(null, transactions));
    }

    @ParameterizedTest
    @CsvSource(value = {"Tom Shelby:true", "Aberama Gold:false"}, delimiter = ':')
    void shouldReturnTrueForClientNames(final String clientName, final boolean expectedValue) {
        List<Transaction> transactions = buildTransaction("test_transactions.json");
        assertEquals(expectedValue, transactionDataFetcher.hasOpenComplianceIssues(clientName, transactions));
    }

    @ParameterizedTest
    @CsvSource(value = {" Tom Shelby:true ", " Aberama Gold :false"}, delimiter = ':')
    void shouldReturnTrueForClientNamesWithWhiteSpaces(final String clientName, final boolean expectedValue) {
        List<Transaction> transactions = buildTransaction("test_transactions.json");
        assertEquals(expectedValue, transactionDataFetcher.hasOpenComplianceIssues(clientName, transactions));
    }

    @ParameterizedTest
    @CsvSource(value = {"550.2:test_transactions.json", "0.0:"}, delimiter = ':')
    void shouldReturnMaxTransactionAmountAndZeroOnNoTransaction(final double expectedValue, final String dataFileName) {
        List<Transaction> transactions = buildTransaction(dataFileName);
        assertEquals(expectedValue, transactionDataFetcher.getMaxTransactionAmount(transactions));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   "})
    void shouldThrowExceptionOnEmptySenderName(final String senderName) {
        List<Transaction> transactions = buildTransaction("test_transactions.json");
        assertThrows(OperationException.class, () -> transactionDataFetcher.getTotalTransactionAmountSentBy(senderName, transactions));
    }

    @Test
    void shouldThrowExceptionWithNullSenderName() {
        List<Transaction> transactions = buildTransaction("test_transactions.json");
        assertThrows(OperationException.class, () -> transactionDataFetcher.getTotalTransactionAmountSentBy(null, transactions));
    }

    @ParameterizedTest
    @CsvSource(value = {"Tom Shelby:600.0", " Tom Shelby :600.0"}, delimiter = ':')
    void shouldReturnTotalTransactionAmountSentBy(final String senderName, final double expectedValue) {
        List<Transaction> transactions = buildTransaction("test_transactions.json");
        assertEquals(expectedValue, transactionDataFetcher.getTotalTransactionAmountSentBy(senderName, transactions));
    }

    @ParameterizedTest
    @CsvSource(value = {"Tom Cruise:0:test_transactions.json", "Tom Shelby:0:"}, delimiter = ':')
    void shouldReturnZeroTransactionAmountSentByOnNoTransaction(final String senderName, final double expectedValue, final String dataFileName) {
        List<Transaction> transactions = buildTransaction(dataFileName);
        assertEquals(expectedValue, transactionDataFetcher.getTotalTransactionAmountSentBy(senderName, transactions));
    }

    @ParameterizedTest
    @CsvSource(value = {"667.8:test_transactions.json", "0.0:"}, delimiter = ':')
    void shouldReturnTotalTransactionsAmount(final double expectedValue, final String dataFileName) {
        List<Transaction> transactions = buildTransaction(dataFileName);
        assertEquals(expectedValue, transactionDataFetcher.getTotalTransactionAmount(transactions));
    }

    @Test
    void shouldReturnAllTransactionByBeneficiary() {
        List<Transaction> transactions = buildTransaction("test_transactions.json");
        Map<String, List<Transaction>> beneficiaryTransaction = transactionDataFetcher.getTransactionsByBeneficiaryName(transactions);
        assertEquals(4, beneficiaryTransaction.size());
        assertEquals(2, beneficiaryTransaction.get("Ben Younger").size());
        assertEquals(3, beneficiaryTransaction.get("Arthur Shelby").size());
        assertEquals(1, beneficiaryTransaction.get("Aberama Gold").size());
        assertEquals(1, beneficiaryTransaction.get("Alfie Solomons").size());
    }

    @Test
    void shouldNotReturnAnyTransactionByBeneficiaryOnEmptyList() {
        Map<String, List<Transaction>> beneficiaryTransaction = transactionDataFetcher.getTransactionsByBeneficiaryName(emptyList());
        assertEquals(0, beneficiaryTransaction.size());
    }

    @ParameterizedTest
    @CsvSource(value = {"4:test_transactions.json", "0:"}, delimiter = ':')
    void shouldReturnUniqueClientsCount(final int expectedValue, final String datafileName) {
        List<Transaction> transactions = buildTransaction(datafileName);
        assertEquals(expectedValue, transactionDataFetcher.countUniqueClients(transactions));
    }

    @Test
    void shouldReturnTopSenderName() {
        List<Transaction> transactions = buildTransaction("test_transactions.json");
        Optional<String> senderOptional = transactionDataFetcher.getTopSender(transactions);
        assertTrue(senderOptional.isPresent());
        assertEquals("Tom Shelby", senderOptional.get());
    }

    @Test
    void shouldNotReturnTopSenderNameOnEmptyList() {
        Optional<String> senderOptional = transactionDataFetcher.getTopSender(emptyList());
        assertTrue(senderOptional.isEmpty());
    }

    private List<Transaction> buildTransaction(final String fileName) {
        List<Transaction> transactions = new ArrayList<>();
        try {
            final Path filePath = Path.of("src/test/resources/" + fileName);
            String jsonString = Files.readString(filePath);
            transactions = Arrays.asList(jsonParser.toObject(jsonString, Transaction[].class));
        } catch (IOException | ParseException ex) {
            System.out.println(format("Failed to read file {0}", fileName));
            System.out.println("Returning empty transaction list");
        }
        return transactions;
    }
}
