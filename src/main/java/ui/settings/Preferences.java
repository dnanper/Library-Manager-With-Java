package ui.settings;

import com.google.gson.Gson;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Preferences {

    public static final String CONFIG_FILE = "config.txt";

    int nDaysWithoutFine;
    float finePerDay;
    String username;
    String password;
    String email;
    String emailpassword;

    /**
     * Constructs a `Preferences` object with default values. Initializes the number of days without fine to 14, fine per day to 2,
     * username to "admin", and sets empty passwords (which may be hashed later if applicable).
     */
    public Preferences() {
        nDaysWithoutFine = 14;
        finePerDay = 2;
        username = "admin";
        setPassword("admin");
        email = "";
        setEmailPassword("");

    }

    public int getnDaysWithoutFine() {
        return nDaysWithoutFine;
    }

    public void setnDaysWithoutFine(int nDaysWithoutFine) {
        this.nDaysWithoutFine = nDaysWithoutFine;
    }

    public float getFinePerDay() {
        return finePerDay;
    }

    public void setFinePerDay(float finePerDay) {
        this.finePerDay = finePerDay;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public String getEmailPassword() {
        return emailpassword;
    }

    public void setPassword(String password) {
        if (password.length() < 16) {
            this.password = DigestUtils.shaHex(password);
        }else
            this.password = password;
    }

    /**
     * Sets the password for the email account. If the password length is less than 16 characters, it will be hashed using `DigestUtils.shaHex`.
     * Otherwise, the password is set as is.
     *
     * @param emailpassword The new email password to set.
     */
    public void setEmailPassword(String emailpassword) {
        if (emailpassword.length() < 16) {
            this.emailpassword = DigestUtils.shaHex(emailpassword);
        }else
            this.emailpassword = emailpassword;
    }

    /**
     * Initializes the configuration file with default preferences. It creates a new `Preferences` object with default values,
     * then uses Gson to serialize it and write it to the configuration file. In case of any I/O errors during the writing process,
     * it logs the exception using the logger for this class.
     */
    public static void initConfig() {
        Writer writer = null;
        try {
            Preferences preference = new Preferences();
            Gson gson = new Gson();
            writer = new FileWriter(CONFIG_FILE);
            gson.toJson(preference, writer);
        } catch (IOException ex) {
            Logger.getLogger(Preferences.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                writer.close();
            } catch (IOException ex) {
                Logger.getLogger(Preferences.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Retrieves the application preferences from the configuration file. It attempts to deserialize the preferences from the file using Gson.
     * If the file is not found, it logs an informational message indicating that the config file is missing and proceeds to create a new one
     * with default configuration by calling `initConfig()`.
     *
     * @return The `Preferences` object containing the retrieved or newly initialized preferences.
     */
    public static Preferences getPreferences() {
        Gson gson = new Gson();
        Preferences preferences = new Preferences();
        try {
            preferences = gson.fromJson(new FileReader(CONFIG_FILE), Preferences.class);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Preferences.class.getName()).info("Config file is missing. Creating new one with default config");
            initConfig();
        }
        return preferences;
    }

    /**
     * Writes the given `Preferences` object to the configuration file. It uses Gson to serialize the preferences object and write it to the file.
     * In case of any I/O errors during the writing process, it logs the exception using the logger for this class.
     *
     * @param preference The `Preferences` object to be written to the file.
     */
    public static void writePreferenceToFile(Preferences preference) {
        Writer writer = null;
        try {
            Gson gson = new Gson();
            writer = new FileWriter(CONFIG_FILE);
            gson.toJson(preference, writer);


        } catch (IOException ex) {
            Logger.getLogger(Preferences.class.getName()).log(Level.SEVERE, null, ex);

        } finally {
            try {
                writer.close();
            } catch (IOException ex) {
                Logger.getLogger(Preferences.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}

