import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
// import javax.swing.border.LineBorder;
// import java.util.*;

class ReversiView extends JFrame implements ActionListener{
  protected GamePanel gamepanel;
  protected SettingPanel settingpanel;
  protected TitlePanel titlepanel;
  protected JPanel cardPanel;
  protected CardLayout layout;
 
  public ReversiView(Model m,String st) {
    super(st);
    this.setTitle("Reversi Panel");
    
    /* タイトルパネル */
    // titlepanel = new TitlePanel();
    // titlepanel.start.addActionListener(this);
    // titlepanel.setting.addActionListener(this);
    // titlepanel.finish.addActionListener(this);

    /* パネル2 */
    gamepanel = new GamePanel(m);
    // gamepanel.finish.addActionListener(this);

    /*パネル3 */
    // settingpanel = new SettingPanel();
    // settingpanel.returnButton.addActionListener(this);

    cardPanel = new JPanel();
    layout = new CardLayout();
    cardPanel.setLayout(layout);

    // cardPanel.add(titlepanel, "title");
    // cardPanel.add(settingpanel, "setting");
    cardPanel.add(gamepanel, "game");

    getContentPane().add(cardPanel, BorderLayout.CENTER);
    // pack は JFrameのサイズを自動設定するメソッド．

    // this.setSize(300,200); などの代わり
    this.pack();
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.setVisible(true);
  }

  public JPanel getPanel(){
    return gamepanel.panel;
  }

  public JPanel getChatPanel(){
    return gamepanel.chatpanel;
  }

  public JButton getResetButton(){
    return gamepanel.reset;
  }

  public JButton getFinishButton(){
    return gamepanel.finish;
  }

  public JButton getButton(){
    return settingpanel.returnButton;
  }

  public void actionPerformed(ActionEvent e){
    // if(e.getSource()==titlepanel.start){
    //   layout.show(cardPanel,"game");
    // }else if(e.getSource()==titlepanel.setting){
    //   layout.show(cardPanel,"setting");
    // }else if(e.getSource()==titlepanel.finish){
    //   System.exit(0);
    // }else 
    if(e.getSource()==settingpanel.returnButton){
      layout.show(cardPanel,"game");
    }
    // else if(e.getSource()==gamepanel.finish){
    //   layout.show(cardPanel,"title");
    // }
  }
  // public void keyTyped(KeyEvent e){}
  // public void keyPressed(KeyEvent e){}
  // public void keyReleased(KeyEvent e){}
}