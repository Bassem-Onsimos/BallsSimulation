
package Game;

import GameEngine.AbstractGame;
import java.awt.BasicStroke;
import static java.awt.BasicStroke.CAP_ROUND;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

public class Game extends AbstractGame {
    
    Controller controller;

    public Game(int width, int height, float scale) {
        super(width, height, scale);
    }

    @Override
    public void initiate() {
        controller = new Controller(this);
        setFPSlimited(true);
        setDebugInfoDisplayed(false);
    }

    @Override
    public void update() {
        controller.update();
    }

    @Override
    public void render(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setStroke(new BasicStroke(3, CAP_ROUND, BasicStroke.JOIN_ROUND));
        controller.render(g);
    }
    
}
