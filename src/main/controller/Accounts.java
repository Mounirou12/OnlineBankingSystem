package main.controller;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;
import main.model.Account;

public class Accounts {
    private static final String URL = "jdbc:mysql://127.0.0.1:3306/OnlineBanking";
    private static final String USERNAME = "mounir";
    private static final String PASSWORD = "agnila10";

    public static boolean insertAccount(int userId, String accountNumber, String accountType, long balance,
            String status) {
        String queryCr = "INSERT INTO Accounts (userId,accountNumber,accountType,balance,status) VALUES (?,?,?,?,?)";
        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                PreparedStatement stmt = conn.prepareStatement(queryCr)) {

            Function<Integer, String> generateAccountNumber = length -> new Random().ints(length, 0, 10)
                    .mapToObj(String::valueOf)
                    .collect(Collectors.joining())
                    .replaceAll("(.{4})(?!$)", "$1 ");

            accountNumber = generateAccountNumber.apply(16);
            stmt.setInt(1, userId);
            stmt.setString(2, accountNumber);
            stmt.setString(3, accountType);
            stmt.setDouble(4, balance);
            stmt.setString(5, status);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public static List<Account> getAllAccountsByUserId(int userId) {
        List<Account> accounts = new ArrayList<>();
        String queryRDBU = "SELECT id, userId, accountNumber, accountType, balance, status  FROM Accounts WHERE userId = ?";
        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                PreparedStatement stmt = conn.prepareStatement(queryRDBU)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String rawAccountNumber = rs.getString("accountNumber");
                    String maskedNumber = "**** "
                            + rawAccountNumber.substring(Math.max(0, rawAccountNumber.length() - 4));
                    accounts.add(new Account(
                            rs.getInt("id"),
                            rs.getInt("userId"),
                            maskedNumber,
                            rs.getString("accountType"),
                            rs.getDouble("balance"),
                            rs.getString("status")));
                }
            }

        } catch (SQLException e) {
            System.out.println("L'utilisateur ne possede pas de compte" + e.getMessage());
        }
        return accounts;
    }

    public static Account getAccountByIdAndUserId(int id, int userId) {
        String queryRID = "SELECT id,userId,accountNumber, accountType, balance, status FROM Accounts WHERE id = ? AND userId = ?;";
        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                PreparedStatement stmt = conn.prepareStatement(queryRID)) {
            stmt.setInt(1, id);
            stmt.setInt(2, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Account(
                            rs.getInt("id"),
                            rs.getInt("userId"),
                            rs.getString("accountNumber"),
                            rs.getString("accountType"),
                            rs.getDouble("balance"),
                            rs.getString("status"));
                }
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération du compte : " + e.getMessage());
        }
        return null;
    }

    public static void main(String[] args) {
        System.out.println(insertAccount(4, "", "COURANT", 30000, "ACTIF"));
        List<Account> accounts = getAllAccountsByUserId(3);
        accounts.forEach(System.out::println);
        System.out.println(getAccountByIdAndUserId(1, 3));
    }
}
