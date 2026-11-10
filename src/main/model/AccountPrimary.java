package main.model;

public class AccountPrimary {
    private final String accountName;
    private final double amount;
    private final String status;

    public AccountPrimary(String accountName, double amount, String status) {
        this.accountName = accountName;
        this.amount = amount;
        this.status = status;
    }

    public String getAccountName() {
        return accountName;
    }

    public double getAmount() {
        return amount;
    }

    public String getStatus() {
        return status;
    }
}
