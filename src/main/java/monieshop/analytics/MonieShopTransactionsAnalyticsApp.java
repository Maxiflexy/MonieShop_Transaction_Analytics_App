package monieshop.analytics;

import monieshop.analytics.service.TransactionProcessor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MonieShopTransactionsAnalyticsApp {

    private static final Path RESOURCE_PATH = Paths.get("src/main/resources");
    private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(10);
    private static final Scanner SCANNER = new Scanner(System.in);

    public static void main(String[] args) {
        printWelcomeMessage();

        while (true) {
            try {
                List<String> testCases = getAvailableTestCases();
                if (testCases.isEmpty()) {
                    System.out.println("No test cases found in resources.");
                    break;
                }

                int choice = getUserChoice(testCases);
                if (choice == -1) continue;

                processSelectedTestCase(testCases.get(choice - 1));

                if (!shouldContinue()) {
                    exitApplication();
                    break;
                }
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
            }
        }
    }

    private static void printWelcomeMessage() {
        System.out.println("=============================================================");
        System.out.println("          WELCOME TO MONIESHOP TRANSACTIONS ANALYTICS APP    ");
        System.out.println("=============================================================\n");
    }

    private static List<String> getAvailableTestCases() throws Exception {
        return Files.list(RESOURCE_PATH)
                .filter(Files::isDirectory)
                .map(path -> path.getFileName().toString())
                .toList();
    }

    private static int getUserChoice(List<String> testCases) {
        System.out.println("Available Test Cases:");
        for (int i = 0; i < testCases.size(); i++) {
            System.out.println((i + 1) + ". " + testCases.get(i));
        }

        System.out.print("\nEnter test case number: ");
        if (!SCANNER.hasNextInt()) {
            SCANNER.next();
            System.out.println("\nInvalid input. Please enter a valid number.\n");
            return -1;
        }

        int choice = SCANNER.nextInt();
        SCANNER.nextLine();

        if (choice < 1 || choice > testCases.size()) {
            System.out.println("\nInvalid choice, Please try again.\n");
            return -1;
        }

        return choice;
    }

    private static void processSelectedTestCase(String selectedFolder) throws IOException {
        System.out.println("\n-------------------------------------------------------------");
        System.out.println("                 PROCESSING: " + selectedFolder);
        System.out.println("-------------------------------------------------------------\n");

        TransactionProcessor processor = new TransactionProcessor(RESOURCE_PATH.resolve(selectedFolder).toString(), EXECUTOR);
        processor.analyzeTransactions();
    }

    private static boolean shouldContinue() {
        System.out.print("\n-------------------------------------------------------------\n");
        System.out.print("Do you wish to continue using the application? (Y/N): ");
        String response = SCANNER.nextLine().trim().toUpperCase();
        return response.equals("Y");
    }

    private static void exitApplication() {
        System.out.println("\nExiting application...\n");
        EXECUTOR.shutdown();
        SCANNER.close();
    }

}
