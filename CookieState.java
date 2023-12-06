import java.time.*;

public class CookieState {
    private String cookie;
    private WordleGameState gameState;
    private Instant creationInstant;

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

    public boolean hasExpired() {
        return Duration.between(Instant.now(), creationInstant).toMinutes() > 10;
    }
}
