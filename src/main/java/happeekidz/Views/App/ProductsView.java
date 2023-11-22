package happeekidz.Views.App;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.json.ParseException;

import happeekidz.Models.App.Products;
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
public class ProductsView extends JPanel implements ActionListener, MouseListener{
    private JTextField txtName, txtCategory, txtPrice, txtSKU, txtDescription;
    private JCheckBox chkTaxable;
    private JButton cmdAdd, cmdCancel, cmdConfirm, cmdBack, cmdExitStack, cmdModify, cmdDelete, cmdDummyButton;
    private JTable table;
    private Products model = new Products();
    private JComponent productPanel;

    @Override
    public void mouseClicked(MouseEvent e) {
        int column = table.getColumnModel().getColumnIndexAtX(e.getX());
        int row    = e.getY()/table.getRowHeight();

        System.out.println("Fucking Debugging Moments, row: " + row + " column: " + column);

            if (column == 4) {
                showLayeredPanel(manageProductPanel(model.fetchProductsFromDatabase()[row]));
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
            showLayeredPanel(addProductPanel());
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
            boolean rs = sendAddProductToDatabase();
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
            if (model.fetchProductsFromDatabase().length == 0) {
                JOptionPane.showMessageDialog(null, "No products to manage");
                return;
            }
            showLayeredPanel(manageProductPanel(model.fetchProductsFromDatabase()[0]));
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
                if (model.fetchProductsFromDatabase().length == 0) {
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
        Object rowData[][] = adjustData(model.fetchProductsFromDatabase()) == null
                ? new Object[][] { { "", "", "", "", "" } }
                : adjustData(model.fetchProductsFromDatabase());
        Object columnNames[] = { "Products / Services", "Category", "Rate", "Taxable", "Action" };
        table.setModel(new DefaultTableModel(rowData, columnNames));
        table.getColumnModel().getColumn(3).setCellRenderer(new ImageRenderer());
        table.getColumnModel().getColumn(4).setCellRenderer(new ButtonRenderer());
        table.getColumnModel().getColumn(0).setPreferredWidth(107);
    }

    private JComponent WindowHeader() {
        JPanel panel = new JPanel(new MigLayout("insets 0, gapx 0", "", ""));
        cmdBack = new JButton("Products");
        cmdBack.putClientProperty(FlatClientProperties.STYLE, "" +
                "font:bold +3;");
        cmdBack.setIcon(new ImageIcon(new ImageIcon(getClass().getResource("/happeekidz/assets/icons/back.png"))
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
        cmdFilter.setIcon(new ImageIcon(new ImageIcon(getClass().getResource("/happeekidz/assets/icons/filter.png"))
                .getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH)));
        cmdFilter.setBorder(new EmptyBorder(0, 0, 0, 0));
        cmdAdd = newFormButton("Add Product","arc: 5;" +"font: +2;" +"background: #16a34a;" +"foreground: #ffffff;");

        /*
         * Dummy button iz here
         */
        cmdDummyButton = newFormButton("Dummy Button","arc: 5;" +"font: +2;" +"background: #16a34a;" +"foreground: #ffffff;");

        panel.add(txtSearch, "growx");
        panel.add(cmdFilter, "shrinkx, gapx 10");
        panel.add(cmdAdd, "right");
        panel.add(cmdDummyButton, "right, gapx 0");
        return panel;
    }

    private JComponent addTablePanel() {
        Object rowData[][] = adjustData(model.fetchProductsFromDatabase()) == null || model.fetchProductsFromDatabase().length == 0
                ? new Object[][] { { "", "", "", "", "" } }
                : adjustData(model.fetchProductsFromDatabase());
        Object columnNames[] = { "Products / Services", "Category", "Rate", "Taxable", "Action" };

        JPanel panel = new JPanel(new MigLayout("fill, insets 9 10 9 10", "[grow]", "[grow]"));
        JScrollPane scrollPane = new JScrollPane();
        panel.add(scrollPane, "growx, growy");

        table = new JTable();
        table.setBackground(Color.white);
        table.setRowHeight(40);

        DefaultTableModel tableModel = new DefaultTableModel(rowData, columnNames);
        table.setModel(tableModel);
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setResizingAllowed(false);
        table.setDefaultEditor(Object.class, null);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        table.setDefaultRenderer(String.class, centerRenderer);

        table.getColumnModel().getColumn(4).setCellRenderer(new ButtonRenderer());
        table.getColumnModel().getColumn(0).setPreferredWidth(107);
        table.getColumnModel().getColumn(3).setCellRenderer(new ImageRenderer());

        table.addMouseListener(this);

        scrollPane.setViewportView(table);

        return panel;
    }
    class ButtonRenderer extends JButton implements TableCellRenderer {
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setText("Manage Product");
            putClientProperty(FlatClientProperties.STYLE, "" +
                    "font: -1;" +
                    "foreground: #16a34a;");
            //set empty border
            setBorder(BorderFactory.createEmptyBorder());
            //add actionlistener based on row index

            return this;
        }
    }

    class ImageRenderer extends DefaultTableCellRenderer {
        JLabel label = new JLabel();
    
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Object columnValue = table.getModel().getValueAt(row, 3);
            // Now you can use columnValue
            if (columnValue instanceof Boolean) {
                label.setText("");
                if ((boolean) columnValue) {
                    label.setIcon(new ImageIcon(getClass().getResource("/happeekidz/assets/icons/verified.png")));
                } else {
                    label.setIcon(new ImageIcon(getClass().getResource("/happeekidz/assets/icons/unverified.png")));
                }
            }
            label.setHorizontalAlignment(JLabel.CENTER);
            return label;
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
    /*
     * refactored code
     * 1: panel header
     * 2: input fields
     * 3: labels
     * 4: control buttons
     */

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
        //add client property for red focus

        txtField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, placeholder);
        txtField.putClientProperty(FlatClientProperties.STYLE, "" +
        "showClearButton:true");

        txtField.setText(setText);
        return txtField;
    }

    private JButton newFormButton (String text, String style) {
        JButton btn = new JButton(text);
        btn.setPreferredSize(new Dimension(160, 40));
        btn.addActionListener(this);
        btn.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_BORDERLESS);
        btn.putClientProperty(FlatClientProperties.STYLE, style);

        return btn;
    }

    private JComponent addProductPanelHeader() {
        JPanel panel = new JPanel(new MigLayout("insets 0", "", ""));
        cmdExitStack = new JButton("Add a Product");
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

    private JComponent addProductInputFieldsPanel() {
        JPanel panel = new JPanel(new MigLayout("wrap 2, fill, insets 18 20 18 20", "", ""));

        txtName = newFormTextField("Product Name", "");
        JLabel lblName = newFormLabel("Product Name");

        txtCategory = newFormTextField("Category", "");
        JLabel lblCategory = newFormLabel("Category");

        txtPrice = newFormTextField("Rate", "");
        JLabel lblPrice = newFormLabel("Rate");

        txtSKU = newFormTextField("SKU", "");
        JLabel lblSKU = newFormLabel("SKU");

        chkTaxable = newFormCheckBox("Taxable");

        txtDescription = newFormTextArea("Sales Description","");
        JLabel lblDescription = newFormLabel("Sales Description");

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
        cmdConfirm = newFormButton("Confirm","arc: 5;" +"font: +2;" +"background: #16a34a;" +"foreground: #ffffff;");

        cmdCancel = newFormButton("Cancel","arc: 5;" +"font: +2;" +"background: #475569;" +"foreground: #ffffff;");
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
    // Refactor this -->
    private JComponent addManageProductPanelHeader() {
        JPanel panel = new JPanel(new MigLayout("insets 0", "", ""));
        cmdExitStack = new JButton("Manage Product");
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

    private JComponent addManageProductInputFieldsPanel(Object[] data) {
        JPanel panel = new JPanel(new MigLayout("wrap 2, fill, insets 18 20 18 20", "", ""));
        /*
         * 0: id, 1: name, 2: description, 3: category, 4: price, 5: SKU, 6: is_taxable
         */

        txtName = newFormTextField("Product Name", data[1].toString());
        JLabel lblName = newFormLabel("Product Name");

        txtCategory = newFormTextField("Category", data[3].toString());
        JLabel lblCategory = newFormLabel("Category");

        txtPrice = newFormTextField("Rate", data[4].toString());
        JLabel lblPrice = newFormLabel("Rate");

        txtSKU = newFormTextField("SKU", data[5].toString());
        JLabel lblSKU = newFormLabel("SKU");

        chkTaxable = newFormCheckBox("Taxable");
        chkTaxable.setSelected(Boolean.parseBoolean(data[6].toString()));

        txtDescription = newFormTextArea("Sales Description", data[2].toString());
        JLabel lblDescription = newFormLabel("Sales Description");


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

        cmdModify = newFormButton("Modify","arc: 5;" +"font: +2;" +"background: #16a34a;" +"foreground: #ffffff;");
        cmdDelete = newFormButton("Delete","arc: 5;" +"font: +2;" +"background: #475569;" +"foreground: #ffffff;");
        cmdCancel = newFormButton("Cancel","arc: 5;" +"font: +2;" +"background: #475569;" +"foreground: #ffffff;");

        panel.add(cmdDelete, "right, gapx 0");
        panel.add(cmdCancel, "right, gapx 0");
        panel.add(cmdModify, "right, gapx 0");
        return panel;
    }

    private boolean sendAddProductToDatabase() {
        boolean rs;
        try {
            rs = checkInvalidFields() && checkDuplicateFields();
            /*
             * TODO: Refactor
             */
            if (rs) {
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
            }
            if (txtCategory.getText().isEmpty()) {
            }
            if (txtPrice.getText().isEmpty()) {
            }
            if (txtSKU.getText().isEmpty()) {
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
        for (int i = 0; i < model.fetchProductsFromDatabase().length; i++) {
            if (model.fetchProductsFromDatabase()[i][1].equals(txtName.getText())) {
                JOptionPane.showMessageDialog(null, txtName.getText() + " already exists in the database");
                txtName.requestFocus();
                return false;
            }
            if (model.fetchProductsFromDatabase()[i][5].equals(txtSKU.getText())) {
                JOptionPane.showMessageDialog(null,
                        "An item with the SKU " + txtSKU.getText() + " already exists in the database");
                txtSKU.requestFocus();
                return false;
            }
        }
        return true;
    }

    private boolean checkDuplicateFields(int index) {
        Object[][] arr = model.fetchProductsFromDatabase();
        if (arr.length == 0) {
            return true;
        }
        for (int i = 0; i < arr.length; i++) {
            if (i == index) {
                continue;
            }
            if (arr[i][1].equals(txtName.getText())) {
                JOptionPane.showMessageDialog(null, txtName.getText() + " already exists in the database");
                return false;
            }
            if (arr[i][5].equals(txtSKU.getText())) {
                JOptionPane.showMessageDialog(null,
                        "An item with the SKU " + txtSKU.getText() + " already exists in the database");
                return false;
            }
            int id = Integer.parseInt(arr[i][0].toString());
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
        Object[][] arr = new Object[data.length][5];
        if (data == null || data.length == 0) {
            return null;
        }
        for (int i = 0; i < data.length; i++) {
            arr[i][0] = data[i][1]; // name
            arr[i][1] = data[i][3]; // category
            arr[i][2] = data[i][4]; // price
            arr[i][3] = data[i][6]; // is_taxable
            arr[i][4] = "Edit";
        }
        return arr;
    }
}
