import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Vector;

public class WordleGameState {
    private String hiddenWord;
    private int tries;
    private Vector<String> wordsTried;

    private final static List<String> listWords = new ArrayList<>(WordleWordSet.WORD_SET);
    private final static int WordLength = 5;

    public WordleGameState() {
        this.tries = 0;
        this.hiddenWord = listWords.get(new Random().nextInt(listWords.size()));
        this.wordsTried = new Vector<>(6);
    }

    public String answerToQuery(String query) {
        System.out.println(query);
        String returned = "WRONG";
        if (query.equals("CHEAT")) {
            returned = hiddenWord.toUpperCase();
            System.out.println("cheat" + returned);
        } else if (query.startsWith("TRY")) {
            String guess = query.substring("TRY ".length());
            System.out.println(guess);
            if (guess.length() == WordLength) {
                if (!listWords.contains(guess.toLowerCase())) {
                    returned = "NONEXISTENT";
                } else {
                    tries++;
                    wordsTried.add(guess + returned);
                    returned = wordleComputePattern(guess.toLowerCase());
                }
                if (returned.equals("GGGGG") || tries == 6)
                    returned = returned.concat(" GAMEOVER");
            }
        } else if (query.equals("QUIT")) {
            return "QUIT";
        }
        return returned;
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

    public byte[] getData(String path) {
        PageHandler pageHandler = new PageHandler(wordsTried);
        if (path.contains("html"))
            return pageHandler.getHTML();
        return null;
    }
}
