import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

class EvilSolutionTest {

    private EvilSolution createDefaultSolution() {
        ArrayList<String> targetList = new ArrayList<>();
        targetList.add("echo");
        targetList.add("heal");
        targetList.add("belt");
        targetList.add("peel");
        targetList.add("hazy");
        return new EvilSolution(targetList);
    }

    private EvilSolution createSingleWordSolution() {
        ArrayList<String> targetList = new ArrayList<>();
        targetList.add("echo");
        return new EvilSolution(targetList);
    }

    @Test
    public void testEvilSolutionConstruct(){
        ArrayList<String> targetList = new ArrayList<>();
        targetList.add("echo");
        targetList.add("heal");
        targetList.add("belt");
        targetList.add("peel");
        targetList.add("hazy");

        ArrayList<Character> partialSolution = new ArrayList<>();
        partialSolution.add('_');
        partialSolution.add('_');
        partialSolution.add('_');
        partialSolution.add('_');

        int expectedMissingChars = 4;

        EvilSolution solution = new EvilSolution(targetList);
        ArrayList<String> actualTargetList = solution.getTargetList();
        ArrayList<Character> actualPartialSolution = solution.getPartialSolution();
        int actualMissingChars = solution.getMissingChars();

        assertEquals(expectedMissingChars, actualMissingChars);
        assertArrayEquals(targetList.toArray(), actualTargetList.toArray());
        assertArrayEquals(partialSolution.toArray(), actualPartialSolution.toArray());

    }

    @Test
    public void testIsSolvedBecomesTrueAfterAllLettersGuessed() {
        EvilSolution solution = createSingleWordSolution();

        // echo
        assertFalse(solution.isSolved());
        assertTrue(solution.addGuess('e'));

        assertFalse(solution.isSolved());
        assertTrue(solution.addGuess('c'));

        assertFalse(solution.isSolved());

        assertTrue(solution.addGuess('h'));

        assertFalse(solution.isSolved());

        assertTrue(solution.addGuess('o'));

        assertTrue(solution.isSolved(), "Game should be solved after all letters are guessed");


        assertEquals(0, solution.getMissingChars(),
                "missingChars should be 0 when solved");

        assertEquals("echo", solution.getTarget(),
                "getTarget should return final word once solved");
    }

    @Test
    public void testAddGuessMissDoesNotChangePartialSolutionOrMissingChars() {
        EvilSolution solution = createDefaultSolution();

        ArrayList<Character> beforePartial = new ArrayList<>(solution.getPartialSolution());
        int beforeMissing = solution.getMissingChars();
        ArrayList<String> beforeTargetList = new ArrayList<>(solution.getTargetList());

        boolean result = solution.addGuess('x');

        assertFalse(result, "Guessing a letter not appearing in any word should be treated as a miss");

        assertIterableEquals(beforePartial, solution.getPartialSolution(),
                "partialSolution should not change after a miss");
        assertEquals(beforeMissing, solution.getMissingChars(),
                "missingChars should remain the same after a miss");

        assertEquals(beforeTargetList.size(), solution.getTargetList().size(),
                "targetList size should not increase after a miss");
    }

    @Test
    public void testAddGuessHitUpdatesPartialSolutionAndMissingCharsAndTargetList() {
        EvilSolution solution = createDefaultSolution();

        // first guess 'e'：
        // echo -> "e___"
        // heal/belt -> "_e__"
        // peel -> "_ee_"
        // hazy -> "____"
        // max word family is "_e__" (2 words)
        boolean result = solution.addGuess('e');

        assertTrue(result, "Guessing 'e' should reveal at least one position");

        ArrayList<Character> partial = solution.getPartialSolution();
        // pattern = _ e _ _
        ArrayList<Character> expected = new ArrayList<>();
        expected.add('_');
        expected.add('e');
        expected.add('_');
        expected.add('_');

        assertIterableEquals(expected, partial,
                "partialSolution should be updated according to the chosen word family");

        assertEquals(3, solution.getMissingChars(),
                "missingChars should decrease from 4 to 3 after revealing one position");

        // targetList 中应该只剩下与 "_e__" 匹配的 "heal" 和 "belt"
        ArrayList<String> newTargets = solution.getTargetList();
        assertEquals(2, newTargets.size(),
                "After guessing 'e', targetList should shrink to the largest family");
        assertTrue(newTargets.contains("heal"));
        assertTrue(newTargets.contains("belt"));
    }

    @Test
    public void testPrintProgressInitialOutput() {
        EvilSolution solution = createDefaultSolution();

        PrintStream originalOut = System.out;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        System.setOut(new PrintStream(baos));
        try {
            solution.printProgress();
        } finally {
            System.setOut(originalOut);
        }

        String output = baos.toString().trim();
        assertEquals("_ _ _ _", output,
                "Initial printProgress should show four underscores separated by spaces");
    }

    @Test
    public void testPrintProgressAfterGuess() {
        EvilSolution solution = createDefaultSolution();

        solution.addGuess('e');

        PrintStream originalOut = System.out;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        System.setOut(new PrintStream(baos));
        try {
            solution.printProgress();
        } finally {
            System.setOut(originalOut);
        }

        String output = baos.toString().trim();
        assertEquals("_ e _ _", output,
                "printProgress should reflect updated partial solution after a guess");
    }

}