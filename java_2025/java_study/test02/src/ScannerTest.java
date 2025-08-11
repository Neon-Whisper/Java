import java.util.Scanner;

public class ScannerTest {


    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String id = scanner.nextLine();
        int[] days = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

        //判断输入内容是否为空
        if (id == null)
        {
            System.out.println("id为空");
            return;
        }
        //判断输入字符串是否为18位
        if (id.length() != 18)
        {
            System.out.println("id长度不对");
            return;
        }
        //判断前17位是否为数字，最后1位是否为数字或X
        for (int i = 0; i < 17; i++)
        {
            if (!Character.isDigit(id.charAt(i)))
            {
                System.out.println("id前17位不是数字");
                return;
            }
        }
        if (!Character.isDigit(id.charAt(17)) && id.charAt(17) != 'X')
        {
            System.out.println("id后1位不是数字也不是X");
            return;
        }
        //判断7至14位是否为合法的日期
        int year = Integer.parseInt(id.substring(6, 10));
        int month = Integer.parseInt(id.substring(10, 12));
        int day = Integer.parseInt(id.substring(12, 14));
        if (year >= 1900 && year <= 2019)
        {
            if (month >= 1 && month <= 12)
            {
                if (day >= 1 && day <= days[month - 1])
                {
                    System.out.println("id合法");
                    return;
                }
                else
                {
                    System.out.println("id日期不合法");
                    return;
                }
            }
        }
        System.out.println("id不合法");
        return;
    }
}
