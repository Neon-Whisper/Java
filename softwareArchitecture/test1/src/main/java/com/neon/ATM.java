package com.neon;

public class ATM {
    private Bank bank;

    public ATM(Bank bank) {
        this.bank = bank;
    }

    public void deposit(Account account, double amount) {
        account.deposit(amount);
        bank.sendSMS(account.getPhone(), "存款成功，金额：" + amount + "，当前余额：" + account.getBalance());
        System.out.println("存款成功，当前余额：" + account.getBalance());
    }

    public void transfer(Account from, Account to, double amount) {
        if (from.transfer(to, amount)) {
            bank.sendSMS(from.getPhone(), "转账成功，转出金额：" + amount + "，当前余额：" + from.getBalance());
            bank.sendSMS(to.getPhone(), "收到转账，金额：" + amount + "，当前余额：" + to.getBalance());
            System.out.println("转账成功");
        } else {
            System.out.println("转账失败，余额不足");
        }
    }
}