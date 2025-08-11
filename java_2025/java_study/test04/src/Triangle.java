public class Triangle extends TwoDimensionalShape implements Printable{

   private double base;
   private double hypotenus1;
   private double hypotenus2;

   public Triangle() {
        this.base = 0;
        this.hypotenus1 = 0;
        this.hypotenus2 = 0;
    }

   public Triangle(double base, double hypotenus1, double hypotenus2) {
        this.base = base;
        this.hypotenus1 = hypotenus1;
        this.hypotenus2 = hypotenus2;
    }

    @Override
    public double getArea() {
        double p = (base + hypotenus1 + hypotenus2)/2;
        double s = Math.sqrt(p*(p-base)*(p-hypotenus1)*(p-hypotenus2));
        return s;
    }

    @Override
    public double getCircumference() {
        return base + hypotenus1 + hypotenus2;
    }

    @Override
    public String toString()
    {
        return "底：" + base + "，斜边1：" + hypotenus1 + "，斜边2：" + hypotenus2 ;
    }
    @Override
    public void printShapeInfo() {

        System.out.println("图形的类型：" + this.getClass().getName());
        System.out.println(this.toString());
    }
}
