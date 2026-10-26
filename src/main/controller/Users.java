package main.controller;


import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;

public class Users {

    private static final String URL = "jdbc:mysql://127.0.0.1:3306/OnlineBanking";
    private static final String USERNAME = "mounir";
    private static final String PASSWORD = "agnila10";

    public static boolean insertUser(String firstName, String lastName, String email, String passwordHash,
            String phone) {
        String queryCR = "INSERT INTO Users(firstName,lastName,email,passwordHash,phone) VALUES (?,?,?,?,?)";
        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                PreparedStatement stmt = conn.prepareStatement(queryCR)) {
            stmt.setString(1, firstName);
            stmt.setString(2, lastName);
            stmt.setString(3, email);
            stmt.setString(4, BCrypt.hashpw(passwordHash, BCrypt.gensalt()));
            stmt.setString(5, phone);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public static boolean connexion(String identifiant, String passwordHash) {
        // 1. On cherche l'utilisateur par email OU firstName (sans le mot de passe)
        String queryCn = "SELECT passwordHash FROM Users "
                + "WHERE email = ? OR firstName = ?";
        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                PreparedStatement stmt = conn.prepareStatement(queryCn)) {

            stmt.setString(1, identifiant); // Pour l'email
            stmt.setString(2, identifiant); // Pour le prénom seul

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String hasCode = rs.getString("passwordHash");
                    return BCrypt.checkpw(passwordHash, hasCode);
                }
                return false;
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la vérification en base de données : " + e.getMessage());
            return false;
        }
    }

    public static boolean updateProfilUser(int id, String firstName, String lastName, String email, String phone) {
        String queryUPU = "UPDATE Users SET firstName=?,lastName=?,email=?,phone=? WHERE id=?";
        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                PreparedStatement stmt = conn.prepareStatement(queryUPU)) {
            stmt.setString(1, firstName);
            stmt.setString(2, lastName);
            stmt.setString(3, email);
            stmt.setString(4, phone);
            stmt.setInt(5, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Erreur lors de la vérification en base de données : " + e.getMessage());
            return false;
        }
    }

    public static String passwordChange(int userId, String oldPassword, String newPassword, String confirmPassword) {

        // ÉTAPE 1 : Vérifier si le nouveau mot de passe et la confirmation sont
        // identiques
        if (newPassword == null || !newPassword.equals(confirmPassword)) {
            return "ÉCHEC : La confirmation ne correspond pas au nouveau mot de passe. Veuillez reprendre le processus.";
        }
        // Connexion à la base de données
        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {

            // ÉTAPE 2 : Vérifier si l'ancien mot de passe est correct
            String sqlVerif = "SELECT passwordHash FROM Users WHERE id = ?";
            try (PreparedStatement pstmtVerif = conn.prepareStatement(sqlVerif)) {
                pstmtVerif.setInt(1, userId);

                try (ResultSet rs = pstmtVerif.executeQuery()) {
                    if (rs.next()) {
                        String passwordStck = rs.getString("passwordHash");

                        // Sécurité : Si vous utilisez du hac]hage (ex: BCrypt), utilisez :
                        // !BCrypt.checkpw(ancienMdp, mdpStocke)
                        if (!BCrypt.checkpw(oldPassword, passwordStck)) {
                            return "ÉCHEC : Le mot de passe actuel est incorrect. Mttez le mot de passe juste";
                        }
                    } else {
                        return "ÉCHEC : Utilisateur introuvable.";
                    }
                }
            }

            // ÉTAPE 3 : Mettre à jour le mot de passe dans la base de données
            String sqlUpdate = "UPDATE Users SET passwordHash = ? WHERE id = ?";
            try (PreparedStatement pstmtUpdate = conn.prepareStatement(sqlUpdate)) {
                // Sécurité : Dans un vrai projet, hachez "nouveauMdp" avant de l'injecter (ex:
                // BCrypt.hashpw(...))
                pstmtUpdate.setString(1, BCrypt.hashpw(newPassword, BCrypt.gensalt()));
                pstmtUpdate.setInt(2, userId);

                int lignesModifiees = pstmtUpdate.executeUpdate();
                if (lignesModifiees > 0) {
                    return "SUCCÈS : Votre mot de passe a été modifié avec succès !";
                } else {
                    return "ÉCHEC : Impossible de mettre à jour le mot de passe.";
                }
            }

        } catch (SQLException e) {
            System.err.println("Erreur SQL : " + e.getMessage());
            return "ÉCHEC : Une erreur technique est survenue.";
        }
    }

//    public static void main(String[] args) {
//         System.out.println(insertUser("John","Doe","johndoe@gmail.com","john@10","+22990427494"));
//        // System.out.println(connexion("wilsonmounirou@gmail.com","mounirou10"));
//        //System.out.println(passwordChange(3, "agnil@10", "agnil@10", "agnil@10"));
//
//    }
}
