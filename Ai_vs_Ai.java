
//使い方(変える場所)
//Ai_1,Ai_2:戦うAiを指定
//FIRST_ATTACK:ai_1が先行(0)かai_1が後攻(1)か交互(2)かを決める
//trials_number:試行回数
//is_Only_Result:結果だけでよいか。途中結果もほしいならfalse
//SIZE:途中経過を表示させるときに横に何個表示させるかの設定
import java.util.*;
import java.lang.reflect.*;

class Ai_vs_Ai {
  // 設定
  // ------------------------------
  final String Ai_1 = "Ai_3", Ai_2 = "Ai_2";// 戦うAiを指定
  final int FIRST_ATTACK = 2;// 0:Ai_1が先,1:Ai_2が先,2:交互
  final int trials_number = 100;// 試行回数
  final boolean is_Only_Result = false;// 結果のみかどうか
  final int SIZE = 14;// 途中結果を表示させるときにどれだけ横に表示させるか。
  // ------------------------------

  private Model.ReversiModel reversiModel;
  private Model ai_1, ai_2;
  private int win = 0, lose = 0, draw = 0;// ai_1の勝ち数,負け数、引き分け数
  private int ai_1_color = 1;// ai_1の石の色
  private float win_rate;// 勝率
  private ArrayList<ArrayList<int[][]>> history = new ArrayList<>();// 途中経過の保存

  public Ai_vs_Ai(Model m) {
    this.reversiModel = m.getReversiModel();
    for (int i = 1; i <= trials_number; i++) {
      setAi(m, i);// 先行後攻を決める
      history.add(new ArrayList<>());// 盤面保存領域の確保
      while (reversiModel.getFinishFlag() == 0) {// 終わるまでずっと繰り返す
        if (reversiModel.getPassFlag("controller") == 0) {// ai_1が実行
          ai_1.run();
          saveBoard(i);// 盤面の保存
        }
        if (reversiModel.getFinishFlag() == 0 && reversiModel.getPassFlag("controller") == 0) {// ai_2が実行
          ai_2.run();
          saveBoard(i);// 盤面の保存
        }
      }
      if (reversiModel.countStorn(ai_1_color) > 32) {// ai_1の勝ち
        if (FIRST_ATTACK == 1 || (FIRST_ATTACK == 2 && i % 2 == 0)) {// ai_2が先頭な時に実行
          lose++;
        }else{
          win++;
        }
      } else if (reversiModel.countStorn(ai_1_color) < 32) {// ai_1の負け
        if (FIRST_ATTACK == 1 || (FIRST_ATTACK == 2 && i % 2 == 0)) {// ai_2が先頭な時に実行
          win++;
        }else{
          lose++;
        }
      } else {// 引き分け
        draw++;
      }
      System.out.println((i) + "回目--------------------------------------終了\n");
      reversiModel.initBoard();
    }
    if (!is_Only_Result) {// 途中経過を表示させるときに用いる
      for (int i = 0; i < trials_number; i++) {
        System.out.println((i + 1) + "回目--------------------------------------\n");
        display(history.get(i));// 表示
      }
    }
    win_rate = (float) win / trials_number;// 勝率の計算
    
    System.out.printf(Ai_1 + ": 勝ち[" + win + "]," + "負け[" + lose + "]," + "引き分け[" + draw + "]," + "勝率[%.3f]\n",
        win_rate);
    System.out.printf(Ai_2 + ": 勝ち[" + lose + "]," + "負け[" + win + "]," + "引き分け[" + draw + "]," + "勝率[%.3f]\n",
        1 - win_rate);
  }

  private void setAi(Model m, int n) {// aiの設定(先行後攻、なんのAiを使うのか選定)
    try {
      ai_1_color = 1;
      Class<?> aiClass1 = aiClass1 = Class.forName(Ai_1);// ai_1を先頭とする
      Class<?> aiClass2 = aiClass2 = Class.forName(Ai_2);
      if (FIRST_ATTACK == 1 || (FIRST_ATTACK == 2 && n % 2 == 0)) {// ai_2が先頭な時に実行
        aiClass1 = Class.forName(Ai_2);
        aiClass2 = Class.forName(Ai_1);
        ai_1_color = 2;
      }
      Constructor ai_constructor_1 = aiClass1.getConstructor(Model.class, int.class);// ai_1のコンストラクタの取得
      ai_1 = (Model) ai_constructor_1.newInstance(m, 1);// ai_1のaiオブジェクト作成
      Constructor ai_constructor_2 = aiClass2.getConstructor(Model.class,int.class);// ai_2のコンストラクタの取得
      ai_2 = (Model) ai_constructor_2.newInstance(m,2);

    } catch (ClassNotFoundException e) {// エラー用
      e.printStackTrace();
      System.exit(1);
    } catch (NoSuchMethodException e) {
      e.printStackTrace();
      System.exit(1);
    } catch (IllegalAccessException e) {
      e.printStackTrace();
      System.exit(1);
    } catch (InstantiationException e) {
      e.printStackTrace();
      System.exit(1);
    } catch (InvocationTargetException e) {
      e.printStackTrace();
      System.exit(1);
    }
  }

  private void saveBoard(int n) {// iはn回目が入る,盤面を記憶する
    history.get(n - 1).add(new int[8][8]);
    for (int k = 0; k < 8; k++) {
      for (int l = 0; l < 8; l++) {
        history.get(n - 1).get(history.get(n - 1).size() - 1)[k][l] = reversiModel.getBoardArray()[k][l];
      }
    }
  }

  private void display(ArrayList<int[][]> history) {
    for (int i = 0; i < history.size() / SIZE; i++) {// SIZEの数だけ横長に表示
      for (int j = 0; j < 8; j++) {
        for (int k = 1; k <= SIZE; k++) {
          for (int l = 0; l < 8; l++) {
            System.out.print("|" + history.get(i * SIZE + k - 1)[j][l]);
          }
          System.out.print("| ");
        }
        System.out.println("");
      }
      System.out.println("");
    }
    if (history.size() % SIZE != 0) {// 余った部分に関して表示する
      for (int j = 0; j < 8; j++) {
        for (int i = history.size() % SIZE; i > 0; i--) {
          for (int l = 0; l < 8; l++) {
            System.out.print("|" + history.get(history.size() - i)[j][l]);
          }
          System.out.print("| ");
        }
        System.out.println("");
      }
    }
  }

  public static void main(String argv[]) {
    // モデルの生成．
    Model model = new Model();
    new Ai_vs_Ai(model);
  }
}
