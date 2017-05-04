package recommender.nonframework;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Stack;

/**
 * Created by simen on 3/24/17.
 * Class that stores info about a user an which items that are rated by the user
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
