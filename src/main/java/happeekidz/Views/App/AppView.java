package happeekidz.Views.App;

import java.awt.*;
import javax.swing.*;
import java.lang.reflect.*;
import java.net.URL;

import javax.swing.border.EmptyBorder;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.formdev.flatlaf.FlatClientProperties;

import happeekidz.Models.App.Customers;
import happeekidz.Models.App.Products;
import happeekidz.Models.Login.Admins;
import happeekidz.Models.App.Invoices;
import net.miginfocom.swing.MigLayout;

import java.time.LocalDate;

public class AppView extends JFrame implements ActionListener {
    private Invoices invoices = new Invoices();
    private Customers customers = new Customers();
    private Products products = new Products();

    /*
     * Implemented Methods
     */

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() instanceof JButton) {
            String text = (((JButton) e.getSource()).getText()).split(" ")[0];
            if (text == "Dashboard") {
                body.removeAll();
                body.add(DashboardView(), "grow");
                body.revalidate();
                body.repaint();
                this.setTitle(text);
            } else if (text == "Logout") {
                logout();
            } else {
                showPanel("happeekidz.Views.App." + text + "View", text);
            }
        }
    }

    /*
     * AppView Fields
     */
    JPanel body = new JPanel(new MigLayout("fill, insets 0", "", ""));
    private String[] links = {
            "Dashboard",
            "Invoices & Sales",
            "Customers",
            "Products & Services",
            "Security & Backup",
            "Logout"
    };

    private void init() {
        setSize(new Dimension(1200, 700));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
        this.add(GroupedPanel());
        showPanel("happeekidz.Views.App.DashboardView", "Dashboard");
    }

    public AppView() {
        init();
    }

    private Component GroupedPanel() {
        JPanel panel = new JPanel(new MigLayout("insets 0 10 0 0, gapx 0", "[][grow]", "grow"));
        panel.add(navBar());
        panel.add(body, "grow");
        return panel;
    }

    public void logout() {
        if (JOptionPane.showConfirmDialog(null, "Are you sure you want to logout?", "Logout",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            Admins admins = new Admins();
            admins.removeCurrentSession();
            this.dispose();
        }
    }

    public void showPanel(String strComponent, String strWindow) {
        Object sameClassObject = null;
        try {
            Class<?> cls = Class.forName(strComponent);
            Constructor<?> cons = cls.getConstructor();
            sameClassObject = cons.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (strComponent == "happeekidz.Views.App.DashboardView") {
            body.add(DashboardView(), "grow");
            body.revalidate();
            body.repaint();
            this.setTitle(strWindow);
            return;
        }
        body.removeAll();
        body.add((Component) sameClassObject, "grow");
        body.revalidate();
        body.repaint();
        this.setTitle(strWindow);
    }

    private JComponent navBar() {
        JPanel panel = new JPanel(new MigLayout(
                "fill, gapy 0, wrap 1, insets 0", "", ""));
        JLabel lblTitle = new JLabel("Rental Services System");
        lblTitle.setIcon(new ImageIcon(new ImageIcon(getClass().getResource("/happeekidz/assets/icons/logo.png"))
                .getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH)));
        lblTitle.putClientProperty(FlatClientProperties.STYLE, "" +
                "font:bold +4");
        panel.add(lblTitle);
        for (int i = 0; i < links.length; i++) {
            addNavigation(panel, links[i], i);
        }
        return panel;
    }

    private Icon getIcon(int index) {
        String[] arr = { "Dashboard", "Invoices", "Customers", "Products", "Security", "Logout" };
        URL url = getClass().getResource("/happeekidz/assets/icons/" + arr[index] + ".png");
        if (url != null) {
            return new ImageIcon(new ImageIcon(url).getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH));
        } else {
            return null;
        }
    }

    private void addNavigation(JPanel paramPanel, String name, int index) {
        JPanel panel = paramPanel;
        NavigationItem item = new NavigationItem(name, index);
        Icon icon = getIcon(index);
        if (icon != null) {
            item.setIcon(icon);
        }
        item.addActionListener(this);
        panel.add(item);
        panel.revalidate();
        panel.repaint();
    }

    public JPanel DashboardView() {
        JPanel panel = new JPanel(new MigLayout("fillx, wrap 1, insets 9 10 9 10, gapx 0", "", ""));
        JLabel lblBusiness = new JLabel("Carrel's Real Property Leasing");
        lblBusiness.putClientProperty(FlatClientProperties.STYLE, "" +
                "font: bold +4");

        JButton cmdAddInvoice = new JButton("Add Invoice");
        cmdAddInvoice.setIcon(
                new ImageIcon(new ImageIcon(getClass().getResource("/happeekidz/assets/icons/ADD_Invoices.png"))
                        .getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH)));
        cmdAddInvoice.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                body.removeAll();
                InvoicesView invoicesView = new InvoicesView();
                body.add(invoicesView, "grow");
                body.revalidate();
                body.repaint();
                setTitle("Invoices");
                invoicesView.showLayeredPanel(invoicesView.addInvoicePanel());
            }
        });

        JButton cmdAddCustomer = new JButton("Add Customer");
        cmdAddCustomer.setIcon(
                new ImageIcon(new ImageIcon(getClass().getResource("/happeekidz/assets/icons/ADD_customers.png"))
                        .getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH)));
        cmdAddCustomer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                body.removeAll();
                CustomersView customersView = new CustomersView();
                body.add(customersView, "grow");
                body.revalidate();
                body.repaint();
                setTitle("Customers");
                customersView.showLayeredPanel(customersView.addCustomerPanel());
            }
        });
        JButton cmdAddProduct = new JButton("Add Product");
        cmdAddProduct.setIcon(
                new ImageIcon(new ImageIcon(getClass().getResource("/happeekidz/assets/icons/ADD_products.png"))
                        .getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH)));
        cmdAddProduct.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                body.removeAll();
                ProductsView productsView = new ProductsView();
                body.add(productsView, "grow");
                body.revalidate();
                body.repaint();
                setTitle("Products");
                productsView.showLayeredPanel(productsView.addProductPanel());
            }
        });
        JLabel lblQuickActions = new JLabel("Quick Actions");
        lblQuickActions.putClientProperty(FlatClientProperties.STYLE, "font: bold +2");

        JPanel bottomPanel = new JPanel(new MigLayout("filly, wrap 3, insets 0, gapx 40", "", ""));
        bottomPanel.add(cmdAddInvoice, "growx");
        bottomPanel.add(cmdAddCustomer, "growx");
        bottomPanel.add(cmdAddProduct, "growx");
        panel.add(lblBusiness, "growx, gapy 12");
        panel.add(addGraphicsPanel(), "growx, gapy 12");
        panel.add(lblQuickActions, "growx, gapy 12");   
        panel.add(bottomPanel);
        return panel;
    }

    private JComponent addGraphicsPanel() {
        JPanel GraphicsPanel = new JPanel(new MigLayout("wrap 3, fillx, insets 0, gapx 4", "", ""));
        GraphicsPanel.add(newInfographicBox("No. of Invoice Issued", "", invoices.getNumberOfInvoice() + "",
                "Invoices.png"), "growx");

        GraphicsPanel.add(newInfographicBox("No. of Customers", "", customers.getNumberOfCustomers() + "",
                "Customers.png"), "growx");
        GraphicsPanel.add(newInfographicBox("No. of Products", "", products.getProducts().length + "",
                "Products.png"), "growx");
        // graphics panel for unpaid invoices
        Object[][] invoices = this.invoices.getInvoices();
        int unpaidInvoices = 0;
        int paidInvoices = 0;
        double amountdue = 0;
        double amountreceived = 0;
        for (int i = 0; i < invoices.length; i++) {
            if (invoices[i][11].toString().equals("Unpaid")) {
                unpaidInvoices++;
                amountdue += Double.parseDouble(invoices[i][10].toString());
            } else {
                paidInvoices++;
                amountreceived += Double.parseDouble(invoices[i][10].toString());
            }
        }
        GraphicsPanel.add(newInfographicBox("No. of Unpaid Invoices", "", unpaidInvoices + "",
                "clock.png"), "growx");
        GraphicsPanel.add(newInfographicBox("No. of Paid Invoices", "", paidInvoices + "", "checked_black.png"),
                "growx");
        GraphicsPanel.add(newInfographicBox("Amount Due", "", amountdue + "", "amount_due.png"), "growx");
        GraphicsPanel.add(newInfographicBox("Amount Received", "", amountreceived + "", "amount_paid.png"),
                "growx");
        int ending_contracts = 0;
        int terminated_contracts = 0;
        for (int i = 0; i < customers.getCustomers().length; i++) {
            // if contract is ending in 30 days or less
            if (LocalDate.now().plusDays(30).isAfter(LocalDate.parse(customers.getCustomers()[i][8].toString())) && LocalDate.now().isBefore(LocalDate.parse(customers.getCustomers()[i][8].toString()))) {
                ending_contracts++;
            }
            if (LocalDate.now().isAfter(LocalDate.parse(customers.getCustomers()[i][8].toString()))) {
                terminated_contracts++;
            }
        }
        GraphicsPanel.add(newInfographicBox("Ending Contracts", "", ending_contracts + "", "clock.png"),
                "growx");
        GraphicsPanel.add(newInfographicBox("Terminated Contracts", "", terminated_contracts + "", "expired.png"),
                "growx");
        return GraphicsPanel;
    }

    private JComponent newInfographicBox(String title, String Description, String value, String icon) {
        JPanel panel = new JPanel(new MigLayout("wrap 2, fillx, insets 12 15 12 15", "", ""));
        panel.putClientProperty(FlatClientProperties.STYLE, "" +
                "[light]background:darken(@background,3%);" +
                "[dark]borderColor:lighten(@background,3%);");
        panel.setPreferredSize(new Dimension(300, 100));
        JLabel lblTitle = new JLabel(title);
        lblTitle.putClientProperty(FlatClientProperties.STYLE, "font: bold +1");
        JLabel lblIcon = new JLabel(
                new ImageIcon(new ImageIcon(getClass().getResource("/happeekidz/assets/icons/" + icon))
                        .getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH)));
        // lblIcon.setIconTextGap(100);
        JLabel lblDescription = new JLabel(Description);
        lblDescription.putClientProperty(FlatClientProperties.STYLE, "font: bold +1");
        JLabel lblValue = new JLabel(value);
        lblValue.putClientProperty(FlatClientProperties.STYLE, "font: bold +1");
        panel.add(lblTitle, "growx");
        panel.add(lblIcon, "dock east, gapright 40");
        panel.add(lblDescription, "wrap");
        panel.add(lblValue, "wrap");
        return panel;
    }
}

class NavigationItem extends JButton {
    private int index;

    public NavigationItem(String name, int index) {
        super(name);
        this.index = index;
        setForeground(Color.BLACK);
        setHorizontalAlignment(SwingConstants.LEFT);
        setBackground(new Color(0, 0, 0, 3));
        setBorder(new EmptyBorder(9, 3, 9, 10));
        setIconTextGap(10);
        setPreferredSize(new Dimension(250, 70));
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

}
