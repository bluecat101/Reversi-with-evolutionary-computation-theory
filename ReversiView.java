import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
 
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
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
  protected int ai_level=1;
  protected boolean first=true;
  // protected Model ai;
  protected Model model;
  protected boolean server;
  protected MultiServerPanel multiserverpanel;
  protected int a=0;
  protected Clip clip;
  protected Clip bgm;
 
  public ReversiView(Model m,String st) {
    super(st);
    this.setTitle("Reversi Panel");
    model=m;
    clip = createClip(new File("set.wav"));
    bgm = createClip(new File("Tea_Time_Waltz.wav"));
    bgm.loop(Clip.LOOP_CONTINUOUSLY);
    bgm.start();
		//ここで再生メソッドの呼び出し
    /* タイトルパネル */
    titlepanel = new TitlePanel();
    titlepanel.start.addActionListener(this);
    titlepanel.setting.addActionListener(this);
    titlepanel.finish.addActionListener(this);

    /* パネル2 */
    gamepanel = new GamePanel(m,clip);
    gamepanel.finish.addActionListener(this);

    /*パネル3 */
    settingpanel = new SettingPanel();
    settingpanel.returnButton.addActionListener(this);

    /*パネル4 */
    singlepanel = new SinglePanel();
    singlepanel.returnButton.addActionListener(this);
    singlepanel.startButton.addActionListener(this);
    singlepanel.cb.addItemListener(new SelectItemListener());
    singlepanel.first.addItemListener(new SelectItemListener());

    // ai=new Ai_1(model);

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
    // this.pack();
    this.setSize(1090,635); 
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.setVisible(true);
  }
  public Clip getClip(){
    return clip;
  }
  class SelectItemListener implements ItemListener {
    public void itemStateChanged(final ItemEvent ie) {
      if(ie.getSource()==singlepanel.cb){
        final int imgidx = singlepanel.cb.getSelectedIndex();
        if (imgidx == 0) {
          ai_level=1;
        } else if (imgidx == 1) {
          ai_level=2;
        } else if (imgidx == 2) {
          ai_level=3;
        }
      }
      if(ie.getSource()==singlepanel.first){
        final int imgidx = singlepanel.first.getSelectedIndex();
        if(imgidx==0){
          first = true;
        }else if(imgidx==1){
          first = false;
        }
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
  // public Model getAiMode(){
  //   return ai;
  // }

  //trueなら先攻falseなら後攻
  public boolean getFirst(){
    return first;
  }

  //int型でaiのれべるを返す
  public int getAiLevel(){
    return ai_level;
  }

  //サーバーかどうかの変数。trueならサーバー
  public boolean getSVMode(){
    return server;
  }

  public void movepanel(String s){
    layout.show(cardPanel,s);
  }
  
  public static Clip createClip(File path) {
		//指定されたURLのオーディオ入力ストリームを取得
		try (AudioInputStream ais = AudioSystem.getAudioInputStream(path)){
			
			//ファイルの形式取得
			AudioFormat af = ais.getFormat();
			
			//単一のオーディオ形式を含む指定した情報からデータラインの情報オブジェクトを構築
			DataLine.Info dataLine = new DataLine.Info(Clip.class,af);
			
			//指定された Line.Info オブジェクトの記述に一致するラインを取得
			Clip c = (Clip)AudioSystem.getLine(dataLine);
			
			//再生準備完了
			c.open(ais);
			
			return c;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}
		return null;
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
      model.getReversiModel().initBoard();
      movepanel("title");
    }else if(e.getSource()==singlepanel.returnButton){
      movepanel("mode");
    }else if(e.getSource()==singlepanel.startButton){
      gamepanel.nochatbox("Ai");
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
      multiserverpanel.s="Decide Password";
      movepanel("multiserver");
      //サーバーのボタンが押されたときの処理
    }else if(e.getSource()==multipanel.clientButton){
      server = false;
      multiserverpanel.s="Enter Password";
      movepanel("multiserver");
      //クライアントのボタンが押されたときの処理
    }else if(e.getSource()==multiserverpanel.password){
      gamepanel.nochatbox("Server");
      movepanel("game");
      getPanel().requestFocus();
      a=1;
    }else if(e.getSource()==multiserverpanel.returnButton){
      movepanel("multi");
    }
    if(e.getSource()!=multiserverpanel.password){
      clip.flush();
      clip.setFramePosition(4500);
      clip.start();
    }
  }
}