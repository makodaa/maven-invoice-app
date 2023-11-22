package happeekidz.Models.App;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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
            pst = con.prepareStatement("CREATE DATABASE IF NOT EXISTS happeekidz");
            pst.executeUpdate();
            pst = con.prepareStatement("USE happeekidz");
            pst.executeUpdate();
            pst = con.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS happeekidz.products (ID_PRODUCTS INT NOT NULL AUTO_INCREMENT, PRODUCT_NAME VARCHAR(255) NOT NULL, PRODUCT_DESCRIPTION VARCHAR(255) NOT NULL, PRODUCT_CATEGORY VARCHAR(255) NOT NULL, PRODUCT_RATE FLOAT NOT NULL, PRODUCT_SKU VARCHAR(255) NOT NULL, PRODUCT_IS_TAXABLE BOOLEAN NOT NULL, PRIMARY KEY (ID_PRODUCTS))");
            pst.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setProductToAdd(String name, String description, String category, float price, String SKU,
            boolean is_taxable) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.price = price;
        this.SKU = SKU;
        this.is_taxable = is_taxable;
        addProductToDatabase();
    }

    public void setProductToRemove(int id) {
        this.id = id;
        removeProduct();
    }

    public void setProductToUpdate(int id, String name, String description, String category, float price, String SKU,
            boolean is_taxable) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.category = category;
        this.price = price;
        this.SKU = SKU;
        this.is_taxable = is_taxable;
        updateProduct();
    }

    private void addProductToDatabase() {
        try {
            pst = con.prepareStatement(
                    "INSERT INTO products (PRODUCT_NAME, PRODUCT_DESCRIPTION, PRODUCT_CATEGORY, PRODUCT_RATE, PRODUCT_SKU, PRODUCT_IS_TAXABLE) VALUES (?, ?, ?, ?, ?, ?)");
            pst.setString(1, name);
            pst.setString(2, description);
            pst.setString(3, category);
            pst.setFloat(4, price);
            pst.setString(5, SKU);
            pst.setBoolean(6, is_taxable);
            pst.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Close resources in a finally block
            try {
                if (pst != null)
                    pst.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateProduct() {
        try {
            pst = con.prepareStatement(
                    "UPDATE products SET PRODUCT_NAME = ?, PRODUCT_DESCRIPTION = ?, PRODUCT_CATEGORY = ?, PRODUCT_RATE = ?, PRODUCT_SKU = ?, PRODUCT_IS_TAXABLE = ? WHERE ID_PRODUCTS = ?");
            pst.setString(1, name);
            pst.setString(2, description);
            pst.setString(3, category);
            pst.setFloat(4, price);
            pst.setString(5, SKU);
            pst.setBoolean(6, is_taxable);
            pst.setInt(7, id);
            pst.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void removeProduct() {
        try {
            pst = con.prepareStatement("DELETE FROM products WHERE ID_PRODUCTS = ?");
            pst.setInt(1, id);
            pst.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Close resources in a finally block
            try {
                if (pst != null)
                    pst.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public Object[][] getProducts() {
        List<Object[]> products = new ArrayList<>();
        try {
            pst = con.prepareStatement("SELECT * FROM happeekidz.products");
            rs = pst.executeQuery();
            while (rs.next()) {
                products.add(new Object[] {
                        rs.getInt("ID_PRODUCTS"),
                        rs.getString("PRODUCT_NAME"),
                        rs.getString("PRODUCT_DESCRIPTION"),
                        rs.getString("PRODUCT_CATEGORY"),
                        rs.getFloat("PRODUCT_RATE"),
                        rs.getString("PRODUCT_SKU"),
                        rs.getBoolean("PRODUCT_IS_TAXABLE")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products.toArray(new Object[0][]);
    }
}
