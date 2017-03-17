import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

/**
 * Created by simen on 3/16/17.
 */
public class Evaluator {

    public static void main(String[] args) {
        Evaluator eval = new Evaluator();
        System.out.println("Test");
        SparkRecommender sr = new SparkRecommender();
        sr.initialize();
        String[] trainingFiles = {"/home/simen/Documents/datasett/crossfold-movielens-binary/training"};
        String[] testFiles = {"/home/simen/Documents/datasett/crossfold-movielens-binary/test"};
        eval.hitRate(sr, trainingFiles, testFiles, 10);
        SparkRecommender.stopSparkContext(); //make instance variable + probably not make new context for each test

    }

    public void hitRate(Recommender rs, String[] trainingFiles, String[] testFiles, int n) {
        if (trainingFiles.length != testFiles.length) {
            System.out.println("Not equal numbers of trainingFiles and testFiles");
            return;
        }

        //Repeats for all of the trainingfiles. i is the fold nr
        for (int i = 0; i < trainingFiles.length; i++) {
            System.out.println("Testing with file " + i + ".");
            rs.update(trainingFiles[i]); //trains recommender with training file
            HashMap<String, HashMap<String, Integer>> testData = readTestData(testFiles[i]);
            int matches = 0;
            int users = testData.size();
            double hr;

            /*System.out.println(testData.get("943").size());
            //String[] = testData.get("1").keySet();
            for (String s : testData.get("75").keySet()) {
                System.out.println(s + ", rating: "+ testData.get("75").get(s));

            }*/
            /*int[] recs = rs.recommend(1, 10);
            System.out.println("Recs for user 1");

            for (int x : recs) {
                System.out.println(x);
            }*/
            //System.out.println(testData.size());


            for (String userId : testData.keySet()) {
                int[] recommendedItems = rs.recommend(Integer.parseInt(userId), n);
                if (isMatch(recommendedItems, testData.get(userId).keySet())) matches++;
                /*for (String itemId : testData.get(userId).keySet()) {
                    for (int recommendedId : recommendedItems) {
                        if (recommendedId  == Integer.parseInt(itemId)) {
                            matches++;
                        }
                    }
                }*/
            }
            hr = (double) matches / users;
            System.out.println(hr);
        }
    }


    //Returns true if one of the recommended items are in the set of relevant items
    //(if I want to include ARHR -> return index for first match, if want to use MAP -> return AP for user)
    public boolean isMatch(int[] recommendedItems, Set<String> relevantItems) {
        for (int recommendedId : recommendedItems) {
            for (String relevantId : relevantItems) {
                if (recommendedId == Integer.parseInt(relevantId)) {
                    return true;
                }
            }
        }
        return false;
    }


    //Returns datastructure with data from trainingFile
    public HashMap<String, HashMap<String, Integer>> readTestData(String testFile){

        //datastructure for relevant intems: HashMap with one hashmap for each test user. The inner hashmap stores the relevant
        //items for the user
        HashMap<String, HashMap<String, Integer>> testData = new HashMap<String, HashMap<String, Integer>>();

        try {
            BufferedReader br = new BufferedReader(new FileReader(testFile));

            String line = br.readLine();
            while (line != null) {
                String[] parts = line.split("\t");
                String userId = parts[0];
                String itemId = parts[1];
                Integer rating = Integer.parseInt(parts[2]);

                //adds relevant item in this user's hashmap
                if (testData.get(userId) == null) testData.put(userId, new HashMap<String, Integer>());
                testData.get(userId).put(itemId, rating);
                line = br.readLine();
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        return testData;
    }
}
