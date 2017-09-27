package bin;

/**
 * Created by Sajid on 2017-09-27.
 */
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.event.KeyEvent;
import java.util.*;
//handles every game state and event
//also all they graphics drawing as well as the keyboard inputs


public class GamePanel extends JPanel implements KeyListener{
    private Main mainFrame;		//the main Jframe
    private boolean transition = false;	//to check whether the game grid background needs to be set
    private boolean pressedDown = false;//used to control keys being pressed so they arent instantaneously scrolling through the menus
    private Cycle[] players;	//player objects
    private boolean []keys;		//boolean of every key
    private boolean [][] trail;	//2d array for the trail the cycles leave behind

    //all the images used in the game
    //new ImageIcon("src\\resources\\AttackSprites\\FireDisk.png").getImage();
    Image back = new ImageIcon("src\\resources\\tron_bg.jpg").getImage();//title screen background
    Image gameBg = new ImageIcon("src\\resources\\grid.jpg").getImage(); //game background
    Image shieldIC = new ImageIcon("src\\resources\\shield.png").getImage();//shield icon
    Image curseIC = new ImageIcon("src\\resources\\cursed.png").getImage(); //curse icon
    Image RedWinIC = new ImageIcon("src\\resources\\red-win.jpg").getImage(); //image for when red wins
    Image BlueWinIC = new ImageIcon("src\\resources\\blue-win.png").getImage();//image for when blue wins
    Image ControlsIG = new ImageIcon("src\\resources\\Controls.jpg").getImage();//controls image page
    Image RedCycleUP = new ImageIcon("src\\resources\\red-up.png").getImage();
    Image RedCycleRIGHT = new ImageIcon("src\\resources\\red-right.png").getImage();
    Image RedCycleLEFT = new ImageIcon("src\\resources\\red-left.png").getImage();
    Image RedCycleDOWN = new ImageIcon("src\\resources\\red-down.png").getImage();

    Image BlueCycleUP = new ImageIcon("src\\resources\\blue-up.png").getImage();
    Image BlueCycleRIGHT = new ImageIcon("src\\resources\\blue-right.png").getImage();
    Image BlueCycleLEFT = new ImageIcon("src\\resources\\blue-left.png").getImage();
    Image BlueCycleDOWN = new ImageIcon("src\\resources\\blue-down.png").getImage();
    private String menuSelection = "start",contSelection = "start"; //preset states for various selections
    private String gameState = "menu"; //state for the game
    private String[] menuSelections,contSelections;
    private ArrayList<Point> shieldLoc,switchLoc,curseLoc; //points to spawn different power ups
    private int counter = 0;  //counter used for speed manipulation
    private int[] pIntervals; //intervals used for speed manipulation
    private int menuIndex = 0,contIndex = 0; //index used for menu different selections

    //sets up for the main game
    public GamePanel(Main m) {
        mainFrame = m;
        keys = new boolean[KeyEvent.KEY_LAST+1];
        addKeyListener(this);
        players = new Cycle[]{new Cycle(600,300,-1),new Cycle(200,300,1)};	//adds the Cycle objects
        menuSelections = new String[]{"start","controls","quit"};			//the different selections for main menu
        contSelections = new String[]{"start","menu","quit"};				//the different selections for continue menu
        trail = new boolean[160][120];	//trail is sized in the arena/5 size as the cycles always move 5 spaces
        pIntervals = new int[]{2,2};	//the regular amount of ticks is 2 for everytime it updates
        setSize(800,800);
        //initialize points arraylists for power ups
        shieldLoc = new ArrayList<Point>();
        curseLoc = new ArrayList<Point>();
        switchLoc = new ArrayList<Point>();
    }
    //starts the timer for the mainframe when ready
    public void addNotify(){
        super.addNotify();
        requestFocus();
        mainFrame.start();
    }
    //resets appropriate objects and items for the new game
    public void resetGame(){
        players = new Cycle[]{new Cycle(200,300,1),new Cycle(600,300,-1)};
        //resets the trail
        for(int i = 0; i < 160;i++){
            for(int j = 0; j < 120;j++){
                trail[i][j] = false;
            }
        }
        //reset all power ups
        shieldLoc = new ArrayList<Point>();
        curseLoc = new ArrayList<Point>();
        switchLoc = new ArrayList<Point>();

        gameState = "play";		//the game state is now play
        transition = true;		//immediatly pastes the game bg
    }

    //checks collision of the cycle with everything else and adds point to opposite player when a player collides
    public boolean checkCollision(){
        //checks players does not collide with the out walls
        if(players[0].getX() >= 800 || players[0].getX() < 0 || players[0].getY() >= 600 || players[0].getY() < 0){
            mainFrame.player2Win();
            return true;
        }
        if(players[1].getX() >= 800 || players[1].getX() < 0 || players[1].getY() >= 600 || players[1].getY() < 0){
            mainFrame.player1Win();
            return true;
        }
        //checks if players collide with eachother
        if(players[0].getX() == players[1].getX() && players[0].getY() == players[1].getY()){
            if(players[0].isSwitching()){//exception is when the switch powerup is used and ignores the instant they switch positions
                return false;
            }
            return true;
        }
        //checks both players the trail
        for(int i = 0; i < 160;i++){
            for(int j = 0; j < 120;j++){
                if(trail[players[0].getX()/5][players[0].getY()/5]){
                    if(players[0].hasShield()){ //exception is when the player has a shield
                        players[0].lostShield(); //then he loses the shield so it only lasts 1 segment
                        return false;
                    }
                    mainFrame.player2Win();
                    return true;
                }
                if(trail[players[1].getX()/5][players[1].getY()/5]){
                    if(players[1].hasShield()){
                        players[1].lostShield();
                        return false;
                    }
                    mainFrame.player1Win();
                    return true;
                }
            }
        }
        return false;
    }

    //updates the direction of the cycles, speed and when they get a powerup
    public void moveCycle(){
        counter++; //counter increases at a steadyrate
        //players velocity changes for corresponding  key presses as long as it already isnt moving in that velocity(so they can only take turns)
        //these velocities are however reversed when the player is cursed
        if(keys[KeyEvent.VK_RIGHT] && players[0].getVx() == 0 ){
            if(players[0].isCursed()){
                players[0].setVx(-1);
            }
            else{
                players[0].setVx(1);
            }
        }
        else if(keys[KeyEvent.VK_LEFT]  && players[0].getVx() == 0){
            if(players[0].isCursed()){
                players[0].setVx(1);
            }
            else{
                players[0].setVx(-1);
            }

        }
        else if(keys[KeyEvent.VK_UP] && players[0].getVy() == 0 ){
            if(players[0].isCursed()){
                players[0].setVy(1);
            }
            else{
                players[0].setVy(-1);
            }

        }
        else if(keys[KeyEvent.VK_DOWN] && players[0].getVy() == 0){
            if(players[0].isCursed()){
                players[0].setVy(-1);
            }
            else{
                players[0].setVy(1);
            }
        }
        if(keys[KeyEvent.VK_ENTER]){ //boost key makes them boost
            players[0].boost();
        }
        //controls for player 2
        if(keys[KeyEvent.VK_D] && players[1].getVx() == 0 ){
            if(players[1].isCursed()){
                players[1].setVx(-1);
            }
            else{
                players[1].setVx(1);
            }
        }
        else if(keys[KeyEvent.VK_A]  && players[1].getVx() == 0){
            if(players[1].isCursed()){
                players[1].setVx(1);
            }
            else{
                players[1].setVx(-1);
            }
        }
        else if(keys[KeyEvent.VK_W] && players[1].getVy() == 0 ){
            if(players[1].isCursed()){
                players[1].setVy(1);
            }
            else{
                players[1].setVy(-1);
            }
        }
        else if(keys[KeyEvent.VK_S] && players[1].getVy() == 0){
            if(players[1].isCursed()){
                players[1].setVy(-1);
            }
            else{
                players[1].setVy(1);
            }
        }
        if(keys[KeyEvent.VK_Q]){
            players[1].boost();
        }

        for(int i = 0; i < 2; i++){
            pIntervals[i] = 2; //regular interval is 2

            if(players[i].isBoosting()){//when a cycle is boosting, their interval is 1, making them update and move twice as fast
                pIntervals[i] = 1;
            }

            if(counter%pIntervals[i] == 0){//cycles move according to interval
                players[i].move();
            }

            trail[players[i].getPX()/5][players[i].getPY()/5] = true;	//whatever the previous x and y are for the cycle are added to the trail
        }


        if(counter%150 == 0){	//every 150 ticks for when it updates, a powerup is spawned
            spawnPowerUp();
        }

        for(int j = 0; j < 2;j++){
            //checks if either player lands on a shield and then gives them shield
            for(int i = 0; i < shieldLoc.size(); i++){
                if(players[j].getX() == (int)shieldLoc.get(i).getX() && players[j].getY() == (int)shieldLoc.get(i).getY()){
                    shieldLoc.remove(shieldLoc.get(i));
                    players[j].obtainShield();
                }
            }
            //checks if either player lands on a curse power up which curses the opposite player
            for(int i = 0; i < curseLoc.size(); i++){
                if(players[j].getX() == (int)curseLoc.get(i).getX() && players[j].getY() == (int)curseLoc.get(i).getY()){
                    curseLoc.remove(curseLoc.get(i));
                    players[1-j].castCurse();
                }
            }
            //checks if either player lands on a switcheroo powerup and switches their positions
            for(int i = 0; i < switchLoc.size(); i++){
                if(players[j].getX() == (int)switchLoc.get(i).getX() && players[j].getY() == (int)switchLoc.get(i).getY()){
                    switchLoc.remove(switchLoc.get(i));
                    players[0].setSwitching();
                    players[1].setSwitching();
                    //keep track of player 1s previous states
                    int prevX1 = players[0].getX();
                    int prevY1 = players[0].getY();
                    int prevVX1 = players[0].getVx();
                    int prevVY1 = players[0].getVy();
                    //switches positions
                    players[0].setX(players[1].getX());
                    players[0].setY(players[1].getY());
                    players[1].setX(prevX1);
                    players[1].setY(prevY1);
                    //switches velocities
                    players[0].setVx(players[1].getVx());
                    players[0].setVy(players[1].getVy());
                    players[1].setVx(prevVX1);
                    players[1].setVy(prevVY1);
                }
            }

        }
    }
    //spawns a random power up in a random location
    public void spawnPowerUp(){
        int type,xSpawn,ySpawn;
        //spawns random x and y location for powerup
        xSpawn = (int)(Math. random() * 160);
        ySpawn = (int)(Math. random() * 120);
        while(trail[xSpawn][ySpawn]){//keeps randomizing if that location is on a trail
            xSpawn = (int)(Math. random() * 160);
            ySpawn = (int)(Math. random() * 120);
        }
        type = (int)(Math.random() * 3); //random between 3 options
        //adds the corresponding x and y points to points for type of powerup
        if(type == 0){
            shieldLoc.add(new Point(xSpawn*5,ySpawn*5));
        }
        else if(type == 1){
            curseLoc.add(new Point(xSpawn*5,ySpawn*5));
        }
        else if(type == 2){
            switchLoc.add(new Point(xSpawn*5,ySpawn*5));
        }
    }

    //Every menue uses pressedDown so the selections are selected as the player presses then lets go of they key
    //This is so the option doesnt instantaneously go to the top or bottom or keeps space keeps getting triggered
//-----------------------------------------------------------------------------------------------------------------//

    //choosing option player chooses in main menu
    public void menuSelect(){
        //the selection is according to the index
        menuSelection = menuSelections[menuIndex];
        //pressing up decreases index
        if(keys[KeyEvent.VK_UP] && !pressedDown){
            pressedDown = true;
            if(menuIndex > 0){
                menuIndex--;
            }
        }
        //pressing up increases index
        else if(keys[KeyEvent.VK_DOWN] && !pressedDown){
            pressedDown = true;
            if(menuIndex < 2){
                menuIndex++;
            }

        }
        if(keys[KeyEvent.VK_SPACE] && !pressedDown){
            pressedDown = true;
            if(menuSelection.equals("start")){
                resetGame();//choosing start starts a new game
            }
            if(menuSelection.equals("controls")){
                gameState = "controls";//goes to control state
            }
            if(menuSelection.equals("quit")){
                System.exit(0);	//closes program
            }
        }
        if(!keys[KeyEvent.VK_SPACE] && !keys[KeyEvent.VK_DOWN] && !keys[KeyEvent.VK_UP]){ //pressedDown is only false once all keys are let go of
            pressedDown = false;
        }
    }
    //in controls menu they can only go back to main menu
    public void controlsSelect(){
        if(keys[KeyEvent.VK_SPACE] && !pressedDown){
            pressedDown = true;
            gameState = "menu";
        }
        if(!keys[KeyEvent.VK_SPACE]){
            pressedDown = false;
        }
    }

    //choosing option player chooses in continue menu
    public void continueSelect(){
        //selection accordance to index
        contSelection = contSelections[contIndex];
        //index decreases with up key
        if(keys[KeyEvent.VK_UP] && !pressedDown){
            pressedDown = true;
            if(contIndex > 0){
                contIndex--;
            }
        }
        //index increases with down key
        else if(keys[KeyEvent.VK_DOWN] && !pressedDown){
            pressedDown = true;
            if(contIndex < 2){
                contIndex++;
            }
        }
        if(keys[KeyEvent.VK_SPACE]){
            pressedDown = true;
            if(contSelection.equals("start")){
                resetGame();	//starts new game
            }
            if(contSelection.equals("menu")){
                mainFrame.resetScore();	//resets scores when they go to main menu
                gameState = "menu";
            }
            if(contSelection.equals("quit")){
                System.exit(0);
            }
        }
        if(!keys[KeyEvent.VK_SPACE] && !keys[KeyEvent.VK_DOWN] && !keys[KeyEvent.VK_UP]){
            pressedDown = false;
        }
    }
    //sets state of game
    public void setState(String state){
        gameState = state;
    }
    //returns the state of the game
    public String gameState(){
        return gameState;
    }
    //all the graphics
    public void paintComponent(Graphics g){
        Graphics2D g2 = (Graphics2D) g;	//uses graphics 2D
        //renders text
        RenderingHints rh =
                new RenderingHints(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

        rh.put(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);

        g2.setRenderingHints(rh);

        //draws gameBackground once
        if(transition){
            g2.drawImage(gameBg,0,0,800,600,this);
            transition  = false;
        }

        if(gameState.equals("play")){
            g2.setColor(Color.black);
            g2.setFont(new Font("Eras Bold ITC", Font.PLAIN, 25));
            g2.fillRect(0,600,800,200);//bottom part of screen
            g2.setColor(Color.blue);
//   			if(players[0].getVx() == 1){
//   				g2.drawImage(BlueCycleRIGHT,players[0].getX(),players[0].getY(),5,5,this);
//   			}
//   			if(players[0].getVx() == -1){
//   				g2.drawImage(BlueCycleLEFT,players[0].getX(),players[0].getY(),5,5,this);
//   			}
//   			if(players[0].getVy() == 1){
//   				g2.drawImage(BlueCycleDOWN,players[0].getX(),players[0].getY(),5,5,this);
//   			}
//   			if(players[0].getVy() == -1){
//   				g2.drawImage(BlueCycleUP,players[0].getX(),players[0].getY(),5,5,this);
//   			}
            g2.fillRect(players[0].getX(),players[0].getY(),5,5); //player 1 and his trail
            g2.drawString("PLAYER 1", 100, 630);
            //amount of points player 1 has
            for(int i = 1; i < mainFrame.getPlayer1Points()+1;i++){
                g2.fillRect(i*30+70,640,15,15);
            }

            g2.setColor(Color.red);
            g2.fillRect(players[1].getX(),players[1].getY(),5,5);
            g2.drawString("PLAYER 2", 550, 630);
            //amount of points player 2 has
            for(int i = 1; i < mainFrame.getPlayer2Points()+1;i++){
                g2.fillRect(i*30+520,640,15,15);
            }
            //shield powerups
            g2.setColor(Color.yellow);//they are yellow
            for(int i = 0; i < shieldLoc.size(); i++){
                g2.fillRect((int)shieldLoc.get(i).getX(),(int)shieldLoc.get(i).getY(),5,5);
            }

            //curse	powerups
            g2.setColor(new Color(125,24,71));//they are purple
            for(int i = 0; i < curseLoc.size(); i++){
                g2.fillRect((int)curseLoc.get(i).getX(),(int)curseLoc.get(i).getY(),5,5);
            }

            //switch powerups
            g2.setColor(Color.red);
            for(int i = 0; i < switchLoc.size(); i++){//they are half red and half blue
                g2.fillRect((int)switchLoc.get(i).getX(),(int)switchLoc.get(i).getY(),5,5);
                g2.setColor(Color.blue);
                g2.fillRect((int)switchLoc.get(i).getX(),(int)switchLoc.get(i).getY(),2,5);
            }

            //boost graphics
            g2.setColor(Color.green);
            g2.setFont(new Font("Eras Bold ITC", Font.PLAIN, 20));
            g2.drawString("BOOSTS", 40, 755);
            for(int i = 0; i < players[0].numBoosts();i++){
                g2.fillRect(130+30*i,742,10,10);
            }
            g2.drawString("BOOSTS", 540, 755);
            for(int i = 0; i < players[1].numBoosts();i++){
                g2.fillRect(630+30*i,742,10,10);
            }
            //power-up icon boxes
            g2.setColor(Color.gray);
            g2.fillRect(100,670,50,50);
            g2.fillRect(550,670,50,50);
            g2.fillRect(170,670,50,50);
            g2.fillRect(620,670,50,50);

            g2.setColor(new Color(52,224,233));
            //if players have powerup, respective boxes light up
            if(players[0].hasShield()){
                g2.fillRect(100,670,50,50);
            }
            if(players[1].hasShield()){
                g2.fillRect(550,670,50,50);
            }
            if(players[0].isCursed()){
                g2.fillRect(170,670,50,50);
            }
            if(players[1].isCursed()){
                g2.fillRect(620,670,50,50);
            }
            //power-up icon
            g2.drawImage(shieldIC,100,670,50,50,this);
            g2.drawImage(shieldIC,550,670,50,50,this);
            g2.drawImage(curseIC,170,670,50,50,this);
            g2.drawImage(curseIC,620,670,50,50,this);

        }

        if(gameState.equals("continue?")){

            g2.setColor(new Color(52,224,233)); //aquamarime
            g2.fillRect(300,190,240,200);	//outline box
            g2.setColor(Color.white);
            g2.fillRect(305,195,230,190);	//main box
            g2.setColor(new Color(253,152,43));	//orange
            g2.setFont(new Font("AR DESTINE", Font.PLAIN, 25));
            //text on top of box for the options
            g2.drawString("NEXT ROUND?", 330, 280);
            g2.drawString("MENU", 330, 310);
            g2.drawString("QUIT", 330, 340);
            g2.setFont(new Font("AR DESTINE", Font.PLAIN, 30));
            //text which indicates which player just won
            if(mainFrame.checkWinner() == 0){
                g2.setColor(Color.green);
                g2.drawString("TIE", 380, 240);
            }
            if(mainFrame.checkWinner() == 1){
                g2.setColor(Color.blue);
                g2.drawString("PLAYER 1 WINS", 310, 240);
            }
            if(mainFrame.checkWinner() == 2){
                g2.setColor(Color.red);
                g2.drawString("PLAYER 2 WINS", 310, 240);
            }
            g2.setColor(Color.red);
            //boxes indicating current selection
            if(contSelection == "start"){
                g.fillRect(310,270,10,10);
            }
            if(contSelection == "menu"){
                g.fillRect(310,300,10,10);
            }
            if(contSelection == "quit"){
                g.fillRect(310,330,10,10);
            }
        }

        if(gameState.equals("p2WinScreen")){
            g2.setColor(Color.black);
            g.fillRect(0,0,800,800);	//black background
            g2.drawImage(RedWinIC,0,200,800,500,this);//red win image background
            g2.setFont(new Font("AR DESTINE", Font.PLAIN, 40));
            g2.setColor(Color.red);
            g2.drawString("PLAYER 2 IS THE CHAMPION", 120, 200);	//says they are the winner
            g2.setColor(Color.white);
            g2.setFont(new Font("AR DESTINE", Font.PLAIN, 25));
            //text for options
            g2.drawString("PLAY AGAIN?", 330, 680);
            g2.drawString("MENU", 330, 710);
            g2.drawString("QUIT", 330, 740);
            g2.setColor(Color.red);
            //boxes indicating current selection
            if(contSelection == "start"){
                g.fillRect(310,670,10,10);
            }
            if(contSelection == "menu"){
                g.fillRect(310,700,10,10);
            }
            if(contSelection == "quit"){
                g.fillRect(310,730,10,10);
            }
        }
        //same as p2 but with blue win background
        if(gameState.equals("p1WinScreen")){
            g2.setColor(Color.black);
            g.fillRect(0,0,800,800);
            g2.drawImage(BlueWinIC,0,200,800,500,this);
            g2.setFont(new Font("AR DESTINE", Font.PLAIN, 40));
            g2.setColor(Color.blue);
            g2.drawString("PLAYER 1 IS THE CHAMPION", 120, 200);
            g2.setColor(Color.white);
            g2.setFont(new Font("AR DESTINE", Font.PLAIN, 25));
            g2.drawString("PLAY AGAIN?", 330, 680);
            g2.drawString("MENU", 330, 710);
            g2.drawString("QUIT", 330, 740);
            if(contSelection == "start"){
                g.fillRect(310,670,10,10);
            }
            if(contSelection == "menu"){
                g.fillRect(310,700,10,10);
            }
            if(contSelection == "quit"){
                g.fillRect(310,730,10,10);
            }
        }

        if(gameState.equals("menu")){
            g2.drawImage(back,0,0,800,800,this);//main menu background
            g2.setColor(new Color(51,205,255));
            g2.setFont(new Font("AR DESTINE", Font.PLAIN, 70));
            g2.drawString("TRON", 317, 180);	//TRON title :D
            g2.setColor(new Color(51,255,115));
            g2.setFont(new Font("AR DESTINE", Font.PLAIN, 40));
            //text of different options
            g2.drawString("START", 330, 350);
            g2.drawString("HOW TO PLAY", 330, 400);
            g2.drawString("QUIT", 330, 450);
            g2.setColor(Color.red);
            //boxes indicating current selection
            if(menuSelection == "start"){
                g.fillRect(305,330,20,20);
            }
            if(menuSelection == "controls"){
                g.fillRect(305,380,20,20);
            }
            if(menuSelection == "quit"){
                g.fillRect(305,430,20,20);
            }
        }
        if(gameState.equals("controls")){
            g2.drawImage(ControlsIG,0,0,800,800,this); //the controls background image
            //appropriate block colours for each power up
            g2.setColor(Color.yellow);
            g2.fillRect(130,525,40,40);
            g2.setColor(new Color(125,24,71));
            g2.fillRect(130,590,40,40);
            g2.setColor(Color.red);
            g2.fillRect(130,655,40,40);
            g2.setColor(Color.blue);
            g2.fillRect(130,655,20,40);


            g2.setColor(new Color(51,255,115));
            g2.setFont(new Font("AR DESTINE", Font.PLAIN, 40));
            //menu is only option indicating its the only option
            g2.drawString("MENU", 80, 100);
            g2.setColor(Color.red);
            g2.fillRect(40,80,20,20);

        }
    }

    public void keyTyped(KeyEvent e) {}

    //checks which keys are pressed
    public void keyPressed(KeyEvent e) {
        keys[e.getKeyCode()] = true;
    }
    //checks which keys are released
    public void keyReleased(KeyEvent e) {
        keys[e.getKeyCode()] = false;
    }

}
