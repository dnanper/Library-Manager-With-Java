//package ui.settings;
//
////import com.google.gson.Gson;
//
//import java.io.FileWriter;
//import java.io.IOException;
//import java.io.Writer;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//
//public class Preferences {
//
//    public static final String CONFIG_FILE = "config.txt";
//
//    int nDaysWithoutFine;
//    float finePerDay;
//    String username;
//    String password;
//
//    public Preferences() {
//        nDaysWithoutFine = 14;
//        finePerDay = 2;
//        username = "admin";
//        password = "admin";
//    }
//
//    public int getnDaysWithoutFine() {
//        return nDaysWithoutFine;
//    }
//
//    public void setnDaysWithoutFine(int nDaysWithoutFine) {
//        this.nDaysWithoutFine = nDaysWithoutFine;
//    }
//
//    public float getFinePerDay() {
//        return finePerDay;
//    }
//
//    public void setFinePerDay(float finePerDay) {
//        this.finePerDay = finePerDay;
//    }
//
//    public String getUsername() {
//        return username;
//    }
//
//    public void setUsername(String username) {
//        this.username = username;
//    }
//
//    public String getPassword() {
//        return password;
//    }
//
//    public void setPassword(String password) {
//
//        this.password = password;
//    }
//    public static void initConfig() {
//        Writer writer = null;
//        try {
//            Preferences preference = new Preferences();
//            Gson gson = new Gson();
//            writer = new FileWriter(CONFIG_FILE);
//            gson.toJson(preference, writer);
//        } catch (IOException ex) {
//            Logger.getLogger(Preferences.class.getName()).log(Level.SEVERE, null, ex);
//        } finally {
//            try {
//                writer.close();
//            } catch (IOException ex) {
//                Logger.getLogger(Preferences.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }
//    }
//}
//
//
