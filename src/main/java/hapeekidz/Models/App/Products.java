package hapeekidz.Models.App;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

public class Products {
    Connection con;
    PreparedStatement pst;
    ResultSet rs;

    private int id;
    private String name;
    private String description;
    private String category;
    private float price;
    private String SKU;
    private boolean is_taxable;

    public Products() {
        init();
    }

    private void init() {
        try {
            con = DriverManager.getConnection(
                    "jdbc:mysql://127.0.0.1:3306",
                    "root",
                    "MkbcMySQL2023-");
            pst = con.prepareStatement("CREATE DATABASE IF NOT EXISTS products");
            pst.executeUpdate();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "1" + e.getMessage());
            e.printStackTrace();
        }
    }
    public Object[][] fetchProductsFromDatabase() {
        Object[][] data;
        List<Object[]> dataList = new ArrayList<>();

        try {
            pst = con.prepareStatement("SELECT * FROM sys.products");
            rs = pst.executeQuery();

            while (rs.next()) {
                Object[] row = new Object[7];
                row[0] = rs.getInt("ID_PRODUCTS");
                row[1] = rs.getString("PRODUCT_NAME");
                row[2] = rs.getString("PRODUCT_DESCRIPTION");
                row[3] = rs.getString("PRODUCT_CATEGORY");
                row[4] = rs.getFloat("PRODUCT_RATE");   
                row[5] = rs.getString("PRODUCT_SKU");
                row[6] = rs.getBoolean("PRODUCT_IS_TAXABLE");
                dataList.add(row);
            }

            // Convert the list to a 2D array
            data = dataList.toArray(new Object[0][]);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
            e.printStackTrace();
            data = new Object[0][0];
        } finally {
            // Close resources in a finally block
            try {
                if (rs != null) rs.close();
                if (pst != null) pst.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return data;
    }
}
