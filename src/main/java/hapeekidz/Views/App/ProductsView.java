package hapeekidz.Views.App;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import com.formdev.flatlaf.FlatClientProperties;

import hapeekidz.Models.App.Products;
import net.miginfocom.swing.MigLayout;

public class ProductsView extends JPanel implements ActionListener {
    private JButton cmdAdd, cmdCancel, cmdConfirm;
    private JTable table;
    private Products model = new Products();
    private JComponent cardPanel = new JPanel(new CardLayout());

    @Override
    public void actionPerformed(ActionEvent e) {
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        if (e.getSource() == cmdAdd) {
            addProductPanel();
        }
        if (e.getSource() == cmdCancel || e.getSource() == cmdConfirm) {
            frame.setContentPane(new AppView());
        }
        frame.revalidate();
        frame.repaint();
    }

    public ProductsView() {
        init();
    }

    public void init() {
        setLayout(new MigLayout("wrap 1, fill, gapx 0, insets 0", "", ""));
        setBackground(Color.red);
        add(WindowHeader(), "growx, left");
        add(ControlPanel(), "growx, growy");
        add(addTablePanel(), "growx, growy");
    }

    private JComponent WindowHeader() {
        JPanel panel = new JPanel(new MigLayout("insets 0, gapx 0", "", ""));
        JButton btnBack = new JButton("Products");
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
        panel.add(btnBack, "growx");
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

        JPanel panel = new JPanel(new MigLayout("fillx, insets 9 10 9 10", "[grow]", "[grow]"));
        Object rowData[][] = adjustData(model.fetchProductsFromDatabase());
        Object columnNames[] = { "Products / Services", "Category", "Rate", "Taxable", "Action" };

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

    private void addProductPanel() {
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        JPanel glassPane = new JPanel(null);
        frame.setGlassPane(glassPane);
        glassPane.add(cardPanel);
        glassPane.setVisible(true);
        cardPanel.setSize(frame.getContentPane().getSize());
        cardPanel.setBounds(0, 0, frame.getContentPane().getWidth(), frame.getContentPane().getHeight());
        glassPane.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
            }
        });
        frame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                SwingUtilities.invokeLater(() -> {
                    cardPanel.setSize(frame.getContentPane().getSize());
                    cardPanel.revalidate();
                    cardPanel.repaint();
                });
            }
        });
    }
    /*
     * adjustData() reassigns data for table display
     * param: { "id", "name", "description", "category", "price", "SKU", "is_taxable",}
     * return: { "name", "category", "price", "is_taxable", "action"}
    */
    private Object[][] adjustData(Object[][] data) {
        Object[][] newData = new Object[data.length][data[0].length + 1];
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
