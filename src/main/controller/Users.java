package main.controller;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Users {

    private static final String URL = "jdbc:mysql://127.0.0.1:3306/OnlineBanking";
    private static final String USERNAME = "mounir";
    private static final String PASSWORD = "agnila10";

    public static boolean insertUser(String firstName,String lastName,String email,String passwordHash,String phone){
        String queryCR = "INSERT INTO Users(firstName,lastName,email,passwordHash,phone) VALUES (?,?,?,?,?)";
        try (Connection conn = DriverManager.getConnection(URL,USERNAME,PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(queryCR)){
            stmt.setString(1,firstName);
            stmt.setString(2,lastName);
            stmt.setString(3,email);
            stmt.setString(4,passwordHash);
            stmt.setString(5,phone);
            return  stmt.executeUpdate() >0;
        } catch (SQLException e) {
            return false;
        }
    }

    public static void main(String[] args) {
        System.out.println(insertUser("Wislon","Mounirou","wilsonmounirou@gmail.com","mounirou10","+22990123474"));
    }
}
