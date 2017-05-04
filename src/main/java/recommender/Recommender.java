package recommender;


/**
 * Created by simen on 3/16/17.
 * Recommender interface that all of the recommendation algorithms implement, to ensure that all
 * algorithms have same functionality.
 */
public interface Recommender {

    /**
     * Method that initializes Recommender, if anything must be initialized for the recommender to work.
     */
    public void initialize();

    /**
     * Method that trains the recommender with a training file
     * @param trainingFile file used for training
     */
    public void update(String trainingFile);


    /**
     *
     * @param userId The id of the user - given as int
     * @param num Number of recommendations to give
     * @return Returns the itemids of the recommendations for the user. Does not include already rated items
     */
    public int[] recommend(int userId, int num);

    /**
     * @return Returns info about which type of recommender this is, and parameters (if any)
     */
    public String getInfo();

    /**
     * Method that shuts down configuration, if any.
     */
    public void close();
}
