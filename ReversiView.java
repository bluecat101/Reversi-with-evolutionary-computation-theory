import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.LineBorder;
import java.util.*;

class ReversiView extends JFrame{
  protected Model model;
  protected Model.ReversiModel reversiModel;
  protected GamePanel GamePanel;
 
  public ReversiView(Model m,String st) {
    super(st);
    model = m;
    this.setTitle("Reversi Panel");
    GamePanel = new GamePanel(model);
    this.add(GamePanel);
 
    // pack は JFrameのサイズを自動設定するメソッド．
    // this.setSize(300,200); などの代わり
    this.pack();
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.setVisible(true);
  }

  public JPanel getPanel(){
    return GamePanel.panel;
  }

  public JPanel getChatPanel(){
    return GamePanel.chatpanel;
  }

  public JButton getResetButton(){
    return GamePanel.reset;
  }

  public JButton getFinishButton(){
    return GamePanel.finish;
  }
}