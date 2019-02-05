
package Model;

import Game.Game;
import GameEngine.Input;
import java.awt.Color;
import java.awt.Graphics2D;
import static java.lang.Math.abs;

public class Ball {
    
    Game game;
    
    float pX, pY;
    float vX, vY;
    float aX, aY;
    float radius;
    float mass;
    
    static float gravity = 0.0f;
    static float stable = 0.01f;
    
    float initialPx, initialPy;
    
    float remainingSimulationTime;
    
    boolean selected = false;
    boolean shooting = false;
    
    public Ball(float pX, float pY, float radius, Game game) {
        this.pX = pX;
        this.pY = pY;
        this.radius = radius;
        this.mass = radius * 10.0f;
        
        this.game = game;
    } 
    
    public void update() {
        
        Input input = game.getInput();
        
        if( input.isButtonDown(1) && pointInBounds(input.getMouseX(), input.getMouseY()) ) {
            selected = true;
        }
        
        if( input.isButtonDown(3) && pointInBounds(input.getMouseX(), input.getMouseY()) ) {
            shooting = true;
        }
        
        if( input.isButton(1) && selected) {
            pX = input.getMouseX();
            pY = input.getMouseY();
            
            clamp();
        }
        
        if(!input.isButton(1)) {
            selected = false;
        }
        
        if(input.isButtonUp(3) && shooting) {
            
            vX = 0.5f * ( pX - input.getMouseX() );
            vY = 0.5f * ( pY - input.getMouseY() );
            
            shooting = false;
        }
        
        initialPx = pX;
        initialPy = pY;
        
        aX = -vX * 0.05f;
        aY = -vY * 0.05f + gravity * mass;
        
        vX += aX* remainingSimulationTime;
        vY += aY* remainingSimulationTime;
        
        pX += vX * remainingSimulationTime;
        pY += vY * remainingSimulationTime;
        
        if(pX+radius <0)
            pX += (float) (game.getWidth());
        
        if(pY+radius <0)
            pY += (float) (game.getHeight());
        
        if(pX-radius >= game.getWidth())
            pX -= game.getWidth();
        
        if(pY-radius >= game.getHeight())
            pY -= game.getHeight();
        
        if(abs( vX * vX + vY * vY ) < stable){
            vX = 0;
            vY = 0;
        }
                
    }
    
    public void render(Graphics2D g) {
        
        Color color;
        
        if(selected || shooting) color = Color.red;
        else color = Color.white;
        
        g.setColor(color);
        
        g.drawOval((int)(pX - radius), (int)(pY - radius), (int)radius*2, (int)radius*2);
        
        if(shooting){
            g.setColor(color);
            g.drawLine((int)pX, (int)pY, game.getInput().getMouseX(), game.getInput().getMouseY());    
        }
    }
    
    public boolean pointInBounds(float x, float y) {
        return abs( ( (x - pX) * (x - pX) ) + ( (y - pY) * (y - pY) ) ) < (radius*radius);
    }
    
    public void clamp() {
        if (pX - radius < 0) pX = radius;       
        if (pY - radius < 0) pY = radius;       
        if (pX + radius >= game.getWidth()) pX = (float) (game.getWidth()) - radius;        
        if (pY + radius >= game.getHeight()) pY = (float) (game.getHeight()) - radius;      
    }

    public float getPX() {
        return pX;
    }

    public void setPX(float pX) {
        this.pX = pX;
    }

    public float getPY() {
        return pY;
    }

    public void setPY(float pY) {
        this.pY = pY;
    }

    public float getVX() {
        return vX;
    }

    public void setVX(float vX) {
        this.vX = vX;
    }

    public float getVY() {
        return vY;
    }

    public void setVY(float vY) {
        this.vY = vY;
    }

    public float getAX() {
        return aX;
    }

    public void setAX(float aX) {
        this.aX = aX;
    }

    public float getAY() {
        return aY;
    }

    public void setAY(float aY) {
        this.aY = aY;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public float getMass() {
        return mass;
    }

    public void setMass(float mass) {
        this.mass = mass;
    }

    public float getRemainingSimulationTime() {
        return remainingSimulationTime;
    }

    public void setRemainingSimulationTime(float remainingSimulationTime) {
        this.remainingSimulationTime = remainingSimulationTime;
    }

    public float getInitialPx() {
        return initialPx;
    }

    public void setInitialPx(float initialPx) {
        this.initialPx = initialPx;
    }

    public float getInitialPy() {
        return initialPy;
    }

    public void setInitialPy(float initialPy) {
        this.initialPy = initialPy;
    }
    
    
    
    
}
