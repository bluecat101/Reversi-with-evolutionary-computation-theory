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
  protected SinglePanel singlepanel;
  protected ModePanel modepanel;
  protected MultiPanel multipanel;
  protected Model ai;
  protected Model model;
  protected boolean server;
  protected MultiServerPanel multiserverpanel;
 
  public ReversiView(Model m,String st) {
    super(st);
    this.setTitle("Leversi Panel");
    model=m;
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
    singlepanel = new SinglePanel();
    singlepanel.returnButton.addActionListener(this);
    singlepanel.startButton.addActionListener(this);
    singlepanel.level1.addActionListener(this);
    singlepanel.level2.addActionListener(this);
    singlepanel.level3.addActionListener(this);
    ai=new Ai_1(model);

    modepanel = new ModePanel();
    modepanel.returnButton.addActionListener(this);
    modepanel.single.addActionListener(this);
    modepanel.multi.addActionListener(this);

    multipanel = new MultiPanel();
    multipanel.returnButton.addActionListener(this);
    multipanel.serverButton.addActionListener(this);
    multipanel.clientButton.addActionListener(this);

    multiserverpanel = new MultiServerPanel();
    multiserverpanel.password.addActionListener(this);
    multiserverpanel.returnButton.addActionListener(this);

    cardPanel = new JPanel();
    layout = new CardLayout();
    cardPanel.setLayout(layout);

    cardPanel.add(titlepanel, "title");
    cardPanel.add(settingpanel, "setting");
    cardPanel.add(gamepanel, "game");
    cardPanel.add(singlepanel,"single");
    cardPanel.add(modepanel,"mode");
    cardPanel.add(multipanel,"multi");
    cardPanel.add(multiserverpanel,"multiserver");

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

  public JButton getSingleStartButton(){
    return singlepanel.startButton;
  }

  public JTextField getMultiPasswordBox(){
    return multiserverpanel.password;
  }

  public Model getAiMode(){
    return ai;
  }

  public boolean getSVMode(){
    return server;
  }

  public void movepanel(String s){
    layout.show(cardPanel,s);
  }
  public void actionPerformed(ActionEvent e){
    if(e.getSource()==titlepanel.start){
      movepanel("mode");
    }else if(e.getSource()==titlepanel.setting){
      movepanel("setting");
    }else if(e.getSource()==titlepanel.finish){
      System.exit(0);
    }else if(e.getSource()==settingpanel.returnButton){
      movepanel("title");
    }else if(e.getSource()==gamepanel.finish){
      movepanel("title");
    }else if(e.getSource()==singlepanel.returnButton){
      movepanel("mode");
    }else if(e.getSource()==singlepanel.startButton){
      movepanel("game");
      getPanel().requestFocus();
    }else if(e.getSource()==modepanel.returnButton){
      movepanel("title");
    }else if(e.getSource()==modepanel.single){
      movepanel("single");
    }else if(e.getSource()==modepanel.multi){
      movepanel("multi");
    }else if(e.getSource()==singlepanel.level1){
      ai=new Ai_1(model);//delete 引数
    }else if(e.getSource()==singlepanel.level2){
      ai=new Ai_2(model);//delete　引数
    }else if(e.getSource()==singlepanel.level3){
      ai=new Ai_3(model);
    }else if(e.getSource()==multipanel.returnButton){
      movepanel("mode");
    }else if(e.getSource()==multipanel.serverButton){
      server = true;movepanel("multiserver");
      //サーバーのボタンが押されたときの処理
    }else if(e.getSource()==multipanel.clientButton){
      server = false;movepanel("multiserver");
      //クライアントのボタンが押されたときの処理
    }else if(e.getSource()==multiserverpanel.password){
      movepanel("game");
      getPanel().requestFocus();
    }else if(e.getSource()==multiserverpanel.returnButton){
      movepanel("multi");
    }
  }
}