package main.controller;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;

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
            stmt.setLong(4, balance);
            stmt.setString(5, status);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public static void main(String[] args) {
        System.out.println(insertAccount(3,"","EPARGNE",25000,"ACTIF"));
    }
}
