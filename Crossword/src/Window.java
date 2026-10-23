/*
 Window where all the components will be put together
 */

import javax.swing.*;
import java.awt.*;

public class Window extends JFrame {
    JLabel titleLabel;
    Crossword_Panel crosswordPanel;
    Clue_Panel cluePanel;

    public Window(Game game) {
        this.setTitle("CROSSWORD GAME");
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setLayout(new FlowLayout());
        this.setSize(500, 700);

        // Create Title
        titleLabel = new JLabel();
        titleLabel.setPreferredSize(new Dimension(400, 25));
        titleLabel.setFont(new Font("Consolas", Font.BOLD, 18));
        titleLabel.setText("CROSSWORD PUZZLE");
        titleLabel.setHorizontalAlignment(JLabel.CENTER);

        // Create Crossword Panel and Clue Panel
        crosswordPanel = new Crossword_Panel(this, game.getBoard(), game.getWords());
        cluePanel = new Clue_Panel(game.getWords());

        // Add components to window
        this.add(titleLabel);
        this.add(crosswordPanel);
        this.add(cluePanel);

        // Place Window in middle of screen
        this.setLocationRelativeTo(null);

        // View window
        this.setVisible(true);
    }

    public void won () {
        titleLabel.setVisible(false);
        crosswordPanel.setVisible(false);
        cluePanel.setVisible(false);

        WinPanel winPanel = new WinPanel();

        this.add(winPanel);
        this.setSize(400, 400);
        this.setTitle("WINNER");
    }
}