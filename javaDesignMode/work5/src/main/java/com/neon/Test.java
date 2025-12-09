package com.neon;

// 抽象类：Coffee
abstract class Coffee {
    public abstract String getDescription();
    public abstract double cost();
}

// 具体构件类：SimpleCoffee
class SimpleCoffee extends Coffee {
    @Override
    public String getDescription() {
        return "基础咖啡";
    }

    @Override
    public double cost() {
        return 5.0;
    }
}

// 具体构件类：Espresso
class Espresso extends Coffee {
    @Override
    public String getDescription() {
        return "浓缩咖啡";
    }

    @Override
    public double cost() {
        return 6.0;
    }
}

// 抽象装饰类：Decorator
abstract class Decorator extends Coffee {
    protected Coffee coffee;

    public Decorator(Coffee coffee) {
        this.coffee = coffee;
    }

    @Override
    public String getDescription() {
        return coffee.getDescription();
    }

    @Override
    public double cost() {
        return coffee.cost();
    }
}

// 具体装饰类：Sugar
class Sugar extends Decorator {
    public Sugar(Coffee coffee) {
        super(coffee);
    }

    @Override
    public String getDescription() {
        return coffee.getDescription() + ", 加一份糖";
    }

    @Override
    public double cost() {
        return coffee.cost() + 0.5;
    }
}

// 具体装饰类：Milk
class Milk extends Decorator {
    public Milk(Coffee coffee) {
        super(coffee);
    }

    @Override
    public String getDescription() {
        return coffee.getDescription() + ", 加一份奶";
    }

    @Override
    public double cost() {
        return coffee.cost() + 1.0;
    }
}

// 具体装饰类：Ice
class Ice extends Decorator {
    public Ice(Coffee coffee) {
        super(coffee);
    }

    @Override
    public String getDescription() {
        return coffee.getDescription() + ", 加一份冰";
    }

    @Override
    public double cost() {
        return coffee.cost() + 0.5;
    }
}

//// 具体装饰类：Chocolate
//class Chocolate extends Decorator {
//    public Chocolate(Coffee coffee) {
//        super(coffee);
//    }
//
//    @Override
//    public String getDescription() {
//        return coffee.getDescription() + ", 加巧克力";
//    }
//
//    @Override
//    public double cost() {
//        return coffee.cost() + 2.0;
//    }
//}

//// 具体装饰类：Foam
//class Foam extends Decorator {
//    public Foam(Coffee coffee) {
//        super(coffee);
//    }
//
//    @Override
//    public String getDescription() {
//        return coffee.getDescription() + ", 加奶泡";
//    }
//
//    @Override
//    public double cost() {
//        return coffee.cost() + 1.5;
//    }
//}

// 测试类
public class Test {
    public static void main(String[] args) {
        // 一：加一份糖和双份奶的拿铁
        Coffee latte = new SimpleCoffee();
        latte = new Sugar(latte);
        latte = new Milk(latte);
        latte = new Milk(latte);
        System.out.println(latte.getDescription() + "，总价：" + latte.cost() + "元");

        // 二：加一份冰的冰美式
        Coffee icedAmericano = new SimpleCoffee();
        icedAmericano = new Ice(icedAmericano);
        System.out.println(icedAmericano.getDescription() + "，总价：" + icedAmericano.cost() + "元");
    }
}
