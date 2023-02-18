//斉藤

import java.lang.annotation.Retention;
import java.util.ArrayList;

class Ai_3 extends Model {
  // final boolean is_maxcount = false;// 相手が置く際小数で判定する
  final int future_hand_num = 3;// 何手先まで読むのか(例：自->相手->自分　=> 3手)相手が置いた際にその手を評価して結果を出す。常に奇数で
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
    float[] result = { can_put_position.get(0)[0], can_put_position.get(0)[1],-1};
    // System.out.println(n);
    ArrayList<Float> evaluation=new ArrayList<Float>();// 置ける位置を配列に格納する
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
        // float[] aa={(1/pre_min_over_storn)*coefficient_a};
        evaluation.add((1/pre_min_over_storn)*coefficient_a);
        // System.out.println(evaluation.get(0));
        evaluation.set(i,((1/pre_min_over_storn)*coefficient_a));
        // evaluation.get(i)=((1/pre_min_over_storn)*coefficient_a);
        evaluation.set(i,evaluation.get(i)+(float)((1.0 / length)*coefficient_b));
        // evaluation.get(i)+= (1.0 / length)*coefficient_b;
        // evaluation += (1.0 / length)*coefficient_b;
        if(can_put_position.get(i)[0]==1&& (1<can_put_position.get(i)[1]&& can_put_position.get(i)[1]<6)){
          // System.out.println("left");
          for(int j=-1;j<2;j++){
            if(search(j+can_put_position.get(i)[0], can_put_position.get(i)[1]-1, this.player)){
              // evaluation.get(i)++;
              evaluation.set(i,evaluation.get(i)+evaluation.get(i));
            }
          }
        }else if(can_put_position.get(i)[0]==6&& (1 < can_put_position.get(i)[1] && can_put_position.get(i)[1] < 6)){
          // System.out.println("right");
          for(int j=-1;j<2;j++){
            if(search(j+can_put_position.get(i)[0], can_put_position.get(i)[1]+1, this.player)){
              // evaluation.get(i)++;
              evaluation.set(i,evaluation.get(i)+evaluation.get(i));
            }
          }
        }else if(can_put_position.get(i)[1]==1&& (1 < can_put_position.get(i)[0] && can_put_position.get(i)[0] < 6)){
          // System.out.println("up");
          for(int j=-1;j<2;j++){
            if(search(can_put_position.get(i)[0]-1,j+can_put_position.get(i)[1], this.player)){
              // evaluation.get(i)++;
              evaluation.set(i,evaluation.get(i)+evaluation.get(i));
            }
          }
        }else if(can_put_position.get(i)[1]==6&& (1 < can_put_position.get(i)[0] && can_put_position.get(i)[0] < 6)){
          // System.out.println("down");
          for(int j=-1;j<2;j++){
            if(search( can_put_position.get(i)[0]+1, j+can_put_position.get(i)[1], this.player)){
              // evaluation.get(i)++;
              evaluation.set(i,evaluation.get(i)+evaluation.get(i));
            }
          }
        }else if(can_put_position.get(i)[0]==1&&can_put_position.get(i)[1]==1&&search( 0, 0, this.player)) {
        //left up
        // System.out.println("left up");
        // evaluation.get(i)+=3;
        evaluation.set(i,evaluation.get(i)+3);
        // print_board(board);
        } else if (can_put_position.get(i)[0] == 1 && can_put_position.get(i)[1] == 6&&search( 0, 7, this.player)) {
        //right up
        // System.out.println("right up");
        // evaluation.get(i)+=3;
        evaluation.set(i,evaluation.get(i)+3);
        } else if (can_put_position.get(i)[0] == 6 && can_put_position.get(i)[1] == 1&&search( 7, 0, this.player)) {
        // left down
        // System.out.println(" left down");
        // evaluation.get(i)+=3;
        evaluation.set(i,evaluation.get(i)+3);
        } else if (can_put_position.get(i)[0] == 6 && can_put_position.get(i)[1] == 6&&search( 7, 7, this.player)) {
        // right down
        // System.out.println(" right down");
        // evaluation.get(i)+=3;
        evaluation.set(i,evaluation.get(i)+3);
        }else if(can_put_position.get(i)[0] == 0 && can_put_position.get(i)[1] == 0&& !search(0, 0, opponentPlayer())){
          // evaluation.get(i) += 4;
          evaluation.set(i,evaluation.get(i)+4);
          // System.out.println("left up");
          // print_board(board);
        } else if (can_put_position.get(i)[0] == 0 && can_put_position.get(i)[1] == 7&& !search(0, 7, opponentPlayer())) {
          // evaluation.get(i) += 4;
          evaluation.set(i,evaluation.get(i)+4);
          // System.out.println("right up");
          // print_board(board);
        } else if (can_put_position.get(i)[0] == 7 && can_put_position.get(i)[1] == 0&& !search(7, 0, opponentPlayer())) {
          // evaluation.get(i) += 4;
          evaluation.set(i,evaluation.get(i)+4);
          // System.out.println(" left down");
          // print_board(board);
        } else if (can_put_position.get(i)[0] == 7 && can_put_position.get(i)[1] == 7&& !search(7, 7, opponentPlayer())) {
          // evaluation.get(i) += 4;
          evaluation.set(i,evaluation.get(i)+4);
          // System.out.println(" right down");
          // print_board(board);
        }
        // evaluation.get(i)+= evaluation_for_present_board(can_put_position.get(i)[0],can_put_position.get(i)[1]);
        evaluation.set(i,evaluation.get(i)+evaluation_for_present_board(can_put_position.get(i)[0],can_put_position.get(i)[1]));
        virtualModel.setBoard_onlyAI(board);// 仮想空間の盤面を現在の再帰の盤面に合わせる。(再帰内での初期化)
      }
      int max_evaluation_index=0;
      int min_evaluation_index=0;
      for(int j =1;j<evaluation.size()-1;j++){
        // System.out.print(evaluation.get(j)+",");
        if(evaluation.get(max_evaluation_index)<evaluation.get(j)){
          max_evaluation_index=j;
        }
        if(evaluation.get(min_evaluation_index)>evaluation.get(j)){
          min_evaluation_index=j;
        }
      }
      
      //   System.out.println("");
      // System.out.println(evaluation.get(max_evaluation_index)+",,"+evaluation.get(min_evaluation_index));
      if(evaluation.get(min_evaluation_index)<0){
        result[2] = evaluation.get(max_evaluation_index);// 最小のひっくり返す数の記憶
      }else{
        result[2] = evaluation.get(max_evaluation_index)+(float)(evaluation.get(min_evaluation_index)*1.1);// 最小のひっくり返す数の記憶
      }
      // System.out.println("result"+result[2]);
    } else {
      float[] pre_result = { can_put_position.get(0)[0], can_put_position.get(0)[1], -1 };// 仮の座標の記憶
      for (int i = 0; i < length; i++) {
        // System.out.print("["+can_put_position.get(i)[0]+","+can_put_position.get(i)[1]+"]");
        evaluation.add((float)0);
        if (n % 2 == 0) {// 仮想空間上の盤面の置くplayerの指定
          virtualModel.setPlayer(this.player);
        } else {
          virtualModel.setPlayer(opponentPlayer());
        }
        virtualModel.setBoard_onlyAI(board);// 仮想空間の盤面を現在の再帰の盤面に合わせる。(再帰内での初期化)
        if(virtualModel.getPlayer()==this.player){
          evaluation.set(i,evaluation.get(i)+evaluation_for_next_position(can_put_position.get(i)[0], can_put_position.get(i)[1],virtualModel.getPlayer()));
        }else{
          evaluation.set(i,evaluation.get(i)-evaluation_for_next_position(can_put_position.get(i)[0], can_put_position.get(i)[1],virtualModel.getPlayer()));
        }
        virtualModel.xySetStone(can_put_position.get(i)[0], can_put_position.get(i)[1]);// 仮想空間の盤面に置く
        if ((virtualModel.getPassFlag("Ai") == 1 && n % 2 == 1)
            || (virtualModel.getFinishFlag("Ai") == 1 && n % 2 == 1)) {// もし相手が置くときにpassまたはfinishかの判定
          virtualModel.getPassFlag("controller");// passflagを初期化
          virtualModel.getFinishFlag();// finishflagを初期化
          continue;// 最悪手と認定
        } else if (virtualModel.getPassFlag("Ai") == 1 && n % 2 == 0) {// もし自分が置いてpassとなったなら
          virtualModel.getPassFlag("controller");
          evaluation.set(i,(float)155);
        } else if (virtualModel.getFinishFlag("Ai") == 1 && n % 2 == 0) {// もし自分が置いてfinishとなったなら
          virtualModel.getFinishFlag();
          evaluation.set(i,(float)156);
        } else {// pass,finishではない
          pre_result = recursive_run(virtualModel.getBoardArray(), n - 1);// 再帰する
          evaluation.set(i,evaluation.get(i)+pre_result[2]);
        }
      }
      if(n+1==future_hand_num){
          // System.out.println(n);
         int max_evaluation_index=0;
          for(int j =1;j<evaluation.size()-1;j++){
            // System.out.print("["+evaluation.get(j)+"],");
            if(evaluation.get(max_evaluation_index)<evaluation.get(j)){
              max_evaluation_index=j;
            }
          }
          // System.out.println("");
          // System.out.println(evaluation.get(max_evaluation_index)+"["+can_put_position.get(max_evaluation_index)[0]+"]"+"["+can_put_position.get(max_evaluation_index)[1]+"]");
          result[0]=can_put_position.get(max_evaluation_index)[0];
          result[1]=can_put_position.get(max_evaluation_index)[1];
          result[2]=evaluation.get(max_evaluation_index);
        }else{
          int max_evaluation_index=0;
          int min_evaluation_index=0;
            // System.out.println(i+"回目-------("+evaluation.size()+")");
          for(int j =1;j<evaluation.size()-1;j++){
            if(evaluation.get(max_evaluation_index)<evaluation.get(j)){
              max_evaluation_index=j;
            }
            if(evaluation.get(min_evaluation_index)>evaluation.get(j)){
              min_evaluation_index=j;
            }
            // System.out.println(evaluation.get(j));
          }
          if(evaluation.get(min_evaluation_index)<0){
            result[2] = evaluation.get(max_evaluation_index);// 最小のひっくり返す数の記憶
          }else{
            result[2] = evaluation.get(max_evaluation_index)+(float)(evaluation.get(min_evaluation_index)*1.1);// 最小のひっくり返す数の記憶
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
        // System.out.println(ij);
        if(virtualModel.getBoardArray()[i][j] == this.player){
            if(ij.equals("00")||ij.equals("07")||ij.equals("70")||ij.equals("77")){
              // System.out.println("00000000000000000000000000000000000000000000000000000000"); 
              evaluation+=10;
            }else if(i==0||i==7||j==0||j==7){
              evaluation += 3;
            }
        }else if(virtualModel.getBoardArray()[i][j] == opponentPlayer()){
          if (ij.equals("00")  || ij.equals("07")  || ij.equals("70")  || ij.equals("77") ) {
            // System.out.println("11111111111111111111111111111111111111111111111111111111"); 
            evaluation -= 7;
          } else if (i == 0 || i == 7 || j == 0 || j == 7) {
            evaluation -= 0.3;
          }
        }
      }
    }
    return evaluation;
  }
  private float evaluation_for_next_position(int x,int y,int player){
    float evaluation=0;
    if((x==0&&y==1)){
      evaluation+=evalu_edge(x,y,"down");
    }else if(x==1&&y==0){
      //right
      evaluation+=evalu_edge(x,y,"right");
    }else if(x==0&&y==6){  
      //up
      evaluation+=evalu_edge(x,y,"up");
    }else if(x==1&&y==7){
      //right
      evaluation+=evalu_edge(x,y,"right");
    }else if(x==6&&y==0){  
      //left
      evaluation+=evalu_edge(x,y,"left");
    }else if(x==7&&y==1){
      //down
      evaluation+=evalu_edge(x,y,"down");
    }else if(x==6&&y==7){  
      //left
      evaluation+=evalu_edge(x,y,"left");
    }else if(x==7&&y==6){
      //up
      evaluation+=evalu_edge(x,y,"up");
    }
    return evaluation;
  }
  private int evalu_edge( int x,int y,String direction){
    int evaluation=0;
    if((getPosition_for_evalu_edge(x, y,direction,0)==player&&getPosition_for_evalu_edge(x, y,direction,2)!=0)||(getPosition_for_evalu_edge(x, y,direction,0)==opponentPlayer()&&getPosition_for_evalu_edge(x, y,direction,2)==player)){
      // System.out.println("1-3");
      for(int i=3;i<8;i++){
        if(getPosition_for_evalu_edge(x, y,direction,i)==0){
          //取られても大丈夫
          evaluation-=1;
          break;
        }
        if(i==7){
          //置ける場所がない
          evaluation+=4;
        }
      }
    }else if(getPosition_for_evalu_edge(x, y,direction,0)==opponentPlayer()&&getPosition_for_evalu_edge(x, y,direction,2)==opponentPlayer()){
      // System.out.println("4");
      for(int i=3;i<8;i++){
        if(getPosition_for_evalu_edge(x, y,direction,i)==0){
          //取られても大丈夫
          evaluation+=7;
          break;
        }else{
          if(getPosition_for_evalu_edge(x, y,direction,i)==opponentPlayer()){
            while(i<8&&getPosition_for_evalu_edge(x, y,direction,i)==opponentPlayer()){
              i++;
            }
          }
          for(;i<8;i++){//下へ
            if(getPosition_for_evalu_edge(x, y,direction,i)==0){
              evaluation-=7;
              break;
            }
            if(i==7){
              evaluation+=3;

            }
          }
          break;
        }
        
      }
    }
    return evaluation;
  }
  private int getPosition_for_evalu_edge(int x,int y,String direction,int position){
    int direct_num=0;
    if(direction.equals("up")||direction.equals("right")){
      direct_num=7;
    }else{
      direct_num=0;
    }
    if(direction.equals("up")||direction.equals("down")){
      return virtualModel.getBoardArray()[x][Math.abs(direct_num-position)];
    }else{
      return virtualModel.getBoardArray()[Math.abs(direct_num-position)][y];
    }
  }
}
