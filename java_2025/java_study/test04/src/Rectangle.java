public class Rectangle extends TwoDimensionalShape implements Printable {

    private double length;
    private double width;

    public Rectangle() {
        this.length = 1;
        this.width = 1;
    }

    public Rectangle(double length, double width) {
        this.length = length;
        this.width = width;
    }

    @Override
    public double getArea() {
        return length * width;
    }

    @Override
    public double getCircumference() {
        return (length + width) * 2;
    }

    @Override
    public String toString()
    {
        return "宽：" + width + "，长：" + length ;
    }

    @Override
    public void printShapeInfo() {
        //（1）	图形的类型（使用getClass方法）
        System.out.println("图形的类型：" + this.getClass().getName());
        System.out.println(this.toString());
    }
}
