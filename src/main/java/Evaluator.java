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
        String[] testFiles = {""};
        eval.hitRate(sr, trainingFiles, testFiles);
        SparkRecommender.stopSparkContext(); //make instance variable + probably not make new context for each test

    }

    public void hitRate(Recommender rs, String[] trainingFiles, String[] testFiles) {
        if (trainingFiles.length != testFiles.length) {
            System.out.println("Not equal numbers of trainingFiles and testFiles");
            return;
        }

        for (int i = 0; i < trainingFiles.length; i++) {
            System.out.println("Testing with file " + i + ".");
            rs.update(trainingFiles[i]);
            int[] recs = rs.recommend(1, 10);
            System.out.println("Recs for user 1");
            for (int x : recs) {

                System.out.println(x);
            }
        }
    }
}
