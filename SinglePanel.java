import javax.swing.*;
import java.awt.*;
import java.io.File;
import javax.imageio.ImageIO;

// このクラスはシングルプレイ専用の画面を作成するクラスである
public class SinglePanel extends JPanel{
  JButton returnButton,startButton;                  // スタートするボタンと、画面をひとつ前に戻すボタンの変数
  JComboBox<Object> cb;                              // AIのレベルを選択するプルダウンメニュー
  JComboBox<Object> first;                           // 先攻、後攻を選択するプルダウンメニュー
  Image imgBack;                                     // 背景画像の変数
  SinglePanel(){
    setLayout(null);
    try {
      imgBack = ImageIO.read(new File("haikei.jpg"));// 背景画像をimgBackに読み込み
    } catch (Exception e) {
        System.out.println(e);
        System.exit(0);
    }
    ImageIcon icon1 = new ImageIcon("osero-illust7.png");// ボタン用の画像を読み込み
    ImageIcon icon2 = new ImageIcon("osero-illust8.png");// ボタン用の画像を読み込み

    // ※
    // ここではプルダウンメニューのそれぞれの項目について表示させる文字と画像をCombolabelを生成、格納し、
    // それをデフォルトコンボボックスのmdに入れて管理
    DefaultComboBoxModel<Object> md = new DefaultComboBoxModel<>();
    md.addElement(new ComboLabel("Level1", new ImageIcon("./osero-black.png")));
    md.addElement(new ComboLabel("Level2", new ImageIcon("./osero-black.png")));
    md.addElement(new ComboLabel("Level3", new ImageIcon("./osero-black.png")));

    // ※
    // 作成したデフォルトコンボボックスmdのデータモデルからコンボボックスを作成
    cb = new JComboBox<>(md);

    // ※
    ListCellRenderer<Object> renderer = new MyCellRenderer();               // MyCellRendererクラスを生成
    cb.setRenderer(renderer);                                               // 定義した表示方法をcbに反映
    cb.setFont(new Font("HGP創英角ﾎﾟｯﾌﾟ体", Font.BOLD, 40));                    // フォントを設定
    cb.setBackground(Color.WHITE);                                          // 背景を選択
    cb.setBounds(250,237,240,100);                                          // このパネルの貼り付け場所を設定

    // ※
    // ここではプルダウンメニューのそれぞれの項目について表示させる文字と画像をCombolabelを生成、格納し、
    // それをデフォルトコンボボックスのmd1に入れて管理
    DefaultComboBoxModel<Object> md1 = new DefaultComboBoxModel<>();
    md1.addElement(new ComboLabel("First", new ImageIcon("./osero-black.png")));
    md1.addElement(new ComboLabel("Second", new ImageIcon("./osero-black.png")));

    // ※
    // 作成したデフォルトコンボボックスmd1のデータモデルからコンボボックスを作成
    first = new JComboBox<>(md1);

    // ※
    ListCellRenderer<Object> renderer1 = new MyCellRenderer();               // MyCellRendererクラスを生成
    first.setRenderer(renderer1);                                            // 定義した表示方法をfirstに反映
    first.setFont(new Font("HGP創英角ﾎﾟｯﾌﾟ体", Font.BOLD, 40));                  // フォントを設定
    first.setBackground(Color.WHITE);                                        // 背景を選択
    first.setBounds(550,237,240,100);                                        // このパネルの貼り付け場所を設定

    /* startボタン */
    startButton = new JButton("Start",icon1);                   // ボタンに表示する文字とボタンの画像を設定
    startButton.setRolloverIcon(icon2);                         // ボタンにマウスが触れたときのボタンの画像を設定
    startButton.setContentAreaFilled(false);                    // 背景透明化
    startButton.setHorizontalTextPosition(JButton.CENTER);      // 画像と文字の両方が真ん中に表示されるようにする
    startButton.setFont(new Font("Arial Black", Font.BOLD, 30));// ボタンの文字のフォント設定
    startButton.setForeground(Color.GREEN);                     // 文字の色
    startButton.setBorderPainted(false);                        // ボタンの枠削除
    startButton.setBounds(550,370,240,135);                     // ボタンの表示位置を設定

    /* backボタン */
    returnButton = new JButton("Back",icon1);                    // ボタンに表示する文字とボタンの画像を設定
    returnButton.setRolloverIcon(icon2);                         // ボタンにマウスが触れたときのボタンの画像を設定
    returnButton.setContentAreaFilled(false);                    // 背景透明化
    returnButton.setHorizontalTextPosition(JButton.CENTER);      // 画像と文字の両方が真ん中に表示されるようにする
    returnButton.setFont(new Font("Arial Black", Font.BOLD, 30));// ボタンの文字のフォント設定
    returnButton.setForeground(Color.GREEN);                     // 文字の色
    returnButton.setBorderPainted(false);                        // ボタンの枠削除
    returnButton.setBounds(250, 370, 240, 135);                  // ボタンの表示位置を設定

    add(returnButton);add(startButton);add(cb);add(first);       // ボタンとコンボボックスをパネルに貼り付け
  }

  // ※
  // このクラスは文字列とアイコンの二つの情報を保持しておくクラスである。
  class ComboLabel{
    String text;
    Icon icon;

    ComboLabel(String text, Icon icon){
      this.text = text;          // テキストを保持
      this.icon = icon;          // 画像を保持
    }

    public String getText(){
      return text;               // テキストを返す
    }

    public Icon getIcon(){
      return icon;               // 画像を返す
    }
  }

  // ※
  // このクラスはコンボボックスの表示方法を自分で定義するためのクラスである。
  class MyCellRenderer extends JLabel implements ListCellRenderer<Object>{

    MyCellRenderer(){
      setOpaque(true);//背景を透明化して文字が見えるようにする
    }

    public Component getListCellRendererComponent(
            JList<? extends Object> list,
            Object value,
            int index,
            boolean isSelected,
            boolean cellHasFocus
            ){

      ComboLabel data = (ComboLabel)value;// コンボボックス内のデータを取得
      setText(data.getText());            // テキストを表示
      setIcon(data.getIcon());            // 画像を表示

      if (isSelected){                    // マウスが置かれている項目なら
        setForeground(Color.white);       // 文字の色を白にし、
        setBackground(Color.black);       // 背景の色を黒にする
      }else{                              // マウスが置かれていない項目なら
        setForeground(Color.black);       // 文字の色を黒にし、
        setBackground(Color.white);       // 背景の色を白にする。
      }

      return this;
    }
  }
  
  // この関数では、背景画像と文字の表示を行う
  public void paintComponent(Graphics g) {
    Graphics2D g2 = (Graphics2D)g;// Graphics2Dクラスへのキャスト
    
		// 文字描画のアンチエイリアシングの有効化
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    
    g.drawImage(imgBack, 0, 0, 1080, 600, null);      // 背景画像を表示させる位置を指定

    Font f2 = new Font("メイリオ", Font.BOLD, 80);        // フォントを保持
    g.setFont(f2);                                    // フォントを設定
    FontMetrics fontMetrics = this.getFontMetrics(f2);// FontMetricsクラスに今使うフォントについての情報を保持
    int a=fontMetrics.stringWidth("Select AILevel");  // 指定したフォントでの"Select AILevel"の横幅をaに格納

    g.setColor(new Color(100,100,100));               // 色を設定
    g.drawString("Select AILevel", 540-a/2+5, 195);   // 文字をaを用いてx軸に関して画面の真ん中に表示
    g.setColor(new Color(30,200,30));                 // 色を設定
    g.drawString("Select AILevel", 540-a/2, 190);     // 文字をaを用いてx軸に関して画面の真ん中に表示
  }
}