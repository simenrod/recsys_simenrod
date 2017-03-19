package recommender;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.mllib.recommendation.ALS;
import org.apache.spark.mllib.recommendation.MatrixFactorizationModel;
import org.apache.spark.mllib.recommendation.Rating;
import scala.Tuple2;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;


/**
 * Created by simen on 2/8/17.
 */

//lag ny klasse med en del av innholdet i recommender.lenskit.ImplicitRecommender -> dvs. det som trengs for aa lage recs for brukere - saa man kan taste inn bruker-id
// og faa recs
//kan vaere lurt aa skille ut Ratings-delen som en egen metode (feks parseRatings()) og updateModel -> saa den kan oppdateres underveis.
//Burde skifte navn til ImplALS ellerno
public class SparkRecommender implements Recommender, Serializable {
    private static SparkConf conf;
    private static JavaSparkContext sc;
    private static MatrixFactorizationModel model;
    private static List<Tuple2<Object, Iterable<Rating>>> ratingPerUser;
    private int rank;
    private int iterations;
    private double lambda;
    private double alpha;


    public static void main(String[] args) {
        //init();
        //recommender.SparkRecommender sr = new recommender.SparkRecommender();
        SparkRecommender r = new SparkRecommender();
        r.init();
        //sr.init2();
        /*int x = 1; //teller
        Rating[] predictionsForUser = model.recommendProducts(1, 10);
        for (Rating rating : predictionsForUser) {
            System.out.println("Nr. " + x++ + " rec for user " + rating.user() + ": " + rating.product() + "  with score: "+ rating.rating());
        }*/

        //interactiveRecs(); // UNCOMMENT LATER
        /*for (Rating rating : getRecsForUser(1, 20)) {
            System.out.println("ID: " + rating.product() + ", score: " + rating.rating());
        }*/

        /*Rating[] recs = getRecsForUserExcludeRatedItems(1, 10);
        System.out.println("Recommendations for user 1");
        for (Rating r : recs) {
            System.out.println("\t" + r.product()+ ", score " + r.rating());
        }
        System.out.println(recs.length);*/

        //writeTopNToFile();

        stopSparkContext();
    }

    public void init() {
        conf = new SparkConf().
                setAppName("Java Ranking Metrics Example").
                setMaster("local");
        sc = new JavaSparkContext(conf);
        sc.setLogLevel("WARN");
        //options for Level include: all, debug, error, fatal, info, off, trace, trace_int, warn
        // $example on$
        //String path = "sample_movielens_ratings.txt";
        //String path = "/home/simen/Documents/test/recommender.lenskit/ratings2.csv";
        //String path = "/home/simen/Documents/datasett/movielens100k.data";
        //String path = "/home/simen/Documents/datasett/crossfold-movielens100k.data/training"; //ikke binary - men gjoer om til
        String path= "/home/simen/Documents/datasett/crossfold-movielens-binary/training";
        JavaRDD<String> data = sc.textFile(path);
        JavaRDD<Rating> ratings = data.map(
                new Function<String, Rating>() {
                    @Override
                    public Rating call(String line) {
                        //String[] parts = line.split("::");
                        String[] parts = line.split("\t");
                        //String[] parts = line.split(",");
                        /*return new Rating(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), Double
                                .parseDouble(parts[2]) - 2.5);*/
                        double ratingValue = Double.parseDouble(parts[2]);
                        if (ratingValue > 0) {
                            ratingValue = 1;
                        }
                        else {
                            ratingValue = 0;
                        }
                        return new Rating(Integer.parseInt(parts[0]),
                                Integer.parseInt(parts[1]), ratingValue);

                    }
                }
        );
        ratings.cache();


        //finn alle ratings for hver bruker her -> bruke list sin removeAll fra recommendationslista
        //boer nok forst groupby user som i userMovies i annen fil
        // Group ratings by common user //LITT USIKKER -> tror gjoer om fra Rating-map til <id, [Rating]>-tupler for hver bruker
        JavaPairRDD<Object, Iterable<Rating>> userMovies = ratings.groupBy( //grupperer ratings etter bruker
                new Function<Rating, Object>() {
                    @Override
                    public Object call(Rating r) {
                        return r.user();
                    }
                }
        );

        JavaPairRDD<Object, Iterable<Rating>> userMovies2 = userMovies.sortByKey(); //sorterer brukerne fra lav til stoerst

        //List<Rating> rlist = ratings.collect(); //faar da en liste med alle ratings (100K pga ml100k)
        //int x = 0;
        //for (Rating r : rlist) {
        //    System.out.println(x++);
        //}
        ratingPerUser = userMovies2.collect(); //gjoer om dataene til en liste der indeks 0 er en Iterable for Ratingene til bruker 1, indeks1->bruker2,osv.
        //gir et tuppel for hver bruker med en iterable for ratingene

        /*for (Tuple2<Object, Iterable<Rating>> tup : ratingPerUser) {
            //System.out.println(x++);
            Iterator<Rating> iterator =  tup._2().iterator();
            Rating r = iterator.next();
            System.out.println("User: " + r.user());
            System.out.print(r.product() + " " + r.rating() + " ");

            while (iterator.hasNext()) {
                r = iterator.next();
                System.out.print(r.product() + " " + r.rating() + " ");
            }
            System.out.println();
        }*/

        /*Tuple2<Object, Iterable<Rating>> tup = ratingPerUser.get(0); //henter bruker 1 sine ratings
        Iterator<Rating> iterator =  tup._2().iterator();
        Rating r = iterator.next();
        System.out.println("User " + r.user() + " sine ratings:");
        while (iterator.hasNext()) {
            r = iterator.next();
            System.out.println(r.product() + " " + r.rating() + " ");
        }
        System.out.println();

        //////////*/


        // Train an ALS model
        //final MatrixFactorizationModel model = ALS.train(JavaRDD.toRDD(ratings), 10, 10, 0.01);
        //trainImplicit(RDD<Rating> ratings, int rank, int iterations, double lambda, double alpha)
        model = ALS.trainImplicit(JavaRDD.toRDD(ratings), 10, 10, 0.01, 0.01);
    }


    public void initialize() {
        conf = new SparkConf().
                setAppName("Model-based recommender").
                setMaster("local");
        sc = new JavaSparkContext(conf);
        sc.setLogLevel("WARN");
        //options for Level include: all, debug, error, fatal, info, off, trace, trace_int, warn
        rank = 10;
        iterations = 10;
        lambda = 0.01;
        alpha = 0.01;
    }

    public void initialize(int rank, int iterations, double lambda, double alpha) {
        conf = new SparkConf().
                setAppName("Model-based recommender").
                setMaster("local");
        sc = new JavaSparkContext(conf);
        sc.setLogLevel("WARN");
        this.rank = rank;
        this.iterations = iterations;
        this.lambda = lambda;
        this.alpha = alpha;
    }

    /*public static void init() {
        conf = new SparkConf().
                setAppName("Java Ranking Metrics Example").
                setMaster("local");
        sc = new JavaSparkContext(conf);
        // $example on$
        //String path = "sample_movielens_ratings.txt";
        //String path = "/home/simen/Documents/test/recommender.lenskit/ratings2.csv";
        //String path = "/home/simen/Documents/datasett/movielens100k.data";
        //String path = "/home/simen/Documents/datasett/crossfold-movielens100k.data/training"; //ikke binary - men gjoer om til
        String path= "/home/simen/Documents/datasett/crossfold-movielens-binary/training";
        JavaRDD<String> data = sc.textFile(path);
        JavaRDD<Rating> ratings = data.map(
                new Function<String, Rating>() {
                    @Override
                    public Rating call(String line) {
                        //String[] parts = line.split("::");
                        String[] parts = line.split("\t");
                        //String[] parts = line.split(",");
                        */
                        /*double ratingValue = Double.parseDouble(parts[2]);
                        if (ratingValue > 0) {
                            ratingValue = 1;
                        }
                        else {
                            ratingValue = 0;
                        }
                        return new Rating(Integer.parseInt(parts[0]),
                                Integer.parseInt(parts[1]), ratingValue);

                    }
                }
        );
        ratings.cache();


        //finn alle ratings for hver bruker her -> bruke list sin removeAll fra recommendationslista
        //boer nok forst groupby user som i userMovies i annen fil
        // Group ratings by common user //LITT USIKKER -> tror gjoer om fra Rating-map til <id, [Rating]>-tupler for hver bruker
        JavaPairRDD<Object, Iterable<Rating>> userMovies = ratings.groupBy( //grupperer ratings etter bruker
                new Function<Rating, Object>() {
                    @Override
                    public Object call(Rating r) {
                        return r.user();
                    }
                }
        );

        JavaPairRDD<Object, Iterable<Rating>> userMovies2 = userMovies.sortByKey(); //sorterer brukerne fra lav til stoerst

        //List<Rating> rlist = ratings.collect(); //faar da en liste med alle ratings (100K pga ml100k)
        //int x = 0;
        //for (Rating r : rlist) {
        //    System.out.println(x++);
        //}
        ratingPerUser = userMovies2.collect(); //gjoer om dataene til en liste der indeks 0 er en Iterable for Ratingene til bruker 1, indeks1->bruker2,osv.
        //gir et tuppel for hver bruker med en iterable for ratingene






        // Train an ALS model
        //final MatrixFactorizationModel model = ALS.train(JavaRDD.toRDD(ratings), 10, 10, 0.01);
        model = ALS.trainImplicit(JavaRDD.toRDD(ratings), 10, 10, 0.01, 0.01);
    }*/


    public void update(String trainingFile) {
        //String path= "/home/simen/Documents/datasett/crossfold-movielens-binary/training";
        JavaRDD<String> data = sc.textFile(trainingFile);
        JavaRDD<Rating> ratings = data.map(
                new Function<String, Rating>() {
                    @Override
                    public Rating call(String line) {
                        //String[] parts = line.split("::");
                        String[] parts = line.split("\t");
                        //String[] parts = line.split(",");
                        /*return new Rating(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), Double
                                .parseDouble(parts[2]) - 2.5);*/
                        double ratingValue = Double.parseDouble(parts[2]);
                        if (ratingValue > 0) {
                            ratingValue = 1;
                        }
                        else {
                            ratingValue = 0;
                        }
                        return new Rating(Integer.parseInt(parts[0]),
                                Integer.parseInt(parts[1]), ratingValue);

                    }
                }
        );
        ratings.cache();


        //finn alle ratings for hver bruker her -> bruke list sin removeAll fra recommendationslista
        //boer nok forst groupby user som i userMovies i annen fil
        // Group ratings by common user //LITT USIKKER -> tror gjoer om fra Rating-map til <id, [Rating]>-tupler for hver bruker
        JavaPairRDD<Object, Iterable<Rating>> userMovies = ratings.groupBy( //grupperer ratings etter bruker
                new Function<Rating, Object>() {
                    @Override
                    public Object call(Rating r) {
                        return r.user();
                    }
                }
        );

        JavaPairRDD<Object, Iterable<Rating>> userMovies2 = userMovies.sortByKey(); //sorterer brukerne fra lav til stoerst

        //List<Rating> rlist = ratings.collect(); //faar da en liste med alle ratings (100K pga ml100k)
        //int x = 0;
        //for (Rating r : rlist) {
        //    System.out.println(x++);
        //}
        ratingPerUser = userMovies2.collect(); //gjoer om dataene til en liste der indeks 0 er en Iterable for Ratingene til bruker 1, indeks1->bruker2,osv.
        //gir et tuppel for hver bruker med en iterable for ratingene

        /*for (Tuple2<Object, Iterable<Rating>> tup : ratingPerUser) {
            //System.out.println(x++);
            Iterator<Rating> iterator =  tup._2().iterator();
            Rating r = iterator.next();
            System.out.println("User: " + r.user());
            System.out.print(r.product() + " " + r.rating() + " ");

            while (iterator.hasNext()) {
                r = iterator.next();
                System.out.print(r.product() + " " + r.rating() + " ");
            }
            System.out.println();
        }*/

        /*Tuple2<Object, Iterable<Rating>> tup = ratingPerUser.get(0); //henter bruker 1 sine ratings
        Iterator<Rating> iterator =  tup._2().iterator();
        Rating r = iterator.next();
        System.out.println("User " + r.user() + " sine ratings:");
        while (iterator.hasNext()) {
            r = iterator.next();
            System.out.println(r.product() + " " + r.rating() + " ");
        }
        System.out.println();

        //////////*/


        // Trains the ALS-model
        model = ALS.trainImplicit(JavaRDD.toRDD(ratings), rank, iterations, lambda, alpha);
    }

    public int[] recommend(int userId, int num) {
        Tuple2<Object, Iterable<Rating>> tup = ratingPerUser.get(userId-1); //henter brukeren sine ratings //dette boer endres -> virker skjoert
        //TROR LURT AA BRUKE lookup(userId) i stedet - men da maa man kanskje bruke groupByKey foerst - undersoek naermere
        //Tuple2<Object, Iterable<Rating>> tup = ratingPerUser.lookup(userId);
        //Iterator<Rating> iterator =  tup._2().iterator();

        //Iterable<Rating> usersRatings = ratingPerUser.get(id-1);
        Rating[] ratings = model.recommendProducts(userId, num+10000);
        System.out.println(ratings.length);
        Rating[] finalRatings = new Rating[num]; //change to num

        //Rating r = iterator.next();
        // System.out.println("User " + r.user() + " - ratings removed:");
        int x = 0;
        int y = 0;
        for (Rating rating : ratings) {
            //System.out.println(rating.product());
            Boolean match = false;
            Iterator<Rating> iterator =  tup._2().iterator();
            while (iterator.hasNext()) {
                //System.out.println(rating.product());
                Rating r = iterator.next();

                //System.out.println("Checking " + r.product() + " against " + rating.product());
                if (r.product() == rating.product()) {
                    //System.out.println(r.product() + " " + r.rating() + " ");
                    y++;
                    //continue; //don't add this Rating to the finalRating because rated before
                    match = true;
                    break;
                }
            }
            if (!match) finalRatings[x++] = rating;
            if (x == num) break;
        }

        //System.out.println(finalRatings.length + ", fjernet: "+y);
        //System.out.println("Making recs for user " + );
        System.out.println("userid: "+ userId);
        int[] recommendedItems = new int[num];
        for (int i = 0; i < num; i++) {
            recommendedItems[i] = finalRatings[i].product();
            System.out.print(i);
        }
        System.out.println("");
        //fjern overlapp
        //return finalRatings;
        return recommendedItems;
    }

    public static Rating[] getRecsForUser(int id, int num) {
        Rating[] ratings = model.recommendProducts(id, num);
        return ratings;
        //Kan hvis nodvendig eksludere gitte items (feks alle sette/20 siste sette)
        //Kan også gjoere om til ekstern Prediction-klasse e.l. for aa ha felles Prediction med annen klasse.
    }

    public static Rating[] getRecsForUserExcludeRatedItems(int id, int num) {
        Tuple2<Object, Iterable<Rating>> tup = ratingPerUser.get(id-1); //henter brukeren sine ratings
        //Iterator<Rating> iterator =  tup._2().iterator();

        //Iterable<Rating> usersRatings = ratingPerUser.get(id-1);
        Rating[] ratings = model.recommendProducts(id, num+1000);
        System.out.println(ratings.length);
        Rating[] finalRatings = new Rating[ratings.length-1000];

        //Rating r = iterator.next();
        // System.out.println("User " + r.user() + " - ratings removed:");
        int x = 0;
        int y = 0;
        for (Rating rating : ratings) {
            //System.out.println(rating.product());
            Boolean match = false;
            Iterator<Rating> iterator =  tup._2().iterator();
            while (iterator.hasNext()) {
                //System.out.println(rating.product());
                Rating r = iterator.next();

                //System.out.println("Checking " + r.product() + " against " + rating.product());
                if (r.product() == rating.product()) {
                    //System.out.println(r.product() + " " + r.rating() + " ");
                    y++;
                    //continue; //don't add this Rating to the finalRating because rated before
                    match = true;
                    break;
                }
            }
            if (!match) finalRatings[x++] = rating;
            if (x == num) break;
        }

        System.out.println(finalRatings.length + ", fjernet: "+y);


        //fjern overlapp
        return finalRatings;
    }

    public static void stopSparkContext() {
        sc.stop();
    }

    public static void interactiveRecs(/*MatrixFactorizationModel model*/) {
        Scanner scan = new Scanner(System.in);
        System.out.println("Tast inn brukerID som det skal gis anbefalinger for. -1 for avslutt");
        int input = Integer.parseInt(scan.next());

        while (input != -1) {

            //List<ScoredId> recommendations = irec.recommend(input, 10);
            Rating[] predictionsForUser = model.recommendProducts(input, 10);

            System.out.println("10 best recommendations for user " + input + ": ");
            /*for (ScoredId si : recommendations) {
                System.out.println("ID: " + si.getId() + ", score:  " + si.getScore());
            }*/
            for (Rating rating : predictionsForUser) {
                System.out.println("ID: " + rating.product() + ", score: " + rating.rating());
            }

            System.out.println("\nTast inn brukerID som det skal gis anbefalinger for. -1 for avslutt");
            input = Integer.parseInt(scan.next());
        }
    }

    public static void writeTopNToFile() {
        //FORTSETT HER: Skriv top-n recs for hver bruker til fil
        //userid, itemid-rec 1, ..., itemid-rec n,
        try {
            FileWriter fw = new FileWriter(new File("spark-topn"));
            int users = 943;
            int n = 10;
            for (int i = 1; i <= users; i++) {
                fw.write(i+"\t");
                Rating[] recs = getRecsForUserExcludeRatedItems(i, n);
                for (Rating r : recs) {
                    fw.write(r.product() + "\t");
                }
                fw.write("\n");

            }
            fw.flush();
            fw.close();
        }
        catch(IOException ie) {
            //System.out.println("Error: "+ ie.stackTrace());
            ie.printStackTrace(System.out);
            System.exit(0);
        }

    }


}
