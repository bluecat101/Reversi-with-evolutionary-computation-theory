
//使い方(変える場所)
//Ai_1,Ai_2:戦うAiを指定
//FIRST_ATTACK:ai_1が先行(0)かai_1が後攻(1)か交互(2)かを決める
//trials_number:試行回数
//select_mode:勝率のみか、最後の盤面がほしいのか、途中の盤面もほしいのか。
//SIZE:途中経過を表示させるときに横に何個表示させるかの設定

import java.util.*;
import java.lang.reflect.*;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Collections;
import java.io.FileWriter;
import java.io.IOException;

class LearnAi {
  // 設定
  // ------------------------------
  final String Ai_1 = "Ai_4", Ai_2 = "Ai_3";// 戦うAiを指定
  final int FIRST_ATTACK = 2;// 0:Ai_1が先,1:Ai_2が先,2:交互
  final int trials_number = 10;// 試行回数
  final int general_number = 10;// 世代数
  final int select_mode =2;// 0:勝率のみ,1:取った個数,2:取った個数と最後の盤面のみ,3:途中盤面の表示
  final int SIZE = 8;// 途中結果を表示させるときにどれだけ横に表示させるか。
  // ------------------------------

  private Model.ReversiModel reversiModel;
  private Model.ChatModel chatModel;
  private Model ai_1, ai_2;
  private int win = 0, lose = 0, draw = 0;// ai_1の勝ち数,負け数、引き分け数
  private int first_Ai_color = 1;// ai_1の石の色
  private float win_rate_1;// 勝率
  private float win_rate_2;// 勝率
  private ArrayList<ArrayList<int[][]>> board_history = new ArrayList<>();// 途中経過の盤面の保存
  private ArrayList<ArrayList<int[]>> position_history = new ArrayList<>();// 途中経過の置いた位置保存
  private ArrayList<int[]> result = new ArrayList<>();// 結果の保存
  private double[][] weight_board = new double[8][8];

  public LearnAi(Model m) {
    this.reversiModel = m.getReversiModel();
    this.chatModel = m.getChatModel();
    weight_board = read_weigth();
    for(int general = 0;general< general_number general++){
    for (int i = 1; i <= trials_number; i++) { // 1ゲームをn回繰り返す。
      setAi(m, i);// 先行後攻を決める
      board_history.add(new ArrayList<>());// 盤面保存領域の確保
      int witch_pass_flag=0;
      // １ゲーム
      while (reversiModel.getFinishFlag() == 0) {// 終わるまでずっと繰り返す
        if (reversiModel.getPassFlag() == 0&&witch_pass_flag!=2) {// ai_1が実行
          ai_1.run();
          saveBoard(i);// 盤面の保存
        }
        if (reversiModel.getFinishFlag() == 0 && reversiModel.getPassFlag() == 0) {// ai_2が実行
          witch_pass_flag=0;
          ai_2.run();
          if(reversiModel.getPassFlag()==1){
            witch_pass_flag=2;
          }
          saveBoard(i);// 盤面の保存
        }
        reversiModel.resetPassFlag();
      }
      int[] result_int={reversiModel.countStorn(1),0,1};//取得数と勝敗と自分の色
      if (FIRST_ATTACK == 1 || (FIRST_ATTACK == 2 && i % 2 == 0)) {//Ai_1とAi_2のどちらが先行かを考える
        result_int[0]=reversiModel.countStorn(2);
        result_int[2] = 2;
      }
      if (reversiModel.countStorn(1) > 32) {// 先行の勝ち
        if (first_Ai_color==2) {// Ai_2が先頭な時に実行
          lose++;
          result_int[1]=-1;
        }else{
          win++;
          result_int[1] = 1;
        }
      } else if (reversiModel.countStorn(1) < 32) {// 先行の負け
        if (first_Ai_color==2) {// Ai_2が先頭な時に実行
          result_int[1] = 1;
          win++;
        }else{
          result_int[1] = -1;
          lose++;
        }
      } else {// 引き分け
        result_int[1] = 0;
        draw++;
      }
      result.add(result_int);
      if(select_mode==3){//打った位置を保存する
        position_history.add(get_position_history());
      }
      chatModel.initChat();
      reversiModel.initBoard();
    }


    //------------- 重さを計算
    int[] score = {0,0}; // first_score_indexとsecond_score_indexとなる。
    if(result.size() > 1){
      if(result.get(0)[0] >result.get(1)[0]){
        score[0] = 0;
        score[1] = 1;
      }else{
        score[0] = 1;
        score[1] = 0;
      }
      // 1番目と2番目のインデックスを取得
      for(int i = 2; i < result.size(); i++){
        if(result.get(score[0])[0] < result.get(i)[0]){
          score[1] = score[0];
          score[0] = i;
        }else if(result.get(score[1])[0] < result.get(i)[0]){
          score[1] = i;
        }
      }
    }
    // System.out.println("board_history.size()"+board_history.get(0).size());
    
    
    double[][][] calculate_board_weight = new double[2][8][8];
    // int[][] second_board_weight = new int[8][8];
    for(int i = 0; i <2;i++){
      copyBoardArray(calculate_board_weight[i],weight_board);
    }
    //淘汰しないパターンも面白いかも
    for(int i = 0; i <2;i++){
      int[][] get_board = board_history.get(score[i]).get(board_history.get(score[i]).size()-1);
      int your_storn_color = result.get(score[i])[2];
      int judge_num = result.get(score[i])[2]; // get_board[i][j]と比較し一致なら掛け算、不一致なら割り算をして重さを計算するための判定材料、0ならそもそも加算しない。
      double weight = (double)result.get(score[i])[0]/((board_history.get(score[i]).size()+4)/2);
      weight = Math.floor(weight*1000)/1000;
      if(judge_num == 0){
        continue;
      }
      System.out.println("weight"+weight);
      for(int j = 0; j <8;j++){
        for(int k = 0; k <8;k++){
          if(get_board[j][k] == judge_num){ 
            calculate_board_weight[i][j][k] += (Math.floor(weight_board[j][k] * weight*1000)/1000);
          

          }else{
            calculate_board_weight[i][j][k] += (Math.floor((weight_board[j][k] / weight)*1000)/1000);
          }
            // System.out.println(calculate_board_weight[i][j][k]);

        }
      }
    }
    double[][] new_board_weight = new double[8][8];
    copyBoardArray(new_board_weight,calculate_board_weight[0]);
    for(int i = 1; i <2;i++){
      for(int j = 0; j <8;j++){
        for(int k = 0; k <8;k++){
          new_board_weight[j][k] += calculate_board_weight[i][j][k];
        }
      }
    }
    for(int i = 0; i <8;i++){
      for(int j = 0; j <8;j++){
        new_board_weight[i][j] /= 2;
      }
    }

    for(int i = 0; i <8;i++){
      for(int j = 0; j <8;j++){
        System.out.println(new_board_weight[i][j]+" ["+i+"]"+" ["+j+"]"+"<"+board_history.get(0).get(board_history.get(score[0]).size()-1)[i][j]+">");
      }
    }
    convertToCSV(new_board_weight, "weight.csv");
    }
    //--------------------
    // weight_board = read_weigth();
    // for(int i = 0; i <8;i++){
    //   for(int j = 0; j <8;j++){
    //     System.out.println(new_board_weight[i][j]+" ["+i+"]"+" ["+j+"]"+"<"+board_history.get(0).get(board_history.get(score[0]).size()-1)[i][j]+">");
    //   }
    // }

    switch(select_mode){
      case 0://何もしない
      break;
      case 1://取得した数だけ表示
        display_only_get_storn(result);
      break;
      case 2://
        // System.out.print()
        display_result_board();
        display_only_get_storn(result);
      break;
      case 3:
        display();// 表示
        display_only_get_storn(result);
      break;

    }
    
    win_rate_1 = (float) win / trials_number;// 勝率の計算
    win_rate_2 = (float) lose / trials_number;// 勝率の計算
    //勝率の表示
    System.out.printf(Ai_1 + ": 勝ち[" + win + "]," + "負け[" + lose + "]," + "引き分け[" + draw + "]," + "勝率[%.3f]\n",win_rate_1);
    System.out.printf(Ai_2 + ": 勝ち[" + lose + "]," + "負け[" + win + "]," + "引き分け[" + draw + "]," + "勝率[%.3f]\n",win_rate_2);
  }

  private void setAi(Model m, int n) {// aiの設定(先行後攻、なんのAiを使うのか選定)
  
    try {
      // ai_1を先頭とする----
      Class<?> aiClass1 = aiClass1 = Class.forName(Ai_1);
      Class<?> aiClass2 = aiClass2 = Class.forName(Ai_2);
      first_Ai_color=1;

      // ai_2が先頭な時に実行、ai_2を先頭にする----
      if (FIRST_ATTACK == 1 || (FIRST_ATTACK == 2 && n % 2 == 0)) {
        aiClass1 = Class.forName(Ai_2);
        aiClass2 = Class.forName(Ai_1);
        first_Ai_color=2;
      }
      if(aiClass1 != Class.forName("Ai_4")){// コンストラクタの引数が異なる。
        Constructor ai_constructor_1 = aiClass1.getConstructor(Model.class, int.class);// ai_1のコンストラクタの取得
        ai_1 = (Model) ai_constructor_1.newInstance(m, 1);// ai_1のaiオブジェクト作成
      }else{
        Constructor ai_constructor_1 = aiClass1.getConstructor(Model.class, int.class,double[][].class);// ai_1のコンストラクタの取得
        ai_1 = (Model) ai_constructor_1.newInstance(m, 1,weight_board);// ai_1のaiオブジェクト作成
      }
      // System.exit(0);

      if(aiClass2 != Class.forName("Ai_4")){// コンストラクタの引数が異なる。
        Constructor ai_constructor_2 = aiClass2.getConstructor(Model.class,int.class);// ai_2のコンストラクタの取得
        ai_2 = (Model) ai_constructor_2.newInstance(m,2);// ai_2のaiオブジェクト作成
      }else{
        Constructor ai_constructor_2 = aiClass2.getConstructor(Model.class,int.class,double[][].class);// ai_2のコンストラクタの取得
        ai_2 = (Model) ai_constructor_2.newInstance(m,2,weight_board);// ai_2のaiオブジェクト作成
      }
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
  private double[][] read_weigth(){
    Path path = Paths.get("./AiDataBase/weight.csv");
    double[][] weight_board = new double[8][8];
    try {
      // CSVファイルの読み込み
      List<String> lines = Files.readAllLines(path, Charset.forName("Shift-JIS"));
      for(int i = 0;i< 8 ;i++){
        String[] data = lines.get(i).split(",");
        for(int j=0; j < 8 ;j++){
          weight_board[i][j]= Double.parseDouble(data[j]);
        }
      }
    } catch (IOException e) {
      System.out.println("ファイル読み込みに失敗");
      System.exit(1);
    }
    return weight_board;
  }
  private void saveBoard(int n) {// iはn回目が入る,盤面を記憶する
    board_history.get(n - 1).add(new int[8][8]);
    for (int k = 0; k < 8; k++) {
      for (int l = 0; l < 8; l++) {
        board_history.get(n - 1).get(board_history.get(n - 1).size() - 1)[k][l] = reversiModel.getBoardArray()[k][l];
      }
    }
  }

  private void display() {//盤面を表示する
    for (int i = 0; i < trials_number; i++) {
      if(FIRST_ATTACK == 0 || (FIRST_ATTACK == 2 && i % 2 == 0)){//Ai_1,Ai_2が1,2のどちらかを置いたかを表示
        System.out.println((i + 1) + "回目--------------------------------------"+Ai_1+"が1,"+Ai_2+"が2です");
      }else{
        System.out.println((i + 1) + "回目--------------------------------------"+Ai_2+"が1,"+Ai_1+"が2です");
      }
      for (int j = 0; j < board_history.get(i).size() / SIZE; j++) {//1ゲームが終わるまでの盤面
        for(int k=0;k<SIZE;k++){//置いた位置を表示する
          if(((FIRST_ATTACK == 1 || (FIRST_ATTACK == 2 && i % 2 == 1))&&position_history.get(i).get(k+j*SIZE)[0]==1)|| ((FIRST_ATTACK == 0 || (FIRST_ATTACK == 2 && i % 2 == 0))&&position_history.get(i).get(k+j*SIZE)[0]==2)){
            System.out.print(Ai_2+"が["+position_history.get(i).get(k+j*SIZE)[1]+","+position_history.get(i).get(k+j*SIZE)[2]+"]       ");
          }else{
            System.out.print(Ai_1+"が["+position_history.get(i).get(k+j*SIZE)[1]+","+position_history.get(i).get(k+j*SIZE)[2]+"]       ");
          }
        }
        System.out.println("");
        for (int k = 0; k < 8; k++) {//行数分のループ
          for (int l = 1; l <= SIZE; l++) {//SIZEによって指定された表示における一列分の盤面
            for (int m = 0; m < 8; m++) {//1つ分の盤面
              System.out.print("|" + board_history.get(i).get(j * SIZE + l - 1)[k][m]);
            }
            System.out.print("| ");
          }
          System.out.println("");
        }
        System.out.println("");
      }

      if (board_history.get(i).size() % SIZE != 0) {// 余った部分(SIZEの数だけを表示)に関して表示する
        for(int k=0;k<board_history.get(i).size()%SIZE;k++){
          if(((FIRST_ATTACK == 1 || (FIRST_ATTACK == 2 && i % 2 == 1))&&position_history.get(i).get(k+(board_history.get(i).size()/SIZE)*SIZE)[0]==1)|| ((FIRST_ATTACK == 0 || (FIRST_ATTACK == 2 && i % 2 == 0))&&position_history.get(i).get(k+(board_history.get(i).size()/SIZE)*SIZE)[0]==2)){
            System.out.print(Ai_2+"が["+position_history.get(i).get(k+(board_history.get(i).size()/SIZE)*SIZE)[1]+","+position_history.get(i).get(k+(board_history.get(i).size()/SIZE)*SIZE)[2]+"]       ");
          }else{
            System.out.print(Ai_1+"が["+position_history.get(i).get(k+(board_history.get(i).size()/SIZE)*SIZE)[1]+","+position_history.get(i).get(k+(board_history.get(i).size()/SIZE)*SIZE)[2]+"]       ");
          }
        }      
        System.out.println("");
        for (int j = 0; j < 8; j++) {
          for (int k = board_history.get(i).size() % SIZE; k > 0; k--) {
            for (int l = 0; l < 8; l++) {
              System.out.print("|" + board_history.get(i).get(board_history.get(i).size() - k)[j][l]);
            }
            System.out.print("| ");
          }
          System.out.println("");
        }
      }

    }
  }
  private void display_only_get_storn(ArrayList<int[]> result){
    for (int i = 0; i < result.size(); i++) {
      if(result.get(i)[1]==1){//勝敗の表示
        System.out.print(Ai_1 +",[win]::");
      }else if(result.get(i)[1]==0){
        System.out.print("[draw]::");
      }else if(result.get(i)[1]==-1){
        System.out.print(Ai_1 +",[lose]::");
      }
        System.out.println(Ai_1+"の取得個数:"+result.get(i)[0]);//取得した個数を表示
    }
  }

  private void display_result_board() {//最後の盤面のみを表示する
    for (int i = 0; i < board_history.size()/SIZE; i++) {//切りのいい盤面数だけ表示させるループ
      for (int j = 0; j < 8; j++) {//行数分のループ
        for (int k = 1; k <= SIZE; k++) {// 一列分のループ
          for (int l = 0; l < 8; l++) {//一盤面分のループ
            System.out.print("|" + board_history.get(i*SIZE).get(board_history.get(i*SIZE).size()-1)[j][l]);
          }
          System.out.print("| ");
        }
        System.out.println("");
      }
      System.out.println("");
    }
    if (board_history.size() % SIZE != 0) {// 余った部分に関して表示する
      for (int j = 0; j < 8; j++) {
        for (int i =0 ; i < board_history.size() % SIZE; i++) {
          for (int l = 0; l < 8; l++) {
            System.out.print("|" + board_history.get(i).get(board_history.get(i).size() - 1)[j][l]);
          }
          System.out.print("| ");
        }
        System.out.println("");
      }
    }
  }
  private ArrayList<int[]> get_position_history(){
    ArrayList<StringBuffer> message=chatModel.getChatMessage();
    ArrayList<Integer> player=chatModel.getChatPlayer();
    ArrayList<int[]> position_history= new ArrayList<>();
    String[] xy_chat;
    for(int i =0;i<message.size();i++){
      int[] pre_history={0,0,0};
      // position_history.add(new ArrayList<>());// 盤面保存領域の確保
      xy_chat = message.get(i).toString().split("　　");// 文字列を解析
      pre_history[0]=player.get(i);
      pre_history[1]=Integer.parseInt(xy_chat[1])-1;//x座標(横)
      pre_history[2]=Integer.parseInt(xy_chat[3])-1;//y座標(縦)
      position_history.add(pre_history);    
    } 
    return position_history;
  }
  public static void main(String argv[]) {
    // モデルの生成．
    Model model = new Model();
    new LearnAi(model);
  }
  
  // 8*8配列をコピーする(第一引数:コピー先,第二引数:コピー元)
  public void copyBoardArray(int[][] target_copy, int[][] source_copy) {
    for (int i = 0; i < 8; i++) {
      for (int j = 0; j < 8; j++) {
        target_copy[i][j] = source_copy[i][j];
      }
    }
  }

  public void copyBoardArray(double[][] target_copy, int[][] source_copy) {
    for (int i = 0; i < 8; i++) {
      for (int j = 0; j < 8; j++) {
        target_copy[i][j] = (double)source_copy[i][j];
      }
    }
  }

  public void copyBoardArray(double[][] target_copy, double[][] source_copy) {
    for (int i = 0; i < 8; i++) {
      for (int j = 0; j < 8; j++) {
        target_copy[i][j] = source_copy[i][j];
      }
    }
  }
  public void convertToCSV(double[][] array, String filename) {
        try {
            //FileWriterインスタンスの生成
            FileWriter csvWriter = new FileWriter("AiDataBase/"+filename);
            //各行をループ
            for(int i=0; i<8; i++) {
              for(int j=0; j<8; j++) {
                  //バッファに追加（行の各項目をカンマ区切りで連結）
                  csvWriter.append(array[i][j]+",");
                  System.out.println(array[i][j]+",");
              }
               //バッファに追加（改行）
                csvWriter.append("\n");
            }
            //ファイルへの書き込み
            csvWriter.flush();
            //ストリームを閉じる
            csvWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
 
    }

}


// Comparator<int[]> comparator = new Comparator<int[]>() {
    //   @Override
    //   public int compare(int[] O1, int[] O2) {
    //     Integer o1 = O1[0];
    //     Integer o2 = O2[0];
    //     Integer judgement = -Integer.valueOf(o1).compareTo(Integer.valueOf(o2));
    //     return judgement;
    //   }
    // };
    // Collections.sort(result, comparator);