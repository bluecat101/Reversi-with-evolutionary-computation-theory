import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.LineBorder;
import java.util.*;


// Controller (C)

// KeyListener が，キー操作のリスナーインタフェース．
class ReversiController implements KeyListener, MouseListener, MouseMotionListener, ActionListener{
  protected Model model;
  protected Model.ReversiModel reversiModel;
  protected Model.ChatModel chatModel;
  protected ReversiView view;
  private int num=0;
  private int test_player = 1;
  //private Model.Ai ai;

  //add-----------------
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
    view.revel1Button().addActionListener(this);
    view.revel2Button().addActionListener(this);
    view.revel3Button().addActionListener(this);//それぞれのレベルのボタン
    ai=new Ai_1(model);//デフォルトのAIを設定。//のちにAiの設定をactionPerformedでやればいいかなって思ってる。
    //----------------------------------------------

  }
  public void actionPerformed(ActionEvent e){
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
    else if(e.getSource()==view.revel1Button()){
      ai=new Ai_1(model);//delete 引数
    }else if(e.getSource()==view.revel2Button()){
      ai=new Ai_2(model);//delete　引数
    }else if(e.getSource()==view.revel3Button()){
      ai=new Ai_3(model);
    }
    //-------------------------------------------

  }
  public void mouseDragged(MouseEvent e){}
  public void mouseMoved(MouseEvent e){
    reversiModel.next_position_mouse(e.getX(), e.getY());
  }
  public void mouseClicked(MouseEvent e){}
  public void mouseEntered(MouseEvent e){}
  public void mouseExited(MouseEvent e){}
  public void mousePressed(MouseEvent e){
    if(e.getSource() == view.getPanel()){
      reversiModel.xySetStone(reversiModel.getPikaPika_x(),reversiModel.getPikaPika_y());

      //add------
      ai.run();//今設定されているaiのrun関数を呼び出して石をセットする。
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
      reversiModel.xySetStone(reversiModel.getPikaPika_x(),reversiModel.getPikaPika_y());
      //add----------
      ai.run();
      //----------------
      break;
      case 'r':
      reversiModel.initBoard();
      break;
      case 'p':
      reversiModel.xySetStone(reversiModel.getPikaPika_x(),reversiModel.getPikaPika_y());
      //ai.exeAi();
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