package main.controller;

import main.model.Transaction;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Transactions {

    private static final String URL = "jdbc:mysql://127.0.0.1:3306/OnlineBanking";
    private static final String USERNAME = "mounir";
    private static final String PASSWORD = "agnila10";

    private static String getAccountOwnerName(String accountNumber) {
        String queryAOW = "SELECT u.firstName, u.lastName FROM Users u " +
                "JOIN Accounts a ON u.id = a.userId " +
                "WHERE a.accountNumber = ?  LIMIT 1";
        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                PreparedStatement stmt = conn.prepareStatement(queryAOW)) {
            stmt.setString(1, accountNumber);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("firstName") + " " + rs.getString("lastName");
                }
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la recherche du propriétaire : " + e.getMessage());
        }
        return null;
    }

    private static String getSourceAccountIdName(int sourceAccountId) {
        String queryAOW = "SELECT u.firstName, u.lastName FROM Users u " +
                "JOIN Accounts a ON u.id = a.userId " +
                "WHERE a.id = ?  LIMIT 1";
        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                PreparedStatement stmt = conn.prepareStatement(queryAOW)) {
            stmt.setInt(1, sourceAccountId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("firstName") + " " + rs.getString("lastName");
                }
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la recherche du propriétaire : " + e.getMessage());
        }
        return null;
    }

    private static boolean transferMoney(int sourceAccountId, String targetAccountNumber, double amount) {
        // 1. Vérifications de base
        if (amount <= 0) {
            System.out.println("Le montant doit etre superieur à 0.");
            return false;
        }
        String targetAccountIdStr = null;
        String targetName = getAccountOwnerName(targetAccountNumber);
        String sourceName = getSourceAccountIdName(sourceAccountId);

        if (targetName == null) {
            System.out.println("Erreur : Le numéro de compte destinataire n'existe pas.");
            return false;
        }

        String queryFT = "SELECT id FROM Accounts WHERE accountNumber = ? ";
        int targetAccountId = -1;
        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                PreparedStatement stmt = conn.prepareStatement(queryFT)) {
            stmt.setString(1, targetAccountNumber);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next())
                    targetAccountId = rs.getInt("id");
            }
        } catch (SQLException e) {
            return false;
        }
        if (sourceAccountId == targetAccountId) {
            System.out.println("Erreur:Impossible de faire un virement vers soi-même.");
            return false;
        }

        Connection conn = null;
        try {
            conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            conn.setAutoCommit(false);

            String checkSource = "SELECT balance, status FROM Accounts where id =?";
            try (PreparedStatement stmt = conn.prepareStatement(checkSource)) {
                stmt.setInt(1, sourceAccountId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (!rs.next())
                        return false;
                    if (!"ACTIF".equals(rs.getString("status"))) {
                        System.out.println("Erreur : Compte source bloqué.");
                        return false;
                    }
                    if (rs.getDouble("balance") < amount) {
                        System.out.println("Erreur : Solde insuffisant.");
                        return false;
                    }
                }
            }
            // B. Débiter la source
            String debit = "UPDATE Accounts SET balance = balance - ? WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(debit)) {
                stmt.setDouble(1, amount);
                stmt.setInt(2, sourceAccountId);
                stmt.executeUpdate();
            }

            // C. Créditer la cible
            String credit = "UPDATE Accounts SET balance = balance + ? WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(credit)) {
                stmt.setDouble(1, amount);
                stmt.setInt(2, targetAccountId);
                stmt.executeUpdate();
            }

            // D. Enregistrer la transaction (Débit)
            String insertTxD = "INSERT INTO Transactions (accountId, type, amount, recipientName, status) VALUES (?, 'DEBIT', ?, ?, 'COMPLETED')";
            try (PreparedStatement stmt = conn.prepareStatement(insertTxD)) {
                stmt.setInt(1, sourceAccountId);
                stmt.setDouble(2, amount);
                stmt.setString(3, targetName); // On utilise le nom récupéré automatiquement
                stmt.executeUpdate();
            }

            // E. ENregistrer la transaction (Credit)
            String insertTxC = "INSERT INTO Transactions (accountId, type, amount, recipientName, status) VALUES (?, 'CREDIT', ?, ?, 'COMPLETED')";
            try (PreparedStatement stmt = conn.prepareStatement(insertTxC)) {
                stmt.setInt(1, targetAccountId);
                stmt.setDouble(2, amount);
                stmt.setString(3, sourceName);
                stmt.executeUpdate();
            }
            conn.commit(); // On valide tout
            return true;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Transaction> getAllTransactionsByUserId(int userId) {
        List<Transaction> transactions = new ArrayList<>();
        String queryATU = "SELECT t.id, t.transactionDate, t.amount, t.type, t.recipientName, t.status, a.accountNumber "
                +
                "FROM Transactions t " +
                "JOIN Accounts a ON t.accountId = a.id " +
                "JOIN Users u ON a.userId = u.id " +
                "WHERE u.id = ? " +
                "ORDER BY t.transactionDate DESC";
        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                PreparedStatement stmt = conn.prepareStatement(queryATU)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    transactions.add(new Transaction(
                            rs.getInt("id"),
                            rs.getDate("transactionDate"),
                            rs.getDouble("amount"),
                            rs.getString("type"),
                            rs.getString("recipientName"),
                            rs.getString("status"),
                            rs.getString("accountNumber")));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return transactions;
    }

    public static void filtrerTransactions(int userId, LocalDate dateDebut, LocalDate dateFin, String type,
            Double montantMin) {

        // On utilise un StringBuilder pour construire la requête au fur et à mesure
        StringBuilder query = new StringBuilder(
                "SELECT t.id, t.transactionDate, t.amount, t.type, t.recipientName, t.status " +
                        "FROM Transactions t " +
                        "JOIN Accounts a ON t.accountId = a.id " +
                        "WHERE a.userId = ? " // ⚠️ Toujours filtrer par l'utilisateur connecté en premier
        );

        // On prépare une liste pour stocker les valeurs des paramètres (les "?")
        List<Object> parametres = new ArrayList<>();
        parametres.add(userId); // Le premier "?" est toujours l'userId

        // --- Ajout des filtres dynamiques ---

        if (dateDebut != null) {
            query.append(" AND DATE(t.transactionDate) >= ?");
            parametres.add(dateDebut);
        }
        if (dateFin != null) {
            query.append(" AND DATE(t.transactionDate) <= ?");
            parametres.add(dateFin);
        }
        if (type != null && !type.trim().isEmpty()) {
            query.append(" AND t.type = ?");
            parametres.add(type);
        }
        if (montantMin != null) {
            query.append(" AND t.amount >= ?");
            parametres.add(montantMin);
        }

        // Optionnel : Toujours trier par date récente
        query.append(" ORDER BY t.transactionDate DESC");

        // --- Exécution de la requête ---
        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                PreparedStatement stmt = conn.prepareStatement(query.toString())) {

            // On remplit les "?" avec la liste de paramètres
            for (int i = 0; i < parametres.size(); i++) {
                stmt.setObject(i + 1, parametres.get(i));
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    // Ici, tu traites les résultats (afficher dans le tableau Swing)
                    System.out.println(
                            rs.getInt("id") + " | " + rs.getDate("transactionDate") + " | " + rs.getDouble("amount"));
                }
            }
        } catch (SQLException e) {
            System.out.println("Erreur : " + e.getMessage());
        }
    }

//    public static void main(String[] args) {
//        System.out.println("=== TEST DE VIREMENT INTERNE ===");
//        /*
//         * int compteSource = 1; // Le compte d'Alex (ACTIF)
//         * String compteDestinataire = "4423 1727 3619 7604"; // Le compte de Taylor
//         * (BLOQUÉ)
//         * double montant = 2500;
//         * String nomDestinataire = getAccountOwnerName(compteDestinataire);
//         * String nomEnvoyeur = getSourceAccountIdName(compteSource);
//         * if (nomDestinataire != null) {
//         * System.out.println("Recherche : Compte trouvé. Bénéficiaire : " +
//         * nomDestinataire);
//         * System.out.println("Recherche : Proprietaire trouve. Debiteur : " +
//         * nomEnvoyeur);
//         * System.out.println("L'utilisateur clique maintenant sur 'Envoyer'...\n");
//         *
//         * // 2. Étape de transfert (le clic sur le bouton)
//         * boolean succes = transferMoney(compteSource, compteDestinataire, montant);
//         *
//         * if (succes) {
//         * System.out.println("✅ Virement effectué avec succès !");
//         * } else {
//         * // C'est ici que l'interface afficherait le popup d'erreur
//         * System.out.
//         * println("❌ Échec du virement. (Voir le message d'erreur ci-dessus).");
//         * }
//         * } else {
//         * System.out.println("❌ Numéro de compte introuvable.");
//         * }
//         */
//        List<Transaction> transactions = getAllTransactionsByUserId(3);
//        transactions.forEach(System.out::println);
//    }
}
