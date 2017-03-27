package dataset;


import it.unimi.dsi.fastutil.Hash;
import shapeless.tag;

import java.io.*;
import java.util.*;

/**
 * Created by simen on 3/21/17.
 */
public class ReformatData {
    private static HashSet<String> stopWords;

    public static void main(String[] args) {
        //movieLensTagsSmall("data/movielens/u.item", "data/movielens/item-tags", "data/movielens/titles");
        /*bookCrossing("data/bx6k/ratings","data/bx/bx-books.csv", "data/bx6k/ratings-transformed",
                "data/bx6k/item-tags","data/bx6k/titles");*/
        //makeSubset("data/bx/bx.csv", "data/bx6k/ratings",";", 6000, 20, 200);
        //makeSubset("/home/simen/Desktop/train_triplets.txt", "data/msd6k/ratings4","\t", 6000, 30, 100);
        //makeSubset("/home/simen/Desktop/ml-1m/ratings.dat", "data/ml6k/ratings","::", 6000, 20, 200);
        /*msd("data/msd6k/ratings3", "data/msd6k/song-to-track", "data/msd6k/itemids",
                "data/msd6k/ratings-transformed", "/home/simen/Desktop/msd/tags-reduced",
                "data/msd6k/tags", "data/msd6k/titles");*/

        //reduceTags("data/msd6k/tags", "data/msd6k/tags-reduced", 10);
        //reduceMsdTags("/home/simen/Desktop/msd/tid_tag.csv", "/home/simen/Desktop/msd/tags-reduced", 30);
        //binarizeRatings("data/msd6k/ratings", "data/msd6k/binarized-ratings");
        //makeSubset("/home/simen/Desktop/ml-10M100K/ratings.dat","data/ml6k/ratings", "::", 6000,20,200);
        movieLensTagsLarge("/home/simen/Desktop/ml-10M100K/movies.dat", "data/ml6k/ratings",
                "data/ml6k/tags", "data/ml6k/titles");
    }

    //makes a new file with tags for movies, and a file with the titles of the movies (for movielens 100k ratings)
    public static void movieLensTagsSmall(String fileName, String outputTagFile, String outputTitleFile) {
        /*FROM MOVIELENS' README: Information about the items (movies); this is a tab separated
        list of
        movie id | movie title | release date | video release date |
                IMDb URL | unknown | Action | Adventure | Animation |
                Children's | Comedy | Crime | Documentary | Drama | Fantasy |
        Film-Noir | Horror | Musical | Mystery | Romance | Sci-Fi |
                Thriller | War | Western |
                The last 19 fields are the genres, a 1 indicates the movie
        is of that genre, a 0 indicates it is not; movies can be in
        several genres at once.
        The movie ids are the ones used in the u.data data set.*/
        String[] tags = {"unknown", "Action", "Adventure", "Animation",
                "Children's", "Comedy", "Crime", "Documentary", "Drama", "Fantasy",
                "Film-Noir", "Horror", "Musical", "Mystery", "Romance", "Sci-Fi",
                        "Thriller", "War", "Western"};
        String splitter = "\\|"; //| is seperator

        try {
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            FileWriter fw = new FileWriter(new File(outputTagFile));
            FileWriter fwTitles = new FileWriter(outputTitleFile);

            String line = br.readLine();
            String[] splitting = line.split(splitter);

            while (line != null) {
                //itemid|name (year)|date-year||website|0|0|0|1|1|1|0|0|0|0|0|0|0|0|0|0|0|0|0
                //19 tags - starts on index 5
                String[] datas = line.split(splitter);
                String itemId = datas[0];
                fwTitles.write(itemId + "," + datas[1] + "\n");
                //if (items.get(datas[0]) == null) items.put(datas[0], new Item(datas[0]));

                for (int i = 0; i < 19; i++) {
                    if (datas[5+i].equals("1")) { //items.get(datas[0]).addCharacteristic(Integer.toString(i));
                        //System.out.println(itemId + "," + tags[i]);
                        fw.write(itemId + "," + tags[i] + "\n");
                    }
                }
                line = br.readLine();
            }
            fw.flush();
            fw.close();
            fwTitles.flush();
            fwTitles.close();
        }
        catch (IOException e){
            e.printStackTrace();
            System.exit(1);
        }
    }

    //making tags file and titles file for the itemids contained in ratingfile, suitable for movielens 1M & 10M
    public static void movieLensTagsLarge(String tagsFile, String ratingFile, String outputTagFile, String outputTitleFile) {
        try {
            String splitter = "::";
            HashSet<String> itemIds = new HashSet<>();
            BufferedReader br = new BufferedReader(new FileReader(ratingFile));

            String line = br.readLine();

            while (line != null) {
                String[] words = line.split(splitter);
                itemIds.add(words[1]);
                line = br.readLine();
            }

            br = new BufferedReader(new FileReader(tagsFile));

            FileWriter fw = new FileWriter(new File(outputTagFile));
            FileWriter fwTitles = new FileWriter(outputTitleFile);

            line = br.readLine();

            while (line != null) {
                String[] datas = line.split(splitter);
                String itemId = datas[0];
                fwTitles.write(itemId + "," + datas[1] + "\n");
                //if (items.get(datas[0]) == null) items.put(datas[0], new Item(datas[0]));

                for (String s : datas[2].split("\\|")) {
                    fw.write(itemId + "," + s + "\n");
                }
                line = br.readLine();
            }
            fw.flush();
            fw.close();
            fwTitles.flush();
            fwTitles.close();
        }
        catch (IOException e){
            e.printStackTrace();
            System.exit(1);
        }
    }

    //makes a new file with ratings (where isbn are substitued with integer values because some of the recommenders not handles itemids
    // as strings), a file for the tags of the items and a file with titles of the items.
    public static void bookCrossing(String ratingFile, String contentFile, String outputRatingFile,
                                    String outputTagFile, String outputTitleFile) {

        HashMap<String, Integer> isbnToUserID = formatBxRatings(ratingFile, outputRatingFile);
        String splitter = ";";
        try {
            BufferedReader br = new BufferedReader(new FileReader(contentFile));
            FileWriter fwTags = new FileWriter(new File(outputTagFile));
            FileWriter fwTitles = new FileWriter(outputTitleFile);

            String line = br.readLine(); //first line is descripton of file, so skips
            line = br.readLine();

			int x = 0;
            while (line != null) {
                //"ISBN";"Book-Title";"Book-Author";"Year-Of-Publication";"Publisher";"Image-URL-S";"Image-URL-M";"Image-URL-L"
                String[] datas = line.split(splitter);
                for (int i = 0; i < datas.length; i++) {
                    datas[i] = datas[i].replace("\"","");
                }
                String isbn= datas[0];
                int itemId;

                //only store tags for items that are rated in the ratingfile
                if (isbnToUserID.containsKey(isbn)) {
                    itemId = isbnToUserID.get(isbn);
                }
                else {
                    System.out.println("ISBN " + isbn + " not contained in ratingsfile");
                    line = br.readLine();
                    continue;
                }


                //System.out.println("ISBN: " + isbn + ", itemID: " + itemId);
                /*for (String s : datas) {
                    System.out.print(s.replace("\"", ""));

                }*/
                fwTitles.write(itemId + "," + datas[1] + "\n"); //adds title to title file
                //System.out.println(itemId + "," + datas[1] + "(title)");

                //adds all words in title (except for words in stoplist) to tags file
                for (String s : datas[1].toLowerCase().split(" ")) {
                   // System.out.println(itemId + "," + s);
                    String word = s.replaceAll("[.\\-!:\\\\;,()\\[\\]\\}\\{\\}#\\\"\\'<>|/*^_+?]","");;
                    if (word.equals("")) continue;
                    if (!inStoplist(word)) fwTags.write(itemId + "," + word + "\n");
                }

                //writes author, year published and publisher to tags file
                for (int i = 2; i <= 4; i++) {
                    if (!datas[i].equals("")) fwTags.write(itemId + "," + datas[i].toLowerCase() + "\n");
                }
                /*fwTags.write(itemId + "," + datas[2].toLowerCase() + "\n");
                fwTags.write(itemId + "," + datas[3].toLowerCase() + "\n");
                fwTags.write(itemId + "," + datas[4].toLowerCase() + "\n");*/
                //System.out.println(itemId + "," + datas[2] + "(author)");
                //System.out.println(itemId + "," + datas[3] + "(year published)");
                //System.out.println(itemId + "," + datas[4] + "(publisher)");


                line = br.readLine();
            }
            fwTags.flush();
            fwTags.close();
            fwTitles.flush();
            fwTitles.close();
        }

        catch (IOException e){
            e.printStackTrace();
            System.exit(1);
        }
    }


    public static void updateStoplist() {
        String[] stoplist = {"a", "an", "and", "are", "as", "at", "be", "but", "by",
                "for", "if", "in", "into", "is", "it",
                "no", "not", "of", "on", "or", "such",
                "that", "the", "their", "then", "there", "these",
                "they", "this", "to", "was", "will", "with"};
        //stopWords
        stopWords = new HashSet<>();
        for (String s : stoplist) {
            stopWords.add(s);
        }
    }
    public static boolean inStoplist(String word) {
        if (stopWords == null) {
            updateStoplist();
        }
        return stopWords.contains(word);
    }

    //method that reads bx ratings, writes to file with <userid, itemid, rating> to file (substituting isbn with integers)
    //and returning a hashmap with one userid value for each isbn read
    public static HashMap<String, Integer> formatBxRatings(String inputFile, String outputFile) {
        System.out.println("Reformating bx...");
        HashMap<String, Integer> hm = new HashMap<>();
        int counter = 1;

        try {
            BufferedReader br = new BufferedReader(new FileReader(inputFile));
            FileWriter fw = new FileWriter(new File(outputFile));

            String line = br.readLine();

            while (line != null) {
                String[]words = line.split(";");

                for (int i = 0; i < words.length; i++) {
                    words[i] = words[i].replace("\"","");
                    //System.out.println(words[i]);
                }

                /*if (words[2].equals("0")) {
                    //System.out.println("0");
                    line = br.readLine();
                    continue; //skips implicit data
                }*/

                if (hm.get(words[1]) == null) {
                    hm.put(words[1], new Integer(counter++));
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
            ie.printStackTrace();
            System.exit(1);
        }
        System.out.println("Reformating successful!");
        return hm;
    }

    //makes a subset in outputFile of inputFile. Chooses numUsers random users from the subset with
    // ratings in the range given
    public static void makeSubset(String inputFile, String outputFile, String delimiter, int numUsers, int minRatings, int maxRatings) {
        //read all lines
        //for each line, store userID in hashset h1
        //shuffle all userIds in set
        //for the x first (e.g. 6000), store the ids in another hashset h2
        //
        //read all lines again
        //for each line, if userid is in hashset h2, write the line to new file

        HashMap<String, Integer> users = new HashMap<>();
        HashSet<String> usersToKeep = new HashSet<>();
        //HashSet<String> users = new HashSet<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(inputFile));
            String line = br.readLine();

            while (line != null) {
                String[] words = line.split(delimiter);

                users.putIfAbsent(words[0],0);
                users.put(words[0], users.get(words[0])+1);
                line = br.readLine();
            }

            //transforms hashmap of users to a arraylist, so it can be shuffled
            List<Map.Entry<String,Integer>> list = new ArrayList<>(users.entrySet());
            Collections.shuffle(list);

            //stores the numUsers users to keep in the set usersToKeep. The list has been shuffled
            //and we store the x first users who has ratings in the range we have chosen (ensures random users)
            int i = 0;
            for (Map.Entry<String,Integer> entry : list) {
                if (entry.getValue() >= minRatings && entry.getValue() <= maxRatings) {
                    usersToKeep.add(entry.getKey());
                    if (i >= numUsers) break;
                    i++;
                }
            }
            System.out.println("Randomly chosen " + i + " users");

            br = new BufferedReader(new FileReader(inputFile));
            FileWriter fw = new FileWriter(new File(outputFile));
            line = br.readLine();

            while (line != null) {
                String[] words = line.split(delimiter);
                if (usersToKeep.contains(words[0])) {
                    //System.out.println(line);
                    fw.write(line + "\n");
                }

                //users.putIfAbsent(words[0],0);
                //users.put(words[0], users.get(words[0])+1);
                line = br.readLine();
            }

            fw.flush();
            fw.close();


        }
        catch(IOException ie) {
            ie.printStackTrace();
            System.exit(1);
        }
    }

    //Reduces the tagfile by removing tags that have frequency smaller or equal to minNum (for bookcrossing)
    public static void reduceTags(String inputFile, String outputFile, int minNum) {
        HashMap<String, Integer> tags = new HashMap<>();

        try {
            BufferedReader br = new BufferedReader(new FileReader(inputFile));
            FileWriter fw = new FileWriter(new File(outputFile));

            String line = br.readLine();

            while (line != null) {
                String[]words = line.split(",");

                tags.putIfAbsent(words[1], 0);
                tags.put(words[1], tags.get(words[1])+1);
                line = br.readLine();
            }

            br = new BufferedReader(new FileReader(inputFile));
            line = br.readLine();
            while (line != null) {
                String[] words = line.split(",");
                if (tags.get(words[1]) >= minNum) fw.write(line + "\n");
                line = br.readLine();
            }
            fw.flush();
            fw.close();
        }
        catch(IOException ie) {
            ie.printStackTrace();
            System.exit(1);
        }
    }

    //Reduces the tag file for MSD by not keeping tags with values below minVal (MSD tags comes with values between 0-100)
    public static void reduceMsdTags(String inputFile, String outputFile, int minVal) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(inputFile));
            FileWriter fw = new FileWriter(new File(outputFile));

            String line = br.readLine();

            while (line != null) {
                String[] words = line.split(",");
                if (Double.parseDouble(words[2]) > minVal) fw.write(words[0] + "," + words[1] + "," + words[2] + "\n");
                line = br.readLine();
            }

            fw.flush();
            fw.close();
        }
        catch(IOException ie) {
            ie.printStackTrace();
            System.exit(1);
        }
    }

    public static void msd(String ratingFile, String trackToSongFile, String itemIdsFile, String outputRatingFile,
                           String tagFile, String outputTagFile, String outputTitleFile) {
        HashMap<String, String> trackToSong = new HashMap<>();
        HashMap<String, Integer> songToItemId = new HashMap<>();
        HashSet<Integer> itemIds = new HashSet<>();
        HashMap<String, Integer> userIds = new HashMap<>();
        HashMap<String, Integer> numRatings = new HashMap<>();

        //for hver linje i ratingfile, lagre item-nokkel

        //gammel item-tag -> ny item-tag
        //gjoere om ny item-tag til int-verdier tilsvarende tag-fil
        //skrive ny tag-fil (fjerne forste linje og tredje kolonne -verdier og evt utvelgelse av tags)
        try {
            BufferedReader br = new BufferedReader(new FileReader(trackToSongFile));
            FileWriter fwRatings = new FileWriter(new File(outputRatingFile));
            FileWriter fwTags = new FileWriter(new File(outputTagFile));
            FileWriter fwTitles = new FileWriter(new File(outputTitleFile));

            String line = br.readLine();

            //reads trackIds to songIds mapping and stores mapping in hashmap
            while (line != null) {
                //System.out.println(line);
                String[] words = line.split("\t");
                if (words.length > 1) trackToSong.put(words[0], words[1]);
                /*tags.putIfAbsent(words[1], 0);
                tags.put(words[1], tags.get(words[1])+1);*/
                line = br.readLine();
            }

            //reads songIds to itemIds, so songs are compatible with tagging files whcih uses itemIds as
            // integers from 1-n (where n is number of items).
            br = new BufferedReader(new FileReader(itemIdsFile));
            line = br.readLine();
            int itemId = 1;

            while (line != null) {
                //if (itemId < 10) System.out.println(line + ", " + itemId);
                songToItemId.put(line, itemId++);
                //System.out.println(itemId);
                line = br.readLine();
            }

            //reads ratingsfile to make int-values for each user and store in hashmap userIds
            br = new BufferedReader(new FileReader(ratingFile));
            line = br.readLine();
            int userId = 1;

            while (line != null) {
                String[] words = line.split("\t");
                userIds.putIfAbsent(words[0], userId++);
                //System.out.println(words[0]);


                //if (itemId < 10) System.out.println(line + ", " + itemId);
                //songToItemId.put(line, itemId++);
                //System.out.println(itemId);
                line = br.readLine();
            }

            br = new BufferedReader(new FileReader(ratingFile));
            line = br.readLine();
            int songIdentifier = 0;
            int x = 1;

            while (line != null) {
                String[] words = line.split("\t");
                //trackToSong.put(words[0], words[1]);
                String songId = trackToSong.get(words[1]);

                //skips songs without mappable trackId FJERN KOMMENTAR
                if (songId == null) {
                    trackToSong.put(words[1], Integer.toString(songIdentifier));
                    songId = Integer.toString(songIdentifier++);
                    //x++;
                    //line = br.readLine();
                    //continue;
                }

                //skips songs without mappable songId FJERN KOMMENTAR
                if (!songToItemId.containsKey(songId)) {
                    songToItemId.put(songId, itemId++);
                    //x++;
                    //line = br.readLine();
                    //continue;
                    //System.out.println(x++);
                }

                fwRatings.write(userIds.get(words[0]) + "\t" + songToItemId.get(songId) + "\t" + words[2] + "\n");
                itemIds.add(songToItemId.get(songId));

                numRatings.putIfAbsent(words[0], 0);
                numRatings.put(words[0], numRatings.get(words[0]) + 1);

                //System.out.println("user id "+ words[0] + ", itemId " + words[1] + " / " + songId + " / " + itemId + ", rating " + words[2]);
                line = br.readLine();
            }

            br = new BufferedReader(new FileReader(tagFile));
            line = br.readLine();
            while (line != null) {
                String[] words = line.split(",");
                //if (songToItemId.containsValue(words[0])) fwTags.write(words[0] + "+\t" + words[1]);
                if (itemIds.contains(Integer.parseInt(words[0]))) fwTags.write(words[0] + "," + words[1] + "\n");
                //if (tags.get(words[1]) >= minNum) fw.write(line + "\n");
                line = br.readLine();
            }

            //because we must have a title file for the content-based recommender, we make a titlefile.
            //but we do not have any titles, so we only add the ids (it will not affect the recommendations)
            for (Integer i : itemIds) {
                fwTitles.write(i + "," + i + "\n");
            }

            fwRatings.flush();
            fwRatings.close();
            fwTags.flush();
            fwTags.close();
            fwTitles.flush();
            fwTitles.close();

            for (Map.Entry<String, Integer> entry : numRatings.entrySet()) {
                if (entry.getValue() < 20) System.out.println(entry.getKey() +" -.-.--.--");
            }
        }
        catch(IOException ie) {
            ie.printStackTrace();
            System.exit(1);
        }


    }

    //Method that substitutes all ratings values with 1-values
    public static void binarizeRatings(String inputFile, String outputFile) {

        try {
            BufferedReader br = new BufferedReader(new FileReader(inputFile));
            FileWriter fw = new FileWriter(new File(outputFile));
            String line = br.readLine();

            while (line != null) {
                String[] words = line.split("\t");
                fw.write(words[0] + "\t" + words[1] + "\t" + 1 + "\n");
                line = br.readLine();
            }
            fw.flush();
            fw.close();
        }
        catch(IOException ie) {
            ie.printStackTrace();
            System.exit(1);
        }
    }
    //TODO  change evaluation-method to measure hit-rate
    //and using several recsizes (e.g. 10,20,30,100/500),
    //format ml10m to 6k and handle tags,
    //make scalability-test (train with whole dataset) and test e.g. recs for 100 persons for ml100k, ml1m, ml10m
    //check the tags for msd, if they are correct (take som "stikkprøver"/samples across the datasets)

    //DONE binarize values, change evaluation-method to all-but-n,
}
