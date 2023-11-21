package happeekidz.Controllers.Login;

import javax.swing.*;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import happeekidz.Controllers.App.AppController;
import happeekidz.Models.App.Products;
import happeekidz.Models.Login.Admins;
import happeekidz.Views.App.AppView;
import happeekidz.Views.Login.LoginView;


public class LoginController implements ActionListener{
    private Admins model;
    private LoginView view;
    private JButton cmdLogin;

    public LoginController(Admins model, LoginView view) {
        this.model = model;
        this.view = view;
        this.cmdLogin = view.getCmdLogin();
        this.cmdLogin.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == cmdLogin) {
            System.out.println("Debugging Moments: cmdLogin clicked");
        String username = view.getUsername();
        String password = view.getPassword();
        boolean Authenticated;
        Authenticated = model.Authenticate(username, password);

        if (Authenticated) {
            if (model.getAccessLevel().equals("admin")){
                view.dispose();
                new AppController(new Products(), new AppView());
            }
            else {
                JOptionPane.showMessageDialog(null, "Access Level Moments: " + model.getAccessLevel(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } 
        
        else {
            JOptionPane.showMessageDialog(null, "Invalid Credentials", "Error", JOptionPane.ERROR_MESSAGE);
        }
        }
    }
}
