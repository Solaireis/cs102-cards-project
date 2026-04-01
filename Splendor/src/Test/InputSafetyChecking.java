package Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

// This class is use to check all the user input
// including correct integer input, correct string input for selecting tokens etc.

public class InputSafetyChecking {

    private static final String WHITE = "WHITE";
    private static final String BLUE = "BLUE";
    private static final String GREEN = "GREEN";
    private static final String RED = "RED";
    private static final String BLACK = "BLACK";

    public static int safeInt(Scanner sc, String invalidPrompt) {
        while (!sc.hasNextInt()) {
            sc.next();
            if (invalidPrompt != null && !invalidPrompt.isBlank()) {
                System.out.print(invalidPrompt);
            }
        }
        return sc.nextInt();
    }

    public static int readIntInRange(Scanner sc, int minInclusive, int maxInclusive, String prompt) {
        while (true) {
            if (prompt != null && !prompt.isBlank()) {
                System.out.print(prompt);
            }

            int color = safeInt(sc, "Please enter a valid number: ");
            sc.nextLine();
            if (color >= minInclusive && color <= maxInclusive) {
                return color;
            }
            System.out.println("Invalid choice, try again.");
        }
    }

    public static List<String> parseThreeColorsFlexibly(String rawInput, String[] takeColors) {

        if (rawInput == null) {
            return null;
        }

        String normalized = normalizeUpper(rawInput);
        if (normalized.isEmpty()) {
            return null;
        }

        String[] chunks = normalized.split("[^A-Z]+");
        ArrayList<String> colors = new ArrayList<>();

        for (String chunk : chunks) {
            if (chunk.isEmpty()) {
                continue;
            }

            int i = 0;
            while (i < chunk.length()) {
                String matched = matchColor(chunk, i);
                if (matched == null) {
                    return null;
                }
                colors.add(matched);
                i += matched.length();
            }
        }

        if (colors.size() != 3) {
            return null;
        }

        if (colors.get(0).equals(colors.get(1)) || colors.get(0).equals(colors.get(2))
                || colors.get(1).equals(colors.get(2))) {
            return null;
        }

        return colors;
    }

    public static String normalizeUpper(String input) {
        if (input == null) {
            return "";
        }
        return input.trim().toUpperCase();
    }

    public static String matchColor(String chunk, int start) {
        if (chunk.startsWith(WHITE, start)) {
            return WHITE;
        }
        if (chunk.startsWith(BLUE, start)) {
            return BLUE;
        }
        if (chunk.startsWith(GREEN, start)) {
            return GREEN;
        }
        if (chunk.startsWith(RED, start)) {
            return RED;
        }
        if (chunk.startsWith(BLACK, start)) {
            return BLACK;
        }
        return null;
    }
}
