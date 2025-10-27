package com.neon;

// 猫接口
interface Cat {
    void catchMouse();
}

// 狗接口
interface Dog {
    void bark();
}

// 具体猫类
class RealCat implements Cat {
    @Override
    public void catchMouse() {
        System.out.println("猫抓老鼠");
    }
}

// 具体狗类
class RealDog implements Dog {
    @Override
    public void bark() {
        System.out.println("狗叫");
    }
}

// 双向适配器类
class CatDogAdapter implements Cat, Dog {
    // 同时维持对猫和狗的引用
    private Cat cat;
    private Dog dog;

    // 构造器注入猫
    public CatDogAdapter(Cat cat) {
        this.cat = cat;
    }

    // 构造器注入狗
    public CatDogAdapter(Dog dog) {
        this.dog = dog;
    }

    // 猫学狗的 bark 方法
    @Override
    public void bark() {
        if (dog != null) {
            System.out.print("猫学：");
            dog.bark();
        }
    }

    // 狗学猫的 catchMouse 方法
    @Override
    public void catchMouse() {
        if (cat != null) {
            System.out.print("狗学：");
            cat.catchMouse();
        }
    }


}

public class Test
{
    // 测试类
    public static void main(String[] args) {
        Cat realCat = new RealCat();
        Dog realDog = new RealDog();

        // 让猫学狗叫
        CatDogAdapter catAdapter = new CatDogAdapter(realDog);
        realCat.catchMouse();
        catAdapter.bark();

        // 让狗学猫抓老鼠
        CatDogAdapter dogAdapter = new CatDogAdapter(realCat);
        realDog.bark();
        dogAdapter.catchMouse();
    }
}