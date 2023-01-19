import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.LineBorder;
import java.util.*;
import javax.sound.sampled.Clip;
import java.io.File;
import javax.imageio.ImageIO;

@SuppressWarnings("deprecation")
class GamePanel extends JPanel implements Observer, ActionListener {
  protected Model model;
  protected Model.ReversiModel reversiModel;
  protected ReversiPanel panel;
  protected ChatPanel chatpanel;
  protected JLabel state;
  protected JButton finish, reset,chat;
  protected JTextField chatbox;
  protected JScrollBar scrollbar;
  protected javax.swing.Timer timer;
  protected javax.swing.Timer animation;
  protected int aninum=0;
  protected Clip clip;
  protected Image imgBack;
  protected JPanel cp;
  
  public GamePanel(Model m,Clip clip) {
    this.setLayout(null);
    try {
      imgBack = ImageIO.read(new File("haikei.jpg"));
    } catch (Exception e) {
        System.out.println(e);
        System.exit(0);
    }
    this.clip=clip;
    timer = new javax.swing.Timer(1000, this);
    animation = new javax.swing.Timer(1,this);
    animation2 = new  javax.swing.Timer(1,this);
    model = m;
    reversiModel = model.getReversiModel();
    reversiModel.addObserver(this);

    // Frame内の要素
    panel = new ReversiPanel();
    panel.setPreferredSize(new Dimension(600, 600));
    panel.setBounds(240,0,600,600);

    CountPanel blackpanel = new CountPanel(1, "Black");
    blackpanel.setBorder(new LineBorder(Color.BLACK, 2, true));
    blackpanel.setBounds(20,20,220,130);

    CountPanel whitepanel = new CountPanel(2, "White");
    whitepanel.setBorder(new LineBorder(Color.BLACK, 2, true));
    whitepanel.setBounds(840,20,220,130);

    scrollbar = new JScrollBar(JScrollBar.VERTICAL);

    chatpanel = new ChatPanel(model, scrollbar);
    cp = new JPanel();
    cp.setLayout(new BorderLayout());
    cp.add(chatpanel, BorderLayout.CENTER);
    cp.add(scrollbar, BorderLayout.EAST);
    cp.setBorder(new LineBorder(Color.BLACK, 2, true));
    cp.setBounds(20,170,220,320);
    if (reversiModel.getIsYourTurn()) {
      state = new JLabel("あなたの番です", JLabel.CENTER);
    } else {
      state = new JLabel("相手の番です", JLabel.CENTER);
    }
    state.setBorder(new LineBorder(Color.BLACK, 2, true));
    Font font = new Font(Font.SANS_SERIF, Font.BOLD, 32);
    state.setFont(font);
    state.setPreferredSize(new Dimension(210,50));
    state.setBounds(840,170,220,365);

    finish = new JButton("Return Title");
    finish.setBounds(940,555,110,25);
    reset = new JButton("Reset");
    reset.setBounds(840,555,80,25);
    chat = new JButton("Chat");
    chat.setBounds(20,555,220,25);

    this.add(blackpanel);
    this.add(cp);
    // this.add(chat);

    // 以下を開放してテキストボックスを追加

    chatbox = new JTextField();
    chatbox.setEnabled(false);
    chatbox.setBounds(20,510,220,25);
    this.add(chatbox);

    this.add(whitepanel);
    this.add(state);
    this.add(chat);
    this.add(reset);
    this.add(finish);
    this.add(panel);
    this.setVisible(true);
  }
  // public void paintComponent(Graphics g) {
  //   g.drawImage(imgBack, 0, 0, 1080, 600, null);
  // }

  public void nochatbox(String witch_Ai_or_Server) {
    if (witch_Ai_or_Server == "Ai") {
      this.remove(chatbox);// chat boxの削除
      chat.setText("HISTORY");// ボタンのtextの変更
      cp.setBounds(20,170,220,365);
      this.add(cp);
    } else if (witch_Ai_or_Server == "Server") {
      if (this.getComponentCount() == 8) {// chat boxがないなら
        chatbox.setBounds(20,510,220,25);
        this.add(chatbox);// chat box
        cp.setBounds(20,170,220,320);
        this.add(cp);
      }
      chat.setText("chat");// ボタンのtextの変更
    }
  }
  
  // ReversiPanel を GamePanel の内部クラスとして実装
  class ReversiPanel extends JPanel {
    public void paintComponent(Graphics g) {
      super.paintComponent(g);
      Graphics2D g2 = (Graphics2D) g;

      // 図形や線のアンチエイリアシングの有効化
      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

      int[][] board_array = reversiModel.getBoardArray();
      int[][] canput = reversiModel.getJudgeBoardArray(reversiModel.getPlayer());// modelのおけるか配列
      int[][] aniarray = reversiModel.getAniArray();
      g.setColor(new Color(0, 180, 0));
      g.fillRect(20, 20, 560, 560);
      g.setColor(Color.BLACK);
      for (int i = 0; i < 9; i++) {
        g.fillRect(19 + 70 * i, 19, 2, 562);
      }
      for (int i = 0; i < 9; i++) {
        g.fillRect(19, 19 + 70 * i, 562, 2);
      }
      for (int i = 0; i < 8; i++) {
        for (int j = 0; j < 8; j++) {
          if (board_array[i][j] == 1) {
            // fillblack(g, i, j);
            g.setColor(Color.BLACK);
            if(aniarray[i][j]==4){
              drawAniStone(g, i, j,1);//白から黒へアニメーション
            }else{
              drawStone(g, i, j, 1, 255);
            }
          }
          if (board_array[i][j] == 2) {
            // fillwhite(g, i, j);
            g.setColor(Color.WHITE);
            if(aniarray[i][j]==4){
              drawAniStone(g, i, j,2);//黒から白へアニメーション
            }else{
              drawStone(g, i, j, 2, 255);
            }
          }
          if (canput[i][j] == 3 
          // && reversiModel.getIsYourTurn()
          ) {
            if (reversiModel.getPlayer() == 1) {
              g.setColor(new Color(0, 0, 0, 70));
              drawStone(g, i, j, 1, 70);
            } else {
              g.setColor(new Color(255, 255, 255, 150));
              drawStone(g, i, j, 2,70);
            }
          }
          if(aniarray[i][j]==3){
            g.setColor(new Color(255, 255, 0, 100));
            // 下の一行は実際に動かすときに使う関数
            g.fillRect(20 + 70 * i, 20 + 70 * j, 70, 70);
          }
        }
      }
      if (reversiModel.getIsYourTurn()) {
        g.setColor(Color.WHITE);
        // 下の一行は実際に動かすときに使う関数
        g.fillRect(22 + 70 * reversiModel.getPikaPika_x(), 22 + 70 * reversiModel.getPikaPika_y(), 2, 66);
        g.fillRect(22 + 70 * reversiModel.getPikaPika_x(), 22 + 70 * reversiModel.getPikaPika_y(), 66, 2);
        g.fillRect(86 + 70 * reversiModel.getPikaPika_x(), 22 + 70 * reversiModel.getPikaPika_y(), 2, 66);
        g.fillRect(22 + 70 * reversiModel.getPikaPika_x(), 86 + 70 * reversiModel.getPikaPika_y(), 66, 2);
      }
      // 下の一行は確認のために一マス特定の場所を光らせたもの。
      // g.fillRect(20+66*3,20+70*2,70,70);
    }
    protected int delay=10;
    public void drawStone(Graphics g,int i,int j,int color,int a){
      if(color==1){
        g.setColor(new Color(175,175,175,a));
        g.fillRoundRect(20 + 70 * i + 8, 20 + 70 * j + 4, 54, 62, 54, 54);
        g.setColor(new Color(80,80,80,a));
        g.fillRoundRect(20 + 70 * i + 8, 20 + 70 * j + 4, 54, 58, 54, 54);
        g.setColor(new Color(0,0,0,a));
      }else{
        g.setColor(new Color(80,80,80,a));
        g.fillRoundRect(20 + 70 * i + 8, 20 + 70 * j + 4, 54, 62, 54, 54);
        g.setColor(new Color(175,175,175,a));
        g.fillRoundRect(20 + 70 * i + 8, 20 + 70 * j + 4, 54, 58, 54, 54);
        g.setColor(new Color(255,255,255,a));
      }
      g.fillOval(20 + 70 * i + 8, 20 + 70 * j + 4, 54, 54);
    }
    //1白→黒2黒→白
    public void drawAniStone(Graphics g,int i,int j,int color){
      if(a==0 && flag==0){
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
      }else{
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

  class CountPanel extends JPanel implements Observer {
    private int player;
    JLabel count;
    JPanel stone;

    CountPanel(int n, String s) {
      player = n;
      this.setLayout(new GridLayout(1, 2));
      Font font = new Font(Font.SANS_SERIF, Font.BOLD, 64);
      if (n == 1) {
        stone = new BlackStone();
      } else {
        stone = new WhiteStone();
      }
      count = new JLabel("2", JLabel.CENTER);
      stone.setPreferredSize(new Dimension(100, 40));// ラベルのサイズを設定
      count.setFont(font);
      reversiModel.addObserver(this);
      this.add(stone);
      this.add(count);
    }

    public void update(Observable o, Object arg) {
      String s = Integer.toString(reversiModel.countStorn(player));
      count.setText(s);
    }
  }

  class BlackStone extends JPanel {
    public void paintComponent(Graphics g) {
      Graphics2D g2 = (Graphics2D) g;

      // 図形や線のアンチエイリアシングの有効化
      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      // fillblack(g, 0, 0);
      g.setColor(Color.BLACK);
      g.fillOval(20 + 15, 20 + 15, 60, 60);
    }
  }

  class WhiteStone extends JPanel {
    public void paintComponent(Graphics g) {
      Graphics2D g2 = (Graphics2D) g;

      // 図形や線のアンチエイリアシングの有効化
      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      // fillwhite(g, 0, 0);
      g.setColor(Color.WHITE);
      g.fillOval(20 + 15, 20 + 15, 60, 60);
    }
  }

  public void fillwhite(Graphics g, int i, int j) {
    for (int k = 0; k <= 20; k++) {
      g.setColor(new Color(k * 3 + 195, k * 3 + 195, k * 3 + 195)); // グラデーション
      g.fillOval(20 + 70 * i + 5 + (int) (k * 1.5), 20 + 70 * j + 5 + (int) (k * 1.5), 60 - k * 3, 60 - k * 3);
    }
  }

  public void fillblack(Graphics g, int i, int j) {
    for (int k = 0; k <= 20; k++) {
      g.setColor(new Color(k * 3, k * 3, k * 3)); // グラデーション
      g.fillOval(20 + 70 * i + 5 + (int) (k * 1.5), 20 + 70 * j + 5 + (int) (k * 1.5), 60 - k * 3, 60 - k * 3);
    }
  }
  private int flag;
  public void update(Observable o, Object arg) {
    if(arg==(Object)1){
      flag=0;
      aninum2=0;
      animation.start();
      clip.flush();
      clip.setFramePosition(4500);
      clip.start();
    }
    panel.repaint();
    if (reversiModel.getIsYourTurn()) {
      state.setText("<html>あなたの番<br /><center> です</center></html>");

    } else {
      state.setText("相手の番です");

    }
    if (reversiModel.getFinishFlag() == 1) {
      if (reversiModel.countStorn(1) > reversiModel.countStorn(2)) {
        state.setText("黒の勝利");
      } else {
        state.setText("白の勝利");
      }
    }
    if (reversiModel.getPassFlag("view") == 1) {
      state.setText("パス");
      timer.start();
    }
  }
  protected int a=0;
  protected int aninum2=0;
  protected javax.swing.Timer animation2;
  public void actionPerformed(ActionEvent e) {
    if (e.getSource() == timer) {
      if (reversiModel.getIsYourTurn()) {
        state.setText("<html>あなたの番<br /><center> です</center></html>");
      } else {
        state.setText("相手の番です");

      }
      timer.stop();
    }
    if (e.getSource() == animation2) {
      aninum2+=1;
      panel.repaint();
      if(aninum2==4){animation2.stop();}
    }
    if(e.getSource() == animation){
      if(a==0){
        aninum+=2;
        if(aninum==54){a=1;}
      }else{
        aninum-=2;
        aninum2=4-(int)(aninum/14);
      }
      // System.out.println(aninum);
      panel.repaint();
      if(aninum==0){a=0;animation.stop();
        // animation2.start();
      }      
    }
  }
}