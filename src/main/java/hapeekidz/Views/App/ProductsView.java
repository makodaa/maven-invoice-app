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
 * - Update
 * 
 * ## Add Product Panel
 * - Confirm button: Adds Input Fields to Database
 * - Close Function: Updates Table
 * - Cancel / Back Functions: Closes Table, Disregards Changes
 * 
 * 
 * ## Manage Product Panel
 * - Fecth Field Content from Database
 * - Update Database on Confirmation
*/
public class ProductsView extends JPanel implements ActionListener {
    private JTextField txtName, txtCategory, txtPrice, txtSKU, txtDescription;
    private JCheckBox chkTaxable;
    private JButton cmdAdd, cmdCancel, cmdConfirm, cmdBack, cmdCancelAddProduct;
    private JTable table;
    private Products model = new Products();
    private JComponent floatingPanel = addProductPanel();
    private Object[][] newData;

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
            boolean rs = sendProductsToDatabase();
            if (rs) {
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
        if (e.getSource() == cmdCancelAddProduct) {
            JPanel glassPane = (JPanel) frame.getGlassPane();
            glassPane.setVisible(false);
            glassPane.removeAll();
        }
        updateFrame(frame);
    }

    public ProductsView() {
        init();
    }

    private void init() {
        setLayout(new MigLayout("wrap 1, fillx, gapx 0, insets 0", "", ""));
        setBackground(Color.red);
        add(WindowHeader(), "growx, left");
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
        Object rowData[][] = adjustData(model.fetchProductsFromDatabase());
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
        panel.add(txtSearch, "growx");
        panel.add(cmdFilter, "shrinkx, gapx 10");
        panel.add(cmdAdd, "right");
        return panel;
    }

    private JComponent addTablePanel() {
        Object rowData[][] = adjustData(model.fetchProductsFromDatabase());
        Object columnNames[] = { "Products / Services", "Category", "Rate", "Taxable", "Action" };

        JPanel panel = new JPanel(new MigLayout("fillx, insets 9 10 9 10", "[grow]", "[grow]"));
        JScrollPane scrollPane = new JScrollPane();
        panel.add(scrollPane, "growx");

        table = new JTable();
        table.setBackground(Color.white);
        table.setRowHeight(40);
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

    private JComponent addProductPanel() {
        JPanel panel = new JPanel(new MigLayout("wrap 1, fillx, insets 0", "", ""));
        panel.add(addProductPanelHeader(), "growx");
        panel.add(inputFieldsPanel(), "growx");
        panel.add(controlButtons(), "right");
        updateComponents(panel);
        return panel;
    }

    private JComponent addProductPanelHeader() {
        JPanel panel = new JPanel(new MigLayout("insets 0", "", ""));
        cmdCancelAddProduct = new JButton("Add a Product");
        cmdCancelAddProduct.putClientProperty(FlatClientProperties.STYLE, "" +
                "font:bold +3;");
        cmdCancelAddProduct
                .setIcon(new ImageIcon(new ImageIcon(getClass().getResource("/hapeekidz/assets/icons/back.png"))
                        .getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH)));
        cmdCancelAddProduct.setBorder(new EmptyBorder(9, 10, 9, 10));
        cmdCancelAddProduct.addActionListener(this);
        panel.add(cmdCancelAddProduct, "growx");
        return panel;
    }

    private JComponent inputFieldsPanel() {
        /*
         * TODO: Make fields required
         */
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
        panel.revalidate();
        panel.repaint();
        return panel;
    }

    private JComponent controlButtons() {
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

    public boolean sendProductsToDatabase() {
        try {
            if (txtName.getText().isEmpty() || txtCategory.getText().isEmpty() || txtPrice.getText().isEmpty()
                    || txtSKU.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please fill out all fields");
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

            for (int i = 0; i < newData.length; i++) {
                if (newData[i][0].equals(txtName.getText())) {
                    JOptionPane.showMessageDialog(null, txtName.getText() + " already exists in the database");
                    return false;
                }
                if (newData[i][1].equals(txtCategory.getText())) {
                    JOptionPane.showMessageDialog(null, txtCategory.getText() + " already exists in the database");
                    return false;
                }
                if (newData[i][4].equals(txtSKU.getText())) {
                    JOptionPane.showMessageDialog(null,
                            "An item with the SKU " + txtSKU.getText() + " already exists in the database");
                    return false;
                }
            }

            model.setProduct(txtName.getText(), txtDescription.getText(), txtCategory.getText(),
                    Float.parseFloat(txtPrice.getText()), txtSKU.getText(), chkTaxable.isSelected());
            JOptionPane.showMessageDialog(null, "Product added successfully");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Invalid Rate");
            return false;
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
