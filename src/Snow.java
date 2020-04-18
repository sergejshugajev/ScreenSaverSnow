
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
 * Program       : Snow (Screen Saver)
 * Refactoring   : Sergej Shugajev (2020-04-17)
 * Original idea : Deepak Monster
 *               : http://www.planet-source-code.com/vb/scripts/ShowCode.asp?txtCodeId=7180
 */
public class Snow extends JFrame {
    
    Sky sky;
    Timer timer;
    final int MAX_PARTICLES = 150;
    final int MAX_RADIUS = 12;
    
    boolean isKeyPressed = false, isMouseMoved = false;
    int oldMouseX = 0, oldMouseY = 0;
    
    public Snow() {
        setTitle("Snow");
        setUndecorated(true);
        setSize(Toolkit.getDefaultToolkit().getScreenSize());
        add(sky = new Sky());
        addMouseMotionListener(mouseOnMoved());
        addKeyListener(keyOnPressed());
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        hideCursor();
        setVisible(true);
        (timer = timerRunner()).start();
    }
    
    public Timer timerRunner() {
        return new Timer(40, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sky.changeParticlePositions();
                sky.repaint();
                if (isKeyPressed || isMouseMoved)
                    System.exit(0);
            }
        });
    }
    
    public KeyAdapter keyOnPressed() {
        return new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getID() == KeyEvent.KEY_PRESSED)
                    isKeyPressed = true;
            }
        };
    }
    
    public MouseAdapter mouseOnMoved() {
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
            setDoubleBuffered(false);
            w = Snow.this.getWidth();
            h = Snow.this.getHeight();
            
            // just storing some default random values for particles
            particles = new ArrayList<SnowParticle>();
            for(int i = 0; i < MAX_PARTICLES; i++)
                particles.add(new SnowParticle((int)(Math.random() * w), (int)(Math.random() * h),
                        (int)(Math.random() * MAX_RADIUS + 2), (int)(Math.random() * MAX_PARTICLES)));
        }

        public void paintComponent(Graphics g) {
            setDoubleBuffered(true);
            g.setColor(skyColor);
            g.fillRect(0, 0, w, h);
            
            // https://docs.oracle.com/javase/tutorial/2d/advanced/quality.html
            // Smooth Renderings Ninja Technique
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
            g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
            g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
            
            // Lets Draw the particles on Screen
            for (SnowParticle p : particles) {
                g.setColor(new Color(255, 255, 255, p.A));                
                g2d.fillOval(p.X, p.Y, p.R, p.R);
            }
            
            // flushes out all the Graphic Memorys ==> Smooth Rendering
            Toolkit.getDefaultToolkit().sync();
        }
        
        public void changeParticlePositions() {
            final int BORDER = 15;
            angle += 0.01; // it is in Radians
            int i = 0;
            for (SnowParticle p : particles) {
                p.Y += Math.cos(angle + p.D) + 2 + (p.R / 2);
                p.X += Math.sin(angle) * 2 + 1;
                // just to create more randomness
                if (p.X > w + BORDER || p.X < -BORDER || p.Y > h) {
                    if (i % 3 > 0) {
                        p.Y = -BORDER;
                        p.X = (int)(Math.random() * w);
                    } else {
                        p.Y = (int)(Math.random() * h);
                        if (Math.sin(angle) > 0)
                            p.X = -BORDER;
                        else
                            p.X = w + BORDER;
                    }
                }
                i++;
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
