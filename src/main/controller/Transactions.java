package main.controller;

import java.sql.*;

public class Transactions {

    private static final String URL = "jdbc:mysql://127.0.0.1:3306/OnlineBanking";
    private static final String USERNAME = "mounir";
    private static final String PASSWORD = "agnila10";

    private static boolean transferMoney(int sourceAccountId, int targetAccountId, double amount,
            String recipientName) {
        // 1. Vérifications de base
        if (amount <= 0) {
            System.out.println("Le montant doit etre superieur à 0.");
            return false;
        }
        if (sourceAccountId == targetAccountId) {
            System.out.println("Les comptes doivent etre different à 0.");
            return false;
        }

        Connection conn = null;

        try {
            conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            conn.setAutoCommit(false);// ⚠️ ÉTAPE CRUCIALE : On désactive l'auto-commit pour gérer la transaction
                                      // manuellement

            // 2. Vérifier que le compte source est ACTIF et a assez d'argent
            String checkSourceQuerry = "SELECT balance,status FROM Accounts WHERE id=?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSourceQuerry)) {
                checkStmt.setInt(1, sourceAccountId);
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (!rs.next()) {
                        System.out.println("Compte source introuvable.");
                        conn.rollback();
                        return false;
                    }
                    String status = rs.getString("status");
                    double sourceBalance = rs.getDouble("balance");

                    if (!"ACTIF".equals(status)) {
                        System.out.println("Le compte source est bloqué.");
                        conn.rollback();
                        return false;
                    }
                    if (sourceBalance < amount) {
                        System.out.println("Solde insuffisant.");
                        conn.rollback();
                        return false;
                    }
                }
            }
            // 3. Débiter le compte source
            String debitQuery = "UPDATE Accounts SET balance = balance - ? WHERE id = ?";
            try (PreparedStatement debitStmt = conn.prepareStatement(debitQuery)) {
                debitStmt.setDouble(1, amount);
                debitStmt.setInt(2, sourceAccountId);
                debitStmt.executeUpdate();
            }

            // 4. Créditer le compte destinataire (si c'est un compte interne)
            String crediQuerry = "UPDATE Accounts SET balance = balance + ? WHERE id = ?";
            try (PreparedStatement debitStmt = conn.prepareStatement(crediQuerry)) {
                debitStmt.setDouble(1, amount);
                debitStmt.setInt(2, targetAccountId);
                debitStmt.executeUpdate();
            }

            // 5. Enregistrer la transaction dans l'historique (Débit pour la source)
            String insertTransactionQuery = "INSERT INTO Transactions (accountId, type, amount, recipientName, status) "
                    + "VALUES (?, 'TRANSFERT', ?, ?, 'COMPLETED')";
            try (PreparedStatement txStmt = conn.prepareStatement(insertTransactionQuery)) {
                txStmt.setInt(1, sourceAccountId);
                txStmt.setDouble(2, amount);
                txStmt.setString(3, recipientName);
                txStmt.executeUpdate();
            }

            // Optionnel : Enregistrer aussi la réception (Crédit) pour le destinataire
            String insertReceivedQuery = "INSERT INTO Transactions (accountId, type, amount, recipientName, status) "
                    + "VALUES (?, 'RECEPTION', ?, 'Virement reçu', 'COMPLETED')";
            try (PreparedStatement rxStmt = conn.prepareStatement(insertReceivedQuery)) {
                rxStmt.setInt(1, targetAccountId);
                rxStmt.setDouble(2, amount);
                rxStmt.executeUpdate();
            }
            // ⚠️ ÉTAPE CRUCIALE : Si tout s'est bien passé, on valide la transaction
            conn.commit();
            System.out.println("Transfert de " + amount + "€ effectué avec succès.");
            return true;
        } catch (SQLException e) {
            System.out.println("Erreur lors du transfert : " + e.getMessage());
            // ⚠️ En cas d'erreur, on annule tout ce qui a été fait (annulation du débit)
            if (conn != null) {
                try {
                    conn.rollback();
                    System.out.println("Transaction annulée (rollback).");
                } catch (SQLException ex) {
                    System.out.println("Erreur de rollback : " + ex.getMessage());
                }
            }
            return false;
        } finally {
            // On ferme la connexion et on remet l'auto-commit à true
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    System.out.println("Erreur lors de la fermeture : " + e.getMessage());
                }
            }
        }
    }

    public static void main(String[] args) {
        System.out.println(transferMoney(1, 3, 5000, "John Doe"));
    }
}
