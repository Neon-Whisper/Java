package com.neon;

public class Main {
    public static void main(String[] args) {
        Bank bank = new Bank();
        ATM atm = new ATM(bank);

        Account zhangsan = new Account("1001", "123", 200, "13800138000");
        Account lisi = new Account("1002", "456", 500, "13900139000");

        System.out.println("张三存款 800：");
        atm.deposit(zhangsan, 800);

        System.out.println("\n张三给李四转账 1000：");
        atm.transfer(zhangsan, lisi, 1000);
    }
}