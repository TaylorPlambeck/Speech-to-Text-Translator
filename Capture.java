import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Arrays;

import javax.sound.sampled.*;
 

public class Capture extends JFrame {
 
  protected static boolean running;
  static ByteArrayOutputStream out;
  static File wavFile = new File(SpeechGUI.PathArray[0]);    	//location of file to be saved
  static AudioFileFormat.Type fileType = AudioFileFormat.Type.WAVE;		// format of audio file  
 
  public Capture() {
    super("Capture Sound Demo");
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    Container content = getContentPane();
 
    final JButton capture = new JButton("Capture");
    final JButton stop = new JButton("Stop");
    final JButton play = new JButton("Play");
 
    capture.setEnabled(true);
    stop.setEnabled(false);
    play.setEnabled(false);
 
    ActionListener captureListener = 
        new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        capture.setEnabled(false);
        stop.setEnabled(true);
        play.setEnabled(false);
        captureAudio();
      }
    };
    capture.addActionListener(captureListener);
    content.add(capture, BorderLayout.NORTH);
 
    ActionListener stopListener = 
        new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        capture.setEnabled(true);
        stop.setEnabled(false);
        play.setEnabled(true);
        running = false;
      }
    };
    stop.addActionListener(stopListener);
    content.add(stop, BorderLayout.CENTER);
 
    ActionListener playListener = 
        new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        playAudio();
      }
    };
    play.addActionListener(playListener);
    content.add(play, BorderLayout.SOUTH);
  }
  
 
  public static void captureAudio() {
    try {
      final AudioFormat format = getFormat();
      DataLine.Info info = new DataLine.Info( TargetDataLine.class, format);
      final TargetDataLine line = (TargetDataLine)   AudioSystem.getLine(info);
      line.open(format);
      line.start();
      Runnable runner = new Runnable() {
        int bufferSize = (int)format.getSampleRate() 
          * format.getFrameSize();
        byte buffer[] = new byte[bufferSize];
  
        public void run() {
          out = new ByteArrayOutputStream();
          running = true;
          try {
            while (running) {
              int count = 
                line.read(buffer, 0, buffer.length);
              if (count > 0) {
                out.write(buffer, 0, count);
              }
            } 
            Save();			//this was added also, saves whatever was recorded and closes
            out.close();	
            line.stop();				//added these two line commands so i can record multiple times in one session
            line.close();				//this is where you should shut the line off
           
          } catch (IOException e) {
            System.err.println("I/O problems: " + e);
            System.exit(-1);
          }
        }
      };
      Thread captureThread = new Thread(runner);
      captureThread.start();
    } catch (LineUnavailableException e) {
      System.err.println("Line unavailable: " + e);
      System.exit(-2);
    }
  }
 
  public static void Save()
  {
	  byte audio[] = out.toByteArray();
      InputStream input = new ByteArrayInputStream(audio);
      final AudioFormat format = getFormat();
      final AudioInputStream ais =   new AudioInputStream(input, format, audio.length / format.getFrameSize());
      try {
		AudioSystem.write(ais, fileType, wavFile);
	} catch (IOException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}// this is what actually puts my recording to the file, with filetype of choice
  }
  
  private void playAudio() {
     byte audio[] = out.toByteArray();
      InputStream input = 
        new ByteArrayInputStream(audio);
      final AudioFormat format = getFormat();
      final AudioInputStream ais =   new AudioInputStream(input, format, audio.length / format.getFrameSize());
      try {
		AudioSystem.write(ais, fileType, wavFile);
	} catch (IOException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}// this actually puts my recording to my file and filetype of choice
	  try {
           
      DataLine.Info info = new DataLine.Info(
        SourceDataLine.class, format);
      final SourceDataLine line = (SourceDataLine)
        AudioSystem.getLine(info);
      line.open(format);
      line.start();
 
      Runnable runner = new Runnable() {
        int bufferSize = (int) format.getSampleRate() 
          * format.getFrameSize();
        byte buffer[] = new byte[bufferSize];
  
        public void run() {
          try {
            int count;
            while ((count = ais.read(
                buffer, 0, buffer.length)) != -1) {
              if (count > 0) {
                line.write(buffer, 0, count);
              }
            }
            line.drain();
            line.close();
          } catch (IOException e) {
            System.err.println("I/O problems: " + e);
            System.exit(-3);
          }
        }
      };
      Thread playThread = new Thread(runner);
      playThread.start();
    } catch (LineUnavailableException e) {
      System.err.println("Line unavailable: " + e);
      System.exit(-4);
    } 
 //   AudioSystem.write(ais, fileType, wavFile);// this actually puts my recording to my file and filetype of choice
  }
 
  public static AudioFormat getFormat() {
    float sampleRate = 16000;	//was 8000
    int sampleSizeInBits = 8;
    int channels = 2;	//was 1.
    boolean signed = true;
    boolean bigEndian = true;
    return new AudioFormat(sampleRate, 
      sampleSizeInBits, channels, signed, bigEndian);
  }
 
  public static void main(String args[]) {
    JFrame frame = new Capture();
    frame.pack();
    frame.show();
  }
}