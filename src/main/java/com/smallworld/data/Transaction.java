package com.smallworld.data;


import java.util.Objects;

public class Transaction {
    private Long mtn;
    private double amount;
    private String senderFullName;
    private int senderAge;
    private String beneficiaryFullName;
    private int beneficiaryAge;
    private Long issueId;
    private boolean issueSolved;
    private String issueMessage;

    private Transaction() {

    }

    private Transaction(final Builder builder) {
        this.mtn = builder.mtn;
        this.amount = builder.amount;
        this.beneficiaryFullName = builder.beneficiaryFullName;
        this.beneficiaryAge = builder.beneficiaryAge;
        this.senderFullName = builder.senderFullName;
        this.senderAge = builder.senderAge;
        this.issueId = builder.issueId;
        this.issueMessage = builder.issueMessage;
        this.issueSolved = builder.issueSolved;
    }

    @Override
    public boolean equals(final Object obj) {
        return !Objects.isNull(obj)
                && obj instanceof Transaction
                && Objects.equals(mtn, ((Transaction) obj).mtn);
    }

    @Override
    public int hashCode() {
        return Long.hashCode(mtn);
    }

    public Long getMtn() {
        return mtn;
    }

    public double getAmount() {
        return amount;
    }

    public String getSenderFullName() {
        return senderFullName;
    }

    public int getSenderAge() {
        return senderAge;
    }

    public String getBeneficiaryFullName() {
        return beneficiaryFullName;
    }

    public int getBeneficiaryAge() {
        return beneficiaryAge;
    }

    public Long getIssueId() {
        return issueId;
    }

    public boolean isIssueSolved() {
        return issueSolved;
    }

    public String getIssueMessage() {
        return issueMessage;
    }

    public static class Builder {

        private Long mtn;
        private double amount;
        private String senderFullName;
        private int senderAge;
        private String beneficiaryFullName;
        private int beneficiaryAge;
        private Long issueId;
        private boolean issueSolved = true;
        private String issueMessage;

        public static Builder newInstance() {
            return new Builder();
        }

        private Builder() {
        }

        public Transaction build() {
            return new Transaction(this);
        }

        public Builder setMtn(final Long mtn) {
            this.mtn = mtn;
            return this;
        }

        public Builder setAmount(final double amount) {
            this.amount = amount;
            return this;
        }

        public Builder setSenderFullName(final String senderFullName) {
            this.senderFullName = senderFullName;
            return this;
        }

        public Builder setSenderAge(final int senderAge) {
            this.senderAge = senderAge;
            return this;
        }

        public Builder setBeneficiaryFullName(final String beneficiaryFullName) {
            this.beneficiaryFullName = beneficiaryFullName;
            return this;
        }

        public Builder setBeneficiaryAge(final int beneficiaryAge) {
            this.beneficiaryAge = beneficiaryAge;
            return this;
        }

        public Builder setIssueId(final Long issueId) {
            this.issueId = issueId;
            return this;
        }

        public Builder setIssueSolved(final boolean issueSolved) {
            this.issueSolved = issueSolved;
            return this;
        }

        public Builder setIssueMessage(final String issueMessage) {
            this.issueMessage = issueMessage;
            return this;
        }
    }
}

