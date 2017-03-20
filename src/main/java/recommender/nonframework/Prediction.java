package recommender.nonframework;

/**
 * Created by simen on 3/20/17.
 */
public class Prediction implements Comparable<Prediction> {
    private User user;
    private Item item;
    private double value;

    Prediction(User u, Item i, double v) {
        user = u;
        item = i;
        value = v;
    }

    Prediction(Item i, double v) {
        item = i;
        value = v;
    }

    public void increaseValue(double d) {
        value += d;
    }

    public double getValue() {
        return value;
    }

    public Item getItem() {
        return item;
    }

    public User getUser() {
        return user;
    }

    //reversed compareTo-method which ensures larger scores are stored before smaller scores when sorting
    public int compareTo(Prediction otherPrediction) {
        if (value > otherPrediction.getValue()) {
            return -1;
        }
        else if (value < otherPrediction.getValue()) {
            return 1;
        }
        else {
            return 0;
        }
    }
}
