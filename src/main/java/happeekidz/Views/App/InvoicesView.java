package happeekidz.Views.App;

import java.awt.*;
import java.awt.List;

import javax.swing.*;
import java.util.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.time.LocalDate;


import net.miginfocom.swing.MigLayout;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;

import happeekidz.Models.App.Invoices;
import happeekidz.Models.App.Products;
import happeekidz.Models.App.Customers;
import happeekidz.Views.App.AppView;

import happeekidz.Controllers.App.GeneratePdf;

import com.formdev.flatlaf.FlatClientProperties;

public class InvoicesView extends JPanel implements ActionListener, MouseListener, TableModelListener {
    private JTable table, formTable;
    private JTextField txtNotes, txtEmail, txtDiscount, txtTax, txtPaymentMethod, txtSearch;
    private JComboBox<String> cmbCustomer, cmbDiscount, cmbStatus, cmbFilter;
    private DatePicker calDateStart, calDateEnd, calDatePaid;
    private JButton cmdShowAddStack, cmdCancel, cmdConfirmAdd, cmdExitStack, cmdConfirmModify, cmdDelete, cmdAddRow,
            cmdClearRow, cmdPrint, cmdSearch;
    private JLabel lblTopPanelBalanceAmount,
            lblBottomPanelBalanceAmount, lblBottomPanelSubtotalAmount, lblBottomPanelDiscountAmount,
            lblBottomPanelTaxAmount;
    int row = 0;
    double balance = 0, subtotal = 0, discount = 0, tax = 0;
    private Customers customers = new Customers();
    private Products products = new Products();
    private Invoices invoices = new Invoices();
    private ArrayList<Object[]> filteredInvoices = new ArrayList<Object[]>();
    private ArrayList<Integer> filteredInvoiceIndexes = new ArrayList<Integer>();
    

    @Override
    public void mouseClicked(MouseEvent e) {
        JTable table = (JTable) e.getSource();
        int column = table.getColumnModel().getColumnIndexAtX(e.getX());
        row = e.getY() / table.getRowHeight();
        row = table.rowAtPoint(e.getPoint());
        if (column == 5) {
            showLayeredPanel(manageInvoicePanel());
        }
    }

    @Override
    public void tableChanged(TableModelEvent e) {
        int formTableRow = e.getFirstRow();
        int formTableColumn = e.getColumn();
        if (formTableColumn == 3 || formTableColumn == 4) {
            try {
                int quantity = Integer.parseInt(formTable.getValueAt(formTableRow, 3).toString());
                double rate = Double.parseDouble(formTable.getValueAt(formTableRow, 4).toString());

                double amount = quantity * rate;
                formTable.setValueAt(String.format("%.2f", amount), formTableRow, 5);

                subtotal = 0;
                for (int i = 0; i < formTable.getRowCount(); i++) {
                    subtotal += Double.parseDouble(formTable.getValueAt(i, 5).toString());
                }

                computeFormTaxDiscountBalance();
                updateFormTaxDiscountBalanceUI();
                updateComponents(formTable);
            } catch (NumberFormatException ex) {
                formTable.setValueAt(formTable.getValueAt(formTableRow, 4), formTableRow, 5);
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        if (e.getSource() == cmdShowAddStack) {
            showLayeredPanel(addInvoicePanel());
        }
        if (e.getSource() == cmdExitStack) {
            JPanel glassPane = (JPanel) frame.getGlassPane();
            glassPane.setVisible(false);
            glassPane.removeAll();
            updateFrame(frame);
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
        if (e.getSource() == cmdAddRow) {
            DefaultTableModel model = (DefaultTableModel) this.formTable.getModel();
            model.addRow(new Object[] { "", "", "", "", "", "", "" });
            updateComponents(this.formTable);
        }
        if (e.getSource() == cmdClearRow) {
            DefaultTableModel model = (DefaultTableModel) this.formTable.getModel();
            model.setRowCount(0);
            updateComponents(this.formTable);
        }
        if (e.getSource() == cmbCustomer) {
            int index = cmbCustomer.getSelectedIndex();
            txtEmail.setText((String) customers.getCustomers()[index][5]);
        }
        // if textfield updates compute balance
        if (e.getSource() == txtDiscount || e.getSource() == txtTax) {
            computeFormTaxDiscountBalance();
            updateFormTaxDiscountBalanceUI();
        }
        if (e.getSource() == cmbDiscount) {
            if (cmbDiscount.getSelectedIndex() == 0) {
                txtDiscount.setText("0");
            } else {
                txtDiscount.setText("0");
            }
        }
        if (e.getSource() == cmdConfirmAdd) {
            if (areFieldsValid()) {
                ArrayList<String[]> products = getListOfTableModel();
                invoices = new Invoices(
                        Integer.parseInt(customers.getCustomers()[cmbCustomer.getSelectedIndex()][0].toString()),
                        calDateStart.getDate(),
                        calDateEnd.getDate(),
                        products,
                        subtotal,
                        discount,
                        Double.parseDouble(txtDiscount.getText()),
                        cmbDiscount.getItemAt(cmbDiscount.getSelectedIndex()),
                        balance,
                        txtNotes.getText());
                
                
                invoices.addInvoice();
                updateTable();
                JPanel glassPane = (JPanel) frame.getGlassPane();
                glassPane.setVisible(false);
                glassPane.removeAll();
                
                
            }
        }
        if (e.getSource() == cmdDelete) {
            int rs = JOptionPane.showConfirmDialog(frame, "Are you sure you want to delete?", "Delete",
                    JOptionPane.YES_NO_OPTION);
            if (rs == JOptionPane.YES_OPTION) {
                // check if filteredInvoices is empty
                if (filteredInvoices.size() > 0) {
                    invoices = new Invoices(Integer.parseInt(filteredInvoices.get(row)[0].toString()));
                    invoices.removeInvoice();
                    filteredInvoices.remove(row);
                    filteredInvoiceIndexes.remove(row);
                    updateTable();
                    JPanel glassPane = (JPanel) frame.getGlassPane();
                    glassPane.setVisible(false);
                    glassPane.removeAll();
                } else {
                    invoices = new Invoices(Integer.parseInt(invoices.getInvoices()[row][0].toString()));
                    invoices.removeInvoice();
                    updateTable();
                    JPanel glassPane = (JPanel) frame.getGlassPane();
                    glassPane.setVisible(false);
                    glassPane.removeAll();
                }
            }
        }
        /*
         * creates a new invoice object with the updated values from the form
         * calls update method from invoice object
         * param: invoice_number, customer_id, invoice_date, due_date, payment_date,
         * products, subtotal, tax, discount, discount_type, total, status,
         * payment_method, message
         */
        
        if (e.getSource() == cmdConfirmModify) {
            updateComponents(this.formTable);
            if (filteredInvoices.size() > 0) {
                ArrayList<String[]> products = getListOfTableModel();
                invoices = new Invoices(
                    Integer.parseInt(filteredInvoices.get(row)[0].toString()),
                    Integer.parseInt(customers.getCustomers()[cmbCustomer.getSelectedIndex()][0].toString()),
                    calDateStart.getDate(),
                    calDateEnd.getDate(),
                    calDatePaid.getDate(),
                    products,
                    subtotal,
                    Double.parseDouble(txtTax.getText()),
                    Double.parseDouble(txtDiscount.getText()),
                    cmbDiscount.getItemAt(cmbDiscount.getSelectedIndex()),
                    balance,
                    cmbStatus.getItemAt(cmbStatus.getSelectedIndex()),
                    txtPaymentMethod.getText(),
                    txtNotes.getText());

                invoices.updateInvoice();
                updateTable();

                JPanel glassPane = (JPanel) frame.getGlassPane();
                glassPane.setVisible(false);
                glassPane.removeAll();
            } else {
                ArrayList<String[]> products = getListOfTableModel();
                invoices = new Invoices(
                    Integer.parseInt(invoices.getInvoices()[row][0].toString()),
                    Integer.parseInt(customers.getCustomers()[cmbCustomer.getSelectedIndex()][0].toString()),
                    calDateStart.getDate(),
                    calDateEnd.getDate(),
                    calDatePaid.getDate(),
                    products,
                    subtotal,
                    Double.parseDouble(txtTax.getText()),

                    Double.parseDouble(txtDiscount.getText()),
                    cmbDiscount.getItemAt(cmbDiscount.getSelectedIndex()),
                    balance,
                    cmbStatus.getItemAt(cmbStatus.getSelectedIndex()),
                    txtPaymentMethod.getText(),
                    txtNotes.getText());

                invoices.updateInvoice();
                updateTable();

                JPanel glassPane = (JPanel) frame.getGlassPane();
                glassPane.setVisible(false);
                glassPane.removeAll();
            }
        }
        if (e.getSource() == cmdPrint) {
            // check if filteredInvoices is empty
            if (filteredInvoices.size() > 0) {
                try {
                    ArrayList<String[]> products = getListOfTableModel();
                    GeneratePdf pdf = new GeneratePdf(
                            Integer.parseInt(filteredInvoices.get(row)[0].toString()),
                            customers.getCustomers()[cmbCustomer.getSelectedIndex()][0].toString(),
                            calDateStart.getDate(),
                            calDateEnd.getDate(),
                            calDatePaid.getDate(),
                            products,
                            subtotal,
                            Double.parseDouble(txtTax.getText()),
                            discount,
                            cmbDiscount.getItemAt(cmbDiscount.getSelectedIndex()),
                            balance);
                    pdf.printRecord(pdf);
                    JPanel glassPane = (JPanel) frame.getGlassPane();
                    glassPane.setVisible(false);
                    glassPane.removeAll();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else {
                try {
                    ArrayList<String[]> products = getListOfTableModel();
                    GeneratePdf pdf = new GeneratePdf(
                            Integer.parseInt(invoices.getInvoices()[row][0].toString()),
                            customers.getCustomers()[cmbCustomer.getSelectedIndex()][0].toString(),
                            calDateStart.getDate(),
                            calDateEnd.getDate(),
                            calDatePaid.getDate(),
                            products,
                            subtotal,
                            Double.parseDouble(txtTax.getText()),
                            discount,
                            cmbDiscount.getItemAt(cmbDiscount.getSelectedIndex()),
                            balance);
                    pdf.printRecord(pdf);
                    JPanel glassPane = (JPanel) frame.getGlassPane();
                    glassPane.setVisible(false);
                    glassPane.removeAll();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        if (e.getSource() == cmdSearch) {
            System.out.println("searching");

            if (cmbFilter.getSelectedItem() == "All Time" && txtSearch.getText().isEmpty()) {
                filteredInvoices.clear();
            }
            if (cmbFilter.getSelectedItem() == "All Time" && !txtSearch.getText().isEmpty()) {
                filteredInvoices.clear();
                for (int i = 0; i < invoices.getInvoices().length; i++) {
                    // if customer first name or last name (from customer id of invoice) contains
                    // search text, or invoice number contains search text, add to filtered invoices
                    String name = customers.getCustomers()[Integer.parseInt(invoices.getInvoices()[i][1].toString())
                            - 1][1].toString().toLowerCase() + " "
                            + customers.getCustomers()[Integer
                                    .parseInt(invoices.getInvoices()[i][1].toString()) - 1][2].toString().toLowerCase();
                    if (name.contains(txtSearch.getText().toLowerCase())
                            || invoices.getInvoices()[i][0].toString().toLowerCase()
                                    .contains(txtSearch.getText().toLowerCase())) {
                        filteredInvoices.add(invoices.getInvoices()[i]);
                        filteredInvoiceIndexes.add(i);
                    }
                }
            }
            if (cmbFilter.getSelectedItem() == "This Week") {
                filteredInvoices.clear();
                if (txtSearch.getText().isEmpty()) {
                    for (int i = 0; i < invoices.getInvoices().length; i++) {
                        if (LocalDate.parse(invoices.getInvoices()[i][3].toString())
                                .isAfter(LocalDate.now().minusDays(7))
                                || LocalDate.parse(invoices.getInvoices()[i][3].toString())
                                        .isEqual(LocalDate.now().minusDays(7))) {
                            filteredInvoices.add(invoices.getInvoices()[i]);
                            filteredInvoiceIndexes.add(i);
                        }
                    }
                } else {
                    for (int i = 0; i < invoices.getInvoices().length; i++) {
                        if (LocalDate.parse(invoices.getInvoices()[i][3].toString())
                                .isAfter(LocalDate.now().minusDays(7))
                                || LocalDate.parse(invoices.getInvoices()[i][3].toString())
                                        .isEqual(LocalDate.now().minusDays(7))) {
                            String name = customers.getCustomers()[Integer
                                    .parseInt(invoices.getInvoices()[i][1].toString()) - 1][1].toString()
                                    .toLowerCase() + " "
                                    + customers.getCustomers()[Integer
                                            .parseInt(invoices.getInvoices()[i][1].toString()) - 1][2].toString()
                                            .toLowerCase();
                            if (name.contains(txtSearch.getText().toLowerCase())
                                    || invoices.getInvoices()[i][0].toString().toLowerCase()
                                            .contains(txtSearch.getText().toLowerCase())) {
                                filteredInvoices.add(invoices.getInvoices()[i]);
                                filteredInvoiceIndexes.add(i);
                            }
                        }
                    }
                }
            }
            if (cmbFilter.getSelectedItem() == "This Month") {
                filteredInvoices.clear();
                if (txtSearch.getText().isEmpty()) {
                    for (int i = 0; i < invoices.getInvoices().length; i++) {
                        if (LocalDate.parse(invoices.getInvoices()[i][3].toString())
                                .isAfter(LocalDate.now().minusDays(30))
                                || LocalDate.parse(invoices.getInvoices()[i][3].toString())
                                        .isEqual(LocalDate.now().minusDays(30))) {
                            filteredInvoices.add(invoices.getInvoices()[i]);
                            filteredInvoiceIndexes.add(i);
                        }
                    }
                } else {
                    for (int i = 0; i < invoices.getInvoices().length; i++) {
                        if (LocalDate.parse(invoices.getInvoices()[i][3].toString())
                                .isAfter(LocalDate.now().minusDays(30))
                                || LocalDate.parse(invoices.getInvoices()[i][3].toString())
                                        .isEqual(LocalDate.now().minusDays(30))) {
                            String name = customers.getCustomers()[Integer
                                    .parseInt(invoices.getInvoices()[i][1].toString()) - 1][1].toString()
                                    .toLowerCase() + " "
                                    + customers.getCustomers()[Integer
                                            .parseInt(invoices.getInvoices()[i][1].toString()) - 1][2].toString()
                                            .toLowerCase();
                            if (name.contains(txtSearch.getText().toLowerCase())
                                    || invoices.getInvoices()[i][0].toString().toLowerCase()
                                            .contains(txtSearch.getText().toLowerCase())) {
                                filteredInvoices.add(invoices.getInvoices()[i]);
                                filteredInvoiceIndexes.add(i);
                            }
                        }
                    }
                }
            }
            if (cmbFilter.getSelectedItem() == "Last 30 Days") {
                filteredInvoices.clear();
                if (txtSearch.getText().isEmpty()) {
                    for (int i = 0; i < invoices.getInvoices().length; i++) {
                        if (LocalDate.parse(invoices.getInvoices()[i][3].toString())
                                .isAfter(LocalDate.now().minusDays(30))
                                || LocalDate.parse(invoices.getInvoices()[i][3].toString())
                                        .isEqual(LocalDate.now().minusDays(30))) {
                            filteredInvoices.add(invoices.getInvoices()[i]);
                            filteredInvoiceIndexes.add(i);
                        }
                    }
                } else {
                    for (int i = 0; i < invoices.getInvoices().length; i++) {
                        if (LocalDate.parse(invoices.getInvoices()[i][3].toString())
                                .isAfter(LocalDate.now().minusDays(30))
                                || LocalDate.parse(invoices.getInvoices()[i][3].toString())
                                        .isEqual(LocalDate.now().minusDays(30))) {
                            String name = customers.getCustomers()[Integer
                                    .parseInt(invoices.getInvoices()[i][1].toString()) - 1][1].toString()
                                    .toLowerCase() + " "
                                    + customers.getCustomers()[Integer
                                            .parseInt(invoices.getInvoices()[i][1].toString()) - 1][2].toString()
                                            .toLowerCase();
                            if (name.contains(txtSearch.getText().toLowerCase())
                                    || invoices.getInvoices()[i][0].toString().toLowerCase()
                                            .contains(txtSearch.getText().toLowerCase())) {
                                filteredInvoices.add(invoices.getInvoices()[i]);
                                filteredInvoiceIndexes.add(i);
                            }
                        }
                    }
                }
            }
            if (cmbFilter.getSelectedItem() == "This Year") {
                filteredInvoices.clear();
                if (txtSearch.getText().isEmpty()) {
                    for (int i = 0; i < invoices.getInvoices().length; i++) {
                        if (LocalDate.parse(invoices.getInvoices()[i][3].toString())
                                .isAfter(LocalDate.now().minusDays(365))
                                || LocalDate.parse(invoices.getInvoices()[i][3].toString())
                                        .isEqual(LocalDate.now().minusDays(365))) {
                            filteredInvoices.add(invoices.getInvoices()[i]);
                            filteredInvoiceIndexes.add(i);
                        }
                    }
                } else {
                    for (int i = 0; i < invoices.getInvoices().length; i++) {
                        if (LocalDate.parse(invoices.getInvoices()[i][3].toString())
                                .isAfter(LocalDate.now().minusDays(365))
                                || LocalDate.parse(invoices.getInvoices()[i][3].toString())
                                        .isEqual(LocalDate.now().minusDays(365))) {
                            String name = customers.getCustomers()[Integer
                                    .parseInt(invoices.getInvoices()[i][1].toString()) - 1][1].toString()
                                    .toLowerCase() + " "
                                    + customers.getCustomers()[Integer
                                            .parseInt(invoices.getInvoices()[i][1].toString()) - 1][2].toString()
                                            .toLowerCase();
                            if (name.contains(txtSearch.getText().toLowerCase())
                                    || invoices.getInvoices()[i][0].toString().toLowerCase()
                                            .contains(txtSearch.getText().toLowerCase())) {
                                filteredInvoices.add(invoices.getInvoices()[i]);
                                filteredInvoiceIndexes.add(i);
                            }
                        }
                    }
                }
            }

            if (filteredInvoices.size() == 0) {
                table.setModel(new DefaultTableModel(getTableRowModelFrom(invoices.getInvoices()),
                        new String[] { "", "NO.", "CUSTOMER", "AMOUNT", "STATUS",
                                "ACTION" }));
                updateComponents(table);
                updateTable();
            } else {
                table.setModel(new DefaultTableModel(
                        getTableRowModelFrom(filteredInvoices.toArray(new Object[filteredInvoices.size()][])),
                        new String[] { "", "NO.", "CUSTOMER", "AMOUNT", "STATUS",
                                "ACTION" }));
                table.getColumnModel().getColumn(0).setPreferredWidth(0);
                table.getColumnModel().getColumn(1).setMaxWidth(50);
                if (filteredInvoices.toArray(new Object[filteredInvoices.size()][]).length > 0) {
                    table.getColumnModel().getColumn(4).setCellRenderer(new ImageRenderer());
                    table.getColumnModel().getColumn(5).setCellRenderer(new ButtonRenderer());
                }
            }
        }
    }

    public InvoicesView() {
        init();
    }

    public void init() {
        setLayout(new MigLayout("wrap 1, fill, gapx 0, insets 9 10 9 10", "", ""));
        add(addWindowHeader(), "growx, left");
        add(addGraphicsPanel(), "growx, left");
        add(addControlPanel(), "growx, growy");
        add(addTablePanel(), "growx, growy");
    }

    private <T extends JComponent> void updateComponents(T component) {
        component.revalidate();
        component.repaint();
    }

    private void updateFrame(JFrame frame) {
        frame.revalidate();
        frame.repaint();
    }

    private void updateTable() {
        Object[][] rowData = invoices.getInvoices() == null || invoices.getInvoices().length == 0
                ? null
                : getTableRowModelFrom(invoices.getInvoices());
        String[] columnNames = { "", "NO.", "CUSTOMER", "AMOUNT", "STATUS", "ACTION" };
        DefaultTableModel model = new DefaultTableModel(rowData, columnNames);
        table.setModel(model);
        table.getColumnModel().getColumn(0).setPreferredWidth(0);
        table.getColumnModel().getColumn(1).setMaxWidth(50);
        if (invoices.getInvoices() != null && invoices.getInvoices().length > 0) {
            table.getColumnModel().getColumn(4).setCellRenderer(new ImageRenderer());
            table.getColumnModel().getColumn(5).setCellRenderer(new ButtonRenderer());
        }
        updateComponents(table);
    }

    private JComponent addWindowHeader() {
        JPanel panel = new JPanel(new MigLayout("insets 0, gapx 0", "", ""));
        JLabel lblPanelName = new JLabel("Invoices");
        lblPanelName.setBorder(new EmptyBorder(9, 10, 9, 10));
        lblPanelName.putClientProperty(FlatClientProperties.STYLE, "" +
                "font:bold +2;");
        lblPanelName.setBorder(new EmptyBorder(9, 10, 9, 10));
        panel.add(lblPanelName, "growx");
        return panel;
    }

    private JComponent addGraphicsPanel() {
        JPanel GraphicsPanel = new JPanel(new MigLayout("wrap 4, fillx, insets 0, gapx 4", "", ""));
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

    private JComponent addControlPanel() {
        JPanel panel = new JPanel(new MigLayout("fill, insets 9 10 9 10", "", ""));
        txtSearch = newFormTextField("Search", "");
        cmbFilter = newFormComboBox(
                new String[] { "All Time", "This Week", "This Month", "Last 30 Days", "This Year" });
        cmbFilter.setPreferredSize(new Dimension(160, 30));
        cmdSearch = newFormButton("Search", "arc: 5;" + "font: +2;" + "background: #475569;" + "foreground: #ffffff;");
        if (invoices.getInvoices() == null || invoices.getInvoices().length == 0) {
            cmdSearch.setEnabled(false);
        }
        cmdShowAddStack = newFormButton("Add an Invoice",
                "arc: 5;" + "font: +2;" + "background: #16a34a;" + "foreground: #ffffff;");

        panel.add(txtSearch, "growx");
        panel.add(cmbFilter, "");
        panel.add(cmdSearch, "");
        panel.add(cmdShowAddStack, "right");
        return panel;
    }

    private JComponent addTablePanel() {
        JPanel panel = new JPanel(new MigLayout("fill, insets 0", "", ""));
        Object[][] rowData = invoices.getInvoices() == null || invoices.getInvoices().length == 0
                ? null
                : getTableRowModelFrom(invoices.getInvoices());
        String[] columnNames = { "", "NO.", "CUSTOMER", "AMOUNT", "STATUS", "ACTION" };
        DefaultTableModel model = new DefaultTableModel(rowData, columnNames);
        table = new JTable(model);
        table.setRowHeight(40);
        table.setShowVerticalLines(true);
        table.setShowHorizontalLines(true);
        table.setGridColor(Color.decode("#cccccc"));
        table.addMouseListener(this);
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setResizingAllowed(false);
        table.setDefaultEditor(Object.class, null);
        ((DefaultTableCellRenderer) table.getTableHeader().getDefaultRenderer())
                .setHorizontalAlignment(SwingConstants.LEFT);

        table.getColumnModel().getColumn(0).setPreferredWidth(0);
        table.getColumnModel().getColumn(1).setMaxWidth(50);
        if (invoices.getInvoices() != null && invoices.getInvoices().length > 0) {
            table.getColumnModel().getColumn(4).setCellRenderer(new ImageRenderer());
            table.getColumnModel().getColumn(5).setCellRenderer(new ButtonRenderer());
        }

        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, "growx, growy");
        return panel;
    }

    class ButtonRenderer extends JButton implements TableCellRenderer {
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                int row, int column) {
            setText("Manage Invoice");
            putClientProperty(FlatClientProperties.STYLE, "" +
                    "foreground: #16a34a;");
            putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_BORDERLESS);
            setHorizontalAlignment(SwingConstants.CENTER);
            return this;
        }
    }

    class ImageRenderer extends JLabel implements TableCellRenderer {
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                int row, int column) {
            Object valueAt = table.getModel().getValueAt(row, column);
            // check if string is paid or unpaid
            if (valueAt.toString().equals("Paid")) {
                setIcon(new ImageIcon(new ImageIcon(getClass().getResource("/happeekidz/assets/icons/paid.png"))
                        .getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH)));
                setText("Paid");
            } else {
                if (LocalDate.parse(invoices.getInvoices()[row][3].toString())
                        .isAfter(LocalDate.now())
                        || LocalDate.parse(invoices.getInvoices()[row][3].toString())
                                .isEqual(LocalDate.now())) {
                    setIcon(new ImageIcon(new ImageIcon(getClass().getResource("/happeekidz/assets/icons/due.png"))
                            .getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH)));
                    setText("Due in " + Math.abs(LocalDate.parse(invoices.getInvoices()[row][3].toString().toString())
                            .until(LocalDate.now()).getDays()) + " days");
                } else {
                    setIcon(new ImageIcon(new ImageIcon(getClass().getResource("/happeekidz/assets/icons/overdue.png"))
                            .getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH)));
                    setText("Overdue by " + Math.abs(LocalDate.parse(invoices.getInvoices()[row][3].toString())
                            .until(LocalDate.now()).getDays()) + " days");
                }
            }
            setHorizontalAlignment(SwingConstants.CENTER);
            return this;
        }
    }

    public void showLayeredPanel(JComponent component) {
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        JPanel glassPane = new JPanel(null);
        frame.setGlassPane(glassPane);
        glassPane.add(component);
        glassPane.setVisible(true);
        component.setSize(frame.getContentPane().getSize());
        component.setBounds(0, 0, frame.getContentPane().getWidth(), frame.getContentPane().getHeight());
        glassPane.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
            }
        });
        frame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                SwingUtilities.invokeLater(() -> {
                    component.setSize(frame.getContentPane().getSize());
                    updateComponents(component);
                });
            }
        });
    }

    public JComponent addInvoicePanel() {
        JPanel panel = new JPanel(new MigLayout("wrap 1, fillx, insets 0", "", ""));
        panel.setBackground(new Color(240, 240, 240));
        panel.add(addFloatingPanelHeader("Add an Invoice"), "growx");
        panel.add(addInputFieldsPanel(), "growx");
        panel.add(addNewInvoiceControlButtonsPanel(), "right");
        updateComponents(panel);
        return panel;
    }

    private JComponent manageInvoicePanel() {
        JPanel panel = new JPanel(new MigLayout("wrap 1, fillx, insets 0", "", ""));
        panel.setBackground(new Color(240, 240, 240));
        panel.add(addFloatingPanelHeader("Manage Invoice"), "growx");
        panel.add(addModifyInvoiceInputFieldsPanel(), "growx");
        panel.add(addModifyInvoiceControlButtonsPanel(), "right");
        updateComponents(panel);
        return panel;
    }

    private JComponent addFloatingPanelHeader(String title) {
        JPanel panel = new JPanel(new MigLayout("insets 0", "", ""));
        cmdExitStack = newFormButton(title, "arc: 5;" + "font: +2;" + "background: #16a34a;" + "foreground: #ffffff;");
        cmdExitStack.putClientProperty(FlatClientProperties.STYLE, "" +
                "font:bold +3;");
        cmdExitStack
                .setIcon(new ImageIcon(new ImageIcon(getClass().getResource("/happeekidz/assets/icons/back.png"))
                        .getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH)));
        cmdExitStack.setBorder(new EmptyBorder(9, 10, 9, 10));
        cmdExitStack.addActionListener(this);
        panel.add(cmdExitStack, "growx");
        return panel;
    }

    private JComponent addInputFieldsPanel() {
        balance = 0;
        subtotal = 0;
        discount = 0;
        tax = 0;

        String[] customer_names = new String[customers.getCustomers().length];
        for (int i = 0; i < customer_names.length; i++) {
            customer_names[i] = (String) customers.getCustomers()[i][1];
            customer_names[i] += " " + (String) customers.getCustomers()[i][2];
        }

        JPanel panel = new JPanel(new MigLayout("fillx, wrap 3, insets 9 10 9 10", "", ""));

        JLabel lblCustomer = newFormLabel("Customer");
        cmbCustomer = newFormComboBox(customer_names);
        cmbCustomer.addActionListener(this);

        JLabel lblEmail = newFormLabel("Email");
        txtEmail = newFormTextField("Email", "");

        JPanel topPanel = new JPanel(new MigLayout("wrap 1, insets 0", "", ""));
        JLabel lblTopPanelBalanceTitle = new JLabel("BALANCE DUE");
        lblTopPanelBalanceTitle.putClientProperty(FlatClientProperties.STYLE, "" +
                "font: light;");
        lblTopPanelBalanceAmount = new JLabel("PHP" + String.format("%.2f", balance));
        lblTopPanelBalanceAmount.putClientProperty(FlatClientProperties.STYLE, "" +
                "font: bold +10;");
        topPanel.add(lblTopPanelBalanceTitle, "growx, wrap, right");
        topPanel.add(lblTopPanelBalanceAmount, "growx, right");

        JLabel lblDateStart = newFormLabel("Invoice Date");
        calDateStart = newFormDatePicker("Invoice Date", LocalDate.now());

        JLabel lblDateEnd = newFormLabel("Due Date");
        calDateEnd = newFormDatePicker("Due Date", LocalDate.now());

        JLabel lblInvoiceDetails = newFormLabel("Invoice Details");
        JComponent formTable = newFormTablePanel(null);

        JComponent formTableButtons = newFormTableButtons();

        JLabel lblBottomPanelSubtotalTitle = newFormLabel("Subtotal");
        lblBottomPanelSubtotalAmount = newFormLabel("PHP" + String.format("%.2f", subtotal));
        lblBottomPanelSubtotalAmount.setHorizontalAlignment(SwingConstants.RIGHT);
        
        

        cmbDiscount = newFormComboBox(new String[] { "Discount Percent", "Discount Value" });
        cmbDiscount.setSelectedIndex(0);
        cmbDiscount.addActionListener(this);
        
        

        txtDiscount = newFormTextField("", String.format("%.2f", discount));
        txtDiscount.setHorizontalAlignment(SwingConstants.RIGHT);
        txtDiscount.addActionListener(this);

        lblBottomPanelDiscountAmount = newFormLabel("PHP" + String.format("%.2f", discount));
        lblBottomPanelDiscountAmount.setHorizontalAlignment(SwingConstants.RIGHT);

        JLabel lblTax = newFormLabel("Tax Percent");
        lblTax.setHorizontalAlignment(SwingConstants.RIGHT);
        txtTax = newFormTextField("", String.format("%.2f", tax));
        txtTax.setHorizontalAlignment(SwingConstants.RIGHT);
        txtTax.addActionListener(this);

        lblBottomPanelTaxAmount = newFormLabel("PHP" + String.format("%.2f", tax));
        lblBottomPanelTaxAmount.setHorizontalAlignment(SwingConstants.RIGHT);

        JLabel lblBottomPanelBalanceTitle = newFormLabel("Balance Due");
        lblBottomPanelBalanceTitle.setHorizontalAlignment(SwingConstants.RIGHT);
        lblBottomPanelBalanceAmount = newFormLabel("PHP" + String.format("%.2f", balance));
        lblBottomPanelBalanceAmount.setHorizontalAlignment(SwingConstants.RIGHT);

        JLabel lblNotes = newFormLabel("Invoice Message");
        txtNotes = newFormTextArea("This message will show up in the invoice", "");

        JPanel bottomPanel = new JPanel(new MigLayout("wrap 3, insets 0", "", ""));

        bottomPanel.add(lblBottomPanelSubtotalTitle, "spanx 2");
        bottomPanel.add(lblBottomPanelSubtotalAmount, "wrap, gapleft 60");
        bottomPanel.add(cmbDiscount, "right");
        bottomPanel.add(txtDiscount, "right");
        bottomPanel.add(lblBottomPanelDiscountAmount, "right, gapleft 60");
        bottomPanel.add(lblTax, "right");
        bottomPanel.add(txtTax, "right");
        bottomPanel.add(lblBottomPanelTaxAmount, "right, gapleft 60");
        bottomPanel.add(lblBottomPanelBalanceTitle, "spanx 2, right");
        bottomPanel.add(lblBottomPanelBalanceAmount, "right, gapleft 60");

        panel.add(lblCustomer, "growx");
        panel.add(lblEmail, "growx, wrap");

        panel.add(cmbCustomer, "growx");
        panel.add(txtEmail, "growx");
        panel.add(topPanel, "growx, gapleft 60");

        panel.add(lblDateStart, "growx");
        panel.add(lblDateEnd, "growx, wrap");

        panel.add(calDateStart, "growx");
        panel.add(calDateEnd, "growx, wrap");

        panel.add(lblInvoiceDetails, "growx, spanx 3, wrap");
        panel.add(formTable, "growx, spanx 3, wrap");

        panel.add(formTableButtons, "growx, wrap");

        panel.add(lblNotes, "growx, wrap");
        panel.add(txtNotes, "growx");
        panel.add(bottomPanel, "spanx 2, right, top");

        updateComponents(bottomPanel);
        return panel;
    }

    private JComponent addModifyInvoiceInputFieldsPanel() {
        String[] customer_names = new String[customers.getCustomers().length];
        for (int i = 0; i < customer_names.length; i++) {
            customer_names[i] = (String) customers.getCustomers()[i][1];
            customer_names[i] += " " + (String) customers.getCustomers()[i][2];
        }

        JPanel panel = new JPanel(new MigLayout("fillx, wrap 3, insets 9 10 9 10", "", ""));

        JLabel lblCustomer = newFormLabel("Customer");
        cmbCustomer = newFormComboBox(customer_names);
        cmbCustomer.addActionListener(this);
        JLabel lblEmail = newFormLabel("Email");
        txtEmail = newFormTextField("Email", "");
        JPanel topPanel = new JPanel(new MigLayout("wrap 1, insets 0", "", ""));
        JLabel lblTopPanelBalanceTitle = new JLabel("BALANCE DUE");
        lblTopPanelBalanceTitle.putClientProperty(FlatClientProperties.STYLE, "" +
                "font: light;");
        lblTopPanelBalanceAmount = new JLabel("PHP" + String.format("%.2f", balance));
        lblTopPanelBalanceAmount.putClientProperty(FlatClientProperties.STYLE, "" +
                "font: bold +10;");
        topPanel.add(lblTopPanelBalanceTitle, "growx, wrap, right");
        topPanel.add(lblTopPanelBalanceAmount, "growx, right");
        JLabel lblDateStart = newFormLabel("Invoice Date");
        calDateStart = newFormDatePicker("Invoice Date", LocalDate.now());
        JLabel lblDateEnd = newFormLabel("Due Date");
        calDateEnd = newFormDatePicker("Due Date", LocalDate.now());
        JLabel lblStatus = newFormLabel("Status");
        cmbStatus = newFormComboBox(new String[] { "Unpaid", "Paid" });
        cmbStatus.addActionListener(this);
        JLabel lblPaymentMethod = newFormLabel("Payment Method");
        txtPaymentMethod = newFormTextField("Payment Method", "");
        JLabel lblDatePaid = newFormLabel("Date Paid");
        calDatePaid = newFormDatePicker("Payment Date", LocalDate.now());
        JLabel lblInvoiceDetails = newFormLabel("Invoice Details");
        JComponent formTableButtons = newFormTableButtons();
        JLabel lblBottomPanelSubtotalTitle = newFormLabel("Subtotal");
        lblBottomPanelSubtotalAmount = newFormLabel("PHP" + String.format("%.2f", subtotal));
        lblBottomPanelSubtotalAmount.setHorizontalAlignment(SwingConstants.RIGHT);
        
        
        cmbDiscount = newFormComboBox(new String[] { "Discount Percent", "Discount Value" });
        cmbDiscount.addActionListener(this);
        
        
        txtDiscount = newFormTextField("", String.format("%.2f", discount));
        txtDiscount.setHorizontalAlignment(SwingConstants.RIGHT);
        txtDiscount.addActionListener(this);
        lblBottomPanelDiscountAmount = newFormLabel("PHP" + String.format("%.2f", discount));
        lblBottomPanelDiscountAmount.setHorizontalAlignment(SwingConstants.RIGHT);
        JLabel lblTax = newFormLabel("Tax Percent");
        lblTax.setHorizontalAlignment(SwingConstants.RIGHT);
        txtTax = newFormTextField("", String.format("%.2f", tax));
        txtTax.setHorizontalAlignment(SwingConstants.RIGHT);
        txtTax.addActionListener(this);
        lblBottomPanelTaxAmount = newFormLabel("PHP" + String.format("%.2f", tax));
        lblBottomPanelTaxAmount.setHorizontalAlignment(SwingConstants.RIGHT);
        JLabel lblBottomPanelBalanceTitle = newFormLabel("Balance Due");
        lblBottomPanelBalanceTitle.setHorizontalAlignment(SwingConstants.RIGHT);
        lblBottomPanelBalanceAmount = newFormLabel("PHP" + String.format("%.2f", balance));
        lblBottomPanelBalanceAmount.setHorizontalAlignment(SwingConstants.RIGHT);

        JLabel lblNotes = newFormLabel("Invoice Message");
        txtNotes = newFormTextArea("This message will show up in the invoice", "");

        JPanel bottomPanel = new JPanel(new MigLayout("wrap 3, insets 0", "", ""));
        JComponent formTable = newFormTablePanel(
                new DefaultTableModel(getFormTableRowModelFrom(invoices.getInvoices()[row][5].toString()),
                        new String[] { "", "PRODUCT/SERVICE", "DESCRIPTION", "QTY", "RATE", "AMOUNT", "" }));
        // fire table data changed
        this.formTable.getModel().addTableModelListener(this);
        bottomPanel.add(lblBottomPanelSubtotalTitle, "spanx 2");
        bottomPanel.add(lblBottomPanelSubtotalAmount, "wrap, gapleft 60");
        bottomPanel.add(cmbDiscount, "right");
        bottomPanel.add(txtDiscount, "right");
        bottomPanel.add(lblBottomPanelDiscountAmount, "right, gapleft 60");
        bottomPanel.add(lblTax, "right");
        bottomPanel.add(txtTax, "right");
        bottomPanel.add(lblBottomPanelTaxAmount, "right, gapleft 60");
        bottomPanel.add(lblBottomPanelBalanceTitle, "spanx 2, right");
        bottomPanel.add(lblBottomPanelBalanceAmount, "right, gapleft 60");
        panel.add(lblCustomer, "growx");
        panel.add(lblEmail, "growx, wrap");
        panel.add(cmbCustomer, "growx");
        panel.add(txtEmail, "growx");
        panel.add(topPanel, "growx, gapleft 60");
        panel.add(lblDateStart, "growx");
        panel.add(lblDateEnd, "growx, wrap");
        panel.add(calDateStart, "growx");
        panel.add(calDateEnd, "growx, wrap");
        panel.add(lblStatus, "growx");
        panel.add(lblPaymentMethod, "growx");
        panel.add(lblDatePaid, "growx");
        panel.add(cmbStatus, "growx");
        panel.add(txtPaymentMethod, "growx");
        panel.add(calDatePaid, "growx, wrap");
        panel.add(lblInvoiceDetails, "growx, spanx 3, wrap");
        panel.add(formTable, "growx, spanx 3, wrap");
        panel.add(formTableButtons, "growx, wrap");
        panel.add(lblNotes, "growx, wrap");
        panel.add(txtNotes, "growx");
        panel.add(bottomPanel, "spanx 2, right, top");
        
        
        


        // modify invoice section start
        if (filteredInvoices.size() > 0) {
            int index = 0;
            for (int i = 0; i < customers.getCustomers().length; i++) {
                if (customers.getCustomers()[i][0].toString().equals(invoices.getInvoices()[filteredInvoiceIndexes.get(row)][1].toString())) {
                    index = i;
                    break;
                }
            }
            cmbCustomer.setSelectedIndex(index);
            calDateStart.setDate(LocalDate.parse(invoices.getInvoices()[filteredInvoiceIndexes.get(row)][2].toString()));
            calDateEnd.setDate(LocalDate.parse(invoices.getInvoices()[filteredInvoiceIndexes.get(row)][3].toString()));
            txtEmail.setText((String) customers.getCustomers()[index][5]);
            System.out.println("tax: " + invoices.getInvoices()[filteredInvoiceIndexes.get(row)][7].toString());
            txtTax.setText(invoices.getInvoices()[filteredInvoiceIndexes.get(row)][7].toString());
            System.out.println();
            txtDiscount.setText(invoices.getInvoices()[row][8].toString());
            cmbDiscount.setSelectedItem(invoices.getInvoices()[row][8].toString());  
            cmbStatus.setSelectedItem(invoices.getInvoices()[filteredInvoiceIndexes.get(row)][10].toString());
            txtPaymentMethod.setText(invoices.getInvoices()[filteredInvoiceIndexes.get(row)][11] == null ? "" : invoices.getInvoices()[filteredInvoiceIndexes.get(row)][11].toString());
            txtNotes.setText(invoices.getInvoices()[filteredInvoiceIndexes.get(row)][12] == null ? "" : invoices.getInvoices()[filteredInvoiceIndexes.get(row)][12].toString());
            computeFormTaxDiscountBalance();
            updateFormTaxDiscountBalanceUI();
            updateComponents(bottomPanel);
            return panel;
            
        }
            int index = 0;
            for (int i = 0; i < customers.getCustomers().length; i++) {
                if (customers.getCustomers()[i][0].toString().equals(invoices.getInvoices()[row][1].toString())) {
                    index = i;
                    break;
                }
            }
            cmbCustomer.setSelectedIndex(index);
            calDateStart.setDate(LocalDate.parse(invoices.getInvoices()[row][2].toString()));
            calDateEnd.setDate(LocalDate.parse(invoices.getInvoices()[row][3].toString()));
            txtEmail.setText((String) customers.getCustomers()[index][5]);
            txtTax.setText(invoices.getInvoices()[row][7].toString());
            cmbDiscount.setSelectedItem(invoices.getInvoices()[row][9].toString());  
            txtDiscount.setText(invoices.getInvoices()[row][8].toString());
            cmbStatus.setSelectedItem(invoices.getInvoices()[row][10].toString());
            txtPaymentMethod
                    .setText(invoices.getInvoices()[row][11] == null ? "" : invoices.getInvoices()[row][11].toString());
            txtNotes.setText(invoices.getInvoices()[row][12] == null ? "" : invoices.getInvoices()[row][12].toString());
            // modify invoice section end
            // for each column in this.formTable print the value of the column
            computeFormTaxDiscountBalance();
            updateFormTaxDiscountBalanceUI();
            updateComponents(bottomPanel);
            return panel;
    }

    private JComponent addNewInvoiceControlButtonsPanel() {
        JPanel panel = new JPanel(new MigLayout("insets 9 10 9 10", "[][]", ""));
        cmdCancel = newFormButton("Cancel", "arc: 5;" + "font: +2;" + "background: #475569;" + "foreground: #ffffff;");
        cmdConfirmAdd = newFormButton("Confirm",
                "arc: 5;" + "font: +2;" + "background: #16a34a;" + "foreground: #ffffff;");
        panel.add(cmdCancel, "right, gapx 0");
        panel.add(cmdConfirmAdd, "right, gapx 0");

        return panel;
    }

    private JComponent addModifyInvoiceControlButtonsPanel() {
        JPanel panel = new JPanel(new MigLayout("insets 9 10 9 10", "[][]", ""));
        cmdPrint = newFormButton("Print", "arc: 5;" + "font: +2;" + "background: #475569;" + "foreground: #ffffff;");
        cmdDelete = newFormButton("Delete", "arc: 5;" + "font: +2;" + "background: #475569;" + "foreground: #ffffff;");
        cmdCancel = newFormButton("Cancel", "arc: 5;" + "font: +2;" + "background: #475569;" + "foreground: #ffffff;");
        cmdConfirmModify = newFormButton("Update",
                "arc: 5;" + "font: +2;" + "background: #16a34a;" + "foreground: #ffffff;");
        panel.add(cmdPrint, "right, gapx 0");
        panel.add(cmdDelete, "right, gapx 0");
        panel.add(cmdCancel, "right, gapx 0");
        panel.add(cmdConfirmModify, "right, gapx 0");
        return panel;
    }

    private JTextField newFormTextField(String placeholder, String setText) {
        JTextField txtField = new JTextField();
        txtField.putClientProperty(FlatClientProperties.STYLE, "" +
                "arc: 5;" +
                "font: +2;");
        txtField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, placeholder);
        txtField.setText(setText);
        return txtField;
    }

    private JLabel newFormLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.putClientProperty(FlatClientProperties.STYLE, "" +
                "font: bold +3;");
        return lbl;
    }

    private JCheckBox newFormCheckBox(String text) {
        JCheckBox chkBox = new JCheckBox(text);
        chkBox.putClientProperty(FlatClientProperties.STYLE, "" +
                "font: bold +3;");
        return chkBox;
    }

    private JTextField newFormTextArea(String placeholder, String setText) {
        JTextField txtField = new JTextField();
        txtField.setPreferredSize(new Dimension(0, 200));
        txtField.putClientProperty(FlatClientProperties.STYLE, "" +
                "arc: 5;" +
                "font: +2;");

        txtField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, placeholder);
        txtField.putClientProperty(FlatClientProperties.STYLE, "" +
                "showClearButton:true");

        txtField.setText(setText);
        return txtField;
    }

    private JComboBox<String> newFormComboBox(String[] arr) {
        JComboBox<String> cmbBox = new JComboBox<String>(arr);
        cmbBox.setLightWeightPopupEnabled(false);
        cmbBox.putClientProperty(FlatClientProperties.STYLE, "" +
                "arc: 5;" +
                "font: +2;");
        return cmbBox;
    }

    private JButton newFormButton(String text, String style) {
        JButton btn = new JButton(text);
        btn.setPreferredSize(new Dimension(160, 40));
        btn.addActionListener(this);
        btn.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_BORDERLESS);
        btn.putClientProperty(FlatClientProperties.STYLE, style);
        return btn;
    }

    private DatePicker newFormDatePicker(String placeholder, LocalDate setDate) {
        DatePickerSettings dateSettings = new DatePickerSettings();
        dateSettings.setFormatForDatesCommonEra("yyyy-MM-dd");
        dateSettings.setFormatForDatesBeforeCommonEra("uuuu-MM-dd");
        dateSettings.setAllowEmptyDates(false);

        DatePicker datePicker = new DatePicker(dateSettings);
        datePicker.getComponentToggleCalendarButton().setPreferredSize(new Dimension(0, 35));
        datePicker.getComponentDateTextField().setBorder(BorderFactory.createLineBorder(Color.decode("#cccccc")));
        datePicker.putClientProperty(FlatClientProperties.STYLE, "" +
                "arc: 5;" +
                "font: +2;");
        datePicker.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, placeholder);
        datePicker.setDate(setDate);
        return datePicker;
    }

    private JComponent newFormTablePanel(DefaultTableModel importedModel) {
        JPanel panel = new JPanel(new MigLayout("fillx, insets 0", "", ""));
        Object[][] data = { { "", "", "", "", "", "", "" }, };
        String[] columnNames = { "", "PRODUCT/SERVICE", "DESCRIPTION", "QTY", "RATE", "AMOUNT", "" };

        String[] product_names = new String[products.getProducts().length];
        for (int i = 0; i < product_names.length; i++) {
            product_names[i] = (String) products.getProducts()[i][1];
        }
        DefaultTableModel model = importedModel == null ? new DefaultTableModel(data, columnNames) : importedModel;
        formTable = new JTable(model);
        formTable.setRowHeight(40);
        formTable.setShowVerticalLines(true);
        formTable.setShowHorizontalLines(true);
        formTable.setGridColor(Color.decode("#cccccc"));
        formTable.getTableHeader().setReorderingAllowed(false);
        formTable.getTableHeader().setResizingAllowed(false);

        formTable.getColumnModel().getColumn(0).setPreferredWidth(0);
        formTable.getColumnModel().getColumn(6).setPreferredWidth(0);

        model.addTableModelListener(this);
        formTable.getColumnModel().getColumn(0).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                setText((row + 1) + "");
                setHorizontalAlignment(JLabel.CENTER);
                return this;
            }
        });

        formTable.getColumnModel().getColumn(1)
                .setCellEditor(new DefaultCellEditor(new JComboBox<String>(product_names)) {
                    @Override
                    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected,
                            int row,
                            int column) {
                        JComboBox<String> cmbBox = (JComboBox<String>) super.getTableCellEditorComponent(table, value,
                                isSelected, row, column);
                        cmbBox.setLightWeightPopupEnabled(false);
                        cmbBox.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                int selectedRow = formTable.getSelectedRow();
                                if (selectedRow == row) {
                                    int index = cmbBox.getSelectedIndex();
                                    formTable.setValueAt(products.getProducts()[index][2], row, 2);
                                    formTable.setValueAt(products.getProducts()[index][4], row, 4);
                                    formTable.setValueAt(formTable.getValueAt(row, 4), row, 5);
                                    updateComponents(formTable);
                                    if (products.getProducts()[index][5].toString().equals("0")) {
                                        txtTax.setText("0");
                                        txtTax.setEnabled(false);
                                    }
                                }
                            }
                        });
                        return cmbBox;
                    }
                });
        formTable.getColumnModel().getColumn(6).setCellRenderer(new DeleteButtonRenderer());
        formTable.getColumnModel().getColumn(6).setCellEditor(new DeleteButtonRenderer());

        JScrollPane scrollPane = new JScrollPane(formTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        panel.add(scrollPane, "growx");
        return panel;
    }

    class DeleteButtonRenderer extends AbstractCellEditor implements TableCellRenderer, TableCellEditor {
        private JButton button;
        private int currentRow;

        public DeleteButtonRenderer() {
            this.button = new JButton();
            this.button.setBorder(BorderFactory.createEmptyBorder());
            this.button
                    .setIcon(new ImageIcon(new ImageIcon(getClass().getResource("/happeekidz/assets/icons/delete.png"))
                            .getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH)));
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (currentRow >= 0) {
                        ((DefaultTableModel) formTable.getModel()).removeRow(currentRow);
                    }
                }
            });
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                int row, int column) {
            return button;
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row,
                int column) {
            currentRow = row;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            return null;
        }
    }

    private JComponent newFormTableButtons() {
        JPanel panel = new JPanel(new MigLayout("insets 9 10 9 10", "", ""));
        cmdAddRow = newFormButton("Add Row", "arc: 5;" + "background: #475569;" + "foreground: #ffffff;");
        cmdClearRow = newFormButton("Clear Row", "arc: 5;" + "background: #475569;" + "foreground: #ffffff;");
        panel.add(cmdAddRow, "growx, gapx 0");
        panel.add(cmdClearRow, "growx, gapx 0");
        return panel;
    }

    private void computeFormTaxDiscountBalance() {
        balance = 0;
        subtotal = 0;
        discount = 0;
        tax = 0;
        for (int i = 0; i < formTable.getRowCount(); i++) {
            subtotal += Double.parseDouble(formTable.getValueAt(i, 5).toString());
        }
        if (cmbDiscount.getSelectedIndex() == 0) {
            discount = Double.parseDouble(txtDiscount.getText());
            discount = subtotal * (discount / 100);
            tax = (subtotal - discount) * (Double.parseDouble(txtTax.getText()) / 100);
            balance = subtotal + tax - discount;
        } else {
            discount = Double.parseDouble(txtDiscount.getText());
            tax = (subtotal - discount) * (Double.parseDouble(txtTax.getText()) / 100);
            balance = subtotal + tax - discount;
        }
    }

    private void updateFormTaxDiscountBalanceUI() {
        lblTopPanelBalanceAmount.setText("PHP" + String.format("%.2f", balance));
        lblBottomPanelTaxAmount.setText("PHP" + String.format("%.2f", tax));
        lblBottomPanelDiscountAmount.setText("PHP" + String.format("%.2f", discount));
        lblBottomPanelSubtotalAmount.setText("PHP" + String.format("%.2f", subtotal));
        lblBottomPanelBalanceAmount.setText("PHP" + String.format("%.2f", balance));
    }

    private boolean areFieldsValid() {
        if (cmbCustomer.getSelectedIndex() == -1) {
            JOptionPane.showMessageDialog(this, "Please select a customer", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (formTable.getRowCount() == 0 || formTable.getValueAt(0, 1).toString().equals("")) {
            JOptionPane.showMessageDialog(this, "Invoice cannot be Empty", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (calDateStart.getDate().isAfter(calDateEnd.getDate())) {
            JOptionPane.showMessageDialog(this, "Invoice Date cannot be after Due Date", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
        try {
            Double.parseDouble(txtDiscount.getText());
            Double.parseDouble(txtTax.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Discount and Tax must be numerical", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    private ArrayList<String[]> getListOfTableModel() {
        ArrayList<String[]> list = new ArrayList<String[]>();
        for (int i = 0; i < formTable.getRowCount(); i++) {
            String[] row = new String[formTable.getColumnCount()];
            for (int j = 0; j < formTable.getColumnCount(); j++) {
                try {
                    row[j] = formTable.getValueAt(i, j).toString();
                } catch (ClassCastException e) {
                    row[j] = (String) formTable.getValueAt(i, j);
                } catch (NullPointerException e) {
                    row[j] = (String) formTable.getValueAt(i, j);
                }
            }
            list.add(row);
        }
        return list;
    }

    private Object[][] getFormTableRowModelFrom(String str) {
        String[] rows = str.split(";");
        Object[][] data = new Object[rows.length][];
        for (int i = 0; i < rows.length; i++) {
            String[] row = rows[i].split(",");
            data[i] = row;
        }
        return data;
    }

    private Object[][] getTableRowModelFrom(Object[][] data) {
        Object[][] tableData = new Object[data.length][];
        for (int i = 0; i < data.length; i++) {
            Object[] row = new Object[7];
            // invoice date, invoice number, customer, amount, status, manage invoice
            row[0] = data[i][2];
            row[1] = data[i][0];
            for (Object[] customer : customers.getCustomers()) {
                if (customer[0].toString().equals(data[i][1].toString())) {
                    row[2] = customer[1] + " " + customer[2];
                }
            }
            row[3] = String.format("%.2f", Double.parseDouble(data[i][10].toString()));
            row[4] = data[i][11].toString();
            row[5] = "Manage Invoice";
            tableData[i] = row;
        } 
        return tableData;
    }

    @Override
    public void mouseEntered(MouseEvent arg0) {
    }

    @Override
    public void mouseExited(MouseEvent arg0) {
    }

    @Override
    public void mousePressed(MouseEvent arg0) {
    }

    @Override
    public void mouseReleased(MouseEvent arg0) {
    }

}
