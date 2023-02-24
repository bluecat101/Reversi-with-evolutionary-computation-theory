import javax.swing.*;
import java.awt.*;
import java.io.File;
import javax.imageio.ImageIO;

// このクラスはタイトル画面を作成するクラスである。
class TitlePanel extends JPanel {
  JButton start,setting,finish;// スタート、操作方法、フィニッシュボタンの変数
  Image imgBack;// 背景画像の変数
  TitlePanel() {
    this.setLayout(null);
    ImageIcon icon1 = new ImageIcon("osero-illust7.png");// ボタン用の画像を読み込み
    ImageIcon icon2 = new ImageIcon("osero-illust8.png");// ボタン用の画像を読み込み

    /* スタートボタン */
    start = new JButton("Start", icon1);                    // ボタンに表示する文字とボタンの画像を設定
    start.setRolloverIcon(icon2);                           // ボタンにマウスが触れたときのボタンの画像を設定
    start.setContentAreaFilled(false);                      // 背景透明化
    start.setHorizontalTextPosition(JButton.CENTER);        // 画像と文字の両方が真ん中に表示されるようにする
    start.setFont(new Font("Arial Black", Font.BOLD, 33));  // ボタンの文字のフォント設定
    start.setForeground(Color.GREEN);                       // 文字の色
    start.setBorderPainted(false);                          // ボタンの枠削除
    start.setBounds(414,370,240,135);                    // ボタンの表示位置を設定
    
    /* 操作方法ボタン */
    setting = new JButton("Rule", icon1);                   // ボタンに表示する文字とボタンの画像を設定
    setting.setRolloverIcon(icon2);                         // ボタンにマウスが触れたときのボタンの画像を設定
    setting.setContentAreaFilled(false);                    // 背景透明化
    setting.setHorizontalTextPosition(JButton.CENTER);      // 画像と文字の両方が真ん中に表示されるようにする
    setting.setFont(new Font("Arial Black", Font.BOLD, 33));// ボタンの文字のフォント設定
    setting.setForeground(Color.GREEN);                     // 文字の色
    setting.setBorderPainted(false);                        // ボタンの枠削除
    setting.setBounds(82,370,240,135);                   // ボタンの表示位置を設定

    /* フィニッシュボタン */
    finish = new JButton("Finish", icon1);                  // ボタンに表示する文字とボタンの画像を設定
    finish.setRolloverIcon(icon2);                          // ボタンにマウスが触れたときのボタンの画像を設定
    finish.setContentAreaFilled(false);                     // 背景透明化
    finish.setHorizontalTextPosition(JButton.CENTER);       // 画像と文字の両方が真ん中に表示されるようにする
    finish.setFont(new Font("Arial Black", Font.BOLD, 33)); // ボタンの文字のフォント設定
    finish.setForeground(Color.GREEN);                      // 文字の色
    finish.setBorderPainted(false);                         // ボタンの枠削除
    finish.setBounds(746,370,240,135);                   // ボタンの表示位置を設定

    try {
      imgBack = ImageIO.read(new File("vs.jpg"));           // 背景画像をimgBackに読み込み
    } catch (Exception e) {
        System.out.println(e);
        System.exit(0);
    }

    add(start);add(setting);add(finish);                    // ボタンを貼り付け
    this.setVisible(true);
  }

  // この関数では、背景画像と文字の表示、オセロの盤面を模した模様の描画を行う
  public void paintComponent(Graphics g) {
    super.paintComponent(g);
		Graphics2D g2 = (Graphics2D)g;// Graphics2Dクラスへのキャスト

		// 図形や線のアンチエイリアシングの有効化
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
 
		// 文字描画のアンチエイリアシングの有効化
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    
    g.drawImage(imgBack, 0, -80, 1080, 680, null);// 背景画像を表示させる位置を指定
    
    // 以下の描画の命令群はタイトル画面のオセロの盤面を模した模様を描画するためのものである
    for(int i=0;i<141;i++){
      g.setColor(new Color(0,0,0,210-(int)(i*1.5)));// ここで黒の透明度を変化させて、
      g.fillRect(400+i,320,1,40);                // これより下の操作で徐々に黒の透明度を高くしながら背景画像のVSの文字に黒い線かぶらないようにしていく。
      g.fillRect(680-i,320,1,40);
      if(i<88||i>140){
        g.fillRect(400+i,200,1,30);
        g.fillRect(680-i,200,1,30);
      }
    }

    g.setColor(new Color(0,0,0,210));// 色を設定
    // 以下では横線を引いている
    g.fillRect(0,520,1080,60);
    g.fillRect(0,320,400,40);
    g.fillRect(680,320,1080,40);
    g.fillRect(0,200,400,30);
    g.fillRect(680,200,1080,30);
    g.fillRect(0,120,1080,20);
    g.fillRect(0,70,1040,10);
    g.fillRect(5,37,940,5);

    // 以下では斜めの線を引いている
    int xpoint[] = {505,520,350,270};                          // 斜めの線を引くにあたってx座標の四隅を指定
    int ypoint[] = {0,0,600,600};                              // 斜めの線を引くにあたってy座標の四隅を指定
    for(int j=0; j<2; j++){
      g.fillPolygon(xpoint,ypoint,4);                          // 斜めの線を引く
      for(int i=0; i<4; i++){xpoint[i] = (1080 - xpoint[i]);}  // 線対称となる座標を計算してこのfor文の二回目で描画
    }

    int xpoint1[] = {425,440,0,-80};                           // 斜めの線を引くにあたってx座標の四隅を指定
    int ypoint1[] = {0,0,460,460};                             // 斜めの線を引くにあたってy座標の四隅を指定
    for(int j=0; j<2; j++){
      g.fillPolygon(xpoint1,ypoint1,4);                        // 斜めの線を引く
      for(int i=0; i<4; i++){xpoint1[i] = (1080 - xpoint1[i]);}// 線対称となる座標を計算してこのfor文の二回目で描画
    }

    int xpoint2[] = {345,360,0,-80};                           // 斜めの線を引くにあたってx座標の四隅を指定
    int ypoint2[] = {0,0,200,200};                             // 斜めの線を引くにあたってy座標の四隅を指定
    for(int j=0; j<2; j++){
      g.fillPolygon(xpoint2,ypoint2,4);                        // 斜めの線を引く
      for(int i=0; i<4; i++){xpoint2[i] = (1080 - xpoint2[i]);}// 線対称となる座標を計算してこのfor文の二回目で描画
    }

    int xpoint4[] = {265,280,0,-80};                           // 斜めの線を引くにあたってx座標の四隅を指定
    int ypoint4[] = {0,0,100,100};                             // 斜めの線を引くにあたってy座標の四隅を指定
    for(int j=0; j<2; j++){
      g.fillPolygon(xpoint4,ypoint4,4);                        // 斜めの線を引く
      for(int i=0; i<4; i++){xpoint4[i] = (1080 - xpoint4[i]);}// 線対称となる座標を計算してこのfor文の二回目で描画
    }

    int xpoint3[] = {205,220,0,-150};                          // 斜めの線を引くにあたってx座標の四隅を指定
    int ypoint3[] = {0,0,40,40};                               // 斜めの線を引くにあたってy座標の四隅を指定
    g.fillPolygon(xpoint3,ypoint3,4);                          // 斜めの線を引く

    Font f2 = new Font("HGP創英角ﾎﾟｯﾌﾟ体", Font.BOLD, 100);// フォントを保持
    g.setFont(f2);                    // フォントを設定

    g.setColor(new Color(0,0,255));   // 色を設定
    g.drawString("REVERSI", 355, 155);// 文字を表示
    g.setColor(new Color(255,0,0));   // 色を設定
    g.drawString("REVERSI", 350, 150);// 文字を表示
  }
}