import javax.swing.JPanel;
import javax.swing.JFrame;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Dimension;
import java.util.Random;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
class Pair{
    public double x;
    public double y;
    
    public Pair(double initX, double initY){
	x = initX;
	y = initY;
    }

    public Pair add(Pair toAdd){
	return new Pair(x + toAdd.x, y + toAdd.y);
    }

    public Pair divide(double denom){
	return new Pair(x / denom, y / denom);
    }

    public Pair times(double val){
	return new Pair(x * val, y * val);
    }

    public void flipX(){
	x = -x;
    }
    
    public void flipY(){
	y = -y;
    }
}

class Sphere{
    Pair position;
    Pair velocity;
    Pair acceleration;
    double radius;
    double dampening;
    Color color;
    public Sphere()
    {
	Random rand = new Random(); 
	position = new Pair(500.0, 500.0);
	velocity = new Pair((double)(rand.nextInt(1000) - 500), (double)(rand.nextInt(1000) - 500));
	acceleration = new Pair(0.0, 200.0);
	radius = 25;
	dampening = 1.3;
	color = new Color(rand.nextFloat(), rand.nextFloat(), rand.nextFloat());
    }
    public void update(World w, double time){
	position = position.add(velocity.times(time));
	velocity = velocity.add(acceleration.times(time));
	bounce(w);
    }
    
    public void setPosition(Pair p){
	position = p;
    }
    public void setVelocity(Pair v){
	velocity = v;
    }
    public void setAcceleration(Pair a){
	acceleration = a;
    } 
    public void draw(Graphics g){
	Color c = g.getColor();
	
	g.setColor(color);
	g.drawOval((int)(position.x - radius), (int)(position.y - radius), (int)(2*radius), (int)(2*radius));
	g.setColor(c);
    }
    private void bounce(World w){
	Boolean bounced = false;
	if (position.x - radius < 0){
	    velocity.flipX();
	    position.x = radius;
	    bounced = true;
	}
	else if (position.x + radius > w.width){
	    velocity.flipX();
	    position.x = w.width - radius;
	    bounced = true;
	}
	if (position.y - radius < 0){
	    velocity.flipY();
	    position.y = radius;
	    bounced = true;
	}
	else if(position.y + radius >  w.height){
	    velocity.flipY();
	    position.y = w.height - radius;
	    bounced = true;
	}
	if (bounced){
	    velocity = velocity.divide(dampening);
	}
    }
    
}

class World{
    int height;
    int width;
    
    int numSpheres;
    Sphere spheres[];

    public World(int initWidth, int initHeight, int initNumSpheres){
	width = initWidth;
	height = initHeight;

	numSpheres = initNumSpheres;
	spheres  = new Sphere[numSpheres];
	
	for (int i = 0; i < numSpheres; i ++)
	    {
		spheres[i] = new Sphere();
	    }

    }

    public void drawSpheres(Graphics g){
	for (int i = 0; i < numSpheres; i++){
	    spheres[i].draw(g);
	}
    }

    public void updateSpheres(double time){
	for (int i = 0; i < numSpheres; i ++)
	    spheres[i].update(this, time);
    }

}

public class KeyboardSpheres extends JPanel implements KeyListener{
    public static final int WIDTH = 1024;
    public static final int HEIGHT = 768;
    public static final int FPS = 60;
    //storing the last direction to call methods after in/decreasing acceleration
    private static char lastDirection = 's';
    World world;
    //my own field for acceleration strength
    private static double accMagnitude = 200;

    class Runner implements Runnable{
	public void run()
	{
	    while(true){
		world.updateSpheres(1.0 / (double)FPS);
		repaint();
		try{
		    Thread.sleep(1000/FPS);
		}
		catch(InterruptedException e){}
	    }

	}
    
    }


    public void keyPressed(KeyEvent e) {
        char keyValue=e.getKeyChar();
    }
    public void keyReleased(KeyEvent e) {
    	char keyValue = e.getKeyChar();
    	changeAccelerationAll(keyValue);
    	changeColorAll(keyValue);
    	changeAccelerationOne(keyValue);
    	changeAccelerationMagnitude(keyValue);
    }

    public void keyTyped(KeyEvent e) {
    	char c = e.getKeyChar();
    }
    
    
     public void addNotify() {
        super.addNotify();
        requestFocus();
    }

   //additional methods
     public void changeAccelerationMagnitude(char keyValue) {
    	 if(keyValue == 'e') {
    		 accMagnitude += 10;
    		 this.changeAccelerationAll(KeyboardSpheres.lastDirection);
    	 }
    	 if(keyValue == 'q') {
    		 accMagnitude -= 10;
    		 this.changeAccelerationAll(KeyboardSpheres.lastDirection);
    	 }
    	 if(accMagnitude < 0) {
    		 accMagnitude = 0;
    	 }
    	 
     }
     public void changeAccelerationAll(char keyValue) {
     	for(int i = 0; i < world.spheres.length; i++) {
     		
             if (keyValue == 'w') {
             	world.spheres[i].acceleration = new Pair(0, -accMagnitude);
             	KeyboardSpheres.lastDirection = keyValue;
             }
             if (keyValue == 's') {
             	world.spheres[i].acceleration = new Pair(0, accMagnitude);
             	KeyboardSpheres.lastDirection = keyValue;
             }
             if (keyValue == 'a') {
             	world.spheres[i].acceleration = new Pair(-accMagnitude, 0);
             	KeyboardSpheres.lastDirection = keyValue;
             }
             if (keyValue == 'd') {
             	world.spheres[i].acceleration = new Pair(accMagnitude, 0);
             	KeyboardSpheres.lastDirection = keyValue;
             }
     	}
     }
     public void changeAccelerationOne(char keyValue) {
              if (keyValue == 'i') {
              	world.spheres[1].acceleration = new Pair(0, -accMagnitude);
              	System.out.println(keyValue);
              }
              if (keyValue == 'k') {
              	world.spheres[1].acceleration = new Pair(0, accMagnitude);
              }
              if (keyValue == 'j') {
              	world.spheres[1].acceleration = new Pair(-accMagnitude, 0);
              }
              if (keyValue == 'l') {
              	world.spheres[1].acceleration = new Pair(accMagnitude, 0);
              }
      }
     public void changeColorAll(char keyValue) {
    	 Color changedColor = Color.BLACK;
    	 Random rand = new Random();
    	 for (int i = 0; i < world.spheres.length; i++) {
    		 if (keyValue == '1') {
    			 changedColor = new Color(rand.nextInt(255), rand.nextInt(255), rand.nextInt(255));
    		 }
    		 if (keyValue == '2') {
    			 changedColor = Color.RED;
    		 }
    		 if (keyValue == '3') {
    			 changedColor = Color.BLUE;
    		 }
    		 if (keyValue == '4') {
    			 changedColor = Color.PINK;
    		 }
    		 if(changedColor != Color.BLACK) {
    			 world.spheres[i].color = changedColor;
    		 }
    	 }
     }
    public KeyboardSpheres(){
	world = new World(WIDTH, HEIGHT, 50);
	addKeyListener(this);
	this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
	Thread mainThread = new Thread(new Runner());
	mainThread.start();
    }
    
    public static void main(String[] args){
	JFrame frame = new JFrame("Physics!!!");
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	KeyboardSpheres mainInstance = new KeyboardSpheres();
	frame.setContentPane(mainInstance);
	frame.pack();
	frame.setVisible(true);
    }


    public void paintComponent(Graphics g) {
	super.paintComponent(g);    	

	g.setColor(Color.BLACK);
	g.fillRect(0, 0, WIDTH, HEIGHT);

	world.drawSpheres(g);

    }

    
}
