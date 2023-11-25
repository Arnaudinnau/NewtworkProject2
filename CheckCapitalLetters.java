public class CheckCapitalLetters {
    public static boolean containsOnlyCapitalLetters(String input) {
        return input.matches("[A-Z]+");
    }

    public static void main(String[] args) {
        String testString = "HELlO";
        if (containsOnlyCapitalLetters(testString)) {
            System.out.println("The string contains only capitalized letters.");
        } else {
            System.out.println("The string contains non-capitalized letters.");
        }
    }
}
