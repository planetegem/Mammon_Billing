package be.planetegem.mammon;

import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

import be.planetegem.mammon.statics.StyleSheet;
import be.planetegem.mammon.util.JAnchor;
import be.planetegem.mammon.util.JFontLabel;
import be.planetegem.mammon.util.JResizedImage;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.io.IOException;

public class IntroSplash extends JInternalFrame implements ActionListener {
    public void actionPerformed(ActionEvent e){
        this.dispose();
    }
    public IntroSplash(){
        super("WELKOM BIJ MAMMON BILLING", false, false, false, false);

        // Assign dimensions of Splash Screen
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension splashSize = new Dimension(screenSize.width, screenSize.height - 60);
        int marginLeft = (int) Math.round((screenSize.getWidth() - splashSize.getWidth())*0.5);
        int marginTop = (int) Math.round((screenSize.getHeight() - splashSize.getHeight())*0.25);
        
        setPreferredSize(splashSize);
        setLocation(marginLeft, marginTop);
        setVisible(true);

        // Content pane: wizardBlue BoxLayout
        getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        getContentPane().setBackground(StyleSheet.wizardBlue);

        // Margin above image
        getContentPane().add(Box.createRigidArea(new Dimension(splashSize.width, 5)));

        // Jean-Pierre image
        Dimension logoSize = new Dimension(400, 400);
        JPanel logoContainer = new JPanel();
        logoContainer.setBackground(StyleSheet.wizardBlue);
        logoContainer.setPreferredSize(logoSize);
        getContentPane().add(logoContainer);
        try {
            JResizedImage image = new JResizedImage("assets/jpvr.png", logoSize.width, logoSize.height);
            JLabel jpLogo = new JLabel(new ImageIcon(image.resized));
            logoContainer.add(jpLogo);
        } catch (IOException e){};
        
        // Image source caption
        JPanel logoCaption = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        logoCaption.setBackground(StyleSheet.wizardBlue);
        getContentPane().add(logoCaption);
        JFontLabel photoLabel = new JFontLabel("Foto: Jean-Pierre Van Rossem (2014)", StyleSheet.captionFont);
        logoCaption.add (photoLabel);

        JPanel logoCaption2 = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        logoCaption2.setBackground(StyleSheet.wizardBlue);
        getContentPane().add(logoCaption2);
        JFontLabel cText1 = new JFontLabel("Bron: Wikimedia Commons - ", StyleSheet.captionFont);
        JAnchor cText2 = new JAnchor("link", "https://commons.wikimedia.org/wiki/File:Jean_Pierre_Van_Rossem.jpg");
        cText2.setFont(StyleSheet.captionFont);
        logoCaption2.add(cText1);
        logoCaption2.add(cText2);

        getContentPane().add(Box.createRigidArea(new Dimension(splashSize.width, 20)));

        // Welcome text
        JPanel welcomeSplashContainer = new JPanel();
        welcomeSplashContainer.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
        getContentPane().add(welcomeSplashContainer);
        JFontLabel welcomeSplash = new JFontLabel("WELKOM BIJ", StyleSheet.smallerLogoFont);
        welcomeSplashContainer.setBackground(StyleSheet.wizardBlue);
        welcomeSplashContainer.add(welcomeSplash);

        JPanel titleSplashContainer = new JPanel();
        titleSplashContainer.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
        getContentPane().add(titleSplashContainer);
        JFontLabel titleSplash = new JFontLabel("MAMMON BILLING", StyleSheet.logoFont);
        titleSplashContainer.setBackground(StyleSheet.wizardBlue);
        titleSplashContainer.add(titleSplash);

        getContentPane().add(Box.createRigidArea(new Dimension(splashSize.width, 10)));

        // Intro text
        String introText = "";
        introText += "Uw factuur aan 170 per uur! Met Mammon Billing maakt u facturen in<br>";
        introText += "stijl, want geld koopt misschien geen stijl, stijl koopt niettemin wel geld.<br>";
        introText += "<br>";
        introText += "Dank aan Jean-Pierre Van Rossem, die tijdens een séance de opdracht<br>";
        introText += "gaf die deze software mogelijk heeft gemaakt.";
        JPanel introTextContainer = new JPanel();
        introTextContainer.setBackground(StyleSheet.wizardBlue);
        introTextContainer.add(new JFontLabel(introText, StyleSheet.headerFont));
        getContentPane().add(introTextContainer);
        
        getContentPane().add(Box.createRigidArea(new Dimension(splashSize.width, 5)));
        
        // Start button
        JPanel buttonContainer = new JPanel();
        JButton makeProfile = new JButton("START");       
        makeProfile.addActionListener(this);
        makeProfile.setFont(StyleSheet.headerFont);
        buttonContainer.add(makeProfile);
        buttonContainer.setBackground(StyleSheet.wizardBlue);
        getContentPane().add(buttonContainer);
        getContentPane().add(Box.createVerticalGlue());
        getContentPane().add(Box.createRigidArea(new Dimension(splashSize.width, 35)));

        pack();
    }
}