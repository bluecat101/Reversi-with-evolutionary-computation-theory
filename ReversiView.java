import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import javax.sound.sampled.*;

//このゲームの全ての画面、効果音などを管理する関数である
class ReversiView extends JFrame implements ActionListener{
  protected GamePanel gamepanel;              // ゲーム画面のパネル
  protected SettingPanel settingpanel;        // 操作説明画面のパネル
  protected TitlePanel titlepanel;            // タイトル画面のパネル
  protected JPanel cardPanel;                 // 画面遷移用パネル
  protected CardLayout layout;                // レイアウト情報保持
  protected SinglePanel singlepanel;          // 一人モードが選択されたときの画面のパネル
  protected ModePanel modepanel;              // 一人か二人かを選択する画面のパネル
  protected MultiPanel multipanel;            // 部屋を立てるか部屋に入るかを選択するパネル
  protected int ai_level=1;                   // 現在選択されているaiのレベルを格納する変数、初期値として1をセット
  protected boolean first=true;               // 現在選択されている先攻後攻を格納する変数、初期値として先攻を意味するtrueをセット
  protected Model model;                      // モデルを保持
  protected boolean server;                   // 二人用の時部屋を立てた方か部屋に入る方かを格納する変数、部屋を立てる方がtrue
  protected MultiServerPanel multiserverpanel;// 合言葉を入力する画面のパネル
  protected Clip clip;                        // 石が置かれる音を保存
  protected Clip bgm;                         // bgmを保存
 
  public ReversiView(Model m,String st) {
    this.setTitle("Reversi Panel");        // タイトル名
    model=m;                               // モデルを保持
    clip = createClip(new File("set.wav"));// 石が置かれる音をClip型の変数に入れる。
    bgm = createClip(new File("Noct.wav"));// ゲーム内のbgmをClip型の変数に入れる。
    bgm.loop(Clip.LOOP_CONTINUOUSLY);      // bgmは最後まで再生し終わっても初めから繰り返し再生できるようにする。
    bgm.start();                           // すぐにbgmをスタートする

    /*以下必要な画面を生成して、動かしたいボタンに対してaddActionListenerを行う */
    /* タイトル画面 */
    titlepanel = new TitlePanel();
    titlepanel.start.addActionListener(this);
    titlepanel.setting.addActionListener(this);
    titlepanel.finish.addActionListener(this);

    /* ゲーム画面 */
    gamepanel = new GamePanel(m,clip);
    gamepanel.return_title.addActionListener(this);
    gamepanel.finish.addActionListener(this);
    gamepanel.reset.addActionListener(this);

    /* 操作方法画面 */
    settingpanel = new SettingPanel();
    settingpanel.returnButton.addActionListener(this);

    /* シングルかマルチかを選択する画面 */
    modepanel = new ModePanel();
    modepanel.returnButton.addActionListener(this);
    modepanel.single.addActionListener(this);
    modepanel.multi.addActionListener(this);

    /* シングルが選択された場合の画面 */
    singlepanel = new SinglePanel();
    singlepanel.returnButton.addActionListener(this);
    singlepanel.startButton.addActionListener(this);
    singlepanel.cb.addItemListener(new SelectItemListener());   // コンボボックスにアイテムリスナを登録
    singlepanel.first.addItemListener(new SelectItemListener());// コンボボックスにアイテムリスナを登録

    /* マルチが選択された場合の部屋を作成するか、部屋に入るかを選択する画面 */
    multipanel = new MultiPanel();
    multipanel.returnButton.addActionListener(this);
    multipanel.serverButton.addActionListener(this);
    multipanel.clientButton.addActionListener(this);

    /* マルチが選択された場合の部屋を作成するか、部屋に入るかを選択した後の画面 */
    multiserverpanel = new MultiServerPanel();
    multiserverpanel.password.addActionListener(this);
    multiserverpanel.returnButton.addActionListener(this);

    cardPanel = new JPanel();   // 画面遷移を行うパネルを生成
    layout = new CardLayout();  // カードレイアウトを生成
    cardPanel.setLayout(layout);// cardPanelのレイアウトを変更

    // cardPanelにそれぞれの画面を追加
    cardPanel.add(titlepanel, "title");
    cardPanel.add(settingpanel, "setting");
    cardPanel.add(gamepanel, "game");
    cardPanel.add(singlepanel,"single");
    cardPanel.add(modepanel,"mode");
    cardPanel.add(multipanel,"multi");
    cardPanel.add(multiserverpanel,"multiserver");

    this.add(cardPanel, BorderLayout.CENTER);// cardPanelをこのパネルに貼り付ける
    this.setSize(1090,635);                  // 画面全体のサイズを設定
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.setVisible(true);
  }

  // コンボボックスで発生したアイテムイベントを受け取るリスナ
  class SelectItemListener implements ItemListener {
    public void itemStateChanged(final ItemEvent ie) {
      if(ie.getSource()==singlepanel.cb){                    // siglepanelのレベル選択に関するコンボボックスが操作されたら
        final int imgidx = singlepanel.cb.getSelectedIndex();// 現在選択されている項目を番号で返す
        if (imgidx == 0) {                                   // プルダウンメニューの一番上が選択されているなら
          ai_level=1;                                        // ai_levelに1をセット
        } else if (imgidx == 1) {                            // 真ん中が選択されているとき
          ai_level=2;                                        // ai_levelに2をセット
        } else if (imgidx == 2) {                            // 一番下が選択されているとき
          ai_level=3;                                        // ai_levelに3をセット
        }
      }
      if(ie.getSource()==singlepanel.first){                    // singlepanelの先攻後攻を選択するコンボボックスが操作されたら
        final int imgidx = singlepanel.first.getSelectedIndex();// 現在選択されている項目を番号で返す
        if(imgidx==0){                                          // 上の項目なら
          first = true;                                         // firstをtrueに
        }else if(imgidx==1){                                    // 下の項目なら
          first = false;                                        // firstをfalseにする
        }
      }
    }
  }

  /* 以下の関数はModel及びControllerで使われる関数である */
  // 効果音のclipを返す関数
  public Clip getClip(){
    return clip;
  }
  
  // ゲーム画面の盤面のパネルを返す関数
  public JPanel getPanel(){
    return gamepanel.panel;
  }

  // ゲーム画面のチャットのパネルを返す関数
  public JPanel getChatPanel(){
    return gamepanel.chatpanel;
  }

  // ゲーム画面から操作説明画面へ遷移するボタンを返す関数
  public JButton getResetButton(){
    return gamepanel.reset;
  }

  // ゲーム画面からタイトルに戻るボタンを返す関数
  public JButton getReturn_titleButton(){
    return gamepanel.return_title;
  }

  // 二人用の時にゲームを終了するボタンを返す関数
  public JButton getfinishButton() {
    return gamepanel.finish;
  }

  // 操作説明画面からタイトルに戻るボタンを返す関数
  public JButton getButton(){
    return settingpanel.returnButton;
  }

  // ゲーム画面の文字を打ち込むテキストボックスを返す関数
  public JTextField getChatBox(){
    return gamepanel.chatbox;
  }

  // ゲーム画面のテキストボックスを有効にするボタンを返す関数
  public JButton getChatButton(){
    return gamepanel.chat;
  }

  // 一人プレイを開始するときのボタン
  public JButton getSingleStartButton(){
    return singlepanel.startButton;
  }

  // 合言葉を入力するテキストボックスを返す関数
  public JTextField getMultiPasswordBox(){
    return multiserverpanel.password;
  }

  // 先攻ならtrue、後攻ならfalseを返す関数
  public boolean getFirst(){
    return first;
  }

  // int型でaiのレベルを返す
  public int getAiLevel(){
    return ai_level;
  }

  // サーバーかどうかの変数。trueならサーバー
  public boolean getSVMode(){
    return server;
  }

  // String型の文字sで指定した画面に遷移する関数
  public void movepanel(String s){
    layout.show(cardPanel,s);
  }
  
  //※ 音声ファイルからClipオブジェクトを作成する関数
  public static Clip createClip(File path) {
		// 指定されたURLのオーディオ入力ストリームを取得
		try (AudioInputStream ais = AudioSystem.getAudioInputStream(path)){
			
			// ファイルの形式取得
			AudioFormat af = ais.getFormat();
			
			// 単一のオーディオ形式を含む指定した情報からデータラインの情報オブジェクトを構築
			DataLine.Info dataLine = new DataLine.Info(Clip.class,af);
			
			// 指定された Line.Info オブジェクトの記述に一致するラインを取得
			Clip c = (Clip)AudioSystem.getLine(dataLine);
			
			// 再生可能状態にする
			c.open(ais);
			
			return c;
		}
    // 以下エラー対処 
    catch (MalformedURLException e) {
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

  protected int ruleflag;  // 操作説明画面に遷移する際にタイトル画面かゲーム画面かどちらに戻るかを分かるようにする変数
  public void actionPerformed(ActionEvent e){
    if(e.getSource()==titlepanel.start){
      movepanel("mode");   // タイトルからモード選択画面
    }else if(e.getSource()==titlepanel.setting){
      ruleflag=0;          // ruleflagを0にする
      movepanel("setting");// タイトルから操作説明画面
    }else if(e.getSource()==titlepanel.finish|| e.getSource()==getfinishButton()){
      System.exit(0);      // タイトル画面かゲーム画面の終了ボタンが押されたら実行終了
    }else if(e.getSource()==gamepanel.reset){
      ruleflag=1;          // ruleflagを1にする
      movepanel("setting");// ゲーム画面から操作説明画面へ
    }else if(e.getSource()==settingpanel.returnButton){
      if(ruleflag==0){     // もしタイトル画面から操作説明画面に遷移したなら
        movepanel("title");// タイトル画面に戻る
      }else{               // ゲーム画面からなら
        movepanel("game"); // ゲーム画面に戻る
      }
    }else if(e.getSource()==getReturn_titleButton()){  // ゲーム画面のタイトルに戻るボタンを押したら
      model.getReversiModel().initBoard();             // オセロの盤面をリセット
      model.getChatModel().initChat();                 // チャットの内容をリセット
      movepanel("title");                              // タイトル画面に戻る
    }else if(e.getSource()==singlepanel.returnButton){
      movepanel("mode");                               // 一人用画面からモード選択画面へ
    }else if(e.getSource()==singlepanel.startButton){  // 一人用でゲームが開始されたら
      gamepanel.nochatbox("Ai");                       // ゲーム画面を一人用のレイアウトに変更
      movepanel("game");                               // ゲーム画面に遷移
      getPanel().requestFocus();                       // キー操作が盤面に対して反応するようにする
    }else if(e.getSource()==modepanel.returnButton){
      movepanel("title");                              // モード選択画面からタイトル画面へ
    }else if(e.getSource()==modepanel.single){
      movepanel("single");                             // モード選択画面から一人用画面へ
    }else if(e.getSource()==modepanel.multi){
      movepanel("multi");                              // モード選択画面から二人用画面へ
    }else if(e.getSource()==multipanel.returnButton){
      movepanel("mode");                               // 部屋を立てるかどうかの画面から二人用画面へ
    }else if(e.getSource()==multipanel.serverButton){  // 部屋を立てるボタンが押されたら
      server = true;                                   // 部屋を立てる方なのでtrueをセット
      multiserverpanel.s="Decide Password";            // 画面に表示する文字を変更
      movepanel("multiserver");                        // 合言葉を打つ画面へ
    }else if(e.getSource()==multipanel.clientButton){  // 部屋に入るボタンが押されたら
      server = false;                                  // 部屋に入る方なのでfalseをセット
      multiserverpanel.s="Enter Password";             // 画面に表示する文字を変更
      movepanel("multiserver");                        // 合言葉を打つ画面へ
    }else if(e.getSource()==multiserverpanel.password){// 合言葉を打ってエンターを押したら
      gamepanel.nochatbox("Server");                   // 二人用画面にゲーム画面をレイアウト変更
      movepanel("game");                               // ゲーム画面に遷移
      getPanel().requestFocus();                       // キー操作が盤面に対して反応するようにする
    }else if(e.getSource()==multiserverpanel.returnButton){
      movepanel("multi");                              // 合言葉画面から二人用画面へ
    }
    if(e.getSource()!=multiserverpanel.password){// 全てのボタンに関して押されたら
      clip.flush();                              // 音声再生の内部バッファを削除
      clip.setFramePosition(4500);               // 再生位置を音がなる直前に戻す
      clip.start();                              // 石が置かれる音を鳴らす
    }
  }
}