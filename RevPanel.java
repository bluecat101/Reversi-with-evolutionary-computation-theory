import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class RevPanel extends JPanel{
  JButton returnButton,startButton;
  JButton revel1,revel2,revel3;
  RevPanel(){
    setLayout(null);
    JPanel revPanel = new JPanel();
    revel1 = new JButton("revel1");
    revel2 = new JButton("revel2");
    revel3 = new JButton("revel3");
    revPanel.setLayout(new GridLayout(3,1));
    revPanel.add(revel1);revPanel.add(revel2);revPanel.add(revel3);
    revPanel.setBounds(400,30,250,100);
    startButton = new JButton("Start");
    startButton.setBounds(400,150,250,100);
    returnButton = new JButton("Return Title");
    returnButton.setBounds(400, 425, 250, 100);
    add(returnButton);add(startButton);add(revPanel);
  }
}