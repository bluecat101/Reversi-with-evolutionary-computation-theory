import javax.swing.*;

public class MultiPanel extends JPanel{
  JButton returnButton;
  JButton serverButton,clientButton,startButton;
  MultiPanel(){
    setLayout(null);
    serverButton = new JButton("Make a room");
    serverButton.setBounds(400,30,250,100);
    clientButton = new JButton("Join a room");
    clientButton.setBounds(400,150,250,100);
    startButton = new JButton("Start");
    startButton.setBounds(400,270,250,100);
    returnButton = new JButton("Back");
    returnButton.setBounds(400, 425, 250, 100);
    add(returnButton);add(startButton);add(clientButton);add(serverButton);
  }
}
