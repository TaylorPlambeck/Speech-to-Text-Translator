import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.*;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import java.io.File;

public class SpeechGUI extends JFrame implements  ActionListener
{
	   static String message="Waiting for Translator....";
	   static String emessage="Waiting for Translator....";
	//   static String sendByte="Sending Bytes...";   USED FOR STATUS INDICATOR
	//   static String streamAudio="Audio Streamed, Closing Connection...";
	//   static String blank="No connection yet";
	   
	// static  int streamTest=0;
	   static String eline1="";
	   static String eline2="";
	   static String eline3="";
	   static String eline4="";
	   static String eline5="";
	   static String line1="";
	   static String line2="";
	   static String line3="";
	   static String line4="";
	   static String line5="";
	//   static String FilePath;
	   static String[] PathArray = new String[100];	//you can add 100 different files.
	   
		 public static Board checkers;	//make screen	
	public JButton  Transbutt,Audiobutt,Changebutt, RecordAudio,StopRecordAudio;
	public JMenuBar buttonHolder;	//menu bar
	static int msgCount=0;
	static int FontTest=0;
	int msgYcord=100;
														//t is character 55
//	"type":"final","id":"0","recognition":"Hello world.","translation":"Hola mundo."
	
	 public SpeechGUI()
		{
			buttonHolder = new JMenuBar();	//add menu bar
		 	Transbutt= new JButton("Translate!");	//add and declare buttons
			Audiobutt = new JButton("Play Speech Audio");
			Changebutt= new JButton("Choose Audio File");
			RecordAudio=new JButton("Start Recording Audio");
			StopRecordAudio=new JButton("Stop Recording Audio");
	        buttonHolder.add(Transbutt);	//add the buttons to the menu bar
	        buttonHolder.add(Audiobutt);
	        buttonHolder.add(Changebutt);
	        buttonHolder.add(RecordAudio);
	        buttonHolder.add(StopRecordAudio);
	        StopRecordAudio.setEnabled(false);  //this will be used to not allow the button to be clicked at first
	        
	        Transbutt.addActionListener(this);	//add action listener to see if these buttons are pressed
	        Audiobutt.addActionListener(this); 
	        Changebutt.addActionListener(this);
	        RecordAudio.addActionListener(this);
	        StopRecordAudio.addActionListener(this);
	        
	        setJMenuBar(buttonHolder);	
			checkers = new Board();	//creates the board set up in main above
			add(checkers);
		}

	class Board extends JPanel	//this draws the screen
	{
		public void paintComponent( Graphics g )
		{			
			super.paintComponent( g );
			 Color backColor = getBackground();
			 g.setColor(Color.red);
			 Font font = new Font("Verdana", Font.BOLD, 15);
			 Font permfont = new Font("Verdana", Font.BOLD, 19);
			 Font longerFont = new Font("Verdana", Font.BOLD, 11);
			 
			 if(FontTest==0)	//this changes font size so we can fit longer sentences on the GUI line.
			 	{
			 		 g.setFont(font);
			 	}
			 else if(FontTest==1)
			 {
				 g.setFont(longerFont);
			 }
			
			 g.setColor(Color.red);
			 if(msgCount==0)	//these two draw the 'waiting for translator''
			 	{
			 		g.drawString(emessage,18,msgYcord);	
			 		g.drawString(message,18,msgYcord+225);
			 	}
			 	else {		//draws up to five lines of english recog and spanish trans
			 		g.drawString(eline1,18,msgYcord+25);	
			 		g.drawString(line1,18,msgYcord+250);
			 		g.drawString(eline2,18,msgYcord+50);	
			 		g.drawString(line2,18,msgYcord+275);
			 		g.drawString(eline3,18,msgYcord+75);	
			 		g.drawString(line3,18,msgYcord+300);
			 		g.drawString(eline4,18,msgYcord+100);	
			 		g.drawString(line4,18,msgYcord+325);
			 		g.drawString(eline5,18,msgYcord+125);	
			 		g.drawString(line5,18,msgYcord+350);
			 	}
				 g.setColor(Color.black);
				 g.setFont(permfont);
				 g.drawString("English Recognition: ",15,75);
				 g.drawString("Spanish Translation: ",15,300);
				// g.drawString("Status:", 30, 30);  intended on having some kind of indication on progress of the translation.
				 g.drawString("Audio Sample Selected: ", 15, 30);
				 g.drawString(SpeechWebSocketClient.FileNameOnly, 275, 30);//draws the name of the file on the screen
		}

	}//end of GUI paint
		
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource().equals(Transbutt)) 	//if you pressed translate! do this:
		{
			eline1="";		//reset everything in case you do multiple translations.
			eline2="";
			eline3="";
			eline4="";
			eline5="";
			line1="";
			line2="";
			line3="";
			line4="";
			line5="";
			SpeechGUI.msgCount=0;
			SpeechGUI.FontTest=0;
			SpeechWebSocketClient.fileMaker(SpeechWebSocketClient.x-1);
					//play audio while translating
			try {
		        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(PathArray[SpeechWebSocketClient.x-1]).getAbsoluteFile());
		        Clip clip = AudioSystem.getClip();
		        clip.open(audioInputStream);
		        clip.start();
		    } catch(Exception ex) {
		        System.out.println("Error with playing sound.");
		        ex.printStackTrace();
		    }	
			//call translator in SPeechClient
			SpeechWebSocketClient.trans("en-us","es-ES");	//this could be two variables that would change based on the language selected
        } 
		
		else if (e.getSource().equals(Audiobutt)) 	//if you pressed play audio do this:
		{
			try {
			        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(PathArray[SpeechWebSocketClient.x-1]).getAbsoluteFile());
			        Clip clip = AudioSystem.getClip();
			        clip.open(audioInputStream);
			        clip.start();
			    } catch(Exception ex) {
			        System.out.println("Error with playing sound.");
			        ex.printStackTrace();
			    }
		} 
		
		else if (e.getSource().equals(Changebutt)) 	//this button changes audio to be played or translated
		{
			if((SpeechWebSocketClient.x)>(SpeechWebSocketClient.ClipCount-1))
			 {
				 SpeechWebSocketClient.fileMaker(0);	//sets it back to first file.
				 checkers.repaint();
				 SpeechWebSocketClient.x=1;	//moves it to 1 so it will goto the else statement next time.
			 }
			 else{
			SpeechWebSocketClient.fileMaker(SpeechWebSocketClient.x++);
			checkers.repaint();
			 }
	
			
        } 
		
		else if (e.getSource().equals(RecordAudio)) 	//this button records audio
		{
					Capture.captureAudio();		//record, rest switches the buttons
					StopRecordAudio.setEnabled(true);    
					Changebutt.setEnabled(false);    
					Transbutt.setEnabled(false);    
					Audiobutt.setEnabled(false);    
					RecordAudio.setEnabled(false);    
		} 
		
		else if (e.getSource().equals(StopRecordAudio)) 	//this button stops recording on command
				{
			  		Capture.running = false;	//only thing it needs, rest changes buttons
			  		RecordAudio.setEnabled(true);    
					Changebutt.setEnabled(true);    
					Transbutt.setEnabled(true);    
					Audiobutt.setEnabled(true); 		
					StopRecordAudio.setEnabled(false);
		        } 
	}
}
