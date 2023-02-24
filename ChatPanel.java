import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.awt.event.*;

// このクラスはチャット画面を生成するクラスである
@SuppressWarnings("deprecation")
class ChatPanel extends JPanel implements Observer, AdjustmentListener {
  protected Model.ChatModel chatmodel;    // モデルの内部クラス、チャットモデルを保持
  private ArrayList<StringBuffer> message;// 今までに入力されたメッセージを保存する配列
  private ArrayList<Integer> player;      // メッセージを入力したプレイヤー番号を入力したメッセージの順に保存
  protected Font font;                    // 作成したフォントを保持
  protected FontMetrics fontMetrics;      // フォントの情報を保持
  protected JScrollBar scrollbar;         // スクロールバー
  protected int stheight;                 // スクロールバーによって変化する表示したいチャット画面の位置
  protected int panelheight;              // このパネルの高さ
  protected int playernum;                // プレイヤーの番号
  protected int opponent = 2;             // 相手の番号を2で初期化

  public ChatPanel(Model m, JScrollBar scrollbar) {
    this.scrollbar = scrollbar;                // スクロールバーをセット
    this.scrollbar.addAdjustmentListener(this);// スクロールバーをAdjustmentListenerに登録
    scrollbar.setMaximum(0);                   // スクロールバーの最大値を0で初期化
    chatmodel = m.getChatModel();          // チャットモデルをセット
    message = chatmodel.getChatMessage();      // メッセージの配列を受け取る
    player = chatmodel.getChatPlayer();        // プレイヤー番号の配列を受け取る
    font = new Font("Serif", Font.PLAIN, 20);  // フォントを設定
    fontMetrics = this.getFontMetrics(font);   // フォントの情報を保持
    chatmodel.addObserver(this);               // このパネルをchatmodelにObserverとして登録
  }

  // チャット画面に描画する関数
  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    Graphics2D g2 = (Graphics2D) g;// Graphics2Dクラスへのキャスト

    // 図形や線のアンチエイリアシングの有効化
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    // 文字描画のアンチエイリアシングの有効化
    g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    panelheight = this.getHeight();             // パネルの高さをセット
    int newextray = panelheight - stheight - 20;// 描画開始位置のy座標の候補を計算
    int extray = 0;                             // 実際の描画開始位置yを0で初期化
    if (newextray < extray) {                   // もし描画位置の候補が負の値なら
      extray = newextray;                       // この値を描画開始位置とする
    }
    drawString(g, extray);                      // 個の描画開始位置を用いて文字及び吹き出しの描画を行う
  }

  // line番目のStringBufferのstartからの文字列を返す関数
  public String getPostfix(int start, int line) {
    if (start < 0)
      start = 0;                              // startが負の値ならstartの値を0とする
    if (start > message.get(line).length())
      start = message.get(line).length();     // startの値が指定したStringBufferよりも大きかったら、そのStringBufferのサイズをstartとする
    return message.get(line).substring(start);// line番目のStringBufferのstartからの文字列を返す
  }

  // line番目のStringBufferのendまでの文字列を返す関数
  public String getPrefix(int end, int line) {
    if (end < 0)
      end = 0;                                 // endが負の値ならendの値を0とする
    if (end > message.get(line).length())
      end = message.get(line).length();        // endの値が指定したStringBufferよりも大きかったら、そのStringBufferのサイズをendとする
    return message.get(line).substring(0, end);// line番目のStringBufferのendまでの文字列を返す関数
  }

  // line番目のStringBufferの文字列を返す関数
  public int getLength(int line) {
    return message.get(line).length();
  }

  // sの文字列の横幅を返す関数
  public int buffersize(StringBuffer s) {
    return fontMetrics.stringWidth(s.substring(0));
  }

  // 新しい文字が配列に入れられるたびにその文字の長さに応じて改行の必要があるかを判断し次の描画のために配列を書き換える関数
  private void remake() {
    int panelwidth = this.getWidth();// このパネルの横幅をセット
    int i = message.size() - 1;// メッセージの配列の最終番目をセット
    while (fontMetrics.stringWidth(getPostfix(0, i)) > panelwidth - 60) {// 新しく入った文字が表示させる予定の横幅より大きかったら
      int j = 0;
      while (fontMetrics.stringWidth(getPrefix(j, i)) <= panelwidth - 60) {
        j++;// 新しく入った文字について、表示させる予定の横幅より少し大きくなる文字数を探す
      }
      if (fontMetrics.stringWidth(getPostfix(j-1, i)) != 0) {// もしj-1文字以降に文字が存在していたら
        message.add(i + 1, new StringBuffer());              // ArrayListにStringBufferを追加
        message.get(i + 1).insert(0, getPostfix(j - 1, i));  // 新しく作ったStringufferに新しく入った文字のj-1文字分を
        message.get(i).delete(j - 1, getLength(i));          // もともと新しい文字があった場所のj-1文字目から最後までの文字を消去
      }
      i++;// 文字を区切ることができたが、区切った後の文字も長い可能性があるのでまた繰り返す
    }
    // 表示したい文字のかたまりが分かりやすいように文字が何も入っていないStringBufferを配列に追加することによって区切る
    if (fontMetrics.stringWidth(getPostfix(0, i)) != 0) {// もし最後の要素に文字列が入っていたら
      message.add(i + 1, new StringBuffer());            // ArrayListにStringBufferを追加
    }
  }

  // この関数はチャットの文字の下にある吹き出しをx,yの位置に幅width,縦height,で描画する関数である。
  // playerは表示する文字を入力したプレイヤー番号
  private void round(Graphics g, int x, int y, int width, int height, int player) {
    // ここからif文までは吹き出しの角が丸い四角形の部分を描画する
    int dir = 20;
    g.fillRect(x, y, width, height);

    g.fillOval(x - dir / 2, y, dir, dir);
    g.fillOval(x - dir / 2 + width, y, dir, dir);

    g.fillRect(x - dir / 2, y + dir / 2, dir, height - dir / 2);
    g.fillRect(x - dir / 2 + width, y + dir / 2, dir, height - dir / 2);

    g.fillOval(x - dir / 2, y + height - dir / 2, dir, dir);
    g.fillOval(x - dir / 2 + width, y + height - dir / 2, dir, dir);

    g.fillRect(x, y + height - dir / 2, width, dir);

    if (player == playernum) {                   // 表示する文字が自分のものであれば
      g.fillPolygon(new int[] { x - dir / 2 + width, x + dir / 2 + width, x + dir / 2 + width + 10 },
          new int[] { y + 2, y + 15, y + 5 }, 3);// 右側にに吹き出しを向ける
    } else {
      g.fillPolygon(new int[] { x - dir / 2 + 10, x - dir / 2, x - dir }, 
          new int[] { y + 2, y + 15, y + 5 }, 3);// 左側に吹き出しを向ける
    }
  }

  // extrayを描画の初めのy座標として配列内のすべての文字を描画
  private void drawString(Graphics g, int extray) {
    int h = fontMetrics.getAscent();// このフォントでの文字の縦幅をセット
    int h1 = h;
    int chatflag = 1;//吹き出しの描画を行うか否か
    int pn = 0;//現在参照しているプレイヤー番号の配列の番号を保持

    for (int i = 0; i < message.size(); i++) {
      StringBuffer f = message.get(i);// message配列にある文字列を一つずつセット
      String s = new String(f);       // 文字列をString型にする
      int j = i;
      int h1sub = h1;
      // 次のif文は吹き出しの描画を行う
      if (chatflag == 1) {                // chatflagが1なら吹き出しの描画を行う
        if (player.get(pn) == playernum) {// player配列内の値を一つずつ見ていって自分の番号と同じなら
          g.setColor(Color.orange);       // オレンジ色にセット
        } else {                          // そうでなければ
          g.setColor(Color.green);        // 緑色をセット
        }
        if (buffersize(message.get(j + 1)) == 0) {                   // 次の要素に文字列が何もなかった場合、一行のかたまりを描画したいので
          if (player.get(pn) == playernum) {                         // 自分が打ち込んだ文字を表示するとき
            int x = 20 + 145 - buffersize(f) + 5;                    // 文字列の大きさに合わせて表示開始位置のx座標をセット
            int y = h1sub + extray + 20 - fontMetrics.getAscent();   // y座標をセット
            int width = buffersize(f) ;                              // 幅を文字列の長さにしてセット
            int height = fontMetrics.getAscent();                    // このフォントの高さをセット
            round(g, x, y, width, height, playernum);                // 指定した大きさ、場所に吹き出しの描画を行う
          } else {                                                   // 相手の打った文字を表示するとき
            int x = 25;                                              // 文字列を表示するx座標をセット
            int y = h1sub + extray + 20 - fontMetrics.getAscent();   // y座標をセット
            int width = buffersize(f) ;                              // 幅を文字列の長さにしてセット
            int height = fontMetrics.getAscent();                    // このフォントの高さをセット
            round(g, x, y, width, height, opponent);                 // 指定した大きさ、場所に吹き出しの描画を行う
          }
        } else {                                                     // 次の要素に文字列がある場合は二行以上の文字のかたまりを描画したいので
          if (player.get(pn) == playernum) {                         // 自分打った文字を表示するとき
            int x = 20 + 20 + 5;                                     // 文字列を表示するx座標をセット
            int ysub = h1sub + extray + 20 - fontMetrics.getAscent();// y座標をセット
            int width = 125;                                         // 幅をセット
            int height = fontMetrics.getAscent();                    // このフォントの高さをセット
            while (buffersize(f) != 0) {
              f = message.get(++j);                                  // かたまりが何行あるのかを調べる
            }
            round(g, x, ysub, width, height * (j - i), playernum);   // 指定した大きさ、場所に吹き出しの描画を行う。高さに関してはフォントの高さの行数分にしている
          } else {
            int x = 25;                                              // 文字列を表示するx座標をセット
            int ysub = h1sub + extray + 20 - fontMetrics.getAscent();// y座標をセット
            int width = 125;                                         // 幅をセット
            int height = fontMetrics.getAscent();                    // このフォントの高さをセット
            while (buffersize(f) != 0) {
              f = message.get(++j);                                  // かたまりが何行あるのかを調べる
            }
            round(g, x, ysub, width, height * (j - i), opponent);   // 指定した大きさ、場所に吹き出しの描画を行う。高さに関してはフォントの高さの行数分にしている
          }
        }
        chatflag = 0;                                               // ここまでで吹き出しの一かたまりの描画が完成したのでフラグを0とする
        pn++;                                                       // プレイヤーの番号に関する配列を一つ進める
      }
      g.setColor(Color.black);                                      // 色を黒色に変更
      g.setFont(font);                                              // フォントをセット
                                                                    // 描画しようとしている文字列が、自分のものであり、かつ今から描画しようとしている文字列が一行のかたまりである場合
      if (player.get(pn - 1) == playernum && (i == 0 || buffersize(message.get(i - 1)) == 0)
          && buffersize(message.get(i + 1)) == 0) {
        g.drawString(s, 20 + 145 - buffersize(f), h1 + extray + 20);// 文字列のx座標の描画位置を文字列の大きさから計算して描画する
      } else {                                                      // その文字列が複数行のかたまりのうちの一行なら
        if (player.get(pn - 1) == playernum) {                      // 描画する文字列が自分の打ったものなら
          g.drawString(s, 20 + 20, h1 + extray + 20);               // 右側に文字列を描画
        } else {                                                    // そうでないなら
          g.drawString(s, 20, h1 + extray + 20);                    // 左側に文字列を描画
        }
      }
      h1 = h1 + h;                                                  // それぞれのStringBufferをずらして置くために値を変更していく
      if (fontMetrics.stringWidth(s) == 0) {                        // ひとかたまりの文字の描画が完了し、何も入っていないStringBufferを読み込んだら
        chatflag = 1;                                               // フラグを1に戻し、次の吹き出し描画の準備を行う
      }
    }
  }

  // 配列に新しい文字が追加されたことがchatModelから知らされたら再描画のために呼び出される
  public void update(Observable o, Object arg) {
    playernum = chatmodel.getPlayerNum();// Chatmodelからplayerの番号を取得する。
    if (playernum == 2) {
      opponent = 1;                      // playerの番号が2なら相手の番号を1とする。
    }
    remake();// 描画のために文字列の配列を書き換える
    stheight = fontMetrics.getAscent() * (message.size());  // 配列内の文字を全て表示した際の縦幅を計算し値をセット
    if (panelheight - stheight - 80 < 0) {                  // 文字全てを表示した際の縦幅がパネルのサイズを超えたら
      scrollbar.setMinimum(panelheight - 20);               // 表示を綺麗にするためにバーの最小値を調整
      scrollbar.setMaximum(stheight);                       // バーの最大値を設定
      stheight = fontMetrics.getAscent() * (message.size());// スクロールされたりするとstheightの値が変わってしまうので再定義
      scrollbar.setValue(stheight);                         // スクロールバーの位置を一番下に変更
    }
    this.repaint();// 再描画
  }

  // スクロールバーに動きがあったらそれに対応したチャット画面を再描画する関数である
  public void adjustmentValueChanged(AdjustmentEvent e) {
    JScrollBar sb = (JScrollBar) e.getSource();// スクロールバーを取得
    if (sb.getValue() != 0) {
      stheight = sb.getValue();// スクロールバーの値を取得
      this.repaint();          // 再描画
    }
  }
}