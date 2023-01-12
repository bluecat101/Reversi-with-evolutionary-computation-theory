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
  protected int a=0;
 
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
    singlepanel.cb.addItemListener(new SelectItemListener());
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
  class SelectItemListener implements ItemListener {
    public void itemStateChanged(final ItemEvent ie) {
      final int imgidx = singlepanel.cb.getSelectedIndex();
      if (imgidx == 0) {
        ai=new Ai_1(model);
      } else if (imgidx == 1) {
        ai=new Ai_2(model);
      } else if (imgidx == 2) {
        ai=new Ai_3(model);
      }
    }
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

  //一人プレイを開始するときのボタン
  public JButton getSingleStartButton(){
    return singlepanel.startButton;
  }

  //二人プレイの時にこのテキストフィールドに合言葉を入れてエンターを押したときにゲーム画面に
  //遷移するようにした。このテキストフィールドを使えばポート番号をもらうのとサーバー立ち上げるのが同時にできると思う。
  //サーバー側もクライアント側もこのテキストのエンターを押したら画面が遷移する。
  public JTextField getMultiPasswordBox(){
    return multiserverpanel.password;
  }

  //aiのクラスが入っている。
  public Model getAiMode(){
    return ai;
  }

  //サーバーかどうかの変数。trueならサーバー
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
    }else if(e.getSource()==multipanel.returnButton){
      movepanel("mode");
    }else if(e.getSource()==multipanel.serverButton){
      server = true;
      multiserverpanel.s="合言葉を決定";
      movepanel("multiserver");
      //サーバーのボタンが押されたときの処理
    }else if(e.getSource()==multipanel.clientButton){
      server = false;
      multiserverpanel.s="決めた合言葉を入力";
      movepanel("multiserver");
      //クライアントのボタンが押されたときの処理
    }else if(e.getSource()==multiserverpanel.password){
      movepanel("game");
      getPanel().requestFocus();
      a=1;
    }else if(e.getSource()==multiserverpanel.returnButton){
      movepanel("multi");
    }
  }
}