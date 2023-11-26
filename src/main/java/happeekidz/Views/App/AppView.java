package happeekidz.Views.App;

import java.awt.*;
import javax.swing.*;
import java.lang.reflect.*;
import java.net.URL;

import javax.swing.border.EmptyBorder;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.formdev.flatlaf.FlatClientProperties;
import net.miginfocom.swing.MigLayout;

public class AppView extends JFrame implements ActionListener{

    /*
     * Implemented Methods
    */

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() instanceof JButton) {
            String text = (((JButton) e.getSource()).getText()).split(" ")[0];
            if (text == "Logout") {
                logout();
            } else {
                showPanel("happeekidz.Views.App." + text + "View", text);
            }
        }
    }

    /*
     * AppView Fields
    */
    JPanel body = new JPanel(new MigLayout("fill, insets 0", "", ""));
    private String[] links = {
            "Dashboard",
            "Invoices & Sales",
            "Customers",
            "Products & Services",
            "Security & Backup",
            "Logout"
    };

    private void init() {
        setSize(new Dimension(1200, 700));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
        this.add(GroupedPanel());
        showPanel("happeekidz.Views.App.DashboardView", "Dashboard");
    }

    public AppView() {
        init();
    }

    private Component GroupedPanel() {
        JPanel panel = new JPanel(new MigLayout("insets 0 10 0 0, gapx 0", "[][grow]", "grow"));
        panel.add(navBar());
        panel.add(body, "grow");
        return panel;
    }

    public void logout() {
        JOptionPane.showMessageDialog(null, "Do you want to logout?");
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
        body.add((Component) sameClassObject, "grow");
        body.revalidate();
        body.repaint();
        this.setTitle(strWindow);
    }

    private JComponent navBar() {
        JPanel panel = new JPanel(new MigLayout(
                "fill, gapy 0, wrap 1, insets 0", "", ""));
        JLabel lblTitle = new JLabel("Rental Services System");
        lblTitle.setIcon(new ImageIcon(new ImageIcon(getClass().getResource("/happeekidz/assets/icons/logo.png"))
                .getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH)));
        lblTitle.putClientProperty(FlatClientProperties.STYLE, "" +
                "font:bold +4");
        panel.add(lblTitle);
        for (int i = 0; i < links.length; i++) {
            addNavigation(panel, links[i], i);
        }
        return panel;
    }

    private Icon getIcon(int index) {
        String[] arr = { "Dashboard", "Invoices", "Customers", "Products", "Security", "Logout" };
        URL url = getClass().getResource("/happeekidz/assets/icons/" + arr[index] + ".png");
        if (url != null) {
            return new ImageIcon(new ImageIcon(url).getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH));
        } else {
            return null;
        }
    }

    private void addNavigation(JPanel paramPanel, String name, int index) {
        JPanel panel = paramPanel;
        NavigationItem item = new NavigationItem(name, index);
        Icon icon = getIcon(index);
        if (icon != null) {
            item.setIcon(icon);
        }
        item.addActionListener(this);
        panel.add(item);
        panel.revalidate();
        panel.repaint();
    }

    ActionListener listener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() instanceof JButton) {
                String text = (((JButton) e.getSource()).getText()).split(" ")[0];
                if (text == "Logout") {
                    logout();
                } else {
                    showPanel("happeekidz.Views.App." + text + "View", text);
                }
            }
        }

};
}

    class NavigationItem extends JButton {
        private int index;

        public NavigationItem(String name, int index) {
            super(name);
            this.index = index;
            setForeground(Color.BLACK);
            setHorizontalAlignment(SwingConstants.LEFT);
            setBackground(new Color(0, 0, 0, 3));
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

