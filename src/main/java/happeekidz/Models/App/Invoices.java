package happeekidz.Models.App;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Invoices {
    Connection con;
    PreparedStatement pst;
    ResultSet rs;

    private int invoice_number;
    private int customer_id;
    private Date invoice_date;
    private Date due_date;
    private Date payment_date;
    private String products;
    private double subtotal;
    private double tax;
    private double discount;
    private String discount_type;
    private double total;
    private String status;
    private String payment_method;
    private String message;

    public Invoices() {
        init();
    }

    public Invoices(int invoice_number) {
        this.invoice_number = invoice_number;
        init();
    }

    public Invoices(int customer_id, LocalDate invoice_date, LocalDate due_date, ArrayList<String[]> products,
            double subtotal, double tax, double discount, String discount_type, double total, String message) {
        this.customer_id = customer_id;
        this.invoice_date = Date.valueOf(invoice_date);
        this.due_date = Date.valueOf(due_date);
        this.payment_date = null;
        this.subtotal = subtotal;
        this.tax = tax;
        this.discount = discount;
        this.discount_type = discount_type;
        this.total = total;
        this.status = "Unpaid";
        this.payment_method = null;
        this.message = message;
        StringBuilder sb = new StringBuilder();
        for (String[] product : products) {
            sb.append(String.join(",", product)).append(";");
        }
        this.products = sb.toString();
        init();
        init();
    }

    public Invoices(int invoice_number, int customer_id, LocalDate invoice_date, LocalDate due_date,
            LocalDate payment_date, ArrayList<String[]> products, double subtotal, double tax, double discount,
            String discount_type, double total, String status, String payment_method, String message) {
        this.invoice_number = invoice_number;
        this.customer_id = customer_id;
        this.invoice_date = Date.valueOf(invoice_date);
        this.due_date = Date.valueOf(due_date);
        this.payment_date = Date.valueOf(payment_date);
        this.subtotal = subtotal;
        this.tax = tax;
        this.discount = discount;
        this.discount_type = discount_type;
        this.total = total;
        this.status = status;
        this.payment_method = payment_method;
        this.message = message;
        StringBuilder sb = new StringBuilder();
        for (String[] product : products) {
            sb.append(String.join(",", product)).append(";");
        }
        this.products = sb.toString();
        init();
    }


    public int getInvoice_number() {
        return invoice_number;
    }

    private void init() {
        try {
            con = DriverManager.getConnection(
                    "jdbc:mysql://127.0.0.1:3306",
                    "root",
                    "MkbcMySQL2023-");
            // TODO: remove soon
            pst = con.prepareStatement("CREATE DATABASE IF NOT EXISTS happeekidz");
            pst.executeUpdate();
            pst = con.prepareStatement("USE happeekidz");
            pst.executeUpdate();
            pst = con.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS invoices (INVOICE_NUMBER INT NOT NULL AUTO_INCREMENT, CUSTOMER_ID INT NOT NULL, INVOICE_DATE DATE NOT NULL, DUE_DATE DATE NOT NULL, PAYMENT_DATE DATE, PRODUCTS TEXT NOT NULL, SUBTOTAL DOUBLE NOT NULL, TAX DOUBLE NOT NULL, DISCOUNT DOUBLE NOT NULL, DISCOUNT_TYPE VARCHAR(255) NOT NULL, TOTAL DOUBLE NOT NULL, STATUS VARCHAR(255), PAYMENT_METHOD VARCHAR(255), MESSAGE TEXT, PRIMARY KEY (INVOICE_NUMBER))");
            pst.executeUpdate();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void addInvoice() {
        try {
            pst = con.prepareStatement(
                    "INSERT INTO invoices (CUSTOMER_ID, INVOICE_DATE, DUE_DATE, PAYMENT_DATE, PRODUCTS, SUBTOTAL, TAX, DISCOUNT, DISCOUNT_TYPE, TOTAL, STATUS, PAYMENT_METHOD, MESSAGE) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?)");

            pst.setInt(1, customer_id);
            pst.setDate(2, invoice_date);
            pst.setDate(3, due_date);
            pst.setDate(4, payment_date);
            pst.setString(5, products);
            pst.setDouble(6, subtotal);
            pst.setDouble(7, tax);
            pst.setDouble(8, discount);
            pst.setString(9, discount_type);
            pst.setDouble(10, total);
            pst.setString(11, status);
            pst.setString(12, payment_method);
            pst.setString(13, message);
            pst.executeUpdate();

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void removeInvoice() {
        try {
            pst = con.prepareStatement("DELETE FROM invoices WHERE INVOICE_NUMBER = ?");
            pst.setInt(1, invoice_number);
            pst.executeUpdate();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void updateInvoice() {
        try {
            pst = con.prepareStatement(
                    "UPDATE invoices SET CUSTOMER_ID = ?, INVOICE_DATE = ?, DUE_DATE = ?, PAYMENT_DATE = ?, PRODUCTS = ?, SUBTOTAL = ?, TAX = ?, DISCOUNT = ?, DISCOUNT_TYPE = ?, TOTAL = ?, STATUS = ?, PAYMENT_METHOD = ?, MESSAGE = ? WHERE INVOICE_NUMBER = ?");
            pst.setInt(1, customer_id);
            pst.setDate(2, invoice_date);
            pst.setDate(3, due_date);
            pst.setDate(4, payment_date);
            pst.setString(5, products);
            pst.setDouble(6, subtotal);
            pst.setDouble(7, tax);
            pst.setDouble(8, discount);
            pst.setString(9, discount_type);
            pst.setDouble(10, total);
            pst.setString(11, status);
            pst.setString(12, payment_method);
            pst.setString(13, message);
            pst.setInt(14, invoice_number);
            pst.executeUpdate();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    
    public int getNumberOfInvoice() {
        int numInvoice = 0;
        try {
            pst = con.prepareStatement("SELECT COUNT(*) FROM invoices");
            rs = pst.executeQuery();
            if (rs.next()) {
                numInvoice = rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return numInvoice;
    }

         public int getTotalInvoiceIssued() {
        int numInvoice = 0;
        try {
            pst = con.prepareStatement("SELECT SUM(TOTAL) FROM invoices");
            rs = pst.executeQuery();
            if (rs.next()) {
                numInvoice = rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return numInvoice;
    }

    /*
     * 0: invoice_number
     * 1: customer_id
     * 2: invoice_date
     * 3: due_date
     * 4: payment_date
     * 5: products
     * 6: subtotal
     * 7: tax
     * 8: discount
     * 9: discount_type
     * 10: total
     * 11: status
     * 12: payment_method
     * 13: message
     */
    public Object[][] getInvoices() {
        List<Object[]> invoices = new ArrayList<Object[]>();
        try {
            pst = con.prepareStatement("SELECT * FROM invoices");
            rs = pst.executeQuery();
            while (rs.next()) {
                Object[] invoice = new Object[13];
                invoice[0] = rs.getInt(1);
                invoice[1] = rs.getInt(2);
                invoice[2] = rs.getDate(3);
                invoice[3] = rs.getDate(4);
                invoice[4] = rs.getDate(5);
                invoice[5] = rs.getString(6);
                invoice[6] = rs.getDouble(7);
                invoice[7] = rs.getDouble(8);
                invoice[8] = rs.getDouble(9);
                invoice[9] = rs.getString(10);
                invoice[10] = rs.getDouble(11);
                invoice[11] = rs.getString(12);
                invoice[12] = rs.getString(13);
                invoices.add(invoice);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return invoices.toArray(new Object[invoices.size()][]);
    }

}
