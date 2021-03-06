package recommender.spark;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.mllib.recommendation.ALS;
import org.apache.spark.mllib.recommendation.MatrixFactorizationModel;
import org.apache.spark.mllib.recommendation.Rating;
import recommender.Recommender;
import scala.Tuple2;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;


/**
 * Created by simen on 2/8/17.
 * Model based collaborative filtering done with an alternating least square matrix factorization algorithm,
 * implemented with Spark's MLlib.
 */

public class ModelBasedRecommender implements Recommender, Serializable {
    private static SparkConf conf;
    private static JavaSparkContext sc;
    private static MatrixFactorizationModel model;
    private static List<Tuple2<Integer, Iterable<Rating>>> ratingPerUser;
    private int rank;
    private int iterations;
    private double lambda;
    private double alpha;



    //constructor without parameters - using default values
    public ModelBasedRecommender() {
        setParameters(10,10,0.01,1.0);
    }

    //constructor with parameters
    public ModelBasedRecommender(int rank, int iterations, double lambda, double alpha) {
        setParameters(rank, iterations, lambda, alpha);
    }


    //Method used for initiating Spark-context and -configuration. Uses defaults parameters for the recommender
    public void initialize() {
        conf = new SparkConf().
                setAppName("Model-based recommender").
                setMaster("local");
        sc = new JavaSparkContext(conf);
        sc.setLogLevel("WARN");
        //options for Level include: all, debug, error, fatal, info, off, trace, trace_int, warn
    }

    //method for updating parameters
    public void setParameters(int rank, int iterations, double lambda, double alpha) {
        this.rank = rank;
        this.iterations = iterations;
        this.lambda = lambda;
        this.alpha = alpha;
    }

    //Train the model-based recommender with the trainingfile, so it's ready to produce recommendation
    public void update(String trainingFile) {

        //reads textfile into a String-RDD, with one String for each line, i.e for each <userid, itemid,rating>triple
        JavaRDD<String> data = sc.textFile(trainingFile);

        //Makes RDD of Ratings
        JavaRDD<Rating> ratings = data.map(
                new Function<String, Rating>() {
                    @Override
                    public Rating call(String line) {
                        String[] parts = line.split("\t");
                        return new Rating(Integer.parseInt(parts[0]),
                                Integer.parseInt(parts[1]), Double.parseDouble(parts[2]));
                    }
                }
        );

        model = ALS.trainImplicit(JavaRDD.toRDD(ratings), rank, iterations, lambda, alpha); // Trains the ALS-model

        //Because Spark recommends already rated items, we want to filer out these items.
        //Therefore, we want to keep each users ratings.
        //Groups the ratings for each user into an Iterable of Ratings
        JavaPairRDD<Integer, Iterable<Rating>> ratingsGrouped = ratings.groupBy(
                (Function<Rating, Integer>) r -> r.user()
        );
        ratingPerUser = ratingsGrouped.collect(); //Converts from RDD of tuples to a list of tuples
    }



    //Gets the Ratings for a user contained in an Iterator
    public Iterator<Rating> getRatingHistory(int userId) {

        //iterates over the <userid, Iterable<Rating>-tuples
        for (Tuple2<Integer, Iterable<Rating>> t : ratingPerUser) {
            //if t._1(), i.e. the key of the tuple, equals userId we have found the correct user,
            // and returns the iterator in the tuple
            if (t._1() == userId) return t._2().iterator();
        }
        return null;
    }

    //Method that recommends num itemIds to user with this userId
    public int[] recommend(int userId, int num) {
        Iterator<Rating> iterator = getRatingHistory(userId);

        //Adds the Ratings in the iterator to a hashmap, to more effectively filter out already rated items
        HashMap<Integer, Integer> hm = new HashMap<>();
        while (iterator.hasNext()) {
            Rating r = iterator.next();
            hm.put(r.product(), r.product());
        }

        Rating[] ratings = model.recommendProducts(userId, num+hm.size()); //get recommendations from model

        //returns an array with the num recommendations with highest score that are not already rated
        int i = 0;
        int[] recommendedItems = new int[num];
        for (Rating r : ratings) {
            if (!hm.containsKey(r.product())) recommendedItems[i++] = r.product();
            if (i == num) break;
        }
        return recommendedItems;
    }

    //shuts down the spark context
    public static void stopSparkContext() {
        sc.stop();
    }

    //returns info about this recommender and its parameters
    public String getInfo() {
        return "Model-based Collaborative filtering | r = " + rank
                +" i = "+ iterations + " l = " + lambda + " a = " + alpha;
    }

    //shuts down spark config
    public void close() {
        sc.stop();
    }
}
