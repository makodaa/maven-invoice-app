package happeekidz.Views.App;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.swing.*;

import com.formdev.flatlaf.FlatClientProperties;

import happeekidz.Models.App.Customers;
import happeekidz.Models.App.Invoices;
import net.miginfocom.swing.MigLayout;

import happeekidz.Views.App.AppView;
import happeekidz.Views.App.ProductsView;

public class DashboardView extends JPanel implements ActionListener {
    AppView instance;
    private JPanel parent;
    Connection con;
    ResultSet rs;
    Statement st;

    private JLabel lblnumofcustomer, lblnumofInvoice, lbltotalamount, displayCustom, displayInv, displayAmount;
    private JButton cmdAddInvoice, cmdAddCustomer, cmdAddProduct;
    private JPanel Headerpanel, GraphicsPanel, cusPanel, invPanel, amountPanel;

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == cmdAddCustomer) {

        }
        if(e.getSource() == cmdAddInvoice) {

        }
        if(e.getSource() == cmdAddProduct) {
            Window window = SwingUtilities.getWindowAncestor(this);
            window.dispose();
            instance = new AppView();
        }

    }
    

    public DashboardView() {
        init();
    }

    private void init() {
        setLayout(new MigLayout("wrap 4, fillx, insets 20", "", ""));
        add(WindowHeader(), "wrap");
        add(Graphics(), "growx");
        add(QuickAction(), "growx");

    }

    private JComponent WindowHeader() {
        Headerpanel = new JPanel(new MigLayout("insets 0, gapx 0", "", ""));
        JLabel lblBusiness = new JLabel("Carrel's Real Property Leasing");
        lblBusiness.putClientProperty(FlatClientProperties.STYLE_CLASS, "" +
                "h1");
        Headerpanel.add(lblBusiness, "growx");

        return Headerpanel;
    }

    private JComponent Graphics() {
        GraphicsPanel = new JPanel(new MigLayout("insets 0, gapx 0", "", ""));
        add(CustomerPanel());
        add(InvoicesPanel(), "span 2");
        add(AmountDue());
        return GraphicsPanel;
    }

    private JComponent CustomerPanel() {
        cusPanel = new JPanel(new MigLayout("insets 0, gapx 0", "", ""));
        cusPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 1, 1, 1, Color.lightGray),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        cusPanel.setPreferredSize(new Dimension(100, 100));
        Customers customers = new Customers();
        int numCustomers = customers.getNumberOfCustomers();
        lblnumofcustomer = new JLabel("Number of Customers ");
        displayCustom = new JLabel("" + numCustomers);
        displayCustom.putClientProperty(FlatClientProperties.STYLE_CLASS, "" +
                "h2");
        cusPanel.add(lblnumofcustomer, "wrap, gapy 6");
        cusPanel.add(displayCustom, "gapy 7, center");

        return cusPanel;
    }

    private JComponent InvoicesPanel() {
        invPanel = new JPanel(new MigLayout("insets 0, gapx 0", "", ""));
        invPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 1, 1, 1, Color.lightGray),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        invPanel.setPreferredSize(new Dimension(100, 100));
        Invoices invoices = new Invoices();
        int numInvoices = invoices.getNumberOfInvoice();
        lblnumofInvoice = new JLabel("Number of Invoices ");
        displayInv = new JLabel("" + numInvoices);
        displayInv.putClientProperty(FlatClientProperties.STYLE_CLASS, "" +
                "h2");
        invPanel.add(lblnumofInvoice, "wrap, gapy 6");
        invPanel.add(displayInv, "gapy 7, center");
        return invPanel;
    }

    private JComponent AmountDue() {
        amountPanel = new JPanel(new MigLayout("insets 0, gapx 0", "", ""));
        amountPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 1, 1, 1, Color.lightGray),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        amountPanel.setPreferredSize(new Dimension(100, 100));
        Invoices invoices = new Invoices();
        int numInvoices = invoices.getTotalInvoiceIssued();
        lbltotalamount = new JLabel("Total Amount Issued");
        displayAmount = new JLabel("" + numInvoices);
        displayAmount.putClientProperty(FlatClientProperties.STYLE_CLASS, "" +
                "h2");
        amountPanel.add(lbltotalamount, "wrap, gapy 6");
        amountPanel.add(displayAmount, "gapy 7, center");

        return amountPanel;
    }

    private JButton newFormButton(String text, String style) {
        JButton btn = new JButton(text);
        btn.setPreferredSize(new Dimension(160, 40));
        btn.addActionListener(this);
        btn.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_BORDERLESS);
        btn.putClientProperty(FlatClientProperties.STYLE, style);

        return btn;
    }

    private JComponent QuickAction() {
        JPanel panel = new JPanel();
        JLabel label = new JLabel("Quick Action!");
        add(label, "wrap");
        add(InvoiceQuickAction());
        add(CustomerQuickAction(), "span 2");
        add(ManageQuickAction());
        return panel;
    }

    private JComponent InvoiceQuickAction() {
        JPanel panel = new JPanel(new MigLayout());
        cmdAddInvoice = newFormButton("Add an Invoice",
                "arc: 5;" + "font: +2;" + "background: #16a34a;" + "foreground: #ffffff;");
        panel.add(cmdAddInvoice, "right");
        return panel;
    }

    private JComponent CustomerQuickAction() {
        JPanel panel = new JPanel(new MigLayout());
        cmdAddCustomer = newFormButton("Add a Customer",
                "arc: 5;" + "font: +2;" + "background: #16a34a;" + "foreground: #ffffff;");
        panel.add(cmdAddCustomer, "right");
        return panel;
    }

    private JComponent ManageQuickAction() {
        JPanel panel = new JPanel(new MigLayout());
        cmdAddProduct = newFormButton("Manage Product",
                "arc: 5;" + "font: +2;" + "background: #16a34a;" + "foreground: #ffffff;");
        cmdAddProduct.addActionListener(this);
        panel.add(cmdAddProduct, "right");
        return panel;
    }

}