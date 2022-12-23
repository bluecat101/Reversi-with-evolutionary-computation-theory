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
  protected ReversiView view;
  private int num=0;
  public ReversiController(Model m, ReversiView v){
    model = m;
    reversiModel = model.getReversiModel();
    view = v;
    view.getPanel().addMouseListener(this);
    view.getPanel().addMouseMotionListener(this);
    view.getPanel().addKeyListener(this);
    view.getPanel().setFocusable(true);
    view.cardPanel.setFocusable(true);//add saitou
    view.cardPanel.addKeyListener(this);//add saitou
    view.getResetButton().addActionListener(this);
    view.getChatButton().addActionListener(this);
    view.getResetButton().addKeyListener(this);
    view.getChatButton().addKeyListener(this);
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
    }
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
      //System.out.println("aaaa");
    }/*else if(e.getSource() == view.getChatPanel()){
      view.getChatPanel().setFocusable(true);
      //System.out.println("bbbb");
    }*/
  }
  public void mouseReleased(MouseEvent e){}
  public void keyTyped(KeyEvent e){
    char c = e.getKeyChar();
    switch(c){
      case 'z':
      reversiModel.xySetStone(reversiModel.getPikaPika_x(),reversiModel.getPikaPika_y());
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
      default:
        break;
    }
  }
  public void keyReleased(KeyEvent e){}
}