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
            pst = con.prepareStatement("CREATE DATABASE IF NOT EXISTS sys");
            pst.executeUpdate();
            pst = con.prepareStatement("USE sys");
            pst.executeUpdate();
            pst = con.prepareStatement("CREATE TABLE IF NOT EXISTS sys.products (ID_PRODUCTS INT NOT NULL AUTO_INCREMENT, PRODUCT_NAME VARCHAR(255) NOT NULL, PRODUCT_DESCRIPTION VARCHAR(255) NOT NULL, PRODUCT_CATEGORY VARCHAR(255) NOT NULL, PRODUCT_RATE FLOAT NOT NULL, PRODUCT_SKU VARCHAR(255) NOT NULL, PRODUCT_IS_TAXABLE BOOLEAN NOT NULL, PRIMARY KEY (ID_PRODUCTS))");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "1" + e.getMessage());
            e.printStackTrace();
        }
    }
    public void setAddProduct(String name, String description, String category, float price, String SKU, boolean is_taxable){
        this.name = name;
        this.description = description;
        this.category = category;
        this.price = price;
        this.SKU = SKU;
        this.is_taxable = is_taxable;
        addProductToDatabase();
    }

    public void setRemoveProduct(int id){
        this.id = id;
        removeProductfromDatabase();
    }

    public void setUpdateProduct(int id, String name, String description, String category, float price, String SKU, boolean is_taxable){
        this.id = id;
        this.name = name;
        this.description = description;
        this.category = category;
        this.price = price;
        this.SKU = SKU;
        this.is_taxable = is_taxable;
        updateProductToDatabase();
    }

    private void addProductToDatabase() {
        try {
            pst = con.prepareStatement("INSERT INTO sys.products (PRODUCT_NAME, PRODUCT_DESCRIPTION, PRODUCT_CATEGORY, PRODUCT_RATE, PRODUCT_SKU, PRODUCT_IS_TAXABLE) VALUES (?, ?, ?, ?, ?, ?)");
            pst.setString(1, name);
            pst.setString(2, description);
            pst.setString(3, category);
            pst.setFloat(4, price);
            pst.setString(5, SKU);
            pst.setBoolean(6, is_taxable);
            pst.executeUpdate();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Close resources in a finally block
            try {
                if (pst != null) pst.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void removeProductfromDatabase() {
        try {
            pst = con.prepareStatement("DELETE FROM sys.products WHERE ID_PRODUCTS = ?");
            pst.setInt(1, id);
            pst.executeUpdate();
            JOptionPane.showMessageDialog(null, "Product Removed Successfully");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Close resources in a finally block
            try {
                if (pst != null) pst.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateProductToDatabase(){
        try {
            pst = con.prepareStatement("UPDATE sys.products SET PRODUCT_NAME = ?, PRODUCT_DESCRIPTION = ?, PRODUCT_CATEGORY = ?, PRODUCT_RATE = ?, PRODUCT_SKU = ?, PRODUCT_IS_TAXABLE = ? WHERE ID_PRODUCTS = ?");
            pst.setString(1, name);
            pst.setString(2, description);
            pst.setString(3, category);
            pst.setFloat(4, price);
            pst.setString(5, SKU);
            pst.setBoolean(6, is_taxable);
            pst.setInt(7, id);
            pst.executeUpdate();
            JOptionPane.showMessageDialog(null, "Product Updated Successfully");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Close resources in a finally block
            try {
                if (pst != null) pst.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
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
