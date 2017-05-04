package recommender.nonframework;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Stack;

/**
 * Created by simen on 3/24/17.
 * Class that store info about an item
 */
public class Item {
    private String id;

    public Item(String id) {
        this.id = id;
    }
    public String getId() {
        return id;
    }

}
