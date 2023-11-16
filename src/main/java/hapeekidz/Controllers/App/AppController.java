package hapeekidz.Controllers.App;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;

import javax.swing.JButton;

import hapeekidz.Models.App.Products;
import hapeekidz.Views.App.AppView;

public class AppController implements ActionListener{
    private Products model;
    private AppView view;
    private JButton cmdLogout;


    public AppController(Products model, AppView view) {
        this.model = model;
        this.view = view;
    }
        @Override
        public void actionPerformed(ActionEvent e) {
            }
}

