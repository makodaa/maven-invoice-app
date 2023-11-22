package happeekidz.Models.App;

import java.sql.*;
import java.util.*;
import java.time.*;

public class Customers {
    Connection con;
    PreparedStatement pst;
    ResultSet rs;

    // id, name, address, email, phone, contract start date, contract end date,
    private int id;
    private String firstName;
    private String lastName;
    private String middleName;
    private String address;
    private String email;
    private String phone;
    private LocalDate contractStartDate;
    private LocalDate contractEndDate;
    private String[] invoices;

    public Customers() {
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
                    "CREATE TABLE IF NOT EXISTS customers (" +
                            "id INT NOT NULL AUTO_INCREMENT," +
                            "firstName VARCHAR(255) NOT NULL," +
                            "lastName VARCHAR(255) NOT NULL," +
                            "middleName VARCHAR(255) NOT NULL," +
                            "address VARCHAR(255) NOT NULL," +
                            "email VARCHAR(255) NOT NULL," +
                            "phone VARCHAR(255) NOT NULL," +
                            "contractStartDate DATE NOT NULL," +
                            "contractEndDate DATE NOT NULL," +
                            "PRIMARY KEY (id)" +
                            ")");
            pst.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void setCustomerToAdd(String firstName, String lastName, String middleName, String address, String email, String phone, LocalDate contractStartDate, LocalDate contractEndDate){
        this.firstName = firstName;
        this.lastName = lastName;
        this.middleName = middleName;
        this.address = address;
        this.email = email;
        this.phone = phone;
        this.contractStartDate = contractStartDate;
        this.contractEndDate = contractEndDate;
        addCustomer();
    }

    public void setCustomerToRemove(int id){
        this.id = id;
        removeCustomer();
    }

    public void setCustomerToUpdate(int id, String firstName, String lastName, String middleName, String address, String email, String phone, LocalDate contractStartDate, LocalDate contractEndDate){
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.middleName = middleName;
        this.address = address;
        this.email = email;
        this.phone = phone;
        this.contractStartDate = contractStartDate;
        this.contractEndDate = contractEndDate;
        updateCustomer();
    }

    private void addCustomer() {
        try {
            pst = con.prepareStatement(
                    "INSERT INTO customers (firstName, lastName, middleName, address, email, phone, contractStartDate, contractEndDate) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
            pst.setString(1, firstName);
            pst.setString(2, lastName);
            pst.setString(3, middleName);
            pst.setString(4, address);
            pst.setString(5, email);
            pst.setString(6, phone);
            pst.setDate(7, java.sql.Date.valueOf(contractStartDate));
            pst.setDate(8, java.sql.Date.valueOf(contractEndDate));
            pst.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void removeCustomer() {
        try {
            pst = con.prepareStatement("DELETE FROM customers WHERE id = ?");
            pst.setInt(1, id);
            pst.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateCustomer(){
        try {
            pst = con.prepareStatement("UPDATE customers SET firstName = ?, lastName = ?, middleName = ?, address = ?, email = ?, phone = ?, contractStartDate = ?, contractEndDate = ? WHERE id = ?");
            pst.setString(1, firstName);
            pst.setString(2, lastName);
            pst.setString(3, middleName);
            pst.setString(4, address);
            pst.setString(5, email);
            pst.setString(6, phone);
            pst.setDate(7, java.sql.Date.valueOf(contractStartDate));
            pst.setDate(8, java.sql.Date.valueOf(contractEndDate));
            pst.setInt(9, id);
            pst.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Object[][] getCustomers() {
        List<Object[]> customers = null;
        try {
            pst = con.prepareStatement("SELECT * FROM customers");
            rs = pst.executeQuery();
            customers = new ArrayList<>();
            while (rs.next()) {
                customers.add(new Object[]{
                        rs.getInt("id"), // idx: 0
                        rs.getString("firstName"), // idx: 1
                        rs.getString("lastName"), // idx: 2
                        rs.getString("middleName"), // idx: 3
                        rs.getString("address"), // idx: 4
                        rs.getString("email"), // idx: 5
                        rs.getString("phone"), // idx: 6
                        rs.getDate("contractStartDate"), // idx: 7
                        rs.getDate("contractEndDate") // idx: 8
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return customers.toArray(new Object[0][]);
    }
}
