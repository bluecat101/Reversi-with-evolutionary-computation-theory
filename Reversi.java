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
    Model m = new Model();

    // 一つ目のビュー
    ReversiView v1 = new ReversiView(m,"View 1");
    // １つ目のビューに対応するコントローラ
    new ReversiController(m,v1);
    //v1.setBounds(100,100,450,400);
  }
}