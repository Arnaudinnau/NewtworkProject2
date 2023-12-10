import java.time.*;

/**
 * This class represents the token utilized within the CookiesStorage class.
 * It stores the gameState, its associated cookie, and the timestamp
 * indicating the creation time of the cookie.
 * 
 * @author Arnaud Innaurato, Sophia Donato
 * @since 2023-12-10
 */
public class CookieState {
    private String cookie;
    private WordleGameState gameState;
    private Instant creationInstant;

    /**
     * Constructor for a given cookie and a given gameState
     * 
     * @param cookie
     * @param gameState
     */
    public CookieState(String cookie, WordleGameState gameState) {
        this.cookie = cookie;
        this.gameState = gameState;
        this.creationInstant = Instant.now();
    }

    public String getCookie() {
        return cookie;
    }

    public WordleGameState getState() {
        return gameState;
    }

    /**
     * Indicated if this cookie has expired
     * 
     * @return true if the cookie exists for more than 10 minutes, false if not
     */
    public boolean hasExpired() {
        return Duration.between(Instant.now(), creationInstant).toMinutes() > 10;
    }
}
