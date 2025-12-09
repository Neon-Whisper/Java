import java.util.ArrayList;

public class ExceptionTest {

    public static void main(String[] args) {

        int sum = 0;

        ArrayList al = new ArrayList();
        for (int i = 0; i < 10; i++) {
            al.add(new Integer(i));
        }
//        al.add("string");
        for(Object o:al){
            sum = sum +(Integer)o;
        }
        System.out.println(sum);
    }
}

