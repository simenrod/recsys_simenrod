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
import org.grouplens.lenskit.vectors.similarity.CosineVectorSimilarity;
import org.grouplens.lenskit.vectors.similarity.VectorSimilarity;
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


    public ItemBasedRecommender() {
        k = 20;
    } //constructor which uses default neighbourhoodsize

    public ItemBasedRecommender(int k) {
        this.k = k;
    } //constructor where k (number of neighbours) can be specified

    //TODO decide if all recommenders must have an initialize-method
    public void initialize() {

    }

    //Method that trains the recommender with the trainingFile, so it is ready to produce recommendations
    public void update(String trainingFile) {

        //configurates the item-based recommender
        config = new LenskitConfiguration();
        config.bind(ItemScorer.class).to(ItemItemScorer.class);
        config.bind(VectorSimilarity.class).to(CosineVectorSimilarity.class);
        config.set(MinNeighbors.class).to(2);
        config.set(ModelSize.class).to(250);
        config.bind(NeighborhoodScorer.class).to(SimilaritySumNeighborhoodScorer.class);
        config.set(NeighborhoodSize.class).to(k);
        config.bind(EventDAO.class).to(new SimpleFileRatingDAO(new File(trainingFile), "\t"));

        //builds the item-recommender irec which is stored as a class variable
        try {
            rec = LenskitRecommender.build(config);
            irec = rec.getItemRecommender();
        } catch (RecommenderBuildException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    //method that recommends num items of recommendations for user userId with item-based collaborative filtering
    public int[] recommend(int userId, int num) {
        List<ScoredId> recommendations = irec.recommend(userId, num);
        int[] recommendedItems = new int[num];
        int i = 0;

        for (ScoredId r : recommendations) {
            recommendedItems[i++] = toIntExact(r.getId());
        }
        return recommendedItems;
    }

    //returns info about this recommender as a String
    public String getInfo() {
        return "Item-based Collaborative filtering | k = " + k;
    }

}


