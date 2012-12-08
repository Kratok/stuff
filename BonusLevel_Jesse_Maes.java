// Jesse Maes
// Quantum Fractal Cipherspace based Prime Factorization Game!
// Writing this was ridiculously fun!

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.PointerInfo;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Random;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;
import java.awt.RenderingHints;
import java.awt.MouseInfo;

@SuppressWarnings("serial")
public class BonusLevel_Jesse_Maes extends BonusLevel implements MouseMotionListener
{

  // Best thing I picked up from the histogram assignment!
  // So I can return two ints in one method!
  public class TwoDatas
  { public int d, e;
  }
  
  private BufferedImage buffer;
  private Graphics canvas;
  private Random rand = new Random();
  private Image background;
  private final int WIDTH = 752;
  private final int HEIGHT = 575;

  private Font mono = new Font(Font.MONOSPACED, Font.PLAIN, 26);
  private Font big = new Font(Font.SANS_SERIF, Font.BOLD, 46);
  private Font little = new Font(Font.SANS_SERIF, Font.PLAIN, 26);
  
  private String msg, crypt, answer;
  private String[] crypto;
  
  private Point[] lightning = new Point[25];
  
  private int p, q, N, PHI, e, d;
  private BigInteger bigE, bigD, bigN;
  private String theE, theD, theN, theP, theQ;
  
  private Point ans1, ans2;
  private Point cursorPos = new Point(370, 130);
  //private boolean firstRun = true;
  private boolean foundP = false;
  private boolean foundQ = false;
  private boolean justOncep = true;
  private boolean justOnceq = true;
  private boolean justOncet = true;
  private int score = 0;
  private int penalty, frameCount;
  private long initTime, startCount, timeSinceMouseMoved;
  
  private Color[] hexColor =
  {
      new Color(12, 22, 2), new Color(22, 67, 7), new Color(24, 114, 9),
      new Color(67, 21, 0), new Color(63, 118, 10), new Color(65, 73, 6),
      new Color(117, 29, 1), new Color(170, 36, 1), new Color(72, 164, 14),
      new Color(26, 163, 13), new Color(118, 67, 5), new Color(206, 42, 3),
      new Color(76, 211, 19), new Color(166, 64, 4), new Color(116, 118, 9),
      new Color(221, 61, 7),
  };
  
  private int hexrad = 32;
  private int cos = Math.round((float) (Math.sqrt(3) * hexrad / 2));
  private int sin = Math.round((float) (hexrad / 2));
  private int hexx = 32 + cos;
  private int hexy = 1 + 2 * sin;
  private Point[] hexPoints =
  {
      new Point(hexx + cos, hexy), new Point(hexx, hexy + 3 * sin),
      new Point(hexx + 2 * cos, hexy + 3 * sin),
      new Point(hexx + cos, hexy + 6 * sin), new Point(hexx, hexy + 9 * sin),
      new Point(hexx + 2 * cos, hexy + 9 * sin),
      new Point(hexx + cos, hexy + 12 * sin), new Point(hexx, hexy + 15 * sin),
      new Point(hexx + 2 * cos, hexy + 15 * sin),
      new Point(hexx + cos, hexy + 18 * sin), new Point(hexx, hexy + 21 * sin),
      new Point(hexx + 2 * cos, hexy + 21 * sin),
      new Point(hexx + cos, hexy + 24 * sin),
  };

  // Change the array size here to get bigger primes!
  private int[] primes = new int[64];
  private String[] somePrimes = new String[hexPoints.length];
  
  private static String[] MESSAGE =
  {
      "We come in peace", "Televerket??", "Interrobang!",
      "Cameron is here!", "Sociociphernetics!", "Data is essential!",
      "Data must flow", "Data is free", "Love Data!", "Datalove<3",
  };
  
  private char[] charMap =
  {
      ' ', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I',
      'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S',
      'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c',
      'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
      'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w',
      'x', 'y', 'z', '.', ',', ':', ';', '\'', '"', '`',
      '!', '@', '#', '$', '%', '^', '&', '*', '-', '+',
      '(', ')', '[', ']', '{', '}', '?', '/', '<', '>',
      '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
  };
  private String[] numMap = new String[charMap.length];
  
  public BonusLevel_Jesse_Maes()
  {
    // Absolute necessities, JFrame size, canvas, buffer, and MouseMotionListener
    this.setSize(WIDTH, HEIGHT);
    buffer = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
    canvas = buffer.getGraphics();
    addMouseMotionListener(this);
    
    // Populate the num-map array that corresponds to charMap
    genNumMap(numMap);
    // Get all the prime numbers we can manage into an array!
    genPrimes(primes);
    int limit = primes.length / 2;
    
    // Generate random primes p and q, ensure they are different!
    p = primes[limit + rand.nextInt(limit)];
    q = p;
    while (q == p)
      q = primes[limit + rand.nextInt(limit)];
    
    // Calculate N and PHI
    N = p * q;
    PHI = (p - 1) * (q - 1);
    
    // And get the exponenets for the valid keypair
    TwoDatas ED = getEandD(N, PHI);
    e = ED.e;
    d = ED.d;
    
    //I need BigInteger equivalents of these for RSA encryption/decryption
    bigE = BigInteger.valueOf((long) e);
    bigD = BigInteger.valueOf((long) d);
    bigN = BigInteger.valueOf((long) N);
    
    //And strings of all of them, for drawing!
    theE = bigE.toString();
    theD = bigD.toString();
    theN = bigN.toString();
    theP = "" + p;
    theQ = "" + q;
    
    // Make to random index values, ensure they are different
    int rand1 = rand.nextInt(hexPoints.length);
    int rand2 = rand1;
    while (rand2 == rand1)
      rand2 = rand.nextInt(hexPoints.length);
    
    // Add the correct answers to the factorization, P and Q, to a
    // String array, for drawing.
    somePrimes[rand1] = theP;
    somePrimes[rand2] = theQ;
    
    // Fill the rest of the array with random other primes, to make the
    // multiple choices truly random.
    String prime;
    int iprime = 1;
    for (int i = 0; i < somePrimes.length; i++)
    {
      iprime = primes[rand.nextInt(primes.length)];
      while (iprime == p || iprime == q)
        iprime = primes[rand.nextInt(primes.length)];
      prime = "" + iprime;
      
      if (i != rand1 && i != rand2) somePrimes[i] = prime;
    }
    
    // The correct points to mousover are now set into memory
    ans1 = hexPoints[rand1];
    ans2 = hexPoints[rand2];
    
    // Get a random message to encrypt, then encrypt it!
    msg = MESSAGE[rand.nextInt(MESSAGE.length)];
    crypt = "";
    crypto = encryptMessage(msg);
    
    // encryptMessage returns a String array. (I need to break the ciphertext up to avoid)
    // memory errors. This makes a string of the entire array front to back. For drawing!
    for (int i = 0; i < crypto.length; i++)
      crypt = crypt + crypto[i];
    
    // Get the answer by decrypting the encrypted message array
    answer = decryptMessage(crypto);
    
    // If the answer is not equal to the original message, the program becomes an hero
    boolean assertion = answer.equals(msg);
    assert assertion;
    
    // About to start! I need to get the time!
    initTime = System.currentTimeMillis();
  }
  
  // Called whenever the user starts your bonus level.
  public void init()
  {
    frameCount = 0;
    
    // This background image sets the stage of quantum fractal cipherspace!
    // I made this myself in GIMP, the symbol is that of Telecomix.
    String str = "resources/background-BonusLevel.png";
    java.net.URL imageURL = BonusLevel_Jesse_Maes.class.getResource(str);
    background = new ImageIcon(imageURL).getImage();
    canvas.drawImage(background, 0, 0, null);
    this.repaint();
    
    // If a welcome screen is welcome, I have nice one ready made!
    // I'm assuming you don't need it.
    
    /*long curTime = System.currentTimeMillis();
    while (firstRun)
    {
      canvas.setColor(Color.RED);
      canvas.setFont(big);
      canvas.drawString("Welcome to Fractal Cipherspace!", 10, 50);
      
      canvas.setFont(little);
      canvas.drawString("You only have two minutes!", 10, 370);
      canvas.setColor(Color.ORANGE);

      canvas.drawString("Below we offer you this mighty gift : our favorite ciphertext!", 10, 100);
      canvas.drawString("(We wrapped it in RSA for you, to keep it safe :)", 10, 130);
      canvas.drawString("To read our message, just find the private key! : (d, N)", 10, 210);
      canvas.drawString("The value of \'d\' is hidden within the prime factors of \'N\'!", 10, 240);      
      canvas.drawString("Master the Prime Factors, and share the data with the world!", 10, 270);
      canvas.drawString("If you hover near them, the fractal laser light will guide your way.", 10, 300);
      canvas.drawString("Good luck, young cryptonaut. And make haste!", 10, 330);

      canvas.setColor(Color.BLACK);
      canvas.drawString("Our public key is (e = " + theE + ", N = " + theN + ")", 11, 171);
      canvas.drawString("You only have two minutes!", 11, 371);
      
      canvas.setColor(Color.GREEN);
      canvas.drawString("Our public key is (e = " + theE
          +", N = " + theN + ")", 10, 170);
      canvas.setFont(mono);
      canvas.drawString(">" + crypt, 12, 480);
      
      this.repaint();
      
      long deltaTime = System.currentTimeMillis() - curTime;
      if (deltaTime > 25000)
      {
        firstRun = false;
      }
      timeSinceMouseMoved = 0;
    }*/
    
    // Antialiasing: If it's slow, comment this out.
    Graphics2D canvas2 = (Graphics2D) canvas;
    canvas2.setRenderingHint(
        RenderingHints.KEY_ANTIALIASING,
        RenderingHints.VALUE_ANTIALIAS_ON);
    
    fillHexagon(hexPoints, hexColor);
  }
  
  public boolean nextFrame()
  {
    timeSinceMouseMoved = System.currentTimeMillis() - startCount;
    
    frameCount++;
    if (frameCount > 100) return false;
    
    // I want to regularly re-draw the background and hexagons and primes so the
    // lightning does not erase theme. Maximum three bolts at a time!
    if (frameCount % 3 == 0)
    {
      canvas.drawImage(background, 0, 0, null);
      fillHexagon(hexPoints, hexColor);
      
      canvas.setColor(Color.WHITE);
      canvas.setFont(little);
      
      for (int i = 0; i < somePrimes.length; i++)
      {
        canvas.drawString(somePrimes[i], hexPoints[i].x - cos + 5,
            hexPoints[i].y + sin / 2);
      }
    }
    long deltaTime = System.currentTimeMillis() - initTime;
    long timeLeft = 120000 - deltaTime;
    
    if (timeLeft > 0)
    {
      // Black box to keep the lightning from killing the console text
      canvas.setColor(Color.BLACK);
      canvas.fillRect(0, 450, WIDTH, HEIGHT - 450);
      
      // Encrypted string on the bottom
      canvas.setColor(Color.GREEN);
      canvas.setFont(mono);
      canvas.drawString(">" + crypt, 12, 480);
      
      // The remaining time
      if (timeLeft > 0)
      {
        canvas.setColor(Color.BLACK);
        canvas.fillRect(490, 530, 200, 40);
        canvas.setColor(Color.GREEN);
        canvas.drawString("Time = " + timeLeft/1000 + " s", 500, 560);
      }
      
      // Value of N on screen
      canvas.setColor(Color.RED);
      canvas.setFont(big);
      canvas.drawString("N = " + theN, 500, 100);
      
      // And the value of e underneath it
      canvas.setColor(Color.WHITE);
      canvas.setFont(little);
      canvas.drawString("(e = " + theE + ")", 500, 130);
      
      // Initialize Point array for the random lightning for the background
      lightning[0] = new Point(370, 130);
      double theta = rand.nextDouble() * 2 * Math.PI;
      int X = (int) (25 * Math.cos(theta));
      int Y = (int) (25 * Math.sin(theta));
      
      for (int i = 1; i < lightning.length; i++)
      {
        lightning[i] = new Point(X + lightning[i - 1].x, Y + lightning[i - 1].y);
        
        // Go  less than 90 degrees one way or the other from the previous line
        boolean plusminus = rand.nextBoolean();
        if (plusminus) theta = theta + rand.nextDouble() * (Math.PI / 2);
        else theta = theta - rand.nextDouble() * (Math.PI / 2);
        
        X = (int) (25 * Math.cos(theta));
        Y = (int) (25 * Math.sin(theta));
      }
      
      // Draw the tesla coil lightning!
      canvas.setColor(Color.WHITE);
      for (int i = 1; i < lightning.length; i++)
      {
        int Xbegin = lightning[i - 1].x;
        int Xend = lightning[i].x;
        int Ybegin = lightning[i - 1].y;
        int Yend = lightning[i].y;
        
        canvas.drawLine(Xbegin, Ybegin, Xend, Yend);
        
        if (Xend != Xbegin) theta = Math.atan((Yend - Ybegin) / (Xend - Xbegin));
        
        // 50/50 chance for random offshoots on the bolts
        boolean plusminus = rand.nextBoolean();
        if (plusminus) theta = theta + rand.nextDouble() * (Math.PI / 2);
        else theta = theta - rand.nextDouble() * (Math.PI / 2);
        
        int Xoff = (int) (Xend + (12 * Math.cos(theta)));
        int Yoff = (int) (Yend + (12 * Math.sin(theta)));
        
        if (rand.nextBoolean()) canvas.drawLine(Xend, Yend, Xoff, Yoff);
      }
      
      // Draw /cooler/ lightning that follows the cursor
      lightningStrike(lightning[0], cursorPos, false);
      
      // Determine if you are winning or losing
      penalty = 0;
      for (int i = 0 ; i < hexPoints.length ; i++)
      {
        // If you are hovering in a hexagon...
        if (distance(cursorPos, hexPoints[i]) < hexrad && timeSinceMouseMoved > 2000)
        {
          // If you are within P/Q's hexagon and hover for two seconds
          // sets found* to true, and strikes you with DOUBLE LIGHTNING!
          if (distance(cursorPos, ans1) < hexrad && timeSinceMouseMoved > 2000)
          {
            foundP = true;
            lightningStrike(lightning[0], cursorPos, true);
          }
          else if (distance(cursorPos, ans2) < hexrad && timeSinceMouseMoved > 2000)
          {
            foundQ = true;
            lightningStrike(lightning[0], cursorPos, true);
          }
          // If you are hovering in the wrong box, incriment penalty
          else penalty++;
        }
      }
      
      
      // What to do when you find the prime factors (or try and fail)
      Point win = new Point(530, 360);
      if (foundP && !foundQ)
      {
        canvas.drawString("(N = " + theP + " * ??? )", win.x, win.y);
        lightningStrike(lightning[0], win, false);
        if (justOncep)
        { score+= (p/10)-(10*penalty);
          justOncep = false;
        }
      }
      else if (foundQ && !foundP)
      {
        canvas.drawString("(N = " + theQ + " * ??? )", win.x, win.y);
        lightningStrike(lightning[0], win, false);
        if (justOnceq)
        { score+= (q/10)-(10*penalty);
          justOnceq = false;
        }
      }
      else if (foundP && foundQ)
      {
        canvas.drawString("(N = " + theP + " * " + theQ + ")", win.x, win.y);
        canvas.drawString(">> d = " + theD, 530, 390);
        lightningStrike(lightning[0], win, true);
        canvas.setColor(Color.GREEN);
        canvas.setFont(mono);
        canvas.drawString("  MESSAGE DECIPHERED :", 12, 520);
        canvas.drawString("  >>" + answer, 12, 560);
        if (justOncet)
        { score+= timeLeft/1000;
          justOncet = false;
        }
        timeLeft = 0;
      }
      this.repaint();
    }
  
    else
    {
      canvas.setColor(Color.BLACK);
      canvas.fillRect(490, 530, 200, 40);
      canvas.setColor(Color.GREEN);
      canvas.drawString("Score = " + score + " !", 500, 560);
      this.repaint();
      return false;
    }
    
    return true;
    
  }
  
  // Return score. This should now be the number of remaining seconds
  // minus 10 for each wrong guess, plus 1/10 the size of each prime
  public int getScore()
  {
    return score;
  }
  
  // Draw the buffered image
  public void paint(Graphics canvas)
  {
    canvas.drawImage(buffer, 0, 0, null);
  }
  
  /*############################################################################
  // This method implements the distance formula on two points.                #
  //##########################################################################*/
  private int distance(Point start, Point end)
  {
    double dx = start.getX() - end.getX();
    double dy = start.getY() - end.getY();
    double length = Math.sqrt(dx * dx + dy * dy);
    
    int dist = (int) length;
    return dist;
  }
  
  /*############################################################################
  // This draws really cool lightning from one point to another, and takes a   #
  // boolean which, if true, causes two more bolts to appear (in color)!       #
  //##########################################################################*/
  private void lightningStrike(Point start, Point end, boolean twice)
  {
    ArrayList<Point> bolt1 = fillBolt(start, end);
    for (Point p : bolt1)
    {
      if (p == bolt1.get(bolt1.size() - 1)) break;
      Point p0 = bolt1.get(bolt1.indexOf(p)+1);
      canvas.setColor(Color.WHITE);
      canvas.drawLine(p.x, p.y, p0.x, p0.y);
    }
    
    if (twice)
    {
      ArrayList<Point> bolt2 = fillBolt(start, end);
      ArrayList<Point> bolt3 = fillBolt(start, end);
      
      for (Point p : bolt2)
      {
        if (p == bolt2.get(bolt2.size() - 1)) break;
        Point p0 = bolt2.get(bolt2.indexOf(p)+1);
        canvas.setColor(Color.YELLOW);
        canvas.drawLine(p.x, p.y, p0.x, p0.y);
      }
      for (Point p : bolt3)
      {
        if (p == bolt3.get(bolt3.size() - 1)) break;
        Point p0 = bolt3.get(bolt3.indexOf(p)+1);
        canvas.setColor(Color.CYAN);
        canvas.drawLine(p.x, p.y, p0.x, p0.y);
      }
    }
  }
  
  /*############################################################################
  // A very smart person on StackOverflow helped me with this! It takes two    #
  // Points, and uses the buildBolt method to create an ArrayList of points    #
  // between them which, when put together, look like realistic lightning      #
  //##########################################################################*/
  private ArrayList<Point> fillBolt(Point start, Point end)
  {
    double varFactor = 0.40;
    ArrayList<Point> bolt = new ArrayList<Point>();
    double dx = start.getX() - end.getX();
    double dy = start.getY() - end.getY();
    double length = Math.sqrt(dx*dx + dy*dy);
    double variance = length * varFactor;
    bolt.add(start);
    buildBolt(start, end, bolt, variance);
    return bolt;
  }
  
  /*############################################################################
  // This takes an ArrayList and a start and end point. First it gets the      #
  // distance between them, and if it is great enough, it uses trigonometry    #
  // and a given percentage which the points can vary from the natural line to #
  // break the line up in a random, yet very sensible way. THEN LIGHNING!!!    # 
  //##########################################################################*/
  private void buildBolt(Point start, Point end, ArrayList<Point> bolt, double variance)
  {
    double varDecrease = 0.55;
    int minLength = 20;
    double dx = start.getX() - end.getX();
    double dy = start.getY() - end.getY();
    double length = Math.sqrt(dx*dx + dy*dy);
    
    if (length > minLength)
    {
      int midX, midY;
      int var = (int) ((Math.random() * variance * 2) - variance);
      
      // My own addition: horizontal variance if the line is more vertical,
      // (and vis versa)! 
      double vert = Math.abs(Math.sin(Math.atan(dy/dx)));
      double horiz = Math.abs(Math.cos(Math.atan(dy/dx)));
      if (vert >= horiz)
      {
        midX = (start.x + end.x)/2 + var;
        midY = (start.y + end.y)/2;
      }
      else
      {
        midX = (start.x + end.x)/2;
        midY = (start.y + end.y)/2 + var;
      }
      Point mid = new Point(midX, midY);
      buildBolt(start, mid, bolt, variance * varDecrease);
      buildBolt(mid, end, bolt, variance * varDecrease);
    }
    else
    {
      bolt.add(end);
    }
    return;      
  }
  
  /*############################################################################
  // Recycled from my DLA Hex Grid idea: This takes an array of hexagon centers#
  // and an array of colors, and draws hexagons around those centers, filling  #
  // them with those colors (color index varies a little on a random basis)    #
  //##########################################################################*/
  private void fillHexagon(Point[] points, Color[] col)
  {
    for (int i = 0; i < hexPoints.length; i++)
    {
      int x = points[i].x;
      int y = points[i].y;
      
      int hexX[] =
      { x, x + cos, x + cos, x, x - cos, x - cos, x, };
      
      int hexY[] =
      { y - hexrad, y - sin, y + sin, y + hexrad, y + sin, y - sin, y - hexrad, };
      if (rand.nextBoolean()) canvas.setColor(col[i]);
      else
        canvas.setColor(col[i + 3]);
      canvas.fillPolygon(hexX, hexY, 7);
    }
  }
  
  /*############################################################################
  # THE REST IS RSA!                                                           #
  ############################################################################## 
  // EXPLANATION OF THE RSA ALGORITHM:                                         #
  //   Step 1 : Generate two different primes  p  and  q                       #
  //   Step 2 : Create N such that  N  = (p)*(q)                               #
  //   Step 3 : Create PHI such that PHI  =  (p-1)*(q-1)                       #
  //   Step 4 : Generate  e  between 1 and PHI such that : e is coprime to N : #
  //   Step 5 : Generate  d  such that  (e*d) mod PHI = 1                      #
  //     --> The public key is (e, N) and the private key is (d, N)            #
  //   Step 6 : Use the function  X^e mod N  to encrypt X                      #
  //   Step 7 : Use the function  X^d mod N  to decrypt X                      #
  //     --> Words can be converted into numbers (X's) with the above keymap   #
  //##########################################################################*/  
  
  /*############################################################################
  // This is a naive primality test, with few optimizations. The numbers we're #
  // working with are unusually small for this specific task, you know.        #
  //##########################################################################*/
  private boolean isPrime(int n)
  {
    if (n < 2) return false;
    if (n == 2 || n == 3) return true;
    if (n % 2 == 0 || n % 3 == 0) return false;
    
    for (int i = 2; i < Math.sqrt(n) + 1; i++)
    {
      if ((n % i) == 0) return false;
    }
    return true;
  }
  
  /*############################################################################
  // Runs through numbers, and when it finds a prime, it adds it to an array.  #
  // The loop breaks when the array is full.                                   #
  //##########################################################################*/
  private void genPrimes(int[] array)
  {
    int primesfound = 0;
    for (int i = 0; i < Integer.MAX_VALUE; i++)
    {
      if (isPrime(i))
      {
        primesfound++;
        
        array[primesfound - 1] = i;
      }
      
      if (primesfound == array.length)
      {
        break;
      }
    }
  }
  
  /*############################################################################
  // The num map should just be a list of every integer from 10 to 99.         #
  //##########################################################################*/
  private void genNumMap(String[] array)
  {
    int idx = 0;
    for (int n = 10; n <= 99; n++)
    {
      if (array.length < 90) break;
      array[idx] = "" + n;
      idx++;
    }
  }
  
  /*############################################################################
  // I was blown away by how simple the method for finding the greatest common #
  // divisor is! To give credit where it is due, I must give praise to Euclid! #
  //##########################################################################*/
  private int gcd(int p, int q)
  {
    if (q == 0) return p;
    
    return gcd(q, p % q);
  }
  
  /*############################################################################
  // This first finds e, the lowest coprime of N (gcd() == 1) between 1 and PHI#
  // Then a nested loop looks for a value for d such that (d * e) % PHI == 1.  #
  // If d does not exist, or if it = e, the next highest possible e is found,  #
  // and the whole process repeats until an RSA keypair has been generated!    #
  //##########################################################################*/
  private TwoDatas getEandD(int N, int PHI)
  {
    TwoDatas data = new TwoDatas();
    int start = 2;
    data.d = -1;
    data.e = 2;
    int mod;
    
    for (int e = start; e < PHI; e = e + 1)
    {
      if (gcd(e, N) == 1)
      {
        data.e = e;
      }
      
      for (int d = 1; d < PHI; d++)
      {
        mod = ((d * (data.e)) % PHI);
        if (mod == 1) data.d = d;
      }
      
      if (data.d == -1 || data.d == data.e) start++;
      else
        break;
    }
    return data;
  }
  
   /*############################################################################
   // RSA encryption uses the discreet logarithm: (x^e mod N) : This returns    #
   // the solution to that as a BigInteger (Because normal sized math just      #
   // doesn't cut it anymore...)                                                #
   //##########################################################################*/
  private BigInteger RSAEncrypt(BigInteger plaintext)
  {
    BigInteger ciphertext = plaintext.modPow(bigE, bigN);
    //System.out.println(plaintext + " ==> " + ciphertext);
    
    return ciphertext;
  }
  
  /*############################################################################
  // If getEandD worked correctly, then (x^d mod N) will undo (x^e mod N)      #
  // Fermat's Little Theorem, is what that's called! Mathematicians are heroes!#
  //##########################################################################*/
  private BigInteger RSADecrypt(BigInteger ciphertext)
  {
    BigInteger deciphertext = ciphertext.modPow(bigD, bigN);
    //System.out.println(deciphertext + " <== " + ciphertext);
    return deciphertext;
  }
  
  /*############################################################################
  // This uses the charMap and the numMap arrays to convert strings into arrays#
  // of numbers which can be broken up and RSA'd! Yay cryptography!            #
  //##########################################################################*/
  private BigInteger[] stringToNumbers(String mesg)
  {
    if (mesg.length() % 2 != 0) mesg = mesg + " ";
    int size = (mesg.length() / 2);
    String numbers = "";
    
    for (int k = 0; k < mesg.length(); k++)
    {
      char c = mesg.charAt(k);
      for (int idx = 0; idx < charMap.length; idx++)
      {
        if (c == charMap[idx]) numbers+=numMap[idx];
      }
    }
    
    BigInteger[] array = new BigInteger[size];
    
    int idx = 0;
    
    for (int k = 0; k < numbers.length(); k += 4)
    {
      char a = numbers.charAt(k);
      char b = numbers.charAt(k + 1);
      char c = numbers.charAt(k + 2);
      char d = numbers.charAt(k + 3);
      
      String four = String.valueOf(a) + String.valueOf(b) + String.valueOf(c)
          + String.valueOf(d);
      
      BigInteger bigint = new BigInteger(four);
      array[idx] = bigint;
      idx++;
    }
    
    return array;
  }
  
  /*############################################################################
  // And this uses the numMap and the charMap to convert numbers into          #
  // corresponding strings. My personal homage to the Code Cracker Game!       #
  //##########################################################################*/
  private String numbersToString(BigInteger[] bigint)
  {
    String msg = "";
    for (int i = 0; i < bigint.length; i++)
    {
      String numbers = bigint[i].toString();
      for (int k = 0; k < numbers.length(); k+=2)
      {
        char a = numbers.charAt(k);
        char b = numbers.charAt(k + 1);
        String num = String.valueOf(a) + String.valueOf(b);
        
        for (int idx = 0; idx < numMap.length; idx++)
        {
          if (num.equals(numMap[idx])) msg = msg + charMap[idx];
        }
      }
    }
    return msg;
  }
  
  /*############################################################################
  // Simply takes a string an converts it into an RSA encrypted number string  #
  //##########################################################################*/
  private String[] encryptMessage(String message)
  {
    BigInteger[] array = stringToNumbers(message);
    String[] encryptedMessage = new String[array.length];
    
    for (int i = 0; i < encryptedMessage.length; i++)
    {
      encryptedMessage[i] = "";
    }
    
    for (int i = 0; i < array.length; i++)
    {
      BigInteger part = RSAEncrypt(array[i]);
      encryptedMessage[i] = encryptedMessage[i] + part.toString();
    }
    return encryptedMessage;
  }
  
  /*############################################################################
  // Simply takes an array of strings (numbers) and RSADecrypts them! Then it  #
  // Turns the array into a single string, which will read the deciphered text #
  //##########################################################################*/
  private String decryptMessage(String[] crypt)
  {
    BigInteger[] solution = new BigInteger[crypt.length];
    
    for (int i = 0; i < crypt.length; i++)
    {
      BigInteger part = new BigInteger(crypt[i]);
      solution[i] = RSADecrypt(part);
      //System.out.println(msg);
      //System.out.println(solution[i]);
    }
    String decryptedMessage = numbersToString(solution);
    return decryptedMessage;
  }
  
  /*############################################################################
  // My Mouse Motion Listener will set the Point for the cursor position, and  #
  // start a new count every time the mouse moves. Dragging does nothing.      #                                                     #
  //##########################################################################*/
  
  @Override
  public void mouseDragged(MouseEvent arg0)
  { // This does nothing, sorry.
  }

  @Override
  public void mouseMoved(MouseEvent e)
  {
    PointerInfo a = MouseInfo.getPointerInfo();
    cursorPos = a.getLocation();
    SwingUtilities.convertPointFromScreen(cursorPos, e.getComponent());
    startCount = System.currentTimeMillis();    
  }
  
  public static void main (String[] args)
  {
    
  }
}