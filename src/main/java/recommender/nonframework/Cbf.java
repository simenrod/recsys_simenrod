package recommender.nonframework;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Stack;

/**
 * Created by simen on 3/20/17.
 */
/*class Prediction implements Comparable<Prediction> {
    private User user;
    private Item item;
    private double value;

    Prediction(User u, Item i, double v) {
        user = u;
        item = i;
        value = v;
    }

    Prediction(Item i, double v) {
        item = i;
        value = v;
    }

    public void increaseValue(double d) {
        value += d;
    }

    public double getValue() {
        return value;
    }

    public Item getItem() {
        return item;
    }

    public User getUser() {
        return user;
    }

    //reversed compareTo-method which ensures larger scores are stored before smaller scores when sorting
    public int compareTo(Prediction otherPrediction) {
        if (value > otherPrediction.getValue()) {
            return -1;
        }
        else if (value < otherPrediction.getValue()) {
            return 1;
        }
        else {
            return 0;
        }
    }
}*/

class Characteristic {
    private int frequency;
    private String word;

    public Characteristic() {
        frequency = 1;
    }

    public int getFrequency() {
        return frequency;
    }

    public void increaseFrequency() {
        frequency++;
    }

    public String getWord() {
        return word;
    }
}

class ItemSimilarity implements Comparable<ItemSimilarity> {
    private Item item;
    private double value;

    ItemSimilarity(Item item, double value) {
        this.item = item; this.value = value;
    }

    public Item getItem() {
        return item;
    }

    public double getValue() {
        return value;
    }

    //reversed compareTo-method which ensures larger scores are stored before smaller scores when sorting
    public int compareTo(ItemSimilarity otherSimilarity) {
        if (value > otherSimilarity.getValue()) {
            return -1;
        }
        else if (value < otherSimilarity.getValue()) {
            return 1;
        }
        else {
            return 0;
        }
    }
}

class Item {
    //hashmap for characteristics
    private String id;
    private HashMap<String, Characteristic> tags = new HashMap<String, Characteristic>();
    private Stack<ItemSimilarity> simItems = new Stack<ItemSimilarity>();
    private ItemSimilarity[] sortedSimItems;

    public double computeCosine(Item i2) {
        double length1 = 0;
        double length2 = 0;

        for (Characteristic c : tags.values())
            length1 += (c.getFrequency() * c.getFrequency());
        for (Characteristic c : i2.getCharacteristics().values())
            length2 += (c.getFrequency() * c.getFrequency());
        length1 = Math.sqrt(length1);
        length2 = Math.sqrt(length2);
        double dotProduct = 0;
        for (String key : tags.keySet()) {
            if (tags.get(key) != null && i2.getCharacteristics().get(key) != null)
                dotProduct += (tags.get(key).getFrequency() * i2.getCharacteristics().get(key).getFrequency());
        }
        //System.out.println(dotProduct);
        return dotProduct/(length1*length2);
    }

    public void findMostSimilar(HashMap<String,Item> items) {
        //System.out.println("In " + id + " finding similar items");
        double largest = 0;
        for (Item i : items.values()) {

            if (!i.getId().equals(id)) {
                double sim = computeCosine(i);
                if (sim > largest) largest = sim;
                simItems.push(new ItemSimilarity(i, sim));
            }

        }
        sortedSimItems = simItems.toArray(new ItemSimilarity[simItems.size()]);
        Arrays.sort(sortedSimItems);

    }

    public Item(String id) {
        this.id = id;
    }

    public void addCharacteristic(String s) {
        tags.put(s, new Characteristic());
    }

    public HashMap<String, Characteristic> getCharacteristics() {
        return tags;
    }

    public String getId() {
        return id;
    }

    public ItemSimilarity[] getSortedSimItems() {
        return sortedSimItems;
    }

}

class User {
    private String id;
    private HashMap<String, Item> history = new HashMap<String, Item>();
    private HashMap<String, Characteristic> featureVector = new HashMap<String, Characteristic>();
    //private HashMap<String, Prediction> predictions = new HashMap<String, Prediction>(); //Maybe better with another data structure; heap, etc.
    private Stack<Prediction> predictions = new Stack<Prediction>();
    private Prediction[] recommendations;

    public HashMap<String, Item> getHistory() {
        return history;
    }

    public HashMap<String, Characteristic> getFeatureVector() {
        return featureVector;
    }

    public String getId() {
        return id;
    }

    public void addItemToHistory(String nr, Item item) {
        history.put(nr,item);
    }

    public void increaseFeature(String value) {
        if (featureVector.get(value) == null) {
            featureVector.put(value, new Characteristic());
        }
        else {
            featureVector.get(value).increaseFrequency();
        }
    }

    public void addPrediction(User user, Item item, double score) {
        //predictions.put(item.getId(), new Prediction(user, item, score));
        predictions.push(new Prediction(user, item, score));
    }

    /*public Prediction[] getRecommendations() {
        Prediction[] predArray = predictions.toArray(new Prediction[predictions.size()]);
        Arrays.sort(predArray);
        return predArray;
    }*/
    public void sortPredictions() {
        recommendations = predictions.toArray(new Prediction[predictions.size()]);
        Arrays.sort(recommendations);
    }

    public Prediction[] getRecommendations() {
        return recommendations;
    }

    public void knnRecommendation(HashMap<String,Item> items) {
        for (Item i : items.values()) {
            int k = 30;
            int x = 0;
            int matches = 0;
            ItemSimilarity simItems[] = i.getSortedSimItems();
            double sum = 0;
            while (x < simItems.length) {

                //System.out.println(simItems[x].getValue());
                String itemId = simItems[x].getItem().getId();
                if (history.get(itemId) != null) {
                    //System.out.println(simItems[x].getValue());
                    matches++;
                    sum += simItems[x].getValue();
                }
                //System.out.println(sum);
                x++;
                if (matches >= k) break;
            }
            addPrediction(this, i, sum);
            //System.out.println(sum);
        }
    }

}

class Baseline {
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

public class Cbf {
    //hashmap for items
    //hashmap for users
    private HashMap<String,Item> items = new HashMap<String, Item>();
    private HashMap<String,User> users = new HashMap<String, User>();

    public static void main(String[] args) {
        System.out.println("Test cbf");
        Cbf recommender = new Cbf();
        recommender.readRatings("\t");
        //String a = "ab|cd|efg|hij";
        //String[]datas = a.split("\\|");
        //System.out.println(datas[0]);
        recommender.readItemData("\\|");
        //Item i1 = recommender.items.get("1");
        //System.out.println("---->" + i1.getCharacteristics().get("3").getValue());

		/*recommender.makeFeatureVectorForUsers();
		//User u = recommender.users.get("1");
		//System.out.println(u.getFeatureVector().get("5").getValue());
		recommender.computeScoresForUsers();
		User u = recommender.users.get("1");
		Prediction[] recommendations = u.getRecommendations();*/
		/*int x = 0;
		System.out.println("Recs for user 1");
		for (Prediction p : recommendations) {
			System.out.print("Item-ID: " + p.getItem().getId() + " Score: " + p.getValue() + "  ");
			if (x++  % 5 == 0) System.out.println("");
		}*/
        //recommender.writeTopNToFile();

        recommender.findMostSimilarItems();
        recommender.knnRecommendation();
		/*User u = recommender.users.get("1");
		u.knnRecommendation(recommender.items);
		u.sortPredictions();
		Prediction[] recommendations = u.getRecommendations();
		int x = 0;
		for (Prediction p : recommendations) {
			System.out.print("Item-ID: " + p.getItem().getId() + " Score: " + p.getValue() + "  ");
			if (x++  % 5 == 0) System.out.println("");
		}*/
        recommender.writeTopNToFile();
    }

    //for each item - find similarities to all other items and sort them in decreasing similarity values
    public void findMostSimilarItems() {
        for (Item i : items.values()) {
            i.findMostSimilar(items);
        }
    }

    public void knnRecommendation() {
        for (User u : users.values()) {
            u.knnRecommendation(items);
            u.sortPredictions();
        }
    }


    public void writeTopNToFile() {
        //FORTSETT HER: Skriv top-n recs for hver bruker til fil
        //userid, itemid-rec 1, ..., itemid-rec n,
        try {
            FileWriter fw = new FileWriter(new File("/home/simen/Documents/datasett/kjoring/cbf.data"));
            int numUsers = 943;
            int n = 10;
            for (int i = 1; i <= numUsers; i++) {
                fw.write(i+"\t");
                if (i < 10) System.out.println("-- user: "+i); //TESTUTSKRIFT
                //Rating[] recs = getRecsForUserExcludeRatedItems(i, n);
                //List<ScoredId> recommendations = irec.recommend(i, 10);
                User u = users.get(Integer.toString(i));
                //System.out.println(i);
                Prediction[] recommendations = u.getRecommendations();

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

    public void readRatings(String splitter) {

        try {
            //BufferedReader br = new BufferedReader(new FileReader("/home/simen/Documents/datasett/ml-100k/u.data"));
            BufferedReader br = new BufferedReader(new FileReader("/home/simen/Documents/datasett/crossfold-movielens-binary/training"));
            String line = br.readLine();

            while (line != null) {
                //userid, itemid, rating, timestamp
                String[] datas = line.split(splitter);
                if (users.get(datas[0]) == null) users.put(datas[0], new User());
                if (items.get(datas[1]) == null) items.put(datas[1], new Item(datas[1]));
                users.get(datas[0]).addItemToHistory(datas[1], items.get(datas[1]));
                line = br.readLine();

                //don't care about ratings given -> because implicit feedback
            }
        }

        catch (IOException e){
            e.printStackTrace();
            System.exit(1);
        }

    }

    public void readItemData(String splitter) {
        try {
            BufferedReader br = new BufferedReader(new FileReader("/home/simen/Documents/datasett/ml-100k/u.item"));
            String line = br.readLine();
            String[] splitting = line.split(splitter);
			/*for (String s : splitting) {
				System.out.println(s);
			}*/
            while (line != null) {
                //itemid|name (year)|date-year||website|0|0|0|1|1|1|0|0|0|0|0|0|0|0|0|0|0|0|0
                //19 tags - starter på indeks 5
                String[] datas = line.split(splitter);
                if (items.get(datas[0]) == null) items.put(datas[0], new Item(datas[0]));
                for (int i = 0; i < 19; i++) {
                    if (datas[5+i].equals("1")) items.get(datas[0]).addCharacteristic(Integer.toString(i));
                }
                line = br.readLine();
            }
        }

        catch (IOException e){
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void makeFeatureVectorForUsers() {
        int x = 0;

        //for each user
        for (User u : users.values()) {
            //System.out.println(++x);
            //i.getCharacteristics();'
            x++;
            //for each item the user has rated
            for (Item i : u.getHistory().values()) {
                //System.out.println(x);

                //adds characteristics to the users feature vector corresponing to the features belonging to the items the user has watched
                for (String c : i.getCharacteristics().keySet()) {
                    u.increaseFeature(c);
                }
            }
        }
    }

    public void computeScoresForUsers() {
        for (User u : users.values()) {
            for (Item i : items.values()) {
                double score = computeDotProduct(u.getFeatureVector(), i.getCharacteristics());
                u.addPrediction(u, i, score);
            }
            u.sortPredictions();
        }
        //for hver bruker b:
        //for hvert item i:
        //beregn dot-produkt d paa b.featureVector,i.characteristics
        //lagre d i en Prediction p for b.

    }

    public double computeDotProduct(HashMap<String, Characteristic> userVector, HashMap<String, Characteristic> itemVector) {
        //System.out.println("->-<-");
        double dotProduct = 0;
        for (String key : userVector.keySet()) {
            if (itemVector.get(key) != null) dotProduct += (itemVector.get(key).getFrequency() * userVector.get(key).getFrequency());
        }
        //System.out.println(dotProduct);
        return dotProduct;
    }
}