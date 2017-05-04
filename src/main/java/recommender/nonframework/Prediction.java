package recommender.nonframework;

/**
 * Created by simen on 3/20/17.
 * Class that stores values for predictions of a item. Higher values means better recommendations.
 */
public class Prediction implements Comparable<Prediction> {
    private Item item;
    private double value;

    Prediction(Item i, double v) {
        item = i;
        value = v;
    }

    //increases the frequency of this item
    public void increaseValue(double d) {
        value += d;
    }

    public double getValue() {
        return value;
    }

    public Item getItem() {
        return item;
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
