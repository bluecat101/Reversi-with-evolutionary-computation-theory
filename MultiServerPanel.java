import javax.swing.*;
import java.awt.*;
import java.io.File;
import javax.imageio.ImageIO;

//このクラスは、部屋を作る、部屋に入るのどちらかが選択されたときに合言葉を入力する画面を作成するクラスである
public class MultiServerPanel extends JPanel{
  JButton returnButton;      // 戻るボタンの変数
  JTextField password;       // 合言葉を入力するためのテキストボックス
  Image imgBack;             // 背景画像の変数
  String s="Decide Password";// String型の文字として"Decide Password"で初期化
  MultiServerPanel(){
    setLayout(null);
    try {
      imgBack = ImageIO.read(new File("haikei.jpg"));// 背景画像をimgBackに読み込み
    } catch (Exception e) {
        System.out.println(e);
        System.exit(0);
    }
    ImageIcon icon1 = new ImageIcon("osero-illust7.png");        // ボタン用の画像を読み込み
    ImageIcon icon2 = new ImageIcon("osero-illust8.png");        // ボタン用の画像を読み込み
    
    /* テキストボックス */
    password = new JTextField();                                 // テキストボックスを生成
    password.setBounds(420,250,240,100);                         // テキストボックスの表示位置を設定
    
    /* 戻るボタン */
    returnButton = new JButton("Back",icon1);                    // ボタンに表示する文字とボタンの画像を設定
    returnButton.setRolloverIcon(icon2);                         // ボタンにマウスが触れたときのボタンの画像を設定
    returnButton.setContentAreaFilled(false);                    // 背景透明化
    returnButton.setHorizontalTextPosition(JButton.CENTER);      // 画像と文字の両方が真ん中に表示されるようにする
    returnButton.setFont(new Font("Arial Black", Font.BOLD, 30));// ボタンの文字のフォント設定
    returnButton.setForeground(Color.GREEN);                     // 文字の色
    returnButton.setBorderPainted(false);                        // ボタンの枠削除
    returnButton.setBounds(420, 370, 240, 135);                  // ボタンの表示位置を設定
    
    add(returnButton);add(password);                             // ボタンとテキストボックスを貼り付け
  }

  // この関数では、背景画像と文字の表示を行う
  public void paintComponent(Graphics g) {
    Graphics2D g2 = (Graphics2D)g;// Graphics2Dクラスへのキャスト
    
		// 文字描画のアンチエイリアシングの有効化
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    
    g.drawImage(imgBack, 0, 0, 1080, 600, null);// 背景画像を表示させる位置を指定

		Font f2 = new Font("メイリオ", Font.BOLD, 80);         // フォントを保持
    g.setFont(f2);                                    // フォントを設定
    FontMetrics fontMetrics = this.getFontMetrics(f2);// FontMetricsクラスに今使うフォントについての情報を保持
    int a=fontMetrics.stringWidth(s);                 // 指定したフォントでの文字sの横幅をaに格納

    g.setColor(new Color(100,100,100));               // 色を設定
    g.drawString(s, 540-a/2+5, 195);                  // 文字をaを用いてx軸に関して画面の真ん中に表示
    g.setColor(new Color(30,200,30));                 // 色を設定
    g.drawString(s, 540-a/2, 190);                    // 文字をaを用いてx軸に関して画面の真ん中に表示
	}
}