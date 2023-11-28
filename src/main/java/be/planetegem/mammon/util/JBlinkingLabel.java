package be.planetegem.mammon.util;

import javax.swing.Timer;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;

// credits to https://www.rgagnon.com/javadetails/java-0616.html

public class JBlinkingLabel extends JLabel {
    public JBlinkingLabel(String text, int blinkRate) {
        super(text);
        Timer timer = new Timer(blinkRate , new TimerListener(this));
        timer.setInitialDelay(0);
        timer.start();
    }
    private class TimerListener implements ActionListener {
        private JBlinkingLabel bl;
        private Color bg;
        private Color fg;
        private boolean isForeground = true;
      
        public TimerListener(JBlinkingLabel bl) {
            this.bl = bl;
            fg = bl.getForeground();
            bg = bl.getBackground();
        }
        public void actionPerformed(ActionEvent e) {
            if (isForeground) {
                bl.setForeground(fg);
            } else {
                bl.setForeground(bg);
            }
            isForeground = !isForeground;
        }
    }
}
