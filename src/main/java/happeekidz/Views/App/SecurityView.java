package happeekidz.Views.App;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import com.formdev.flatlaf.FlatClientProperties;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import net.miginfocom.swing.MigLayout;

public class SecurityView extends JPanel implements ActionListener{
    @Override
    public void actionPerformed(ActionEvent e) {
        
    }
    public SecurityView() {
        init();
    }

    private void init() {
        setLayout(new MigLayout("wrap 1, fill, gapx 0, insets 0", "", ""));
        add(addWindowHeader(), "growx");
        add(addSecurityPanel(), "growx");
    }
        private JComponent addWindowHeader() {
        JPanel panel = new JPanel(new MigLayout("insets 0, gapx 0", "", ""));
        JLabel lblPanelTitle = new JLabel("Security & Backup");
        lblPanelTitle.setBorder(new EmptyBorder(9, 10, 9, 10));
        lblPanelTitle.putClientProperty(FlatClientProperties.STYLE, "" +
                "font:bold +2;");
        panel.add(lblPanelTitle, "growx");
        return panel;
    }

    private JComponent addSecurityPanel(){
        JPanel panel = new JPanel(new MigLayout("insets 0, gapx 0", "", ""));
        JLabel lblPanelTitle = new JLabel("Security");
        lblPanelTitle.setBorder(new EmptyBorder(9, 10, 9, 10));
        lblPanelTitle.putClientProperty(FlatClientProperties.STYLE, "" +
                "font:bold +2;");
        panel.add(lblPanelTitle, "growx");
        return panel;
    }
}
