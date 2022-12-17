import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.LineBorder;
import java.util.*;

@SuppressWarnings("deprecation")
class GamePanel extends JPanel implements Observer {
  protected Model model;
  protected Model.ReversiModel reversiModel;
  protected ReversiPanel panel;
  protected ChatPanel chatpanel;
  protected JLabel state;
  protected JButton finish,reset;
  protected JTextField chatbox;
 
  public GamePanel(Model m) {
    model = m;
    reversiModel = model.getReversiModel();
    reversiModel.addObserver(this);
    JPanel  p1=new JPanel();
    JPanel  p2=new JPanel();

    //Frame内の要素
    panel=new ReversiPanel();
    panel.setPreferredSize(new Dimension(600, 600));

    CountPanel blackpanel = new CountPanel(1,"Black");
    blackpanel.setBorder(new LineBorder(Color.BLACK,2,true));

    CountPanel whitepanel = new CountPanel(2,"White");
    whitepanel.setBorder(new LineBorder(Color.BLACK,2,true));

    chatpanel = new ChatPanel();
    chatpanel.setBorder(new LineBorder(Color.BLACK,2,true));

    state = new JLabel("黒の手番です",JLabel.CENTER);
    state.setBorder(new LineBorder(Color.BLACK,2,true));
    Font font = new Font(Font.SANS_SERIF,Font.BOLD,32);
    state.setFont(font);

    finish = new JButton("Finish");
    reset = new JButton("Reset");

    
    //Panelによる塊の作成
    GridBagLayout layout = new GridBagLayout();
    p1.setLayout(layout);
    GridBagConstraints gbc = new GridBagConstraints();
    
    gbc.fill = GridBagConstraints.BOTH;
    gbc.gridx=0;
    gbc.gridy=0;
    gbc.insets = new Insets(20, 20, 0, 0);
    gbc.weightx = 1.0;
    gbc.weighty = 0.2;
    layout.setConstraints(blackpanel,gbc);

    gbc.gridy=1;
    gbc.insets = new Insets(20, 20, 0, 0);
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    layout.setConstraints(chatpanel, gbc);
    
    p1.add(blackpanel);p1.add(chatpanel);

    //以下を開放してテキストボックスを追加

    // chatbox = new JTextField();
    // gbc.gridy=2;
    // gbc.insets = new Insets(20, 20, 20, 0);
    // gbc.weightx = 1.0;
    // gbc.weighty = 0.02;
    // layout.setConstraints(chatbox, gbc);
    // p1.add(chatbox);

    GridBagLayout layout2 = new GridBagLayout();
    p2.setLayout(layout2);

    gbc.gridx=0;
    gbc.gridy=0;
    gbc.gridwidth = 2;
    gbc.insets = new Insets(20, 0, 0, 20);
    gbc.weightx = 1.0;
    gbc.weighty = 0.2;
    layout2.setConstraints(whitepanel,gbc);

    gbc.gridy=1;
    gbc.insets = new Insets(20, 0, 20, 20);
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    layout2.setConstraints(state, gbc);

    gbc.gridy=2;
    gbc.gridwidth = 1;
    gbc.insets = new Insets(0, 0, 20, 20);
    gbc.weightx = 1.0;
    gbc.weighty = 0.01;
    layout2.setConstraints(reset, gbc);

    gbc.gridx=1;
    gbc.gridy=2;
    gbc.insets = new Insets(0, 0, 20, 20);
    gbc.weightx = 1.0;
    gbc.weighty = 0.01;
    layout2.setConstraints(finish, gbc);

    p2.add(whitepanel);p2.add(state);p2.add(reset);p2.add(finish);
    this.setLayout(new BorderLayout());

    this.add(p1,BorderLayout.WEST);
    this.add(p2,BorderLayout.EAST);

    this.add(panel,BorderLayout.CENTER);
 
    // pack は JFrameのサイズを自動設定するメソッド．
    // this.setSize(300,200); などの代わり
    this.setVisible(true);
  }

  // ReversiPanel を GamePanel の内部クラスとして実装
class ReversiPanel extends JPanel {
    public void paintComponent(Graphics g) {
      super.paintComponent(g);
      int[][] board_array = reversiModel.getBoardArray();
      int[][] canput = reversiModel.getJudgeBoardArray(reversiModel.getPlayer());//modelのおけるか配列
      g.setColor(new Color(0,180,0));
      g.fillRect(20,20,560,560);
      g.setColor(Color.BLACK);
      for(int i=0;i<9;i++){
        g.fillRect(19+70*i,19,2,562);
      }
      for(int i=0;i<9;i++){
        g.fillRect(19,19+70*i,562,2);
      }
      for(int i=0;i<8;i++){
        for(int j=0;j<8;j++){
          if(board_array[i][j]==1){
            drawblack(g, i, j);
          }
          if(board_array[i][j]==2){
            drawwhite(g, i, j);
          }
          if(canput[i][j]==3){
            if(reversiModel.getPlayer()==1){
              g.setColor(new Color(0,0,0,70));
            }else{
              g.setColor(new Color(255,255,255,150));
            }
            g.fillOval(20+70*i+5,20+70*j+5,60,60);
          }
        }
      }
      g.setColor(new Color(255,255,0,100));

      //下の一行は実際に動かすときに使う関数
      g.fillRect(20+70*reversiModel.getPikaPika_x(), 20+70*reversiModel.getPikaPika_y(), 70, 70);

      //下の一行は確認のために一マス特定の場所を光らせたもの。
      //g.fillRect(20+70*3,20+70*2,70,70);
    }
  }

  class ChatPanel extends JPanel {
    public void paintComponent(Graphics g){
      super.paintComponent(g);
      g.setColor(new Color(0,180,0));
      g.fillRect(0, 0, 100, 100);
    }
  }

  class CountPanel extends JPanel implements Observer{
    private int player;
    JLabel count;
    JPanel stone;

    CountPanel(int n,String s){
      player = n;
      this.setLayout(new GridLayout(1,2));
      Font font = new Font(Font.SANS_SERIF,Font.BOLD,64);
      if(n==1){
        stone = new BlackStone();
      }else{
        stone = new WhiteStone();
      }
      count =new JLabel("2",JLabel.CENTER);
      stone.setPreferredSize(new Dimension(100, 40));//ラベルのサイズを設定
      count.setFont(font);
      reversiModel.addObserver(this);
      this.add(stone);
      this.add(count);
    }
    public void update(Observable o,Object arg){
      String s = Integer.toString(reversiModel.countStorn(player));
      count.setText(s);
    }
  }

  class BlackStone extends JPanel {
    public void paintComponent(Graphics g){
      drawblack(g, 0, 0);
    }
  }

  class WhiteStone extends JPanel {
    public void paintComponent(Graphics g){
      drawwhite(g, 0, 0);
    }
  }

  public void drawwhite(Graphics g,int i,int j){
    for (int k=0;k<=20;k++){
      g.setColor(new Color(k*3+195, k*3+195, k*3+195)); //グラデーション
      g.fillOval(20+70*i+5 +(int)(k*1.5) ,20+70*j+5+(int)(k*1.5), 60-k*3, 60-k*3);
    }
  }

  public void drawblack(Graphics g,int i,int j){
    for (int k=0;k<=20;k++){
      g.setColor(new Color(k*3, k*3, k*3)); //グラデーション
      g.fillOval(20+70*i+5 +(int)(k*1.5) ,20+70*j+5+(int)(k*1.5), 60-k*3, 60-k*3);
    }
  }

  public void update(Observable o, Object arg) {
    panel.repaint();state.setText("パス");
    if(reversiModel.getPassFlag()==1){
     
      System.out.println("pass");
      panel.repaint();
      try {
        Thread.sleep(1000); // 1秒間だけ処理を止める
      } catch (InterruptedException e) {
      }
    }
    if(reversiModel.getPlayer()==1){
      state.setText("黒の手番です");
    }else{
      state.setText("白の手番です");
    }
    if(reversiModel.getFinishFlag()==1){
      if(reversiModel.countStorn(1)>reversiModel.countStorn(2)){
        state.setText("黒の勝利");
      }else{
        state.setText("白の勝利");
      }
    }
  }
}