/*
 Class for JPanel where the clue's for the crossword game gui will be stored
 */

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class Clue_Panel extends JPanel {
    private final Word[] words;

    public Clue_Panel (Word[] words) {
        // initialization
        this.words = words;

        this.setPreferredSize(new Dimension(450, 150));
        this.setLayout(new GridLayout(5, 2));

        TitledBorder border = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(66, 66, 66), 3),
                "Clue's"
        );

        border.setTitleFont(new Font("Consolas", Font.BOLD, 14));

        this.setBorder(border);

        addDescriptions();
    }

    public void addDescriptions () {
        int count = 1;

        for (Word word : words) {
            JTextPane textPane = new JTextPane();

            if (word.getDirection() == 0) {
                // horizontal
                textPane.setText(count + " → | " + word.getDescription());
            } else {
                // vertical
                textPane.setText(count + " ↑ | " + word.getDescription());
            }

            textPane.setFont(new Font("Consolas", Font.PLAIN, 8));

            textPane.setEditable(false);
            textPane.setFocusable(false);
            textPane.setCursor(null);
            textPane.setHighlighter(null);
            textPane.setOpaque(false);

            count++;
            this.add(textPane);
        }
    }
}