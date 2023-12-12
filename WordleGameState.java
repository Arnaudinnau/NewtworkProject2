import java.io.IOError;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Vector;

/**
 * This class encapsulates all the information necessary to fully describe a
 * game state of Wordle,
 * including the cookie, the hidden word, the number of trials, and a list of
 * previous trials along
 * with their respective replies and the game status.
 * It is responsible for accurately responding to CHEAT and TRY queries.
 * 
 * @author Arnaud Innaurato, Sophia Donato
 * @since 2023-12-10
 */

public class WordleGameState {
    private String hiddenWord;
    private int tries;
    private Vector<String> wordsTried;
    private GameStatus status;
    /**
     * @value 5 WordLength (size of a wordle word)
     * @value listWords (database of all possible 5 letters words accepted)
     */
    private final static int WordLength = 5;
    private final static List<String> listWords = new ArrayList<>(WordleWordSet.WORD_SET);

    /**
     * Constructor of a specific game state
     */
    public WordleGameState() {
        this.tries = 0;
        this.hiddenWord = listWords.get(new Random().nextInt(listWords.size()));
        this.wordsTried = new Vector<>();
        this.status = GameStatus.RUNNING;
    }

    /**
     * Answer to a TRY, CHEAT query
     * 
     * @param query
     * @return word to send to the client
     */
    public String answerToQuery(String query) {
        String returned = "WRONG";
        if (query.equals("CHEAT")) {
            returned = hiddenWord.toUpperCase();
        } else if (query.startsWith("TRY")) {
            String guess = query.replace("TRY ", "");
            guess = guess.toUpperCase();
            if (guess.length() == WordLength && guess.matches("[A-Z]+")) {
                if (!listWords.contains(guess.toLowerCase())) {
                    returned = "NONEXISTENT";
                } else {
                    tries++;
                    returned = wordleComputePattern(guess.toLowerCase());
                    if (returned.equals("GGGGG") || tries == 6)
                        returned = returned.concat(" GAMEOVER");
                    synchronized (wordsTried) {
                        wordsTried.add(guess.toUpperCase() + returned);
                    }
                }
            }
        } else if (query.equals("QUIT")) {
            return "QUIT";
        }
        return returned;
    }

    /**
     * Method to compute the pattern of a guess given
     * 
     * @param guess String with the correct format and in the database
     * @return String which contains the pattern computed (Wordle)
     */
    private String wordleComputePattern(String guess) {
        char[] returned = { 'B', 'B', 'B', 'B', 'B' };
        char[] guessTab = guess.toCharArray();
        char[] answerTab = hiddenWord.toCharArray();
        int[] tab1 = { 1, 1, 1, 1, 1 };
        int[] tab2 = { 1, 1, 1, 1, 1 };
        for (int i = 0; i < WordLength; i++) {
            if (guessTab[i] == answerTab[i]) {
                tab1[i] = 0;
                tab2[i] = 0;
                returned[i] = 'G';
            }
        }
        for (int i = 0; i < WordLength; i++) {
            for (int j = 0; j < WordLength; j++) {
                if (guessTab[i] == answerTab[j] && i != j && tab1[j] == 1 && tab2[i] == 1) {
                    returned[i] = 'Y';
                    tab1[j] = 0;
                    tab2[i] = 0;
                    break;
                }
            }
        }
        return new String(returned);
    }

    /**
     * Return a complete html file related to this gameState in byte[] format
     * 
     * @param path
     * @return complete html file
     */
    public byte[] getData(String path, boolean acceptGzip) throws IOException {
        PageHandler pageHandler = new PageHandler(wordsTried);
        if (path.contains("html"))
            if (acceptGzip)
                return pageHandler.getCompressedHTML();
            else
                return pageHandler.getHTML();
        return null;
    }

    public GameStatus GetStatus() {
        return status;
    }

    /**
     * Change the status of the game
     * Running => LastQuery
     * LastQuery => ToClose
     */
    public void NextStatus() {
        if (status == GameStatus.RUNNING)
            this.status = GameStatus.LASTQUERY;
        else if (status == GameStatus.LASTQUERY) {
            this.status = GameStatus.TOCLOSE;
        }
    }
}
