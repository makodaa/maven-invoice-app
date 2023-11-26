package happeekidz.Views.App;

import java.awt.*;
import javax.swing.*;
import java.util.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
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

import com.formdev.flatlaf.FlatClientProperties;

public class InvoicesView extends JPanel implements ActionListener, MouseListener, TableModelListener {
    private JTable table, formTable;
    private JTextField txtNotes, txtAmount, txtEmail, txtDiscount, txtTax;
    private JComboBox<String> cmbCustomer, cmbProduct, cmbDiscount;
    private DatePicker calDateStart, calDateEnd;
    private JButton cmdShowAddStack, cmdCancel, cmdConfirmAdd, cmdBack, cmdExitStack, cmdUpdate, cmdDelete, cmdAddRow, cmdClearRow;
    private JLabel lblTopPanelBalanceAmount,
            lblBottomPanelBalanceAmount, lblBottomPanelSubtotalAmount, lblBottomPanelDiscountAmount,
            lblBottomPanelTaxAmount;
    int row = 0;
    double balance = 0, subtotal = 0, discount = 0, tax = 0;
    private Customers customers = new Customers();
    private Products products = new Products();
    private Invoices invoices = new Invoices();

    @Override
    public void mouseClicked(MouseEvent e) {
        JTable table = (JTable) e.getSource();
        int column = table.getColumnModel().getColumnIndexAtX(e.getX());
        row = e.getY() / table.getRowHeight();
        row = table.rowAtPoint(e.getPoint());

        System.out.println("Fucking Debugging Moments, row: " + row + " column: " + column);

        if (row == 7) {

        }
    }

    @Override
    public void tableChanged(TableModelEvent e) {
        int row = e.getFirstRow();
        int column = e.getColumn();
        if (column == 3 || column == 4) {
            try {
                System.out.println("Debugging Moments, column 3: " + formTable.getValueAt(row, 3));
                System.out.println("Debugging Moments, column 4: " + formTable.getValueAt(row, 4));
                int quantity = Integer.parseInt(formTable.getValueAt(row, 3).toString());
                double rate = Double.parseDouble(formTable.getValueAt(row, 4).toString());

                double amount = quantity * rate;
                formTable.setValueAt(String.format("%.2f", amount), row, 5);

                System.out.println("Debugging Moments, subtotal: " + subtotal);
                subtotal = 0;
                for (int i = 0; i < formTable.getRowCount(); i++) {
                    subtotal += Double.parseDouble(formTable.getValueAt(i, 5).toString());
                }

                computeFormTaxDiscountBalance();
                updateFormTaxDiscountBalanceUI();
                updateComponents(formTable);
            } catch (NumberFormatException ex) {
                formTable.setValueAt(formTable.getValueAt(row, 4), row, 5);
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        if (e.getSource() == cmdShowAddStack) {
            showLayeredPanel(addInvoicePanel());
        }
        if (e.getSource() == cmdBack) {
            JPanel panel = (JPanel) cmdBack.getParent().getParent();
            panel.removeAll();
            panel.add(new DashboardView(), "grow");
            updateFrame(frame);
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
            DefaultTableModel model = (DefaultTableModel) formTable.getModel();
            model.addRow(new Object[] { "", "", "", "", "", "", "" });
            updateComponents(formTable);
        }
        if (e.getSource() == cmdClearRow) {
            DefaultTableModel model = (DefaultTableModel) formTable.getModel();
            model.setRowCount(0);
            updateComponents(formTable);
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
            computeFormTaxDiscountBalance();
            updateFormTaxDiscountBalanceUI();
        }
        if (e.getSource() == cmdConfirmAdd) {
            if (areFieldsValid()) {
                ArrayList<String[]> products = getListOfTableModel();
                // customer id from customer database, invoice date, due date, products, message
                invoices = new Invoices(
                    Integer.parseInt(customers.getCustomers()[cmbCustomer.getSelectedIndex()][0].toString()),
                    calDateStart.getDate(),
                    calDateEnd.getDate(),
                    products,
                    txtNotes.getText());
                invoices.addInvoice();
                JPanel glassPane = (JPanel) frame.getGlassPane();
                glassPane.setVisible(false);
                glassPane.removeAll();
            }
        }
    }

    public InvoicesView() {
        init();
    }

    public void init() {
        setBackground(new Color(255, 255, 0));
        setLayout(new MigLayout("wrap 1, fill, gapx 0, insets 0", "", ""));
        add(addWindowHeader(), "growx, left");
        add(addControlPanel(), "growx, growy");
    }

    private <T extends JComponent> void updateComponents(T component) {
        component.revalidate();
        component.repaint();
    }

    private void updateFrame(JFrame frame) {
        frame.revalidate();
        frame.repaint();
    }

    private JComponent addWindowHeader() {
        JPanel panel = new JPanel(new MigLayout("insets 0, gapx 0", "", ""));
        cmdBack = new JButton("Invoices & Sales");
        cmdBack.setBorder(new EmptyBorder(9, 10, 9, 10));
        cmdBack.putClientProperty(FlatClientProperties.STYLE, "" +
                "font:bold +2;");
        cmdBack.setIcon(new ImageIcon(new ImageIcon(getClass().getResource("/happeekidz/assets/icons/back.png"))
                .getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH)));
        cmdBack.setBorder(new EmptyBorder(9, 10, 9, 10));
        cmdBack.addActionListener(this);
        panel.add(cmdBack, "growx");
        return panel;
    }

    private JComponent addControlPanel() {
        JPanel panel = new JPanel(new MigLayout("fill, insets 9 10 9 10", "", ""));

        JTextField txtSearch = new JTextField();
        txtSearch.putClientProperty(FlatClientProperties.STYLE, "" +
                "arc: 5;" +
                "font: +2;");
        txtSearch.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Search for an Invoice");

        JButton cmdFilter = new JButton("");
        cmdFilter.setBorder(new EmptyBorder(0, 0, 0, 0));
        cmdFilter.setIcon(new ImageIcon(new ImageIcon(getClass().getResource("/happeekidz/assets/icons/filter.png"))
                .getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH)));

        cmdShowAddStack = newFormButton("Add an Invoice",
                "arc: 5;" + "font: +2;" + "background: #16a34a;" + "foreground: #ffffff;");
        panel.add(txtSearch, "growx");
        panel.add(cmdFilter, "shrinkx, gapx 10");
        panel.add(cmdShowAddStack, "right");
        return panel;
    }

    class ButtonRenderer extends JButton implements TableCellRenderer {
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                int row, int column) {
            setText("Manage Invoice");
            putClientProperty(FlatClientProperties.STYLE, "" +
                    "foreground: #16a34a;");

            setBorder(BorderFactory.createEmptyBorder());

            return this;
        }
    }

    private void showLayeredPanel(JComponent component) {
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

    private JComponent addInvoicePanel() {
        JPanel panel = new JPanel(new MigLayout("wrap 1, fillx, insets 0", "", ""));
        panel.setBackground(new Color(240, 240, 240));
        panel.add(addFloatingPanelHeader("Add an Invoice"), "growx");
        panel.add(addInputFieldsPanel(new Object[] { "", "", "", "", "", "", "", LocalDate.now(), LocalDate.now() }),
                "growx");
        panel.add(addNewInvoiceControlButtonsPanel(), "right");
        updateComponents(panel);
        return panel;
    }

    private JComponent manageInvoicePanel(Object[] data) {
        JPanel panel = new JPanel(new MigLayout("wrap 1, fillx, insets 0", "", ""));
        panel.setBackground(new Color(240, 240, 240));
        panel.add(addFloatingPanelHeader("Manage Invoice"), "growx");
        panel.add(addInputFieldsPanel(data), "growx");
        panel.add(addUpdateInvoiceControlButtonsPanel(), "right");
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

    private JComponent addInputFieldsPanel(Object[] data) {
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
        JComponent formTable = newFormTablePanel();

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

    private JComponent addNewInvoiceControlButtonsPanel() {
        JPanel panel = new JPanel(new MigLayout("insets 9 10 9 10", "[][]", ""));
        cmdCancel = newFormButton("Cancel", "arc: 5;" + "font: +2;" + "background: #475569;" + "foreground: #ffffff;");
        cmdConfirmAdd = newFormButton("Confirm",
                "arc: 5;" + "font: +2;" + "background: #16a34a;" + "foreground: #ffffff;");
        panel.add(cmdCancel, "right, gapx 0");
        panel.add(cmdConfirmAdd, "right, gapx 0");

        return panel;
    }

    private JComponent addUpdateInvoiceControlButtonsPanel() {
        JPanel panel = new JPanel(new MigLayout("insets 9 10 9 10", "[][]", ""));
        cmdDelete = newFormButton("Delete", "arc: 5;" + "font: +2;" + "background: #475569;" + "foreground: #ffffff;");
        cmdCancel = newFormButton("Cancel", "arc: 5;" + "font: +2;" + "background: #475569;" + "foreground: #ffffff;");
        cmdUpdate = newFormButton("Update", "arc: 5;" + "font: +2;" + "background: #16a34a;" + "foreground: #ffffff;");
        panel.add(cmdDelete, "right, gapx 0");
        panel.add(cmdCancel, "right, gapx 0");
        panel.add(cmdUpdate, "right, gapx 0");
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

    private JComponent newFormTablePanel() {
        JPanel panel = new JPanel(new MigLayout("fillx, insets 0", "", ""));
        Object[][] data = { { "", "", "", "", "", "", "" }, };
        String[] columnNames = { "", "PRODUCT/SERVICE", "DESCRIPTION", "QTY", "RATE", "AMOUNT", "" };

        String[] product_names = new String[products.getProducts().length];
        for (int i = 0; i < product_names.length; i++) {
            product_names[i] = (String) products.getProducts()[i][1];
        }
        DefaultTableModel model = new DefaultTableModel(data, columnNames);
        formTable = new JTable(model);
        formTable.setRowHeight(40);
        formTable.setShowVerticalLines(true);
        formTable.setShowHorizontalLines(true);
        formTable.setGridColor(Color.decode("#cccccc"));
        formTable.addMouseListener(this);
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
    private ArrayList<String[]> getListOfTableModel(){
        ArrayList<String[]> list = new ArrayList<String[]>();
        for(int i = 0; i < formTable.getRowCount(); i++){
            String[] row = new String[formTable.getColumnCount()];
            for(int j = 0; j < formTable.getColumnCount(); j++){
                row[j] = formTable.getValueAt(i, j).toString();
            }
            list.add(row);
        }
        return list;
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
