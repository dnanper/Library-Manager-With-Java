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

        private int nDaysWithoutFine;
        private final String username;
        private String password;

        public User(String username, String password, int nDaysWithoutFine) {
            this.username = username;
            this.password = DigestUtils.shaHex(password);
            this.nDaysWithoutFine = nDaysWithoutFine;
        }

        public int getnDaysWithoutFine() {
            return nDaysWithoutFine;
        }

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword() {
            if (password.length() < 16) {
                this.password = DigestUtils.shaHex(password);
            } else {
                this.password = password;
            }
        }
    }

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

    public static User findUser(String username) {
        List<User> users = loadUsers();
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }

    public static boolean checkUser(String username, String password) {
        User user = findUser(username);
        if (user != null) {
            return user.getPassword().equals(password);
        }
        return false;
    }
}
