package recommender.nonframework;

import recommender.Recommender;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by simen on 3/20/17.
 */
public class BaselineRecommender implements Recommender {
    private HashMap<String,Item> items;
    private HashMap<String, User> users;
    private HashMap<String,Prediction> predictions;
    private Prediction[] sortedPredictions;


    //TODO only include if this is included in interface
    public void initialize() {
        //nothing to initialize
    }

    //makes recommender ready to produce recommendations by calling ReadRatings, and sorting the predictions based
    //on number of users who have interacted with the item
    public void update(String trainingFile) {
        readRatings("\t", trainingFile);
        sortedPredictions = predictions.values().toArray(new Prediction[predictions.size()]);
        Arrays.sort(sortedPredictions);
    }

    //Recommends the most popular items which the user has not rated before
    public int[] recommend(int userId, int num) {
        int[] recIds = new int[num];
        User u = users.get(Integer.toString(userId));
        int x = 0;
        for (Prediction p : sortedPredictions) {
            String itemId = p.getItem().getId();
            if (u.getHistory().get(itemId) == null) recIds[x++] = Integer.parseInt(itemId);
            if (x == num) break;
        }
        return recIds;
    }


    //Reads the ratings from the training file and stores them
    public void readRatings(String splitter, String trainingFile) {
        items = new HashMap<>();
        users = new HashMap<>();
        predictions = new HashMap<>();

        try {
            BufferedReader br = new BufferedReader(new FileReader(trainingFile));
            String line = br.readLine();

            //for each line adds users, items and predictions to h
            while (line != null) {
                String[] datas = line.split(splitter);
                Prediction p = predictions.get(datas[1]);

                users.putIfAbsent(datas[0], new User());
                items.putIfAbsent(datas[1], new Item(datas[1]));
                users.get(datas[0]).addItemToHistory(datas[1], items.get(datas[1]));

                //either adds new Prediction to prediction hashmap or increase value of a prediction
                if (p == null) {
                    predictions.put(datas[1], new Prediction(items.get(datas[1]), 1));
                }
                else {
                    p.increaseValue(1);
                }

                line = br.readLine();
            }
        }
        catch (IOException e){
            e.printStackTrace();
            System.exit(1);
        }
    }

    public String getInfo() {
        return "Popularity baseline recommender";
    }

    public void close() {/*nothing to close*/}

}
