package be.planetegem.mammon.util;

import java.util.ArrayList;

import javax.swing.JLabel;

import be.planetegem.mammon.statics.StyleSheet;

public class FormattedCell {
    public int y;
    public int height;
    public String string;
    public float value;

    public int lineCount(double width){
        int lines = 1;

        JLabel tester = new JLabel();
        tester.setFont(StyleSheet.documentFont);
        
        ArrayList<String> lineArray = new ArrayList<String>();
        String testString = "";
        lineArray.add(testString);

        String[] words = this.string.split(" ");
        for (String word : words){
            testString += word + " ";
            tester.setText(testString);

            if (tester.getPreferredSize().getWidth() >= width){
                lineArray.add(word);
                testString = word;
                lines++;
            } else {
                lineArray.set(lines -1, testString);
            }
        }
        return lines;
    }

    public FormattedCell(int y, int height, String string){
        this.y = y;
        this.height = height;
        this.string = string;
        this.value = 0f;
    }
    public FormattedCell(int y, int height, float value){
        this.y = y;
        this.height = height;
        this.string = Float.toString(value);
        this.value = value;
    }
}
