package converter;

import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args){
        JFrame app = new JFrame("F2C");
        app.setSize(400, 150);
        app.setLayout(new BorderLayout());
        app.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        //The Celsius box
        Box cRow = Box.createHorizontalBox();
        cRow.add(new JLabel("C: "));
        JTextField cField = new JTextField();
        cRow.add(cField);

        //The Farenheit box›
        Box fRow = Box.createHorizontalBox();
        fRow.add(new JLabel("F: "));
        JTextField fField = new JTextField();
        fRow.add(fField);

        //The temp box containers
        Box box = Box.createVerticalBox();
        box.add(cRow);
        box.add(fRow);
        app.add(box, BorderLayout.CENTER);

        app.setVisible(true);
    }
}
