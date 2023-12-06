package be.planetegem.mammon;

import javax.swing.Box;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import be.planetegem.mammon.db.DbConsole;
import be.planetegem.mammon.ivf.InvoiceFactory;
import be.planetegem.mammon.statics.StyleSheet;
import be.planetegem.mammon.wizards.CustomerWizard;
import be.planetegem.mammon.wizards.ProfileWizard;

import java.util.HashMap;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Mammon extends JFrame implements ActionListener
{   
    // Main components:
    private DbConsole db; // Manages database interactions 
    private InvoiceFactory ivf; // composes invoice 
    private ProfileWizard pw; // Profile creation interface
    private CustomerWizard cw; // Customer creation interface
    private IntroSplash splash; // Welcome screen
    private PdfPenman pdfPen; // holds logic to generate pdf

    private JDesktopPane cPane; // Main content pane (for internal frame management)
    private JMenuItem infoItem, consoleItem; // Menu items

    // getters
    public DbConsole getDb(){
        return db;
    }
    public InvoiceFactory getIvf(){
        return ivf;
    }
    public JDesktopPane getDesktopPane(){
        return cPane;
    }
    public PdfPenman getPdfPenman(){
        return pdfPen;
    }

    // setters
    public void setProfileWizard(){
        if (pw != null){
            pw.dispose();
        }
        pw = new ProfileWizard(this);
        getContentPane().add(pw);
        cPane.moveToFront(pw);
    }
    public void setProfileWizard(HashMap<String, String> profile){
        if (pw != null){
            pw.dispose();
        }
        pw = new ProfileWizard(this, profile);
        getContentPane().add(pw);
        cPane.moveToFront(pw);
    }
    public void setCustomerWizard(){
        if (cw != null){
            cw.dispose();
        }
        cw = new CustomerWizard(this);
        getContentPane().add(cw);
        cPane.moveToFront(cw);
    }
    public void setCustomerWizard(HashMap<String, String> customer){
        if (cw != null){
            cw.dispose();
        }
        cw = new CustomerWizard(this, customer);
        getContentPane().add(cw);
        cPane.moveToFront(cw);
    }
    public void setIntroSplash(){
        if (splash != null){
            splash.dispose();
        }
        splash = new IntroSplash();
        getContentPane().add(splash);
        cPane.moveToFront(splash);
    }


    public void actionPerformed(ActionEvent e){
        if (e.getSource() == consoleItem){
            db.showConsole();
        }
        if (e.getSource() == infoItem){
            setIntroSplash();
        }
    }

    Mammon() {
        super("Mammon Billing");
        cPane = new JDesktopPane();
        setContentPane(cPane);
        cPane.setDragMode(JDesktopPane.OUTLINE_DRAG_MODE);

        db = new DbConsole(this, cPane);
        pdfPen = new PdfPenman(this);

        JPanel mainContent = new JPanel();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        mainContent.setLayout(new BorderLayout());
        mainContent.setBounds(0, 0, screenSize.width, screenSize.height - 60);
        cPane.add(mainContent);

        JMenuBar topMenu = new JMenuBar();
        mainContent.add(BorderLayout.NORTH, topMenu);
        JMenu realMenu = new JMenu("?");
        topMenu.add(Box.createHorizontalGlue());
        topMenu.add(realMenu);

        infoItem = new JMenuItem("info");
        infoItem.addActionListener(this);
        consoleItem = new JMenuItem("console");
        consoleItem.addActionListener(this);
        realMenu.add(infoItem);
        realMenu.add(consoleItem);

        JPanel ivfContainer = new JPanel();
        ivfContainer.setBackground(StyleSheet.backgroundGrey);
        ivfContainer.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));

        ivf = new InvoiceFactory(this);
        ivfContainer.add(ivf);

        JScrollPane scrollPane = new JScrollPane(ivfContainer);
        scrollPane.setPreferredSize(new Dimension(30, 500));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        mainContent.add(BorderLayout.CENTER, scrollPane);

        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static void main( String[] args )
    {
        Mammon app = new Mammon();
        app.setVisible(true);
    }
}