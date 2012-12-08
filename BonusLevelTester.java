import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import javax.swing.Timer;

public class BonusLevelTester extends JFrame implements ActionListener
{
  private final int MAX_TIME_FOR_NEXTFRAME = 1000/50;
  
  //Both are created in constructor and used in actionPerformed
  //  Therefore, it is useful to declare as a class variables, not a local variables
  private Timer myTimer;
  //private BonusLevel_Dummy_Bob bob;
  private BonusLevel_Jesse_Maes jesse;
 
 
  public BonusLevelTester() 
  {
    this.setBounds(0,0, 752, 575); 
    this.setVisible(true);
    this.setDefaultCloseOperation(EXIT_ON_CLOSE);
   
    jesse = new BonusLevel_Jesse_Maes();
    this.add(jesse);    
  
    this.setTitle("Bonus Level Tester");

    jesse.init();

    myTimer = new Timer(20, this); // 1000 ms / 50 = 20 ms
    myTimer.start();

  }
  
  public void actionPerformed(ActionEvent arg0)
  {
    long curTime = System.currentTimeMillis();
    boolean stillRunning = jesse.nextFrame();
    long deltaTime = System.currentTimeMillis()-curTime;
    
    if (deltaTime > MAX_TIME_FOR_NEXTFRAME)
    {
      //Note: it is okay if an occasional frame is slightly overtime.
      System.out.println("Overtime on nextFrame(): " + deltaTime + "ms.");
    }
    
    if (!stillRunning)
    {
      myTimer.stop();
      jesse.init(); 
      myTimer.start();
    }
  }

  public static void main(String[] args)
  {
    new BonusLevelTester();
  }
}