package com.kasoverskiy.ovchipkaart.model;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Created by Vadelic on 03.04.2016.
 */
public class Transaction {
    private LocalDate dateCheckIn;
    private LocalTime checkIn;
    private String departure;
    private LocalTime checkOut;
    private String destination;
    private double amount;
    private String transaction;
    private String classTrans;
    private String product;
    private String comments;

    private Transaction(Builder builder) {
        this.dateCheckIn = builder.dateCheckIn;
        this.checkIn = builder.checkIn;
        this.departure = builder.departure;
        this.checkOut = builder.checkOut;
        this.destination = builder.destination;
        this.amount = builder.amount;
        this.transaction = builder.transaction;
        this.classTrans = builder.classTrans;
        this.comments = builder.comments;
        this.product = builder.product;
    }

    public LocalDate getDateCheckIn() {
        return dateCheckIn;
    }

    public LocalTime getCheckIn() {
        return checkIn;
    }

    public String getDeparture() {
        return departure;
    }

    public LocalTime getCheckOut() {
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

    public String getClassTrans() {
        return classTrans;
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
//        return (dateCheckIn) + " " + departure + " -> " + (checkOut) + " " + destination;
        return dateCheckIn.format(DateTimeFormatter.ofPattern("E dd-MM-yyyy")) + " " + departure + " -> " +
                checkOut.format(DateTimeFormatter.ofPattern("HH:mm"))+" " +checkOut.getHour()+ " " + destination;
    }

    static public class Builder {

        private LocalDate dateCheckIn;
        private LocalTime checkIn;
        private String departure;
        private LocalTime checkOut;
        private String destination;
        private double amount;
        private String transaction;
        private String classTrans;
        private String product;
        private String comments;

        public Transaction build() {
            return new Transaction(this);
        }

        public Builder dateCheckIn(LocalDate dateCheckIn) {
            this.dateCheckIn = dateCheckIn;
            return this;
        }

        public Builder checkIn(LocalTime checkIn) {
            this.checkIn = checkIn;
            return this;
        }

        public Builder departure(String departure) {
            this.departure = departure;
            return this;
        }

        public Builder checkOut(LocalTime checkOut) {
            this.checkOut = checkOut;
            return this;
        }

        public Builder destination(String destination) {
            this.destination = destination;
            return this;
        }

        public Builder amount(double amount) {
            this.amount = amount;
            return this;
        }

        public Builder transaction(String transaction) {
            this.transaction = transaction;
            return this;
        }

        public Builder classTrans(String classTrans) {
            this.classTrans = classTrans;
            return this;
        }

        public Builder product(String product) {
            this.product = product;
            return this;
        }

        public Builder comments(String comments) {
            this.comments = comments;
            return this;
        }

    }
}
