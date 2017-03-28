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
public class ContentBasedRecommender implements Recommender {
    private LenskitConfiguration config;
    private LenskitRecommender rec;
    private ItemRecommender irec;
    private String tagFile;
    private String titleFile;


    public ContentBasedRecommender(String tagFile, String titleFile){
        this.tagFile = tagFile;
        this.titleFile = titleFile;
    }

    public void initialize() {/*nothing to inialize*/}


    public void update(String trainingFile) {

        //Configures the content-based recommender
        config = new LenskitConfiguration();
        config.bind(EventDAO.class).to(MOOCRatingDAO.class); //could maybe have used SimpleFileRatingDAO
        config.set(RatingFile.class).to(new File(trainingFile));
        config.bind(ItemDAO.class).to(CSVItemTagDAO.class);
        config.set(TagFile.class).to(new File(tagFile));
        config.set(TitleFile.class).to(new File(titleFile));
        config.bind(ItemScorer.class).to(TFIDFItemScorer.class);

        /*// our user DAO can look up by user name
        config.bind(UserDAO.class)
                .to(MOOCUserDAO.class);
        config.set(UserFile.class)
                .to(new File("data/users.csv"));*/

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

    public String getInfo() {
        return "Content-based";
    }

    public void close() {/*nothing to close*/}
}
