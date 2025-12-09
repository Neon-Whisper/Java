import java.util.Scanner;

public class ExceptionHandle {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        int n = -1;
        boolean flag = false;

        while(!flag)
        {
            try
            {
                System.out.println("Enter an integer: ");
                n = sc.nextInt();
                flag = true;
            }
            catch(Exception e)
            {
                System.out.println("Invalid input. Please enter an integer: ");
                sc.next();
            }
        }
        System.out.println("Your number is: " + n);
        sc.close();
    }
}
