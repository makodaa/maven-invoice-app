package hapeekidz.Controllers.App;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.lang.reflect.Constructor;

import javax.swing.*;

import hapeekidz.Models.App.Products;
import hapeekidz.Views.App.AppView;

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
