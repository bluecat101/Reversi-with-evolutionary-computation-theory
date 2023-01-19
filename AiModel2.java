//阪上用

import java.util.ArrayList;
import java.util.Random;
class Ai_2 extends Model{
  private int player;
  private int board_size;
  private Model.ReversiModel reversiModel;
  //back_judge_arrayの中身は変えないでね(変えるなら一度コピーしてからしてください。)
  private int[][] back_judge_array;
  private Model.ChatModel chatModel;// chatmodel
  
  //コンストラクタ
  public Ai_2(Model m, int aiPlayer){
    reversiModel = m.getReversiModel();
    chatModel = m.getChatModel();
    this.player=aiPlayer;//playerの指定
    this.board_size=reversiModel.board_size;//盤面のサイズを指定。
  }
  
  //呼び出される関数
  public void run(){//みんなのプログラムを追加する。
    int x=-1,y=-1;
    System.out.println("2da-");
    back_judge_array=reversiModel.getJudgeBoardArray(player);
    ArrayList<int[]> can_put_array_edge = new ArrayList<>();
    ArrayList<int[]> can_put_array_sub_edge = new ArrayList<>();
    ArrayList<int[]> can_put_array_otherwise = new ArrayList<>();
    ArrayList<int[]> can_put_array_outside = new ArrayList<>();
    ArrayList<int[]> can_put_array_inside = new ArrayList<>();
    for(int i=0;i<board_size;i++){
      for(int j=0;j<board_size;j++){
        if(back_judge_array[i][j]==3 && ((i==0 || i==7) && (j==0 || j==7))){
          int[] k={i,j};
          can_put_array_edge.add(k);
        }else if(back_judge_array[i][j]==3 && ((i<=1 || i>=6) && (j<=1 || j>=6))){
          int[] k={i,j};
          can_put_array_sub_edge.add(k);
        }else if(back_judge_array[i][j]==3 && (((i==0 || i==7) && ((j>=2 && j<=5))) || ((j==0 || j==7) && ((i>=2 && i<=5))))){
          int[] k={i,j};
          can_put_array_outside.add(k);
        }else if(back_judge_array[i][j]==3 && ((i>=2 && i<=5) && (j>=2 && j<=5))){
          int[] k={i,j};
          can_put_array_inside.add(k);
        }else if(back_judge_array[i][j]==3){
          int[] k={i,j};
          can_put_array_otherwise.add(k);
        }
      }
    }
    int edge_size = can_put_array_edge.size();
    int sub_edge_size = can_put_array_sub_edge.size();
    int outside_size = can_put_array_outside.size();
    int inside_size = can_put_array_inside.size();
    int otherwise_size = can_put_array_otherwise.size();
    Random rand = new Random();
    if(edge_size != 0){
      int element = rand.nextInt(edge_size);
      chatModel.writeHistroy(can_put_array_edge.get(element)[0],can_put_array_edge.get(element)[1] , reversiModel.getIsYourTurn());//履歴に書く。
      reversiModel.xySetStone(can_put_array_edge.get(element)[0],can_put_array_edge.get(element)[1]);
      return;
    }else if(outside_size != 0){
      int element = rand.nextInt(outside_size);
      chatModel.writeHistroy(can_put_array_outside.get(element)[0],can_put_array_outside.get(element)[1] , reversiModel.getIsYourTurn());//履歴に書く。
      reversiModel.xySetStone(can_put_array_outside.get(element)[0],can_put_array_outside.get(element)[1]);
      return;
    }else if(inside_size != 0){
      int element = rand.nextInt(inside_size);
      chatModel.writeHistroy(can_put_array_inside.get(element)[0],can_put_array_inside.get(element)[1] , reversiModel.getIsYourTurn());//履歴に書く。
      reversiModel.xySetStone(can_put_array_inside.get(element)[0],can_put_array_inside.get(element)[1]);
      return;
    }else if(otherwise_size != 0){
      int element = rand.nextInt(otherwise_size);
      chatModel.writeHistroy(can_put_array_otherwise.get(element)[0],can_put_array_otherwise.get(element)[1] , reversiModel.getIsYourTurn());//履歴に書く。
      reversiModel.xySetStone(can_put_array_otherwise.get(element)[0],can_put_array_otherwise.get(element)[1]);
      return;
    }else if(sub_edge_size != 0){
      int element = rand.nextInt(sub_edge_size);
      chatModel.writeHistroy(can_put_array_sub_edge.get(element)[0],can_put_array_sub_edge.get(element)[1] , reversiModel.getIsYourTurn());//履歴に書く。
      reversiModel.xySetStone(can_put_array_sub_edge.get(element)[0],can_put_array_sub_edge.get(element)[1]);
      return;
    }
  }
}
