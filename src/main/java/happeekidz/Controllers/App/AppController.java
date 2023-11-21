package happeekidz.Controllers.App;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import happeekidz.Models.App.Products;
import happeekidz.Views.App.AppView;

public class AppController implements ActionListener {
    private Products model;
    private AppView view;
    private JButton cmdLogout;

    public AppController() {
    }

    public AppController(Products model, AppView view) {
        this.model = model;
        this.view = view;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
    }

}
