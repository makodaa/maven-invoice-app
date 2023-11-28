package happeekidz.Views.App;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import com.formdev.flatlaf.FlatClientProperties;

import java.awt.event.ActionListener;
import java.util.Arrays;
import java.awt.event.ActionEvent;

import net.miginfocom.swing.MigLayout;

import happeekidz.Models.Login.Admins;

public class SecurityView extends JPanel implements ActionListener {
    private Admins admins;
    private JButton cmdChangePassword, cmdAddUser;

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == cmdChangePassword) {
            JPasswordField newPasswordField = new JPasswordField();
            JPasswordField confirmPasswordField = new JPasswordField();

            Object[] message = {
                    "New Password:", newPasswordField,
                    "Confirm Password:", confirmPasswordField
            };

            int option = JOptionPane.showConfirmDialog(null, message, "Change Password", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                char[] newPassword = newPasswordField.getPassword();
                char[] confirmedPassword = confirmPasswordField.getPassword();
                if (Arrays.equals(newPassword, confirmedPassword)) {
                    admins.changePassword(String.valueOf(newPassword));
                    JOptionPane.showMessageDialog(null, "Password successfully changed.", "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    // Passwords don't match, show error
                    JOptionPane.showMessageDialog(null, "Passwords do not match. Please try again.", "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }
        if (e.getSource() == cmdAddUser) {
            JTextField usernameField = new JTextField();
            JPasswordField passwordField = new JPasswordField();
            JComboBox<String> accessLevelField = new JComboBox<String>();
            accessLevelField.addItem("admin");
            accessLevelField.addItem("user");

            Object[] message = {
                    "Username:", usernameField,
                    "Password:", passwordField,
                    "Access Level:", accessLevelField
            };

            int option = JOptionPane.showConfirmDialog(null, message, "Add User", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                char[] password = passwordField.getPassword();
                admins.addUser(usernameField.getText(), String.valueOf(password), accessLevelField.getSelectedItem().toString());
                JOptionPane.showMessageDialog(null, usernameField.getText() + " is successfully added.", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    public SecurityView() {
        init();
    }

    private void init() {
        admins = new Admins();
        admins.fetchCurrentSession();

        setLayout(new MigLayout("wrap 1, fill, gapx 0, insets 0", "", ""));
        if (admins.getAccessLevel().equals("admin")) {
            add(SecurityPanel(), "growx");
        } else {
            add(blockedPanel(), "growx");
        }
    }

    private JComponent SecurityPanel() {
        JPanel panel = new JPanel(new MigLayout("fillx, insets 9 10 9 10, gapx 0", "", ""));
        JLabel lblPanelTitle = new JLabel("Security");
        lblPanelTitle.putClientProperty(FlatClientProperties.STYLE, "" +
                "font:bold +2;");
        JLabel lblCurrentPassword = new JLabel("User Password");

        cmdChangePassword = new JButton("Change Password");
        cmdChangePassword.putClientProperty(FlatClientProperties.STYLE, "" +
                "font:bold;" +
                "background: #4caf50;" +
                "foreground: #ffffff;");
        cmdChangePassword.putClientProperty(FlatClientProperties.BUTTON_TYPE,
                FlatClientProperties.BUTTON_TYPE_BORDERLESS);
        cmdChangePassword.addActionListener(this);

        JLabel lblAddUser = new JLabel("Add User");
        cmdAddUser = new JButton("Add User");
        cmdAddUser.putClientProperty(FlatClientProperties.STYLE, "" +
                "font:bold;" +
                "background: #4caf50;" +
                "foreground: #ffffff;");
        cmdAddUser.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_BORDERLESS);
        cmdAddUser.addActionListener(this);

        panel.add(lblPanelTitle, "growx, wrap, gapy 10");
        panel.add(lblCurrentPassword, "growx");
        panel.add(cmdChangePassword, "right, wrap, gapy 10");
        panel.add(new JSeparator(), "growx, wrap, gapy 10");
        panel.add(lblAddUser, "growx, gapy 10");
        panel.add(cmdAddUser, "right, gapy 10");
        return panel;
    }

    private JComponent blockedPanel() {
        JPanel panel = new JPanel(new MigLayout("wrap 1, fill, insets 0, gapx 0", "", ""));
        JLabel lblBlocked = new JLabel("You do not have access to this page.");
        lblBlocked.putClientProperty(FlatClientProperties.STYLE, "" +
                "font:bold +2;");
        panel.add(lblBlocked, "center");
        return panel;
    }
}
