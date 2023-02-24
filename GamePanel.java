import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.LineBorder;
import java.util.*;
import javax.sound.sampled.*;
import java.io.File;
import javax.imageio.ImageIO;

// このクラスはゲーム画面を作成するクラスである。
// 具体的にはオセロの盤面、チャット画面、石を数えているパネルなどを含むパネルである。
@SuppressWarnings("deprecation")
class GamePanel extends JPanel implements Observer, ActionListener {
  protected Model.ReversiModel reversiModel;        // モデルの内部クラス、リバーシモデルを保持
  protected ReversiPanel panel;                     // オセロの盤面のパネル
  protected ChatPanel chatpanel;                    // チャットのパネル
  protected JLabel state;                           // 現在の状態を表示するラベル
  protected JButton return_title, reset,chat,finish;// タイトルに戻るボタン、操作方法を見るボタン、チャットを有効にするボタン、フィニッシュボタン
  protected JTextField chatbox;                     // チャットに送る文字を打つテキストボックス
  protected JScrollBar scrollbar;                   // チャット内のスクロールバー
  protected javax.swing.Timer timer;                // パスの表示をするためのタイマー
  protected javax.swing.Timer animation;            // 石が回転するアニメーションで用いるタイマー
  protected int Aniswitch=0;                        // 石の回転アニメーションの前半部分を1とし、後半部分を2としている。
  protected int aninum=0;                           // アニメーションにおいての石の描画の微調整する値
  protected int aninum2=0;                          // アニメーションにおいての石の描画の微調整する値
  protected Clip clip;                              // 石が置かれたときの効果音
  protected Image imgBack;                          // 背景画像を保持
  protected JPanel cp;                              // スクロールバーも含めたチャット画面
  
  // モデルと効果音を引数としている
  public GamePanel(Model m,Clip clip) {
    this.setLayout(null);
    try {
      imgBack = ImageIO.read(new File("haikei2.jpg"));// 背景画像を読み込み
    } catch (Exception e) {
        System.out.println(e);
        System.exit(0);
    }
    this.clip=clip;                           // 効果音を保持
    timer = new javax.swing.Timer(1000, this);//タイマーを1000フレームで設定
    animation = new javax.swing.Timer(1,this);//タイマーをできるだけ速いフレームで設定
    reversiModel = m.getReversiModel();   // リバーシクラスモデルを保持
    reversiModel.addObserver(this);           // このクラスをObserberとして登録

    // Frame内の要素
    /* オセロの盤面 */
    panel = new ReversiPanel();                     // オセロの盤面を生成
    panel.setPreferredSize(new Dimension(600, 600));// このパネルの縦横サイズを設定
    panel.setBounds(240,0,600,600);                 // GamePanel内の盤面の位置を設定
    panel.setOpaque(false);                         // このパネルを透明化して描画位置以外で背景が映るようにする

    /* 黒い石をカウントするパネル */
    CountPanel blackpanel = new CountPanel(1, "Black");        // CountPanelクラスより黒い石をカウントするパネルを生成
    blackpanel.setBorder(new LineBorder(Color.BLACK, 2, true));// パネルに黒い枠を付ける
    blackpanel.setBounds(20,20,220,130);                       // このパネルのGamePanel内の位置を設定

    /* 白い石をカウントするパネル */
    CountPanel whitepanel = new CountPanel(2, "White");        // CountPanelクラスより白い石をカウントするパネルを生成
    whitepanel.setBorder(new LineBorder(Color.BLACK, 2, true));// パネルに黒い枠を付ける
    whitepanel.setBounds(840,20,220,130);                      // このパネルのGamePanel内の位置を設定

    /* チャットパネル */
    scrollbar = new JScrollBar(JScrollBar.VERTICAL);   // 垂直方向のスクロールバーを生成
    chatpanel = new ChatPanel(m, scrollbar);       // チャット画面を生成
    cp = new JPanel();                                 // 新しく一つパネルを生成
    cp.setLayout(new BorderLayout());                  // パネルのレイアウトを設定
    cp.add(chatpanel, BorderLayout.CENTER);            // チャット画面をこのパネルの真ん中に
    cp.add(scrollbar, BorderLayout.EAST);              // スクロールバーをこのパネルの右側に配置
    cp.setBorder(new LineBorder(Color.BLACK, 2, true));// このパネルの周りに黒い枠をつける
    cp.setBounds(20,170,220,320);                      // このパネルのGamePanel内の位置を設定

    /* 状況を表示するパネル */
    if (reversiModel.getIsYourTurn()) {
      state = new JLabel("<html>Your<br /><center> turn</center></html>", JLabel.CENTER);      // ゲームが始まるときに自分のターンだった時のラベルの表示文字を指定
    } else {
      state = new JLabel("<html>Opponent's<br /><center> turn</center></html>", JLabel.CENTER);// 相手の時だった時のパネルの表示文字を指定
    }
    state.setBorder(new LineBorder(Color.BLACK, 2, true));                                     // 黒い枠を付ける
    Font font = new Font(Font.SANS_SERIF, Font.BOLD, 36);                                      // フォントを作成
    state.setOpaque(true);                                                                     // パネルを透明化しないようにする
    state.setFont(font);                                                                       // フォントを設定
    state.setPreferredSize(new Dimension(210,50));                                             // このラベルのサイズを設定
    state.setBounds(840,170,220,365);                                                          // このラベルのGamePanel内の位置を設定

    /* それぞれのボタン作成 */
    return_title = new JButton("Return Title");// ボタン生成
    return_title.setBounds(940,555,110,25);    // このボタンのGamePanel内の位置を設定
    reset = new JButton("Rule");               // ボタン生成
    reset.setBounds(840,555,80,25);            // このボタンのGamePanel内の位置を設定
    chat = new JButton("Chat");                // ボタン生成
    chat.setBounds(20,555,220,25);             // このボタンのGamePanel内の位置を設定
    finish = new JButton("Finish");            // ボタン生成
    finish.setBounds(940, 555, 110, 25);       // このボタンのGamePanel内の位置を設定

    /* チャットのテキストボックス */
    chatbox = new JTextField();      // テキストボックスを生成
    chatbox.setEnabled(false);       // 初期状態ではテキストボックスは無効状態にする
    chatbox.setBounds(20,510,220,25);// テキストボックスの位置を設定

    // それぞれのオブジェクトをGamePanelに貼り付け
    this.add(blackpanel);  // 黒い石を数えるパネル
    this.add(cp);          // チャット画面
    this.add(chatbox);     // テキストボックス
    this.add(whitepanel);  // 白い石を数えるパネル
    this.add(state);       // 状況を表示するラベル
    this.add(chat);        // チャットを有効にするボタン
    this.add(reset);       // 操作方法を表示するボタン
    this.add(return_title);// タイトルに戻るボタン
    this.add(panel);       // オセロの盤面のパネル
    this.setVisible(true);
  }

  /* 背景画像を貼り付け */
  public void paintComponent(Graphics g) {
    g.drawImage(imgBack, 0, 0, 1080, 600, null);
  }

  // 一人用か二人用かでゲームパネルの内容が少し異なるのでこの関数で変更を行う
  // 引数はAiかServerかを文字で指定
  public void nochatbox(String witch_Ai_or_Server) {
    if (witch_Ai_or_Server == "Ai") {// もし、一人用ならチャットの画面に石を置いた場所の履歴が表示されるようにしたいので
      this.remove(chatbox);// chat boxの削除
      chat.setText("HISTORY");// ボタンのtextの変更
      cp.setBounds(20,170,220,365);// チャット画面を少し大きくする
    } 
    else if (witch_Ai_or_Server == "Server") {// もし二人用ならチャット画面とテキストボックスが必要なので
      if (this.getComponentCount() == 8) {// chat boxがないなら
        System.out.println("server");
        this.add(chatbox);// chat boxを追加
        cp.setBounds(20,170,220,320);// チャット画面を少し小さくする
      }
      this.remove(return_title);// タイトルに戻るボタンを除去
      this.add(finish);// 代わりに終了ボタンを追加
      chat.setText("chat");// ボタンのtextの変更
    }
  }
  
  // ReversiPanel を GamePanel の内部クラスとして実装
  // このクラスはオセロの盤面を生成するクラスである
  class ReversiPanel extends JPanel {
    // オセロの盤面を描画
    public void paintComponent(Graphics g) {
      super.paintComponent(g);
      Graphics2D g2 = (Graphics2D) g;// Graphics2Dクラスへのキャスト

      // 図形や線のアンチエイリアシングの有効化
      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

      int[][] board_array = reversiModel.getBoardArray();                        // 現在の盤面を二次元配列で、黒が1、白が2で受け取る
      int[][] canput = reversiModel.getJudgeBoardArray(reversiModel.getPlayer());// 石をおける位置が3となっている二次元配列を受け取る
      int[][] aniarray = reversiModel.getAniArray();                             // 直前に置かれた石の場所には3、アニメーションで石を動かす場所を4としている二次元配列を受け取る
      
      g.setColor(new Color(0, 180, 0));// 盤面の色を選択
      g.fillRect(20, 20, 560, 560);    // 盤面を描画
      g.setColor(Color.BLACK);         // 色を黒に設定

      for (int i = 0; i < 9; i++) {
        g.fillRect(19 + 70 * i, 19, 2, 562);// 盤面上の黒い縦線を描画
      }

      for (int i = 0; i < 9; i++) {
        g.fillRect(19, 19 + 70 * i, 562, 2);// 盤面上の黒い横線を描画
      }

      // 64の盤面すべての状態を確認して結果に応じて描画
      for (int i = 0; i < 8; i++) {
        for (int j = 0; j < 8; j++) {
          if (board_array[i][j] == 1) {                           // 黒い石が置かれている場所には
            if(aniarray[i][j]==4){                                // アニメーションをする必要がある場合
              drawAniStone(g, i, j,1);                            // 白から黒へアニメーション
            }else{
              drawStone(g, i, j, 1, 255);                         // アニメーションなしで石を描画
            }
          }
          if (board_array[i][j] == 2) {                           // 白い石が置かれている場所には
            if(aniarray[i][j]==4){                                // アニメーションをする必要がある場合
              drawAniStone(g, i, j,2);                            // 黒から白へアニメーション
            }else{
              drawStone(g, i, j, 2, 255);                         // アニメーションなしで石を描画
            }
          }
          if (canput[i][j] == 3 && reversiModel.getIsYourTurn()) {// 石が置けてかつ自分の番であれば
            if (reversiModel.getPlayer() == 1) {                  // プレイヤーの石の色が黒の場合
              drawStone(g, i, j, 1, 70);                          // 少し透明にして黒色の石を描画
            } else {                                              // プレイヤーの石が白の場合
              drawStone(g, i, j, 2,70);                           // 少し透明にして白色の石を描画
            }
          }
          if(aniarray[i][j]==3){                                  // 直前に石が置かれた場所なら
            g.setColor(new Color(255, 255, 0, 100));              // 色を設定
            g.fillRect(20 + 70 * i, 20 + 70 * j, 70, 70);         // 四角に少し透明に塗りつぶす
          }
        }
      }
      // 下のif文はマウスやカーソルで選択されている場所を四角で囲む描画をする物である
      if (reversiModel.getIsYourTurn()) {
        g.setColor(Color.WHITE);
        g.fillRect(22 + 70 * reversiModel.getPikaPika_x(), 22 + 70 * reversiModel.getPikaPika_y(), 2, 66);
        g.fillRect(22 + 70 * reversiModel.getPikaPika_x(), 22 + 70 * reversiModel.getPikaPika_y(), 66, 2);
        g.fillRect(86 + 70 * reversiModel.getPikaPika_x(), 22 + 70 * reversiModel.getPikaPika_y(), 2, 66);
        g.fillRect(22 + 70 * reversiModel.getPikaPika_x(), 86 + 70 * reversiModel.getPikaPika_y(), 66, 2);
      }
    }

    // 石を描画する関数
    // color変数が1なら黒、2なら白を指定、aは透明度、i,jは盤面の場所
    public void drawStone(Graphics g,int i,int j,int color,int a){
      if(color==1){                                                       // 黒色の石を立体に見えるように描画
        g.setColor(new Color(175,175,175,a));                             // 色を指定
        g.fillRoundRect(20 + 70 * i + 8, 20 + 70 * j + 4, 54, 62, 54, 54);// 楕円のような図形を作成
        g.setColor(new Color(80,80,80,a));                                // 色を指定
        g.fillRoundRect(20 + 70 * i + 8, 20 + 70 * j + 4, 54, 58, 54, 54);// 楕円のような図形を作成
        g.setColor(new Color(0,0,0,a));                                   // 色を指定
      }else{                                                              // 白色の石を立体に見えるように描画
        g.setColor(new Color(80,80,80,a));                                // 色を指定
        g.fillRoundRect(20 + 70 * i + 8, 20 + 70 * j + 4, 54, 62, 54, 54);// 楕円のような図形を作成
        g.setColor(new Color(175,175,175,a));                             // 色を指定
        g.fillRoundRect(20 + 70 * i + 8, 20 + 70 * j + 4, 54, 58, 54, 54);// 楕円のような図形を作成
        g.setColor(new Color(255,255,255,a));                             // 色を指定
      }                                                                   // 
      g.fillOval(20 + 70 * i + 8, 20 + 70 * j + 4, 54, 54);               // 最後に石の色にあった円形を描画
    }

    // color変数が1なら白→黒、2なら黒→白にアニメーションを行う関数である。i,jは盤面の場所
    public void drawAniStone(Graphics g,int i,int j,int color){
      // アニメーションはaninumとaninum2の数を変更していくことによって描画する図形を徐々に変更している
      if(Aniswitch==0 && flag==0){// ここでは回転のアニメーションの前半半分を行う
        if(color==1){
          g.setColor(new Color(80,80,80));
          g.fillRoundRect(20 + 70 * i + 8, 20 + 70 * j + 4+aninum/2, 54, 62-aninum, 54, 54);
          g.setColor(new Color(175,175,175));
          g.fillRoundRect(20 + 70 * i + 8, 20 + 70 * j + 4+aninum/2, 54, 58-aninum, 54, 54);
          g.setColor(new Color(255,255,255));
        }else{
          g.setColor(new Color(175,175,175));
          g.fillRoundRect(20 + 70 * i + 8, 20 + 70 * j + 4+aninum/2, 54, 62-aninum, 54, 54);
          g.setColor(new Color(80,80,80));
          g.fillRoundRect(20 + 70 * i + 8, 20 + 70 * j + 4+aninum/2, 54, 58-aninum, 54, 54);
          g.setColor(new Color(0,0,0));
        }
        g.fillOval(20 + 70 * i + 8, 20 + 70 * j + 4+aninum/2, 54, 54-aninum);
      }
      else{// こっちでは回転のアニメーションの後半半分を行う
        flag=1;
        if(color==1){
          g.setColor(new Color(175,175,175));
          g.fillRoundRect(20 + 70 * i + 8, 20 + 70 * j + 8+aninum/2-aninum2, 54, 62-aninum, 54, 54);
          g.setColor(new Color(80,80,80));
          g.fillRoundRect(20 + 70 * i + 8, 20 + 70 * j + 8+aninum/2-aninum2, 54, 58-aninum, 54, 54);
          g.setColor(new Color(0,0,0));
        }else{
          g.setColor(new Color(80,80,80));
          g.fillRoundRect(20 + 70 * i + 8, 20 + 70 * j + 8+aninum/2-aninum2, 54, 62-aninum, 54, 54);
          g.setColor(new Color(175,175,175));
          g.fillRoundRect(20 + 70 * i + 8, 20 + 70 * j + 8+aninum/2-aninum2, 54, 58-aninum, 54, 54);
          g.setColor(new Color(255,255,255));
        }
        g.fillOval(20 + 70 * i + 8, 20 + 70 * j + 12+aninum/2-2*aninum2, 54, 54-aninum);
      }
    }
  }
  
  // このクラスは黒い石が描画されたパネルを生成するクラスである。
  class BlackStone extends JPanel {
    public void paintComponent(Graphics g) {
      Graphics2D g2 = (Graphics2D) g;// Graphics2Dクラスへのキャスト

      // 図形や線のアンチエイリアシングの有効化
      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      g.setColor(Color.BLACK);             // 色を黒に設定
      g.fillOval(20 + 15, 20 + 15, 60, 60);// 円を描画
    }
  }

  // このクラスは白い石が描画されたパネルを生成するクラスである
  class WhiteStone extends JPanel {
    public void paintComponent(Graphics g) {
      Graphics2D g2 = (Graphics2D) g;// Graphics2Dクラスへのキャスト

      // 図形や線のアンチエイリアシングの有効化
      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      g.setColor(Color.WHITE);             // 色を白に設定
      g.fillOval(20 + 15, 20 + 15, 60, 60);// 円を描画
    }
  }

  // このクラスは石の数を数えるパネルを生成するクラスである
  class CountPanel extends JPanel implements Observer {
    private int player;// プレイヤーが白か黒かを格納
    JLabel count;      // 石の数が表示されるラベル
    JPanel stone;      // 石の絵が描画されたパネル

    CountPanel(int n, String s) {
      player = n;                                          // プレイヤーの石の色を保持
      this.setLayout(new GridLayout(1, 2));                // レイアウトを設定
      Font font = new Font(Font.SANS_SERIF, Font.BOLD, 64);// フォントを作成
      if (n == 1) {                                        // 黒色のパネルの場合
        stone = new BlackStone();                          // 黒色の石の絵のパネルを生成
      } else {
        stone = new WhiteStone();                          // 白色の石の絵のパネルを生成
      }
      count = new JLabel("2", JLabel.CENTER);              // ラベルの文字を2として初期化
      stone.setPreferredSize(new Dimension(100, 40));      // ラベルのサイズを設定
      count.setFont(font);                                 // フォントを設定
      reversiModel.addObserver(this);                      // reversiModelクラスにこのパネルをObserverとして登録
      this.add(stone);                                     // パネルを貼り付け
      this.add(count);                                     // ラベルを貼り付け
    }

    // この関数は石の数が変更されると呼び出される
    public void update(Observable o, Object arg) {                 // reversiModelクラスから呼び出されると
      String s = Integer.toString(reversiModel.countStorn(player));// playerに対応する石の数をString型としてセット
      count.setText(s);                                            // countラベルの文字を今取ってきた数字に変更
    }
  }
  private int flag;
  // この関数は画面の描画を変更したいときに呼び出される
  public void update(Observable o, Object arg) {// 描画の変更をreversiModelから要求されたら
    if(arg==(Object)1){ 
      flag=0;                        // 新たに石が置かれたときに描画を変更する場合は以下を行う
      Aniswitch=0;                              // Aniswitchを0に初期化
      aninum2=0;                                // aninum2を0に初期化
      animation.start();                        // 石をひっくる返すアニメーションを開始
      clip.flush();                             // 音声再生の内部バッファを削除
      clip.setFramePosition(4500);              // 再生位置を音がなる直前に戻す
      clip.start();                             // 石を置く効果音を出す
    }
    panel.repaint();                                        // 再描画
    if (reversiModel.getIsYourTurn()) {                     // 自分のターンかどうかでラベルの文字を変更
      state.setText("<html>Your<br /><center>turn</center></html>");
    } else {
      state.setText("<html>Opponent's<br /><center> turn</center></html>");
    }
    if (reversiModel.getFinishFlag() == 1) {                // もしこのときゲームが終了しているのなら
      if((reversiModel.countStorn(reversiModel.getPlayer()))==(reversiModel.countStorn(reversiModel.getOpponentStone(reversiModel.getPlayer())))){
        state.setText("<html><center>draw</center></html>");// 引き分けの時に表示する文字
      }
      // 「最後に置いた色の方が多く、かつ、それが自分」もしくは、「最後に置いた色の方が少なく、かつ、それが相手」=>win
      else if ((reversiModel.countStorn(reversiModel.getPlayer()) > reversiModel.countStorn(reversiModel.getOpponentStone(reversiModel.getPlayer())))&&reversiModel.getIsYourTurn()
      || (reversiModel.countStorn(reversiModel.getPlayer()) < reversiModel.countStorn(reversiModel.getOpponentStone(reversiModel.getPlayer()))) && !reversiModel.getIsYourTurn()
      ) {
        state.setText("<html>You<br /><center>win!!</center></html>");
      } else {
        state.setText("<html>Opponent<br /><center>win!!</center></html>");
      }
    }
    if (reversiModel.getPassFlag() == 1) {// もしこの時パスであれば
      state.setText("パス");                // パスをラベルに表示
      timer.start();                      // パス表示を一瞬だけ表示してどちらの番かを表示したいのでタイマーをスタートする
    }
  }

  public void actionPerformed(ActionEvent e) {
    if (e.getSource() == timer) {        // タイマーがスタートされたら少しだけ時間がたった後に
      if (reversiModel.getIsYourTurn()) {// どちらかの番が表示される
        state.setText("<html>Your<br /><center>turn</center></html>");
      } else {
        state.setText("<html>Opponent's<br /><center> turn</center></html>");
      }
      timer.stop();                      // タイマーをストップする
    }
    if(e.getSource() == animation){      // animationのタイマーがスタートされたら
      if(Aniswitch==0){                  // まず初めに前半部分の描画の時
        aninum+=2;                       // aninumの値を2ずつ増やしていく
        if(aninum==54){Aniswitch=1;}     // aninumが54になったらアニメーションの後半部分をスタートさせる
      }else{
        aninum-=2;                       // aninumの値を2ずつ減らしていく
        aninum2=4-(int)(aninum/14);      // 他の石と同じ座標になるように微調整
      }
      panel.repaint();                   // 再描画
      if(aninum==0){animation.stop();    // 再度aninumが0になったらアニメーションを停止させる。
      }      
    }
  }
}