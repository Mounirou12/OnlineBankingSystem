package main.controller;

import java.time.LocalDate;

public class Session {
    private static int currentUserId = -1;
    private static String currentUserFirstName = null;
    private static String currentUserEmail = null;
    private static String currentUserLastName = null;
    private static String currentUserPhone = null;
    private static LocalDate currentDateNaissance = null;
    private static String currentUserSexe = null;
    private static String currentUserAdresse = null;

    public static void setCurrentUser(int id, String firstName,String lastName, String email,String phone, LocalDate dateNaissance, String sexe, String adresse) {
        currentUserId = id;
        currentUserFirstName = firstName;
        currentUserLastName = lastName;
        currentUserEmail = email;
        currentUserPhone = phone;
        currentDateNaissance = dateNaissance;
        currentUserSexe = sexe;
        currentUserAdresse = adresse;

    }

    public static void clear() {
        currentUserId = -1;
        currentUserFirstName = null;
        currentUserEmail = null;
        currentUserLastName = null;
        currentUserPhone = null;
        currentDateNaissance = null;
        currentUserSexe = null;
        currentUserAdresse = null;
    }

    public static int getCurrentUserId() {
        return currentUserId;
    }

    public static String getCurrentUserFirstName() {
        return currentUserFirstName;
    }

    public static String getCurrentUserEmail() {
        return currentUserEmail;
    }

    public static boolean isloggesIn() {
        return currentUserId != -1;
    }

    public static String getCurrentUserLastName() {
        return currentUserLastName;
    }

    public static String getCurrentUserPhone() {
        return currentUserPhone;
    }

    public static LocalDate getCurrentDateNaissance() {
        return currentDateNaissance;
    }

    public static String getCurrentUserSexe() {
        return currentUserSexe;
    }

    public static String getCurrentUserAdresse() {
        return currentUserAdresse;
    }
}
