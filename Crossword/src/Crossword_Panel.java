/*
 Class for JPanel where the crossword game gui will be stored
 */

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.Arrays;

public class Crossword_Panel extends JPanel {
    private final char[][] board;
    private final char[][] answers;
    private final Window window;

    private final Word[] words;

    private final JComponent[][] cells;

    private int currentRow;
    private int currentCol;

    public Crossword_Panel (Window window, char[][] board, Word[] words) {
        // initialization
        this.window = window;
        this.board = board;
        this.words = words;

        this.cells = new JComponent[10][10];
        this.answers = new char[10][10];

        // Panel Settings
        this.setPreferredSize(new Dimension(400, 450));

        this.setBackground(new Color(117, 117, 117));
        this.setBorder(BorderFactory.createLineBorder(new Color(66, 66, 66), 5));

        this.setLayout(new GridLayout(10, 10));

        // Create board with valid and invalid cells
        createBoard();
    }

    private void createBoard () {
        for (int r=0; r<board.length; r++) {
            for (int c=0; c<board[r].length; c++) {
                if (board[r][c] == '\0') {
                    // Invalid Cell
                    this.add(new InvalidCell());
                } else {
                    // Valid Cell

                    Word word = findWordStart(new int[]{r, c});

                    if (word != null) {
                        if (cells[r][c] == null) {
                            StartCell cell = new StartCell(String.valueOf(word.getIndex()));

                            cell.row = r;
                            cell.col = c;
                            cells[r][c] = cell;

                            this.add(cell);
                        } else {
                            StartCell cell = (StartCell) cells[r][c];

                            cell.addStart(String.valueOf(word.getIndex()));
                        }
                    } else {
                        CrosswordCell cell = new CrosswordCell();

                        cell.row = r;
                        cell.col = c;
                        cells[r][c] = cell;

                        this.add(cell);
                    }
                }
            }
        }
    }

    private void moveToNextCell(int r, int c) {

        Word w = findWordAt(r, c);
        if (w == null) return;

        int nextR = r;
        int nextC = c;

        if (w.getDirection() == 0) {
            nextC++;
        } else {
            nextR++;
        }

        // 🔥 validate next cell
        if (!isValid(nextR, nextC)) return;

        cells[nextR][nextC].requestFocus();
    }

    private boolean isValid(int r, int c) {
        if (r < 0 || r >= 10 || c < 0 || c >= 10) return false;
        if (cells[r][c] == null) return false;
        if (cells[r][c] instanceof InvalidCell) return false;
        return true;
    }

    private Word findWordAt(int r, int c) {
        for (Word w : words) {

            int wr = w.getPosition()[0];
            int wc = w.getPosition()[1];

            if (w.getDirection() == 0) { // horizontal
                if (r == wr && c >= wc && c < wc + w.getWord().length()) {
                    return w;
                }
            } else { // vertical
                if (c == wc && r >= wr && r < wr + w.getWord().length()) {
                    return w;
                }
            }
        }
        return null;
    }

    private Word findWordStart (int[] position) {
        for (Word word : words) {
            int[] wordPosition = word.getPosition();

            if (position[0] == wordPosition[0] && position[1] == wordPosition[1]) {
                return word;
            }
        }

        return null;
    }

    private void checkWin() {
        for (int r = 0; r < board.length; r++) {
            for (int c = 0; c < board[r].length; c++) {

                if (board[r][c] == '\0') continue; // ignore black cells

                if (answers[r][c] == '\0') return; // unfinished cell

                if (answers[r][c] != board[r][c]) return; // wrong letter
            }
        }

        // Player won
        window.won();
    }

    // Valid Cell to click
    class CrosswordCell extends JTextField {
        int row, col;

        public CrosswordCell () {
            this.setOpaque(true);
            this.setBackground(Color.WHITE);
            this.setHorizontalAlignment(CENTER);
            this.setFocusTraversalKeysEnabled(false);

            // add focus listener
            this.addFocusListener(new FocusAdapter() {
                public void focusGained(FocusEvent e) {
                    currentRow = row;
                    currentCol = col;
                }
            });

            // add document filter
            ((AbstractDocument) this.getDocument()).setDocumentFilter(new DocumentFilter() {
                @Override
                public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
                        throws BadLocationException {

                    if (string == null) return;

                    if (fb.getDocument().getLength() == 0) {
                        super.insertString(fb, 0, string.substring(0, 1), attr);
                    }
                }

                @Override
                public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                        throws BadLocationException {

                    fb.remove(0, fb.getDocument().getLength());

                    if (text != null && !text.isEmpty()) {
                        super.insertString(fb, 0, text.substring(0, 1), attrs);
                    }
                }
            });

            // add document listener
            this.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    move();
                }

                @Override
                public void removeUpdate(DocumentEvent e) {}

                @Override
                public void changedUpdate(DocumentEvent e) {}

                private void move () {
                    if (getText().length() == 1) {
                        if (getText().isEmpty()) {
                            answers[row][col] = '\0';
                        } else {
                            answers[row][col] = getText().charAt(0);
                        }

                        moveToNextCell(row, col);

                        checkWin();
                    }
                }
            });
        }
    }

    // Start cell
    class StartCell extends JPanel {
        private JTextField field;
        private JLabel label;

        int row, col;

        public StartCell(String number) {
            setLayout(new BorderLayout());

            field = new JTextField();
            field.setHorizontalAlignment(JTextField.CENTER);
            field.setFocusTraversalKeysEnabled(false);

            label = new JLabel(number);
            label.setFont(new Font("Arial", Font.PLAIN, 10));

            setBorder(BorderFactory.createLineBorder(Color.BLACK));

            add(label, BorderLayout.NORTH);
            add(field, BorderLayout.CENTER);

            // add focus listener
            field.addFocusListener(new FocusAdapter() {
                public void focusGained(FocusEvent e) {
                    currentRow = row;
                    currentCol = col;
                }
            });

            // add document filter
            ((AbstractDocument) field.getDocument()).setDocumentFilter(new DocumentFilter() {
                @Override
                public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
                        throws BadLocationException {

                    if (string == null) return;

                    if (fb.getDocument().getLength() == 0) {
                        super.insertString(fb, 0, string.substring(0, 1), attr);
                    }
                }

                @Override
                public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                        throws BadLocationException {

                    fb.remove(0, fb.getDocument().getLength());

                    if (text != null && !text.isEmpty()) {
                        super.insertString(fb, 0, text.substring(0, 1), attrs);
                    }
                }
            });

            // add document listener
            field.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    move();
                }

                @Override
                public void removeUpdate(DocumentEvent e) {}

                @Override
                public void changedUpdate(DocumentEvent e) { }

                private void move () {
                    if (field.getText().length() == 1) {
                        if (field.getText().isEmpty()) {
                            answers[row][col] = '\0';
                        } else {
                            answers[row][col] = field.getText().charAt(0);
                        }

                        moveToNextCell(row, col);

                        checkWin();
                    }
                }
            });
        }

        public void addStart(String num) {
            label.setText(label.getText() + " & " + num);
        }
    }

    // Invalid Cell for visual
    class InvalidCell extends JLabel {
        public InvalidCell () {
            this.setOpaque(true);
            this.setBackground(Color.BLACK);
            this.setHorizontalAlignment(CENTER);
        }
    }
}