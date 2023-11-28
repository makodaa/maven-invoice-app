package happeekidz.Controllers.App;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;

import happeekidz.Models.App.Customers;

public class GeneratePdf implements Printable {
    private int invoice_number;
    private String customer;
    private LocalDate invoice_date;
    private LocalDate due_date;
    private LocalDate date_paid;
    private ArrayList<String[]> products;
    private double subtotal;
    private double tax;
    private double discount;
    private String discount_type;
    private double balance;
    private Customers customers = new Customers();

    public GeneratePdf() {
        // Default constructor
    }

    public GeneratePdf(int invoice_number, String customer, LocalDate invoice_date, LocalDate due_date,
            LocalDate date_paid, ArrayList<String[]> products, double subtotal, double tax, double discount,
            String discount_type, double total) throws FileNotFoundException, IOException {
        this.invoice_number = invoice_number;
        this.customer = customer;
        this.invoice_date = invoice_date;
        this.due_date = due_date;
        this.date_paid = date_paid;
        this.products = products;
        this.subtotal = subtotal;
        this.tax = tax;
        this.discount = discount;
        this.discount_type = discount_type;
        this.balance = total;
    }

    public void printRecord(GeneratePdf pdf) {
        PrinterJob pr = PrinterJob.getPrinterJob();

        pr.setJobName("Print Record");

        pr.setPrintable((Printable) pdf);

        boolean returnResult = pr.printDialog();

        if (returnResult) {
            try {
                pr.print();
            } catch (PrinterException printerException) {
                printerException.printStackTrace();
            }
        }
    }
    
    @Override
    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
    	String selectedCustomerName = "";
    	
        if (pageIndex > 0) {
            return Printable.NO_SUCH_PAGE;
        }

        //iterate through the customers to get the name of the customer from the customer id
        for (int i = 0; i < customers.getCustomers().length; i++) {
            if (customers.getCustomers()[i][0] != null) {
                if (customers.getCustomers()[i][0].toString().equals(customer)) {
                    System.out.println(customers.getCustomers()[i][0] + " " + customers.getCustomers()[i][1] + " " + customers.getCustomers()[i][2]);
                    selectedCustomerName = (String) customers.getCustomers()[i][1] + " " + (String) customers.getCustomers()[i][2];
                }
            }
        }
        

        Graphics2D graphics2D = (Graphics2D) graphics;
        graphics2D.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
        // Customize the rendering of your content here
        graphics2D.setFont(graphics2D.getFont().deriveFont(20f));
        graphics2D.drawString("Carrel's Real Property Leasing", 50, 50);
        graphics2D.setFont(graphics2D.getFont().deriveFont(12f));
        graphics2D.drawString("115 Tandang Sora Ave, Tandang Sora, Quezon City, 1116 Metro Manila", 50, 90);
        graphics2D.drawString("Contact Number: 09178356396", 50, 120);
        graphics2D.setFont(graphics2D.getFont().deriveFont(20f));
        graphics2D.setColor(java.awt.Color.BLUE);
        graphics2D.drawString("Invoice", 50, 160);
        graphics2D.setFont(graphics2D.getFont().deriveFont(12f));
        graphics2D.setColor(java.awt.Color.BLACK);
        graphics2D.drawString("Invoice To:", 50, 190);
        graphics2D.drawString("Customer: " + selectedCustomerName, 50, 210);
        graphics2D.drawString("Invoice Number: " + invoice_number, 50, 240);
        graphics2D.drawString("Invoice Date: " + invoice_date, 50, 270);
        graphics2D.drawString("Due Date: " + due_date, 50, 300);

        // Starting Y-coordinate for product details
        int currentY = 350; 
       
        int xPosition = 70; // 

        String[] headers = {"Product", "Description", "QTY", "Rate", "Amount"};
        // Calculate column widths dynamically
        int[] columnWidths = new int[products.get(0).length - 1];

        for (int i = 0; i < headers.length; i++) {
            // Adjust the width of the column based on the dynamically calculated columnWidths
            int adjustedWidth = Math.max(20, columnWidths[i]);
            graphics2D.drawString(String.format("%-" + adjustedWidth + "s", headers[i]), xPosition, currentY);
            xPosition += adjustedWidth + 70; // Add some padding between columns
        }

        // Draw horizontal line between headers and data
        int lineHeight = graphics2D.getFontMetrics().getHeight();
        int lineEndX = Math.min(50 + xPosition, (int) pageFormat.getWidth() - 50);
        graphics2D.drawLine(50, currentY + lineHeight, lineEndX, currentY + lineHeight);
        currentY += 2 * lineHeight; // Increase the vertical space between headers and data

        // Iterate through the products
        for (String[] product : products) {
            if (product != null && product.length >= 4) {
                xPosition = -20; // Reset X-coordinate for each row

                for (int i = 0; i < product.length - 1; i++) {
                    if (product[i] != null) {
                        // Adjust the width of the column based on the dynamically calculated columnWidths
                        int adjustedWidth = Math.max(20, columnWidths[i]);
                        graphics2D.drawString(String.format("%-" + adjustedWidth + "s", product[i]), xPosition, currentY);
                        xPosition += adjustedWidth + 70; // Add some padding between columns
                    }
                }

                // Draw horizontal line between rows
                lineEndX = Math.min(50 + xPosition, (int) pageFormat.getWidth() - 50);
                graphics2D.drawLine(50, currentY + lineHeight, lineEndX, currentY + lineHeight);
                currentY += 2 * lineHeight; // Increase the vertical space between rows
            }
        }

        // Print other details (subtotal, tax, discount, etc.) below products
        graphics2D.drawString("Subtotal: " + subtotal, 400, 50 + currentY);
        currentY += lineHeight;
        graphics2D.drawString("Tax: " + tax, 400, 50 + currentY);
        currentY += lineHeight;
        graphics2D.drawString("Discount: " + discount, 400, 50 + currentY);
        currentY += lineHeight;
        graphics2D.drawString("Discount Type: " + discount_type, 400, 50 + currentY);
        currentY += lineHeight;

        // Print the balance
        graphics2D.drawString("Balance Due: " + balance, 400, 50 + currentY);

        return Printable.PAGE_EXISTS;
    }

}