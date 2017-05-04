package dataset;

/**
 * Created by simen on 3/19/17.
 */
import java.io.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/*class User {
    HashMap<String,Rating> ratings10m;

    User() {
        ratings10m = new HashMap<String,Rating>();
    }
}*/

class Rating {
    private String user;
    private String item;
    private String rating;

    Rating(String user, String item, String rating) {
        this.user = user;
        this.item = item;
        this.rating = rating;
    }

    public String getUser() {
        return user;
    }

    public String getItem() {
        return item;
    }

    public String getRating() {
        return rating;
    }

    public String getString() {
        return user + "\t" + item + "\t" + rating + "\n";
    }
}

public class DataSplitter {
    public static void main(String[] args) {
        //leaveOneOut("movielens100k.data");
        //leaveOneOut("data/movielens/u.data", "data/movielens/leave_one_out", 5, "\t"); //not change leave_one_out for movielens
        //kFoldCrossValidationSets("data/movielens/u.data", "data/movielens/cross-val", 5, "\t", -1);
        //kFoldCrossValidationSets("data/bx/bx-ratings10m.csv", "data/bx/cross-val", 5, "\t", -1);
        //kFoldCrossValidationSets("data/ml6k/ratings10m", "data/ml6k/cross-val", 5, "::", 10, false);
        //kFoldCrossValidationSets("data/ml6k/ratings10m", "data/ml6k/ab10", 5, "::", 10, false);
        //kFoldCrossValidationSets("data/bx6k/ratings10m-transformed", "data/bx6k/ab10", 5, "\t", 10, false);
        //kFoldCrossValidationSets("data/msd6k/ratings10m-transformed", "data/msd6k/ab10", 5, "\t", 10, false);

        //MAYBE USE 3-6-9-12 instead (or 2-5-8-11)
        kFoldCrossValidationSets("data/msd6k/binarized-ratings10m", "data/msd6k/g2", 5, "\t", 2, true);
        kFoldCrossValidationSets("data/msd6k/binarized-ratings10m", "data/msd6k/g5", 5, "\t", 5, true);
        kFoldCrossValidationSets("data/msd6k/binarized-ratings10m", "data/msd6k/g8", 5, "\t", 8, true);

        kFoldCrossValidationSets("data/ml6k/binarized-ratings10m", "data/ml6k/g2", 5, "\t", 2, true);
        kFoldCrossValidationSets("data/ml6k/binarized-ratings10m", "data/ml6k/g5", 5, "\t", 5, true);
        kFoldCrossValidationSets("data/ml6k/binarized-ratings10m", "data/ml6k/g8", 5, "\t", 8, true);

        kFoldCrossValidationSets("data/bx6k/binarized-ratings10m", "data/bx6k/g2", 5, "\t", 2, true);
        kFoldCrossValidationSets("data/bx6k/binarized-ratings10m", "data/bx6k/g5", 5, "\t", 5, true);
        kFoldCrossValidationSets("data/bx6k/binarized-ratings10m", "data/bx6k/g8", 5, "\t", 8, true);
    }


    //Method that takes an input file inFile, reads it and writes it to train/test files nFolds times
    //in the directory outDirectory. For each user, one rating is written to test file, while
    //the rest of the ratings10m are written to the training file
    public static void leaveOneOut(String inFile, String outDirectory, int nFolds, String delimiter) {
        //2d data structure -> an outer hashmap with userids as keys, and hashmaps with ratings10m
        // for the given user as inner values
        /*HashMap<String,HashMap<String,Rating>> usersRatings = new HashMap<String,HashMap<String,Rating>>();

        try {
            BufferedReader br = new BufferedReader(new FileReader(inFile));
            String line = br.readLine();

            //reads in data to data structure
            while (line != null) {
                String[] words = line.split(delimiter);
                usersRatings.putIfAbsent(words[0], new HashMap<String, Rating>());
                usersRatings.get(words[0]).put(words[1], new Rating(words[0], words[1], words[2]));
                line = br.readLine();
            }*/
        HashMap<String,HashMap<String,Rating>> usersRatings = readRatingData(inFile, delimiter);

        try {

            //Repeats for all n folds
            for (int i = 1; i <= nFolds; i++) {
                FileWriter writeTrain = new FileWriter(new File(outDirectory + "/train" + i));
                FileWriter writeTest = new FileWriter(new File(outDirectory + "/test" + i));

                for (String userId : usersRatings.keySet()) {

                    HashMap<String, Rating> ratings = usersRatings.get(userId); //gets thie given user's ratings10m
                    int numRatings = ratings.size();
                    if (numRatings <= 2) continue; //dont add data for users with too few ratings10m
                    int randomNum = ThreadLocalRandom.current().nextInt(0, numRatings); //chooses which index of the ratings10m to leave out
                    int j = 0;

                    for (Rating rating : ratings.values()) {
                        String ratingString = rating.getUser() + "\t" + rating.getItem() + "\t" + rating.getRating() + "\n";

                        //if the random rating, write to test set, otherwise write to training set
                        if (j == randomNum) {
                            writeTest.write(ratingString);
                        }
                        else {
                            writeTrain.write(ratingString);
                        }
                        j++;
                    }
                }
                writeTrain.flush();
                writeTrain.close();
                writeTest.flush();
                writeTest.close();
            }
        }
        catch (IOException ie) {
            //System.out.println("Error: "+ ie.stackTrace());
            ie.printStackTrace(System.out);
            System.exit(0);
        }
    }

    //Method to make a cross-validation set of a dataset. Gives k train/test sets
    //n = -1 indicates 50/50 train/test for test users, givenN specifies if given-n or all-but-n method for splitting
    //should be used
    public static void kFoldCrossValidationSets(String inFile, String outDirectory, int kFolds, String delimiter, int n, boolean givenN) {
        HashMap<String,HashMap<String,Rating>> usersRatings = readRatingData(inFile, delimiter);
        int numUsers = usersRatings.size();

        //Transforms entries in hashmap to a list which is shuffled, so the users are in random order
        List<Map.Entry<String,HashMap<String,Rating>>> list = new ArrayList<>(usersRatings.entrySet());
        Collections.shuffle(list);

        int usersPerFraction = numUsers / kFolds;
        int rest = usersPerFraction % kFolds;
        int lowerBorder = 0;
        int upperBorder;

        System.out.println(rest);
        try {

            //Repeats for all n folds
            for (int i = 1; i <= kFolds; i++) {
                FileWriter writeTrain = new FileWriter(new File(outDirectory + "/train" + i));
                FileWriter writeTest = new FileWriter(new File(outDirectory + "/test" + i));
                upperBorder = lowerBorder + usersPerFraction; //borders that divide users in different sets
                int x = 1;
                int usersRetained = 0;
                int ratingsRetained = 0;
                if (i <= rest) upperBorder++;


                for (int j = 0; j < numUsers; j++) {

                    //ensures all users have at least 20 ratings10m
                    if (list.get(j).getValue().size() < 20) {
                        System.out.println("Skipping user with less than 20 ratings10m");
                        continue;
                    }
                    else {
                        usersRetained++;
                        ratingsRetained += list.get(j).getValue().size();
                    }

                    //if user is inside the set that are used for test set in this fold
                    if (j < upperBorder && j >= lowerBorder) {
                        List<Map.Entry<String, Rating>> ratingsForUser = new ArrayList<>(list.get(j).getValue().entrySet());
                        Collections.shuffle(ratingsForUser);
                        int numToTrain;

                        if (n == -1) numToTrain = ratingsForUser.size() / 2; //divides by half
                        else if (givenN) numToTrain = n; //given-n, i.e. n ratings10m are written to train set
                        else numToTrain = ratingsForUser.size() - n; //all-but-n, i.e. all ratings10m except n are written to train set

                        for (int k = 0; k < ratingsForUser.size(); k++) {
                            Rating rating = ratingsForUser.get(k).getValue();

                            if (k < numToTrain) {
                                writeTrain.write(rating.getString());
                            }
                            else {
                                writeTest.write(rating.getString());
                            }
                        }
                    }
                    //if user outside testset for this fold - write all ratings10m to train set
                    else {
                        List<Map.Entry<String, Rating>> ratingsForUser = new ArrayList<>(list.get(j).getValue().entrySet());
                        Collections.shuffle(ratingsForUser);
                        for (Map.Entry<String, Rating> entry : ratingsForUser) {
                            Rating rating = entry.getValue();
                            writeTrain.write(rating.getString());
                        }
                    }
                }
                lowerBorder = upperBorder;
                writeTrain.flush();
                writeTrain.close();
                writeTest.flush();
                writeTest.close();
                System.out.println("-----------------------");
                System.out.println("Users retained: " + usersRetained + ", ratings10m retained: " + ratingsRetained);
            }
        }
        catch (IOException ie) {
            //System.out.println("Error: "+ ie.stackTrace());
            ie.printStackTrace(System.out);
            System.exit(0);
        }

    }


    /*//givenN = -1 indicates 50/50 train/test for test users
    public static void kFoldCrossValidationSets(String inFile, String outDirectory, int nFolds, String delimiter, int givenN) {
        HashMap<String,HashMap<String,Rating>> usersRatings = readRatingData(inFile, delimiter);
        int numUsers = usersRatings.size();

        //Transforms entries in hashmap to a list which is shuffled, so the users are in random order
        List<Map.Entry<String,HashMap<String,Rating>>> list = new ArrayList<>(usersRatings.entrySet());
        Collections.shuffle(list);

        int usersPerFraction = numUsers / nFolds;
        int rest = usersPerFraction % nFolds;
        int lowerBorder = 0;
        int upperBorder;

        System.out.println(rest);
        try {

            //Repeats for all n folds
            for (int i = 1; i <= nFolds; i++) {
                FileWriter writeTrain = new FileWriter(new File(outDirectory + "/train" + i));
                FileWriter writeTest = new FileWriter(new File(outDirectory + "/test" + i));
                upperBorder = lowerBorder + usersPerFraction;
                int x = 1;
                int usersRetained = 0;
                int ratingsRetained = 0;
                if (i <= rest) upperBorder++;

                for (int j = 0; j < numUsers; j++) {
                    //System.out.println(list.get(j).getKey());

                    if (list.get(j).getValue().size() < 10) {
                        continue;
                    }
                    else {
                        usersRetained++;
                        ratingsRetained += list.get(j).getValue().size();
                    }

                    if (j < upperBorder && j >= lowerBorder) {
                        //System.out.println(x++);
                        //System.out.println("Userid: "+list.get(j).getKey());
                        List<Map.Entry<String, Rating>> ratingsForUser = new ArrayList<>(list.get(j).getValue().entrySet());
                        Collections.shuffle(ratingsForUser);
                        int numToTrain;

                        if (givenN == -1) numToTrain = ratingsForUser.size() / 2;
                        else numToTrain = givenN;

                        for (int k = 0; k < ratingsForUser.size(); k++) {
                            Rating rating = ratingsForUser.get(k).getValue();

                            if (k < numToTrain) {
                                writeTrain.write(rating.getString());
                            }
                            else {
                                writeTest.write(rating.getString());
                            }
                        }
                        //Skriv halvparten (eller gitt verdi) til trening og resten til test
                    }
                    else {
                        //skriv alt til trening
                        List<Map.Entry<String, Rating>> ratingsForUser = new ArrayList<>(list.get(j).getValue().entrySet());
                        Collections.shuffle(ratingsForUser);
                        for (Map.Entry<String, Rating> entry : ratingsForUser) {
                            Rating rating = entry.getValue();
                            writeTrain.write(rating.getString());
                        }
                    }
                }
                lowerBorder = upperBorder;
                writeTrain.flush();
                writeTrain.close();
                writeTest.flush();
                writeTest.close();
                System.out.println("-----------------------");
                System.out.println("Users retained: " + usersRetained + ", ratings10m retained: " + ratingsRetained);
            }
        }
        catch (IOException ie) {
            //System.out.println("Error: "+ ie.stackTrace());
            ie.printStackTrace(System.out);
            System.exit(0);
        }

    }*/


    public static HashMap<String,HashMap<String,Rating>> readRatingData(String file, String delimiter) {
        HashMap<String,HashMap<String,Rating>> usersRatings = new HashMap<String,HashMap<String,Rating>>();

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line = br.readLine();

            //reads in data to data structure
            while (line != null) {
                String[] words = line.split(delimiter);
                usersRatings.putIfAbsent(words[0], new HashMap<String, Rating>());
                usersRatings.get(words[0]).put(words[1], new Rating(words[0], words[1], words[2]));
                line = br.readLine();
            }
        }
        catch (IOException ie) {
            ie.printStackTrace();
            System.exit(0);
        }
        return usersRatings;

    }
}

