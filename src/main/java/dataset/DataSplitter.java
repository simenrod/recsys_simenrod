package dataset;

/**
 * Created by simen on 3/19/17.
 */
import java.io.*;
import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

class User {
    HashMap<String,Rating> ratings;

    User() {
        ratings = new HashMap<String,Rating>();
    }
}

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
}

public class DataSplitter {
    public static void main(String[] args) {
        //leaveOneOut("movielens100k.data");
        leaveOneOut("data/movielens/ratings.csv", "data/movielens/leave_one_out", 1, ",");
    }

    public static void leaveOneOut(String inFile, String outDirectory, int nFolds, String delimiter) {
		/*for (int i = 0; i < 5; i++) {
			int randomNum = ThreadLocalRandom.current().nextInt(0, 2);
			System.out.println(randomNum);
		}*/


        //String outDirectory = "crossfold-"+inFile;

        //2D datastruktur - feks en hashmap med brukerid som nøkkel, og ny hashmap som verdi.
        //indre hashmap inneholder itemid som nøkkel og Rating som verdi
        HashMap<String,HashMap<String,Rating>> usersRatings = new HashMap<String,HashMap<String,Rating>>();


        //if (new File(outDirectory).mkdirs()) System.out.println("Lagde mappe"); //SKIPS MAKING NEW FOLDER - uses specified instead

        try {
            BufferedReader br = new BufferedReader(new FileReader(inFile));
            String line = br.readLine();
            //int x = 0;
            System.out.println(line + "asads");
            //reads in data to data structure
            while (line != null) {
                String[] words = line.split(delimiter);

                if (usersRatings.get(words[0]) == null) {
                    //x++;
                    usersRatings.put(words[0], new HashMap<String,Rating>());
                }
                usersRatings.get(words[0]).put(words[1], new Rating(words[0], words[1], words[2]));

                line = br.readLine();
            }

            //Maa utvide her til aa kjore lokke saa det lages n antall trening/test-par

            //writes data to partitions
            FileWriter writeTrain = new FileWriter(new File(outDirectory+"/training"));
            FileWriter writeTest = new FileWriter(new File(outDirectory+"/test"));

            for (String userId : usersRatings.keySet()) {
                //System.out.println(userId);
                //System.out.println(x++);

                HashMap<String,Rating> ratings = usersRatings.get(userId);
                int numRatings = ratings.size();
                int randomNum = ThreadLocalRandom.current().nextInt(0, numRatings);
                int i = 0;

                for (Rating rating : ratings.values()) {
                    //can change to 'delimiter', but better to always use the same delimiter in the traing/test files
                    String ratingString = rating.getUser() + "\t" + rating.getItem() + "\t" + rating.getRating() + "\n";

                    //if the random rating, write to test set, otherwise write to training set
                    if (i == randomNum) {
                        writeTest.write(ratingString);
                    }
                    else {
                        writeTrain.write(ratingString);
                    }
                    i++;
                    System.out.println(i);
                }
            }

            writeTrain.flush();
            writeTrain.close();
            writeTest.flush();
            writeTest.close();
            //System.out.println(usersRatings.size() + " " + x);

        }
        catch(IOException ie) {
            //System.out.println("Error: "+ ie.stackTrace());
            ie.printStackTrace(System.out);
            System.exit(0);
        }


		/*
		//!!Burde kanskje fjerne all 0 siden dette er implisitte data og ikke betyr rating 0!!
		System.out.println("Reformating bx...");
		HashMap<String, Integer> hm = new HashMap<String, Integer>();
		int teller = 1;

		try {
			BufferedReader br = new BufferedReader(new FileReader("bx.csv"));
			FileWriter fw = new FileWriter(new File("bx4.csv"));

			String line = br.readLine();
			int lineNr = 0;

			while (line != null) {
				String[]words = line.split(";");
				//System.out.println(words.length);

				for (int i = 0; i < words.length; i++) {
					words[i] = words[i].replace("\"","");
					//System.out.println(words[i]);
				}

				if (words[2].equals("0")) {
					//System.out.println("0");
					line = br.readLine();
					continue; //skips implicit data
				}

				if (hm.get(words[1]) == null) {
					hm.put(words[1], new Integer(teller++));
				}
				int book_nr = hm.get(words[1]);

				fw.write(words[0]+"\t"+book_nr+"\t"+words[2]+"\n");
				line = br.readLine();
				//if (lineNr++ >= 10) break;
			}
			fw.flush();
      		fw.close();


		}
		catch(IOException ie) {
			System.out.println("Error");
			System.exit(0);
		}
		System.out.println("Reformating successful!");*/
    }

}