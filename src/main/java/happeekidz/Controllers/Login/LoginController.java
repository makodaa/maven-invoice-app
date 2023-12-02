package happeekidz.Controllers.Login;

import javax.swing.*;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import happeekidz.Controllers.App.AppController;
import happeekidz.Models.App.Products;
import happeekidz.Models.Login.Admins;
import happeekidz.Views.App.AppView;
import happeekidz.Views.Login.LoginView;

public class LoginController implements ActionListener {
    private Admins model;
    private LoginView view;
    private JButton cmdLogin, cmdSignUp;

    public LoginController(Admins model, LoginView view) {
        this.model = model;
        this.view = view;
        if (view.isVisible() && model.isTableEmpty("users")) {
            view.add(view.SignUpPanel());
            this.cmdSignUp = view.getCmdSignUp();
            this.cmdSignUp.addActionListener(this);
        } else if (view.isVisible()) {
            view.add(view.LoginPanel());
            this.cmdLogin = view.getCmdLogin();
            this.cmdLogin.addActionListener(this);
        }

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == cmdLogin) {
            String username = view.getUsername();
            String password = view.getPassword();
            boolean Authenticated;
            Authenticated = model.Authenticate(username, password);

            if (Authenticated) {
                    model.removeCurrentSession();
                    view.dispose();
                    model.addCurrentSession();
                    view.dispose();
                    new AppController(new Products(), new AppView());
            }

            else {
                JOptionPane.showMessageDialog(null, "Invalid Credentials", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        if (e.getSource() == view.getCmdSignUp()) {
            String username = view.getUsername();
            String password = view.getPassword();
            if (username.equals("") || password.equals("")) {
                JOptionPane.showMessageDialog(null, "Please fill up all fields", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                model.setUsername(username);
                model.setPassword(password);
                model.setAccessLevel("admin");
                model.addUser();
                view.dispose();
                model.addCurrentSession();
                new AppController(new Products(), new AppView());
            }

        }
    }
}
