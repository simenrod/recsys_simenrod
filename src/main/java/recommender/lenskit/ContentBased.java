package recommender.lenskit;

import org.grouplens.lenskit.ItemRecommender;
import org.grouplens.lenskit.ItemScorer;
import org.grouplens.lenskit.RecommenderBuildException;
import org.grouplens.lenskit.core.LenskitConfiguration;
import org.grouplens.lenskit.core.LenskitRecommender;
import org.grouplens.lenskit.data.dao.EventDAO;
import org.grouplens.lenskit.data.dao.ItemDAO;
import org.grouplens.lenskit.data.dao.UserDAO;
import org.grouplens.lenskit.scored.ScoredId;
import org.grouplens.mooc.cbf.TFIDFItemScorer;
import org.grouplens.mooc.cbf.dao.*;
import recommender.Recommender;

import java.io.File;
import java.util.List;

import static java.lang.Math.toIntExact;

/**
 * Created by simen on 3/21/17.
 */
public class ContentBased implements Recommender {
    private LenskitConfiguration config;
    private LenskitRecommender rec;
    private ItemRecommender irec;
    private String tagFile;
    private String titleFile;


    public void initialize(String tagFile, String titleFile) {
        this.tagFile = tagFile;
        this.titleFile = titleFile;
    }

    public void update(String trainingFile) {
        config = new LenskitConfiguration();
        // configure the rating data source
        config.bind(EventDAO.class)
                .to(MOOCRatingDAO.class);
        config.set(RatingFile.class)
                .to(new File(trainingFile));

        // use custom item and user DAOs
        // specify item DAO implementation with tags
        config.bind(ItemDAO.class)
                .to(CSVItemTagDAO.class);
        // specify tag file
        config.set(TagFile.class)
                .to(new File(tagFile));
        // and title file
        config.set(TitleFile.class)
                .to(new File(titleFile));

        /*// our user DAO can look up by user name
        config.bind(UserDAO.class)
                .to(MOOCUserDAO.class);
        config.set(UserFile.class)
                .to(new File("data/users.csv"));*/

        // use the TF-IDF scorer you will implement to score items
        config.bind(ItemScorer.class)
                .to(TFIDFItemScorer.class);

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
}
