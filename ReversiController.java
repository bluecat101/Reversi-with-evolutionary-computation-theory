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
  protected int stoneX=0, stoneY=0;
  public ReversiController(Model m, ReversiView v){
    model = m;
    reversiModel = model.getReversiModel();
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
      reversiModel.initBoard();
    }else if(e.getSource() == view.getFinishButton()){
      System.exit(0);
    }
  }
  public void mouseDragged(MouseEvent e){}
  public void mouseMoved(MouseEvent e){}
  public void mouseClicked(MouseEvent e){}
  public void mouseEntered(MouseEvent e){
  }
  public void mouseExited(MouseEvent e){}
  public void mousePressed(MouseEvent e){
    stoneX = e.getX(); stoneY = e.getY();
  }
  public void mouseReleased(MouseEvent e){}
  public void keyTyped(KeyEvent e){
    char c = e.getKeyChar();
    switch(c){
      case 'z':
      reversiModel.setStone(stoneX,stoneY);
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
    stoneX = 70*(reversiModel.getPikaPika_x())+40;
    stoneY = 70*(reversiModel.getPikaPika_y())+40;
  }
  public void keyReleased(KeyEvent e){}
}