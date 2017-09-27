package bin;//Tron
//by Sajid Rahman

//Tron game which allows 2 players to play against eachother
//Games are up to 5 wins and it includes various power-ups and each player has boosts

//This is the frame that handles all the scores for the game as well as
//keeps track of and controls all the states of the game


import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


public class Main extends JFrame implements ActionListener{
    Timer timer;
    private GamePanel game;	//JPanel for the game
    private boolean player1Win = false,player2Win = false;	//indicates which player just won
    private int player1Score,player2Score,gamePoint;		//keeps track of respective players points and what the game point is

    public Main() {

        super("Tron");	//title is Tron
        setSize(800,800);			//size is 800 x 800
        timer = new Timer(15,this);	//triggers every 15 ms
        setResizable(false);		//cant be resized
        setLocationRelativeTo(null);//centers the screen
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        player1Score = 0;	//each player has a default score of 0
        player2Score = 0;
        gamePoint = 5;		//point to win
        game = new GamePanel(this);
        add(game);
        setVisible(true);
    }
    //just creats the JFrame, everything else starts automatically
    public static void main(String[] args) {
        Main m = new Main();
    }
    //method to start the timer
    public void start(){
        timer.start();
    }
    //resets the scores
    public void resetScore(){
        player1Score = 0;
        player2Score = 0;
    }
    //when player 1 wins, their score goes up
    public void player1Win(){
        player1Score++;
        player1Win = true;
    }
    //when player 2 wins, their score goes up
    public void player2Win(){
        player2Score++;
        player2Win = true;
    }

    public int getPlayer1Points(){
        return player1Score;
    }

    public int getPlayer2Points(){
        return player2Score;
    }
    //checks whether a player reaches the game point and becomes the champ
    public int checkGameOver(){
        if(player1Score == gamePoint){
            return 1;
        }
        else if(player2Score == gamePoint){
            return 2;
        }
        else{
            return 0;
        }
    }
    //checks which player won the round or it was a tie and returns corresponding integer
    public int checkWinner(){
        if(player1Win){
            return 1;
        }

        else if(player2Win){
            return 2;
        }
        else{
            return 0;
        }
    }
    //checks every state of the game and always repaints the screen
    public void actionPerformed(ActionEvent evt){

        game.repaint();

        if(game.gameState().equals("play")){
            player1Win = false;			//no ones won while the game is being played
            player2Win = false;
            game.moveCycle();			//constantly updates cycles actions
            if(game.checkCollision()){
                //checks the scores when a cycle collides
                if(checkGameOver() == 1){		//if player 1 has won, then it goes to his win screen
                    resetScore();
                    game.setState("p1WinScreen");
                }
                else if(checkGameOver() == 2){	//if player 2 has won, then it goes to his win screen
                    resetScore();
                    game.setState("p2WinScreen");
                }
                else{
                    game.setState("continue?");//if neither has won then it goes to the "continue?" screen
                }
            }
        }
        //appropriate screen updates for every screen the game is in
        else if(game.gameState().equals("menu")){
            game.menuSelect();
        }
        else if(game.gameState().equals("continue?")){
            game.continueSelect();
        }
        else if(game.gameState().equals("controls")){
            game.controlsSelect();
        }
        else if(game.gameState().equals("p1WinScreen") || game.gameState().equals("p2WinScreen")){
            game.continueSelect();
        }
    }
}