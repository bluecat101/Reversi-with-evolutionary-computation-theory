import javax.swing.*;

public class MultiServerPanel extends JPanel{
  JButton returnButton;
  JTextField password;
  MultiServerPanel(){
    setLayout(null);
    password = new JTextField();
    password.setBounds(400,270,250,100);
    returnButton = new JButton("Back");
    returnButton.setBounds(400, 425, 250, 100);
    add(returnButton);add(password);
  }
}