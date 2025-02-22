package monieshop.analytics.model;

import java.time.LocalDateTime;
import java.util.Map;

public class TransactionRecord {

    private int salesStaffId;
    private LocalDateTime transactionTime;
    private Map<Integer, Integer> productsSold; // productID -> quantity
    private double saleAmount;

    public TransactionRecord(int salesStaffId, LocalDateTime transactionTime, Map<Integer, Integer> productsSold, double saleAmount) {
        this.salesStaffId = salesStaffId;
        this.transactionTime = transactionTime;
        this.productsSold = productsSold;
        this.saleAmount = saleAmount;
    }

    public Map<Integer, Integer> getProductsSold() {
        return productsSold;
    }

    public void setProductsSold(Map<Integer, Integer> productsSold) {
        this.productsSold = productsSold;
    }

    public double getSaleAmount() {
        return saleAmount;
    }

    public void setSaleAmount(double saleAmount) {
        this.saleAmount = saleAmount;
    }

    public int getSalesStaffId() {
        return salesStaffId;
    }

    public void setSalesStaffId(int salesStaffId) {
        this.salesStaffId = salesStaffId;
    }

    public LocalDateTime getTransactionTime() {
        return transactionTime;
    }

    public void setTransactionTime(LocalDateTime transactionTime) {
        this.transactionTime = transactionTime;
    }
}
