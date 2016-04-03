package com.kasoverskiy.ovchipkaart.model;

import javax.annotation.Nullable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Вадим on 03.04.2016.
 */
public class Transaction {
    private Date checkIn;
    private String departure;
    private Date checkOut;


    private String destination;
    private double amount;
    private String transaction;
    private String classTrans;
    private String product;
    private String comments;

    public Transaction(Date checkIn, String departure,
                       Date checkOut, String destination,
                       double amount, String transaction,
                       @Nullable String classTrans, @Nullable String comments, @Nullable String product) {
        this.checkIn = checkIn;
        this.departure = departure;
        this.checkOut = checkOut;
        this.destination = destination;
        this.amount = amount;
        this.transaction = transaction;
        this.classTrans = classTrans;
        this.comments = comments;
        this.product = product;
    }

    public Date getCheckIn() {
        return checkIn;
    }

    public String getDeparture() {
        return departure;
    }

    public Date getCheckOut() {
        return checkOut;
    }

    public String getDestination() {
        return destination;
    }

    public double getAmount() {
        return amount;
    }

    public String getTransaction() {
        return transaction;
    }

    public String getProduct() {
        return product;
    }

    public String getComments() {
        return comments;
    }

    @Override
    public String toString() {
        SimpleDateFormat start = new SimpleDateFormat("E yyyy/MM/dd");
        SimpleDateFormat end = new SimpleDateFormat("HH:mm");
        return start.format(checkIn) + " " + departure + " -> " + end.format(checkOut) + " " + destination;
    }
}
