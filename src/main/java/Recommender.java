import org.apache.spark.mllib.recommendation.Rating;

/**
 * Created by simen on 3/16/17.
 */
public interface Recommender {

    //public static Rating[] getRecsForUser(int id, int num);
    //public void init2();

    public void update(String trainingFile);
    public int[] recommend(int userId, int num);
}
