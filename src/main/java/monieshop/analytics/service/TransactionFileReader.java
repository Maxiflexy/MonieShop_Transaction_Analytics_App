package monieshop.analytics.service;

import monieshop.analytics.model.TransactionRecord;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TransactionFileReader {

    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    public static List<TransactionRecord> readTransactionsFromFile(Path filePath) throws IOException {
        List<TransactionRecord> records = new ArrayList<>();
        List<String> lines = Files.readAllLines(filePath);

        for (String line : lines) {
            records.add(parseTransaction(line));
        }
        return records;
    }

    private static TransactionRecord parseTransaction(String line) {
        String[] parts = line.split(",");
        int staffId = Integer.parseInt(parts[0].trim());

        String transactionTimeStr = parts[1].trim();
        if (transactionTimeStr.length() == 16) {
            transactionTimeStr += ":00";
        }
        LocalDateTime transactionTime = LocalDateTime.parse(transactionTimeStr, DATE_TIME_FORMAT);

        Map<Integer, Integer> products = parseProducts(parts[2]);
        double saleAmount = Double.parseDouble(parts[3]);

        return new TransactionRecord(staffId, transactionTime, products, saleAmount);
    }

    private static Map<Integer, Integer> parseProducts(String productData) {
        Map<Integer, Integer> products = new HashMap<>();
        String[] items = productData.replace("[", "").replace("]", "").split("\\|");
        for (String item : items) {
            String[] itemParts = item.split(":");
            products.put(Integer.parseInt(itemParts[0]), Integer.parseInt(itemParts[1]));
        }
        return products;
    }
}
