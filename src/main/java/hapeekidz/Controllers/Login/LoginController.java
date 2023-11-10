package hapeekidz.Controllers.Login;

import javax.swing.*;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import hapeekidz.Models.Admin.AdminModel;
import hapeekidz.Models.Login.Admin;
import hapeekidz.Views.Admin.AdminView;
import hapeekidz.Views.Login.LoginView;
import hapeekidz.Controllers.Admin.AdminController;


public class LoginController implements ActionListener{
    private Admin model;
    private LoginView view;
    private JButton cmdLogin;

    public LoginController(Admin model, LoginView view) {
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
                AdminModel dashboardModel = new AdminModel();
                AdminView dashboardView = new AdminView();
                AdminController dashboardController = new AdminController(dashboardModel, dashboardView);
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
