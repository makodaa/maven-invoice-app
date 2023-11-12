package hapeekidz.Views.Admin;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import com.formdev.flatlaf.FlatClientProperties;
import net.miginfocom.swing.MigLayout;


public class ProductsView extends JPanel {
    public ProductsView() {
        setLayout(new MigLayout("wrap 1, fill, insets 0", "", ""));
        setBackground(Color.red);
        add(addWindowHeader(), "growx, left"); 
        add(addControlInterfacePanel(), "growx"); 
    }
    private JComponent addWindowHeader(){
        JPanel panel = new JPanel(new MigLayout("insets 0, gapx 0", "", ""));
        JButton btnBack = new JButton("Products");
        btnBack.putClientProperty(FlatClientProperties.STYLE, "" +
                "font:bold +2;");
        btnBack.setIcon(new ImageIcon(new ImageIcon(getClass().getResource("/hapeekidz/assets/icons/back.png")).getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH)));
        btnBack.setBorder(new EmptyBorder(9, 10, 9 ,10));
        btnBack.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Back");
            }
        });
        panel.add(btnBack, "growx");
        return panel;
    }
    private JComponent addControlInterfacePanel(){
        JPanel panel = new JPanel(new MigLayout("fill, insets 9 10 9 10", "", ""));
        JTextField txtSearch = new JTextField();
        txtSearch.putClientProperty(FlatClientProperties.STYLE, "" +
        "arc: 5;" + 
        "font: +2;");
        txtSearch.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Search for a product");
        JButton cmdFilter = new JButton("");
        cmdFilter.setIcon(new ImageIcon(new ImageIcon(getClass().getResource("/hapeekidz/assets/icons/filter.png")).getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH)));
        cmdFilter.setBorder(new EmptyBorder(0, 0, 0, 0));
        JButton cmdAdd = new JButton("Add a Product");
        cmdAdd.setPreferredSize(new Dimension(160, 40));
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
    private JComponent addTablePanel(){
        JPanel panel = new JPanel();
        return panel;
    }
}
