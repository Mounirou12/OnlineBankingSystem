package main.model;

public class Account {
    private final int id;
    private final int userId;
    private final String accountNumber;
    private final String accountType;
    private final double balance;
    private final String status;

    public Account(int id, int userId, String accountNumber, String accountType, double balance, String status) {
        this.id = id;
        this.userId = userId;
        this.accountNumber = accountNumber;
        this.accountType = accountType;
        this.balance = balance;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getAccountType() {
        return accountType;
    }

    public double getBalance() {
        return balance;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "Account [Id()=" + getId() + ", AccountNumber()="
                + getAccountNumber() + ", AccountType()=" + getAccountType() + ", Balance()=" + getBalance()
                + ", Status()=" + getStatus() + "]";
    }
}
