package main.distle;

import java.util.*;

public class EditDistanceUtils {

    /**
     * Returns the completed Edit Distance memoization structure, a 2D array
     * of ints representing the number of string manipulations required to minimally
     * turn each subproblem's string into the other.
     * 
     * @param s0 String to transform into other
     * @param s1 Target of transformation
     * @return Completed Memoization structure for editDistance(s0, s1)
     */
    public static int[][] getEditDistTable(String s0, String s1) {
        int[][] T = new int[s0.length() + 1][s1.length() + 1];

        for (int r = 0; r <= s0.length(); r++) {
            T[r][0] = r;
        }
        for (int c = 0; c <= s1.length(); c++) {
            T[0][c] = c;
        }
        for (int r = 1; r <= s0.length(); r++) {
            for (int c = 1; c <= s1.length(); c++) {
                int e = Integer.MAX_VALUE;
                int f = Integer.MAX_VALUE;
                int g = Integer.MAX_VALUE;
                int h = Integer.MAX_VALUE;
                if (r >= 1) { // deletion case
                    e = T[r - 1][c] + 1;
                }
                if (c >= 1) { // insertion case
                    f = T[r][c - 1] + 1;
                }
                if (r >= 1 && c >= 1) { // replacement case
                    g = T[r - 1][c - 1] + (s1.charAt(c - 1) != s0.charAt(r - 1) ? 1 : 0);
                }
                if (r >= 2 && c >= 2 && (s0.charAt(r - 1) == s1.charAt(c - 2))
                        && (s0.charAt(r - 2) == s1.charAt(c - 1))) { // transposition case
                    h = T[r - 2][c - 2] + 1;
                }
                T[r][c] = Math.min(Math.min(e, f), Math.min(g, h));
            }
        }
        return T;
    }

    /**
     * Returns one possible sequence of transformations that turns String s0
     * into s1. The list is in top-down order (i.e., starting from the largest
     * subproblem in the memoization structure) and consists of Strings representing
     * the String manipulations of:
     * <ol>
     * <li>"R" = Replacement</li>
     * <li>"T" = Transposition</li>
     * <li>"I" = Insertion</li>
     * <li>"D" = Deletion</li>
     * </ol>
     * In case of multiple minimal edit distance sequences, returns a list with
     * ties in manipulations broken by the order listed above (i.e., replacements
     * preferred over transpositions, which in turn are preferred over insertions,
     * etc.)
     * 
     * @param s0    String transforming into other
     * @param s1    Target of transformation
     * @param table Precomputed memoization structure for edit distance between s0,
     *              s1
     * @return List that represents a top-down sequence of manipulations required to
     *         turn s0 into s1, e.g., ["R", "R", "T", "I"] would be two replacements
     *         followed
     *         by a transposition, then insertion.
     */
    public static List<String> getTransformationList(String s0, String s1, int[][] table) {
        List<String> minimalEdits = new ArrayList<String>();
        int currPos = table[s0.length()][s1.length()];
        int r = s0.length();
        int c = s1.length();
        while (r != 0 || c != 0) {
            int transposition = 0;
            int replacement = 0;
            int insertion = 0;
            int deletion = 0;
            if (r >= 1) {
                deletion = table[r - 1][c] + 1;
            }
            if (c >= 1) {
                insertion = table[r][c - 1] + 1;
            }
            if (c >= 1 && r >= 1) {
                if (s1.charAt(c - 1) != s0.charAt(r - 1)) {
                    replacement = table[r - 1][c - 1] + 1;
                    if (replacement == currPos) {
                        minimalEdits.add("R");
                        currPos = table[r - 1][c - 1];
                        r--;
                        c--;
                    }
                } else if (s1.charAt(c - 1) == s0.charAt(r - 1)) {
                    replacement = table[r - 1][c - 1];
                    if (replacement == currPos) {
                        currPos = table[r - 1][c - 1];
                        r--;
                        c--;
                    }
                }
            }
            if (c >= 2 && r >= 2 && (s0.charAt(r - 1) == s1.charAt(c - 2)) && (s0.charAt(r - 2) == s1.charAt(c - 1))) {
                transposition = table[r - 2][c - 2] + 1;
                if (transposition == currPos) {
                    minimalEdits.add("T");
                    currPos = table[r - 2][c - 2];
                    r -= 2;
                    c -= 2;
                }
            }
            if (insertion == currPos) {
                minimalEdits.add("I");
                currPos = table[r][c - 1];
                c--;
            } else if (deletion == currPos) {
                minimalEdits.add("D");
                currPos = table[r - 1][c];
                r--;
            }
        }
        return minimalEdits;
    }

    /**
     * Returns the edit distance between the two given strings: an int
     * representing the number of String manipulations (Insertions, Deletions,
     * Replacements, and Transpositions) minimally required to turn one into
     * the other.
     * 
     * @param s0 String to transform into other
     * @param s1 Target of transformation
     * @return The minimal number of manipulations required to turn s0 into s1
     */
    public static int editDistance(String s0, String s1) {
        if (s0.equals(s1)) {
            return 0;
        }
        return getEditDistTable(s0, s1)[s0.length()][s1.length()];
    }

    /**
     * See {@link #getTransformationList(String s0, String s1, int[][] table)}.
     */
    public static List<String> getTransformationList(String s0, String s1) {
        return getTransformationList(s0, s1, getEditDistTable(s0, s1));
    }

}
