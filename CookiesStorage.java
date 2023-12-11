import java.util.*;

/**
 * This class manages the association between each game state of a specific game
 * and its corresponding cookie.
 * 
 * @author Arnaud Innaurato, Sophia Donato
 * @since 2023-12-10
 */

public class CookiesStorage {
    private Vector<CookieState> cookiesList;

    public CookiesStorage() {
        this.cookiesList = new Vector<>();
    }

    /**
     * For a given game state, creates a cookie and stores it
     * 
     * @param gameState
     * @return cookie for the game
     */
    public String createCookie(WordleGameState gameState) {
        Long randomLong = new Random().nextLong();
        String cookie = "_SessionWordle=" + randomLong.toString();
        synchronized (cookiesList) {
            cookiesList.add(new CookieState(cookie, gameState));
        }
        return cookie;
    }

    /**
     * Returns a gameState for a given cookie
     * 
     * @param cookie
     * @return gameState
     * @throws CookiesNotInListException Exception triggered if the cookie is
     *                                   unknown
     */
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

    /**
     * Remove a specific cookie from the list
     * 
     * @param cookie
     */
    public void removeSpecificCookie(String cookie) {
        synchronized (cookiesList) {
            for (int i = 0; i < cookiesList.size(); i++) {
                if (cookiesList.get(i).getCookie().equals(cookie)) {
                    cookiesList.remove(i);
                }
            }
        }
    }

    /**
     * Remove all expired cookies (created more than 10 minutes ago) from the list
     */
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
