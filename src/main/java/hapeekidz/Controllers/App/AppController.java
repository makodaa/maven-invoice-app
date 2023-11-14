package hapeekidz.Controllers.App;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;

import hapeekidz.Models.App.Products;
import hapeekidz.Views.App.AppView;

public class AppController{
    private Products model;
    private AppView view;

    public AppController(Products model, AppView view) {
        this.model = model;
        this.view = view;
    }

}
