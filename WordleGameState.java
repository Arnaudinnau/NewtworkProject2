import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Vector;

public class WordleGameState {
    private String hiddenWord;
    private int tries;
    private Vector<String> wordsTried;
    private String cookie;

    private final static List<String> listWords = new ArrayList<>(WordleWordSet.WORD_SET);
    private final static int WordLength = 5;

    public WordleGameState(String cookie) {
        this.cookie = cookie;
        this.tries = 0;
        this.hiddenWord = listWords.get(new Random().nextInt(listWords.size()));
        this.wordsTried = new Vector<>(6);
    }

    public String answerToQuery(String query) {
        String returned = "WRONG";
        if (query.equals("CHEAT")) {
            returned = hiddenWord.toUpperCase();
        } else if (query.startsWith("TRY")) {
            String guess = query.substring("TRY ".length());
            System.out.println(guess);
            if (guess.length() == WordLength) {
                // if (guess.matches("[A-Z]+") && guess.length() == WordLength) {
                if (!listWords.contains(guess.toLowerCase())) {
                    returned = "NONEXISTENT";
                } else {
                    tries++;
                    returned = wordleComputePattern(guess.toLowerCase());
                }
                if (returned.equals("GGGGG") || tries == 6)
                    returned = returned.concat(" GAMEOVER");
            }
        } else if (query.equals("QUIT")) {
            this.clientSocket.close();
            return null;
        }
        return returned;
    }

    }

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
}