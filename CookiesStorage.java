import java.util.*;

public class CookiesStorage {
    private Vector<CookieState> cookiesList;

    public CookiesStorage() {
        this.cookiesList = new Vector<>();
    }

    public String createCookie(WordleGameState state) {
        Long randomLong = new Random().nextLong();
        String cookie = "_SessionWordle=" + randomLong.toString();
        synchronized (cookiesList) {
            cookiesList.add(new CookieState(cookie, state));
        }
        return cookie;
    }

    public WordleGameState getState(String cookie) throws CookiesNotInListException {
        WordleGameState state = null;
        synchronized (cookiesList) {
            for (CookieState elem : cookiesList) {
                if (elem.getCookie().equals(cookie))
                    state = elem.getState();
            }
        }
        if (state != null) {
            return state;
        } else
            throw new CookiesNotInListException();
    }

    public void removeSpecificCookie(String cookie) {
        synchronized (cookiesList) {
            for (int i = 0; i < cookiesList.size(); i++) {
                if (cookiesList.get(i).getCookie().equals(cookie)) {
                    cookiesList.remove(i);
                }
            }
        }
    }

    public void removeExpiredCookie() {
        synchronized (cookiesList) {
            for (int i = 0; i < cookiesList.size(); i++) {
                if (cookiesList.get(i).hasExpired()) {
                    cookiesList.remove(i);
                }
            }
        }
    }

}
