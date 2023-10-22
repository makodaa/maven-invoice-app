package hapeekidz.Controllers.Login;

import javax.swing.*;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import hapeekidz.Models.Login.Admin;
import hapeekidz.Views.Login.LoginView;

import hapeekidz.Models.Dashboard.DashboardModel;
import hapeekidz.Views.Dashboard.TabbedPanelView;
import hapeekidz.Controllers.Dashboard.DashboardController;


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
            view.dispose();
            TabbedPanelView dashboardView = new TabbedPanelView();
            DashboardModel dashboardModel = new DashboardModel();
            DashboardController dashboardController = new DashboardController();
        } 
        
        else {
            JOptionPane.showMessageDialog(null, "Invalid Credentials", "Error", JOptionPane.ERROR_MESSAGE);
        }
        }
    }
}
