package hapeekidz.Controllers.Admin;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import hapeekidz.Models.Admin.AdminModel;
import hapeekidz.Views.Admin.AdminView;

public class AdminController{
    private AdminModel model;
    private AdminView view;

    public AdminController(AdminModel model, AdminView view) {
        this.model = model;
        this.view = view;
    }

}
