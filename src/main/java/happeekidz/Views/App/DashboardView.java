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

public class DashboardView extends JPanel {
    public DashboardView() {
        init();
    }

    private void init() {
        setLayout(new MigLayout("wrap 1, fill, gapx 0, insets 0", "", ""));
        add(WindowHeader(), "growx");
    }

    private JComponent WindowHeader() {
        JPanel panel = new JPanel(new MigLayout("insets 0, gapx 0", "", ""));
        JLabel lblBusiness = new JLabel("Carrel's Real Property Leasing");
        lblBusiness.putClientProperty(FlatClientProperties.STYLE_CLASS, "" +
                "h1");
        panel.add(lblBusiness, "growx");
        return panel;
    }

}