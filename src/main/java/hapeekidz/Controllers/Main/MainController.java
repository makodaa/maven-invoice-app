package hapeekidz.Controllers.Main;

import javax.swing.UIManager;
import java.awt.*;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont;
import com.formdev.flatlaf.themes.FlatMacLightLaf;

import hapeekidz.Controllers.Login.LoginController;
import hapeekidz.Models.Login.Admin;
import hapeekidz.Views.Login.LoginView;

import hapeekidz.Views.Dashboard.TabbedPanelView;

public class MainController {
    public static void main(String[] args) {
        setLookAndFeel();
        EventQueue.invokeLater(() -> {
            // Admin model = retrieveAdminFromDataBase();
            // LoginView view = new LoginView();
            // LoginController controller = new LoginController(model, view);
            new TabbedPanelView();
        });
    }

    public static Admin retrieveAdminFromDataBase() {
        Admin model = new Admin();
        model.setUsername("admin");
        model.setPassword("admin");
        model.setRole("admin");
        return model;
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