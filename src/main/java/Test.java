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
    }
}
