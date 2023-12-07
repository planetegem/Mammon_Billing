package be.planetegem.mammon.db;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import be.planetegem.mammon.Mammon;

public class DbConsole extends DbCommands {
    public void showConsole(){
        if (!consoleContainer.isVisible()){
            consoleContainer.setVisible(true);
            for (String entry : log){
                console.append(entry + "\n");
            }
            JScrollBar bar = scrollPane.getVerticalScrollBar();
            bar.addAdjustmentListener(new AdjustmentListener() {
                public void adjustmentValueChanged(AdjustmentEvent e){
                    e.getAdjustable().setValue(e.getAdjustable().getMaximum());
                    bar.removeAdjustmentListener(this);
                }
            });
        }
    }
    public DbConsole(Mammon parent, JDesktopPane ctx){
        super();
        this.parent = parent;
        this.ctx = ctx;
                
        parent.getContentPane().add(consoleContainer);
        ctx.moveToFront(consoleContainer);
        consoleContainer.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

        // Console dimensions & layout
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension windowSize = new Dimension (300, 250);
        int leftMargin = (int) Math.round((screenSize.getWidth() - windowSize.width));
        int topMargin = (int) Math.round((screenSize.getHeight() - windowSize.height)*0.10);

        consoleContainer.setSize(windowSize);
        consoleContainer.setLocation(leftMargin, topMargin);
        consoleContainer.setVisible(false);

        console = new JTextArea(10, 10);
        console.setEditable(false);
        console.setLineWrap(true);
        console.setWrapStyleWord(true);
        
        scrollPane = new JScrollPane(console);
        scrollPane.setPreferredSize(new Dimension(30, 500));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        consoleContainer.add(scrollPane);

        checkUsersTable();
        checkCustomersTable();
        checkInvoicesTable();
        checkInvoiceLinesTable();

    }
}
