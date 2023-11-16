package hapeekidz.Controllers.Main;

import javax.swing.UIManager;
import java.awt.*;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont;
import com.formdev.flatlaf.themes.FlatMacLightLaf;

import hapeekidz.Controllers.Login.LoginController;
import hapeekidz.Models.Login.Admins;
import hapeekidz.Views.App.AppView;
import hapeekidz.Views.Login.LoginView;

public class MainController {
    public static void main(String[] args) {
        setLookAndFeel();
        EventQueue.invokeLater(() -> {
            // new LoginController(new Admins(), new LoginView());
            new AppView("Dashboard");
        });
    }

    public static void setLookAndFeel() {
        try {
            FlatRobotoFont.install();
            FlatLaf.registerCustomDefaultsSource("hapeekidz.themes");
            UIManager.put("defaultFont", new Font(FlatRobotoFont.FAMILY, Font.PLAIN, 14));
            FlatMacLightLaf.setup();
        } catch (Exception e) {
            System.err.println(e);
        }
    }
}