import java.util.Arrays;

/**
 * Created by simen on 3/17/17.
 */
public class Test {
    //http://stackoverflow.com/questions/12815460/hashmap-iterating-the-key-value-pairs-in-random-order
    public static void main (String[] args) {
        String ord = "./-?adsfasdf!:\\tdsf;,()[]{}#\"\'";
        System.out.println(ord);
        //ord.replaceAll("[./-!:\\;,()[]{}#\"\']","");

        ord = ord.replaceAll("[.\\-!:\\\\;,()\\[\\]\\}\\{\\}#\\\"\\'<>|/*^_+?]","");
        //ord = ord.replaceAll("[.]","");
        System.out.println(ord);

        int[] x = {0,1,2,3,4,5,6,7,8,9};
        int[] y = Arrays.copyOfRange(x, 0,5);
        System.out.println(Arrays.toString(y));

        double multiplier = 1 + Math.log10(3);
        System.out.println(multiplier);
    }
}
