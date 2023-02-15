//斉藤

import java.lang.annotation.Retention;
import java.util.ArrayList;

class Ai_3 extends Model {
  // final boolean is_maxcount = false;// 相手が置く際小数で判定する
  final int future_hand_num = 5;// 何手先まで読むのか(例：自->相手->自分　=> 3手)相手が置いた際にその手を評価して結果を出す。常に奇数で
  final float coefficient_a = 10/10;
  final float coefficient_b = 2;
  private int player;
  private int board_size;
  private Model.ReversiModel reversiModel;// 実際に動かすmodel
  private Model.ReversiModel virtualModel;// 仮想的に動かして様子を見るmodel
  private Model.ChatModel chatModel;// chatmodel
  private int[][] back_judge_array;
  
  // コンストラクタ
  public Ai_3(Model m,int aiPlayer) {
    reversiModel = m.getReversiModel();
    chatModel = m.getChatModel();
    virtualModel = (new Model()).getReversiModel();
    this.player = aiPlayer;// playerの指定
    this.board_size = reversiModel.board_size;// 盤面のサイズを指定。
  }

  // 呼び出される関数
  public void run() {// みんなのプログラムを追加する。
    // 再帰のことも考え、今の盤面を入れる配列を関数内で作成する。
    int[][] back_board_array = new int[board_size][board_size];
    // 今の盤面をコピーする
    getBackBoardArray(back_board_array);
    // virtualの盤面を合わせる。
    virtualModel.setBoard_onlyAI(back_board_array);
    virtualModel.setPlayer(this.player);
    // System.out.println("start------------------------------");
    float[] storn_position = recursive_run(back_board_array, future_hand_num  - 1);
    // System.out.println("end------------------------------");
    chatModel.writeHistroy((int)storn_position[0], (int)storn_position[1], reversiModel.getIsYourTurn());// 履歴に書く。
    reversiModel.xySetStone((int)storn_position[0], (int)storn_position[1]);
  }

  public float[] recursive_run(int[][] parent_board, int n) {// 第一返り値:x,第二返り値:y,第二返り値:ひっくり返す数,
    int[][] board = new int[board_size][board_size];
    copyBoardArray(board, parent_board);// 親のboardをコピー
    // can_put_positionで必要な変数
    back_judge_array = virtualModel.getJudgeBoardArray(virtualModel.getPlayer());
    ArrayList<int[]> can_put_position = getCanPutArray(back_judge_array);// 置ける位置を配列に格納する
    int length = can_put_position.size();// 置ける位置の個数を数える
    float[] result = { can_put_position.get(0)[0], can_put_position.get(0)[1], -1 };
    // System.out.println(n);
    if (n == 1) {// 結果を出す
      // int min_over_storn = 100;// 最小のひっくり返す個数の数を記憶
      float max_evaluation = 0;// 
      int good_position = 0;// 良い手の座標
      int storn_num = virtualModel.countStorn(opponentPlayer());
      // System.out.println("length["+length+"]");
      for (int i = 0; i < length; i++) {
        // System.out.println("start");
        // print_board(board);
        virtualModel.setPlayer(opponentPlayer());
        // int pre_min_over_storn = over_search(can_put_position.get(i)[0], can_put_position.get(i)[1], board,virtualModel.getOpponentStone(player));
        virtualModel.xySetStone(can_put_position.get(i)[0], can_put_position.get(i)[1]);// 仮想空間の盤面に置く
        int pre_min_over_storn=virtualModel.countStorn(opponentPlayer())-storn_num-1;
        float evaluation=(1/pre_min_over_storn)*coefficient_a;
        evaluation += (1.0 / length)*coefficient_b;
        if(can_put_position.get(i)[0]==1&& (1<can_put_position.get(i)[1]&& can_put_position.get(i)[1]<6)){
          // System.out.println("left");
          for(int j=-1;j<2;j++){
            if(search(j+can_put_position.get(i)[0], can_put_position.get(i)[1]-1, this.player)){
              evaluation++;
            }
          }
        }else if(can_put_position.get(i)[0]==6&& (1 < can_put_position.get(i)[1] && can_put_position.get(i)[1] < 6)){
          // System.out.println("right");
          for(int j=-1;j<2;j++){
            if(search(j+can_put_position.get(i)[0], can_put_position.get(i)[1]+1, this.player)){
              evaluation++;
            }
          }
        }else if(can_put_position.get(i)[1]==1&& (1 < can_put_position.get(i)[0] && can_put_position.get(i)[0] < 6)){
          // System.out.println("up");
          for(int j=-1;j<2;j++){
            if(search(can_put_position.get(i)[0]-1,j+can_put_position.get(i)[1], this.player)){
              evaluation++;
            }
          }
        }else if(can_put_position.get(i)[1]==6&& (1 < can_put_position.get(i)[0] && can_put_position.get(i)[0] < 6)){
          // System.out.println("down");
          for(int j=-1;j<2;j++){
            if(search( can_put_position.get(i)[0]+1, j+can_put_position.get(i)[1], this.player)){
              evaluation++;
            }
          }
        }else if(can_put_position.get(i)[0]==1&&can_put_position.get(i)[1]==1&&search( 0, 0, this.player)) {
        //left up
        // System.out.println("left up");
        evaluation+=3;
        // print_board(board);
        } else if (can_put_position.get(i)[0] == 1 && can_put_position.get(i)[1] == 6&&search( 0, 7, this.player)) {
        //right up
        // System.out.println("right up");
        evaluation+=3;
        } else if (can_put_position.get(i)[0] == 6 && can_put_position.get(i)[1] == 1&&search( 7, 0, this.player)) {
        // left down
        // System.out.println(" left down");
        evaluation+=3;
        } else if (can_put_position.get(i)[0] == 6 && can_put_position.get(i)[1] == 6&&search( 7, 7, this.player)) {
        // right down
        // System.out.println(" right down");
        evaluation+=3;
        }else if(can_put_position.get(i)[0] == 0 && can_put_position.get(i)[1] == 0&& !search(0, 0, opponentPlayer())){
          evaluation += 4;
          // System.out.println("left up");
          // print_board(board);
        } else if (can_put_position.get(i)[0] == 0 && can_put_position.get(i)[1] == 7&& !search(0, 7, opponentPlayer())) {
          evaluation += 4;
          // System.out.println("right up");
          // print_board(board);
        } else if (can_put_position.get(i)[0] == 7 && can_put_position.get(i)[1] == 0&& !search(7, 0, opponentPlayer())) {
          evaluation += 4;
          // System.out.println(" left down");
          // print_board(board);
        } else if (can_put_position.get(i)[0] == 7 && can_put_position.get(i)[1] == 7&& !search(7, 7, opponentPlayer())) {
          evaluation += 4;
          // System.out.println(" right down");
          // print_board(board);
        }
        evaluation+= evaluation_for_present_board(can_put_position.get(i)[0],can_put_position.get(i)[1]);
        // System.out.println("evaluuation"+evaluation);
        if(evaluation<0){
          // System.out.print("check"+evaluation+"    ");
          evaluation=1;
          evaluation=evaluation/100;
        }
        if (max_evaluation< evaluation||(max_evaluation == evaluation&&((int) (Math.random() * 2) == 1))) {// 現状の最善手なら良い手として記憶する
          max_evaluation= evaluation;
          good_position = i;
        }
        virtualModel.setBoard_onlyAI(board);// 仮想空間の盤面を現在の再帰の盤面に合わせる。(再帰内での初期化)
      }
      // System.out.println("max" + max_evaluation);
      //相手が置けるマスの数を計算
      // max_evaluation+=1.0/length;
      // returnように記憶する
      result[0] = (float)can_put_position.get(good_position)[0];
      result[1] = (float)can_put_position.get(good_position)[1];
      result[2] = max_evaluation;// 最小のひっくり返す数の記憶
      // System.out.println("end"+result[2]);

    } else {
      float[] pre_result = { can_put_position.get(0)[0], can_put_position.get(0)[1], -1 };// 仮の座標の記憶
      for (int i = 0; i < length; i++) {
        if (n % 2 == 0) {// 仮想空間上の盤面の置くplayerの指定
          virtualModel.setPlayer(this.player);
        } else {
          virtualModel.setPlayer(opponentPlayer());
        }
        virtualModel.setBoard_onlyAI(board);// 仮想空間の盤面を現在の再帰の盤面に合わせる。(再帰内での初期化)
        virtualModel.xySetStone(can_put_position.get(i)[0], can_put_position.get(i)[1]);// 仮想空間の盤面に置く
        if ((virtualModel.getPassFlag("Ai") == 1 && n % 2 == 1)
            || (virtualModel.getFinishFlag("Ai") == 1 && n % 2 == 1)) {// もし相手が置くときにpassまたはfinishかの判定
          virtualModel.getPassFlag("controller");// passflagを初期化
          virtualModel.getFinishFlag();// finishflagを初期化
          continue;// 最悪手と認定
        } else if (virtualModel.getPassFlag("Ai") == 1 && n % 2 == 0) {// もし自分が置いてpassとなったなら
          virtualModel.getPassFlag("controller");
          result[0] = (float)can_put_position.get(i)[0];
          result[1] = (float)can_put_position.get(i)[1];
          result[2] = (float)155;// 最大の優先度
          // System.out.println("good hand");
        } else if (virtualModel.getFinishFlag("Ai") == 1 && n % 2 == 0) {// もし自分が置いてfinishとなったなら
          virtualModel.getFinishFlag();
          result[0] = (float)can_put_position.get(i)[0];
          result[1] = (float)can_put_position.get(i)[1];
          result[2] = (float)156;// passよりも高い優先度
          // System.out.println("parfect hand");
        } else {// pass,finishではない
          // System.out.println("start recursive"+i+","+length);
          pre_result = recursive_run(virtualModel.getBoardArray(), n - 1);// 再帰する
          // System.out.println("end recursive");

        }
          // System.out.println("result[2]" + result[2]+","+ "pre_result" + pre_result[2]+","+ "length" + length+ "," + "i" + i);
          if (result[2] < pre_result[2]) {//評価関数が高い
            result[0] = (float)can_put_position.get(i)[0];
            result[1] = (float)can_put_position.get(i)[1];
            result[2] = (float)pre_result[2];
          }

      }
    }
    // System.out.println("result[0]" + result[0] + "," + "result[1]" + result[1] + ","+ "result[2]" + result[2] + ","+n);
    return result;
  }

  // 現在の盤面のコピー
  public void getBackBoardArray(int[][] back_board_array) {
    copyBoardArray(back_board_array, reversiModel.getBoardArray());
  }

  // 8*8配列をコピーする(第一引数:コピー先,第二引数:コピー元)
  public void copyBoardArray(int[][] target_copy, int[][] source_copy) {
    for (int i = 0; i < board_size; i++) {
      for (int j = 0; j < board_size; j++) {
        target_copy[i][j] = source_copy[i][j];
      }
    }
  }

  // 置ける位置を配列に格納する。
  public ArrayList<int[]> getCanPutArray(int[][] targetBoard) {// board_jadge_arryaに直すと思う
    ArrayList<int[]> can_put_position = new ArrayList<>();
    for (int i = 0; i < board_size; i++) {
      for (int j = 0; j < board_size; j++) {
        if (targetBoard[i][j] == 3) {
          int[] k = { i, j };
          can_put_position.add(k);
        }
      }
    }
    return can_put_position;
  }

  private void print_board(int[][] board){
      for (int j = 0; j < 8; j++) {
          for (int l = 0; l < 8; l++) {
            System.out.print("|" + board[j][l]);
          }
          System.out.println("|");
        }
      }
  private boolean search(int x,int y,int player){
    return virtualModel.getBoardArray()[x][y]==0&&virtualModel.pre_search(x, y,player);
  }
  private int opponentPlayer(){
    return virtualModel.getOpponentStone(this.player);
  }
  private int evaluation_for_present_board(int x, int y){
    int evaluation =0;
      String ij;
    for(int i=0;i<8;i++){
      for (int j = 0; j < 8; j++) {
        ij=i+""+j;
        if(virtualModel.getBoardArray()[i][j] == this.player){
            if(ij=="00"||ij=="07"||ij=="70"||ij=="77"){
              evaluation+=7;
            }else if(i==0||i==7||j==0||j==7){
              evaluation += 0.7;
            }
        }else if(virtualModel.getBoardArray()[i][j] == opponentPlayer()){
          if (ij == "00" || ij == "07" || ij == "70" || ij == "77") {
            evaluation -= 4;
          } else if (i == 0 || i == 7 || j == 0 || j == 7) {
            evaluation -= 0.3;
          }
        }
      }
    }
    return evaluation;
  }
}
