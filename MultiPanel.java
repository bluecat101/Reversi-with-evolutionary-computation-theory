import javax.swing.*;

public class MultiPanel extends JPanel{
  JButton returnButton,serverButton,clientButton;
  MultiPanel(){
    setLayout(null);
    serverButton = new JButton("Make a room");
    serverButton.setBounds(400,30,250,100);
    clientButton = new JButton("Join a room");
    clientButton.setBounds(400,150,250,100);
    returnButton = new JButton("Back");
    returnButton.setBounds(400, 425, 250, 100);
    add(returnButton);add(clientButton);add(serverButton);
  }
}
