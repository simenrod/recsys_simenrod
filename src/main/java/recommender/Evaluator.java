package recommender;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;


import org.dmg.pmml.Model;
import recommender.lenskit.ContentBasedRecommender;
import recommender.lenskit.ItemBasedRecommender;
import recommender.nonframework.BaselineRecommender;
import recommender.spark.ModelBasedRecommender;
//import recommender.nonframework.BaselineRecommender;


/**
 * Created by simen on 3/16/17.
 */
public class Evaluator {

    public static void main(String[] args) {
        Evaluator eval = new Evaluator();
        //ContentBasedRecommender sr = new ContentBasedRecommender();
        //sr.initialize("data/ml6k/tags", "data/ml6k/titles");
        //sr.initialize("data/msd6k/tags-reduced", "data/msd6k/titles");
        //sr.initialize("data/movie-tags.csv", "data/movie-titles-test.csv");
        //sr.initialize("data/movielens/item-tags", "data/movielens/titles");
        //sr.initialize("data/bx/item-tags", "data/bx/titles");
        //sr.initialize("data/bx6k/item-tags-reduced", "data/bx6k/titles");
        //ItemBasedRecommender sr = new ItemBasedRecommender();
        //ModelBasedRecommender sr = new ModelBasedRecommender();
        //BaselineRecommender sr = new BaselineRecommender();
        //sr.initialize();
        //String[] trainingFiles = {"/home/simen/Documents/datasett/crossfold-movielens-binary/training"};
        //String[] testFiles = {"/home/simen/Documents/datasett/crossfold-movielens-binary/test"};
        /*String[] trainingFiles = {"data/movielens/leave_one_out/train1","data/movielens/leave_one_out/train2",
                "data/movielens/leave_one_out/train3","data/movielens/leave_one_out/train4",
                "data/movielens/leave_one_out/train5"};
        String[] testFiles = {"data/movielens/leave_one_out/test1","data/movielens/leave_one_out/test2",
                "data/movielens/leave_one_out/test3","data/movielens/leave_one_out/test4",
                "data/movielens/leave_one_out/test5"};*/
        /*String[] trainingFiles = {"data/movielens/cross-val/train1","data/movielens/cross-val/train2",
                "data/movielens/cross-val/train3","data/movielens/cross-val/train4",
                "data/movielens/cross-val/train5"};
        String[] testFiles = {"data/movielens/cross-val/test1","data/movielens/cross-val/test2",
                "data/movielens/cross-val/test3","data/movielens/cross-val/test4",
                "data/movielens/cross-val/test5"};*/
        /*String[] testFiles = {"data/bx/cross-val/test1"};
        String[] trainingFiles = {"data/bx/cross-val/train1"};*/
        /*String[] testFiles = {"data/bx6k/cross-val/test1"};
        String[] trainingFiles = {"data/bx6k/cross-val/train1"};*/
        //String[] testFiles = {"data/msd6k/cross-val3/test1"};
        //String[] trainingFiles = {"data/msd6k/cross-val3/train1"};
        //String[] testFiles = {"data/ml6k/cross-val/test1"};
        //String[] trainingFiles = {"data/ml6k/cross-val/train1"};

        //String[] testFiles = {"data/tag-test/test1"};
        //String[] trainingFiles = {"data/tag-test/train1"};

        //eval.hitRate(sr, trainingFiles, testFiles, 10);
        //eval.map(sr, trainingFiles, testFiles, 10);
        //eval.combinedEvaluator(sr, trainingFiles, testFiles);
        //ModelBasedRecommender.stopSparkContext(); //make instance variable + probably not make new context for each test


        //Tests run 28.03.17 - 5 fold cross validation all-but-10 MAP - MSD+BX+ML
        //Recommender[] rss = eval.getTopNRecommenders("data/ml6k/tags", "data/ml6k/titles");
        Recommender[] rss = new Recommender[1];
        ModelBasedRecommender mbr = new ModelBasedRecommender(10, 10, 0.01, 1.0);
        //ContentBasedRecommender cbr = new ContentBasedRecommender("data/ml6k/tags", "data/ml6k/titles");
        rss[0] = mbr;
        eval.evaluateCrossFold(rss, "data/ml6k/ab10", 1, 5);

        //rss = eval.getTopNRecommenders("data/msd6k/tags", "data/msd6k/titles");
        //cbr = new ContentBasedRecommender("data/msd6k/tags", "data/msd6k/titles");
        //rss[0] = cbr;
        //eval.evaluateCrossFold(rss, "data/msd6k/ab10", 1, 5);

        //rss = eval.getTopNRecommenders("data/bx6k/item-tags-reduced", "data/bx6k/titles");
        //cbr = new ContentBasedRecommender("data/bx6k/item-tags-reduced", "data/bx6k/titles");
        //rss[0] = cbr;
        //eval.evaluateCrossFold(rss, "data/bx6k/ab10", 1, 5);

        /*ContentBasedRecommender cbr = new ContentBasedRecommender("data/bx6k/item-tags-reduced","data/bx6k/titles");
        Recommender[] rss = {cbr};
        eval.evaluateCrossFold(rss, "data/bx6k/ab10", 2, 5);*/
        //eval.evalBab10();
        /*ContentBasedRecommender cbr1 = new ContentBasedRecommender("data/msd6k/tags","data/msd6k/titles");
        Recommender[] rs1 = {cbr1};

        //eval.evaluateCrossFold(rs1, "data/ml6k/ab10", 1, 5);
        eval.evaluateCrossFold(rs1, "data/msd6k/ab10", 1, 5);

        ContentBasedRecommender cbr2 = new ContentBasedRecommender("data/bx6k/item-tags-reduced","data/bx6k/titles");
        Recommender[] rs2 = {cbr2};

        eval.evaluateCrossFold(rs2, "data/bx6k/ab10", 1, 5);*/

        /*BaselineRecommender bsr = new BaselineRecommender();
        String[] trainingFiles = {"data/bx6k/ab10/train1", "data/bx6k/ab10/train2", "data/bx6k/ab10/train3", "data/bx6k/ab10/train4", "data/bx6k/ab10/train5"};
        String[] testingFiles = {"data/bx6k/ab10/test1", "data/bx6k/ab10/test2", "data/bx6k/ab10/test3", "data/bx6k/ab10/test4", "data/bx6k/ab10/test5"};
        eval.combinedEvaluator(bsr, trainingFiles, testingFiles);*/
        //eval.evalBab10();
        //eval.evalAb10();
        //eval.evalGiven(2);
        //eval.evalGiven(5);
        //eval.evalGiven(8);
        /*Recommender[] rss = new Recommender[4];
        rss[0] = new ModelBasedRecommender();
        rss[1] = new ItemBasedRecommender();
        rss[2] = new ContentBasedRecommender("data/ml10m/tags5m", "data/ml10m/titles5m");
        rss[3] = new BaselineRecommender();
        //eval.testScalability(rss, "data/bx/ratings10m", "data/bx/item-tags", "data/bx/titles");
        //eval.testScalability(rss, "data/ml10m/ratings5m", "data/ml10m/tags", "data/ml10m/titles");
        eval.testScalability(rss, "data/ml10m/ratings5m");*/

        /*Recommender[] rss = new Recommender[4];
        rss[0] = new ModelBasedRecommender();
        rss[1] = new ItemBasedRecommender();
        rss[2] = new ContentBasedRecommender("data/ml10m/tags1m", "data/ml10m/titles1m");
        rss[3] = new BaselineRecommender();
        //eval.testScalability(rss, "data/bx/ratings10m", "data/bx/item-tags", "data/bx/titles");
        //eval.testScalability(rss, "data/ml10m/ratings5m", "data/ml10m/tags", "data/ml10m/titles");
        eval.testScalability(rss, "data/ml10m/ratings1m");*/

        /*Recommender[] rss = new Recommender[2];
        //rss[0] = new ModelBasedRecommender();
        //rss[1] = new ItemBasedRecommender();
        //rss[0] = new ContentBasedRecommender("data/ml10m/tags5m", "data/ml10m/titles5m");
        //rss[1] = new BaselineRecommender();
        //rss[0] = new ContentBasedRecommender("data/ml10m/tags5m", "data/ml10m/titles5m");
        //eval.testScalability(rss, "data/bx/ratings10m", "data/bx/item-tags", "data/bx/titles");
        //eval.testScalability(rss, "data/ml10m/ratings5m", "data/ml10m/tags", "data/ml10m/titles");
        eval.testScalability(rss, "data/ml10m/ratings5m");*/

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
        String printString;

        try {
            FileWriter fw = new FileWriter(new File("test-results.txt"), true);

            if (trainingFiles.length != testFiles.length) {
                System.out.println("Not equal numbers of trainingFiles and testFiles");
                return;
            }
            printString = "\nRecommender: " + rs.getInfo() + "\n";
            System.out.print(printString);
            fw.append(printString);

            //Repeats for all of the trainingfiles. i is the fold nr
            for (int i = 0; i < trainingFiles.length; i++) {
                //System.out.println("Testing with file " + (i+1) + ".");

                startTime = System.nanoTime();
                rs.update(trainingFiles[i]); //trains recommender with training file
                endTime = System.nanoTime();
                double trainingTime = endTime - startTime;

                printString = "Fold " + (i+1) + "\n Time used for training recommender:" + trainingTime + "\n";
                System.out.print(printString);
                fw.append(printString);

                avgTrainTime += trainingTime / nFolds;
                HashMap<Integer, HashMap<Integer, Double>> testData = readTestData(testFiles[i]);
                double sum = 0;
                double avgTime = 0;
                int users = testData.size();
                double map;

                for (int userId : testData.keySet()) {
                    startTime = System.nanoTime();
                    int[] recommendedItems = rs.recommend(userId, n);
                    endTime = System.nanoTime();
                    avgTime += (endTime - startTime) / users;
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
                printString = " Map: " + map + "\n Avg time producing rec: " + avgTime + "\n";
                //System.out.println("Map: " + map);
                //System.out.println("Avg time producing rec: " + avgTime);
                fw.append(printString);
                System.out.print(printString);

                avgMap += map / nFolds;
                avgRecTime += avgTime / nFolds;
            }
            printString = "Data all folds: \n Avg map: " + avgMap + "\n Avg train time: " +
                    avgTrainTime + "\n Avg test time: " + avgRecTime + "\n";
            System.out.print(printString);
            fw.append(printString);
            //System.out.println("Average map (all folds): " + avgMap);
            //System.out.println("Average train time (all folds): " + avgTrainTime);
            //System.out.println("Average test time (all folds): " + avgRecTime);
            fw.flush();
            fw.close();
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
            System.exit(1);
        }
    }

    public double averagePrecision(int[] recommendedItems, Set<Integer> relevantItems) {
        double rel = 0;
        double sum = 0;
        double ap;
        int div;

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

        //ap = sum / 10;

        //divides by the smallest of recommendation list-size and the number of relevant items
        if (recommendedItems.length < relevantItems.size()) div = recommendedItems.length;
        else div = relevantItems.size();
        ap = sum / div;

        //ap = sum / rel;
        return ap;
    }

    public double precision(int[] recommendedItems, Set<Integer> relevantItems) {
        double rel = 0;

        for (int i = 0; i < recommendedItems.length; i++) {
            //System.out.println(recommendedItems[i]);
            for (int relevantId : relevantItems) {
                if (recommendedItems[i] == relevantId) {
                    rel++;
                    //sum += (++rel / (i+1));
                    //System.out.println("Nr rel: " + rel + ", pos: " + (i+1));
                    //System.out.println("(MATCH)");
                }
            }
        }
        //System.out.println("Rel: " + rel + ", numItems: " + recommendedItems.length + ", prec: " + (rel/recommendedItems.length));
        return rel/recommendedItems.length;
    }

    public double rhr(int[] recommendedItems, Set<Integer> relevantItems) {
        double arhr;
        for (int i = 0; i < recommendedItems.length; i++) {
            //System.out.println(recommendedItems[i]);
            for (int relevantId : relevantItems) {
                if (recommendedItems[i] == relevantId) {
                    arhr = (double) 1 / (i+1);
                    //System.out.println("ARHR " + arhr);
                    return arhr;
                }
            }
        }
        //System.out.println("ARHR 0");
        return 0;
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

    public void combinedEvaluator(Recommender rs, String[] trainingFiles, String[] testFiles) {
        int[] n = {10,20,30,100,500}; //recs for map, arhr
        int[] m = {3,5,10,20,30}; //recs for hr, precision
        int numRecs = 30;
        long startTime;
        long endTime;
        double[] avgMap = {0,0,0,0,0};
        double avgTrainTime = 0;
        double avgRecTime = 0;
        double[] avgHr = {0,0,0,0,0};
        double[] avgArhr = {0,0,0,0,0};
        double[] avgPrecision = {0,0,0,0,0};
        String printString;
        int nFolds = trainingFiles.length;

        try {
            FileWriter fw = new FileWriter(new File("test-results.txt"), true);

            if (trainingFiles.length != testFiles.length) {
                System.out.println("Not equal numbers of trainingFiles and testFiles");
                return;
            }

            printString = "\nRecommender: " + rs.getInfo() + "\n";
            System.out.print(printString);
            fw.append(printString);

            //Repeats for all of the trainingfiles. i is the fold nr
            for (int i = 0; i < trainingFiles.length; i++) {
                //System.out.println("Testing with file " + (i+1) + ".");

                startTime = System.nanoTime();
                rs.update(trainingFiles[i]); //trains recommender with training file
                endTime = System.nanoTime();
                double trainingTime = endTime - startTime;
                printString = "Fold " + (i+1) + "\n Time used for training recommender:" + trainingTime + "\n";
                System.out.print(printString);
                fw.append(printString);
                avgTrainTime += trainingTime / nFolds;

                HashMap<Integer, HashMap<Integer, Double>> testData = readTestData(testFiles[i]);
                double[] sumAp = {0,0,0,0,0};
                double[] sumRhr = {0,0,0,0,0};
                double[] sumPrecision = {0,0,0,0,0};
                double[] matches = {0,0,0,0,0};
                double avgTime = 0;
                int users = testData.size();
                double[] map = new double[5];
                double[] arhr = new double[5];
                double[] hr = new double[5];
                double[] precision = new double[5];


                for (int userId : testData.keySet()) {
                    startTime = System.nanoTime();
                    int[] recommendedItems = rs.recommend(userId, numRecs);
                    endTime = System.nanoTime();
                    avgTime += (endTime - startTime) / users;
                    //if (isMatch(recommendedItems, testData.get(userId).keySet())) matches++;
                    //double ap = averagePrecision(recommendedItems, testData.get(userId).keySet());
                    //System.out.println(ap);
                    //sumAp +=  ap;
                    //System.out.println("Recs for user "+userId);

                    //updates measures for the different recommendations lengths
                    for (int k = 0; k < n.length; k++) {
                        sumRhr[k] += rhr(Arrays.copyOfRange(recommendedItems, 0, n[k]), testData.get(userId).keySet());
                        //sumAp[k] += precision(Arrays.copyOfRange(recommendedItems, 0, n[k]), testData.get(userId).keySet());
                        sumAp[k] += averagePrecision(Arrays.copyOfRange(recommendedItems, 0, n[k]), testData.get(userId).keySet());//recommendedItems, testData.get(userId).keySet());
                        if (isMatch(Arrays.copyOfRange(recommendedItems, 0, m[k]), testData.get(userId).keySet()))
                            matches[k]++;
                        sumPrecision[k] += precision(Arrays.copyOfRange(recommendedItems, 0, m[k]), testData.get(userId).keySet());
                    }

                /*for (String itemId : testData.get(userId).keySet()) {
                    for (int recommendedId : recommendedItems) {
                        if (recommendedId  == Integer.parseInt(itemId)) {
                            matches++;
                        }
                    }
                }*/
                }
                //printString = "Fold " + i + ":";
                //System.out.println(printString);
                //fw.append(printString);

                //for each of the reommendation lengths, calculates average for this fold, plus adding values to the scores for all folds
                for (int k = 0; k < n.length; k++) {
                    map[k] = (double) sumAp[k] / users;
                    avgMap[k] += map[k] / nFolds;
                    hr[k] = matches[k] / users;
                    avgHr[k] += hr[k] / nFolds;
                    arhr[k] = sumRhr[k] / users;
                    avgArhr[k] += arhr[k] / nFolds;
                    precision[k] = sumPrecision[k] / users;
                    avgPrecision[k] += precision[k] / nFolds;

                    printString = " Map for " + n[k] + " recommendations: " + map[k] + "\n" +
                            " ARHR for " + n[k] + " recommendations:" + arhr[k] + "\n" +
                            " Hr for " + m[k] + " recommendations: " + hr[k] + "\n" +
                            " Precision for " + m[k] + " recommendations: " + precision[k] + "\n";
                    System.out.print(printString);
                    fw.append(printString);
                }

                printString = " Avg time producing rec: " + avgTime + "\n";
                System.out.print(printString);
                fw.append(printString);
                avgRecTime += avgTime / nFolds;
            }

            printString = "===Results all folds:" + "===\n" +
                    " Average train time: " + avgTrainTime + "\n" +
                    " Average test time: " + avgRecTime + "\n";


            /*for (int k = 0; k < n.length; k++) {
                printString = " Average map  for " + n[k] + " recommendations: " + avgMap[k] + "\n" +
                        " Average hr for " + m[k] + " recommendations: " + avgHr[k] + "\n" +
                        " Average arhr for " + n[k] + " recommendations: " + avgArhr[k] + "\n" +
                        " Average precison for " + m[k] + "recommendations: " + avgPrecision[k] + "\n";
                System.out.print(printString);
                fw.append(printString);
            }*/

            for (int k = 0; k < n.length; k++) printString += " Average map  for " + n[k] + " recommendations: " + avgMap[k] + "\n";
            for (int k = 0; k < n.length; k++) printString += " Average arhr for " + n[k] + " recommendations: " + avgArhr[k] + "\n";
            for (int k = 0; k < n.length; k++) printString +=  " Average hr for " + m[k] + " recommendations: " + avgHr[k] + "\n";
            for (int k = 0; k < n.length; k++) printString += " Average precison for " + m[k] + " recommendations: " + avgPrecision[k] + "\n";


            System.out.print(printString);
            fw.append(printString);

            fw.flush();
            fw.close();
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
            System.exit(1);
        }
    }

    /*public void combinedEvaluator(Recommender rs, String[] trainingFiles, String[] testFiles) {
        int[] n = {10,20,30}; //recs for map
        int[] m = {3,5,10}; //recs for hr
        int numRecs = 30;
        long startTime;
        long endTime;
        double[] avgMap = {0,0,0};
        double avgTrainTime = 0;
        double avgRecTime = 0;
        double[] avgHr = {0,0,0};
        String printString;
        int nFolds = trainingFiles.length;

        try {
            FileWriter fw = new FileWriter(new File("test-results.txt"), true);

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
                double trainingTime = endTime - startTime;
                printString = "Fold " + (i+1) + "\nTime used for training recommender:" + trainingTime + "\n";
                System.out.print(printString);
                fw.append(printString);
                avgTrainTime += trainingTime / nFolds;

                HashMap<Integer, HashMap<Integer, Double>> testData = readTestData(testFiles[i]);
                double[] sumAp = {0, 0, 0};
                double avgTime = 0;
                int users = testData.size();
                double[] map = new double[3];
                double[] hr = new double[3];
                double[] matches = {0, 0, 0};

                for (int userId : testData.keySet()) {
                    startTime = System.nanoTime();
                    int[] recommendedItems = rs.recommend(userId, numRecs);
                    endTime = System.nanoTime();
                    avgTime += (endTime - startTime) / users;
                    //if (isMatch(recommendedItems, testData.get(userId).keySet())) matches++;
                    //double ap = averagePrecision(recommendedItems, testData.get(userId).keySet());
                    //System.out.println(ap);
                    //sumAp +=  ap;
                    //System.out.println("Recs for user "+userId);

                    //updates measures for the different recommendations lengths
                    for (int k = 0; k < n.length; k++) {
                        sumAp[k] += averagePrecision(Arrays.copyOfRange(recommendedItems, 0, n[k]), testData.get(userId).keySet());//recommendedItems, testData.get(userId).keySet());
                        if (isMatch(Arrays.copyOfRange(recommendedItems, 0, m[k]), testData.get(userId).keySet()))
                            matches[k]++;
                    }


                }
                //printString = "Fold " + i + ":";
                //System.out.println(printString);
                //fw.append(printString);

                //for each of the reommendation lengths, calculates average for this fold, plus adding values to the scores for all folds
                for (int k = 0; k < n.length; k++) {
                    map[k] = (double) sumAp[k] / users;
                    avgMap[k] += map[k] / nFolds;
                    hr[k] = matches[k] / users;
                    avgHr[k] += hr[k] / nFolds;
                    //printString = "Map for " + n[k] + "recommendations: " + map[k] + "\n" + "Hr for " + n[k] + "recommendations: " + hr[k];
                    //System.out.println("Map for " + n[k] + "recommendations: " + map[k]);
                    //System.out.println("Hr for " + n[k] + "recommendations: " + hr[k]);
                }

                printString = "Avg time producing rec: " + avgTime + "\n";
                System.out.print(printString);
                fw.append(printString);
                avgRecTime += avgTime / nFolds;
            }
            for (int k = 0; k < n.length; k++) {
                printString = "Average map (all folds) for " + n[k] + " recommendations: " + avgMap[k] + "\n" +
                        "Average hr (all folds) for " + m[k] + " recommendations: " + avgHr[k] + "\n";
                System.out.print(printString);
                fw.append(printString);
            }

            printString = "Average train time (all folds): " + avgTrainTime + "\n" + "Average test time (all folds): " + avgRecTime + "\n";
            System.out.print(printString);
            fw.append(printString);

            fw.flush();
            fw.close();
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
            System.exit(1);
        }
    }*/


    public void evaluateCrossFold(Recommender[] rss, String folder, int from, int to) {
        //String[] trainMl = {"data/ab10/train1", "data/ab10/train2","data/ab10/train3","data/ab10/train4","data/ab10/train5"};
        //String[] testMl = {"data/ab10/test1", "data/ab10/test2","data/ab10/test3","data/ab10/test4","data/ab10/test5"};


        int numFolds = to - from + 1;

        String[] trainFiles = new String[numFolds];
        String[] testFiles = new String[numFolds];

        for (int i = 0; i < numFolds; i++) {
            trainFiles[i] = folder + "/train" + (from + i);
            testFiles[i] = folder + "/test" + (from + i);
            System.out.println(trainFiles[i] +", " + testFiles[i]);
        }

        try {
            FileWriter fw = new FileWriter(new File("test-results.txt"), true);
            fw.write("\nTest of dataset in folder: " + folder +"\n");
            fw.flush();
            fw.close();
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
            System.exit(1);
        }

        //Recommender[] rss = getTopNRecommenders(tagFile, titleFile);

        for (Recommender rs : rss) {
            rs.initialize();
            //map(rs, trainFiles, testFiles, 10);
            combinedEvaluator(rs, trainFiles, testFiles);
            rs.close();
        }

    }

    public Recommender[] getTopNRecommenders(String tagFile, String titlesFile) {
        ModelBasedRecommender mbr = new ModelBasedRecommender();
        ItemBasedRecommender ibr = new ItemBasedRecommender();
        ContentBasedRecommender cbr = new ContentBasedRecommender(tagFile, titlesFile);
        BaselineRecommender br = new BaselineRecommender();

        //cbr.initialize();
        //ibr.initialize();
        //mbr.initialize();
        Recommender[] rss = {mbr, ibr, cbr, br};
        return rss;
    }

    //5 fold cross validation all-but-10 MAP - MSD+BX+ML arbitrart value ratings10m
    public void evalAb10() {
        //Tests run 28.03.17
        Recommender[] rss = getTopNRecommenders("data/ml6k/tags", "data/ml6k/titles");
        evaluateCrossFold(rss, "data/ml6k/ab10", 1, 5);

        rss = getTopNRecommenders("data/msd6k/tags", "data/msd6k/titles");
        evaluateCrossFold(rss, "data/msd6k/ab10", 1, 5);

        rss = getTopNRecommenders("data/bx6k/item-tags-reduced", "data/bx6k/titles");
        evaluateCrossFold(rss, "data/bx6k/ab10", 1, 5);
    }

    //5 fold cross validation all-but-10 MAP - MSD+BX+ML binarized ratings10m
    public void evalBab10() {
        Recommender[] rss = getTopNRecommenders("data/ml6k/tags", "data/ml6k/titles");
        evaluateCrossFold(rss, "data/ml6k/bab10", 1, 5);

        rss = getTopNRecommenders("data/msd6k/tags", "data/msd6k/titles");
        evaluateCrossFold(rss, "data/msd6k/bab10", 1, 5);

        rss = getTopNRecommenders("data/bx6k/item-tags-reduced", "data/bx6k/titles");
        evaluateCrossFold(rss, "data/bx6k/bab10", 1, 5);
    }

    public void evalGiven(int n) {
        String folderMl = "data/ml6k/g" + n;
        Recommender[] rss = getTopNRecommenders("data/ml6k/tags", "data/ml6k/titles");
        evaluateCrossFold(rss, folderMl, 1, 5);

        String folderMsd = "data/msd6k/g" + n;
        rss = getTopNRecommenders("data/msd6k/tags", "data/msd6k/titles");
        evaluateCrossFold(rss, folderMsd, 1, 5);

        String folderBx = "data/bx6k/g" + n;
        rss = getTopNRecommenders("data/bx6k/item-tags-reduced", "data/bx6k/titles");
        evaluateCrossFold(rss, folderBx, 1, 5);
    }

    public void testScalability(Recommender[] rss, String ratingFile) {
        long startTime;
        long endTime;
        int n = 10;

        //reads training file into datastructure, so we have users to test on (we will not measure
        // accuracy so it does not matter that train file is the same as the test file)
        HashMap<Integer, HashMap<Integer, Double>> testData = readTestData(ratingFile);


        for (Recommender rs : rss) {
            long overallAvgTrain = 0;
            long overallAvgTest = 0;

            System.out.println(rs.getInfo());
            for (int i = 0; i < 5; i++ ) {
                long trainTime;
                long avgTestTime = 0;
                rs.initialize();
                startTime = System.nanoTime();
                rs.update(ratingFile); //trains recommender with training file
                endTime = System.nanoTime();
                trainTime = endTime - startTime;


                int x = 0;
                int max = 100;
                for (Integer userId : testData.keySet()) {
                    //System.out.println(userId);
                    startTime = System.nanoTime();
                    int[] recommendedItems = rs.recommend(userId, n);
                    endTime = System.nanoTime();
                    avgTestTime += (endTime - startTime) / max;
                    if (x++ == max) break;
                }
                rs.close();
                System.out.println("Iteration: " + i + ", train time: " + trainTime + ", avgTestTime: " + avgTestTime);
                overallAvgTest += avgTestTime/5;
                overallAvgTrain += trainTime/5;
            }

            System.out.println("Avg results: Train time: " + overallAvgTrain +", test time: " + overallAvgTest);
        }


    }

}
