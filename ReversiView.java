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
  protected LevPanel levpanel;
 
  public ReversiView(Model m,String st) {
    super(st);
    this.setTitle("Leversi Panel");
    
    /* タイトルパネル */
    titlepanel = new TitlePanel();
    titlepanel.start.addActionListener(this);
    titlepanel.setting.addActionListener(this);
    titlepanel.finish.addActionListener(this);

    /* パネル2 */
    gamepanel = new GamePanel(m);
    gamepanel.finish.addActionListener(this);

    /*パネル3 */
    settingpanel = new SettingPanel();
    settingpanel.returnButton.addActionListener(this);

    /*パネル4 */
    levpanel = new LevPanel();
    levpanel.returnButton.addActionListener(this);
    levpanel.startButton.addActionListener(this);

    cardPanel = new JPanel();
    layout = new CardLayout();
    cardPanel.setLayout(layout);

    cardPanel.add(titlepanel, "title");
    cardPanel.add(settingpanel, "setting");
    cardPanel.add(gamepanel, "game");
    cardPanel.add(levpanel,"level");

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

  public JTextField getChatBox(){
    return gamepanel.chatbox;
  }

  public JButton getChatButton(){
    return gamepanel.chat;
  }

  public JButton level1Button(){
    return levpanel.level1;
  }

  public JButton level2Button(){
    return levpanel.level2;
  }

  public JButton level3Button(){
    return levpanel.level3;
  }

  public void actionPerformed(ActionEvent e){
    if(e.getSource()==titlepanel.start){
      layout.show(cardPanel,"level");
    }else if(e.getSource()==titlepanel.setting){
      layout.show(cardPanel,"setting");
    }else if(e.getSource()==titlepanel.finish){
      System.exit(0);
    }else if(e.getSource()==settingpanel.returnButton){
      layout.show(cardPanel,"title");
    }else if(e.getSource()==gamepanel.finish){
      layout.show(cardPanel,"title");
    }else if(e.getSource()==levpanel.returnButton){
      layout.show(cardPanel,"title");
    }else if(e.getSource()==levpanel.startButton){
      layout.show(cardPanel,"game");
      getPanel().requestFocus();
    }
  }
}