package dataset;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;

/**
 * Created by simen on 3/30/17.
 */
public class Statistics {

    public static void main(String[] args) {
        //printInfoAboutTags("data/bx6k/item-tags-reduced", ",");
        //printInfoAboutTags("data/msd6k/tags", ",");
        printInfoAboutTags("data/ml6k/tags", ",");
    }

    public static void printInfoAboutData(String ratingFile, String delimiter) {
        HashSet<String> users = new HashSet<>();
        HashSet<String> items = new HashSet<>();
        double ratings = 0;

        try {
            BufferedReader br = new BufferedReader(new FileReader(ratingFile));
            String line = br.readLine();

            while (line != null) {
                String[] words = line.split(delimiter);
                users.add(words[0]);
                items.add(words[1]);
                ratings++;
                line = br.readLine();
            }
            System.out.println("Total number of users: " + users.size());
            System.out.println("Total number of items: " + items.size());
            System.out.println("Total number of ratings: " + ratings);
            System.out.println("Avg ratings per user: " + ratings/users.size());
            System.out.println("Avg ratings per item: " + ratings/items.size());

        }
        catch(IOException ie) {
            ie.printStackTrace();
            System.exit(1);
        }

    }

    public static void printInfoAboutTags(String tagFile, String delimiter) {
        HashSet<String> distinctTags = new HashSet<>();
        HashSet<String> items = new HashSet<>();
        int numTags = 0;


        try {
            BufferedReader br = new BufferedReader(new FileReader(tagFile));
            String line = br.readLine();

            while (line != null) {
                String[] words = line.split(delimiter);

                items.add(words[0]);
                distinctTags.add(words[1]);
                numTags++;
                line = br.readLine();
            }

            System.out.println("Distinct tags: " + distinctTags.size());
            System.out.println("Total number of <item,tag>-pairs: " + numTags);
            System.out.println("Total number of items: " + items.size());
            System.out.println("Avg tags per item: " + numTags/items.size());

        }
        catch(IOException ie) {
            ie.printStackTrace();
            System.exit(1);
        }
    }
}
