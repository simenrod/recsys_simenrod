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
 * Content-based filtering recommender built with Lenskit. Uses both classes that are built in LensKit and some
 * classes from org/grouplens/mooc/cbf, which is a continuation of the content-based recommender in this project:
 * https://github.com/eugenelin89/recommender_content_based.
 */
public class ContentBasedRecommender implements Recommender {
    private LenskitConfiguration config;
    private LenskitRecommender rec;
    private ItemRecommender irec;
    private String tagFile;
    private String titleFile;


    //Constructor where tag file and title file to use must be specified
    public ContentBasedRecommender(String tagFile, String titleFile){
        this.tagFile = tagFile;
        this.titleFile = titleFile;
    }

    public void initialize() {/*nothing to inialize*/}


    //Method that configures and trains the content-based recommender
    public void update(String trainingFile) {

        //Configures the content-based recommender
        config = new LenskitConfiguration();
        config.bind(EventDAO.class).to(MOOCRatingDAO.class);
        config.set(RatingFile.class).to(new File(trainingFile));
        config.bind(ItemDAO.class).to(CSVItemTagDAO.class);
        config.set(TagFile.class).to(new File(tagFile));
        config.set(TitleFile.class).to(new File(titleFile));
        config.bind(ItemScorer.class).to(TFIDFItemScorer.class);

        //builds recommender
        try {
            rec = LenskitRecommender.build(config);
            irec = rec.getItemRecommender();
        } catch (RecommenderBuildException e) {
            e.printStackTrace();
            System.exit(1);
        }

    }

    //returns an array of num recommendations of itemids to the user with this userid
    public int[] recommend(int userId, int num) {
        List<ScoredId> recommendations = irec.recommend(userId, num);
        int[] recommendedItems = new int[num];
        int i = 0;

        for (ScoredId r : recommendations) {
            recommendedItems[i++] = toIntExact(r.getId());
        }
        return recommendedItems;
    }

    //returns info about which type of recommender this is
    public String getInfo() {
        return "Content-based";
    }

    public void close() {/*nothing to close*/}
}
