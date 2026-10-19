package main.controller;

import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;

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
            stmt.setString(4,BCrypt.hashpw(passwordHash,BCrypt.gensalt()));
            stmt.setString(5,phone);
            return  stmt.executeUpdate() >0;
        } catch (SQLException e) {
            return false;
        }
    }


    public static boolean connexion(String identifiant ,String passwordHash){
        // 1. On cherche l'utilisateur par email OU firstName (sans le mot de passe)
        String queryCn = "SELECT passwordHash FROM Users "
                      + "WHERE email = ? OR firstName = ?";
        try (Connection conn = DriverManager.getConnection(URL,USERNAME,PASSWORD);
        PreparedStatement stmt = conn.prepareStatement(queryCn)){

            stmt.setString(1, identifiant); // Pour l'email
            stmt.setString(2, identifiant); // Pour le prénom seul

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()){
                    String hasCode = rs.getString("passwordHash");
                    return BCrypt.checkpw(passwordHash,hasCode);
                }
                return  false;
            }
        }catch (SQLException e) {
            System.err.println("Erreur lors de la vérification en base de données : " + e.getMessage());
            return false;
        }
    }

    public static void main(String[] args) {
       // System.out.println(insertUser("Wislon","Mounirou","wilsonmounirou@gmail.com","mounirou10","+22990123474"));
        System.out.println(connexion("wilsonmounirou@gmail.com","mouniou10"));
    }
}
