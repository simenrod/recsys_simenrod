package recommender;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;


import recommender.lenskit.ContentBased;
import recommender.lenskit.ItemBasedRecommender;
import recommender.nonframework.Baseline;
import recommender.spark.ModelBased;
//import recommender.nonframework.Baseline;


/**
 * Created by simen on 3/16/17.
 */
public class Evaluator {

    public static void main(String[] args) {
        Evaluator eval = new Evaluator();
        System.out.println("Test");
        //ContentBased sr = new ContentBased();
        //sr.initialize("data/movie-tags.csv", "data/movie-titles-test.csv");
        //sr.initialize("data/movielens/item-tags", "data/movielens/titles");
        ItemBasedRecommender sr = new ItemBasedRecommender();
        //ModelBased sr = new ModelBased();
        //Cbf sr = new Cbf();
        //Baseline sr = new Baseline();
        sr.initialize();
        //String[] trainingFiles = {"/home/simen/Documents/datasett/crossfold-movielens-binary/training"};
        //String[] testFiles = {"/home/simen/Documents/datasett/crossfold-movielens-binary/test"};
        /*String[] trainingFiles = {"data/movielens/leave_one_out/train1","data/movielens/leave_one_out/train2",
                "data/movielens/leave_one_out/train3","data/movielens/leave_one_out/train4",
                "data/movielens/leave_one_out/train5"};
        String[] testFiles = {"data/movielens/leave_one_out/test1","data/movielens/leave_one_out/test2",
                "data/movielens/leave_one_out/test3","data/movielens/leave_one_out/test4",
                "data/movielens/leave_one_out/test5"};*/
        String[] trainingFiles = {"data/movielens/cross-val/train1","data/movielens/cross-val/train2",
                "data/movielens/cross-val/train3","data/movielens/cross-val/train4",
                "data/movielens/cross-val/train5"};
        String[] testFiles = {"data/movielens/cross-val/test1","data/movielens/cross-val/test2",
                "data/movielens/cross-val/test3","data/movielens/cross-val/test4",
                "data/movielens/cross-val/test5"};
        //String[] testFiles = {"data/bx/cross-val/test1"};
        //String[] trainingFiles = {"data/bx/cross-val/train1"};
        /*String[] testFiles = {"data/tag-test/test1"};
        String[] trainingFiles = {"data/tag-test/train1"};*/

        //eval.hitRate(sr, trainingFiles, testFiles, 10);
        eval.map(sr, trainingFiles, testFiles, 10);
        //ModelBased.stopSparkContext(); //make instance variable + probably not make new context for each test

    }

    public void hitRate(Recommender rs, String[] trainingFiles, String[] testFiles, int n) {
        if (trainingFiles.length != testFiles.length) {
            System.out.println("Not equal numbers of trainingFiles and testFiles");
            return;
        }

        double avgHr = 0;
        double avgTime = 0;

        //Repeats for all of the trainingfiles. i is the fold nr
        for (int i = 0; i < trainingFiles.length; i++) {
            //System.out.println("Testing with file " + (i+1) + ".");
            rs.update(trainingFiles[i]); //trains recommender with training file
            HashMap<Integer, HashMap<Integer, Double>> testData = readTestData(testFiles[i]);
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


            for (int userId : testData.keySet()) {

                int[] recommendedItems = rs.recommend(userId, n);
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
            avgHr += hr/trainingFiles.length;
            System.out.println("Hr fold nr " + i + ": " + hr);
        }

        System.out.println("Average hr all folds: " + avgHr);
    }


    //Returns true if one of the recommended items are in the set of relevant items
    //(if I want to include ARHR -> return index for first match, if want to use MAP -> return AP for user)
    public boolean isMatch(int[] recommendedItems, Set<Integer> relevantItems) {
        for (int recommendedId : recommendedItems) {
            for (int relevantId : relevantItems) {
                if (recommendedId == relevantId) {
                    return true;
                }
            }
        }
        return false;
    }

//https://github.com/lenskit/lenskit/blob/master/lenskit-eval/src/main/java/org/lenskit/eval/traintest/recommend/TopNMAPMetric.java


    //n=number of recs
    public void map(Recommender rs, String[] trainingFiles, String[] testFiles, int n) {
        long startTime;
        long endTime;
        double avgMap = 0;
        double avgTrainTime = 0;
        double avgRecTime = 0;
        int nFolds = trainingFiles.length;

        if (trainingFiles.length != testFiles.length) {
            System.out.println("Not equal numbers of trainingFiles and testFiles");
            return;
        }

        //Repeats for all of the trainingfiles. i is the fold nr
        for (int i = 0; i < trainingFiles.length; i++) {
            //System.out.println("Testing with file " + (i+1) + ".");

            startTime = System.nanoTime();
            rs.update(trainingFiles[i]); //trains recommender with training file
            endTime = System.nanoTime();
            double trainingTime = endTime-startTime;
            System.out.println("Time used for training recommender:" + trainingTime);
            avgTrainTime += trainingTime/nFolds;


            HashMap<Integer, HashMap<Integer, Double>> testData = readTestData(testFiles[i]);
            double sum = 0;
            double avgTime = 0;
            int users = testData.size();
            double map;

            for (int userId : testData.keySet()) {
                startTime = System.nanoTime();
                int[] recommendedItems = rs.recommend(userId, n);
                endTime = System.nanoTime();
                avgTime += (endTime-startTime)/users;
                //if (isMatch(recommendedItems, testData.get(userId).keySet())) matches++;
                //double ap = averagePrecision(recommendedItems, testData.get(userId).keySet());
                //System.out.println(ap);
                //sum +=  ap;
                //System.out.println("Recs for user "+userId);
                sum += averagePrecision(recommendedItems, testData.get(userId).keySet());
                /*for (String itemId : testData.get(userId).keySet()) {
                    for (int recommendedId : recommendedItems) {
                        if (recommendedId  == Integer.parseInt(itemId)) {
                            matches++;
                        }
                    }
                }*/
            }
            map = (double) sum / users;
            System.out.println("Map: " + map);
            System.out.println("Avg time producing rec: " + avgTime);
            avgMap += map/nFolds;
            avgRecTime += avgTime/nFolds;
        }
        System.out.println("Average map (all folds): " + avgMap);
        System.out.println("Average train time (all folds): " + avgTrainTime);
        System.out.println("Average test time (all folds): " + avgRecTime);
    }

    public double averagePrecision(int[] recommendedItems, Set<Integer> relevantItems) {
        double rel = 0;
        double sum = 0;
        double ap;

        for (int i = 0; i < recommendedItems.length; i++) {
            //System.out.println(recommendedItems[i]);
            for (int relevantId : relevantItems) {
                if (recommendedItems[i] == relevantId) {
                    sum += (++rel / (i+1));
                    //System.out.println("Nr rel: " + rel + ", pos: " + (i+1));
                    //System.out.println("(MATCH)");
                }
            }
        }
        //System.out.println("sum: " + sum + ", rel: " + rel);
        if (rel == 0) return 0;

        ap = sum / rel;
        return ap;
    }


    //Returns datastructure with data from trainingFile
    public HashMap<Integer, HashMap<Integer, Double>> readTestData(String testFile){

        //datastructure for relevant intems: HashMap with one hashmap for each test user. The inner hashmap stores the relevant
        //items for the user
        HashMap<Integer, HashMap<Integer, Double>> testData = new HashMap<Integer, HashMap<Integer, Double>>();

        try {
            BufferedReader br = new BufferedReader(new FileReader(testFile));

            String line = br.readLine();
            while (line != null) {
                String[] parts = line.split("\t");
                Integer userId = Integer.parseInt(parts[0]);
                Integer itemId = Integer.parseInt(parts[1]);
                Double rating = Double.parseDouble(parts[2]);

                //adds relevant item in this user's hashmap
                if (testData.get(userId) == null) testData.put(userId, new HashMap<Integer, Double>());
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
