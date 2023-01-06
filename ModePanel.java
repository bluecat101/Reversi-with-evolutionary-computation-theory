import javax.swing.*;

public class ModePanel extends JPanel{
  JButton returnButton;
  JButton single,multi;
  ModePanel(){
    setLayout(null);
    single = new JButton("Single");
    single.setBounds(400,30,250,100);
    multi = new JButton("Multi");
    multi.setBounds(400,150,250,100);
    returnButton = new JButton("Return Title");
    returnButton.setBounds(400, 425, 250, 100);
    add(returnButton);add(multi);add(single);
  }
}