package signup;

import java.util.logging.Logger;
import javax.swing.UIManager;
import com.formdev.flatlaf.FlatDarkLaf;

import signup.constants.AppConstants;

public class RMain {

    private final SignupApplicationContext applicationContext;

    public RMain() {
        this.applicationContext = new SignupApplicationContext();
    }

    public void initialize() {
        applicationContext.initialize();
    }

    public static void main(String[] args) {
        System.setProperty("flatlaf.uiScale", AppConstants.UI_SCALE);
        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
        } catch(javax.swing.UnsupportedLookAndFeelException ex) {
            Logger.getLogger(RMain.class.getName()).severe("Failed to initialize LaF");
        }

        RMain rMain = new RMain();
        rMain.initialize();
    }
}