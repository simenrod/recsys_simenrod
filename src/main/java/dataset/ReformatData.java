package dataset;


import it.unimi.dsi.fastutil.Hash;

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by simen on 3/21/17.
 */
public class ReformatData {
    private static HashSet<String> stopWords;

    public static void main(String[] args) {
        //movieLens("data/movielens/u.item", "data/movielens/item-tags", "data/movielens/titles");
        bookCrossing("data/bx/bx.csv","data/bx/bx-books.csv", "data/bx/ratings",
                "data/bx/item-tags","data/bx/titles");
    }

    //makes a new file with tags for movies, and a file with the titles of the movies
    public static void movieLens(String fileName, String outputTagFile, String outputTitleFile) {
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
			/*for (String s : splitting) {
				System.out.println(s);
			}*/
            while (line != null) {
                //itemid|name (year)|date-year||website|0|0|0|1|1|1|0|0|0|0|0|0|0|0|0|0|0|0|0
                //19 tags - starter på indeks 5
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

    public static void makeSubset(String inputFile, String outputFile, String delimiter) {
        //read all lines
        //for each line, store userID in hashset h1
        //shuffle all userIds in set
        //for the x first (e.g. 6000), store the ids in another hashset h2
        //
        //read all lines again
        //for each line, if userid is in hashset h2, write the line to new file

        /*HashSet<String> users;
        try {
            BufferedReader br = new BufferedReader(new FileReader(inputFile));
            FileWriter fw = new FileWriter(new File(outputFile));

            String line = br.readLine();
            int lineNr = 0;

            while (line != null) {
                String[]words = line.split(";");

                for (int i = 0; i < words.length; i++) {
                    words[i] = words[i].replace("\"","");
                    //System.out.println(words[i]);
                }

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
        }*/
    }

}
