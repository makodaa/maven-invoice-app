package happeekidz.Views.Login;

import java.awt.*;
import javax.swing.*;

import com.formdev.flatlaf.FlatClientProperties;
import net.miginfocom.swing.MigLayout;

public class LoginView extends JFrame{

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton cmdLogin, cmdSignUp;

    public LoginView() {
        init();
    }

    private void init() {
        setTitle("Login Page");
        setSize(new Dimension(1200, 700));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
        setLayout(new MigLayout("fill, insets 20", "[center]", "[center]"));
    }

    public JComponent LoginPanel() {
        txtUsername = new JTextField();
        txtPassword = new JPasswordField();
        cmdLogin = new JButton("Login");
        JPanel panel = new JPanel(new MigLayout("wrap, fillx, insets 35 45 30 45", "fill,250:280"));

        panel.putClientProperty(FlatClientProperties.STYLE, "" +
                "arc:20;" +
                "[light]background:darken(@background,3%);" +
                "[dark]borderColor:lighten(@background,3%);");

        txtUsername.putClientProperty(FlatClientProperties.STYLE, "" +
                "showClearButton:true");

        txtUsername.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Enter your username");
        txtPassword.putClientProperty(FlatClientProperties.STYLE, "" +
                "showRevealButton:true")
                ;
        txtPassword.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Enter your password");

        cmdLogin.putClientProperty(FlatClientProperties.STYLE, "" +
                "[light]background:darken(@background,10%);" +
                "[dark]background:lighten(@background,10%);" +
                "borderWidth:0;" +
                "focusWidth:0;" +
                "innerFocusWidth:0");

        JLabel lblTitle = new JLabel("Carrel's Real Property Leasing");
        JLabel description = new JLabel("Please log in to continue");
        lblTitle.putClientProperty(FlatClientProperties.STYLE, "" +
                "font:bold +4");
        description.putClientProperty(FlatClientProperties.STYLE, "" +
                "[light]foreground:lighten(@foreground,30%);" +
                "[dark]foreground:darken(@foreground,30%);");

        panel.add(lblTitle);
        panel.add(description);
        panel.add(new JLabel("Username"), "gapy 8");
        panel.add(txtUsername);
        panel.add(new JLabel("Password"), "gapy 8");
        panel.add(txtPassword);
        panel.add(cmdLogin, "gapy 10");
        return panel;
    }

    public JComponent SignUpPanel() {
        txtUsername = new JTextField();
        txtPassword = new JPasswordField();
        cmdSignUp = new JButton("Sign Up");
        JPanel panel = new JPanel(new MigLayout("wrap, fillx, insets 35 45 30 45", "fill,250:280"));

        panel.putClientProperty(FlatClientProperties.STYLE, "" +
                "arc:20;" +
                "[light]background:darken(@background,3%);" +
                "[dark]borderColor:lighten(@background,3%);");

        txtUsername.putClientProperty(FlatClientProperties.STYLE, "" +
                "showClearButton:true");

        txtUsername.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Enter username");
        txtPassword.putClientProperty(FlatClientProperties.STYLE, "" +
                "showRevealButton:true")
                ;
        txtPassword.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Enter your password");

        cmdSignUp.putClientProperty(FlatClientProperties.STYLE, "" +
                "[light]background:darken(@background,10%);" +
                "[dark]background:lighten(@background,10%);" +
                "borderWidth:0;" +
                "focusWidth:0;" +
                "innerFocusWidth:0");

        JLabel lblTitle = new JLabel("Carrel's Real Property Leasing");
        JLabel description = new JLabel("First Run Detected. Please Sign Up to continue");
        lblTitle.putClientProperty(FlatClientProperties.STYLE, "" +
                "font:bold +4");
        description.putClientProperty(FlatClientProperties.STYLE, "" +
                "[light]foreground:lighten(@foreground,30%);" +
                "[dark]foreground:darken(@foreground,30%);");

        panel.add(lblTitle);
        panel.add(description);
        panel.add(new JLabel("Admin Username"), "gapy 8");
        panel.add(txtUsername);
        panel.add(new JLabel("Password"), "gapy 8");
        panel.add(txtPassword);
        panel.add(cmdSignUp, "gapy 10");
        return panel;
    }

        public String getUsername() {
                return txtUsername.getText();
        }

        public String getPassword() {
                return new String(txtPassword.getPassword());
        } 

        public JButton getCmdLogin() {
                return cmdLogin;
        }
        public JButton getCmdSignUp() {
                return cmdSignUp;
        }

}
