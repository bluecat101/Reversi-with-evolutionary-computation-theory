import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.LineBorder;
import java.util.*;
import java.nio.charset.StandardCharsets;

// Controller (C)

// KeyListener が，キー操作のリスナーインタフェース．
class ReversiController implements KeyListener, MouseListener, MouseMotionListener, ActionListener{
  protected Model model;
  protected Model.ReversiModel reversiModel;
  protected Model.ChatModel chatModel;
  protected ReversiView view;
  private int num=0;
  private int test_player = 1;
  private javax.swing.Timer timer;

  //add-----------------
  private String mode;
  private CommClient cl;
  private Model ai;//aiのクラスを保持
  //--------------------

  public ReversiController(Model m, ReversiView v){
    model = m;
    reversiModel = model.getReversiModel();
    chatModel = model.getChatModel();
    view = v;
    view.getPanel().addMouseListener(this);
    view.getPanel().addMouseMotionListener(this);
    view.getPanel().addKeyListener(this);
    view.getPanel().setFocusable(true);
    view.getChatPanel().addMouseListener(this);
    view.getResetButton().addActionListener(this);
    view.getChatButton().addActionListener(this);
    view.getChatBox().addActionListener(this);
    view.getResetButton().addKeyListener(this);
    view.getChatButton().addKeyListener(this);
    //add------------------------------------------
    view.level1Button().addActionListener(this);
    view.level2Button().addActionListener(this);
    view.level3Button().addActionListener(this);//それぞれのレベルのボタン
    ai=new Ai_1(model);//デフォルトのAIを設定。//のちにAiの設定をactionPerformedでやればいいかなって思ってる。
    timer = new javax.swing.Timer(2000,this);
    //----------------------------------------------
    view.getSingleStartButton().addActionListener(this);
    view.getMultiPasswordBox().addActionListener(this);

  }
  public void actionPerformed(ActionEvent e){
    if(e.getSource() == timer){
      ai.run();
      view.getPanel().requestFocus();
      timer.stop();
    }else if(mode=="server&client"){
      String msg;
      if ((msg=reversiModel.getServer().recv())!=null)  {//クライアントからの変化があるかを確認する
        reversiModel.getServer().send(msg);//変化をサーバーに送信
      }
    }else if(mode=="client"){
      cl.recv();
    }
    if(e.getSource() == view.getResetButton()){
      reversiModel.initBoard();
    }else if(e.getSource() == view.getChatButton()){
      if(num%2==1){
        view.getChatBox().setEnabled(false); num--;
      }else{
        view.getChatBox().setEnabled(true);
        view.getChatBox().grabFocus(); num++;
      }
    }else if(e.getSource() == view.getChatBox()){
      String chat_sentence = view.getChatBox().getText();
      if(chat_sentence.equals("")){
        view.getChatBox().setEnabled(false); num--;
      }else{
        chatModel.setChat(chat_sentence, test_player);
        System.out.println(chat_sentence);
        if(test_player == 1){test_player++;}
        else{test_player--;}
        view.getChatBox().setText("");
      }
    }
    
    //add--------------------------------
    //ここでレベルを変えている。aiというModelクラスの変数を変更するだけで今のaiのレベルを保持。
    else if(e.getSource()==view.level1Button()){
      ai=new Ai_1(model);//delete 引数
    }else if(e.getSource()==view.level2Button()){
      ai=new Ai_2(model);//delete　引数
    }else if(e.getSource()==view.level3Button()){
      ai=new Ai_3(model);
    }
    //-------------------------------------------
    if(e.getSource()==view.getSingleStartButton()){
      System.out.println("startcontoroller");
      if(!Objects.isNull(view.getAiMode())){//aiが押されているかを判定
        System.out.println("ai");
        ai=view.getAiMode();
        mode="Ai";
        timer =new javax.swing.Timer(2000,this);//aiが2秒後に行動する。
      }
    }else if(e.getSource()==view.getMultiPasswordBox()){
      if(view.getSVMode()){
        System.out.println("server");
        mode="server&client";
        reversiModel.newServer();//サーバーの設立
      }else{
        System.out.println("client");
        mode="client";
        cl = new CommClient("localhost",10010,this.reversiModel);//クライアントの確立
        cl.setTimeout(10);//タイムアウトを設定
      }
      timer =new javax.swing.Timer(300,this);//0.3秒ごとにクライアントかサーバーで変化があったのかを確認する。
        timer.start();//変化の探査開始
    }
  }
  private int getPort(String password){
    int port=0;//port番号
    for(int i=password.getBytes(StandardCharsets.US_ASCII).length-1;0<=i;port+=password.getBytes(StandardCharsets.US_ASCII)[i--]){}//port番号をpasswordから計算
    //↑合言葉の全ての文字をそれぞれACSIIコードに変換してそれを足す。
    //↓合言葉をint型にしたものを9973(一万以下の最大の素数)で余りを取って50000を足す。
    port=port%9973+50000;//port番号を算出
    return port;
  }

  public void mouseDragged(MouseEvent e){}
  public void mouseMoved(MouseEvent e){
    reversiModel.next_position_mouse(e.getX(), e.getY());
  }
  public void mouseClicked(MouseEvent e){}
  public void mouseEntered(MouseEvent e){}
  public void mouseExited(MouseEvent e){}
  public void mousePressed(MouseEvent e){
    if(e.getSource() == view.getPanel() && reversiModel.transformMousePoint(e.getX()) == reversiModel.getPikaPika_x() && reversiModel.transformMousePoint(e.getY()) == reversiModel.getPikaPika_y()){
      reversiModel.xySetStone(reversiModel.getPikaPika_x(),reversiModel.getPikaPika_y());

      //add------
      //ai.run();//今設定されているaiのrun関数を呼び出して石をセットする。
      timer.start();
      view.getChatBox().setEnabled(true);
      view.getChatBox().grabFocus(); num++;
      //-----------

    }/*else if(e.getSource() == view.getChatPanel()){
      System.out.println("chat");
      if(num%2==1){
        view.getChatBox().setEnabled(false); num--;
      }else{
        view.getChatBox().setEnabled(true);
        view.getChatBox().grabFocus(); num++;
      }
    }*/
  }
  public void mouseReleased(MouseEvent e){}
  public void keyTyped(KeyEvent e){
    char c = e.getKeyChar();
    switch(c){
      case 'z':
      if(mode.contains("Ai")){
        reversiModel.xySetStone(reversiModel.getPikaPika_x(),reversiModel.getPikaPika_y());
        timer.start();
        view.getChatBox().setEnabled(true);
        view.getChatBox().grabFocus(); num++;
      }else if(mode=="server&client"){
        reversiModel.getServer().send(reversiModel.getPikaPika_x(),reversiModel.getPikaPika_y());//サーバーに直接送る
      }else if(mode=="client"){
        cl.send(reversiModel.getPikaPika_x(),reversiModel.getPikaPika_y());//クライアントからサーバーに送る
      }
      break;
      case 'r':
      reversiModel.initBoard();
      break;
    }
  }
 public void keyPressed(KeyEvent e){
    int k = e.getKeyCode();
    switch(k){
      case KeyEvent.VK_RIGHT:
        //下四つは盤面の移動を矢印キーでやる場合に使う
        reversiModel.next_position(reversiModel.getPikaPika_x(),reversiModel.getPikaPika_y(),0);//0:右,1:左,2:下,3:上
        break;
      case KeyEvent.VK_LEFT:
        reversiModel.next_position(reversiModel.getPikaPika_x(),reversiModel.getPikaPika_y(),1);
        break;
      case KeyEvent.VK_UP:
        reversiModel.next_position(reversiModel.getPikaPika_x(),reversiModel.getPikaPika_y(),3);
        break;
      case KeyEvent.VK_DOWN:
        reversiModel.next_position(reversiModel.getPikaPika_x(),reversiModel.getPikaPika_y(),2);
        break;
      case KeyEvent.VK_ENTER:
        view.getChatBox().setEnabled(true);
        view.getChatBox().grabFocus(); num++;
        break;
      default:
        break;
    }
  }
  public void keyReleased(KeyEvent e){}
}