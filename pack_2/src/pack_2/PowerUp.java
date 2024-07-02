package pack_2;

import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;

public class PowerUp {
    private Timer timer;
    private int delay = 140;
    private Board board;
    //private float fadeOpacity = 1.0f;
    private JButton nextLevelButton;

    public PowerUp(Board board) {
        this.board = board;
        timer = new Timer(delay, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                board.actionPerformed(e);
            }
        });
    }

    public void startTimer() {
        timer.start();
    }

    public void changeSpeed() {
        delay -= 20;
        if (delay < 20) { // Set a minimum delay limit to prevent it from becoming too fast
            delay = 20;
        }
       
        timer.stop();
        timer.setDelay(delay);
        //timer.start();
    }

    public void stopTimer() {
        if (timer != null) {
            timer.stop();
        }
    }
    
    public JButton nextButton(int pos_x, int pos_y) {
        nextLevelButton = new JButton("Next Level");
        nextLevelButton.setBounds(pos_x / 2 - 75, pos_y / 2 - 25, 150, 70);
        nextLevelButton.setVisible(true);
        nextLevelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                board.resumeGame(); // Call resumeGame method in Board
                nextLevelButton.setVisible(false);
            }
        });
        return nextLevelButton;
    }

   
}