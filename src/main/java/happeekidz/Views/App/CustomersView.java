package happeekidz.Views.App;

import java.awt.*;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import net.miginfocom.swing.MigLayout;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import com.formdev.flatlaf.FlatClientProperties;

public class CustomersView extends JPanel implements ActionListener {
    private JTable customerTable;
    private JTextField txtName, txtDate, txtUnit, txtInvoiceAmount;
    private JButton cmdAdd, cmdCancel, cmdConfirm, cmdBack, cmdCancelAddProduct;
    private JTextField txtEmail, txtContactNumber;
    private JComponent floatingPanel = addCustomerPanel();

    @Override
    public void actionPerformed(ActionEvent e) {
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        if (e.getSource() == cmdAdd) {
            showAddProductPanel();
        }
        if (e.getSource() == cmdCancel) {
            int rs = JOptionPane.showConfirmDialog(frame, "Are you sure you want to cancel?", "Cancel",
                    JOptionPane.YES_NO_OPTION);
            if (rs == JOptionPane.YES_OPTION) {
                JPanel glassPane = (JPanel) frame.getGlassPane();
                glassPane.setVisible(false);
                glassPane.removeAll();
            }
        }
        if (e.getSource() == cmdConfirm) {
            // boolean rs = sendProductsToDatabase();
            // if (rs) {
            JPanel glassPane = (JPanel) frame.getGlassPane();
            glassPane.setVisible(false);
            glassPane.removeAll();
            updateFrame(frame);
            // updateTable();
        }
        // }
        if (e.getSource() == cmdBack) {
            JPanel panel = (JPanel) cmdBack.getParent().getParent();
            panel.removeAll();
            panel.add(new DashboardView(), "grow");
            updateFrame(frame);
        }
        if (e.getSource() == cmdCancelAddProduct) {
            JPanel glassPane = (JPanel) frame.getGlassPane();
            glassPane.setVisible(false);
            glassPane.removeAll();
        }
        updateFrame(frame);
    }

    public CustomersView() {
        init();
    }

    public void init() {
        setBackground(new Color(255, 255, 0));
        setLayout(new MigLayout("wrap 1, fill, gapx 0, insets 0", "", ""));
        add(windowHeader(), "growx, left");
        add(accountDetailsPanel(), "growx, growy");
        add(controlPanel(), "growx, growy");
        add(tablePanel(), "growx, growy");
    }

    private <T extends JComponent> void updateComponents(T component) {
        component.revalidate();
        component.repaint();
    }

    private void updateFrame(JFrame frame) {
        frame.revalidate();
        frame.repaint();
    }

    private JComponent windowHeader() {
        JPanel btnBackMainPanel = new JPanel((LayoutManager) null);
        add(btnBackMainPanel, "cell 0 0,growx,alignx left");
        btnBackMainPanel.setLayout(new MigLayout("insets 0, gapx 0", "", ""));

        JButton btnBack = new JButton("Customers");
        btnBack.setBorder(new EmptyBorder(9, 10, 9, 10));
        btnBack.putClientProperty(FlatClientProperties.STYLE, "" +
                "font:bold +2;");
        btnBack.setIcon(new ImageIcon(new ImageIcon(getClass().getResource("/hapeekidz/assets/icons/back.png"))
                .getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH)));
        btnBack.setBorder(new EmptyBorder(9, 10, 9, 10));
        btnBack.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JPanel panel = (JPanel) btnBack.getParent().getParent();
                panel.removeAll();
                panel.add(new DashboardView(), "grow");
                panel.revalidate();
                panel.repaint();
            }
        });
        btnBackMainPanel.add(btnBack, "growx");
        return btnBackMainPanel;
    }

    private JComponent accountDetailsPanel() {
        JPanel accountDetailsMainPanel = new JPanel((LayoutManager) null);
        add(accountDetailsMainPanel, "cell 0 1,growx");
        accountDetailsMainPanel.setLayout(new MigLayout("", "[grow]", "[grow][grow]"));

        JPanel titlePanel = new JPanel();
        accountDetailsMainPanel.add(titlePanel, "cell 0 0,grow");

        JLabel lblAccountDetails = new JLabel("Account Details");
        titlePanel.add(lblAccountDetails, "cell 0 0");

        JPanel detailsPanel = new JPanel();
        accountDetailsMainPanel.add(detailsPanel, "cell 0 1,grow");

        detailsPanel.setLayout(new MigLayout("", "[grow][grow]", "[grow][grow][grow][grow][grow]"));
        JLabel accountName = new JLabel("Random Name");
        detailsPanel.add(accountName, "cell 0 0");

        JPanel statementOfAccountTitlePanel = new JPanel();
        detailsPanel.add(statementOfAccountTitlePanel, "cell 1 0,grow");

        JLabel lblStatementOfAccount = new JLabel("Statement of Account");
        statementOfAccountTitlePanel.add(lblStatementOfAccount);

        JLabel accountEmail = new JLabel("Random@gmail.com");
        detailsPanel.add(accountEmail, "cell 0 1");

        JPanel invoiceAmountPanel = new JPanel();
        detailsPanel.add(invoiceAmountPanel, "cell 1 1,grow");
        invoiceAmountPanel.setLayout(new MigLayout("", "[grow]", "[grow]"));

        JLabel lblInvoiceAmount = new JLabel("Invoice Amount");
        invoiceAmountPanel.add(lblInvoiceAmount, "cell 0 0,alignx left");

        JLabel accountInvoiceAmount = new JLabel("₱69420");
        invoiceAmountPanel.add(accountInvoiceAmount, "cell 1 0,alignx right");

        JLabel accountNumber = new JLabel("09696969696");
        detailsPanel.add(accountNumber, "cell 0 2");

        JPanel amountPaidPanel = new JPanel();
        detailsPanel.add(amountPaidPanel, "cell 1 2,grow");
        amountPaidPanel.setLayout(new MigLayout("", "[grow]", "[grow]"));

        JLabel lblamountPaid = new JLabel("Amount Paid");
        amountPaidPanel.add(lblamountPaid, "cell 0 0,alignx left");

        JLabel accountAmountPaid = new JLabel("₱42069");
        amountPaidPanel.add(accountAmountPaid, "cell 1 0,alignx right,aligny baseline");

        JLabel accountOtherInfo = new JLabel("Additional Info");
        detailsPanel.add(accountOtherInfo, "cell 0 3");

        JPanel balanceDuePanel = new JPanel();
        detailsPanel.add(balanceDuePanel, "cell 1 3,grow");
        balanceDuePanel.setLayout(new MigLayout("", "[grow]", "[grow]"));

        JLabel lblBalanceDue = new JLabel("Balance Due");
        balanceDuePanel.add(lblBalanceDue, "cell 0 0,alignx left");

        JLabel accountBalanceDue = new JLabel("₱69420");
        balanceDuePanel.add(accountBalanceDue, "cell 1 0,alignx right");

        return accountDetailsMainPanel;
    }

    private JComponent controlPanel() {
        JPanel cmdMainPanel = new JPanel();
        add(cmdMainPanel, "cell 0 2,grow");
        cmdMainPanel.setLayout(new MigLayout("fill, insets 9 10 9 10", "", ""));

        JTextField txtSearch = new JTextField();
        txtSearch.putClientProperty(FlatClientProperties.STYLE, "" +
                "arc: 5;" +
                "font: +2;");
        txtSearch.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Search for a customer");
        cmdMainPanel.add(txtSearch, "growx");

        JButton cmdFilter = new JButton("");
        cmdFilter.setBorder(new EmptyBorder(0, 0, 0, 0));
        cmdFilter.setIcon(new ImageIcon(new ImageIcon(getClass().getResource("/hapeekidz/assets/icons/filter.png"))
                .getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH)));
        cmdFilter.setBorder(new EmptyBorder(0, 0, 0, 0));
        cmdMainPanel.add(cmdFilter, "shrinkx, gapx 10");

        cmdAdd = new JButton("Add a Customer");
        cmdAdd.setPreferredSize(new Dimension(160, 40));
        cmdAdd.setPreferredSize(new Dimension(160, 40));
        cmdAdd.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_BORDERLESS);
        cmdAdd.putClientProperty(FlatClientProperties.STYLE, "" +
                "arc: 5;" +
                "font: +2;" +
                "background: #16a34a;" +
                "foreground: #ffffff;");
        cmdAdd.addActionListener(this);
        cmdMainPanel.add(cmdAdd, "right");
        return cmdMainPanel;
    }

    @SuppressWarnings("serial")
    private JComponent tablePanel() {
        JPanel tableMainPanel = new JPanel();
        add(tableMainPanel, "cell 0 3,growx");
        tableMainPanel.setLayout(new MigLayout("", "[grow]", "[grow]"));

        JScrollPane tableScrollPane = new JScrollPane();
        tableMainPanel.add(tableScrollPane, "cell 0 0,grow");

        customerTable = new JTable();
        customerTable.setModel(new DefaultTableModel(
                new Object[][] {
                        { null, null, null, null },
                        { null, null, null, null },
                        { null, null, null, null },
                        { null, null, null, null },
                        { null, null, null, null },
                },
                new String[] {
                        "Customer Name", "Room No. (?)", "Date Started", "Action"
                }) {
            @SuppressWarnings("rawtypes")
            Class[] columnTypes = new Class[] {
                    String.class, Integer.class, String.class, Object.class
            };

            @SuppressWarnings({ "rawtypes", "unchecked" })
            public Class getColumnClass(int columnIndex) {
                return columnTypes[columnIndex];
            }
        });
        tableScrollPane.setViewportView(customerTable);

        return tableMainPanel;
    }

    private void showAddProductPanel() {
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        JPanel glassPane = new JPanel(null);
        frame.setGlassPane(glassPane);
        glassPane.add(floatingPanel);
        glassPane.setVisible(true);
        floatingPanel.setSize(frame.getContentPane().getSize());
        floatingPanel.setBounds(0, 0, frame.getContentPane().getWidth(), frame.getContentPane().getHeight());
        glassPane.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
            }
        });
        frame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                SwingUtilities.invokeLater(() -> {
                    floatingPanel.setSize(frame.getContentPane().getSize());
                    updateComponents(floatingPanel);
                });
            }
        });
    }

    private JComponent addCustomerPanel() {
        JPanel panel = new JPanel(new MigLayout("wrap 1, fillx, insets 0", "", ""));
        panel.setBackground(new Color(240, 240, 240));
        panel.add(addCustomerWindowHeader(), "growx");
        panel.add(inputDetailsPanel(), "growx");
        panel.add(controlButtons(), "right");
        // updateComponents(panel);
        return panel;
    }

    private JComponent addCustomerWindowHeader() {
        JPanel panel = new JPanel(new MigLayout("insets 0", "", ""));
        cmdCancelAddProduct = new JButton("Add a Customer");
        cmdCancelAddProduct.putClientProperty(FlatClientProperties.STYLE, "" +
                "font:bold +3;");
        cmdCancelAddProduct
                .setIcon(new ImageIcon(new ImageIcon(getClass().getResource("/hapeekidz/assets/icons/back.png"))
                        .getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH)));
        cmdCancelAddProduct.setBorder(new EmptyBorder(9, 10, 9, 10));
        cmdCancelAddProduct.addActionListener((ActionListener) this);
        panel.add(cmdCancelAddProduct, "growx");
        return panel;
    }

    private JComponent inputDetailsPanel() {
        /*
         * TODO: Make fields required
         */
        JPanel panel = new JPanel(new MigLayout("wrap 2, fill, insets 18 20 18 20", "[grow][grow]", "[][][][][][][]"));

        txtName = new JTextField();
        JLabel lblName = new JLabel("Client Name");
        txtName.putClientProperty(FlatClientProperties.STYLE, "" +
                "arc: 5;" +
                "font: +2;");
        txtName.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Add Client Name");
        lblName.putClientProperty(FlatClientProperties.STYLE, "" +
                "font: bold +3;");

        txtDate = new JTextField();
        JLabel lblDate = new JLabel("Date Started");
        txtDate.putClientProperty(FlatClientProperties.STYLE, "" +
                "arc: 5;" +
                "font: +2;");
        txtDate.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Add Date");
        lblDate.putClientProperty(FlatClientProperties.STYLE, "" +
                "font: bold +3;");

        txtUnit = new JTextField();
        JLabel lblUnit = new JLabel("Unit No.");
        txtUnit.putClientProperty(FlatClientProperties.STYLE, "" +
                "arc: 5;" +
                "font: +2;");
        txtUnit.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Add Unit No.");
        lblUnit.putClientProperty(FlatClientProperties.STYLE, "" +
                "font: bold +3;");

        txtInvoiceAmount = new JTextField();
        JLabel lblInvoiceAmount = new JLabel("Invoice Amount");
        txtInvoiceAmount.putClientProperty(FlatClientProperties.STYLE, "" +
                "arc: 5;" +
                "font: +2;");
        txtInvoiceAmount.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Add Invoice Amount");
        lblInvoiceAmount.putClientProperty(FlatClientProperties.STYLE, "" +
                "font: bold +3;");
        JLabel lblEmail = new JLabel("Email");
        lblEmail.putClientProperty(FlatClientProperties.STYLE, "" +
                "font: bold +3;");
        JLabel lblContactNo = new JLabel("Contact No.");

        txtEmail = new JTextField();
        txtEmail.putClientProperty(FlatClientProperties.STYLE, "" +
                "arc: 5;" +
                "font: +2;");
        txtEmail.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Add Email");

        txtContactNumber = new JTextField();
        txtContactNumber.putClientProperty(FlatClientProperties.STYLE, "" +
                "arc: 5;" +
                "font: +2;");
        txtContactNumber.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Add Contact No.");

        panel.add(lblName, "cell 0 0,gapy 8");
        panel.add(lblDate, "cell 1 0,gapy 8");
        panel.add(txtName, "cell 0 1,growx,gapy 16");
        panel.add(txtDate, "cell 1 1,growx,gapy 16");

        panel.add(lblUnit, "cell 0 2,gapy 8");
        panel.add(lblInvoiceAmount, "cell 1 2,gapy 8");
        panel.add(txtUnit, "cell 0 3,growx,gapy 16");
        panel.add(txtInvoiceAmount, "cell 1 3,growx,gapy 16");

        panel.add(lblEmail, "flowy,cell 0 4,growx,gapy 8");
        panel.add(lblContactNo, "cell 1 4,growx,gapy 16");

        panel.add(txtEmail, "cell 0 5,growx,gapy 8");
        panel.add(txtContactNumber, "cell 1 5,growx,gapy 16");

        panel.revalidate();
        panel.repaint();
        return panel;
    }

    private JComponent controlButtons() {
        JPanel panel = new JPanel(new MigLayout("insets 9 10 9 10", "[][]", ""));
        cmdConfirm = new JButton("Confirm");
        cmdConfirm.setPreferredSize(new Dimension(160, 40));
        cmdConfirm.addActionListener((ActionListener) this);
        cmdConfirm.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_BORDERLESS);
        cmdConfirm.putClientProperty(FlatClientProperties.STYLE, "" +
                "arc: 5;" +
                "font: +2;" +
                "background: #16a34a;" +
                "foreground: #ffffff;");

        cmdCancel = new JButton("Cancel");
        cmdCancel.setPreferredSize(new Dimension(160, 40));
        cmdCancel.addActionListener((ActionListener) this);
        cmdCancel.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_BORDERLESS);
        cmdCancel.putClientProperty(FlatClientProperties.STYLE, "" +
                "arc: 5;" +
                "font: +2;" +
                "background: #475569;" +
                "foreground: #ffffff;");
        panel.add(cmdCancel, "right, gapx 0");
        panel.add(cmdConfirm, "right, gapx 0");

        return panel;
    }

}
