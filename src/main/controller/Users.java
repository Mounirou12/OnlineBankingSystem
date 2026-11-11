package main.controller;

import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.time.LocalDate;

public class Users {

    private static final String URL = "jdbc:mysql://127.0.0.1:3306/OnlineBanking";
    private static final String USERNAME = "USER";
    private static final String PASSWORD = "MOTDEPASSE";

    public static boolean insertUser(String firstName, String lastName, String email, String passwordHash,
            String phone, LocalDate dateNaissance, String sexe, String adresse) {
        String queryCR = "INSERT INTO Users(firstName,lastName,email,passwordHash,phone,dateNaissance,sexe,adresse) VALUES (?,?,?,?,?,?,?,?)";
        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                PreparedStatement stmt = conn.prepareStatement(queryCR, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, firstName);
            stmt.setString(2, lastName);
            stmt.setString(3, email);
            stmt.setString(4, BCrypt.hashpw(passwordHash, BCrypt.gensalt()));
            stmt.setString(5, phone);
            stmt.setObject(6, dateNaissance);
            stmt.setString(7, sexe);
            stmt.setString(8, adresse);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        int newUserId = rs.getInt(1);
                        Session.setCurrentUser(
                                newUserId,
                                firstName,
                                lastName,
                                email,
                                phone,
                                dateNaissance,
                                sexe,
                                adresse);
                        return true; // Succès total
                    }
                }
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean connexion(String identifiant, String passwordHash) {
        // 1. On cherche l'utilisateur par email OU firstName (sans le mot de passe)
        String queryCn = "SELECT id,firstName,lastName,email,phone,dateNaissance,sexe,adresse,passwordHash FROM Users "
                + "WHERE email = ? OR firstName = ?";
        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                PreparedStatement stmt = conn.prepareStatement(queryCn)) {

            stmt.setString(1, identifiant); // Pour l'email
            stmt.setString(2, identifiant); // Pour le prénom seul

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String hasCode = rs.getString("passwordHash");
                    if (BCrypt.checkpw(passwordHash, hasCode)) {
                        Date sqlDateNaissance = rs.getDate("dateNaissance");
                        LocalDate dateNaissance = (sqlDateNaissance != null)
                                ? sqlDateNaissance.toLocalDate()
                                : null;
                        Session.setCurrentUser(
                                rs.getInt("id"),
                                rs.getString("firstName"),
                                rs.getString("lastName"),
                                rs.getString("email"),
                                rs.getString("phone"),
                                dateNaissance,
                                rs.getString("sexe"),
                                rs.getString("adresse"));
                        return true;
                    }
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

    public static boolean passwordChange(int userId, String oldPassword, String newPassword, String confirmPassword) {

        // ÉTAPE 1 : Vérifier si le nouveau mot de passe et la confirmation sont
        // identiques
        if (newPassword == null || !newPassword.equals(confirmPassword)) {
            return false;
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
                            return false;
                        }
                    } else {
                        return false;
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
                    return true;
                } else {
                    return false;
                }
            }

        } catch (SQLException e) {
            System.err.println("Erreur SQL : " + e.getMessage());
            return false;
        }
    }

    // public static void main(String[] args) {
    // System.out.println(insertUser("John","Doe","johndoe@gmail.com","john@10","+22990427494"));
    // // System.out.println(connexion("wilsonmounirou@gmail.com","mounirou10"));
    // //System.out.println(passwordChange(3, "agnil@10", "agnil@10", "agnil@10"));
    //
    // }
}
