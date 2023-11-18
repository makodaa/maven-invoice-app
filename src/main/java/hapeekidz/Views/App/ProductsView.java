package hapeekidz.Views.App;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.json.ParseException;

import hapeekidz.Models.App.Products;
import net.miginfocom.swing.MigLayout;

/*
 * TODO:
 * ## Product Overview Panel
 * - Remove Editable State of Table
 * - Fix Table Cell Contents
 * 
 * ## Add Product Panel
 * 
 * 
 * ## Manage Product Panel
 * - Fecth Field Content from Database
 * - Update Database on Confirmation
*/
public class ProductsView extends JPanel implements ActionListener {
    private JTextField txtName, txtCategory, txtPrice, txtSKU, txtDescription;
    private JCheckBox chkTaxable;
    private JButton cmdAdd, cmdCancel, cmdConfirm, cmdBack, cmdExitStack, cmdModify, cmdDelete, cmdDummyButton;
    private JTable table;
    private Products model = new Products();
    private JComponent productPanel;
    private Object[][] newData;

    @Override
    public void actionPerformed(ActionEvent e) {
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        if (e.getSource() == cmdAdd) {
            showProductPanel(addProductPanel());
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
            boolean rs = sendProductsToDatabase();
            if (rs) {
                JPanel glassPane = (JPanel) frame.getGlassPane();
                glassPane.setVisible(false);
                glassPane.removeAll();
            }
            updateTable();
            updateFrame(frame);
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
        }
        if (e.getSource() == cmdDummyButton) {
            if(model.fetchProductsFromDatabase().length == 0) {
                JOptionPane.showMessageDialog(null, "No products to manage");
                return;
            }
            showProductPanel(manageProductPanel(model.fetchProductsFromDatabase()[0]));
        }
        if (e.getSource() == cmdModify) {
            int rs = JOptionPane.showConfirmDialog(frame, "Are you sure you want to modify?", "Modify",
                    JOptionPane.YES_NO_OPTION);
            if (rs == JOptionPane.YES_OPTION) {
                if (sendUpdateProductToDatabase(model.fetchProductsFromDatabase()[0], 0)) {
                    EventQueue.invokeLater(() -> {
                        JPanel glassPane = (JPanel) frame.getGlassPane();
                        glassPane.setVisible(false);
                        glassPane.removeAll();
                    });
                }
                updateTable();
                updateFrame(frame);
            }
        }
        if (e.getSource() == cmdDelete) {
            int rs = JOptionPane.showConfirmDialog(frame, "Are you sure you want to delete?", "Delete",
                    JOptionPane.YES_NO_OPTION);
            if (rs == JOptionPane.YES_OPTION) {
                sendRemoveProudctToDatabase(model.fetchProductsFromDatabase()[0]);
                EventQueue.invokeLater(() -> {
                    JPanel glassPane = (JPanel) frame.getGlassPane();
                    glassPane.setVisible(false);
                    glassPane.removeAll();
                });
                updateTable();
                if (model.fetchProductsFromDatabase().length > 1) {
                    updateFrame(frame);
                } else {
                }
            }
        }
    }

    public ProductsView() {
        init();
    }

    private void init() {
        setLayout(new MigLayout("wrap 1, fill, gapx 0, insets 0", "", ""));
        setBackground(Color.red);
        add(WindowHeader(), "growx");
        add(ControlPanel(), "growx, growy");
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
        Object rowData[][] = adjustData(model.fetchProductsFromDatabase()) == null ? new Object[][] { { "", "", "", "", "" } }
                : adjustData(model.fetchProductsFromDatabase());
        Object columnNames[] = { "Products / Services", "Category", "Rate", "Taxable", "Action" };
        table.setModel(new DefaultTableModel(rowData, columnNames));
        table.getColumnModel().getColumn(4).setCellRenderer(new ButtonRenderer());
        table.getColumnModel().getColumn(3).setCellEditor(new ButtonEditor(new JCheckBox()));
        table.getColumnModel().getColumn(0).setPreferredWidth(107);
    }

    private JComponent WindowHeader() {
        JPanel panel = new JPanel(new MigLayout("insets 0, gapx 0", "", ""));
        cmdBack = new JButton("Products");
        cmdBack.putClientProperty(FlatClientProperties.STYLE, "" +
                "font:bold +3;");
        cmdBack.setIcon(new ImageIcon(new ImageIcon(getClass().getResource("/hapeekidz/assets/icons/back.png"))
                .getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH)));
        cmdBack.setBorder(new EmptyBorder(9, 10, 9, 10));
        cmdBack.addActionListener(this);
        panel.add(cmdBack, "growx");
        return panel;
    }

    private JComponent ControlPanel() {
        JPanel panel = new JPanel(new MigLayout("fill, insets 9 10 9 10", "", ""));
        JTextField txtSearch = new JTextField();
        txtSearch.putClientProperty(FlatClientProperties.STYLE, "" +
                "arc: 5;" +
                "font: +2;");
        txtSearch.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Search for a product");
        JButton cmdFilter = new JButton("");
        cmdFilter.setIcon(new ImageIcon(new ImageIcon(getClass().getResource("/hapeekidz/assets/icons/filter.png"))
                .getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH)));
        cmdFilter.setBorder(new EmptyBorder(0, 0, 0, 0));
        cmdAdd = new JButton("Add a Product");
        cmdAdd.setPreferredSize(new Dimension(160, 40));
        cmdAdd.addActionListener(this);
        cmdAdd.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_BORDERLESS);
        cmdAdd.putClientProperty(FlatClientProperties.STYLE, "" +
                "arc: 5;" +
                "font: +2;" +
                "background: #16a34a;" +
                "foreground: #ffffff;");

        /*
         * Dummy button iz here
         */
        cmdDummyButton = new JButton("Manage Product");
        cmdDummyButton.setPreferredSize(new Dimension(160, 40));
        cmdDummyButton.addActionListener(this);
        cmdDummyButton.putClientProperty(FlatClientProperties.BUTTON_TYPE,
                FlatClientProperties.BUTTON_TYPE_BORDERLESS);
        cmdDummyButton.putClientProperty(FlatClientProperties.STYLE, "" +
                "arc: 5;" +
                "font: +2;" +
                "background: #475569;" +
                "foreground: #ffffff;");

        panel.add(txtSearch, "growx");
        panel.add(cmdFilter, "shrinkx, gapx 10");
        panel.add(cmdAdd, "right");
        panel.add(cmdDummyButton, "right, gapx 0");
        return panel;
    }

    private JComponent addTablePanel() {
        Object rowData[][] = adjustData(model.fetchProductsFromDatabase()) == null ? new Object[][] { { "", "", "", "", "" } }
                : adjustData(model.fetchProductsFromDatabase());
        Object columnNames[] = { "Products / Services", "Category", "Rate", "Taxable", "Action" };

        JPanel panel = new JPanel(new MigLayout("fill, insets 9 10 9 10", "[grow]", "[grow]"));
        JScrollPane scrollPane = new JScrollPane();
        panel.add(scrollPane, "growx, growy");

        table = new JTable();
        table.setBackground(Color.white);
        table.setRowHeight(40);
        table.setPreferredSize(new Dimension(0, 0));
        table.setModel(new DefaultTableModel(rowData, columnNames));
        table.getColumnModel().getColumn(4).setCellRenderer(new ButtonRenderer());
        table.getColumnModel().getColumn(3).setCellEditor(new ButtonEditor(new JCheckBox()));

        table.getColumnModel().getColumn(0).setPreferredWidth(107);
        scrollPane.setViewportView(table);
        return panel;

    }

    @SuppressWarnings("serial")
    class ButtonRenderer extends JButton implements TableCellRenderer {
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                int row, int column) {
            if (value instanceof JButton) {
                setText(((JButton) value).getText());
            } else {
                setText((value == null) ? "" : value.toString());
            }
            return this;
        }
    }

    @SuppressWarnings("serial")
    class ButtonEditor extends DefaultCellEditor {
        protected JButton button;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent arg0) {
                    fireEditingStopped();
                }
            });
        }

        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row,
                int column) {
            button.setText((value == null) ? "" : value.toString());
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            return button.getText();
        }
    }

    private void showProductPanel(JComponent component) {
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
                    updateComponents(productPanel);
                });
            }
        });
    }

    private JComponent addProductPanel() {
        JPanel panel = new JPanel(new MigLayout("wrap 1, fillx, insets 0", "", ""));
        panel.add(addProductPanelHeader(), "growx");
        panel.add(addProductInputFieldsPanel(), "growx");
        panel.add(addProductControlButtons(), "right");
        updateComponents(panel);
        return panel;
    }

    private JComponent addProductPanelHeader() {
        JPanel panel = new JPanel(new MigLayout("insets 0", "", ""));
        cmdExitStack = new JButton("Add a Product");
        cmdExitStack.putClientProperty(FlatClientProperties.STYLE, "" +
                "font:bold +3;");
        cmdExitStack
                .setIcon(new ImageIcon(new ImageIcon(getClass().getResource("/hapeekidz/assets/icons/back.png"))
                        .getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH)));
        cmdExitStack.setBorder(new EmptyBorder(9, 10, 9, 10));
        cmdExitStack.addActionListener(this);
        panel.add(cmdExitStack, "growx");
        return panel;
    }

    private JComponent addProductInputFieldsPanel() {
        JPanel panel = new JPanel(new MigLayout("wrap 2, fill, insets 18 20 18 20", "", ""));

        txtName = new JTextField();
        JLabel lblName = new JLabel("Product Name");
        txtName.putClientProperty(FlatClientProperties.STYLE, "" +
                "arc: 5;" +
                "font: +2;");
        txtName.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Product Name");
        lblName.putClientProperty(FlatClientProperties.STYLE, "" +
                "font: bold +3;");

        txtCategory = new JTextField();
        JLabel lblCategory = new JLabel("Category");
        txtCategory.putClientProperty(FlatClientProperties.STYLE, "" +
                "arc: 5;" +
                "font: +2;");
        txtCategory.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Category");
        lblCategory.putClientProperty(FlatClientProperties.STYLE, "" +
                "font: bold +3;");

        txtPrice = new JTextField();
        JLabel lblPrice = new JLabel("Rate");
        txtPrice.putClientProperty(FlatClientProperties.STYLE, "" +
                "arc: 5;" +
                "font: +2;");
        txtPrice.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Rate");
        lblPrice.putClientProperty(FlatClientProperties.STYLE, "" +
                "font: bold +3;");

        txtSKU = new JTextField();
        JLabel lblSKU = new JLabel("SKU");
        txtSKU.putClientProperty(FlatClientProperties.STYLE, "" +
                "arc: 5;" +
                "font: +2;");
        txtSKU.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "SKU");
        lblSKU.putClientProperty(FlatClientProperties.STYLE, "" +
                "font: bold +3;");

        chkTaxable = new JCheckBox("Taxable");
        JLabel lblTaxable = new JLabel("Taxable");
        lblTaxable.putClientProperty(FlatClientProperties.STYLE, "" +
                "font: bold +3;");

        txtDescription = new JTextField();
        JLabel lblDescription = new JLabel("Sales Description");
        txtDescription.setPreferredSize(new Dimension(0, 200));
        // make text description text be at top left

        txtDescription.putClientProperty(FlatClientProperties.STYLE, "" +
                "arc: 5;" +
                "font: +2;");
        txtDescription.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Description");
        lblDescription.putClientProperty(FlatClientProperties.STYLE, "" +
                "font: bold +3;");

        panel.add(lblName, "gapy 8");
        panel.add(lblCategory, "gapy 8");
        panel.add(txtName, "growx, gapy 16");
        panel.add(txtCategory, "growx, gapy 16");

        panel.add(lblPrice, "gapy 8");
        panel.add(lblSKU, "gapy 8");
        panel.add(txtPrice, "growx, gapy 16");
        panel.add(txtSKU, "growx, gapy 16");

        panel.add(chkTaxable, "spanx 2, growx, right, wrap");

        panel.add(lblDescription, "gapy 8, spanx 2, growx, wrap");
        panel.add(txtDescription, "spanx 2, growx");
        updateComponents(panel);
        return panel;
    }

    private JComponent addProductControlButtons() {
        JPanel panel = new JPanel(new MigLayout("insets 9 10 9 10", "[][]", ""));
        cmdConfirm = new JButton("Confirm");
        cmdConfirm.setPreferredSize(new Dimension(160, 40));
        cmdConfirm.addActionListener(this);
        cmdConfirm.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_BORDERLESS);
        cmdConfirm.putClientProperty(FlatClientProperties.STYLE, "" +
                "arc: 5;" +
                "font: +2;" +
                "background: #16a34a;" +
                "foreground: #ffffff;");

        cmdCancel = new JButton("Cancel");
        cmdCancel.setPreferredSize(new Dimension(160, 40));
        cmdCancel.addActionListener(this);
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

    private JComponent manageProductPanel(Object[] data) {
        JPanel panel = new JPanel(new MigLayout("wrap 1, fillx, insets 0", "", ""));
        panel.add(addManageProductPanelHeader(), "growx");
        panel.add(addManageProductInputFieldsPanel(data), "growx");
        panel.add(addManageProductControlButtons(), "right");
        updateComponents(panel);
        return panel;
    }

    private JComponent addManageProductPanelHeader() {
        JPanel panel = new JPanel(new MigLayout("insets 0", "", ""));
        cmdExitStack = new JButton("Manage Product");
        cmdExitStack.putClientProperty(FlatClientProperties.STYLE, "" +
                "font:bold +3;");
        cmdExitStack
                .setIcon(new ImageIcon(new ImageIcon(getClass().getResource("/hapeekidz/assets/icons/back.png"))
                        .getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH)));
        cmdExitStack.setBorder(new EmptyBorder(9, 10, 9, 10));
        cmdExitStack.addActionListener(this);
        panel.add(cmdExitStack, "growx");
        return panel;
    }

    private JComponent addManageProductInputFieldsPanel(Object[] data) {
        JPanel panel = new JPanel(new MigLayout("wrap 2, fill, insets 18 20 18 20", "", ""));
        /*
         * 0: id, 1: name, 2: description, 3: category, 4: price, 5: SKU, 6: is_taxable
         */
        txtName = new JTextField();
        JLabel lblName = new JLabel("Product Name");
        txtName.putClientProperty(FlatClientProperties.STYLE, "" +
                "arc: 5;" +
                "font: +2;");
        txtName.setText(data[1].toString());

        txtCategory = new JTextField();
        JLabel lblCategory = new JLabel("Category");
        txtCategory.putClientProperty(FlatClientProperties.STYLE, "" +
                "arc: 5;" +
                "font: +2;");
        txtCategory.setText(data[3].toString());

        txtPrice = new JTextField();
        JLabel lblPrice = new JLabel("Rate");
        txtPrice.putClientProperty(FlatClientProperties.STYLE, "" +
                "arc: 5;" +
                "font: +2;");
        txtPrice.setText(data[4].toString());

        txtSKU = new JTextField();
        JLabel lblSKU = new JLabel("SKU");
        txtSKU.putClientProperty(FlatClientProperties.STYLE, "" +
                "arc: 5;" +
                "font: +2;");
        txtSKU.setText(data[5].toString());

        chkTaxable = new JCheckBox("Taxable");
        chkTaxable.setSelected(Boolean.parseBoolean(data[6].toString()));

        txtDescription = new JTextField();
        JLabel lblDescription = new JLabel("Sales Description");
        txtDescription.setPreferredSize(new Dimension(0, 200));
        txtDescription.putClientProperty(FlatClientProperties.STYLE, "" +
                "arc: 5;" +
                "font: +2;");
        txtDescription.setText(data[2].toString());

        panel.add(lblName, "gapy 8");
        panel.add(lblCategory, "gapy 8");
        panel.add(txtName, "growx, gapy 16");
        panel.add(txtCategory, "growx, gapy 16");

        panel.add(lblPrice, "gapy 8");
        panel.add(lblSKU, "gapy 8");
        panel.add(txtPrice, "growx, gapy 16");
        panel.add(txtSKU, "growx, gapy 16");

        panel.add(chkTaxable, "spanx 2, growx, right, wrap");

        panel.add(lblDescription, "gapy 8, spanx 2, growx, wrap");
        panel.add(txtDescription, "spanx 2, growx");
        updateComponents(panel);

        return panel;
    }

    private JComponent addManageProductControlButtons() {
        JPanel panel = new JPanel(new MigLayout("insets 9 10 9 10", "", ""));
        cmdModify = new JButton("Modify");
        cmdModify.setPreferredSize(new Dimension(160, 40));
        cmdModify.addActionListener(this);
        cmdModify.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_BORDERLESS);
        cmdModify.putClientProperty(FlatClientProperties.STYLE, "" +
                "arc: 5;" +
                "font: +2;" +
                "background: #16a34a;" +
                "foreground: #ffffff;");

        cmdDelete = new JButton("Delete");
        cmdDelete.setPreferredSize(new Dimension(160, 40));
        cmdDelete.addActionListener(this);
        cmdDelete.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_BORDERLESS);
        cmdDelete.putClientProperty(FlatClientProperties.STYLE, "" +
                "arc: 5;" +
                "font: +2;" +
                "background: #475569;" +
                "foreground: #ffffff;");

        cmdCancel = new JButton("Cancel");
        cmdCancel.setPreferredSize(new Dimension(160, 40));
        cmdCancel.addActionListener(this);
        cmdCancel.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_BORDERLESS);

        panel.add(cmdDelete, "right, gapx 0");
        panel.add(cmdCancel, "right, gapx 0");
        panel.add(cmdModify, "right, gapx 0");
        return panel;
    }

    private boolean sendProductsToDatabase() {
        boolean rs;
        try {
            rs = checkInvalidFields() && checkDuplicateFields();
            /*
             * TODO: Refactor
             */
            if(rs){
            model.setAddProduct(txtName.getText(), txtDescription.getText(), txtCategory.getText(),
                    Float.parseFloat(txtPrice.getText()), txtSKU.getText(), chkTaxable.isSelected());
            JOptionPane.showMessageDialog(null, "Product added successfully");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Invalid Rate");
            return false;
        }
        return rs;
    }

    private void sendRemoveProudctToDatabase(Object[] data) {
        try {
            int id = Integer.parseInt(data[0].toString());
            model.setRemoveProduct(id);
        } catch (Exception e) {
        }
    }

    private boolean sendUpdateProductToDatabase(Object[] data, int index) {
        try {
            // check if name, category, sku already exists
            boolean rs = checkInvalidFields() && checkDuplicateFields(index);
            if (rs) {
                int id = Integer.parseInt(data[0].toString());
                model.setUpdateProduct(id, txtName.getText(), txtDescription.getText(), txtCategory.getText(),
                        Float.parseFloat(txtPrice.getText()), txtSKU.getText(), chkTaxable.isSelected());
            }
            return rs;
        } catch (Exception e) {

        }
        return true;
    }

    private boolean checkInvalidFields() {
        if (txtName.getText().isEmpty() || txtCategory.getText().isEmpty() || txtPrice.getText().isEmpty()
                || txtSKU.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Please fill out all fields");
            // highlight empty fields
            if (txtName.getText().isEmpty()) {
                txtName.setBorder(BorderFactory.createLineBorder(Color.red));
            }
            if (txtCategory.getText().isEmpty()) {
                txtCategory.setBorder(BorderFactory.createLineBorder(Color.red));
            }
            if (txtPrice.getText().isEmpty()) {
                txtPrice.setBorder(BorderFactory.createLineBorder(Color.red));
            }
            if (txtSKU.getText().isEmpty()) {
                txtSKU.setBorder(BorderFactory.createLineBorder(Color.red));
            }
            return false;
        }
        if (txtPrice.getText().equals("0") || txtPrice.getText().equals("0.0")) {
            JOptionPane.showMessageDialog(null, "Price cannot be 0");
            return false;
        }
        if (Float.parseFloat(txtPrice.getText()) < 0 || Float.parseFloat(txtPrice.getText()) < 0.0) {
            JOptionPane.showMessageDialog(null, "Rate cannot be negative");
            return false;
        }
        return true;
    }

    private boolean checkDuplicateFields() {
        if (model.fetchProductsFromDatabase() == null || model.fetchProductsFromDatabase().length == 0) {
            return true;
        }
        for (int i = 0; i < newData.length; i++) {
            if (newData[i][0].equals(txtName.getText())) {
                JOptionPane.showMessageDialog(null, txtName.getText() + " already exists in the database");
                txtName.setBorder(BorderFactory.createLineBorder(Color.red));
                return false;
            }
            if (newData[i][4].equals(txtSKU.getText())) {
                JOptionPane.showMessageDialog(null,
                        "An item with the SKU " + txtSKU.getText() + " already exists in the database");
                txtSKU.setBorder(BorderFactory.createLineBorder(Color.red));
                return false;
            }
        }
        return true;
    }

    private boolean checkDuplicateFields(int index) {
        if (newData == null || newData.length == 0) {
            return true;
        }
        for (int i = 0; i < newData.length; i++) {
            if (i == index) {
                continue;
            }
            if (newData[i][0].equals(txtName.getText())) {
                JOptionPane.showMessageDialog(null, txtName.getText() + " already exists in the database");
                txtName.setBorder(BorderFactory.createLineBorder(Color.red));
                return false;
            }
            if (newData[i][1].equals(txtCategory.getText())) {
                JOptionPane.showMessageDialog(null, txtCategory.getText() + " already exists in the database");
                txtCategory.setBorder(BorderFactory.createLineBorder(Color.red));
                return false;
            }
            if (newData[i][4].equals(txtSKU.getText())) {
                JOptionPane.showMessageDialog(null,
                        "An item with the SKU " + txtSKU.getText() + " already exists in the database");
                txtSKU.setBorder(BorderFactory.createLineBorder(Color.red));
                return false;
            }
            int id = Integer.parseInt(model.fetchProductsFromDatabase()[0].toString());
            model.setUpdateProduct(id, txtName.getText(), txtDescription.getText(), txtCategory.getText(),
                    Float.parseFloat(txtPrice.getText()), txtSKU.getText(), chkTaxable.isSelected());
        }
        return true;
    }

    /*
     * adjustData() reassigns data for table display
     * param: { "id", "name", "description", "category", "price", "SKU",
     * "is_taxable",}
     * return: { "name", "category", "price", "is_taxable", "action"}
     * TODO: alter 5th column
     */
    private Object[][] adjustData(Object[][] data) {
        if (data == null || data.length == 0) {
            return null;
        }
        newData = new Object[data.length][data[0].length + 1];
        for (int i = 0; i < data.length; i++) {
            newData[i][0] = data[i][1]; // name
            newData[i][1] = data[i][3]; // category
            newData[i][2] = data[i][4]; // price
            newData[i][3] = data[i][6]; // is_taxable
            newData[i][4] = "Edit";
        }
        return newData;
    }
}
