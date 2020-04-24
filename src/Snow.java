
//**************************************
// Name: Snow Particles Animation
// Description: This is the simple program which helps a newbie to understand the concept of particles in Animation field.
// By: Deepak Monster
//**************************************

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

/**
 * Program       : Snow (Screen Saver) v0.1.3
 * Refactoring   : Sergej Shugajev (2020-04-24)
 * Original idea : Deepak Monster
 *               : http://www.planet-source-code.com/vb/scripts/ShowCode.asp?txtCodeId=7180
 */
public class Snow extends JFrame {
    
    Sky sky;
    Timer timer;
    final int TIMER_TICK = 1000 / 25; // 25 FPS 
    long fpsLastTime = 0;
    int fpsTick = 0, fpsTickSecond = 0;
    final boolean VIEW_FPS = false;
    final boolean USE_ANTIALIASING = true; // render use or not antialiasing for draw
    final int MAX_PARTICLES = 150;
    final int MAX_RADIUS = 12;
    
    boolean isKeyPressed = false, isMouseMoved = false;
    int oldMouseX = 0, oldMouseY = 0;
    
    public Snow() {
        setTitle("Snow");
        setUndecorated(true);
        GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice()
                .setFullScreenWindow(this); // for full screen in Linux
        setSize(Toolkit.getDefaultToolkit().getScreenSize());
        add(sky = new Sky());
        addMouseMotionListener(onMouseMoved());
        addKeyListener(onKeyPressed());
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        hideCursor();
        setVisible(true);
        (timer = timerRunner()).start();
    }
    
    /** Get the FPS ticks per second */
    public int getFpsTickSecond() { return fpsTickSecond; }
    
    /** Add one tick for FPS ticks (use: fpsLastTime, fpsTick, fpsTickSecond ) */
    public void addFpsTick() {
        long curTime = System.currentTimeMillis();;
        if (curTime >= fpsLastTime + 1000 || fpsLastTime == 0) { // 1 second
            fpsLastTime = curTime;
            if (fpsTick < fpsTickSecond) fpsTickSecond = fpsTick;
            fpsTick = 1;
        } else {
            fpsTick++;
            if (fpsTick >= fpsTickSecond) fpsTickSecond = fpsTick;
        }
    }
    
    public Timer timerRunner() {
        return new Timer(TIMER_TICK, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sky.changeParticlePositions();
                addFpsTick();
                sky.repaint();
                if (isKeyPressed || isMouseMoved)
                    System.exit(0);
            }
        });
    }
    
    public KeyAdapter onKeyPressed() {
        return new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getID() == KeyEvent.KEY_PRESSED)
                    isKeyPressed = true;
            }
        };
    }
    
    public MouseAdapter onMouseMoved() {
        return new MouseAdapter() {
            public void mouseMoved(MouseEvent e) {
                if (oldMouseX == 0 || oldMouseY == 0) {
                    oldMouseX = e.getX(); oldMouseY = e.getY();
                    return;
                }
                if (oldMouseX != e.getX() || oldMouseY != e.getY())
                    isMouseMoved = true;
            }
        };
    }
    
    public void hideCursor() {
        final Toolkit tk = getToolkit();
        final Cursor hidenCursor = tk.createCustomCursor(tk.getImage(""), new Point(), "hidenCursor");
        this.setCursor(hidenCursor);
    }
    
    class Sky extends JComponent {
        Color skyColor = new Color(107, 146, 185); // almost sky color
        int w, h; // width and height of screen
        float angle = 0;
        
        ArrayList<SnowParticle> particles; 
        // dint use array of SnowParticle objects because ArrayList has the special property which 
        // it can delete the element inside the array.
        
        public Sky() {
            setDoubleBuffered(true);
            w = Snow.this.getWidth();
            h = Snow.this.getHeight();
            
            // just storing some default random values for particles
            particles = new ArrayList<SnowParticle>();
            for(int i = 0; i < MAX_PARTICLES; i++)
                particles.add(new SnowParticle((int)(Math.random() * w), (int)(Math.random() * h),
                        (int)(Math.random() * MAX_RADIUS + 2), (int)(Math.random() * MAX_PARTICLES)));
        }
        
        public void paintComponent(Graphics g) {
            g.setColor(skyColor);
            g.fillRect(0, 0, w, h);
            
            // https://docs.oracle.com/javase/tutorial/2d/advanced/quality.html
            if (USE_ANTIALIASING) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            }
            
            // Lets Draw the particles on Screen
            for (SnowParticle p : particles) {
                g.setColor(new Color(255, 255, 255, p.A));
                g.fillOval(p.X, p.Y, p.R, p.R);
            }
            
            if (VIEW_FPS) {
                g.setColor(Color.WHITE);
                g.drawString("FPS: " + getFpsTickSecond(), 10, 18);
            }
            
            // flushes out all the Graphic Memorys ==> Smooth Rendering
            Toolkit.getDefaultToolkit().sync();
        }
        
        public void changeParticlePositions() {
            final int BORDER = 15;
            angle += 0.01; // it is in Radians
            for (SnowParticle p : particles) {
                p.Y += Math.round(Math.cos(angle + p.D)) + 2 + (p.R / 2);
                p.X += Math.round(Math.sin(angle) * 2) + 1;
                // just to create more randomness
                if (p.Y > h) {
                    p.Y = -BORDER;
                    p.X = (int)(Math.random() * w);
                }
                if (p.X > w + BORDER)
                    p.X = -BORDER;
                else if (p.X < -BORDER)
                    p.X = w + BORDER;
            }
        } 
        
        /** Stores the parameters of Snow particles */
        public class SnowParticle {
            public int X, Y, R, D, A;
            /** x-coordinate, y-coordinate, radius, density, alpha */
            public SnowParticle(int x, int y, int r, int d) {
                this.X = x; this.Y = y; this.R = r; this.D = d; setAlpha();
            }
            public void setAlpha() {
                this.A = (255 / MAX_RADIUS) * this.R;
                if (this.A > 255) this.A = 255;
            }
        }
        
    }
    
    public static void main(String args[]) {
        // https://support.microsoft.com/en-us/help/182383/info-screen-saver-command-line-arguments
                
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
         } catch (Exception e) {}
        
        String firstArgument = (args.length > 0) ? args[0].toLowerCase().trim() : "";
        if (firstArgument.startsWith("/s") || args.length == 0) {
            // Run the Screen Saver.
            new Snow();
        }
        if (firstArgument.startsWith("/p")) {
            // Preview Screen Saver as child of window <HWND>.
            // Not support in Java.
            return;
        }
        if (firstArgument.startsWith("/c")) {
            // Show the Settings dialog box, modal to the foreground window.
            String infoMessage = "This screen-save does not have configurable settings.";
            JOptionPane.showMessageDialog(null, infoMessage, "Configuration", JOptionPane.INFORMATION_MESSAGE);
        }
        
    }
    
}
