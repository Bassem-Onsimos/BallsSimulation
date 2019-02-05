package Game;

import Model.Ball;
import Model.Edge;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import static java.lang.Math.abs;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;
import java.util.Random;
import java.util.Vector;
import javafx.util.Pair;

public class Controller {

    Game game;

    private ArrayList<Ball> balls = new ArrayList<>();
    private ArrayList<Edge> edges = new ArrayList<>();
    
    private Random rand = new Random();

    public Controller(Game game) {
        this.game = game;

        for(int i=0; i<20; i++) {
            balls.add(new Ball(rand.nextInt((int)(game.getWidth())) , rand.nextInt((int)(game.getHeight())) , rand.nextInt(60) + 10 , game));
        }
        
        float edgeRadius = 1;
        float width = (float)(game.getWidth()) - edgeRadius;
        float height = (float)(game.getHeight()) - edgeRadius;
        
        edges.add(new Edge(edgeRadius, edgeRadius,  width, edgeRadius , edgeRadius, game));
        
        edges.add(new Edge(edgeRadius, edgeRadius, edgeRadius, height, edgeRadius, game));
        
        edges.add(new Edge(width, edgeRadius, width, height, edgeRadius, game));
        
        edges.add(new Edge(edgeRadius, height, width, height, edgeRadius, game));
    }

    public void update() {
        
        int maxSimulationSteps = 15;
        int simulationUpdates = 5;
        float simulationElapsedTime = game.getElapsedTime() / (float)simulationUpdates;
        
        for(int i=0; i<simulationUpdates; i++) {
            
            for (Ball ball : balls) {
                ball.setRemainingSimulationTime(simulationElapsedTime);
            }
            
            for(int j=0; j<maxSimulationSteps; j++) {
                
                for (Ball ball : balls) {
                    if(ball.getRemainingSimulationTime() > 0)
                        ball.update();
                }
                
                for(Edge edge : edges) {
                    edge.update();
                }

                resolveCollisions();
            }
        }
        
    }

    public void render(Graphics2D g) {
        for (Ball ball : balls) {
            ball.render(g);
        }

        g.setColor(Color.gray);
        
        for (Ball ball : balls) {
            for (Ball target : balls) {
                if ((ball != target) && ballsOverlap(ball, target)) {
                    g.drawLine((int) ball.getPX(), (int) ball.getPY(), (int) target.getPX(), (int) target.getPY());
                }
            }
        }
        
        for(Edge edge : edges)
            edge.render(g);
    }

    public void resolveCollisions() {
        
        Vector<Pair<Ball, Ball>> collidingBalls = new Vector<>();

        for (Ball ball : balls) {
            
            for(Edge edge : edges) {
                
                float lineX1 = edge.getEndX() - edge.getStartX();
                float lineY1 = edge.getEndY() - edge.getStartY();
                
                float lineX2 = ball.getPX() - edge.getStartX();
                float lineY2 = ball.getPY() - edge.getStartY();
                
                float edgeLength = (float)pow(lineX1, 2) + (float)pow(lineY1, 2);
                
                float t = max(0, min(edgeLength, (lineX1 * lineX2 + lineY1 * lineY2))) / edgeLength;
                
                float closestPointX = edge.getStartX() + t * lineX1;
                float closestPointY = edge.getStartY() + t * lineY1;
                
                float distance = (float)sqrt( pow( ball.getPX() - closestPointX , 2 ) + pow( ball.getPY() - closestPointY , 2) );
                
                if(distance <= ball.getRadius() + edge.getRadius()) {
                    //collision
                    
                    Ball imaginaryBall = new Ball(closestPointX, closestPointY, edge.getRadius(), game);
                    imaginaryBall.setMass(ball.getMass() * 0.8f);
                    imaginaryBall.setVX(- ball.getVX());
                    imaginaryBall.setVY(- ball.getVY());
                    
                    collidingBalls.add(new Pair(ball, imaginaryBall));
                    
                    float overlap = distance - ball.getRadius() - imaginaryBall.getRadius();
                    
                    ball.setPX(ball.getPX() - (overlap * (ball.getPX() - imaginaryBall.getPX()) / distance));
                    ball.setPY(ball.getPY() - (overlap * (ball.getPY() - imaginaryBall.getPY()) / distance));
                    
                }
            }
            
            for (Ball target : balls) {
                if ((ball != target) && ballsOverlap(ball, target)) {

                    collidingBalls.add(new Pair(ball, target));

                    //static collisions with other balls
                    float distance = (float) sqrt(pow(ball.getPX() - target.getPX(), 2) + pow(ball.getPY() - target.getPY(), 2));

                    float overlap = 0.5f * (distance - ball.getRadius() - target.getRadius());

                    ball.setPX(ball.getPX() - (overlap * (ball.getPX() - target.getPX()) / distance));
                    ball.setPY(ball.getPY() - (overlap * (ball.getPY() - target.getPY()) / distance));

                    target.setPX(target.getPX() + (overlap * (ball.getPX() - target.getPX()) / distance));
                    target.setPY(target.getPY() + (overlap * (ball.getPY() - target.getPY()) / distance));
                    
                    ball.clamp();
                    target.clamp();
                    
                    //time displacement
                    float intendedSpeed = (float) sqrt( pow(ball.getVX() , 2) + pow(ball.getVY() , 2));
                    float actualDistance = (float) sqrt( pow( ball.getPX() - ball.getInitialPx() , 2) + pow( ball.getPY() - ball.getInitialPy() , 2)); 
                    float actualTime = actualDistance / intendedSpeed;
                    
                    ball.setRemainingSimulationTime(ball.getRemainingSimulationTime() - actualTime);
                }
            }
        }

        //dynamic collisions
        for (Pair<Ball, Ball> pair : collidingBalls) {
            Ball b1 = pair.getKey();
            Ball b2 = pair.getValue();

            float distance = (float) sqrt(pow(b1.getPX() - b2.getPX(), 2) + pow(b1.getPY() - b2.getPY(), 2));
            
            //normal
            float nx = (b2.getPX() - b1.getPX()) / distance;
            float ny = (b2.getPY() - b1.getPY()) / distance;

            // Tangent
            float tx = -ny;
            float ty = nx;

            // Dot Product Tangent
            float dpTan1 = b1.getVX() * tx + b1.getVY() * ty;
            float dpTan2 = b2.getVX() * tx + b2.getVY() * ty;

            // Dot Product Normal
            float dpNorm1 = b1.getVX() * nx + b1.getVY() * ny;
            float dpNorm2 = b2.getVX() * nx + b2.getVY() * ny;

            // Conservation of momentum in 1D
            float m1 = (dpNorm1 * (b1.getMass() - b2.getMass()) + 2.0f * b2.getMass() * dpNorm2) / (b1.getMass() + b2.getMass());
            float m2 = (dpNorm2 * (b2.getMass() - b1.getMass()) + 2.0f * b1.getMass() * dpNorm1) / (b1.getMass() + b2.getMass());
            
            // Update ball velocities
            b1.setVX(tx * dpTan1 + nx * m1);
            b1.setVY(ty * dpTan1 + ny * m1);
            b2.setVX(tx * dpTan2 + nx * m2);
            b2.setVY(ty * dpTan2 + ny * m2);

        }
        
    }

    public boolean ballsOverlap(Ball ball1, Ball ball2) {
        float x1 = ball1.getPX();
        float x2 = ball2.getPX();
        float y1 = ball1.getPY();
        float y2 = ball2.getPY();
        float r1 = ball1.getRadius();
        float r2 = ball2.getRadius();

        return abs(pow(x1 - x2, 2) + pow(y1 - y2, 2)) <= pow(r1 + r2, 2);
    }

}
