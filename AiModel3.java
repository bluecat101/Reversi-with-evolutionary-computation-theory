//斉藤
import java.util.ArrayList;

class Ai_3 extends Model {
  // final boolean is_maxcount = false;// 相手が置く際小数で判定する
  final int future_hand_num = 3;// 何手先まで読むのか(例：自->相手->自分　=> 3手)相手が置いた際にその手を評価して結果を出す。常に奇数で
  final float coefficient_a = 20/1;
  final float coefficient_b = 20/1;
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
    int[][] back_board_array = new int[board_size][board_size];
    // reversiModelの盤面をvirtualModelへとコピーする
    copyBoardArray(back_board_array, reversiModel.getBoardArray());
    copyBoardArray(virtualModel.getBoardArray(), back_board_array);// 仮想空間の盤面を現在の再帰の盤面に合わせる。(再帰内での初期化)
    virtualModel.setPlayer(this.player);
    float[] storn_position = recursive_run(back_board_array, future_hand_num  - 1);
    chatModel.writeHistroy((int)storn_position[0], (int)storn_position[1], reversiModel.getIsYourTurn());// 履歴に書く。
    reversiModel.xySetStone((int)storn_position[0], (int)storn_position[1]);
  }

  public float[] recursive_run(int[][] parent_board, int n) {// 第一返り値:x,第二返り値:y,第二返り値:ひっくり返す数,
    int[][] board = new int[board_size][board_size];
    copyBoardArray(board, parent_board);// 親のboardをコピー
    back_judge_array = virtualModel.getJudgeBoardArray(virtualModel.getPlayer());
    ArrayList<int[]> can_put_position = getCanPutArray(back_judge_array);// 置ける位置を配列に格納する
    int length = can_put_position.size();// 置ける位置の個数を数える
    float[] result = { can_put_position.get(0)[0], can_put_position.get(0)[1],-1};
    ArrayList<Float> evaluation=new ArrayList<Float>();// 置ける位置を配列に格納する
    if (n == 1) {// 結果を出す
      int storn_num = virtualModel.countStorn(opponentPlayer());
      for (int i = 0; i < length; i++) {
        virtualModel.setPlayer(opponentPlayer());
        virtualModel.xySetStone(can_put_position.get(i)[0], can_put_position.get(i)[1]);// 仮想空間の盤面に置く
        int pre_min_over_storn=virtualModel.countStorn(opponentPlayer())-storn_num-1;
        evaluation.add((1/pre_min_over_storn)*coefficient_a);// 相手がひっくり返した石の個数で評価
        evaluation.set(i,evaluation.get(i)+(float)((1.0 / length)*coefficient_b));// 相手が置ける個数によって評価
        evaluation.set(i,evaluation.get(i)+evaluation_for_present_board());//相手が置いた後の盤面について評価
        ArrayList<int[]> can_put_position_last =getCanPutArray(virtualModel.getJudgeBoardArray(this.player));// 自分が置ける位置を配列に格納する
        for(int j=0;j<can_put_position_last.size();j++){//自分が置ける位置について評価
          evaluation.set(i,evaluation.get(i)+evaluation_for_next_position( can_put_position_last.get(j)[0],can_put_position_last.get(j)[1] , this.player));
        }
        copyBoardArray(virtualModel.getBoardArray(), board);// 仮想空間の盤面を現在の再帰の盤面に合わせる。(再帰内での初期化)
      }
      int max_evaluation_index=0;
      int min_evaluation_index=0;
      for(int j =1;j<evaluation.size();j++){//最大の評価値と最小の評価値を見つける
        if(evaluation.get(max_evaluation_index)<evaluation.get(j)){
          max_evaluation_index=j;
        }
        if(evaluation.get(min_evaluation_index)>evaluation.get(j)){
          min_evaluation_index=j;
        }
      }
      //最大の評価値と最小の評価値の差が15以上あるか、最小の評価値がマイナスなら最小の評価値を考慮する。
      if(evaluation.get(max_evaluation_index)-evaluation.get(min_evaluation_index)>15||evaluation.get(min_evaluation_index)<0){
        result[2] = evaluation.get(max_evaluation_index)-Math.abs((float)(evaluation.get(min_evaluation_index)*1.1));
      }else{
        result[2] = evaluation.get(max_evaluation_index);
      }
    } else {
      float[] pre_result = { can_put_position.get(0)[0], can_put_position.get(0)[1], -1 };// 仮の座標の記憶
      for (int i = 0; i < length; i++) {
        evaluation.add((float)0);
        if (n % 2 == 0) {// 仮想空間上の盤面の置くplayerの指定
          virtualModel.setPlayer(this.player);
        } else {
          virtualModel.setPlayer(opponentPlayer());
        }
        copyBoardArray(virtualModel.getBoardArray(), board);// 仮想空間の盤面を現在の再帰の盤面に合わせる。(再帰内での初期化)
        if(virtualModel.getPlayer()==this.player){//途中の盤面について評価
          evaluation.set(i,evaluation.get(i)+evaluation_for_next_position(can_put_position.get(i)[0], can_put_position.get(i)[1],virtualModel.getPlayer()));
        }else{
          evaluation.set(i,evaluation.get(i)-evaluation_for_next_position(can_put_position.get(i)[0], can_put_position.get(i)[1],virtualModel.getPlayer()));
        }
        virtualModel.xySetStone(can_put_position.get(i)[0], can_put_position.get(i)[1]);// 仮想空間の盤面に置く
        if ((virtualModel.getPassFlag() == 1 && n % 2 == 1)
            || (virtualModel.getFinishFlag() == 1 && n % 2 == 1)) {// もし相手が置くときにpassまたはfinishかの判定
          continue;// 最悪手と認定
        } else if (virtualModel.getPassFlag() == 1 && n % 2 == 0) {// もし自分が置いてpassとなったなら
          evaluation.set(i,(float)155);
        } else if (virtualModel.getFinishFlag() == 1 && n % 2 == 0) {// もし自分が置いてfinishとなったなら          
          evaluation.set(i,(float)156);
        } else {// pass,finishではない
          pre_result = recursive_run(virtualModel.getBoardArray(), n - 1);// 再帰する
          evaluation.set(i,evaluation.get(i)+pre_result[2]);
        }
        virtualModel.resetPassFlag();
        virtualModel.resetFinishFlag();
      }
      if(n+1==future_hand_num){
        int max_evaluation_index=0;
        for(int j =1;j<evaluation.size();j++){
          if(evaluation.get(max_evaluation_index)<evaluation.get(j)){
            max_evaluation_index=j;
          }
        }
        result[0]=can_put_position.get(max_evaluation_index)[0];
        result[1]=can_put_position.get(max_evaluation_index)[1];
        result[2]=evaluation.get(max_evaluation_index);
      }else{
        int max_evaluation_index=0;
        int min_evaluation_index=0;
        for(int j =1;j<evaluation.size();j++){//最大の評価値と最小の評価値を見つける
          if(evaluation.get(max_evaluation_index)<evaluation.get(j)){
            max_evaluation_index=j;
          }
          if(evaluation.get(min_evaluation_index)>evaluation.get(j)){
            min_evaluation_index=j;
          }
        }
        //最大の評価値と最小の評価値の差が15以上あるか、最小の評価値がマイナスなら最小の評価値を考慮する。
        if(evaluation.get(max_evaluation_index)-evaluation.get(min_evaluation_index)>15||evaluation.get(min_evaluation_index)<0){
          result[2] = evaluation.get(max_evaluation_index)-Math.abs((float)(evaluation.get(min_evaluation_index)*1.1));// 最小のひっくり返す数の記憶
        }else{
          result[2] = evaluation.get(max_evaluation_index);// 最小のひっくり返す数の記憶
        }
      }
    }
    return result;
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


  private int opponentPlayer(){
    return virtualModel.getOpponentStone(this.player);
  }
  private float evaluation_for_present_board(){
    int evaluation =0;
    String ij;
    for(int i=0;i<8;i++){
      for (int j = 0; j < 8; j++) {
        ij=i+""+j;
        if(virtualModel.getBoardArray()[i][j] == this.player){
            if(ij.equals("00")||ij.equals("07")||ij.equals("70")||ij.equals("77")){
              evaluation+=10;
            }else if(i==0||i==7||j==0||j==7){
              evaluation += 3;
            }
        }else if(virtualModel.getBoardArray()[i][j] == opponentPlayer()){
          if (ij.equals("00")  || ij.equals("07")  || ij.equals("70")  || ij.equals("77") ) {
            evaluation -= 50;
          } else if (i == 0 || i == 7 || j == 0 || j == 7) {
            evaluation -= 0.3;
          }
        }
      }
    }
    return evaluation;
  }

  private float evaluation_for_next_position(int x,int y,int player){//x,yにはplayerが置くことのできる位置が入る
    float evaluation=0;
    String xy=x+""+y;
    if(xy.equals("00")||xy.equals("07")||xy.equals("70")||xy.equals("77")){//隅なら評価
      evaluation+=30;
    }else if(x==0||x==7||y==0||y==7){//端なら評価
      evaluation += 3;
    }
    if(xy.equals("01")){//隅の一つと隣ならevalu_edgeを実行
      evaluation+=evalu_edge(x,y,"down");
    }else if(xy.equals("10")){
      evaluation+=evalu_edge(x,y,"right");
    }else if(xy.equals("06")){  
      evaluation+=evalu_edge(x,y,"up");
    }else if(xy.equals("17")){
      evaluation+=evalu_edge(x,y,"right");
    }else if(xy.equals("60")){  
      evaluation+=evalu_edge(x,y,"left");
    }else if(xy.equals("71")){
      evaluation+=evalu_edge(x,y,"down");
    }else if(xy.equals("67")){  
      evaluation+=evalu_edge(x,y,"left");
    }else if(xy.equals("76")){
      evaluation+=evalu_edge(x,y,"up");
    }
    return evaluation;
  }
  
  private int evalu_edge( int x,int y,String direction){//隅の色、隅から2つ隣の色によって置くべきかく必要がないかを判定する
    int evaluation=0;
    if((getPosition_for_evalu_edge(x, y,direction,0)==player&&getPosition_for_evalu_edge(x, y,direction,2)!=0)||(getPosition_for_evalu_edge(x, y,direction,0)==opponentPlayer()&&getPosition_for_evalu_edge(x, y,direction,2)==player)){
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
          for(;i<8;i++){//隅から置く場所の座標と同じ列の方向へ進み、何が入っているかを確認する
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
    return evaluation;//評価値を返す
  }
  
  private int getPosition_for_evalu_edge(int x,int y,String direction,int position){//引数をもとにした座標に入っている値を取得を返す。(evalu_edge専用関数)
    int direct_num=0;
    if(direction.equals("up")||direction.equals("right")){
      direct_num=7;
    }else{
      direct_num=0;
    }
    if(direction.equals("up")||direction.equals("down")){
      return virtualModel.getBoardArray()[x][Math.abs(direct_num-position)];//座標の値を返す。
    }else{
      return virtualModel.getBoardArray()[Math.abs(direct_num-position)][y];//座標の値を返す。
    }
  }
}
