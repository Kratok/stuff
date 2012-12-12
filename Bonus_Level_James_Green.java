import java.awt.Cursor;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Point;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.util.Random;
import java.lang.Math;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseEvent;

public class BonusLevel_James_Green extends BonusLevel implements MouseMotionListener
{
  private BufferedImage buffer;
  private Graphics canvas;
  private FontMetrics metrics;
  private Random rand = new Random();
  private Font myFont = new Font("SansSerif",Font.BOLD, 12);

  private final int HEIGHT = 600;
  private final int WIDTH = 600;
  private static int MAX_DIST = 1000;
  private static byte MAX_PRIMES = 50;
  // Main source of score
  private int distTraveled;
  private byte primesHit;
  private byte collisions;
  //Easier reference to ammount of primes
  int[] primes = {2,  3, 5, 7, 11, 13, 17, 19, 23, 29,
                  31, 37, 41, 43, 47, 53, 59, 61, 67,
                  71, 73, 79, 83, 89, 97, 101, 103, 107, 109, 113};
  private int collisionBorder = WIDTH/4;
  private int frameCount;
  private boolean clickCheck;
  private int mouseX = 0;
  private int mouseY = 0;
  int textHeight;
  private Color background;

  private Meteor[] objects = new Meteor[20];
  
  static double objectACCEL = -.05;
  double objectVelocity = -2;
  int playerX = HEIGHT/2;
  int playerY = 20;
  private int score;
  
  public BonusLevel_James_Green()
  { 
    this.setSize(WIDTH,HEIGHT);
    buffer = new BufferedImage(WIDTH,HEIGHT, BufferedImage.TYPE_INT_RGB);
    canvas = buffer.getGraphics();
    canvas.setFont(myFont);
    metrics = canvas.getFontMetrics(myFont);
    textHeight = metrics.getHeight();
    this.addMouseMotionListener(new MouseMotionAdapter()
    {
    public void mouseMoved(MouseEvent evt) 
      {
        mouseX = (int) (evt.getPoint().x);
        mouseY = (int) (evt.getPoint().y);
        clickCheck = false;
      }
      public void mouseDragged(MouseEvent evt) 
      {
        mouseX = (int) (evt.getPoint().x);
        mouseY = (int) (evt.getPoint().y);
        clickCheck = true;
      }
    });
    // Transparent 16 x 16 pixel cursor image.
    BufferedImage cursorImg = new BufferedImage(1,1, BufferedImage.TYPE_INT_ARGB);
    // Create a new blank cursor.
    Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(
      cursorImg, new Point(0, 0), "blank cursor");
    // Set the blank cursor to the JFrame.
    setCursor(blankCursor);;
    
  }
  
  //Called whenever the user starts your bonus level.
  public void init()
  { 
    frameCount = 0;
    score = 0;
    distTraveled = 0;
    primesHit = 0;
    //Clear Frame
    background = new Color(0, 25, 25);
    canvas.setColor(background);
    canvas.fillRect(0, 0, WIDTH, HEIGHT);

    objectVelocity = -1;
    //Object Creation
    
    canvas.setColor(Color.RED);
    for (byte i = 0; i<20; i++) 
    {
      objects[i] = new Meteor();
      objects[i].drawMeteor();
    }
    canvas.setColor(Color.BLACK);
    playerX = WIDTH/4;
    playerY = HEIGHT/2;
    drawPolarCircle(playerX, playerY, 5);
  }
   //draws a circle with the coordinates in the middle
  private void drawPolarCircle(int x, int y, int radius)
  {
    canvas.fillOval(x-radius, y-radius, 2*radius, 2*radius);
  }
  //Called right after init() and every 1/50 of a second afterwards.
  // PrimeFactorAttack stops calling this method when it returns false.
  public boolean nextFrame()
  {
    frameCount++;
    distTraveled = (int) (distTraveled - objectVelocity);
    if (frameCount > 6000) return false;
    
    canvas.setColor(background);
    canvas.fillRect(0, 0, WIDTH, HEIGHT);
    canvas.setColor(Color.BLUE);
    
    
    
    //clickCheck is for holding down the mouse, to make the game harder, get more points
    if (clickCheck) objectVelocity += objectACCEL;
    drawPolarCircle(playerX, playerY, 5);
    playerX = (int) criticallyDampedSpring(mouseX, (float) playerX);
    playerY = (int) criticallyDampedSpring(mouseY, (float) playerY);
    drawPolarCircle(playerX, playerY, 5);
    
    canvas.setColor(Color.RED);
    for (byte i = 0; i<20; i++)
    { 
      objects[i].move((int)objectVelocity);
      objects[i].drawMeteor();
      objectCollisionCheck(i);
      if (objects[i].getX() < -10) objects[i] = new Meteor();
    }
    canvas.setColor(Color.WHITE);
    if (frameCount < 600) canvas.drawString("Hold down the mouse to accelerate! Collect primes! Dont crash! Go fast!",
        50, HEIGHT-50);
    
    this.repaint();
    
    return true;
  }
  public void objectCollisionCheck(int i)
  {
    int objectX = objects[i].getX();
    int objectY = objects[i].getY();
    if (objectX - playerX > -15 && objectX - playerX < 15)
      if (objectY - playerY > -15 && objectY - playerY <15)
      {
        if (objects[i].counter==0)
        {
          primesHit++;
          objects[i] = new Meteor();
        }
        else 
        {
          objectVelocity = 0;
          collisions++;
        }
      }
  }
    //Can be called anytime after init().
    //Will be called after nextFrame() returns false.
    //Return the current score between 0 and 100.
  public int getScore()
  { 
    score = ((distTraveled *75 / MAX_DIST) + (primesHit * 25 / MAX_PRIMES));
    if (score > 100) score = 100;
    return score;
  }
  public void paint(Graphics canvas)
  { 
    canvas.drawImage(buffer, 0, 0, null);
  }
  public void mouseMoved(MouseEvent evt) 
  {
    mouseX = (int) (evt.getPoint().x);
    mouseY = (int) (evt.getPoint().y);
    clickCheck = false;
  }
  public void mouseDragged(MouseEvent evt) 
  {
    mouseX = (int) (evt.getPoint().x);
    mouseY = (int) (evt.getPoint().y);
    clickCheck = true;
  }
  float criticallyDampedSpring( float a_Target, float a_Current)
  {
    float a_Velocity = (float) 0.2;
    float currentToTarget = a_Target - a_Current;
    float springForce = (float) (currentToTarget * .3);
    float dampingForce =  -a_Velocity * 2 * (float) Math.sqrt(.3);
    float force = springForce + dampingForce;
    a_Velocity += force;
    float displacement = a_Velocity;
    return a_Current + displacement;
  }
  public class Meteor 
  {
    public boolean isPrime;
    private int radius = 10;
    private int number;
    private int x;
    private int y;
    private byte counter = 0;
    private Color color;
    private byte[] primeList = new byte[7];
    Meteor()
    {
      x = rand.nextInt(collisionBorder)+WIDTH;
      y = rand.nextInt(HEIGHT);
      color = Color.red;
      primeList[counter] = (byte) primes[rand.nextInt(30)];
      number = primeList[counter];
      while(number < 100 && (rand.nextInt(4)!=0)) 
        {
          counter++;
          primeList[counter] = (byte) primes[rand.nextInt(30)];
          number = number * primeList[counter];
        };
    }
    public void drawMeteor()
    {
      canvas.setColor(color);
      canvas.fillOval(x-radius,y-radius,2*radius,2*radius);
      canvas.setColor(color.white);
      int adv = metrics.stringWidth(""+number);
      canvas.drawString(""+number,x-(adv/2),y+(textHeight/2));
    }
    public void move(int objectVelocity, int i)
    {
      x= x+objectVelocity;
      y= y+i;
      return;
    }
    public void move(int objectVelocity)
    {
      x= x+objectVelocity;
      return;
    }
    public int getY()
    {
      return y;
    }
    public int getX()
    {
      return x;
    }
    public void Shot(Meteor meteor)
    {
      if (counter!=0)
      {
        //Meteor(primeList[counter],x,y);
        number = number/primeList[counter];
        counter--;
      }
    }
    public int getNumber()
    {
      return number;
    }
  }
}
