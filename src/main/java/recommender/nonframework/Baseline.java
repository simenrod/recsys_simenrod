package recommender.nonframework;

import recommender.Recommender;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by simen on 3/20/17.
 */
public class Baseline implements Recommender {
    //private HashMap<String,Integer> items = new HashMap<String, Integer>();
    private HashMap<String,Item> items = new HashMap<String, Item>();
    private HashMap<String, User> users = new HashMap<String, User>();
    private HashMap<String,Prediction> predictions = new HashMap<String, Prediction>();
    private Prediction[] sortedPredictions;

    public static void main(String[] args) {
        Baseline baseRec = new Baseline();
        baseRec.readRatings("\t");
        System.out.println("Har lest ratings");
        //baseRec.recommend();
        baseRec.writeTopNToFile();
    }

    //TODO only include if this is included in interface
    public void initialize() {
        //nothing to initialize
    }

    public void update(String trainingFile) {
        readRatings("\t");
    }

    public int[] recommend(int userId, int num) {
        int[] recIds = new int[num];
        User u = users.get(Integer.toString(userId));
        int j = 0;
        int x = 0;
        while (j < sortedPredictions.length) {
            Prediction p = sortedPredictions[j];
            String itemId = p.getItem().getId();
            if (u.getHistory().get(itemId) == null) { //ikke ratet fra foer
                //if (i < 10) System.out.println("-"+itemId + " ("+p.getValue()); //TESTUTSKRIFT
                //fw.write(itemId + "\t");
                recIds[x] = Integer.parseInt(itemId);
                x++;
            }
            j++;
            if (x == num) break;
        }
        return recIds;
    }

    public Prediction[] recommend() {
		/*Integer[] frequencies = items.values().toArray(new Integer[items.size()]);
		Arrays.sort(frequencies);
		for (Integer i : frequencies) {
			System.out.println(i.intValue());
		}*/

        //Prediction[] predArray = predictions.values().toArray(new Prediction[predictions.size()]);
        //Arrays.sort(predArray);
        //System.out.println(predArray[0].getValue());
        return sortedPredictions;
    }

    public void readRatings(String splitter) {

        try {
            //BufferedReader br = new BufferedReader(new FileReader("/home/simen/Documents/datasett/ml-100k/u.data"));
            BufferedReader br = new BufferedReader(new FileReader("/home/simen/Documents/datasett/crossfold-movielens-binary/training"));
            String line = br.readLine();

            while (line != null) {
                //userid, itemid, rating, timestamp

                String[] datas = line.split(splitter);

                Prediction p = predictions.get(datas[1]);

                if (users.get(datas[0]) == null) users.put(datas[0], new User());
                if (items.get(datas[1]) == null) items.put(datas[1], new Item(datas[1]));
                users.get(datas[0]).addItemToHistory(datas[1], items.get(datas[1]));

                if (p == null) {
                    predictions.put(datas[1], new Prediction(items.get(datas[1]), 1));
                }
                else {
                    //items.put(datas[1], numRatings.intValue()+1);
                    p.increaseValue(1);
                }

				/*Integer numRatings = items.get(datas[1]);

				if (numRatings == null) {
					items.put(datas[1], 1);
				}
				else {
					items.put(datas[1], numRatings.intValue()+1);
				}*/

                line = br.readLine();
            }
            sortedPredictions = predictions.values().toArray(new Prediction[predictions.size()]);
            Arrays.sort(sortedPredictions);
        }

        catch (IOException e){
            e.printStackTrace();
            System.exit(1);
        }

    }


    public void writeTopNToFile() {

        //userid, itemid-rec 1, ..., itemid-rec n,
        try {
            FileWriter fw = new FileWriter(new File("/home/simen/Documents/datasett/kjoring/baseline.data"));
            int numUsers = 943;
            int n = 10;
            for (int i = 1; i <= numUsers; i++) {
                fw.write(i+"\t");
                if (i < 10) System.out.println("-- user: "+i); //TESTUTSKRIFT
                //Rating[] recs = getRecsForUserExcludeRatedItems(i, n);
                //List<ScoredId> recommendations = irec.recommend(i, 10);
                User u = users.get(Integer.toString(i));
                //System.out.println(i);
                Prediction[] recommendations = recommend(); //change to use class variable!!

                /*for (int j = 0; j < 10; j++) {
                	Prediction p = recommendations[j];
                    fw.write(p.getItem().getId() + "\t");
                }*/
                //System.out.println("Test1"+recommendations.length);
                int j = 0;
                int x = 0;
                while (j < recommendations.length) {
                    Prediction p = recommendations[j];
                    String id = p.getItem().getId();
                    if (u.getHistory().get(id) == null) { //ikke ratet fra foer
                        if (i < 10) System.out.println("-"+id + " ("+p.getValue()); //TESTUTSKRIFT
                        fw.write(id + "\t");
                        x++;
                    }
                    j++;
                    if (x == 10) break;
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
