package com.kasoverskiy.ovchipkaart.model;

import javax.annotation.Nullable;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Вадим on 03.04.2016.
 */
public class Transaction {
    private Calendar checkIn;
    private String departure;
    private Calendar checkOut;


    private String destination;
    private double amount;
    private String transaction;
    private String classTrans;
    private String product;
    private String comments;

    public Transaction(Calendar checkIn, String departure,
                       Calendar checkOut, String destination,
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

    public Calendar getCheckIn() {
        return checkIn;
    }

    public String getDeparture() {
        return departure;
    }

    public Calendar getCheckOut() {
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
//        return (checkIn) + " " + departure + " -> " + (checkOut) + " " + destination;
        return start.format(checkIn.getTime()) + " " + departure + " -> " + end.format(checkOut.getTime()) + " " + destination;
    }
}
