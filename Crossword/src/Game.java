/*
 Class where all the game logic will take place
 */


import org.json.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class Game{
    private final int MAX_WORDS = 10;
    private final int GRID = 10;

    private final Word[] words = new Word[MAX_WORDS]; //  Word, Description
    private final File wordFile = new File("words.json");

    private final char[][] board = new char[GRID][GRID];
    private final int[][] owners = new int[GRID][GRID];

    private final int intersections = 0;

    public Game() {
        // generate words for game
        generateWords();

        // Place words on board
        placeWords(0);

        // View board
        for (int r=0; r<board.length; r++) {
            for (int c=0; c<board[r].length; c++) {
                if (board[r][c] == '\0') {
                    System.out.print("# ");
                    continue;
                }

                System.out.print(board[r][c] + " ");
            }
            System.out.println();
        }
    }

    // return board
    public char[][] getBoard () {
        return board;
    }

    // return all words
    public Word[] getWords () {
        return words;
    }

    // Place words on the grid using a backtracking algorithm
    public boolean placeWords (int index) {
        // Base case -> when the next placement is equal to the max amount of words then break the recursion
        if (index == MAX_WORDS) { return true; }

        // current word
        Word word = words[index];

        // Check every grid
        for (int r=0; r<board.length; r++) {
            for (int c=0; c<board[r].length; c++) {
                // Check both directions
                for (int d=0; d<=1; d++) {
                    if (canPlace(word, r, c, d)) {
                        place(word, r, c, d, index);

                        if (placeWords(index + 1)) return true;

                        remove(word, r, c ,d);
                    }
                }

            }
        }

        return false;
    }

    // Constraint to place word
    public boolean canPlace (Word word, int row, int col, int direction) {
        String text = word.getWord();
        int size = text.length();

        return
            // Check boundries of cell and the letters inside the cell
            checkCells(text, size, row, col, direction);
    }

    // Check boundries of cell and the letters inside the cell
    public boolean checkCells (String text, int size, int row, int col, int direction) {
        if (direction == 0) {
            // horizontal
            if (col+size > GRID) return false; // return false if the col went over the grid barrier

            for (int c=col; c<(col+size); c++) {
                if (board[row][c] == '\0') {
                    // if new grid then check the top and bottom row for invalid placement
                    boolean outTop = row - 1 < 0;
                    boolean outBottom = row + 1 >= GRID;

                    if (!outTop && board[row-1][c] != '\0') return false;
                    if (!outBottom && board[row+1][c] != '\0') return false;

                    continue; // if empty then continue
                }

                // not empty so check character
                if (board[row][c] != text.charAt(c-col)) {
                    return false;
                }
            }
        } else {
            // vertical

            if (row+size > GRID) return false; // return false if the row went over the grid barrier

            for (int r=row; r<(row+size); r++) {
                if (board[r][col] == '\0') {
                    // if new grid then check left and right row for invalid placement
                    boolean outLeft = col - 1 < 0;
                    boolean outRight = col + 1 >= GRID;

                    if (!outLeft && board[r][col-1] != '\0') return false;
                    if (!outRight && board[r][col+1] != '\0') return false;

                    continue; // if empty then continue
                }

                // not empty so check character and if it matches to the character I want to put down
                if (board[r][col] != text.charAt(r-row)) {
                    return false;
                }
            }
        }

        //if (intersections == 0) return false;

        return true;
    }

    // Place board
    public void place (Word word, int row, int col, int direction, int index) {
        word.setDirection(direction);
        word.setPosition(new int[]{row, col});
        word.setIndex(index);

        String text = word.getWord();
        int size = text.length();

        if (direction == 0) {
            // horizontal
            for (int c=col; c<(col+size); c++) {
                board[row][c] = text.charAt(c-col);
                owners[row][c]++;
            }
        } else {
            // vertical
            for (int r=row; r<(row+size); r++) {
                board[r][col] = text.charAt(r-row);
                owners[r][col]++;
            }
        }
    }

    // Remove board
    public void remove (Word word, int row, int col, int direction) {
        String text = word.getWord();
        int size = text.length();

        if (direction == 0) {
            // horizontal
            for (int c=col; c<(col+size); c++) {
                owners[row][c]--;

                if (owners[row][c] == 0) board[row][c] = '\0';
            }
        } else {
            // vertical
            for (int r=row; r<(row+size); r++) {
                owners[r][col]--;

                if (owners[r][col] == 0) board[r][col] = '\0';
            }
        }
    }

    // Generate words for the game from the json file
    private void generateWords() {
        try {
            String json = Files.readString(Paths.get("words.json"));

            JSONObject obj = new JSONObject(json);
            JSONArray array = obj.getJSONArray("words");

            ArrayList<JSONObject> list = new ArrayList<>();

            // Save words in list
            for (int i=0; i<array.length(); i++) {
                list.add(array.getJSONObject(i));
            }

            // Shuffle words
            Collections.shuffle(list);

            // Get first 5 words
            for (int i = 0; i < MAX_WORDS; i++) {
                JSONObject w = list.get(i);

                words[i] = new Word(
                        w.getString("word"),
                        w.getString("description")
                );
            }
        } catch (IOException | JSONException e) {
            System.out.println(e.getMessage());
        }
    }
}