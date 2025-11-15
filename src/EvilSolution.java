import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EvilSolution {

    private ArrayList<String> targetList;
    private ArrayList<Character> partialSolution;
    private int missingChars;

    public EvilSolution(ArrayList<String> targetList) {
        this.targetList = targetList;
        missingChars = targetList.get(0).length();
        partialSolution = new ArrayList<>(missingChars);
        for (int i = 0; i < missingChars; i++) {
            partialSolution.add('_');
        }
    }

    public boolean isSolved() {
        return missingChars == 0;
    }

    public void printProgress() {
        for (char c : partialSolution) {
            System.out.print(c + " ");
        }
        System.out.println();
    }

    public boolean addGuess(char guess) {
        HashMap<String, ArrayList<String>> wordFamily = getWordFamily(guess);

        Map.Entry<String, ArrayList<String>> maxWordMap = getMaxWordList(wordFamily);

        String pattern = maxWordMap.getKey();
        this.targetList = maxWordMap.getValue();

        int countMissingChar = 0;
        for (int i = 0; i < pattern.length(); i++){
            if (pattern.charAt(i) == '_'){
                countMissingChar++;
            } else if (this.partialSolution.get(i) == '_'){
                this.partialSolution.set(i, guess);
            }
        }

        assert countMissingChar > this.missingChars: "countMissingChar shouldn't be larger than missingChars";
        if (countMissingChar == this.missingChars){
            // guess not hit
            return false;
        } else {
            // guess hits
            this.missingChars = countMissingChar;
            return true;

        }
    }

    private Map.Entry<String, ArrayList<String>> getMaxWordList(HashMap<String, ArrayList<String>> wordFamily){
        ArrayList<String> maxWordList = new ArrayList<>();
        String maxPattern = "";
        int max = 0;
        for (String pattern : wordFamily.keySet()){
            ArrayList<String> curWordList = wordFamily.get(pattern);
            if (curWordList.size() > max){
                maxWordList = curWordList;
                maxPattern = pattern;
                max = curWordList.size();
            }
        }

        return new AbstractMap.SimpleEntry<>(maxPattern, maxWordList);
    }

    private HashMap<String, ArrayList<String>> getWordFamily(char guess){
        HashMap<String, ArrayList<String>> wordFamily = new HashMap<>();

        for (int i = 0; i < targetList.size(); i++){
            String word = targetList.get(i);
            String pattern = getWordPattern(word, guess);

            if (!wordFamily.containsKey(pattern)){
                ArrayList<String> init = new ArrayList<>();
                init.add(word);
                wordFamily.put(pattern, init);
            } else {
                wordFamily.get(pattern).add(word);
            }
        }

        return wordFamily;

    }

    private String getWordPattern(String word, char guess){

        String pattern = "";
        for (int i = 0; i < word.length(); i++){
            if (word.charAt(i) == guess){
                pattern = pattern + guess;
            } else {
                // update based on partialSolution
                pattern = pattern + this.partialSolution.get(i);
            }
        }
        return pattern;
    }

    public String getTarget() {
        assert (missingChars == 0): "missingChars not equal to 0, so there's no exact target";
        // when game ends, targetList should only contain one word
        return targetList.get(0);
    }
}
