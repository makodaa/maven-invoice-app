package happeekidz.Models.App;

import java.sql.*;
import java.time.LocalDate;
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

    // constructor with no invoice number, status, payment method
    public Invoices(int customer_id, LocalDate invoice_date, LocalDate due_date, List<String[]> products, String message) {
        this.customer_id = customer_id;
        this.invoice_date = Date.valueOf(invoice_date);
        this.due_date = Date.valueOf(due_date);
        this.payment_date = null;
        this.status = "unpaid";
        this.payment_method = null;
        this.message = message;


        StringBuilder sb = new StringBuilder();
        for(String[] product : products ){
            sb.append(String.join(",", product)).append(";");
        }
        this.products = sb.toString();
        init();
    }

    public Invoices(int invoice_number, int customer_id, LocalDate invoice_date, LocalDate due_date,
            LocalDate payment_date, List<String[]> products, String status, String payment_method, String message) {
        this.invoice_number = invoice_number;
        this.customer_id = customer_id;
        this.invoice_date = Date.valueOf(invoice_date);
        this.due_date = Date.valueOf(due_date);
        this.payment_date = Date.valueOf(payment_date) == null ? null : Date.valueOf(payment_date);
        this.status = status;
        this.payment_method = payment_method;
        this.message = message;

        StringBuilder sb = new StringBuilder();
        for(String[] product : products ){
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
            //TODO: remove soon
            pst = con.prepareStatement("CREATE DATABASE IF NOT EXISTS happeekidz");
            pst.executeUpdate();
            pst = con.prepareStatement("USE happeekidz");
            pst.executeUpdate();
            pst = con.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS happeekidz.invoices (INVOICE_NUMBER INT NOT NULL AUTO_INCREMENT, CUSTOMER_ID INT NOT NULL, INVOICE_DATE DATE NOT NULL, DUE_DATE DATE NOT NULL, PAYMENT_DATE DATE, PRODUCTS TEXT NOT NULL, STATUS VARCHAR(255) NOT NULL, PAYMENT_METHOD VARCHAR(255), MESSAGE VARCHAR(255) NOT NULL, PRIMARY KEY (INVOICE_NUMBER))");
            pst.executeUpdate();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    
    public void addInvoice() {
        try {
            pst = con.prepareStatement(
                    "INSERT INTO happeekidz.invoices (CUSTOMER_ID, INVOICE_DATE, DUE_DATE, PAYMENT_DATE, PRODUCTS, STATUS, PAYMENT_METHOD, MESSAGE) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
            pst.setInt(1, customer_id);
            pst.setDate(2, invoice_date);
            pst.setDate(3, due_date);
            pst.setDate(4, payment_date);
            pst.setString(5, products);
            pst.setString(6, status);
            pst.setString(7, payment_method);
            pst.setString(8, message);
            pst.executeUpdate();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

}
