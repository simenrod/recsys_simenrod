package recommender.nonframework;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Stack;

/**
 * Created by simen on 3/24/17.
 */
class User {
    private String id;
    private HashMap<String, Item> history = new HashMap<String, Item>();

    public HashMap<String, Item> getHistory() {
        return history;
    }
    public String getId() {
        return id;
    }
    public void addItemToHistory(String nr, Item item) {
        history.put(nr,item);
    }

}
