package dataset;


import it.unimi.dsi.fastutil.Hash;
import shapeless.tag;

import java.io.*;
import java.util.*;

/**
 * Created by simen on 3/21/17.
 * Class used for reformating datasets, e.g. changing format of tags dataset and ratings dataset
 * so they get the same format.
 */
public class ReformatData {
    private static HashSet<String> stopWords;

    public static void main(String[] args) {
        //movieLensTagsSmall("data/movielens/u.item", "data/movielens/item-tags", "data/movielens/titles");
        /*bookCrossing("data/bx6k/ratings10m","data/bx/bx-books.csv", "data/bx6k/ratings10m-transformed",
                "data/bx6k/item-tags","data/bx6k/titles");*/
        //makeSubset("data/bx/bx.csv", "data/bx6k/ratings10m",";", 6000, 20, 200);
        //makeSubset("/home/simen/Desktop/train_triplets.txt", "data/msd6k/ratings4","\t", 6000, 30, 100);
        //makeSubset("/home/simen/Desktop/ml-1m/ratings10m.dat", "data/ml6k/ratings10m","::", 6000, 20, 200);
        /*msd("data/msd6k/ratings3", "data/msd6k/song-to-track", "data/msd6k/itemids",
                "data/msd6k/ratings10m-transformed", "/home/simen/Desktop/msd/tags-reduced",
                "data/msd6k/tags", "data/msd6k/titles");*/

        //reduceTags("data/msd6k/tags", "data/msd6k/tags-reduced", 10);
        //reduceMsdTags("/home/simen/Desktop/msd/tid_tag.csv", "/home/simen/Desktop/msd/tags-reduced", 30);
        //binarizeRatings("data/msd6k/ratings10m", "data/msd6k/binarized-ratings10m");
        //makeSubset("/home/simen/Desktop/ml-10M100K/ratings10m.dat","data/ml6k/ratings10m", "::", 6000,20,200);
        /*movieLensTagsLarge("/home/simen/Desktop/ml-10M100K/movies.dat", "data/ml6k/ratings10m",
                "data/ml6k/tags", "data/ml6k/titles");*/
        /*movieLensTagsLarge("/home/simen/Desktop/ml-10M100K/movies.dat", "data/ml6k/ratings10m",
                "data/ml6k/tags", "data/ml6k/titles");*/
        /*movieLensTagsLarge("/home/simen/Desktop/ml-10M100K/movies.dat", "/home/simen/Desktop/ml-10M100K/ratings10m.dat",
                "data/ml10m/tags", "data/ml10m/titles");*/
        movieLensTagsLarge("/home/simen/Desktop/ml-10M100K/movies.dat", "data/ml10m/ratings5m",
                "data/ml10m/tags5m", "data/ml10m/titles5m", "\t");
        movieLensTagsLarge("/home/simen/Desktop/ml-10M100K/movies.dat", "data/ml10m/ratings1m",
                "data/ml10m/tags1m", "data/ml10m/titles1m", "\t");
        movieLensTagsLarge("/home/simen/Desktop/ml-10M100K/movies.dat", "data/ml10m/ratings100k",
                "data/ml10m/tags100k", "data/ml10m/titles100k", "\t");
        //binarizeRatings("/home/simen/Desktop/ml-10M100K/ratings10m.dat", "data/ml10m/ratings10m", "::");

        /*bookCrossing("data/bx6k/ratings10m","data/bx/bx-books.csv", "data/bx6k/ratings10m-transformed",
                "data/bx6k/item-tags","data/bx6k/titles");*/
        //reduceTags("data/bx6k/item-tags", "data/bx6k/item-tags-reduced", 2);
        //printInfoAboutData("data/msd6k/ratings3", "\t");
        //printInfoAboutData("data/bx6k/ratings10m-transformed", "\t");
        //printInfoAboutData("data/ml6k/ratings10m", "::");
        //featureExtraction();
        //binarizeTrainingFiles("data/bx6k/ab10","data/bx6k/bab10", 1, 5);
        //binarizeTrainingFiles("data/msd6k/ab10","data/msd6k/bab10", 1, 5);
        //binarizeTrainingFiles("data/ml6k/ab10","data/ml6k/bab10", 1, 5);
        /*binarizeRatings("data/bx6k/ratings10m-transformed", "data/bx6k/binarized-ratings10m", "\t");
        binarizeRatings("data/ml6k/ratings10m", "data/ml6k/binarized-ratings10m", "::");
        binarizeRatings("data/msd6k/ratings10m-transformed", "data/msd6k/binarized-ratings10m", "\t");*/
    }


    //makes a new file with tags for movies, and a file with the titles of the movies (for movielens 100k dataset)
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

                for (int i = 0; i < 19; i++) {
                    if (datas[5+i].equals("1")) {
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
    public static void movieLensTagsLarge(String tagsFile, String ratingFile, String outputTagFile, String outputTitleFile, String splitter) {
        try {
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
                String[] datas = line.split("::");
                String itemId = datas[0];

                if (itemIds.contains(itemId)) {
                    fwTitles.write(itemId + "," + datas[1] + "\n");
                    for (String s : datas[2].split("\\|")) {
                        fw.write(itemId + "," + s + "\n");
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

    //Reformatting of Book-Crossing dataset. Makes a new file with ratings (where isbn is substitued with integer values
    // because some of the recommenders not handles itemids as strings), a file for the tags of the items and a file
    // with titles of the items.
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
                //Format of input:
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

                fwTitles.write(itemId + "," + datas[1] + "\n"); //adds title to title file

                //adds all words in title (except for words in stoplist) to tags file
                for (String s : datas[1].toLowerCase().split(" ")) {
                    String word = s.replaceAll("[.\\-!:\\\\;,()\\[\\]\\}\\{\\}#\\\"\\'<>|/*^_+?]","");;
                    if (word.equals("")) continue;
                    if (!inStoplist(word)) fwTags.write(itemId + "," + word + "\n");
                }

                //writes author, year published and publisher to tags file
                for (int i = 2; i <= 4; i++) {
                    if (!datas[i].equals("")) fwTags.write(itemId + "," + datas[i].toLowerCase() + "\n");
                }

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


    //Stoplist to use for filtering out common words with little meaning from tags dataset
    public static void updateStoplist() {
        String[] stoplist = {"a", "an", "and", "are", "as", "at", "be", "but", "by",
                "for", "if", "in", "into", "is", "it",
                "no", "not", "of", "on", "or", "such",
                "that", "the", "their", "then", "there", "these",
                "they", "this", "to", "was", "will", "with"};
        stopWords = new HashSet<>();
        for (String s : stoplist) {
            stopWords.add(s);
        }
    }

    //Method that returns true if a word is in the stoplist
    public static boolean inStoplist(String word) {
        if (stopWords == null) {
            updateStoplist();
        }
        return stopWords.contains(word);
    }

    //method that reads bx ratings, writes to file with <userid, itemid, rating> to file (substituting isbn with integers)
    //and returns a hashmap with one userid value for each isbn read
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
                }

                if (hm.get(words[1]) == null) {
                    hm.put(words[1], new Integer(counter++));
                }
                int book_nr = hm.get(words[1]);

                fw.write(words[0]+"\t"+book_nr+"\t"+words[2]+"\n");
                line = br.readLine();
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
        HashMap<String, Integer> users = new HashMap<>();
        HashSet<String> usersToKeep = new HashSet<>();

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
            //and we store the x first users who have ratings in the range we have chosen (ensures random users)
            int i = 0;
            for (Map.Entry<String,Integer> entry : list) {
                if (entry.getValue() >= minRatings && entry.getValue() <= maxRatings) {
                    usersToKeep.add(entry.getKey());
                    if (i >= numUsers) break;
                    i++;
                }
            }

            br = new BufferedReader(new FileReader(inputFile));
            FileWriter fw = new FileWriter(new File(outputFile));
            line = br.readLine();

            //Prints the subset, by only only printing the ratings of the x randomly chosen users
            while (line != null) {
                String[] words = line.split(delimiter);
                if (usersToKeep.contains(words[0])) {
                    fw.write(line + "\n");
                }
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

    //Reduces the tagfile by removing tags that have frequency smaller or equal to minNum (for bookcrossing and million song)
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

    /*Method that reformats million song dataset rating data and tag data. ratingFile is the rating-file to use,
    *trackToSongFile is a file which maps track ids to song ids (because the tags use another set of ids than the rating
    * file). The songIds are stored in the itemIdsFile, and the index of a song in the file is the item id used
    * in the tagFile. Therefore, the ratings written to outputRatingFile contain these itemIDs.
    **/
    public static void msd(String ratingFile, String trackToSongFile, String itemIdsFile, String outputRatingFile,
                           String tagFile, String outputTagFile, String outputTitleFile) {

        HashMap<String, String> trackToSong = new HashMap<>();
        HashMap<String, Integer> songToItemId = new HashMap<>();
        HashSet<Integer> itemIds = new HashSet<>();
        HashMap<String, Integer> userIds = new HashMap<>();
        HashMap<String, Integer> numRatings = new HashMap<>();

        try {
            BufferedReader br = new BufferedReader(new FileReader(trackToSongFile));
            FileWriter fwRatings = new FileWriter(new File(outputRatingFile));
            FileWriter fwTags = new FileWriter(new File(outputTagFile));
            FileWriter fwTitles = new FileWriter(new File(outputTitleFile));

            String line = br.readLine();

            //reads trackIds to songIds mapping and stores mapping in hashmap
            while (line != null) {
                String[] words = line.split("\t");
                if (words.length > 1) trackToSong.put(words[0], words[1]);
                line = br.readLine();
            }

            //reads songIds to itemIds, so songs are compatible with tagging files which uses itemIds as
            // integers from 1-n (where n is number of items).
            br = new BufferedReader(new FileReader(itemIdsFile));
            line = br.readLine();
            int itemId = 1;
            while (line != null) {
                songToItemId.put(line, itemId++);
                line = br.readLine();
            }

            //reads ratingsfile to make int-ids for each user and store in hashmap userIds
            br = new BufferedReader(new FileReader(ratingFile));
            line = br.readLine();
            int userId = 1;
            while (line != null) {
                String[] words = line.split("\t");
                userIds.putIfAbsent(words[0], userId++);
                line = br.readLine();
            }

            //reads rating file, and writes to file with itemids that are compatible with tag file
            br = new BufferedReader(new FileReader(ratingFile));
            line = br.readLine();
            int songIdentifier = 0;
            int x = 1;
            while (line != null) {
                String[] words = line.split("\t");
                String songId = trackToSong.get(words[1]);

                //if song without mappable trackId -> adds to hashmap and gets songId
                if (songId == null) {
                    trackToSong.put(words[1], Integer.toString(songIdentifier));
                    songId = Integer.toString(songIdentifier++);
                }
                //if songs without mappable songId -> adds to hashmap
                if (!songToItemId.containsKey(songId)) {
                    songToItemId.put(songId, itemId++);
                }

                //Writes ratings to file with itemids instead of songIds
                fwRatings.write(userIds.get(words[0]) + "\t" + songToItemId.get(songId) + "\t" + words[2] + "\n");
                itemIds.add(songToItemId.get(songId));

                numRatings.putIfAbsent(words[0], 0);
                numRatings.put(words[0], numRatings.get(words[0]) + 1);

                line = br.readLine();
            }

            //Writes tag data to tagfile, only storing info about the rated items
            br = new BufferedReader(new FileReader(tagFile));
            line = br.readLine();
            while (line != null) {
                String[] words = line.split(",");
                if (itemIds.contains(Integer.parseInt(words[0]))) fwTags.write(words[0] + "," + words[1] + "\n");
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
        }
        catch(IOException ie) {
            ie.printStackTrace();
            System.exit(1);
        }
    }


    //Method that substitutes all ratings values with 1-values
    public static void binarizeRatings(String inputFile, String outputFile, String delimiter) {

        try {
            BufferedReader br = new BufferedReader(new FileReader(inputFile));
            FileWriter fw = new FileWriter(new File(outputFile));
            String line = br.readLine();

            while (line != null) {
                String[] words = line.split(delimiter);
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

    //Reads train-files (from nr "from" to nr "to") from inputDirectory and writes as binary ratings to outputDirectory.
    public static void binarizeTrainingFiles(String inputDirectory, String outputDirectory, int from, int to) {
        int numFiles = to - from + 1;

        for (int i = 0; i < numFiles; i++) {
            String inFile = inputDirectory + "/train" + (from + i);
            String outFile = outputDirectory + "/train" + (from + i);
            binarizeRatings(inFile, outFile, "\t");
        }
    }
}
