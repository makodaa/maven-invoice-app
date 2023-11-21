package happeekidz.Controllers.Main;

import javax.swing.UIManager;
import java.awt.*;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont;
import com.formdev.flatlaf.themes.FlatMacLightLaf;

import happeekidz.Controllers.App.AppController;
import happeekidz.Controllers.Login.LoginController;
import happeekidz.Models.Login.Admins;
import happeekidz.Views.App.AppView;
import happeekidz.Views.Login.LoginView;

public class MainController {
    public static void main(String[] args) {
        setLookAndFeel();
        EventQueue.invokeLater(() -> {
            // new LoginController(new Admins(), new LoginView());
            new AppView();
        });
    }

    public static void setLookAndFeel() {
        try {
            FlatRobotoFont.install();
            FlatLaf.registerCustomDefaultsSource("happeekidz.themes");
            UIManager.put("defaultFont", new Font(FlatRobotoFont.FAMILY, Font.PLAIN, 14));
            FlatMacLightLaf.setup();
        } catch (Exception e) {
            System.err.println(e);
        }
    }
}