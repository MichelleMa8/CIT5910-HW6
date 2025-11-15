import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

public class EvilHangman {

    // private ArrayList<String> wordList;
    private HashMap<Integer, ArrayList<String>> wordLenMap; // all possible lengths of words in the dictionary
    private HashSet<Character> previousGuesses;
    private TreeSet<Character> incorrectGuesses; // behaves like a hash set, but orders the entries!
    private EvilSolution solution;
    private Scanner inputReader;

    public EvilHangman() {
        this("engDictionary.txt");
    }

    public EvilHangman(String filename) {
        try {
            wordLenMap = dictionaryToMap(filename);
        } catch (IOException e) {
            System.out.printf(
                    "Couldn't read from the file %s. Verify that you have it in the right place and try running again.",
                    filename);
            e.printStackTrace();
            System.exit(0); // stop the program--no point in trying if you don't have a dictionary
        }

        previousGuesses = new HashSet<>();
        incorrectGuesses = new TreeSet<>();
        int randomWordLen = getRandomMapKey(wordLenMap);
        // used in testing
        // int randomWordLen = 4;

        ArrayList<String> targetList = wordLenMap.get(randomWordLen);

        solution = new EvilSolution(targetList);
        inputReader = new Scanner(System.in);

    }

    public void start() {
        while (!solution.isSolved()) {
            char guess = promptForGuess();
            recordGuess(guess);
        }
        printVictory();
        inputReader.close();
    }

    private char promptForGuess() {
        while (true) {
            System.out.println("Guess a letter.\n");
            solution.printProgress();
            System.out.println("Incorrect guesses:\n" + incorrectGuesses.toString());
            String input = inputReader.next();
            if (input.length() != 1) {
                System.out.println("Please enter a single character.");
            } else if (previousGuesses.contains(input.charAt(0))) {
                System.out.println("You've already guessed that.");
            } else if (!Character.isAlphabetic(input.charAt(0))) {
                System.out.println("Please enter an alphabetic character.");
            } else {
                return input.charAt(0);
            }
        }
    }

    private void recordGuess(char guess) {
        previousGuesses.add(guess);
        boolean isCorrect = solution.addGuess(guess);
        if (!isCorrect) {
            incorrectGuesses.add(guess);
        }
    }

    private void printVictory() {
        System.out.printf("Congrats! The word was %s%n", solution.getTarget());
    }

    private static HashMap<Integer, ArrayList<String>> dictionaryToMap(String filename) throws IOException {
        FileInputStream fileInput = new FileInputStream(filename);
        Scanner fileReader = new Scanner(fileInput);

        HashMap<Integer, ArrayList<String>> wordLenMap = new HashMap<>();

        while (fileReader.hasNext()) {
            String word = fileReader.next();
            int wordLen = word.length();
            // add the word to wordLenMap
            if (wordLenMap.containsKey(wordLen)){
                wordLenMap.get(wordLen).add(word);
            } else {
                ArrayList<String> initWords = new ArrayList<>();
                initWords.add(word);
                wordLenMap.put(wordLen, initWords);
            }
        }
        fileInput.close();
        fileReader.close();

        return wordLenMap;
    }

    private static int getRandomMapKey(HashMap<Integer, ArrayList<String>> map) {
        List<Integer> keys = new ArrayList<>(map.keySet());

        Random random = new Random();
        int randomIndex = random.nextInt(keys.size());

        return keys.get(randomIndex);
    }
}
