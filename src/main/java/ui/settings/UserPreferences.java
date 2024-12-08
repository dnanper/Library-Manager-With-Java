package ui.settings;

import com.google.gson.Gson;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserPreferences {
    public static final String CONFIG_FILE = "user.txt";

    public class User {

        private final String username;
        private String password;
        private String fine;
        private Boolean banned;

        public User(String username, String password, String fine) {
            this.username = username;
            this.password = DigestUtils.shaHex(password);
            this.fine = fine;
            this.banned = false;
        }

        public String getFine() { return fine; }

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }

        public Boolean getBanned() { return banned; }

        public void setPassword(String password) {
            if (password.length() < 16) {
                this.password = DigestUtils.shaHex(password);
            } else {
                this.password = password;
            }
        }

        public void setBanned(Boolean banned) {
            this.banned = banned;
        }

        public void setFine(String fine) {
            this.fine = fine;
        }
    }

    /**
     * Initializes the configuration file with default user preferences.
     * It creates a new `UserPreferences` object (which may have an empty list of users internally),
     * then uses Gson to serialize it and write it to the configuration file.
     * In case of any I/O errors during the writing process, it logs the exception using the logger for this class.
     */
    public static void initConfig() {
        Writer writer = null;
        try {
            UserPreferences preference = new UserPreferences();
            Gson gson = new Gson();
            writer = new FileWriter(CONFIG_FILE);
            gson.toJson(preference, writer);
        } catch (IOException ex) {
            Logger.getLogger(UserPreferences.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                writer.close();
            } catch (IOException ex) {
                Logger.getLogger(UserPreferences.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Retrieves the user preferences from the configuration file.
     * It attempts to deserialize the `UserPreferences` object from the file using Gson.
     * If the file is not found, it logs an informational message indicating that the config file is missing
     * and proceeds to create a new one with default configuration by calling `initConfig()`.
     *
     * @return The `UserPreferences` object containing the retrieved or newly initialized user preferences.
     */
    public static UserPreferences getPreferences() {
        Gson gson = new Gson();
        UserPreferences preferences = new UserPreferences();
        try {
            preferences = gson.fromJson(new FileReader(CONFIG_FILE), UserPreferences.class);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(UserPreferences.class.getName()).info("Config file is missing. Creating new one with default config");
            initConfig();
        }
        return preferences;
    }

    /**
     * Writes the given `UserPreferences` object to the configuration file.
     * It uses Gson to serialize the preferences object and write it to the file.
     * In case of any I/O errors during the writing process, it logs the exception using the logger for this class.
     *
     * @param preference The `UserPreferences` object to be written to the file.
     */
    public static void writePreferenceToFile(UserPreferences preference) {
        Writer writer = null;
        try {
            Gson gson = new Gson();
            writer = new FileWriter(CONFIG_FILE);
            gson.toJson(preference, writer);


        } catch (IOException ex) {
            Logger.getLogger(UserPreferences.class.getName()).log(Level.SEVERE, null, ex);

        } finally {
            try {
                writer.close();
            } catch (IOException ex) {
                Logger.getLogger(UserPreferences.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Loads all the users from the configuration file.
     * It reads each line of the file, deserializes each line into a `User` object using Gson,
     * and adds them to a list. In case of any I/O errors during the reading process,
     * it prints an error message to the console.
     *
     * @return A list containing all the `User` objects loaded from the file.
     */
    public static List<User> loadUsers() {
        List<User> users = new ArrayList<>();
        Gson gson = new Gson();
        try (BufferedReader reader = new BufferedReader(new FileReader(CONFIG_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                User user = gson.fromJson(line, User.class);
                users.add(user);
            }
        } catch (IOException e) {
            System.out.println("Error loading users: " + e.getMessage());
        }
        return users;
    }

    /**
     * Finds a user in the list of loaded users by their username.
     * It iterates through the list of users retrieved from the configuration file
     * and returns the user object if a match is found for the given username.
     * If no match is found, it returns null.
     *
     * @param username The username of the user to find.
     * @return The `User` object if found, or null if not found.
     */
    public static User findUser(String username) {
        List<User> users = loadUsers();
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }

    /**
     * Checks if the provided username and password match the credentials of a user in the configuration file.
     * It first finds the user by username using `findUser` method and then compares the provided password
     * with the stored password of the found user. Returns true if the credentials match, false otherwise.
     *
     * @param username The username to check.
     * @param password The password to check.
     * @return True if the username and password match a user's credentials, false otherwise.
     */
    public static boolean checkUser(String username, String password) {
        User user = findUser(username);
        if (user != null) {
            return user.getPassword().equals(password);
        }
        return false;
    }

    /**
     * Appends a new user to the configuration file.
     * It serializes the given `User` object into JSON format using Gson and writes it as a new line
     * to the configuration file. In case of any I/O errors during the writing process,
     * it logs the exception with a specific error message using the logger for this class.
     *
     * @param user The `User` object to be appended to the file.
     */
    public static void appendUserToFile(User user) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(CONFIG_FILE, true))) {
            Gson gson = new Gson();
            String userJson = gson.toJson(user);
            writer.write(userJson);
            writer.newLine();
        } catch (IOException e) {
            Logger.getLogger(UserPreferences.class.getName()).log(Level.SEVERE, "Error writing user to file", e);
        }
    }

    /**
     * Retrieves the fine amount associated with a specific member (user) identified by their member ID (username).
     * It first finds the user by the given member ID using `findUser` method and then returns the fine amount
     * of the found user. If the user is not found, it returns "0" as the default fine amount.
     *
     * @param memberID The member ID (username) of the user whose fine amount is to be retrieved.
     * @return The fine amount of the user as a string, or "0" if the user is not found.
     */
    public static String getFine(String memberID) {
        User user = findUser(memberID);
        if (user != null) {
            return user.getFine();
        }
        return "0";
    }

    /**
     * Updates the list of users in the configuration file.
     * It iterates through the given list of updated `User` objects, serializes each of them into JSON format using Gson,
     * and writes them to the configuration file, overwriting the existing user data.
     * In case of any I/O errors during the writing process, it logs the exception with a specific error message
     * using the logger for this class.
     *
     * @param updatedUsers The list of updated `User` objects to be written to the file.
     */
    public static void updateUserList(List<User> updatedUsers) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(CONFIG_FILE))) {
            Gson gson = new Gson();
            for (User user : updatedUsers) {
                String userJson = gson.toJson(user);
                writer.write(userJson);
                writer.newLine();
            }
        } catch (IOException e) {
            Logger.getLogger(UserPreferences.class.getName()).log(Level.SEVERE, "Error updating user list", e);
        }
    }
}
