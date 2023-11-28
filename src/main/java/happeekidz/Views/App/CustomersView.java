package happeekidz.Views.App;

import java.awt.*;
import javax.swing.*;
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
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;

import happeekidz.Models.App.Customers;
import happeekidz.Models.App.Invoices;

import com.formdev.flatlaf.FlatClientProperties;

public class CustomersView extends JPanel implements ActionListener, MouseListener {
    private JTable table;
    private JTextField txtFirstName, txtLastName, txtMiddleName, txtAddress, txtEmail, txtContactNumber;
    private DatePicker calDateStart, calDateEnd;
    private JButton cmdAdd, cmdCancel, cmdConfirm, cmdBack, cmdExitStack, cmdUpdate, cmdDelete;
    private Customers customers = new Customers();
    int row = 0;

    @Override
    public void mouseClicked(MouseEvent e) {
        int column = table.getColumnModel().getColumnIndexAtX(e.getX());
        row = e.getY() / table.getRowHeight();

        if (column == 3) {
            showLayeredPanel(manageCustomerPanel(customers.getCustomers()[row]));
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        if (e.getSource() == cmdAdd) {
            showLayeredPanel(addCustomerPanel());
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
            if (isCustomerAdded()) {
                JOptionPane.showMessageDialog(frame, "Customer has been added", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                JPanel glassPane = (JPanel) frame.getGlassPane();
                glassPane.setVisible(false);
                glassPane.removeAll();
                updateFrame(frame);
                updateTable();
            }
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
        if (e.getSource() == cmdUpdate) {
            if (isCustomerUpdated(customers.getCustomers()[row], row)) {
                JOptionPane.showMessageDialog(frame, "Customer has been updated", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                JPanel glassPane = (JPanel) frame.getGlassPane();
                glassPane.setVisible(false);
                glassPane.removeAll();
                updateFrame(frame);
                updateTable();
            }
        }
        if (e.getSource() == cmdDelete) {
            int rs = JOptionPane.showConfirmDialog(frame, "Are you sure you want to delete this customer?", "Delete",
                    JOptionPane.YES_NO_OPTION);
            if (rs == JOptionPane.YES_OPTION) {
                if (isCustomerRemoved(Integer.parseInt(customers.getCustomers()[row][0].toString()))) {
                    JOptionPane.showMessageDialog(frame, "Customer has been deleted", "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                    JPanel glassPane = (JPanel) frame.getGlassPane();
                    glassPane.setVisible(false);
                    glassPane.removeAll();
                    updateFrame(frame);
                    updateTable();
                }
            }
        }
    }

    public CustomersView() {
        init();
    }

    public void init() {
        setLayout(new MigLayout("wrap 1, fill, gapx 0, insets 9 10 9 10", "", ""));
        add(addWindowHeader(), "growx, left");
        add(addGraphicsPanel(), "growx, growy");
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
        Object[][] rowdata = customers.getCustomers() == null || customers.getCustomers().length == 0
                ? new String[1][4]
                : getTableDataOf(customers.getCustomers());
        String[] columnNames = { "Customer Name", "Open Balance", "Date Started", "Action" };
        table.setModel(new DefaultTableModel(rowdata, columnNames));
        if (customers.getCustomers().length > 0) {
            table.getColumnModel().getColumn(3).setCellRenderer(new ButtonRenderer());
        }
        table.addMouseListener(this);
    }

    private JComponent addWindowHeader() {
        JPanel panel = new JPanel(new MigLayout("insets 0, gapx 0", "", ""));
        JLabel lblPanelName = new JLabel("Customers");
        lblPanelName.setBorder(new EmptyBorder(9, 10, 9, 10));
        lblPanelName.putClientProperty(FlatClientProperties.STYLE, "" +
                "font:bold +2;");
        panel.add(lblPanelName, "growx");
        return panel;
    }

    private JComponent addControlPanel() {
        JPanel panel = new JPanel(new MigLayout("fill, insets 0", "", ""));
        cmdAdd = newFormButton("Add a Customer",
                "arc: 5;" + "font: +2;" + "background: #16a34a;" + "foreground: #ffffff;");
        panel.add(cmdAdd, "right");
        return panel;
    }

    private JComponent addGraphicsPanel() {
        JPanel GraphicsPanel = new JPanel(new MigLayout("wrap 4, fillx, insets 0 , gapx 4", "", ""));
        int ending_contracts = 0;
        int terminated_contracts = 0;
        for (int i = 0; i < customers.getCustomers().length; i++) {
            // if contract is ending in 30 days or less
            if (LocalDate.now().plusDays(30).isAfter(LocalDate.parse(customers.getCustomers()[i][8].toString()))
                    && LocalDate.now().isBefore(LocalDate.parse(customers.getCustomers()[i][8].toString()))) {
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
        GraphicsPanel.add(
                newInfographicBox("Total Customers", "", customers.getCustomers().length + "", "customers.png"),
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

    private JComponent addTablePanel() {
        Object[][] rowdata = customers.getCustomers() == null || customers.getCustomers().length == 0
                ? new String[1][4]
                : getTableDataOf(customers.getCustomers());
        String[] columnNames = { "Customer Name", "Sum Payment", "Date Started", "Action" };

        JPanel panel = new JPanel(new MigLayout("fill, insets 0", "[grow]", "[grow]"));
        JScrollPane scrollPane = new JScrollPane();
        panel.add(scrollPane, "growx, growy");

        table = new JTable();
        table.setRowHeight(40);
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setResizingAllowed(false);
        table.setDefaultEditor(Object.class, null);
        table.setModel(new DefaultTableModel(rowdata, columnNames));
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        table.setDefaultRenderer(String.class, centerRenderer);
        table.setDefaultRenderer(Object.class, centerRenderer);

        if (customers.getCustomers().length > 0) {
            table.getColumnModel().getColumn(3).setCellRenderer(new ButtonRenderer());
        }

        table.addMouseListener(this);

        scrollPane.setViewportView(table);
        return panel;
    }

    class ButtonRenderer extends JButton implements TableCellRenderer {
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                int row, int column) {
            setText("Manage Customer");
            putClientProperty(FlatClientProperties.STYLE, "" +
                    "foreground: #16a34a;");

            setBorder(BorderFactory.createEmptyBorder());

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

    public JComponent addCustomerPanel() {
        JPanel panel = new JPanel(new MigLayout("wrap 1, fillx, insets 0", "", ""));
        panel.setBackground(new Color(240, 240, 240));
        panel.add(addFloatingPanelHeader("Add a Customer"), "growx");
        panel.add(addInputFieldsPanel(new Object[] { "", "", "", "", "", "", "", LocalDate.now(), LocalDate.now() }),
                "growx");
        panel.add(addNewCustomerControlButtonsPanel(), "right");
        updateComponents(panel);
        return panel;
    }

    private JComponent manageCustomerPanel(Object[] data) {
        JPanel panel = new JPanel(new MigLayout("wrap 1, fillx, insets 0", "", ""));
        panel.setBackground(new Color(240, 240, 240));
        panel.add(addFloatingPanelHeader("Manage Customer"), "growx");
        panel.add(addInputFieldsPanel(data), "growx");
        panel.add(addUpdateCustomerControlButtonsPanel(), "right");
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

        JPanel panel = new JPanel(new MigLayout("wrap 2, fill, insets 18 20 18 20", "", ""));

        txtFirstName = newFormTextField("Add First Name", data[1].toString());
        JLabel lblFirstName = newFormLabel("First Name");

        txtLastName = newFormTextField("Add Last Name", data[2].toString());
        JLabel lblLastName = newFormLabel("Last Name");

        txtMiddleName = newFormTextField("Add Middle Name", data[3].toString());
        JLabel lblMiddleName = newFormLabel("Middle Name");

        txtAddress = newFormTextField("Add Address", data[4].toString());
        JLabel lblAddress = newFormLabel("Address");

        txtEmail = newFormTextField("Add Email", data[5].toString());
        JLabel lblEmail = newFormLabel("Email");

        txtContactNumber = newFormTextField("Add Contact Number", data[6].toString());
        JLabel lblContactNumber = newFormLabel("Contact Number");

        calDateStart = newFormDatePicker("Contract Start", ((java.sql.Date.valueOf(data[7].toString()))).toLocalDate());

        JLabel lblDateStart = newFormLabel("Date Started");

        calDateEnd = newFormDatePicker("Contract End", ((java.sql.Date.valueOf(data[8].toString()))).toLocalDate());
        JLabel lblDateEnd = newFormLabel("Due Date");

        panel.add(lblFirstName, "growx");
        panel.add(lblLastName, "growx");
        panel.add(txtFirstName, "growx");
        panel.add(txtLastName, "growx");

        panel.add(lblMiddleName, "growx");
        panel.add(lblAddress, "growx");

        panel.add(txtMiddleName, "growx");
        panel.add(txtAddress, "growx");

        panel.add(lblEmail, "growx");
        panel.add(lblContactNumber, "growx");

        panel.add(txtEmail, "growx");
        panel.add(txtContactNumber, "growx");

        panel.add(lblDateStart, "growx");
        panel.add(lblDateEnd, "growx");

        panel.add(calDateStart, "growx");
        panel.add(calDateEnd, "growx");
        panel.revalidate();
        panel.repaint();
        return panel;
    }

    private JComponent addNewCustomerControlButtonsPanel() {
        JPanel panel = new JPanel(new MigLayout("insets 9 10 9 10", "[][]", ""));
        cmdCancel = newFormButton("Cancel", "arc: 5;" + "font: +2;" + "background: #475569;" + "foreground: #ffffff;");
        cmdConfirm = newFormButton("Confirm",
                "arc: 5;" + "font: +2;" + "background: #16a34a;" + "foreground: #ffffff;");
        panel.add(cmdCancel, "right, gapx 0");
        panel.add(cmdConfirm, "right, gapx 0");

        return panel;
    }

    private JComponent addUpdateCustomerControlButtonsPanel() {
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
        // change height of component date text field
        datePicker.getComponentToggleCalendarButton().setPreferredSize(new Dimension(0, 35));
        // change color of border
        datePicker.getComponentDateTextField().setBorder(BorderFactory.createLineBorder(Color.decode("#cccccc")));
        // change horizontal margin of date text field
        datePicker.putClientProperty(FlatClientProperties.STYLE, "" +
                "arc: 5;" +
                "font: +2;");
        datePicker.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, placeholder);
        datePicker.setDate(setDate);
        return datePicker;
    }

    private Object[][] getTableDataOf(Object[][] data) {
        Object[][] tableData = new Object[data.length][4];
        for (int i = 0; i < data.length; i++) {
            tableData[i][0] = data[i][1].toString() + " " + data[i][2].toString();
            // cycle through all invoices and get the sum of all payments for each customer
            Invoices invoices = new Invoices();
            Object[][] invoicesData = invoices.getInvoices();
            double sum = 0;
            for (int j = 0; j < invoicesData.length; j++) {
                if (invoicesData[j][1].toString().equals(data[i][0].toString())
                        && invoicesData[j][11].toString().equals("Unpaid")) {
                    if (invoicesData[j][9].toString().equals("Discount Percent")) {
                                //sum = subtotal - (subtotal * discount)
                        sum += Double.parseDouble(invoicesData[j][6].toString()) - (Double.parseDouble(invoicesData[j][6].toString()) * Double.parseDouble(invoicesData[j][8].toString()));
                    } else {
                    }

                }
            }
            tableData[i][1] = sum;
            tableData[i][2] = data[i][7];
        }
        return tableData;
    }

    // method to check if all fields are filled
    private boolean areFieldsValid() {
        if (txtFirstName.getText().isEmpty() || txtLastName.getText().isEmpty() || txtMiddleName.getText().isEmpty()
                || txtAddress.getText().isEmpty() || txtEmail.getText().isEmpty()
                || txtContactNumber.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill out all fields", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (calDateStart.getDate().isAfter(calDateEnd.getDate())) {
            JOptionPane.showMessageDialog(this, "Contract start date cannot be after contract end date", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    private boolean isEntryUnique() {
        Object[][] data = customers.getCustomers();
        for (int i = 0; i < data.length; i++) {
            if (data[i][1].toString().equals(txtFirstName.getText())
                    && data[i][2].toString().equals(txtLastName.getText())
                    && data[i][3].toString().equals(txtMiddleName.getText())
                    && data[i][4].toString().equals(txtAddress.getText())
                    && data[i][5].toString().equals(txtEmail.getText())
                    && data[i][6].toString().equals(txtContactNumber.getText())) {
                JOptionPane.showMessageDialog(this, txtFirstName.getText() + " " + txtLastName + " already exists",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }
        return true;
    }

    private boolean isEntryUnique(int id) {
        Object[][] data = customers.getCustomers();
        for (int i = 0; i < data.length; i++) {
            if (data[i][0].toString().equals(Integer.toString(id))) {
                continue;
            }
            if (data[i][1].toString().equals(txtFirstName.getText())
                    && data[i][2].toString().equals(txtLastName.getText())
                    && data[i][3].toString().equals(txtMiddleName.getText())
                    && data[i][4].toString().equals(txtAddress.getText())
                    && data[i][5].toString().equals(txtEmail.getText())
                    && data[i][6].toString().equals(txtContactNumber.getText())) {
                JOptionPane.showMessageDialog(this, txtFirstName.getText() + " " + txtLastName + " already exists",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }
        return true;
    }

    private boolean isCustomerAdded() {
        if (areFieldsValid() && isEntryUnique()) {
            customers.setCustomerToAdd(txtFirstName.getText(), txtLastName.getText(), txtMiddleName.getText(),
                    txtAddress.getText(), txtEmail.getText(), txtContactNumber.getText(), calDateStart.getDate(),
                    calDateEnd.getDate());
            return true;
        }
        return false;
    }

    private boolean isCustomerRemoved(int id) {
        if (id != -1) {
            customers.setCustomerToRemove(id);
            return true;
        }
        return false;
    }

    private boolean isCustomerUpdated(Object[] data, int index) {
        if (areFieldsValid() && isEntryUnique(index)) {
            customers.setCustomerToUpdate(Integer.parseInt(data[0].toString()), txtFirstName.getText(),
                    txtLastName.getText(), txtMiddleName.getText(), txtAddress.getText(), txtEmail.getText(),
                    txtContactNumber.getText(), calDateStart.getDate(), calDateEnd.getDate());
            return true;
        }
        return false;
    }

}
