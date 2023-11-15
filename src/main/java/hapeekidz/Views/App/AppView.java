package hapeekidz.Views.App;

import java.awt.*;
import javax.swing.*;
import java.lang.reflect.*;
import java.net.URL;
import java.util.*;

import javax.swing.border.EmptyBorder;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.formdev.flatlaf.FlatClientProperties;
import net.miginfocom.swing.MigLayout;

public class AppView extends JFrame {

    JPanel body = new JPanel(new MigLayout("fill, insets 0", "", ""));

    private void init() {
        setTitle("Invoice System");
        setSize(new Dimension(1200, 700));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
        this.add(GroupedPanel());
    }

    public AppView() {
        init();
    }

    public AppView(String current) {
        init();
        showPanel("hapeekidz.Views.App." + current + "View", current);
        setTitle(current);
    }

    private Component GroupedPanel() {
        JPanel panel = new JPanel(new MigLayout("insets 0 10 0 0, gapx 0", "[][grow]", "grow"));
        Navigator navigator = new Navigator(this);
        panel.add(navigator);
        panel.add(body, "grow");
        return panel;
    }

    public void logout() {
        JOptionPane.showMessageDialog(null, "Do you want to logout");
    }

    public void showPanel(String strComponent, String strWindow) {
        Object sameClassObject = null;
        try {
            Class<?> cls = Class.forName(strComponent);
            Constructor<?> cons = cls.getConstructor();
            sameClassObject = cons.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        body.removeAll();
        body.add((Component)sameClassObject, "grow");
        body.revalidate();
        body.repaint();
        this.setTitle(strWindow);
    }

}

class Navigator extends JComponent {
    private AppView view;
    ActionListener listener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() instanceof JButton) {
                String text = (((JButton) e.getSource()).getText()).split(" ")[0];
                if(text == "Logout"){
                    view.logout();
                }
                else {
                    view.showPanel("hapeekidz.Views.App." + text + "View", text);
                }
            }
        }
    };

    private String[] links = {
            "Dashboard",
            "Invoices & Sales",
            "Customers",
            "Products & Services",
            "Security & Backup",
            "Logout"
    };

    private void init() {
        setLayout(new MigLayout(
            "fill, gapy 0, wrap 1, insets 0","",""));
        JLabel lblTitle = new JLabel("Rental Services System");
        lblTitle.setIcon(new ImageIcon(new ImageIcon(getClass().getResource("/hapeekidz/assets/icons/logo.png")).getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH)));
        lblTitle.putClientProperty(FlatClientProperties.STYLE, "" +
                "font:bold +4");
        add(lblTitle);
        for (int i = 0; i < links.length; i++) {
            addNavigation(links[i], i);
        }
    }

    public Navigator(AppView view) {
        this.view = view;
        init();
    }

    private Icon getIcon(int index){
        String[] arr = {"Dashboard", "Invoices", "Customers", "Products", "Security", "Logout"};
        URL url = getClass().getResource("/hapeekidz/assets/icons/" + arr[index] + ".png");
        if (url != null) {
            return new ImageIcon(new ImageIcon(url).getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH));
        } else {
            return null;    
        }
    }

    private void addNavigation(String name, int index) {
        NavigationItem item = new NavigationItem(name, index);
        Icon icon = getIcon(index);
        if (icon != null) {
            item.setIcon(icon);
        }
        item.addActionListener(listener);
        add(item);
        revalidate();
        repaint();
    }
}

class NavigationItem extends JButton {
    private int index;

    public NavigationItem(String name, int index) {
        super(name);
        this.index = index;
        setForeground(Color.BLACK);
        setHorizontalAlignment(SwingConstants.LEFT);
        setBackground(new Color(0,0,0, 3));
        setBorder(new EmptyBorder(9, 3, 9, 10));
        setIconTextGap(10);
        setPreferredSize(new Dimension(250, 70));
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

}
