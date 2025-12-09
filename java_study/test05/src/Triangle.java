public class Triangle {
    private double a;
    private double b;
    private double c;

    public Triangle(double a, double b, double c) throws TriangleException
    {
        if (a <= 0 || b <= 0 || c <= 0) {
            throw new TriangleException("边长必须为正数！");
        }
        if (a + b <= c || a + c <= b || b + c <= a) {
            throw new TriangleException("两边之和必须大于第三边！");
        }

        // 如果合法，初始化三边
        this.a = a;
        this.b = b;
        this.c = c;
    }

    public String toString() {
        return "三角形的三边为：" + a + " " + b + " " + c;
    }
    // 计算周长
    public double getPerimeter() {
        return a + b + c;
    }

    // 计算面积（海伦公式）
    public double getArea() {
        double s = getPerimeter() / 2;
        return Math.sqrt(s * (s - a) * (s - b) * (s - c));
    }
}
