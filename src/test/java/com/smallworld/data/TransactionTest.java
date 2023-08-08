package com.smallworld.data;

import org.junit.jupiter.api.Test;

import static com.smallworld.constant.TestConstant.amount;
import static com.smallworld.constant.TestConstant.beneficiaryAge;
import static com.smallworld.constant.TestConstant.beneficiaryFullName;
import static com.smallworld.constant.TestConstant.issueId;
import static com.smallworld.constant.TestConstant.issueMessage;
import static com.smallworld.constant.TestConstant.issueSolved;
import static com.smallworld.constant.TestConstant.mtn;
import static com.smallworld.constant.TestConstant.senderAge;
import static com.smallworld.constant.TestConstant.senderFullName;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TransactionTest {

    @Test
    void shouldCreateValidObject() {
        final Transaction transaction = Transaction.Builder.newInstance()
                .setMtn(mtn)
                .setAmount(amount)
                .setSenderFullName(senderFullName)
                .setSenderAge(senderAge)
                .setBeneficiaryFullName(beneficiaryFullName)
                .setBeneficiaryAge(beneficiaryAge)
                .setIssueId(issueId)
                .setIssueMessage(issueMessage)
                .setIssueSolved(issueSolved)
                .build();

        assertEquals(mtn, transaction.getMtn());
        assertEquals(amount, transaction.getAmount());
        assertEquals(senderFullName, transaction.getSenderFullName());
        assertEquals(senderAge, transaction.getSenderAge());
        assertEquals(beneficiaryFullName, transaction.getBeneficiaryFullName());
        assertEquals(beneficiaryAge, transaction.getBeneficiaryAge());
        assertEquals(issueId, transaction.getIssueId());
        assertEquals(issueMessage, transaction.getIssueMessage());
        assertEquals(issueSolved, transaction.isIssueSolved());
    }

    @Test
    void shouldCreateObjectWithDefaults() {
        final Transaction transaction = Transaction.Builder.newInstance()
                .setMtn(mtn)
                .setAmount(amount)
                .setSenderFullName(senderFullName)
                .setSenderAge(senderAge)
                .setBeneficiaryFullName(beneficiaryFullName)
                .setBeneficiaryAge(beneficiaryAge)
                .build();

        assertEquals(mtn, transaction.getMtn());
        assertEquals(amount, transaction.getAmount());
        assertEquals(senderFullName, transaction.getSenderFullName());
        assertEquals(senderAge, transaction.getSenderAge());
        assertEquals(beneficiaryFullName, transaction.getBeneficiaryFullName());
        assertEquals(beneficiaryAge, transaction.getBeneficiaryAge());
        assertNull(transaction.getIssueId());
        assertNull(transaction.getIssueMessage());
        assertTrue(transaction.isIssueSolved());
    }
}
