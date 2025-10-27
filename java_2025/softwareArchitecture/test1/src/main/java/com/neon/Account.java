package com.neon;

public class Account {
    private String id;
    private String password;
    private double balance;
    private String phone;

    public Account(String id, String password, double balance, String phone) {
        this.id = id;
        this.password = password;
        this.balance = balance;
        this.phone = phone;
    }

    public boolean verifyPassword(String pwd) {
        return password.equals(pwd);
    }

    public void deposit(double amount) {
        balance += amount;
    }

    public boolean withdraw(double amount) {
        if (balance >= amount) {
            balance -= amount;
            return true;
        }
        return false;
    }

    public boolean transfer(Account target, double amount) {
        if (withdraw(amount)) {
            target.deposit(amount);
            return true;
        }
        return false;
    }

    public String getId() {
        return id;
    }

    public double getBalance() {
        return balance;
    }

    public String getPhone() {
        return phone;
    }
}