package com.example.retail.config;

import com.example.retail.model.Customer;
import com.example.retail.model.CustomerOrder;
import com.example.retail.model.Offer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads test data from CSV files located in src/main/resources/data/
 * and returns typed model objects for use by RetailService.
 *
 * CSV files:
 *   data/customers.csv  — id, name, email, loyaltyPoints
 *   data/orders.csv     — id, customerId, orderNumber, totalAmount, status
 *   data/offers.csv     — code, discountPercent, active
 */
@Component
public class TestDataLoader {

    private static final Logger log = LoggerFactory.getLogger(TestDataLoader.class);

    private static final String CUSTOMERS_CSV = "data/customers.csv";
    private static final String ORDERS_CSV    = "data/orders.csv";
    private static final String OFFERS_CSV    = "data/offers.csv";

    // -------------------------------------------------------------------------
    // Public load methods
    // -------------------------------------------------------------------------

    public List<Customer> loadCustomers() {
        List<Customer> result = new ArrayList<>();

        for (String[] row : readCsv(CUSTOMERS_CSV)) {
            try {
                Long      id            = Long.parseLong(row[0].trim());
                String    name          = row[1].trim();
                String    email         = row[2].trim();
                int       loyaltyPoints = Integer.parseInt(row[3].trim());
                boolean   active        = Boolean.parseBoolean(row[4].trim());
                LocalDate joinedDate    = LocalDate.parse(row[5].trim());

                result.add(new Customer(id, name, email, loyaltyPoints, active, joinedDate));
            } catch (Exception e) {
                log.warn("Skipping invalid customer row {}: {}", row, e.getMessage());
            }
        }

        log.info("Loaded {} customer(s) from {}", result.size(), CUSTOMERS_CSV);
        return result;
    }

    public List<CustomerOrder> loadOrders() {
        List<CustomerOrder> result = new ArrayList<>();

        for (String[] row : readCsv(ORDERS_CSV)) {
            try {
                Long       id          = Long.parseLong(row[0].trim());
                Long       customerId  = Long.parseLong(row[1].trim());
                String     orderNumber = row[2].trim();
                BigDecimal totalAmount = new BigDecimal(row[3].trim());
                String     status      = row[4].trim();

                result.add(new CustomerOrder(id, customerId, orderNumber, totalAmount, status));
            } catch (Exception e) {
                log.warn("Skipping invalid order row {}: {}", row, e.getMessage());
            }
        }

        log.info("Loaded {} order(s) from {}", result.size(), ORDERS_CSV);
        return result;
    }

    public List<Offer> loadOffers() {
        List<Offer> result = new ArrayList<>();

        for (String[] row : readCsv(OFFERS_CSV)) {
            try {
                String  code            = row[0].trim();
                int     discountPercent = Integer.parseInt(row[1].trim());
                boolean active          = Boolean.parseBoolean(row[2].trim());

                result.add(new Offer(code, discountPercent, active));
            } catch (Exception e) {
                log.warn("Skipping invalid offer row {}: {}", row, e.getMessage());
            }
        }

        log.info("Loaded {} offer(s) from {}", result.size(), OFFERS_CSV);
        return result;
    }

    // -------------------------------------------------------------------------
    // CSV reader — skips the header row and blank lines
    // -------------------------------------------------------------------------

    private List<String[]> readCsv(String classpathPath) {
        List<String[]> rows = new ArrayList<>();

        InputStream stream = getClass().getClassLoader().getResourceAsStream(classpathPath);
        if (stream == null) {
            log.error("CSV file not found on classpath: {}", classpathPath);
            return rows;
        }

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(stream, StandardCharsets.UTF_8))) {

            String line;
            boolean headerSkipped = false;

            while ((line = reader.readLine()) != null) {
                line = line.trim();

                if (line.isEmpty()) {
                    continue;
                }

                if (!headerSkipped) {
                    headerSkipped = true;   // first non-blank line is the header
                    continue;
                }

                rows.add(line.split(",", -1));
            }

        } catch (Exception e) {
            log.error("Failed to read CSV file {}: {}", classpathPath, e.getMessage());
        }

        return rows;
    }
}
