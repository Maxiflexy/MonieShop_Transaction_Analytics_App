# MonieShop_Transaction_Analytics_App

## Overview
The MonieShop Transactions Analytics App is a command-line application designed
to analyze daily transaction data and provide key business insights. It processes transaction records from multiple text files, calculates various sales metrics, and displays analytics such as:

- **Highest sales volume in a day** (total number of products sold on the busiest day). 
- **Highest sales value in a day** (day with the highest total revenue).
- **Most sold product ID by volume** (the product sold in the highest quantity).
- **Highest sales staff ID for each month** (staff member with the most transactions in a given month).
- **Highest hour of the day by average transaction volume** (hour with the most sales activity on average).

The application processes transaction data from sample text files stored in the src/main/resources directory. It utilizes multi-threading to improve performance by parallelizing file processing so as to reduce time taken to process the entire files.


## Getting Started
### Prerequisites
Ensure you have the following installed:

- Java 17+
- Maven (for dependency management)
- Git (to clone the repository)


## Build and Run the Application
### Compile the project using Maven

- mvn clean install
- Run the application => java -jar target/dream-dev-1.0.jar

### Alternatively, 
if running from an IDE, execute the main method inside MonieShopTransactionsAnalyticsApp.java.


## How It Works
- The app scans the src/main/resources folder for available test case directories.
- The user selects a test case (folder) containing daily transaction files.
- The app processes each file, reading transaction records asynchronously.
- It computes key business metrics and prints them to the console.
- It then asks the user if they wish to continue using the application, which user can respond with a y/n

