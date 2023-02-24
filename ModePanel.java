import javax.swing.*;
import java.awt.*;
import java.io.File;
import javax.imageio.ImageIO;

// このクラスでは一人用か二人用かモードを選択する画面を生成するクラスである。
public class ModePanel extends JPanel{
  JButton returnButton,single,multi;// 戻るボタン、一人用ボタン、二人用ボタン
  Image imgBack;// 背景画像の変数
  ModePanel(){
    setLayout(null);
    try {
      imgBack = ImageIO.read(new File("haikei.jpg"));// 背景画像をimgBackに読み込み
    } catch (Exception e) {
        System.out.println(e);
        System.exit(0);
    }
    ImageIcon icon1 = new ImageIcon("osero-illust7.png");// ボタン用の画像を読み込み
    ImageIcon icon2 = new ImageIcon("osero-illust8.png");// ボタン用の画像を読み込み

    /* 一人用ボタン */
    single = new JButton("Single",icon1);                  // ボタンに表示する文字とボタンの画像を設定
    single.setRolloverIcon(icon2);                         // ボタンにマウスが触れたときのボタンの画像を設定
    single.setContentAreaFilled(false);                    // 背景透明化
    single.setHorizontalTextPosition(JButton.CENTER);      // 画像と文字の両方が真ん中に表示されるようにする
    single.setFont(new Font("Arial Black", Font.BOLD, 30));// ボタンの文字のフォント設定
    single.setForeground(Color.GREEN);                     // 文字の色
    single.setBorderPainted(false);                        // ボタンの枠削除
    single.setBounds(270,220,240,135);                     // ボタンの表示位置を設定

    /* 二人用ボタン */
    multi = new JButton("Multi",icon1);                   // ボタンに表示する文字とボタンの画像を設定
    multi.setRolloverIcon(icon2);                         // ボタンにマウスが触れたときのボタンの画像を設定
    multi.setContentAreaFilled(false);                    // 背景透明化
    multi.setHorizontalTextPosition(JButton.CENTER);      // 画像と文字の両方が真ん中に表示されるようにする
    multi.setFont(new Font("Arial Black", Font.BOLD, 30));// ボタンの文字のフォント設定
    multi.setForeground(Color.GREEN);                     // 文字の色
    multi.setBorderPainted(false);                        // ボタンの枠削除
    multi.setBounds(570,220,240,135);                     // ボタンの表示位置を設定

    /* 戻るボタン */
    returnButton = new JButton("<html>Return<br /><center> Title</center></html>",icon1);// ボタンに表示する文字とボタンの画像を設定
    returnButton.setRolloverIcon(icon2);                                                 // ボタンにマウスが触れたときのボタンの画像を設定
    returnButton.setContentAreaFilled(false);                                            // 背景透明化
    returnButton.setHorizontalTextPosition(JButton.CENTER);                              // 画像と文字の両方が真ん中に表示されるようにする
    returnButton.setFont(new Font("Arial Black", Font.BOLD, 30));                        // ボタンの文字のフォント設定
    returnButton.setForeground(Color.GREEN);                                             // 文字の色
    returnButton.setBorderPainted(false);                                                // ボタンの枠削除
    returnButton.setBounds(420, 370, 240, 135);                                          // ボタンの表示位置を設定

    add(returnButton);add(multi);add(single);                                            // ボタンの貼り付け
  }

  // この関数では、背景画像と文字の表示を行う
  public void paintComponent(Graphics g) {
    Graphics2D g2 = (Graphics2D)g;// Graphics2Dクラスへのキャスト
    
    
		// 文字描画のアンチエイリアシングの有効化
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    
    g.drawImage(imgBack, 0, 0, 1080, 600, null);// 背景画像を表示させる位置を指定

    Font f2 = new Font("メイリオ", Font.BOLD, 80);        // フォントを保持
    g.setFont(f2);                                    // フォントを設定
    FontMetrics fontMetrics = this.getFontMetrics(f2);// FontMetricsクラスに今使うフォントについての情報を保持
    int a=fontMetrics.stringWidth("Select Mode");     // 指定したフォントでの"Select Mode"の横幅をaに格納

    g.setColor(new Color(100,100,100));               // 色を設定
    g.drawString("Select Mode", 540-a/2+5, 195);      // 文字をaを用いてx軸に関して画面の真ん中に表示
    g.setColor(new Color(30,200,30));                 // 色を設定
    g.drawString("Select Mode", 540-a/2, 190);        // 文字をaを用いてx軸に関して画面の真ん中に表示
  }
}