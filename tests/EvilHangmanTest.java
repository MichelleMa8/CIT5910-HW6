import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

class EvilHangmanTest {

    private InputStream originalIn;
    private PrintStream originalOut;

    @BeforeEach
    void setUpStreams() {
        originalIn = System.in;
        originalOut = System.out;
    }

    @AfterEach
    void restoreStreams() {
        System.setIn(originalIn);
        System.setOut(originalOut);
    }

    private String runGameWithInputFile(String inputFileName) throws Exception {
        FileInputStream inputFileStream = new FileInputStream(inputFileName);
        System.setIn(inputFileStream);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream testOut = new PrintStream(baos);
        System.setOut(testOut);

        EvilHangman game = new EvilHangman("testDictionary.txt");
        game.start();

        testOut.flush();
        return baos.toString();
    }


    @Test
    void testStartCompletesGameAndPrintsCongrats() throws Exception {
        String output = runGameWithInputFile("input_normal_echo.txt");

        assertTrue(output.contains("Guess a letter."),
                "Game should prompt user to guess a letter.");
        assertTrue(output.contains("Incorrect guesses:"),
                "Game should print incorrect guesses each round.");

        assertTrue(output.contains("Congrats! The word was echo"),
                "Game should print the correct final word when solved.");
    }

    @Test
    void testStartHandlesInvalidInputThenValidInput() throws Exception {
        String output = runGameWithInputFile("input_invalid_then_valid.txt");

        assertTrue(output.contains("Please enter a single character."),
                "Should warn when input is not a single character.");
        assertTrue(output.contains("Please enter an alphabetic character."),
                "Should warn when input is not alphabetic.");

        assertTrue(output.contains("Congrats! The word was echo"),
                "Even after invalid input, game should eventually finish with correct word.");
    }

    @Test
    void testStartHandlesRepeatedGuess() throws Exception {
        String output = runGameWithInputFile("input_repeated_guess.txt");

        assertTrue(output.contains("You've already guessed that."),
                "Game should warn when user guesses a previously guessed letter.");

        assertTrue(output.contains("Congrats! The word was echo"),
                "Game should still complete successfully after repeated guesses.");
    }

    @Test
    void testStartIncorrectGuessesAreSorted() throws Exception {
        String output = runGameWithInputFile("input_sorted_incorrect.txt");

        assertTrue(output.contains("Incorrect guesses:"),
                "Output should contain 'Incorrect guesses:' label.");

        assertTrue(output.contains("[a, z]") || output.contains("[z, a]"),
                "Incorrect guesses should be printed as a set, e.g., [a, z].");

        assertTrue(output.contains("Congrats! The word was echo"),
                "Game should still end successfully.");
    }
}
