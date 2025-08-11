public class Circular extends TwoDimensionalShape implements Printable{

    private double radius;

    public Circular() {
        this.radius = 1;
    }

    public Circular(double radius) {
        this.radius = radius;
    }

    @Override
    public double getArea() {
        return Math.PI*radius*radius;
    }

    @Override
    public double getCircumference() {
        return 2*Math.PI*radius;
    }

    @Override
    public String toString()
    {
        return "半径：" + radius ;
    }

    @Override
    public void printShapeInfo() {
        System.out.println("图形的类型：" + this.getClass().getName());
        System.out.println(this.toString());
    }
}
