import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.LineBorder;
import java.util.*;
import javax.sound.sampled.Clip;

@SuppressWarnings("deprecation")
class GamePanel extends JPanel implements Observer, ActionListener {
  protected Model model;
  protected Model.ReversiModel reversiModel;
  protected ReversiPanel panel;
  protected ChatPanel chatpanel;
  protected JLabel state;
  protected JButton finish, reset;
  protected JTextField chatbox;
  protected JButton chat;
  protected JScrollBar scrollbar;
  protected javax.swing.Timer timer;
  protected javax.swing.Timer animation;
  protected int aninum=0;
  protected Clip clip;

  public GamePanel(Model m,Clip clip) {
    this.clip=clip;
    timer = new javax.swing.Timer(1000, this);
    animation = new javax.swing.Timer(1,this);
    model = m;
    reversiModel = model.getReversiModel();
    reversiModel.addObserver(this);
    JPanel p1 = new JPanel();
    JPanel p2 = new JPanel();

    // Frame内の要素
    panel = new ReversiPanel();
    panel.setPreferredSize(new Dimension(600, 600));

    CountPanel blackpanel = new CountPanel(1, "Black");
    blackpanel.setBorder(new LineBorder(Color.BLACK, 2, true));

    CountPanel whitepanel = new CountPanel(2, "White");
    whitepanel.setBorder(new LineBorder(Color.BLACK, 2, true));

    scrollbar = new JScrollBar(JScrollBar.VERTICAL);

    chatpanel = new ChatPanel(model, scrollbar);
    JPanel cp = new JPanel();
    cp.setLayout(new BorderLayout());
    cp.add(chatpanel, BorderLayout.CENTER);
    cp.add(scrollbar, BorderLayout.EAST);
    cp.setBorder(new LineBorder(Color.BLACK, 2, true));
    if (reversiModel.getIsYourTurn()) {
      state = new JLabel("あなたの番です", JLabel.CENTER);
    } else {
      state = new JLabel("相手の番です", JLabel.CENTER);
    }
    state.setBorder(new LineBorder(Color.BLACK, 2, true));
    Font font = new Font(Font.SANS_SERIF, Font.BOLD, 32);
    state.setFont(font);
    state.setPreferredSize(new Dimension(210,50));

    finish = new JButton("Return Title");
    reset = new JButton("Reset");
    chat = new JButton("Chat");

    // Panelによる塊の作成
    GridBagLayout layout = new GridBagLayout();
    p1.setLayout(layout);
    GridBagConstraints gbc = new GridBagConstraints();

    gbc.fill = GridBagConstraints.BOTH;
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.insets = new Insets(20, 20, 0, 0);
    gbc.weightx = 1.0;
    gbc.weighty = 0.2;
    layout.setConstraints(blackpanel, gbc);

    gbc.gridy = 1;
    gbc.gridwidth = 1;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    layout.setConstraints(cp, gbc);

    gbc.gridx = 0;
    gbc.gridy = 3;
    gbc.insets = new Insets(0, 20, 20, 0);
    gbc.weightx = 1.0;
    gbc.weighty = 0.01;
    layout.setConstraints(chat, gbc);

    p1.add(blackpanel);
    p1.add(cp);
    p1.add(chat);

    // 以下を開放してテキストボックスを追加

    chatbox = new JTextField();
    chatbox.setEnabled(false);
    gbc.gridy = 2;
    gbc.insets = new Insets(20, 20, 20, 0);
    gbc.weightx = 1.0;
    gbc.weighty = 0.02;
    layout.setConstraints(chatbox, gbc);
    p1.add(chatbox);

    GridBagLayout layout2 = new GridBagLayout();
    p2.setLayout(layout2);

    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.gridwidth = 2;
    gbc.insets = new Insets(20, 0, 0, 20);
    gbc.weightx = 1.0;
    gbc.weighty = 0.15;
    layout2.setConstraints(whitepanel, gbc);

    gbc.gridy = 1;
    gbc.insets = new Insets(20, 0, 20, 20);
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    layout2.setConstraints(state, gbc);

    gbc.gridy = 2;
    gbc.gridwidth = 1;
    gbc.insets = new Insets(0, 0, 20, 20);
    gbc.weightx = 1.0;
    gbc.weighty = 0.01;
    layout2.setConstraints(reset, gbc);

    gbc.gridx = 1;
    gbc.gridy = 2;
    gbc.insets = new Insets(0, 0, 20, 20);
    gbc.weightx = 1.0;
    gbc.weighty = 0.01;
    layout2.setConstraints(finish, gbc);

    p2.add(whitepanel);
    p2.add(state);
    p2.add(reset);
    p2.add(finish);
    this.setLayout(new BorderLayout());

    this.add(p1, BorderLayout.WEST);
    this.add(p2, BorderLayout.EAST);

    this.add(panel, BorderLayout.CENTER);

    // pack は JFrameのサイズを自動設定するメソッド．
    // this.setSize(300,200); などの代わり
    this.setVisible(true);
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
              drawStone(g, i, j,1);//白から黒へアニメーション
            }else{
              g.fillOval(20 + 70 * i + 5, 20 + 70 * j + 5, 60, 60);
            }
          }
          if (board_array[i][j] == 2) {
            // fillwhite(g, i, j);
            g.setColor(Color.WHITE);
            if(aniarray[i][j]==4){
              drawStone(g, i, j,2);//黒から白へアニメーション
            }else{
              g.fillOval(20 + 70 * i + 5, 20 + 70 * j + 5, 60, 60);//黒から白へアニメーション
            }
          }
          if (canput[i][j] == 3 && reversiModel.getIsYourTurn()) {
            if (reversiModel.getPlayer() == 1) {
              g.setColor(new Color(0, 0, 0, 70));
            } else {
              g.setColor(new Color(255, 255, 255, 150));
            }
            g.fillOval(20 + 70 * i + 5, 20 + 70 * j + 5, 60, 60);
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
    public void drawStone(Graphics g,int i,int j,int color){
      if(a==0 && flag==0){
        if(color==1){
          g.setColor(Color.WHITE);
        }else{
          g.setColor(Color.BLACK);
        }
      }else{
        flag=1;
        if(color==1){
          g.setColor(Color.BLACK);
        }else{
          g.setColor(Color.WHITE);          
        }
      }
      g.fillOval(20+70*i+5 ,20+70*j+5+aninum/2, 60, 60-aninum);
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
      animation.start();
      clip.flush();
      clip.setFramePosition(0);
      clip.start();
    }
    panel.repaint();
    if (reversiModel.getIsYourTurn()) {
      state.setText("あなたの番です");

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
  public void actionPerformed(ActionEvent e) {
    if (e.getSource() == timer) {
      if (reversiModel.getIsYourTurn()) {
        state.setText("あなたの番です");
      } else {
        state.setText("相手の番です");

      }
      timer.stop();
    }
    if(e.getSource() == animation){
      if(a==0){
        aninum+=2;
        if(aninum==60){a=1;}
      }else{
        aninum-=2;
      }
      // System.out.println(aninum);
      panel.repaint();
      if(aninum==0){a=0;animation.stop();}      
    }
  }
}