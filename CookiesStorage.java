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
        System.out.println(cookie);
        synchronized (cookiesList) {
            for (CookieState elem : cookiesList) {
                if (elem.getCookie().equals(cookie))
                    cookiesList.remove(elem);
                System.out.println("Prorbeê");
            }
        }
    }

    public void removeExpiredCookie() {
        synchronized (cookiesList) {
            for (CookieState elem : cookiesList) {
                if (elem.hasExpired())
                    cookiesList.remove(elem);
            }
        }
    }

}
