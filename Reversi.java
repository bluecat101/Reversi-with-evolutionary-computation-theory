import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.LineBorder;
import java.util.*;

///////////////////////////////////////
// Main class
//
class Reversi {
  public static void main(String argv[]) {
    // モデルの生成．
    ReversiModel m = new ReversiModel();

    // 一つ目のビュー
    ReversiView v1 = new ReversiView(m,"View 1");
    // １つ目のビューに対応するコントローラ
    new ReversiController(m,v1);
    //v1.setBounds(100,100,450,400);
  }
}

// model
// model
//document------
// 0 in board_array means blank
// 1 in board_array means black storn
// 2 in board_array means white storn
// 3 in board_array means judge
//player is type of int and player1 is 1,player2 is 2
//定数の説明
//  board_size: 盤面の大きさ
//  sizeOfOne: 表示する盤面の一つのマスの大きさ
//変数の説明
//   int[][] board_array: ボードの配列
// 　int[][] judge_array: 次に打てるマスの情報の入った配列
//   int player: playerの番号(1,2)
//   int pass_flag: パスがあるかのフラグ(0がパス無し、1がパス。)
//   int pikapika_x=0; 光っている場所のx座標
//   int pikapika_y=0; 光っている場所のy座標
//関数の説明
// public ReversiModel(){
//    ReversiModel関数のコンポーネント

// public void setStone(int mouse_x, int mouse_y){
//    コントローラーから呼び出される関数。マウスの座標を受け取って石をひっくり返す。
//    引数: mouse_x: マウスのx座標
//   　　　 mouse_y: マウスのy座標

// public int[][] getBoardArray(){
//    今の状態の配列を返す関数

// public int[][] getJudgeBoardArray(int player){
//    置ける位置の配列を返す関数。現状の石の位置にplayerが置ける位置を3として配列を返す。
//    引数: player: playerの番号(先手が1,後手が2)

//
// public int getPlayer(){
//      judge_arrayを返す。
// public int getPassFlag(){
//      パスなのかを判定す関数。リセットも含まれている。

// private void search(int x,int y){
//    x,yの周辺をみてひっくり返すことができるかを判定し、ひっくり返す関数。
//    引数: x: 石を置くx座標
//   　　　 y: 石を置くy座標

// private boolean pre_search(int x,int y,int player){
//    x,yにplayerが置くことができるかを判定する関数。
//    引数: x: 石を置くx座標
//   　　　 y: 石を置くy座標

// private int getOpponentStone(int player){
//    相手のplayerの番号を返す関数。
//    引数: player: playerの番号(先手が1,後手が2)

// private int transformMousePoint(int mouse){
//    マウスの座標を配列の番号に変換する関数。
//    引数: mouse: マウスの座標

// public void initBoard()
//     board_arrayを初期化する。また、playerを1に、pass_flagをリセットする。

// public int countStorn(int colornum)
//     colornumの石の個数を数え、intで返す。
//     引数: colornum: 黒を1,白を2とする。
//
//public void xySetStone(int x,int y){
//      配列値でsetStoneを呼び出す。
//      引数: x:配列の行の値
//      　　  y:配列の列の値
//public int toGraphics(int num){
//      配列の値を描画の座標に変換して返す
//      引数: num: 配列の添え字
//
//--------------

@SuppressWarnings("deprecation")
class ReversiModel extends Observable{
  final int board_size=8;
  final int sizeOfOne=70;
  private int[][] board_array= new int[board_size][board_size];
  private int[][] judge_array= new int[board_size][board_size];
  private int pikapika_x=2;//阪上
  private int pikapika_y=3;//阪上
  private int player;
  private int pass_flag;
  public ReversiModel(){
    initBoard();

  }

  //コントローラーから呼び出される。playerは1,2で渡してほしい。
  public void setStone(int mouse_x, int mouse_y){
    // int x=mouse_x;int y=mouse_y;
    int x = transformMousePoint(mouse_x);//mouseの座標の変換
    int y = transformMousePoint(mouse_y);//mouseの座標の変換
    System.out.println(x+" "+y);
    if(x==-1||y==-1||board_array[x][y]!=0){//範囲外
      return ;
    }
    //searchする座標を探す
    judge_array = getJudgeBoardArray(player);
    if(judge_array[x][y]==3){//自分のところに置ける
      search(x,y);
      board_array[x][y]=player;//自分の位置に置く
      judge_array = getJudgeBoardArray(getOpponentStone(player));//相手プレイヤーが置けるかどうか
      if(canPut()==true){//置けるなら
        player=getOpponentStone(player);//プレイヤーの交代
      }else{
        judge_array = getJudgeBoardArray(player);//自分プレイヤーの置ける位置を格納。
        if(canPut()==true){
          pass_flag=1;
        }
      }
      setChanged();
      notifyObservers();
    }
  }
  private void search(int x,int y){
    int search_x=x;int search_y=y;
    for(int i=-1;i<=1;i++){
      for(int j=-1;j<=1;j++){
        search_x=x+i;
        search_y=y+j;
        if(search_x==-1||search_y==-1||search_x==8||search_y==8){//範囲外なら抜ける。
          continue;
        }else if(board_array[search_x][search_y]!=getOpponentStone(player)){//相手の石じゃないなら抜ける
          continue;
        }
        while(search_x!=-1&&search_y!=-1&&search_x!=8&&search_y!=8&&board_array[search_x][search_y]==getOpponentStone(player)){//範囲内かつ相手の石ならループする
          search_x+=i;search_y+=j;
        }
        if(search_x!=-1&&search_y!=-1&&search_x!=8&&search_y!=8&&board_array[search_x][search_y]==player){//範囲内であるかつ自分の石である
          search_x-=i;search_y-=j;
          while(search_x!=x||search_y!=y){
            board_array[search_x][search_y]=player;//ひっくり返す。
            search_x-=i;search_y-=j;
          }
        }
      }
    }
  }
  private boolean pre_search(int x,int y,int player){
    int search_x=x;int search_y=y;
    for(int i=-1;i<2;i++){
      for(int j=-1;j<2;j++){
        search_x=x+i;search_y=y+j;

        if(search_x==-1||search_y==-1||search_x==8||search_y==8){//範囲外なら抜ける。
          continue;
        }else if(board_array[search_x][search_y]!=getOpponentStone(player)){//相手の石じゃないなら抜ける
          continue;
        }
        while(search_x!=-1&&search_y!=-1&&search_x!=8&&search_y!=8&&board_array[search_x][search_y]==getOpponentStone(player)){//範囲内かつ相手の石ならループする
          search_x+=i;search_y+=j;
        }
        //置いた石の真上が自分の石の時にも実行されるが、while文には入らない。
        if(search_x!=-1&&search_y!=-1&&search_x!=8&&search_y!=8&&board_array[search_x][search_y]==player){//範囲内である
          return true;//置けること返す。
        }
      }
    }
    return false;
  }
  //相手のプレイヤーの番号を返す関数。
  private int getOpponentStone(int player){
    if(player == 1){
      return 2;
    }
    return 1;
  }
  //マウスの座標を配列に変換する関数。
  private int transformMousePoint(int mouse){
    if(mouse<20||mouse>=sizeOfOne*8+20){
      return -1;
    }
    mouse-=20;
    int index=mouse/sizeOfOne;
    return index;
  }
  //modelが呼び出す。---------
  public int[][] getBoardArray(){
    return board_array;
  }
  //置けるかどうかの判定結果を格納して配列を返す関数。
  public int[][] getJudgeBoardArray(int player){
    int[][] judge_array= new int[board_size][board_size];//boardの用意
    for(int i=0;i<board_size;i++){
      for(int j=0;j<board_size;j++){
        judge_array[i][j]=board_array[i][j];
      }
    }
    for(int i=0;i<board_size;i++){
      for(int j=0;j<board_size;j++){
        if(board_array[i][j]==0&&pre_search(i,j,player)==true){
          judge_array[i][j]=3;//置けることが判明したら3を入れる。
        }      
      }
    }
    return judge_array;
  }
  public int getPlayer(){
    return player;
  }
  public int getPassFlag(){
    if(pass_flag==0){//passでない。
      return pass_flag;
    }else{
      pass_flag=0;//初期化
      return 1;//元の値
    }
   
  }
  //置けるかどうかを判定する関数
  public boolean canPut(){
    for(int i=0;i<board_size;i++){
      for(int j=0;j<board_size;j++){
        if(judge_array[i][j]==3){
          setCanPut_x(i);
          setCanPut_y(j);
          return true;//playerは置ける
        }
      }
    }
    return false;
  }
  //置ける位置を含む配列を返す。
  public int[][] getCanPut(){
    return judge_array;
  }
 
  public void initBoard(){
    pass_flag=0;
    player=1;
    pikapika_x=2;
    pikapika_y=3;
    for(int i=0;i<board_size;i++){
      for(int j=0;j<board_size;j++){
        board_array[i][j]=0;
      }
    }
    //white
    board_array[3][3]=2;board_array[4][4]=2;
    //black
    board_array[3][4]=1;board_array[4][3]=1;
    setCanPut_x(2);
    setCanPut_y(3);
    judge_array = getJudgeBoardArray(player);
    setChanged();
    notifyObservers();
  }
  public int countStorn(int colornum){
    int count=0;
    for(int i=0;i<board_size;i++){
      for(int j=0;j<board_size;j++){
        if(board_array[i][j]==colornum){
          count++;
        }
      }
    }
    return count;
  }
  //光る座標
  public int getCanPut_x(){ //ココから阪上↓
    return pikapika_x;
  }
  public int getCanPut_y(){
    return pikapika_y;
  }
  public void setCanPut_x(int x){
    pikapika_x = x;
  }
  public void setCanPut_y(int y){
    pikapika_y = y;
  }

  public int distance(int x1, int y1, int x2, int y2){
    int d = (x1-x2)*(x1-x2) + (y1-y2)*(y1-y2);
    return d;
  }

  public void next_position(int x, int y,int direction){//0:右,1:左,2:下,3:上
    int[] array={0,8,0,8};
    switch(direction){
      case 0:
      array[direction]=getCanPut_x()+1;
      break;
      case 1:
      array[direction]=getCanPut_x();
      break;
      case 2:
      array[direction]=getCanPut_y()+1;
      break;
      case 3:
      array[direction]=getCanPut_y();
      break;
    }
    int next_x = 30, next_y = 30;//十分に離れているとする。
    int[][] arr = new int[board_size][board_size];
    arr = getCanPut();
    for(int i=array[0]; i<array[1]; i++){
      for(int j=array[2]; j<array[3]; j++){
        if(arr[i][j] == 3){
          if(distance(x,y,next_x,next_y)>distance(x,y,i,j)){next_x = i; next_y = j;}
        }
      }
    }
    if(next_x == 30 || next_y == 30){next_x = x; next_y = y;}
    System.out.println(next_x+"    "+next_y);
    setCanPut_x(next_x); setCanPut_y(next_y);
    setChanged();
    notifyObservers();
  }
  public void xySetStone(int x,int y){//配列値でsetStoneを呼び出す。
    setStone(toGraphics(x), toGraphics(y));
  }
  public int toGraphics(int num){//配列の値を描画の座標に変換
    return 20+70*num;
  }
}
////////////////////////////////////////////////////
// View (V)
@SuppressWarnings("deprecation")
class ReversiView extends JFrame implements Observer {
  protected ReversiModel model;
  protected ReversiPanel panel;
  protected ChatPanel chatpanel;
  protected JLabel state;
  protected JButton finish,reset;
  protected JTextField chatbox;
 
  public ReversiView(ReversiModel m,String st) {
    super(st);
    model = m;
    model.addObserver(this);
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
    reset = new JButton("reset");

    //chatbox = new JTextField();
 
    //Panelによる塊の作成
    this.setTitle("Reversi Panel");
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

    // gbc.gridy=2;
    // gbc.insets = new Insets(20, 20, 20, 0);
    // gbc.weightx = 1.0;
    // gbc.weighty = 0.02;
    // layout.setConstraints(chatbox, gbc);

    p1.add(blackpanel);p1.add(chatpanel);
    //p1.add(chatbox);

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

    getContentPane().add(p1,BorderLayout.WEST);
    getContentPane().add(p2,BorderLayout.EAST);

    this.add(panel,BorderLayout.CENTER);
 
    // pack は JFrameのサイズを自動設定するメソッド．
    // this.setSize(300,200); などの代わり
    this.pack();
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.setVisible(true);
  }

  // ReversiPanel を ReversiView の内部クラスとして実装
  class ReversiPanel extends JPanel {
    public void paintComponent(Graphics g) {
      super.paintComponent(g);
      int[][] board_array = model.getBoardArray();
      int[][] canput = model.getJudgeBoardArray(model.getPlayer());//modelのおけるか配列
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
            if(model.getPlayer()==1){
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
      g.fillRect(20+70*model.getCanPut_x(), 20+70*model.getCanPut_y(), 70, 70);

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
      model.addObserver(this);
      this.add(stone);
      this.add(count);
    }
    public void update(Observable o,Object arg){
      String s = Integer.toString(model.countStorn(player));
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

  public ReversiPanel getPanel(){
    return panel;
  }

  public JButton getResetButton(){
    return reset;
  }

  public JButton getFinishButton(){
    return finish;
  }

  public void update(Observable o, Object arg) {
    panel.repaint();state.setText("パス");
    if(model.getPassFlag()==1){
     
      System.out.println("pass");
      panel.repaint();
      try {
        Thread.sleep(1000); // 1秒間だけ処理を止める
      } catch (InterruptedException e) {
      }
    }
    if(model.getPlayer()==1){
      state.setText("黒の手番です");
    }else{
      state.setText("白の手番です");
    }
    // if(model.getFinishFlag()==1){
    //   if(model.stoneCount(1)>model.stoneCount(2)){
    //     state.setText("黒の勝利");
    //   }else{
    //     state.setText("白の勝利");
    //   }
    // }
  }
}

/////////////////////////////////////////////////////
// Controller (C)

// KeyListener が，キー操作のリスナーインタフェース．
class ReversiController implements KeyListener, MouseListener, MouseMotionListener, ActionListener{
  protected ReversiModel model;
  protected ReversiView view;
  protected int stoneX=0, stoneY=0;
  private int count_p=0;
  public ReversiController(ReversiModel m, ReversiView v){
    model = m;
    view = v;
    view.getPanel().addMouseListener(this);
    view.getPanel().addKeyListener(this);
    view.getPanel().setFocusable(true);
    view.getResetButton().addActionListener(this);
    view.getFinishButton().addActionListener(this);
    view.getResetButton().addKeyListener(this);
  }
  public void actionPerformed(ActionEvent e){
    if(e.getSource() == view.getResetButton()){
      model.initBoard();
    }else if(e.getSource() == view.getFinishButton()){
      System.exit(0);
    }
  }
  public void mouseDragged(MouseEvent e){}
  public void mouseMoved(MouseEvent e){}
  public void mouseClicked(MouseEvent e){}
  public void mouseEntered(MouseEvent e){
    //System.out.println(stoneX+" "+stoneY);
  }
  public void mouseExited(MouseEvent e){}
  public void mousePressed(MouseEvent e){
    stoneX = e.getX(); stoneY = e.getY();
    //model.setStone(stoneX,stoneY);
  }
  public void mouseReleased(MouseEvent e){}
  public void keyTyped(KeyEvent e){
    char c = e.getKeyChar();
    switch(c){
      case 'z':
      model.setStone(stoneX,stoneY);
      break;
    }
  }
 public void keyPressed(KeyEvent e){
    int k = e.getKeyCode();
    switch(k){
      case KeyEvent.VK_RIGHT:
        //下四つは盤面の移動を矢印キーでやる場合に使う
        model.next_position(model.getCanPut_x(),model.getCanPut_y(),0);//0:右,1:左,2:下,3:上
        break;
      case KeyEvent.VK_LEFT:
        model.next_position(model.getCanPut_x(),model.getCanPut_y(),1);
        break;
      case KeyEvent.VK_UP:
        model.next_position(model.getCanPut_x(),model.getCanPut_y(),3);

        break;
      case KeyEvent.VK_DOWN:
        model.next_position(model.getCanPut_x(),model.getCanPut_y(),2);
        break;
      default:
        break;
    }
    stoneX = 70*(model.getCanPut_x())+40;
    stoneY = 70*(model.getCanPut_y())+40;
  }
  public void keyReleased(KeyEvent e){}
}