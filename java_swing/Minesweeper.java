

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.event.*;
import java.io.IOException;
import java.awt.Color;

import javax.imageio.ImageIO;
import java.awt.Image;

public class Minesweeper {

    public final int COLUMNS = 16;
    public final int ROWS = 10;
    public final int CELL_SIZE = 50;
    public final int FRAME_WIDTH = 1000;
    public final int FRAME_HEIGHT = 700;
    public Cell[][] cells;
    JFrame gameFrame;
    JLabel narratorLabel;
    Image flagImg;


    Minesweeper() {

        try {
            flagImg = ImageIO.read(getClass().getResource("assets/flag.png"));
        } catch (IOException ex) {
            System.out.println("Exception while attempting to use image asset: " + ex);
        }
        
        cells = new Cell[ROWS][COLUMNS];

        gameFrame = new JFrame("Minesweeper Improved :)");
        gameFrame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gameFrame.setLayout(null);

        initializeCells();

        narratorLabel = new JLabel("Click anywhere!");
        narratorLabel.setBounds(FRAME_WIDTH / 2 - 80 / 2, FRAME_HEIGHT - 60, 130, 30);
        gameFrame.add(narratorLabel);

        gameFrame.setVisible(true);
    }

    void initializeCells() {
        int rpos, cpos, x_offset, y_offset;

        for (int i = 0; i < ROWS * COLUMNS; i++) {
            rpos = i / COLUMNS;
            cpos = i % COLUMNS;

            y_offset = 50;
            x_offset = (FRAME_WIDTH - COLUMNS * (CELL_SIZE - 5)) / 2;

            cells[rpos][cpos] = new Cell(rpos, cpos, CELL_SIZE);
            cells[rpos][cpos].setBounds(cpos * (CELL_SIZE - 5) + x_offset, rpos * (CELL_SIZE - 6) + y_offset, CELL_SIZE, CELL_SIZE);
            gameFrame.getContentPane().add(cells[rpos][cpos]);
        }

        for (int i = 0; i < ROWS * COLUMNS; i++) {
            rpos = i / COLUMNS;
            cpos = i % COLUMNS;

            cells[rpos][cpos].checkNeighbors();
        }
    }

    public class Cell extends JButton implements ActionListener {

        boolean touched;
        boolean mine;
        int row_pos;
        int column_pos;
        int mine_neighbors;
        boolean flagged;

        Cell(int rpos, int cpos, int cell_size) {
            super();
            this.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
            row_pos = rpos;
            column_pos = cpos;
            touched = flagged = false;

            mine = Math.random() > 0.12 ? false : true;

            this.setBackground(Color.WHITE);
            this.setOpaque(true);

            this.addActionListener(this);
            this.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent me) {
                    if (me.getButton() == MouseEvent.BUTTON3 || me.getButton() == MouseEvent.BUTTON2) {
                        if (!Cell.this.touched && !Cell.this.flagged) {
                            Image flagImg = Minesweeper.this.flagImg.getScaledInstance(CELL_SIZE - 10, CELL_SIZE - 10, Image.SCALE_FAST);
                            Cell.this.setIcon(new ImageIcon(flagImg));
                            Cell.this.flagged = true;
                        } else {
                            Cell.this.setIcon(null);
                        }
                        
                    }
                }
            });
        }

        public void setTouched(boolean tf) {
            this.setBackground(Color.GRAY);
            touched = tf;
        }
        
        public void actionPerformed(ActionEvent ae) {
            this.setTouched(true);

            if (mine) {
                this.setText("M");
                Minesweeper.main(null);
                gameFrame.dispose();
            } else if (mine_neighbors == 0) {
                this.setText("" + mine_neighbors);
                dispatchNeighbors(ae);
            } else
                this.setText("" + mine_neighbors);
            narratorLabel.setText("Yes we hear ya: " + mine_neighbors);
        }

        public void dispatchNeighbors(ActionEvent ae) {

            if (row_pos > 0 && !cells[row_pos - 1][column_pos].touched) {
                cells[row_pos - 1][column_pos].actionPerformed(ae);
            }
            if ((row_pos > 0 && column_pos + 1 < COLUMNS) && !cells[row_pos - 1][column_pos + 1].touched) {
                cells[row_pos - 1][column_pos + 1].actionPerformed(ae);
            }
            if (column_pos + 1 < COLUMNS && !cells[row_pos][column_pos + 1].touched) {
                cells[row_pos][column_pos + 1].actionPerformed(ae);
            }
            if ((row_pos + 1 < ROWS && column_pos + 1 < COLUMNS) && !cells[row_pos + 1][column_pos + 1].touched) {
                cells[row_pos + 1][column_pos + 1].actionPerformed(ae);
            }
            if (row_pos + 1 < ROWS && !cells[row_pos + 1][column_pos].touched) {
                cells[row_pos + 1][column_pos].actionPerformed(ae);
            }
            if ((row_pos + 1 < ROWS && column_pos > 0) && !cells[row_pos + 1][column_pos - 1].touched) {
                cells[row_pos + 1][column_pos - 1].actionPerformed(ae);
            }
            if (column_pos > 0 && !cells[row_pos][column_pos - 1].touched) {
                cells[row_pos][column_pos - 1].actionPerformed(ae);
            }
            if ((column_pos > 0 && row_pos > 0) && !cells[row_pos - 1][column_pos - 1].touched) {
                cells[row_pos - 1][column_pos - 1].actionPerformed(ae);
            }
        }

        // Checks how many neighbors are mines. Returns the number of neighbors that are mines.
        public void checkNeighbors() {
            mine_neighbors = 0;

            if (row_pos > 0 && cells[row_pos - 1][column_pos].mine)
                mine_neighbors++;
            if ((row_pos > 0 && column_pos + 1 < COLUMNS) && cells[row_pos - 1][column_pos + 1].mine)
                mine_neighbors++;
            if (column_pos + 1 < COLUMNS && cells[row_pos][column_pos + 1].mine)
                mine_neighbors++;
            if ((row_pos + 1 < ROWS && column_pos + 1 < COLUMNS) && cells[row_pos + 1][column_pos + 1].mine)
                mine_neighbors++;
            if (row_pos + 1 < ROWS && cells[row_pos + 1][column_pos].mine)
                mine_neighbors++;
            if ((row_pos + 1 < ROWS && column_pos > 0) && cells[row_pos + 1][column_pos - 1].mine)
                mine_neighbors++;
            if (column_pos > 0 && cells[row_pos][column_pos - 1].mine)
                mine_neighbors++;
            if ((column_pos > 0 && row_pos > 0) && cells[row_pos - 1][column_pos - 1].mine)
                mine_neighbors++;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new Minesweeper();
            }
        });
    }
}
