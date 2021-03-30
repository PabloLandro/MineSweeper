/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minesweeper;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.Random;
/**
 *
 * @author landr
 */
public class GamePanel extends JPanel implements ActionListener{
    final static int SCREEN_WIDTH = 600;
    final static int SCREEN_HEIGHT = 660;
    final static int UNIT_SIZE = 60;
    Tile mineArray[][] = new Tile[SCREEN_WIDTH/UNIT_SIZE][(SCREEN_HEIGHT-60)/UNIT_SIZE];
    int mines = 0;
    int defused = 0;
    int marked = 0;
    boolean running = true;
    boolean win = false;
    Random random;
    Timer timer;
    
    GamePanel(){
        this.addMouseListener(new MyMouseAdapter());
        random = new Random();
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.black);
        this.setFocusable(true);
        startGame();
        timer = new Timer(1, this);
        timer.start();
    }
    
    public void startGame(){
        //Tile creation
        for(int i = 0; i < SCREEN_WIDTH/UNIT_SIZE; i++)
            for(int j = 0; j < (SCREEN_HEIGHT-60)/UNIT_SIZE; j++){
                if(random.nextInt(8) != 6){
                    mineArray[i][j] = new Tile(false);
                }
                else{
                    mineArray[i][j] = new Tile(true);
                    mines++;
                }
            }
        for(int i = 0; i < SCREEN_WIDTH/UNIT_SIZE; i++)
            for(int j = 0; j < (SCREEN_HEIGHT-60)/UNIT_SIZE; j++)
                if(!mineArray[i][j].getMine())
                    for(int h = -1; h <= 1; h++)
                        for(int k = -1; k <= 1; k++)
                            if((i+h >= 0) && (j+k >= 0) && (i+h < SCREEN_WIDTH/UNIT_SIZE) && (j+k < (SCREEN_HEIGHT-60)/UNIT_SIZE))
                                if(mineArray[i+h][j+k].getMine())
                                    mineArray[i][j].setAdyacents(mineArray[i][j].getAdyacents() + 1);
                
    }
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        draw(g);
    }
    public void draw(Graphics g){
        if(running){
            //GRID
            for(int i = 0; i < SCREEN_WIDTH/UNIT_SIZE; i++)
                g.drawLine(i*UNIT_SIZE, 60, i*UNIT_SIZE, SCREEN_HEIGHT);
            for(int i = 0; i < SCREEN_HEIGHT/UNIT_SIZE; i++)
                g.drawLine(0, i*UNIT_SIZE, SCREEN_WIDTH, i*UNIT_SIZE);
            
            //COUNTERS
            g.setColor(Color.white);
            g.setFont(new Font("Comic Sans MS", Font.BOLD, 40));
            FontMetrics metrics = getFontMetrics(g.getFont());
            g.drawString("Mines: " + mines + "    Marked: " + marked, 0, 50);
            
            //TILES
            for(int i = 0; i < SCREEN_WIDTH/UNIT_SIZE; i++)
                for(int j = 0; j < (SCREEN_HEIGHT-60)/UNIT_SIZE; j++){
                    if((!mineArray[i][j].getMine()) && mineArray[i][j].getRevealed()){
                        g.setColor(Color.green);
                        g.setFont(new Font("Comic Sans MS", Font.BOLD, 40));
                        g.drawString(""+mineArray[i][j].getAdyacents(), (i*UNIT_SIZE)+UNIT_SIZE/4, (j*UNIT_SIZE)+UNIT_SIZE*3/4+60);
                    }
                    else if(mineArray[i][j].getMine() && mineArray[i][j].getRevealed()){
                        g.setColor(Color.red);
                        g.fillOval((i*UNIT_SIZE)+5, (j*UNIT_SIZE)+5+60, UNIT_SIZE-10, UNIT_SIZE-10);
                        gameOver();
                    }
                    else if(mineArray[i][j].getMarked()){
                        g.setColor(Color.cyan);
                        g.fillRect((i*UNIT_SIZE)+10, (j*UNIT_SIZE)+10+60, UNIT_SIZE-20, UNIT_SIZE-20);
                    }
                    else{
                        g.setColor(Color.darkGray);
                        g.fillRect((i*UNIT_SIZE)+5, (j*UNIT_SIZE)+5+60, UNIT_SIZE-10, UNIT_SIZE-10);
                    }
                }
        }
        else if(!win){            
            g.setColor(Color.red);
            g.setFont(new Font("Comic Sans MS", Font.BOLD, 40));
            FontMetrics metrics = getFontMetrics(g.getFont());
            g.drawString("GameOver", (SCREEN_WIDTH-metrics.stringWidth("GameOver"))/2, SCREEN_HEIGHT/2);
            timer.stop();
        }
        else if(win){
            g.setColor(Color.green);
            g.setFont(new Font("Comic Sans MS", Font.BOLD, 40));
            FontMetrics metrics = getFontMetrics(g.getFont());
            g.drawString("You Win!", (SCREEN_WIDTH-metrics.stringWidth("You Win!"))/2, SCREEN_HEIGHT/2);
            timer.stop();
        }
    }
    
    public void revealAdyacents(int i, int j){
        for(int h = -1; h <= 1; h++){
            for(int k = -1; k <= 1; k++){
                if((i+h >= 0) && (i+h < SCREEN_WIDTH/UNIT_SIZE) && (j+k >= 0) && (j+k < (SCREEN_HEIGHT-60)/UNIT_SIZE)){
                    if((!mineArray[i+h][j+k].getMine()) && (!mineArray[i+h][j+k].getRevealed())){
                        mineArray[i+h][j+k].setRevealed(true);
                        if(mineArray[i+h][j+k].getAdyacents() == 0)
                            revealAdyacents(i+h, j+k);
                    }
                }
            }
        }
    }
    
    public void checkWin(){
        if(defused == mines)
        {
            win = true;
            running = false;
        }
    }
    
    public void gameOver(){
        running = false;
    }
    
    @Override
    public void actionPerformed(ActionEvent e){
        repaint();
    }
    
    public class MyMouseAdapter extends MouseAdapter{
        
        @Override
        public void mouseClicked(MouseEvent e){
            int x, y, xTile = 0, yTile = 0;
            x = e.getX();
            y = e.getY()-60;
            while(x > UNIT_SIZE){
                xTile++;
                x -= UNIT_SIZE;
            }
            while(y > UNIT_SIZE){
                yTile++;
                y -= UNIT_SIZE;
            }
            if(SwingUtilities.isLeftMouseButton(e) && (!mineArray[xTile][yTile].getMarked())){
                mineArray[xTile][yTile].setRevealed(true);
                if((!mineArray[xTile][yTile].getMine()) && (mineArray[xTile][yTile].getAdyacents() == 0))
                    revealAdyacents(xTile, yTile);
            }
            else if(SwingUtilities.isRightMouseButton(e) && (!mineArray[xTile][yTile].getRevealed())){
                if(mineArray[xTile][yTile].getMarked()){
                    mineArray[xTile][yTile].setMarked(false);
                    marked--;
                }
                else{
                    mineArray[xTile][yTile].setMarked(true);
                    marked++;
                }
            }
            checkWin();
        }
    }
    
    
    public class Tile{
        int adyacents;
        boolean isMine;
        boolean revealed;
        boolean marked;
        
        Tile(boolean Mine){
            this.isMine = Mine;
            this.adyacents = 0;
            this.revealed = false;
            marked = false;
        }
        public int getAdyacents(){
            return adyacents;
        }
        public boolean getMine(){
            return isMine;
        }
        public boolean getRevealed(){
            return revealed;
        }
        public boolean getMarked(){
            return marked;
        }
        public void setAdyacents(int i){
            this.adyacents = i;
        }
        public void setRevealed(boolean b){
            this.revealed = b;
        }
        public void setMarked(boolean b){
            this.marked = b;
            if (isMine && b)
                defused++;
            else if(isMine && !b)
                defused--;
        }
    }
    
}
