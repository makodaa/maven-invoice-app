package happeekidz.Controllers.App;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;

public class GeneratePdf {
    public GeneratePdf() {
    }
    public GeneratePdf(int invoice_number, String customer, LocalDate invoice_date, LocalDate due_date,
            LocalDate date_paid, ArrayList<String[]> products, double subtotal, double tax, double discount,
            String discount_type, double total) throws FileNotFoundException, IOException {
    }
}
