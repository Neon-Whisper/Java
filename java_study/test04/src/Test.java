public class Test {
    public static void main(String[] args) {
        TwoDimensionalShape[] Shapes = new TwoDimensionalShape[3];
        Shapes[0] = new Rectangle(3, 4);
        Shapes[1] = new Triangle(3, 4, 5);
        Shapes[2] = new Circular(3);

        ShapeDetector sd = new ShapeDetector();

        for (TwoDimensionalShape s : Shapes)
        {
            if (s instanceof Rectangle)
            {
                sd.detectShape((Rectangle) s);
            }
            else if (s instanceof Triangle)
            {
                sd.detectShape((Triangle) s);
            }
            else if (s instanceof Circular)
            {
                sd.detectShape((Circular) s);
            }

            s.printShapeInfo();

            System.out.println("面积：" + s.getArea());
            System.out.println("周长" + s.getCircumference());

        }
    }
}


