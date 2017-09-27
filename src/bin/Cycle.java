package bin;

/**
 * Created by Sajid on 2017-09-27.
 */
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
//Cycle Object which ontains all of its states as well its velocity and position
//controls how long powerups last

public class Cycle{
    private int x,y,dx,dy,prevX,prevY,numBoosts; //positions,velocities,previous positions,number of boosts
    private final int currSpeed = 5;			//speed or how many pixels the cycle moves each time
    //whether Cycle is boosting, has various powerups etc.
    private boolean boost = false,hasShield = false,lostShield = false,isCursed = false,switchInvunerability = false;
    //standard time limit for boost and curse
    private final int maxBoostLimit = 30,maxCurseLimit = 100;
    //current tick the boost time or curse time is on
    private int boostLimit = 30,curseLimit = 100;

    //initializes their x, y and direction of velocity
    public Cycle(int x,int y,int dir) {
        this.x = x;
        this.y = y;
        dx = dir;
        dy = 0;
        numBoosts = 3;	//starts with 3 boosts
    }
    //updates the x and y as well any powerup condition
    public void move(){
        //the x and y become the previous x and y
        prevX = x;
        prevY = y;
        //boost keeps going for set amount of time
        if(boost){
            boostTimeLimit();
        }
        //curse is active for set time
        if(isCursed){
            curseTimeLimit();
        }
        //when the shield is lost, theres not more shield
        if(lostShield){
            hasShield = false;
            lostShield = false;
        }
        //when the switch is active the invunerability lasts just as the cycle moves once and is false again
        if(switchInvunerability){
            switchInvunerability = false;
        }
        //moves x and y by direction(velocity)
        x += dx*currSpeed;
        y += dy*currSpeed;
    }

    public boolean isBoosting(){
        return boost;
    }

    public boolean isSwitching(){
        return switchInvunerability;
    }

    public void setSwitching(){
        switchInvunerability = true;
    }

    public int numBoosts(){
        return numBoosts;
    }
    //keeps boosting until boost time is done
    public void boostTimeLimit(){
        boostLimit --;
        if(boostLimit == 0){
            boostLimit = maxBoostLimit;
            boost = false;
        }
    }

    public void castCurse(){
        isCursed = true;
    }

    public boolean isCursed(){
        return isCursed;
    }
    //curse is kept until curse time is done
    public void curseTimeLimit(){
        curseLimit --;
        if(curseLimit == 0){
            curseLimit = maxCurseLimit;
            isCursed = false;
        }
    }
    //only allows boost as long as boost isnt already active and theres at least 1 boost left
    public void boost(){
        if(boost == false && numBoosts > 0){
            boost = true;
            numBoosts--;
        }
    }

    public void unBoost(){
        boost = false;
    }

    public boolean hasShield(){
        return hasShield;
    }

    public void obtainShield(){
        hasShield = true;
    }

    public void lostShield(){
        lostShield = true;
    }

    public int getX(){
        return x;
    }
    public void setX(int ix){
        x = ix;
    }

    public void setY(int iy){
        y = iy;
    }

    public int getY(){
        return y;
    }
    public int getVx(){
        return dx;
    }

    public int getVy(){
        return dy;
    }
    public int getPX(){
        return prevX;
    }

    public int getPY(){
        return prevY;
    }
    //only sets direction if there is a direction to set(its not 0)
    public void setVx(int dir){
        if(dir!= 0){
            dx = dir;
            dy = 0;
        }
    }

    public void setVy(int dir){
        if(dir!= 0){
            dy = dir;
            dx = 0;
        }

    }

}
