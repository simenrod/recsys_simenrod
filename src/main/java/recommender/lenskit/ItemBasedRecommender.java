package recommender.lenskit;

import org.grouplens.lenskit.ItemRecommender;
import org.grouplens.lenskit.ItemScorer;
import org.grouplens.lenskit.RecommenderBuildException;
import org.grouplens.lenskit.baseline.BaselineScorer;
import org.grouplens.lenskit.baseline.ItemMeanRatingItemScorer;
import org.grouplens.lenskit.baseline.UserMeanBaseline;
import org.grouplens.lenskit.baseline.UserMeanItemScorer;
import org.grouplens.lenskit.core.LenskitConfiguration;
import org.grouplens.lenskit.core.LenskitRecommender;
import org.grouplens.lenskit.data.dao.EventDAO;
import org.grouplens.lenskit.data.dao.SimpleFileRatingDAO;
import org.grouplens.lenskit.knn.MinNeighbors;
import org.grouplens.lenskit.knn.NeighborhoodSize;
import org.grouplens.lenskit.knn.item.ItemItemScorer;
import org.grouplens.lenskit.knn.item.ModelSize;
import org.grouplens.lenskit.knn.item.NeighborhoodScorer;
import org.grouplens.lenskit.knn.item.SimilaritySumNeighborhoodScorer;
import org.grouplens.lenskit.scored.ScoredId;
import recommender.Recommender;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import static java.lang.Math.toIntExact;

/**
 * Created by simen on 2/15/17.
 */
public class ItemBasedRecommender implements Recommender {
    private LenskitConfiguration config;
    private LenskitRecommender rec;
    private ItemRecommender irec;
    private int k; //nr of neighbours to use



    public static void main(String[] args) {
        System.out.println("1");
        ItemBasedRecommender implRec = new ItemBasedRecommender();
        System.out.println("2");
        implRec.initiate();
        System.out.println("3");
        implRec.writeTopNToFile();
        System.out.println("4");

    }

    public ItemBasedRecommender() {
        k = 20;
    }

    public ItemBasedRecommender(int k) {
        this.k = k;
    }

    //remove
    public void initialize() {

    }

    public void update(String trainingFile) {
        config = new LenskitConfiguration();
        config.bind(ItemScorer.class).to(ItemItemScorer.class);
        config.set(MinNeighbors.class).to(2);
        config.set(ModelSize.class).to(1000);
        config.bind(NeighborhoodScorer.class).to(SimilaritySumNeighborhoodScorer.class);
        config.bind(BaselineScorer.class, ItemScorer.class).to(UserMeanItemScorer.class);
        config.bind(UserMeanBaseline.class, ItemScorer.class).to(ItemMeanRatingItemScorer.class);
        config.set(NeighborhoodSize.class).to(k);
        //config.bind(EventDAO.class).to(new SimpleFileRatingDAO(new File("/home/simen/Documents/datasett/movielens-binary"), ","));
        config.bind(EventDAO.class).to(new SimpleFileRatingDAO(new File(trainingFile), "\t"));

        //TODO
        // Proev aa bytte SimpleFileRatingDAO til TextEvenDAO pga sfrdao er deprecated
        //fjerne unoedvendige ting / faa til aa stemme med kodesnutt i oppgava

        try {
            rec = LenskitRecommender.build(config);
            irec = rec.getItemRecommender();
        } catch (RecommenderBuildException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public int[] recommend(int userId, int num) {
        List<ScoredId> recommendations = irec.recommend(userId, num);
        int[] recommendedItems = new int[num];
        int i = 0;

        for (ScoredId r : recommendations) {
            recommendedItems[i++] = toIntExact(r.getId());
            //fw.write(r.getId() + "\t");
        }
        return recommendedItems;
    }

    public void initiate() {
        System.out.println("a");
        config = new LenskitConfiguration();
        config.bind(ItemScorer.class).to(ItemItemScorer.class);
        System.out.println("b");
        config.set(MinNeighbors.class).to(2);
        config.set(ModelSize.class).to(1000);
        config.bind(NeighborhoodScorer.class).to(SimilaritySumNeighborhoodScorer.class);
        config.bind(BaselineScorer.class, ItemScorer.class).to(UserMeanItemScorer.class);
        config.bind(UserMeanBaseline.class, ItemScorer.class).to(ItemMeanRatingItemScorer.class);
        System.out.println("c");
        //config.bind(EventDAO.class).to(new SimpleFileRatingDAO(new File("/home/simen/Documents/datasett/movielens-binary"), ","));
        config.bind(EventDAO.class).to(new SimpleFileRatingDAO(new File("/home/simen/Documents/datasett/crossfold-movielens-binary/training"), "\t"));

        //Proev aa bytte SimpleFileRatingDAO til TextEvenDAO pga sfrdao er deprecated
        System.out.println("d");
        try {
            System.out.println("e");
            rec = LenskitRecommender.build(config);
            System.out.println("f");
            irec = rec.getItemRecommender();
            System.out.println("g");
        } catch (RecommenderBuildException e) {
            e.printStackTrace();
        }
    }

    public void writeTopNToFile() {
        //FORTSETT HER: Skriv top-n recs for hver bruker til fil
        //userid, itemid-rec 1, ..., itemid-rec n,
        try {
            System.out.println("a");
            FileWriter fw = new FileWriter(new File("/home/simen/Documents/datasett/kjoring/recommender.lenskit.data"));
            int users = 943;
            int n = 10;
            System.out.println("b");
            for (int i = 1; i <= users; i++) {
                fw.write(i+"\t");
                //Rating[] recs = getRecsForUserExcludeRatedItems(i, n);
                List<ScoredId> recommendations = irec.recommend(i, 10);
                for (ScoredId r : recommendations) {
                    fw.write(r.getId() + "\t");
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


