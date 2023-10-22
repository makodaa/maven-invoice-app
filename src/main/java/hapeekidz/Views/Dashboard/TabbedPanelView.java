package hapeekidz.Views.Dashboard;

import java.awt.*;
import javax.swing.*;
import java.lang.reflect.*;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.components.FlatTabbedPane.TabAlignment;

import net.miginfocom.swing.MigLayout;

public class TabbedPanelView extends JFrame {

    JPanel body = new JPanel();

    private void init() {
        setTitle("Login Page");
        setSize(new Dimension(1200, 700));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
        this.add(GroupedPanel());
    }

    public TabbedPanelView() {
        init();
    }

    private Component GroupedPanel() {
        JPanel panel = new JPanel(new MigLayout("insets 0", "[] [grow]", "[grow]"));
        Navigator navigator = new Navigator(this);
        body.setBackground(Color.black);
        panel.add(navigator);
        panel.add(body, "grow");
        return panel;
    }

    public void showPanel(String strComponent) {
        Object sameClassObject = null;
        try {
            Class<?> cls = Class.forName(strComponent);
            Constructor<?> cons = cls.getConstructor();
            sameClassObject = cons.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        body.removeAll();
        body.add((Component)sameClassObject);
        body.revalidate();
        body.repaint();
    }

}

class DashboardPanel extends JPanel {
    public DashboardPanel() {
        setBackground(Color.red);
        setPreferredSize(new Dimension(100, 100));
    }
}

class InvoicesPanel extends JPanel {
    public InvoicesPanel() {
        setBackground(Color.blue);
        setPreferredSize(new Dimension(100, 100));
    }
}

class CustomerPanel extends JPanel {
    public CustomerPanel() {
        setBackground(Color.green);
        setPreferredSize(new Dimension(100, 100));
    }
}

class ProductsPanel extends JPanel {
    public ProductsPanel() {
        setBackground(Color.yellow);
        setPreferredSize(new Dimension(100, 100));
    }
}

class SecurityPanel extends JPanel {
    public SecurityPanel() {
        setBackground(Color.orange);
        setPreferredSize(new Dimension(100, 100));
    }
}

class LogoutPanel extends JPanel {
    public LogoutPanel() {
        JOptionPane.showMessageDialog(null, "Insert Logout Function");
    }
}

class Navigator extends JComponent {
    private TabbedPanelView view;
    /*
     * Temporary Action Listener, should be in Controller
     * TODO: Move to Controller
     */
    ActionListener listener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() instanceof JButton) {
                String text = (((JButton) e.getSource()).getText()).split(" ")[0];
                view.showPanel("hapeekidz.Views.Dashboard." + text + "Panel");
            }
        }
    };

    private String[] links = {
            "Dashboard",
            "Invoices & Sales",
            "Customer",
            "Products & Services",
            "Security & Backup",
            "Logout"
    };

    private void init() {
        setLayout(new MigLayout("wrap 1, fill, gapy 0, insets 2 0 2 2", "fill"));
        JLabel lblTitle = new JLabel("Rental Services System");
        lblTitle.putClientProperty(FlatClientProperties.STYLE, "" +
                "font:bold +4");
        add(lblTitle, "growy");
        for (int i = 0; i < links.length; i++) {
            addNavigation(links[i], i);
        }
    }

    public Navigator(TabbedPanelView view) {
        this.view = view;
        init();
    }

    private void addNavigation(String name, int index) {
        NavigationItem item = new NavigationItem(name, index);
        /*
         * Temporary Action Listener, should be in Controller
         * TODO: Move to Controller
         */
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
        setBorder(new EmptyBorder(9, 10, 9, 10));
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
