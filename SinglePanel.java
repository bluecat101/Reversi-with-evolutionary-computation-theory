import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class SinglePanel extends JPanel{
  JButton returnButton,startButton;
  JButton level1,level2,level3;
  JComboBox<String> cb;
  SinglePanel(){
    setLayout(null);
    cb=new JComboBox<>();
    cb.setFont(new Font("HGP創英角ﾎﾟｯﾌﾟ体", Font.BOLD, 60));
    cb.addItem("level1");cb.addItem("level2");cb.addItem("level3");
    cb.setBounds(400,30,250,100);
    startButton = new JButton("Start");
    startButton.setBounds(400,150,250,100);
    returnButton = new JButton("Return Title");
    returnButton.setBounds(400, 425, 250, 100);
    add(returnButton);add(startButton);add(cb);
  }
}