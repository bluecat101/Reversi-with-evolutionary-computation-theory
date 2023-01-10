import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class SinglePanel extends JPanel{
  JButton returnButton,startButton;
  JButton level1,level2,level3;
  SinglePanel(){
    setLayout(null);
    JPanel levPanel = new JPanel();
    level1 = new JButton("level1");
    level2 = new JButton("level2");
    level3 = new JButton("level3");
    levPanel.setLayout(new GridLayout(3,1));
    levPanel.add(level1);levPanel.add(level2);levPanel.add(level3);
    levPanel.setBounds(400,30,250,100);
    startButton = new JButton("Start");
    startButton.setBounds(400,150,250,100);
    returnButton = new JButton("Return Title");
    returnButton.setBounds(400, 425, 250, 100);
    add(returnButton);add(startButton);add(levPanel);
  }
}