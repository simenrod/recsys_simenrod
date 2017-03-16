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
import org.grouplens.lenskit.iterative.IterationCount;
import org.grouplens.lenskit.knn.MinNeighbors;
import org.grouplens.lenskit.knn.NeighborhoodSize;
import org.grouplens.lenskit.knn.item.ItemItemScorer;
import org.grouplens.lenskit.knn.item.ModelSize;
import org.grouplens.lenskit.knn.item.NeighborhoodScorer;
import org.grouplens.lenskit.knn.item.SimilaritySumNeighborhoodScorer;
import org.grouplens.lenskit.knn.user.UserSimilarity;
import org.grouplens.lenskit.knn.user.UserUserItemScorer;
import org.grouplens.lenskit.knn.user.UserVectorSimilarity;
import org.grouplens.lenskit.mf.funksvd.FeatureCount;
import org.grouplens.lenskit.mf.funksvd.FunkSVDItemScorer;
import org.grouplens.lenskit.scored.ScoredId;
import org.grouplens.lenskit.slopeone.DeviationDamping;
import org.grouplens.lenskit.slopeone.SlopeOneItemScorer;
import org.grouplens.lenskit.transform.normalize.BaselineSubtractingUserVectorNormalizer;
import org.grouplens.lenskit.transform.normalize.MeanCenteringVectorNormalizer;
import org.grouplens.lenskit.transform.normalize.UserVectorNormalizer;
import org.grouplens.lenskit.transform.normalize.VectorNormalizer;
import org.grouplens.lenskit.vectors.similarity.PearsonCorrelation;
import org.grouplens.lenskit.vectors.similarity.VectorSimilarity;

import java.io.File;
import java.util.List;
import java.util.Scanner;

/**
 * Created by simen on 1/17/17.
 */
public class InteractiveRecs {
    public static void main(String[] args) {
        System.out.println("Test");
        InteractiveRecs ir = new InteractiveRecs();
        ir.makeRecommender();
        //ir.slopeOne();
        //ir.implItemItem();
        //ir.ubfc();
    }

    //item-based collaborative filtering recommender
    public void makeRecommender() {
        LenskitConfiguration config = new LenskitConfiguration();
        // Use item-item CF to score items
        config.bind(ItemScorer.class)
                .to(ItemItemScorer.class);
        // let's use personalized mean rating as the baseline/fallback predictor.
        // 2-step process:
        // First, use the user mean rating as the baseline scorer
        config.bind(BaselineScorer.class, ItemScorer.class)
                .to(UserMeanItemScorer.class);
        // Second, use the item mean rating as the base for user means
        config.bind(UserMeanBaseline.class, ItemScorer.class)
                .to(ItemMeanRatingItemScorer.class);
        // and normalize ratings by baseline prior to computing similarities
        config.bind(UserVectorNormalizer.class)
                .to(BaselineSubtractingUserVectorNormalizer.class);

        config.bind(EventDAO.class).to(new SimpleFileRatingDAO(new File("ratings.csv"), ","));

        try {
            LenskitRecommender rec = LenskitRecommender.build(config);

            ItemRecommender irec = rec.getItemRecommender();

            Scanner sc = new Scanner(System.in);
            int input = 0;
            System.out.println("Tast inn brukerID som det skal gis anbefalinger for. -1 for avslutt");

            while (input != -1) {

                input = Integer.parseInt(sc.next());

                List<ScoredId> recommendations = irec.recommend(input, 10);

                System.out.println("10 best recommendations for user " + input + " (item-based recs): ");
                for (ScoredId si : recommendations) {
                    System.out.println("ID: " + si.getId() + ", score:  " + si.getScore());
                }

                System.out.println("Tast inn brukerID som det skal gis anbefalinger for. -1 for avslutt");
            }

        } catch (RecommenderBuildException e) {
            e.printStackTrace();
        }
    }

    //User-based collaborative filtering recommender
    public void ubfc() {
        LenskitConfiguration config = new LenskitConfiguration();
        // Use item-item CF to score items
        config.bind(ItemScorer.class)
                .to(UserUserItemScorer.class);

        config.set(NeighborhoodSize.class).to(50);


        // let's use personalized mean rating as the baseline/fallback predictor.
        // 2-step process:
        // First, use the user mean rating as the baseline scorer
        config.bind(BaselineScorer.class, ItemScorer.class)
                .to(UserMeanItemScorer.class);
        // Second, use the item mean rating as the base for user means
        config.bind(UserMeanBaseline.class, ItemScorer.class)
                .to(ItemMeanRatingItemScorer.class);
        // and normalize ratings by baseline prior to computing similarities
        config.within(UserVectorNormalizer.class)
                .bind(VectorNormalizer.class)
                .to(MeanCenteringVectorNormalizer.class);

        config.bind(EventDAO.class).to(new SimpleFileRatingDAO(new File("ratings.csv"), ","));


        /*
        //Use PerasonCorrelation instead of CosineSimilarity
        config.within(UserVectorSimilarity.class)
                .bind(VectorSimilarity.class)
                .to(PearsonCorrelation.class);*/
        //config.bind(UserVectorSimilarity.class).
        //to(PearsonCorrelation.class);

        try {
            LenskitRecommender rec = LenskitRecommender.build(config);

            ItemRecommender irec = rec.getItemRecommender();

            Scanner sc = new Scanner(System.in);
            int input = 0;
            System.out.println("Tast inn brukerID som det skal gis anbefalinger for. -1 for avslutt");
            input = Integer.parseInt(sc.next());

            while (input != -1) {

                List<ScoredId> recommendations = irec.recommend(input, 10);

                System.out.println("10 best recommendations for user " + input + " (user-based recs): ");
                for (ScoredId si : recommendations) {
                    System.out.println("ID: " + si.getId() + ", score:  " + si.getScore());
                }

                System.out.println("Tast inn brukerID som det skal gis anbefalinger for. -1 for avslutt");
                input = Integer.parseInt(sc.next());
            }

        } catch (RecommenderBuildException e) {
            e.printStackTrace();
        }
    }

    public void fsvd() {
        LenskitConfiguration config = new LenskitConfiguration();
        // Use item-item CF to score items
        config.bind(ItemScorer.class)
                .to(FunkSVDItemScorer.class);

        // let's use personalized mean rating as the baseline/fallback predictor.
        // 2-step process:
        // First, use the user mean rating as the baseline scorer
        config.bind(BaselineScorer.class, ItemScorer.class)
                .to(UserMeanItemScorer.class);
        // Second, use the item mean rating as the base for user means
        config.bind(UserMeanBaseline.class, ItemScorer.class)
                .to(ItemMeanRatingItemScorer.class);
        /*// and normalize ratings by baseline prior to computing similarities
        config.within(UserVectorNormalizer.class)
                .bind(VectorNormalizer.class)
                .to(MeanCenteringVectorNormalizer.class);*/

        config.set(FeatureCount.class).to(25);
        config.set(IterationCount.class).to(125);

        config.bind(EventDAO.class).to(new SimpleFileRatingDAO(new File("ratings.csv"), ","));

        try {
            LenskitRecommender rec = LenskitRecommender.build(config);

            ItemRecommender irec = rec.getItemRecommender();

            Scanner sc = new Scanner(System.in);
            int input = 0;
            System.out.println("Tast inn brukerID som det skal gis anbefalinger for. -1 for avslutt");
            input = Integer.parseInt(sc.next());

            while (input != -1) {

                List<ScoredId> recommendations = irec.recommend(input, 10);

                System.out.println("10 best recommendations for user " + input + " (matrix factorization (fsvd) recs): ");
                for (ScoredId si : recommendations) {
                    System.out.println("ID: " + si.getId() + ", score:  " + si.getScore());
                }

                System.out.println("Tast inn brukerID som det skal gis anbefalinger for. -1 for avslutt");
                input = Integer.parseInt(sc.next());
            }

        } catch (RecommenderBuildException e) {
            e.printStackTrace();
        }
    }

    public void slopeOne() {
        LenskitConfiguration config = new LenskitConfiguration();
        // Use item-item CF to score items
        config.bind(ItemScorer.class)
                .to(SlopeOneItemScorer.class);

        // let's use personalized mean rating as the baseline/fallback predictor.
        // 2-step process:
        // First, use the user mean rating as the baseline scorer
        config.bind(BaselineScorer.class, ItemScorer.class)
                .to(UserMeanItemScorer.class);
        // Second, use the item mean rating as the base for user means
        config.bind(UserMeanBaseline.class, ItemScorer.class)
                .to(ItemMeanRatingItemScorer.class);
        /*// and normalize ratings by baseline prior to computing similarities
        config.within(UserVectorNormalizer.class)
                .bind(VectorNormalizer.class)
                .to(MeanCenteringVectorNormalizer.class);*/
        config.set(DeviationDamping.class).to(0.0d);

        config.bind(EventDAO.class).to(new SimpleFileRatingDAO(new File("ratings2.csv"), ","));

        try {
            LenskitRecommender rec = LenskitRecommender.build(config);

            ItemRecommender irec = rec.getItemRecommender();

            Scanner sc = new Scanner(System.in);
            int input = 0;
            System.out.println("Tast inn brukerID som det skal gis anbefalinger for. -1 for avslutt");
            input = Integer.parseInt(sc.next());

            while (input != -1) {

                List<ScoredId> recommendations = irec.recommend(input, 10);

                System.out.println("10 best recommendations for user " + input + " (slope one recs): ");
                for (ScoredId si : recommendations) {
                    System.out.println("ID: " + si.getId() + ", score:  " + si.getScore());
                }

                System.out.println("Tast inn brukerID som det skal gis anbefalinger for. -1 for avslutt");
                input = Integer.parseInt(sc.next());
            }

        } catch (RecommenderBuildException e) {
            e.printStackTrace();
        }
    }

    public void implItemItem() {
        LenskitConfiguration config = new LenskitConfiguration();
        config.bind(ItemScorer.class).to(ItemItemScorer.class);

        config.set(MinNeighbors.class).to(2);
        config.set(ModelSize.class).to(1000);
        config.bind(NeighborhoodScorer.class).to(SimilaritySumNeighborhoodScorer.class);
        config.bind(BaselineScorer.class, ItemScorer.class).to(UserMeanItemScorer.class);
        config.bind(UserMeanBaseline.class, ItemScorer.class).to(ItemMeanRatingItemScorer.class);

        //config.bind(EventDAO.class).to(new SimpleFileRatingDAO(new File("/home/simen/Documents/datasett/movielens-binary"), ","));
        config.bind(EventDAO.class).to(new SimpleFileRatingDAO(new File("/home/simen/Documents/datasett/movielens-binary"), "\t"));

        try {
            LenskitRecommender rec = LenskitRecommender.build(config);

            ItemRecommender irec = rec.getItemRecommender();
            //ItemRecommender irec = rec.getItemRecommender();

            Scanner sc = new Scanner(System.in);
            int input = 0;
            System.out.println("Tast inn brukerID som det skal gis anbefalinger for. -1 for avslutt");
            input = Integer.parseInt(sc.next());

            while (input != -1) {

                List<ScoredId> recommendations = irec.recommend(input, 10);

                System.out.println("10 best recommendations for user " + input + " (impl item-item): ");
                for (ScoredId si : recommendations) {
                    System.out.println("ID: " + si.getId() + ", score:  " + si.getScore());
                }

                System.out.println("Tast inn brukerID som det skal gis anbefalinger for. -1 for avslutt");
                input = Integer.parseInt(sc.next());
            }

        } catch (RecommenderBuildException e) {
            e.printStackTrace();
        }
    }
}
