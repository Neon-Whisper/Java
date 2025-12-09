package com.neon;

// 自定义异常类
class UnsupportedShapeException extends Exception {
    public UnsupportedShapeException(String message) {
        super(message);
    }
}

// 抽象产品接口
interface Shape {
    void draw();
    void erase();
}

//圆形
class Circle implements Shape {
    @Override
    public void draw() {
        System.out.println("绘制圆形");
    }

    @Override
    public void erase() {
        System.out.println("擦除圆形");
    }
}

//矩形
class Rectangle implements Shape {
    @Override
    public void draw() {
        System.out.println("绘制矩形");
    }

    @Override
    public void erase() {
        System.out.println("擦除矩形");
    }
}

//三角形
class Triangle implements Shape {
    @Override
    public void draw() {
        System.out.println("绘制三角形");
    }

    @Override
    public void erase() {
        System.out.println("擦除三角形");
    }
}

//工厂类
class ShapeFactory {
    public static Shape createShape(String shapeType) throws UnsupportedShapeException {
        if (shapeType.equalsIgnoreCase("circle")) {
            return new Circle();
        } else if (shapeType.equalsIgnoreCase("rectangle")) {
            return new Rectangle();
        } else if (shapeType.equalsIgnoreCase("triangle")) {
            return new Triangle();
        } else {
            throw new UnsupportedShapeException("不支持的图形类型：" + shapeType);
        }
    }
}

public class Client {
    public static void main(String[] args) {
        try {
            // 绘制矩形
            Shape rectangle = ShapeFactory.createShape("rectangle");
            rectangle.draw();
            rectangle.erase();
            //绘制五角型
            Shape pentagon = ShapeFactory.createShape("pentagon");
            pentagon.draw();
        } catch (UnsupportedShapeException e) {
            System.out.println("异常捕获：" + e.getMessage());
        }
    }
}