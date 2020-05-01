
//**************************************
// Name: Snow Particles Animation
// Description: This is the simple program which helps a newbie to understand the concept of particles in Animation field.
// By: Deepak Monster
//**************************************

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * Program       : Snow (Screen Saver) v0.2.2
 * Refactoring   : Sergej Shugajev (2020-04-29)
 * Original idea : Deepak Monster
 *               : http://www.planet-source-code.com/vb/scripts/ShowCode.asp?txtCodeId=7180
 */
public class Snow extends JFrame {
    
    Fps fps;
    Sky sky;
    Timer timer;
    Container pane;
    final boolean VIEW_FPS = false;
    final boolean USE_ANTIALIASING = true; // render use or not antialiasing for draw
    final int MAX_PARTICLES = 300;
    final int MAX_RADIUS = 12;
    
    final boolean isDebug = java.lang.management.ManagementFactory.getRuntimeMXBean().
            getInputArguments().toString().indexOf("-agentlib:jdwp") > 0; // for test in debug
    
    public Snow() {
        System.getProperties().setProperty("sun.java2d.opengl", "true"); // force ogl
        setTitle("Snow");
        setResizable(false);
        setIgnoreRepaint(true);
        setUndecorated(true);
        if (!isDebug) GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice()
                .setFullScreenWindow(this); // for full screen in Linux
        setSize(Toolkit.getDefaultToolkit().getScreenSize());
        addMouseMotionListener(Events.onMouseMoved());
        addKeyListener(Events.onKeyPressed());
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        hideCursor();
        setVisible(true);
        fps = new Fps(25);
        sky = new Sky(getSize());
//        sky = new Sky(new Dimension(getSize().width/2, getSize().height/2)); // test w|h/2
        pane = getContentPane(); // for render
        (timer = timerRunner(fps.getTickMillis())).start(); // main loop and render
    }
    
    public synchronized Timer timerRunner(int tick) {
        return new Timer(tick, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sky.movePositions(); // old render
                sky.renderParticles();
                pane.getGraphics().drawImage(sky.getImage(), 0, 0, null);
                Toolkit.getDefaultToolkit().sync(); // synchronize screen /**/
/*                do sky.movePositions(); while (fps.isNeedUps()); // new render
                if (fps.isNeedPaint()) {
                    sky.renderParticles();
                    pane.getGraphics().drawImage(sky.getImage(), 0, 0, null);
                    Toolkit.getDefaultToolkit().sync(); // synchronize screen
                } /**/
                fps.check(); // check fps after render (and wait for update screen) 
                if (Events.isKeyPressed || Events.isMouseMoved)
                    System.exit(0);
            }
        });
    }
    
    /** Check FPS and UPS rate (to use: -> isNeedUps(), isNeedPaint(), check() <- loop)
     * @author Sergej Shugajev */
    class Fps {
        private final double ONE_SECOND = 1000;
        private final int MAX_FRAME_SKIPS = 5;
        private int framerate = 60; // 25 FPS (or 60 FPS)
        private double tickdelay = ONE_SECOND / framerate;
        private int upsTick, upsTickPerSecond, fpsTick, fpsTickPerSecond;
        private double startTime = 0, timeDiff, upsLimit;
        private boolean isPaint;
        Fps () {}
        Fps (int framerate) { this.framerate = framerate; tickdelay = ONE_SECOND / framerate; }
        private double getTime() { return System.currentTimeMillis(); }
        public int getTickMillis() { return (int) tickdelay; }
        public int getUpsPerSecond() { return upsTickPerSecond; }
        public int getFpsPerSecond() { return fpsTickPerSecond; }
        public void start() {
            if (startTime == 0) startTime = getTime();
            upsTick = 1; fpsTick = 1; isPaint = true;
        }
        private void calculate() {
            if (startTime == 0) start();
            timeDiff = getTime() - startTime;
            upsLimit = tickdelay * upsTick;
        }
        public boolean isNeedUps() {
            calculate();
            boolean isNeed = (timeDiff >= upsLimit && upsTick < framerate);
            if (isNeed) upsTick++;
            return isNeed;
        }
        public boolean isNeedPaint() {
            calculate();
            isPaint = (timeDiff < upsLimit || (timeDiff - upsLimit) >= tickdelay * MAX_FRAME_SKIPS);
            return isPaint;
        }
        public void check() {
            try { Thread.sleep(1); } catch (Exception e) {} // wait for update screen
            calculate();
            if (timeDiff >= ONE_SECOND || upsTick >= framerate) {
                upsTickPerSecond = upsTick; fpsTickPerSecond = fpsTick;
                startTime = 0; start(); // restart
            } else {
                upsTick++;
                if (isPaint) fpsTick++;
            }
        }
    }
    
    class Sky {
        Color skyColor = new Color(107, 146, 185); // almost sky color
        int w, h; // width and height of screen
        float angle = 0;
        BufferedImage buffer; // shadow image for render
        
        ArrayList<SnowParticle> particles; 
        // dint use array of SnowParticle objects because ArrayList has the special property which
        // it can delete the element inside the array
        
        public Sky(Dimension d) {
            w = d.width; h = d.height;            
            buffer = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            // just storing some default random values for particles
            particles = new ArrayList<SnowParticle>();
            for(int i = 0; i < MAX_PARTICLES; i++)
                particles.add(new SnowParticle((int)(Math.random() * w), (int)(Math.random() * h),
                        (int)(Math.random() * MAX_RADIUS + 2), (int)(Math.random() * MAX_PARTICLES)));
        }
        
        /** Shadow screen for rendering */
        public BufferedImage getImage() { return buffer; }
        
        /** Render snowflakes on the shadow screen */
        public void renderParticles() {
            Graphics2D g = (Graphics2D) buffer.getGraphics();
            g.setColor(skyColor);
            g.fillRect(0, 0, w, h);
            // https://docs.oracle.com/javase/tutorial/2d/advanced/quality.html
            if (USE_ANTIALIASING) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            }
            // Lets Draw the particles on Shadow Screen
            for (SnowParticle p : particles) {
                g.setColor(new Color(255, 255, 255, p.A));
                g.fillOval(p.X, p.Y, p.R, p.R);
            }
            if (VIEW_FPS) {
                g.setColor(Color.WHITE);
                g.drawString("FPS/UPS: " + fps.getFpsPerSecond() + "/" + fps.getUpsPerSecond(), 10, 18);
            }
        }
        
        /** Move the position of the snowflakes (in ArrayList) */
        public void movePositions() {
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
    
    /** Application events (key, mouse) */
    static class Events {
        static public boolean isKeyPressed = false, isMouseMoved = false;
        static int oldMouseX = 0, oldMouseY = 0;
        
        static public KeyAdapter onKeyPressed() {
            return new KeyAdapter() {
                public void keyPressed(KeyEvent e) {
                    if (e.getID() == KeyEvent.KEY_PRESSED)
                        isKeyPressed = true;
                }
            };
        }
        
        static public MouseAdapter onMouseMoved() {
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
    }
    
    /** Create hidden cursor and hide mouse */
    public void hideCursor() {
        final Toolkit tk = getToolkit();
        final Cursor hidenCursor = tk.createCustomCursor(tk.getImage(""), new Point(), "hidenCursor");
        this.setCursor(hidenCursor);
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
