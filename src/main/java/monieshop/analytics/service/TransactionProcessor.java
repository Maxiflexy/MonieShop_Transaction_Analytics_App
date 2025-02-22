package monieshop.analytics.service;

import monieshop.analytics.model.TransactionRecord;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class TransactionProcessor {

    private final Path folderPath;
    private final ExecutorService executor;

    public TransactionProcessor(String folderPath, ExecutorService executor) {
        this.folderPath = Paths.get(folderPath);
        this.executor = executor;
    }

    public void analyzeTransactions() throws IOException {

        List<Path> files = Files.list(folderPath)
                .filter(Files::isRegularFile)
                .filter(path -> path.toString().endsWith(".txt"))
                .toList();

        List<Future<List<TransactionRecord>>> futures = new ArrayList<>();
        for (Path file : files) {
            futures.add(executor.submit(() -> TransactionFileReader.readTransactionsFromFile(file)));
        }

        // Collect results
        List<TransactionRecord> allTransactions = new ArrayList<>();
        for (Future<List<TransactionRecord>> future : futures) {
            try {
                allTransactions.addAll(future.get());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        computeMetrics(allTransactions);
    }

    private void computeMetrics(List<TransactionRecord> transactions) {
        Map<LocalDate, Integer> dailySalesVolume = new HashMap<>();
        Map<LocalDate, Double> dailySalesValue = new HashMap<>();
        Map<Integer, Integer> productSales = new HashMap<>();
        Map<Integer, Map<Integer, Integer>> monthlyStaffSales = new HashMap<>();
        Map<Integer, List<Integer>> hourlyTransactionVolumes = new HashMap<>();


        List<String> monthNames = Arrays.asList(
                "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"
        );

        for (TransactionRecord record : transactions) {
            LocalDate date = record.getTransactionTime().toLocalDate();
            dailySalesVolume.merge(date, record.getProductsSold().values().stream().mapToInt(Integer::intValue).sum(), Integer::sum);
            dailySalesValue.merge(date, record.getSaleAmount(), Double::sum);

            record.getProductsSold().forEach((productId, quantity) ->
                    productSales.merge(productId, quantity, Integer::sum));

            int month = record.getTransactionTime().getMonthValue();
            int staffId = record.getSalesStaffId();

            monthlyStaffSales.putIfAbsent(month, new HashMap<>());
            monthlyStaffSales.get(month).merge(staffId, 1, Integer::sum);

            int hour = record.getTransactionTime().getHour();
            hourlyTransactionVolumes.putIfAbsent(hour, new ArrayList<>());
            hourlyTransactionVolumes.get(hour).add(record.getProductsSold().values().stream()
                    .mapToInt(Integer::intValue).sum());
        }


        System.out.println("\n==============================================");
        System.out.println("                 RESULT SUMMARY               ");
        System.out.println("==============================================\n");

        System.out.println("1.Highest sales volume in a day: " + Collections.max(dailySalesVolume.values()));
        System.out.println("\n2.Highest Sales Value in a Day: " + String.format("%.2f", Collections.max(dailySalesValue.values())));
        System.out.println("\n3.Most sold product ID: " + productSales.entrySet().stream()
                .max(Map.Entry.comparingByValue()).get().getKey());

        System.out.println("\n4.Highest sales staff ID per month:");
        for (int month = 1; month <= 12; month++) {
            String monthName = monthNames.get(month - 1);

            if (monthlyStaffSales.containsKey(month)) {
                Map<Integer, Integer> staffSales = monthlyStaffSales.get(month);
                int topStaff = staffSales.entrySet().stream()
                        .max(Map.Entry.comparingByValue())
                        .get()
                        .getKey();
                System.out.printf("    %s: Id %d%n", monthName, topStaff);
            } else {
                System.out.printf("    %s: No Data%n", monthName);
            }
        }

        int highestHour = -1;
        double highestAvgVolume = 0;

        for (Map.Entry<Integer, List<Integer>> entry : hourlyTransactionVolumes.entrySet()) {
            int hour = entry.getKey();
            List<Integer> volumes = entry.getValue();
            double avgVolume = volumes.stream().mapToInt(Integer::intValue).average().orElse(0);

            if (avgVolume > highestAvgVolume) {
                highestAvgVolume = avgVolume;
                highestHour = hour;
            }
        }

        System.out.println("\n5.Highest Hour of the Day by Average Transaction Volume:");
        System.out.printf("    %02d:00 with avg volume of %.5f%n", highestHour, highestAvgVolume);
        System.out.println("\n==============================================\n");
    }
}
