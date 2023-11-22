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
import java.sql.Date;
import java.time.LocalDate;

import net.miginfocom.swing.MigLayout;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;

import com.formdev.flatlaf.FlatClientProperties;

public class CustomersView extends JPanel implements ActionListener, MouseListener {
    private JTable table;
    private JTextField txtFirstName, txtLastName, txtMiddleName, txtAddress,  txtEmail, txtContactNumber;
    private DatePicker calDateStart, calDateEnd;
    private JButton cmdAdd, cmdCancel, cmdConfirm, cmdBack, cmdExitStack;
    private JComponent floatingPanel;

    
    @Override
    public void mouseClicked(MouseEvent e) {
        int column = table.getColumnModel().getColumnIndexAtX(e.getX());
        int row    = e.getY()/table.getRowHeight();

        System.out.println("Fucking Debugging Moments, row: " + row + " column: " + column);

            if (column == 4) {
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
        if (e.getSource() == cmdExitStack) {
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
        add(addWindowHeader(), "growx, left");
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

    private JComponent addWindowHeader() {
        JPanel panel = new JPanel(new MigLayout("insets 0, gapx 0", "", ""));
        cmdBack = new JButton("Customers");
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
        txtSearch.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Search for a customer");

        JButton cmdFilter = new JButton("");
        cmdFilter.setBorder(new EmptyBorder(0, 0, 0, 0));
        cmdFilter.setIcon(new ImageIcon(new ImageIcon(getClass().getResource("/happeekidz/assets/icons/filter.png"))
                .getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH)));

        cmdAdd = newFormButton("Add a Customer","arc: 5;" + "font: +2;" + "background: #16a34a;" + "foreground: #ffffff;");
        panel.add(txtSearch, "growx");
        panel.add(cmdFilter, "shrinkx, gapx 10");
        panel.add(cmdAdd, "right");
        return panel;
    }

    private JComponent addTablePanel() {
        Object[][] rowdata = {{"Johnny Bravo","42069","11/20/23",""}};
        String[] columnNames = {"Customer Name", "Sum Payment", "Date Started", "Action"};
        
        JPanel panel = new JPanel(new MigLayout("fill, insets 9 10 9 10", "[grow]", "[grow]"));
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

        table.getColumnModel().getColumn(3).setCellRenderer(new ButtonRenderer());
        table.addMouseListener(this);
        
        scrollPane.setViewportView(table);
        return panel;
    }
        class ButtonRenderer extends JButton implements TableCellRenderer {
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setText("Manage Customer");
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
    
    private JComponent addCustomerPanel() {
        JPanel panel = new JPanel(new MigLayout("wrap 1, fillx, insets 0", "", ""));
        panel.setBackground(new Color(240, 240, 240));
        panel.add(addFloatingPanelHeader("Add a Customer"), "growx");
        panel.add(addInputFieldsPanel(new Object[] {"","","","","","",LocalDate.now(),LocalDate.now()}), "growx");
        panel.add(addControlButtonsPanel(), "right");
        updateComponents(panel);
        return panel;
    }

    private JComponent manageCustomerPanel(Object[] data){
        JPanel panel = new JPanel(new MigLayout("wrap 1, fillx, insets 0", "", ""));
        panel.setBackground(new Color(240, 240, 240));
        panel.add(addFloatingPanelHeader("Manage Customer"), "growx");
        panel.add(addInputFieldsPanel(data), "growx");
        panel.add(addControlButtonsPanel(), "right");
        updateComponents(panel);
        return panel;
    }

    private JComponent addFloatingPanelHeader(String title) {
        JPanel panel = new JPanel(new MigLayout("insets 0", "", ""));
        cmdExitStack = newFormButton(title,"arc: 5;" + "font: +2;" + "background: #16a34a;" + "foreground: #ffffff;");
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
        /* 
         * 0 - firstName, 1 - lastName, 2 - middleName, 3 - address, 4 - email, 5 - phone, 6 - contractStartDate, 7 - contractEndDate
        */

        JPanel panel = new JPanel(new MigLayout("wrap 2, fill, insets 18 20 18 20", "", ""));

        txtFirstName = newFormTextField("Add First Name", data[0].toString());
        JLabel lblFirstName = newFormLabel("First Name");

        txtLastName = newFormTextField("Add Last Name", data[1].toString());
        JLabel lblLastName = newFormLabel("Last Name");

        txtMiddleName = newFormTextField("Add Middle Name", data[2].toString());
        JLabel lblMiddleName = newFormLabel("Middle Name");

        txtAddress = newFormTextField("Add Address", data[3].toString());
        JLabel lblAddress = newFormLabel("Address");

        txtEmail = newFormTextField("Add Email", data[4].toString());
        JLabel lblEmail = newFormLabel("Email");

        txtContactNumber = newFormTextField("Add Contact Number", data[5].toString());
        JLabel lblContactNumber = newFormLabel("Contact Number");
        //get date from data[6] and data[7
        calDateStart = newFormDatePicker("Contract Start", (LocalDate) data[6]);

        JLabel lblDateStart = newFormLabel("Date Started");

        calDateEnd = newFormDatePicker("Contract End", (LocalDate) data[7]);
        JLabel lblDateEnd = newFormLabel("Date Ended");

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

    private JComponent addControlButtonsPanel() {
        JPanel panel = new JPanel(new MigLayout("insets 9 10 9 10", "[][]", ""));
        cmdCancel = newFormButton("Cancel","arc: 5;" + "font: +2;" + "background: #16a34a;" + "foreground: #ffffff;");
        cmdConfirm = newFormButton("Confirm","arc: 5;" + "font: +2;" + "background: #16a34a;" + "foreground: #ffffff;");
        panel.add(cmdCancel, "right, gapx 0");
        panel.add(cmdConfirm, "right, gapx 0");

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
        //change height of component date text field 
        datePicker.getComponentToggleCalendarButton().setPreferredSize(new Dimension(0, 35));
        //change color of border 
        datePicker.getComponentDateTextField().setBorder(BorderFactory.createLineBorder(Color.decode("#cccccc")));
        //change horizontal margin of date text field
        datePicker.putClientProperty(FlatClientProperties.STYLE, "" +   
                "arc: 5;" +
                "font: +2;");
        datePicker.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, placeholder);
        datePicker.setDate(setDate);
        return datePicker;
    }
}

