package dataset;

/**
 * Created by simen on 3/19/17.
 */
import java.io.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/*class User {
    HashMap<String,Rating> ratings;

    User() {
        ratings = new HashMap<String,Rating>();
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
        nFoldCrossValidationSets("data/movielens/u.data", "data/movielens/cross-val", 5, "\t", -1);
    }


    //Method that takes an input file inFile, reads it and writes it to train/test files nFolds times
    //in the directory outDirectory. For each user, one rating is written to test file, while
    //the rest of the ratings are written to the training file
    public static void leaveOneOut(String inFile, String outDirectory, int nFolds, String delimiter) {
        //2d data structure -> an outer hashmap with userids as keys, and hashmaps with ratings
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

                    HashMap<String, Rating> ratings = usersRatings.get(userId); //gets thie given user's ratings
                    int numRatings = ratings.size();
                    if (numRatings <= 2) continue; //dont add data for users with too few ratings
                    int randomNum = ThreadLocalRandom.current().nextInt(0, numRatings); //chooses which index of the ratings to leave out
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


    public static void nFoldCrossValidationSets(String inFile, String outDirectory, int nFolds, String delimiter, int givenN) {
        HashMap<String,HashMap<String,Rating>> usersRatings = readRatingData(inFile, delimiter);
        int numUsers = usersRatings.size();

        //Transforms entries in hashmap to a list which is shuffled, so the users are in random order
        List<Map.Entry<String,HashMap<String,Rating>>> list = new ArrayList<>(usersRatings.entrySet());
        Collections.shuffle(list);

        int usersPerFraction = numUsers / nFolds; //MUST CHANGE -> NOT EQUAL SIZES (BECAUSE OF REST)
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
                if (i <= rest) upperBorder++;

                for (int j = 0; j < numUsers; j++) {
                    //System.out.println(list.get(j).getKey());

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
            }
        }
        catch (IOException ie) {
            //System.out.println("Error: "+ ie.stackTrace());
            ie.printStackTrace(System.out);
            System.exit(0);
        }

    }

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

