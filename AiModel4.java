//saito
import java.util.ArrayList;
import java.util.Random;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

class Ai_4 extends Model {
  // final boolean is_maxcount = false;// 相手が置く際小数で判定する
  private int player;
  private int board_size;
  private Model.ReversiModel reversiModel;// 実際に動かすmodel
  private Model.ChatModel chatModel;// chatmodel
  private int[][] back_judge_array;
  private double[][] weight_board;
  
  // コンストラクタ
  public Ai_4(Model m,int aiPlayer) {
    reversiModel = m.getReversiModel();
    chatModel = m.getChatModel();
    this.player = aiPlayer;// playerの指定
    this.board_size = reversiModel.board_size;// 盤面のサイズを指定。
    this.weight_board = new double[board_size][board_size];
    read_weigth(this.weight_board);
    // System.exit(0);
  }

  // 学習用
  public Ai_4(Model m,int aiPlayer,double[][] weight_board) {
    reversiModel = m.getReversiModel();
    chatModel = m.getChatModel();
    this.player = aiPlayer;// playerの指定
    this.board_size = reversiModel.board_size;// 盤面のサイズを指定。
    this.weight_board = new double[board_size][board_size];
    copyBoardArray(this.weight_board,weight_board);
  }

  // 呼び出される関数
  public void run() {// みんなのプログラムを追加する。
    back_judge_array = reversiModel.getJudgeBoardArray(player);
    ArrayList<double[]> can_put_array = new ArrayList<double[]>();
    ArrayList<double[]> max_wieght_array = new ArrayList<double[]>();
    double max_wieght = 0;
    for(int i = 0; i<board_size ;i++){
      for(int j = 0; j<board_size ;j++){
        if(back_judge_array[i][j] == 3){
          // int[] tmp = {i,j,weight_board[i][j]};
          can_put_array.add(new double[]{i,j,weight_board[i][j]});
          // 三項条件式, sortが楽かも(sortしてカウントして取得する)
          max_wieght = max_wieght < weight_board[i][j] ? weight_board[i][j] : max_wieght;
        }
      } 
    }
    for(double[] can_put: can_put_array){
      if(can_put[2] == max_wieght){
        max_wieght_array.add(can_put);
      } 
    }
    Random rand = new Random();
    int num = rand.nextInt(max_wieght_array.size());
    double[] storn_position = max_wieght_array.get(num);
    chatModel.writeHistroy((int)storn_position[0], (int)storn_position[1], reversiModel.getIsYourTurn());// 履歴に書く。
    reversiModel.xySetStone((int)storn_position[0], (int)storn_position[1]);
  }
  private void read_weigth(double[][] weight_board){
    Path path = Paths.get("./AiDataBase/weight.csv");
    try {
      // CSVファイルの読み込み
      List<String> lines = Files.readAllLines(path, Charset.forName("Shift-JIS"));
      for(int i = 0;i< board_size ;i++){
        String[] data = lines.get(i).split(",");
        for(int j=0; j < board_size ;j++){
          weight_board[i][j]= Integer.parseInt(data[j]);
        }
      }
      // for(int i = 0; i< board_size ; i++){
      //   for(int j = 0; j< board_size ; j++){
      //     System.out.println(weight_board[i][j]);
      //   }
      // }
    } catch (IOException e) {
        System.out.println("ファイル読み込みに失敗");
    }
  }
  
  // 8*8配列をコピーする(第一引数:コピー先,第二引数:コピー元)
  public void copyBoardArray(int[][] target_copy, int[][] source_copy) {
    for (int i = 0; i < board_size; i++) {
      for (int j = 0; j < board_size; j++) {
        target_copy[i][j] = source_copy[i][j];
      }
    }
  }
  public void copyBoardArray(double[][] target_copy, double[][] source_copy) {
    for (int i = 0; i < board_size; i++) {
      for (int j = 0; j < board_size; j++) {
        target_copy[i][j] = source_copy[i][j];
      }
    }
  }
}
  