package main.controller;

public class Session {
    private static int currentUserId = -1;
    private static String currentUserFirstName = null;
    private static String currentUserEmail = null;

    public static void setCurrentUser(int id, String userName, String email) {
        currentUserId = id;
        currentUserFirstName = userName;
        currentUserEmail = email;
    }

    public static void clear() {
        currentUserId = -1;
        currentUserFirstName = null;
        currentUserEmail = null;
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
}
