import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.LineBorder;
import java.util.*;
import java.nio.charset.StandardCharsets;

// Controller (C)

// KeyListener が，キー操作のリスナーインタフェース．
class ReversiController implements KeyListener, MouseListener, MouseMotionListener, ActionListener {
  protected Model model;
  protected Model.ReversiModel reversiModel;
  protected Model.ChatModel chatModel;
  protected ReversiView view;
  private int num = 0;
  private javax.swing.Timer timer;
  private String mode;// Ai,client,server&client
  private CommClient cl;// client
  private CommServer sv;// server
  private Model ai;// aiのクラスを保持

  public ReversiController(Model m, ReversiView v) {
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
    view.getChatButton().addKeyListener(this);
    view.getSingleStartButton().addActionListener(this);
    view.getMultiPasswordBox().addActionListener(this);
  }

  public void actionPerformed(ActionEvent e) {
    if (e.getSource() == timer) {
      if (mode.contains("Ai")) {
        ai.run();
        if (reversiModel.getPassFlag("controller") == 0) {// 現状がpassじゃない<=>自分のターンに移る
          view.getPanel().requestFocus();
          timer.stop();
        }
      } else if (mode == "server&client") {
        String msg;
        if ((msg = sv.recv()) != null) {// クライアントからの変化があるかを確認する
          sv.send(msg);// 変化をサーバーに送信
        }
      } else if (mode == "client") {
        cl.recv();
      }
    }
    if (e.getSource() == view.getResetButton()) {
      reversiModel.initBoard();
    } else if (e.getSource() == view.getChatButton()) {
      if (num % 2 == 1) {
        view.getChatBox().setEnabled(false);
        num--;
      } else {
        view.getChatBox().setEnabled(true);
        view.getChatBox().grabFocus();
        num++;
      }
    } else if (e.getSource() == view.getChatBox()) {
      String chat_sentence = view.getChatBox().getText();
      if (chat_sentence.equals("")) {
        view.getChatBox().setEnabled(false);
        num--;
      } else {
        if (mode == "server&client") {
          sv.sendChat(chat_sentence, chatModel.getPlayerNum());
        } else if (mode == "client") {
          cl.sendChat(chat_sentence, chatModel.getPlayerNum());
        }
        view.getChatBox().setText("");
      }
    }
    if (e.getSource() == view.getSingleStartButton()) {
        mode = "Ai";
        timer = new javax.swing.Timer(2000, this);// aiが2秒後に行動する。
        if (!view.getFirst()) {// 後攻なら
          reversiModel.changeIsYourTurn();// クライアントを後攻とする
          if (view.getAiLevel() == 1) {
            ai = new Ai_1(model, 1);
          } else if (view.getAiLevel() == 2) {
            ai = new Ai_2(model, 1);
          } else if (view.getAiLevel() == 3) {
            ai = new Ai_3(model, 1);
          }
          // 先にaiの実行
          timer.start();
          view.getChatBox().setEnabled(true);
          view.getChatBox().grabFocus();
          num++;
        } else {
          if (view.getAiLevel() == 1) {// 先行なら
            ai = new Ai_1(model, 2);
          } else if (view.getAiLevel() == 2) {
            ai = new Ai_2(model,2);
          } else if (view.getAiLevel() == 3) {
            ai = new Ai_3(model, 2);
          }
        }
        reversiModel.clickStart();// 初期画面をリセットする
    } else if (e.getSource() == view.getMultiPasswordBox()) {
      if (view.getSVMode()) {
        int port = getPort(view.getMultiPasswordBox().getText());// 合言葉をポート番号に変換する。
        mode = "server&client";
        sv = new CommServer(port, model);// サーバーの設立
        sv.setTimeout(10);// タイムアウト時間の設定
        // 先行後攻を決める
        if ((int) (Math.random() * 2) == 1) {// 2回に１回変更する,1なら後攻
          reversiModel.changeIsYourTurn();
          chatModel.changePlayerNum();// chatの番号を２番目であるとする
          sv.send("serve first");// serverが後攻であることと通知する
        }
      } else {
        mode = "client";
        int port = getPort(view.getMultiPasswordBox().getText());// 合言葉をポート番号に変換する。
        cl = new CommClient("localhost", port, model);// クライアントの確立
        cl.setTimeout(10);// タイムアウトを設定
        if (!cl.recv()) {// メッセージが何もない<=>cl.recv()=falseなら自分が後攻
          reversiModel.changeIsYourTurn();// クライアントを後攻とする
          chatModel.changePlayerNum();// chatの番号を２番目であるとする
        }
      }
      reversiModel.clickStart();// 初期画面の更新
      timer = new javax.swing.Timer(300, this);// 0.3秒ごとにクライアントかサーバーで変化があったのかを確認する。
      timer.start();// 変化の探査開始
    }
  }

  private int getPort(String password) {
    int port = 0;// port番号
    for (int i = password.getBytes(StandardCharsets.US_ASCII).length - 1; 0 <= i; port += password
        .getBytes(StandardCharsets.US_ASCII)[i--]) {
    } // port番号をpasswordから計算
    // ↑合言葉の全ての文字をそれぞれACSIIコードに変換してそれを足す。
    // ↓合言葉をint型にしたものを9973(一万以下の最大の素数)で余りを取って50000を足す。
    port = port % 9973 + 50000;// port番号を算出
    return port;
  }

  public void mouseDragged(MouseEvent e) {
  }

  public void mouseMoved(MouseEvent e) {
    reversiModel.next_position_mouse(e.getX(), e.getY());
  }

  public void mouseClicked(MouseEvent e) {
  }

  public void mouseEntered(MouseEvent e) {
  }

  public void mouseExited(MouseEvent e) {
  }

  public void mousePressed(MouseEvent e) {
    if (e.getSource() == view.getPanel() && reversiModel.transformMousePoint(e.getX()) == reversiModel.getPikaPika_x()
        && reversiModel.transformMousePoint(e.getY()) == reversiModel.getPikaPika_y()&&reversiModel.getIsYourTurn()) {
      if (mode.contains("Ai")) {
        chatModel.writeHistroy(reversiModel.getPikaPika_x(),reversiModel.getPikaPika_y(), reversiModel.getIsYourTurn());// 履歴に書く。
        reversiModel.xySetStone(reversiModel.getPikaPika_x(), reversiModel.getPikaPika_y());
        timer.start();
        view.getChatBox().setEnabled(true);
        view.getChatBox().grabFocus();
        num++;
      } else if (mode == "server&client") {
        sv.send(reversiModel.getPikaPika_x(), reversiModel.getPikaPika_y());// サーバーに直接送る
      } else if (mode == "client") {
        cl.send(reversiModel.getPikaPika_x(), reversiModel.getPikaPika_y());// クライアントからサーバーに送る
      }

    } 
  }

  public void mouseReleased(MouseEvent e) {
  }

  public void keyTyped(KeyEvent e) {
    char c = e.getKeyChar();
    switch (c) {
      case 'z':
      if(reversiModel.getIsYourTurn()){
        if (mode.contains("Ai")) {
          chatModel.writeHistroy(reversiModel.getPikaPika_x(), reversiModel.getPikaPika_y(),reversiModel.getIsYourTurn());// 履歴に書く。
          reversiModel.xySetStone(reversiModel.getPikaPika_x(), reversiModel.getPikaPika_y());
          timer.start();
          view.getChatBox().setEnabled(true);
          view.getChatBox().grabFocus();
          num++;
        } else if (mode == "server&client") {
          sv.send(reversiModel.getPikaPika_x(), reversiModel.getPikaPika_y());// サーバーに直接送る
        } else if (mode == "client") {
          cl.send(reversiModel.getPikaPika_x(), reversiModel.getPikaPika_y());// クライアントからサーバーに送る
        }
        
      }
      break;
      
    }
  }

  public void keyPressed(KeyEvent e) {
    int k = e.getKeyCode();
    switch (k) {
      case KeyEvent.VK_RIGHT:
        // 下四つは盤面の移動を矢印キーでやる場合に使う
        reversiModel.next_position(reversiModel.getPikaPika_x(), reversiModel.getPikaPika_y(), 0);// 0:右,1:左,2:下,3:上
        break;
      case KeyEvent.VK_LEFT:
        reversiModel.next_position(reversiModel.getPikaPika_x(), reversiModel.getPikaPika_y(), 1);
        break;
      case KeyEvent.VK_UP:
        reversiModel.next_position(reversiModel.getPikaPika_x(), reversiModel.getPikaPika_y(), 3);
        break;
      case KeyEvent.VK_DOWN:
        reversiModel.next_position(reversiModel.getPikaPika_x(), reversiModel.getPikaPika_y(), 2);
        break;
      case KeyEvent.VK_ENTER:
        view.getChatBox().setEnabled(true);
        view.getChatBox().grabFocus();
        num++;
        break;
      default:
        break;
    }
  }

  public void keyReleased(KeyEvent e) {
  }
}