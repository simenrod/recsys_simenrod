package dataset;


import java.io.*;

/**
 * Created by simen on 3/21/17.
 */
public class MakeTagsFile {
    public static void main(String[] args) {
        movieLens("data/movielens/u.item", "data/movielens/item-tags", "data/movielens/titles");
    }

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
        }

        catch (IOException e){
            e.printStackTrace();
            System.exit(1);
        }
    }

}
