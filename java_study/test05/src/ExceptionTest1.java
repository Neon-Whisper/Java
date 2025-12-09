
import java.util.ArrayList;

public class ExceptionTest1 {

    public static void main(String[] args) {

        ArrayList<Integer> al = new ArrayList<Integer>();
        for (int i = 0; i < 10; i++) {
            al.add(new Integer(i));
        }

        for(Integer i:al){
            System.out.println(i);
			if(i.equals(new Integer(5))){
				al.remove(i);
			}
        }
        System.out.println(al);
    }
}
