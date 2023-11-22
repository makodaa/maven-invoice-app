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
}
