import java.util.Scanner;

public class Test {
    public static void main(String[] args)
    {
        Scanner  sc = new Scanner(System.in);
        boolean flag = false;

        while(!flag)
        {
            try
            {
                System.out.println("输入三角形的三边: ");
                double a = sc.nextDouble();
                double b = sc.nextDouble();
                double c = sc.nextDouble();
                Triangle t = new Triangle(a, b, c);
                flag = true;

                System.out.println(t.toString());
                System.out.println("周长: " + t.getPerimeter());
                System.out.println("面积: " + t.getArea());
            }
            catch (TriangleException e)
            {
                System.out.println("错误: " + e.getMessage());
            }
            catch(Exception e)
            {
                System.out.println("输入无效，请输入数字！");
                sc.next();
            }
        }

        sc.close();
    }
}
