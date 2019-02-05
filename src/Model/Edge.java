
package Model;

import Game.Game;
import GameEngine.Input;
import java.awt.Color;
import java.awt.Graphics2D;
import static java.lang.Math.abs;
import static java.lang.Math.sqrt;

public class Edge {
    
    private float startX, startY;
    private float endX, endY;
    private float radius;
    
    private boolean startSelected = false;
    private boolean endSelected = false;

    private Game game;
    
    public Edge(float startX, float startY, float endX, float endY, float radius, Game game) {
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
        this.radius = radius;
    
        this.game = game;
    }
    
    public void update() {
        
        Input input = game.getInput();
        
        if(input.isButtonDown(1)) {
            if(pointInBounds(startX, startY, input.getMouseX(), input.getMouseY()))
                startSelected = true;
            
            else if(pointInBounds(endX, endY, input.getMouseX(), input.getMouseY()))
                endSelected = true;
                
        }
        
        if(input.isButton(1)) {
            if(startSelected) {
                startX = input.getMouseX();
                startY = input.getMouseY();
                
                if(startX - radius <= 0) startX = radius;
                if(startY - radius <= 0) startY = radius;
                if(startX + radius >= game.getWidth()) startX = game.getWidth() - radius;
                if(startY + radius >= game.getHeight()) startY = game.getHeight() - radius;
                
            }
            else if(endSelected) {
                endX = input.getMouseX();
                endY = input.getMouseY();
                
                if(endX - radius <= 0) endX = radius;
                if(endY - radius <= 0) endY =  radius;
                if(endX + radius >= game.getWidth()) endX = game.getWidth() - radius;
                if(endY + radius >= game.getHeight()) endY = game.getHeight() - radius;
            }
        }
        
        if(!input.isButton(1)) {
            startSelected = false;
            endSelected = false;
        }
        
    }
    
    public void render(Graphics2D g) {
        
        float nx = - (endY - startY);
        float ny = endX - startX;
        
        float d = (float)sqrt(nx*nx + ny*ny);
        
        nx /= d;
        ny /= d;
        
        if(startSelected || endSelected) g.setColor(Color.red);
        else g.setColor(Color.white);
        
        g.fillOval((int)(startX - radius), (int)(startY - radius), (int)radius*2, (int)radius*2);
        g.fillOval((int)(endX - radius), (int)(endY - radius), (int)radius*2, (int)radius*2);
        
        g.drawLine((int)(startX + nx * radius), (int)(startY + ny * radius), (int)(endX + nx * radius), (int)(endY + ny * radius));
        g.drawLine((int)(startX - nx * radius), (int)(startY - ny * radius), (int)(endX - nx * radius), (int)(endY - ny * radius));
    }
    
    public boolean pointInBounds(float circleX, float circleY,float pointX, float pointY) {
        return abs(( (pointX - circleX) * (pointX - circleX) ) + ( (pointY - circleY) * (pointY - circleY) ) ) < (radius*radius);
    }

    public float getStartX() {
        return startX;
    }

    public void setStartX(float startX) {
        this.startX = startX;
    }

    public float getStartY() {
        return startY;
    }

    public void setStartY(float startY) {
        this.startY = startY;
    }

    public float getEndX() {
        return endX;
    }

    public void setEndX(float endX) {
        this.endX = endX;
    }

    public float getEndY() {
        return endY;
    }

    public void setEndY(float endY) {
        this.endY = endY;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }
    
    
}
