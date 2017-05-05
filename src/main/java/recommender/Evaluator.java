package recommender;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;


import recommender.lenskit.ContentBasedRecommender;
import recommender.lenskit.ItemBasedRecommender;
import recommender.nonframework.BaselineRecommender;
import recommender.spark.ModelBasedRecommender;


/**
 * Created by simen on 3/16/17.
 * Class used for evaluating Recommenders in terms of accuracy (MAP, ARHR, HR, precision) and scalability (training and
 * prediction time).
 */
public class Evaluator {

    public static void main(String[] args) {
        Evaluator eval = new Evaluator();


        /*The accuracy tests run for the thesis - gives MAP, ARHR, HR and MAP + training and prediction times
        for all of our 4 implemented recommenders on MovieLens, Million Song and Book-Crossing dataset.
        */
        //eval.evalBab10(); //using binary ratings, all-but-10
        //eval.evalAb10(); //using orignal ratings, all-but-10
        //eval.evalGiven(2); //using binary ratings, given-2
        //eval.evalGiven(5); //using binary ratings, given-5
        //eval.evalGiven(8); //using binary ratings, given-8


        /*The scalability tests run for the thesis for the 4 algorithms on three subsets of MovieLens:
         */
        //Recommender[] rss;
        //rss = eval.getTopNRecommenders("data/ml10m/tags100k", "data/ml10m/titles100k"); //100K subset
        //eval.testScalability(rss, "data/ml10m/ratings100k");

        //rss = eval.getTopNRecommenders("data/ml10m/tags1m", "data/ml10m/titles1m"); //1M subset
        //eval.testScalability(rss, "data/ml10m/ratings1m");

        //rss = eval.getTopNRecommenders("data/ml10m/tags5m", "data/ml10m/titles5m");//5M subset
        //eval.testScalability(rss, "data/ml10m/ratings5m");
    }


    //Method that computes the hit-rate of a recommender rs, for a set of training-files and a set of test files.
    //Uses a recommendation list size of n
    public void hitRate(Recommender rs, String[] trainingFiles, String[] testFiles, int n) {
        if (trainingFiles.length != testFiles.length) {
            System.out.println("Not equal numbers of trainingFiles and testFiles");
            return;
        }

        double avgHr = 0;
        double avgTime = 0;

        //Repeats for all of the trainingfiles. i is the fold nr
        for (int i = 0; i < trainingFiles.length; i++) {
            rs.update(trainingFiles[i]); //trains recommender with training file
            HashMap<Integer, HashMap<Integer, Double>> testData = readTestData(testFiles[i]); //stores ratings in a 2d hashmap
            int matches = 0;
            int users = testData.size();
            double hr;

            //checks for each user in test set if there is a match in the recommendations given to him/her
            for (int userId : testData.keySet()) {
                int[] recommendedItems = rs.recommend(userId, n);
                if (isMatch(recommendedItems, testData.get(userId).keySet())) matches++; //if match - increases counter
            }

            hr = (double) matches / users;
            avgHr += hr/trainingFiles.length;
            System.out.println("Hr fold nr " + i + ": " + hr);
        }

        System.out.println("Average hr all folds: " + avgHr);
    }


    //Returns true if one of the recommended items are in the set of relevant items
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


    //Measures MAP for a recommender rs on a set of training files and a set of test files.
    //The results are written to the file test-results.txt
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

                //trains recommender with training file and measures time used
                startTime = System.nanoTime();
                rs.update(trainingFiles[i]);
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

                //Computes average precision for each user in test set and measures time used for making recommendations
                for (int userId : testData.keySet()) {
                    startTime = System.nanoTime();
                    int[] recommendedItems = rs.recommend(userId, n);
                    endTime = System.nanoTime();
                    avgTime += (endTime - startTime) / users;
                    sum += averagePrecision(recommendedItems, testData.get(userId).keySet());
                }

                //computes map in this fold
                map = (double) sum / users;
                printString = " Map: " + map + "\n Avg time producing rec: " + avgTime + "\n";
                fw.append(printString);
                System.out.print(printString);

                avgMap += map / nFolds;
                avgRecTime += avgTime / nFolds;
            }

            //computes average map over all folds
            printString = "Data all folds: \n Avg map: " + avgMap + "\n Avg train time: " +
                    avgTrainTime + "\n Avg test time: " + avgRecTime + "\n";
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


    //Method that measures average precison for an ordered list of recommended items and a set of relevant items for a user
    public double averagePrecision(int[] recommendedItems, Set<Integer> relevantItems) {
        double rel = 0;
        double sum = 0;
        double ap;
        int div;

        //checks for each recommended item if it is relevant.
        //if relevant recommendation - add proportion of relevant recs in the i first recs to sum
        for (int i = 0; i < recommendedItems.length; i++) {
            for (int relevantId : relevantItems) {
                if (recommendedItems[i] == relevantId) {
                    sum += (++rel / (i+1));
                }
            }
        }
        if (rel == 0) return 0;

        //gets ap by dividing the sum by the smallest of recommendation list-size and the number of relevant items
        if (recommendedItems.length < relevantItems.size()) div = recommendedItems.length;
        else div = relevantItems.size();
        ap = sum / div;

        return ap;
    }


    //Measures the precison of a recommendation list for a user with a set of relevant items
    public double precision(int[] recommendedItems, Set<Integer> relevantItems) {
        double rel = 0;

        //counts number of relevant recs and divides by number of items in the recommendation list
        for (int i = 0; i < recommendedItems.length; i++) {
            for (int relevantId : relevantItems) {
                if (recommendedItems[i] == relevantId) {
                    rel++;
                }
            }
        }
        return rel/recommendedItems.length;
    }


    //Measures reciprocal hit rate for a recommendation list and a set of relevant items
    public double rhr(int[] recommendedItems, Set<Integer> relevantItems) {
        double rhr;

        //finds first relevant item in recommendation list and returns 1 divided by this index
        for (int i = 0; i < recommendedItems.length; i++) {
            for (int relevantId : relevantItems) {
                if (recommendedItems[i] == relevantId) {
                    rhr = (double) 1 / (i+1);
                    return rhr;
                }
            }
        }
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


    //Combined evaluator that measures MAP, ARHR, HR and precision for a recommender rs on a set of training files and a set of test files
    //MAP and ARHR are measured with recommendation list size 10,20,30,40 and 50, while hr and precision are measured with list size
    //of 3,5,10,20 and 30. Results are both written to file and to terminal
    public void combinedEvaluator(Recommender rs, String[] trainingFiles, String[] testFiles) {
        int[] n = {10,20,30,40,50}; //recs for map, arhr
        int[] m = {3,5,10,20,30}; //recs for hr, precision
        int numRecs = 500;
        long startTime;
        long endTime;
        double avgTrainTime = 0;
        double avgRecTime = 0;
        double[] avgMap = {0,0,0,0,0};
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

                //Trains recommender and measures time used for this
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

                //produces recommendation for each user, measure time used and updates the different measures
                for (int userId : testData.keySet()) {
                    startTime = System.nanoTime();
                    int[] recommendedItems = rs.recommend(userId, numRecs);
                    endTime = System.nanoTime();
                    avgTime += (endTime - startTime) / users;

                    //updates measures for the different recommendations lengths
                    for (int k = 0; k < n.length; k++) {
                        sumRhr[k] += rhr(Arrays.copyOfRange(recommendedItems, 0, n[k]), testData.get(userId).keySet());
                        sumAp[k] += averagePrecision(Arrays.copyOfRange(recommendedItems, 0, n[k]), testData.get(userId).keySet());//recommendedItems, testData.get(userId).keySet());
                        if (isMatch(Arrays.copyOfRange(recommendedItems, 0, m[k]), testData.get(userId).keySet()))
                            matches[k]++;
                        sumPrecision[k] += precision(Arrays.copyOfRange(recommendedItems, 0, m[k]), testData.get(userId).keySet());
                    }
                }

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


    //Method that evaluates a set of recommenders with a cross-fold approach for the training and test files in folder.
    //from and to indicates the range of training/test sets that should be used.
    //Uses combinedEvaluator() to evaluate the recommender
    public void evaluateCrossFold(Recommender[] rss, String folder, int from, int to) {

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


    //Method that returns all four of our implemented recommenders
    public Recommender[] getTopNRecommenders(String tagFile, String titlesFile) {
        ModelBasedRecommender mbr = new ModelBasedRecommender();
        ItemBasedRecommender ibr = new ItemBasedRecommender();
        ContentBasedRecommender cbr = new ContentBasedRecommender(tagFile, titlesFile);
        BaselineRecommender br = new BaselineRecommender();
        Recommender[] rss = {mbr, ibr, cbr, br};
        return rss;
    }


    //5 fold cross validation all-but - with various measures
    public void evalAb10() {
        Recommender[] rss = getTopNRecommenders("data/ml6k/tags", "data/ml6k/titles");
        evaluateCrossFold(rss, "data/ml6k/ab10", 1, 5);

        rss = getTopNRecommenders("data/msd6k/tags", "data/msd6k/titles");
        evaluateCrossFold(rss, "data/msd6k/ab10", 1, 5);

        rss = getTopNRecommenders("data/bx6k/item-tags-reduced", "data/bx6k/titles");
        evaluateCrossFold(rss, "data/bx6k/ab10", 1, 5);
    }


    //5 fold cross validation all-but - with various measures
    public void evalBab10() {
        Recommender[] rss = getTopNRecommenders("data/ml6k/tags", "data/ml6k/titles");
        evaluateCrossFold(rss, "data/ml6k/bab10", 1, 5);

        rss = getTopNRecommenders("data/msd6k/tags", "data/msd6k/titles");
        evaluateCrossFold(rss, "data/msd6k/bab10", 1, 5);

        rss = getTopNRecommenders("data/bx6k/item-tags-reduced", "data/bx6k/titles");
        evaluateCrossFold(rss, "data/bx6k/bab10", 1, 5);
    }


    //5 fold cross validation - given-n - with various measures
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


    //Method that takes average training and prediciton time for each recommender in the set recommenders for a rating
    // file. To get more reliable data, we repeat the training and predictions 5 times (and for each repetition we recommend
    // items to 100 users).
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


                int x = 0; //counter
                int max = 100; //number of users to recommend items for
                for (Integer userId : testData.keySet()) {
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
