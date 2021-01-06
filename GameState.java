/**
* This class contains the methods to make changes to the 2D grid of the game
* 4028 and also implements the fundemental logic of the game including the point
* system.
*/

import java.util.*;

public class GameState{

  private Random rng;
  private int[][] board;
  private int score;

  public String toString () {
    StringBuilder outputString = new StringBuilder();
    outputString.append(String.format("Score: %d\n", getScore()));
    for (int row = 0; row < getBoard().length; row++) {
        for (int column = 0; column < getBoard()[0].length; column++) {
            outputString.append(getBoard()[row][column] == 0 ? "    -" :
                String.format("%5d", getBoard()[row][column]));
        }
        outputString.append("\n");
    }
    return outputString.toString();
  }

  /**
  * Constructor that initializes the number of rows and columns
  * the grid should have
  * @param numRows The number of rows in the grid
  * @param numCols The number of cols in the grid
  */
  public GameState(int numRows, int numCols){
    this.board = new int[numRows][numCols];
    this.score = 0;
    this.rng = new Random(Config.RANDOM_SEED);
  }

  /**
  * Getter method that returns a deepCopy of the grid (2Darray)
  * @return a deepCopy 2D array of the current state of the grid
  */
  public int[][] getBoard(){
    int[][] deepCopy = new int[this.board.length][this.board[0].length];
    for(int i = 0; i < deepCopy.length; i++){
      for(int j = 0; j < deepCopy[i].length; j++){
        deepCopy[i][j] = this.board[i][j];
      }
    }
    return deepCopy;
  }

  /**
  * Setter method that sets the current grid to a new grid
  * @param newBoard 2D array that is to become the grid for the current game
  */
  public void setBoard(int[][] newBoard){
    if(newBoard == null){
      return;
    }
    int[][] deepCopy = new int[newBoard.length][newBoard[0].length];
    for(int i = 0; i < deepCopy.length; i++){
      for(int j = 0; j <deepCopy[i].length; j++){
        deepCopy[i][j] = newBoard[i][j];
      }
    }
    this.board = deepCopy;
  }

  /**
  * Getter method that returns the user's current score
  * @return the user's current score
  */
  public int getScore(){
    return this.score;
  }

  /**
  * Setter method that sets the score of the user
  * @param newScore the new score of the user
  */
  public void setScore(int newScore){
    this.score = newScore;
  }

  /**
  * Generates a random number from 0 up to bound
  * @param bound the number the random number generator can go up to
  * but not including
  */
  protected int rollRNG (int bound){
    int goingOut = this.rng.nextInt(bound);
    return goingOut;
  }

  /**
  * Generates a random tile 2 or 4 but 2 has a 70 percent chance
  * and 4 has a 30 percent chance of being generated
  * @return returns 2 if a 2tile was generated and 4 if a 4tile was generated
  */
  protected int randomTile (){
    int randomNumber = this.rng.nextInt(100) + 1;
    if(randomNumber <= Config.TWO_PROB){
      return Config.TWO_TILE;
    }else{
      return Config.FOUR_TILE;
    }
  }

  /**
  * Counts the number of empty tiles in the current grid
  * @return the number of empty tiles in the current grid
  */
  protected int countEmptyTiles(){
    int numberOfEmptyTiles = 0;
    for(int i = 0; i < this.board.length; i++){
      for(int j = 0; j < this.board[i].length; j++){
        if(this.board[i][j] == 0){
          numberOfEmptyTiles += 1;
        }
      }
    }
    return numberOfEmptyTiles;
  }

  /**
  * Method that adds a tile to a random empty spot on the grid
  * @return the number(2 or 4) of the randomly added tile if added successfully
  * returns 0 otherwise
  */
  protected int addTile(){
    if(countEmptyTiles() != 0){
      int count = 1;
      int randomSpot = rollRNG(countEmptyTiles()) + 1;
      for(int i = 0; i < this.board.length;i++){
        for(int j = 0; j < this.board[i].length; j++){
          if(this.board[i][j] == 0){
            if(randomSpot == count){
              int randomTile = randomTile();
              this.board[i][j] = randomTile;
              return randomTile;
            }else{
              count += 1;
            }
          }
        }
      }
    }
    return 0;
  }

  /**
  * Method that rotates the current grid counterclockwise
  */
  protected void rotateCounterClockwise (){

    if(this.board.length != 1 && this.board.length != 0){
      int[][] copiedBoard = getBoard();
      int[] storedColumn = new int[this.board.length];
      for(int i = this.board[0].length-1; i >= 0; i--){
        for(int j = 0; j < this.board.length; j++){
          storedColumn[j] = copiedBoard[j][i];
        }
        for(int m = 0; m < this.board[0].length; m++){
          this.board[Math.abs(i-(this.board.length-1))][m] = storedColumn[m];
        }
      }
    }
  }

  /**
  * Method that checks if any tiles in the grid can slide down
  * @return Boolean value true or false. True if a tile is found that can slide down.
  * false if there are no tiles that can slide down
  */
  protected boolean canSlideDown (){
    for(int i = 0; i < this.board.length-1; i++){
      for(int j = 0; j < this.board[i].length; j++){
        if(this.board[i][j] == this.board[i+1][j] && board[i][j] != 0){
          return true;
        }
        else if(this.board[i+1][j] == 0 && board[i][j] != 0){
          return true;
        }
      }
    }
    return false;
  }

  /**
  * Method to check if the game is over
  * @return Boolean value true if the game is over and false of the game is not
  * over
  */
  public boolean isGameOver (){
    if(this.board.length == 1 && this.board[0].length == 1){
      return true;
    }
    if(this.canSlideDown()){
      return false;
    }else{
      this.rotateCounterClockwise();
      if(this.canSlideDown()){
        this.rotateCounterClockwise();
        this.rotateCounterClockwise();
        this.rotateCounterClockwise();
        return false;
      }else{
        this.rotateCounterClockwise();
        if(this.canSlideDown()){
          this.rotateCounterClockwise();
          this.rotateCounterClockwise();
          return false;
        }else{
          this.rotateCounterClockwise();
          if(this.canSlideDown()){
            this.rotateCounterClockwise();
            return false;
          }
        }
      }
    }
    return true;
  }

  /**
  * Method that slides tiles of the grid down
  * @return boolean value true if the grid slid down succesfully and
  * false if the grid made no changes (aka could not slide down)
  */
  protected boolean slideDown (){
    if(this.canSlideDown()){
      for(int col = 0; col < this.board[0].length; col++){
        int count = 0;
        for(int row = this.board.length -2; row >= 0; row--){
          int j = 0;
          while(j <= count){
            if(this.board[(row + 1+j)][col] == 0){
              this.board[(row + 1+j)][col] = this.board[row +j][col];
              board[row +j][col] = 0;
            }else if(this.board[(row + 1+j)][col] == this.board[row +j][col]){
              int sum = this.board[(row + 1+j)][col] + this.board[row +j][col];
              this.board[(row + 1+j)][col] = sum;
              this.board[row +j][col] = 0;
              this.score += sum;
              count--;
              break;
            }else{
              break;
            }
            j +=1;
          }
          count += 1;
        }
      }
      return true;
    }
    return false;
  }

  /**
  * Method that moves all possible tiles in the grid in a specific direction (
  * left, right, down, or up)
  * @param dir direction up, down, left, or right
  * @return boolean value true if the movement was done succesfully and false
  * otherwise
  */
  public boolean move (Direction dir){
    if(dir == null){
      return false;
    }
    if(dir.getRotationCount() == 0){
      if(this.slideDown()){
        this.addTile();
        return true;
      }
    }else if(dir.getRotationCount() == 1){
      this.rotateCounterClockwise();
      if(this.slideDown()){
        this.rotateCounterClockwise();
        this.rotateCounterClockwise();
        this.rotateCounterClockwise();
        this.addTile();
        return true;
      }else{
        this.rotateCounterClockwise();
        this.rotateCounterClockwise();
        this.rotateCounterClockwise();
      }

    }else if(dir.getRotationCount() == 2){
      this.rotateCounterClockwise();
      this.rotateCounterClockwise();
      if(this.slideDown()){
        this.rotateCounterClockwise();
        this.rotateCounterClockwise();
        this.addTile();
        return true;
      }else{
        this.rotateCounterClockwise();
        this.rotateCounterClockwise();
      }
    }else if(dir.getRotationCount() == 3){
      this.rotateCounterClockwise();
      this.rotateCounterClockwise();
      this.rotateCounterClockwise();
      if(this.slideDown()){
        this.rotateCounterClockwise();
        this.addTile();
        return true;
      }else{
        this.rotateCounterClockwise();
      }
    }
    return false;
  }

}
