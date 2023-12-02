import java.util.*;

public class CookiesStorage {
    private Vector<String> cookiesList;
    private Vector<WordleGameState> stateList;

    public CookiesStorage() {
        this.cookiesList = new Vector<>();
        this.stateList = new Vector<>();
    }

    public String createCookie(WordleGameState state) {
        Long randomLong = new Random().nextLong();
        String cookie = "_SessionWordle=" + randomLong.toString();
        cookiesList.add(cookie);
        stateList.add(state);

        return cookie;
    }

    public WordleGameState getState(String cookie) {
        return stateList.get(cookiesList.indexOf(cookie));
    }
}
