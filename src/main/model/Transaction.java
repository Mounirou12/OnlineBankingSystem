package main.model;

import java.util.Date;

public class Transaction {
    private final Date transactionDate;
    private final double amount;
    private final String type;
    private final String destinataire;
    private final String status;
    private final String compte;

    public Transaction(Date transactionDate, double amount, String type, String destinataire, String status,
            String compte) {
        this.transactionDate = transactionDate;
        this.amount = amount;
        this.type = type;
        this.destinataire = destinataire;
        this.status = status;
        this.compte = compte;
    }

    public Date getTransactionDate() {
        return transactionDate;
    }

    public double getAmount() {
        return amount;
    }

    public String getType() {
        return type;
    }

    public String getDestinataire() {
        return destinataire;
    }

    public String getStatus() {
        return status;
    }

    public String getCompte() {
        return compte;
    }

    @Override
    public String toString() {
        return "getTransactionDate()=" + getTransactionDate() + ", getAmount()="
                + getAmount() + ", getType()=" + getType() + ", getDestinataire()=" + getDestinataire()
                + ", getStatus()=" + getStatus() + ", getCompte()=" + getCompte() + "]";
    }
}
