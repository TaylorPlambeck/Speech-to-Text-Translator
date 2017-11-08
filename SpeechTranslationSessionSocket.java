import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

//Websocket client that communicates with speech/translate api 
@WebSocket
public class SpeechTranslationSessionSocket {
      private final byte[] silenceBytes = new byte[32000];
      private final CountDownLatch closeLatch;
      private String inputAudioFileName;

      @SuppressWarnings("unused")
      private Session session;
      //THIS OnMessage IS THE ONLY IMPORTANT METHOD IN THIS .JAVA FILE!!!----------------------------------------------------------
      
      //  	NORMALLY THE API OUTPUTS THE TRANSLATION LIKE THIS:  {"type":"final","id":"0","recognition":"Hello world.","translation":"Hola mundo."}
      // 	THUS WE HAVE TO PRINT ONLY THE PARTS WE WANT, the translation and the recognition.
      //	THE API ALSO GIVES MULTIPLE MESSAGES TO US IF WE GIVE IT MULTIPLE SENTENCES. IT BREAKS THEM UP BELOW
      @OnWebSocketMessage
      public void onMessage(String msg) 
      {
    	  System.out.printf("[%s] Message Received: %s%n", LocalDateTime.now().toString(), msg);//msg sent back from MS, displays on console
          SpeechGUI.msgCount++;	//this counts how many lines we have received (used for font size), in case the translator gives us multiple lines.
    	  
    	  	int index=0;	//two index variables to store where the TRANSLATION starts and ends at in the output string msg.
    	  	int endIndex=0;
    	  	 int Eindex=0;	//two index variables to store where the RECOGNITION starts and ends at. recog variables have an e in front of their name
     	  	int EendIndex=0;
    	  	endIndex=msg.length();	//finds total length of msg from API
            String send;	//manipulated (translated) message, sent to the GUI
                
            String wordToFind = "translation";	//search for the word 'translation', because our translated text will always be 4 spaces to the right 
            Pattern word = Pattern.compile(wordToFind);	//this is a handy little word matcher, gives the index of a match with our wordtoFind
            Matcher match = word.matcher(msg);
            while (match.find()) 
            {
                 System.out.println("Found 'translation' at index: "+ match.start() +" - "+ (match.end()-1));	//this prints out the location of translation
                 EendIndex=(match.start()-2);	//this finds the beginning of the word translation, to find when the english recognition ENDS
                 index=(match.end()-1+3);	//this uses the end of the word translation, to find the beginning of our translated message
            }
     
   //THIS IS NOW THE SAME EXACT PROCESS BUT FOR THE ENGLISH RECOGNITION-----------------------------------------------------------------------
          String Esend;	//manipulated (recognized) message, sent to the GUI
          String EwordToFind = "recognition";	//this searches for the word 'recognition', because our english text will always 4 spaces right
          Pattern Eword = Pattern.compile(EwordToFind);	//word matcher, gives the index of a match with our wordtoFind, which is now recognition
          Matcher Ematch = Eword.matcher(msg);	
          while (Ematch.find()) 
          {
               System.out.println("Found 'recognition' at index: "+ Ematch.start() +" - "+ (Ematch.end()-1));	//this prints out the location of recognition
               Eindex=(Ematch.end()-1+3);	//this uses the end of the word recognition to find the beginning of our english phrase, end of phrase found above
          }
     
          //Now that we have our translation and english recognition, we need to decide how to dislay it on the GUI.
          //IF statement below would be used if we just had a single sentence or phrase. 
          //THIS ENTIRE SECTION WOULD HAVE BEEN IRRELEVANT IF WE OUTPUT IN A TEXTAREA, BUT WE DIDN'T KNOW NETBEANS YET...LuL.
          if(SpeechGUI.msgCount==1)
          {
        	    send=msg.substring(index, endIndex-1);	//this takes our text and reads it only from the correct points so we get a clean phrase, no })" @ the end
        	    SpeechGUI.line1=send;	//this changes the message in the SpeechGUI
        	    Esend=msg.substring(Eindex, EendIndex);	//recog text and reads it only from the correct points so we get a clean phrase, no })" @ the end
        	    SpeechGUI.eline1=Esend;	//this changes the message in the SpeechGUI 
        	    System.out.println(SpeechGUI.eline1);
        	    System.out.println(SpeechGUI.line1);
          }
          else  if(SpeechGUI.msgCount==2)	//Two lines recieved from MS
          {
    	    send=msg.substring(index, endIndex-1);	//this takes our text and reads it only from the correct points so we get a clean phrase, no })" @ the end
    	    SpeechGUI.line2=send;	//this changes the message in the SpeechGUI 
    	    Esend=msg.substring(Eindex, EendIndex);	//recog text and reads it only from the correct points so we get a clean phrase, no })" @ the end
    	    SpeechGUI.eline2=Esend;	//this changes the message in the SpeechGUI   
    	    System.out.println(SpeechGUI.eline2);
    	    System.out.println(SpeechGUI.line2);
          }
      else  if(SpeechGUI.msgCount==3)	//three lines
      	{
    	    send=msg.substring(index, endIndex-1);	//this takes our text and reads it only from the correct points so we get a clean phrase, no })" @ the end
    	    SpeechGUI.line3=send;	//this changes the message in the SpeechGUI 
    	    Esend=msg.substring(Eindex, EendIndex);	//recog text and reads it only from the correct points so we get a clean phrase, no })" @ the end
    	    SpeechGUI.eline3=Esend;	//this changes the message in the SpeechGUI   
    	    System.out.println(SpeechGUI.eline3);
    	    System.out.println(SpeechGUI.line3);
      	}    
          
      else  if(SpeechGUI.msgCount==4)	//four lines
    	{
  	    send=msg.substring(index, endIndex-1);	//this takes our text and reads it only from the correct points so we get a clean phrase, no })" @ the end
  	    SpeechGUI.line4=send;	//this changes the message in the SpeechGUI 
  	    Esend=msg.substring(Eindex, EendIndex);	//recog text and reads it only from the correct points so we get a clean phrase, no })" @ the end
  	    SpeechGUI.eline4=Esend;	//this changes the message in the SpeechGUI   
  	    System.out.println(SpeechGUI.eline4);
  	    System.out.println(SpeechGUI.line4);
    	}    
          
      else  if(SpeechGUI.msgCount==5)
  	{
	    send=msg.substring(index, endIndex-1);	//this takes our text and reads it only from the correct points so we get a clean phrase, no })" @ the end
	    SpeechGUI.line5=send;	//this changes the message in the SpeechGUI 
	    Esend=msg.substring(Eindex, EendIndex);	//recog text and reads it only from the correct points so we get a clean phrase, no })" @ the end
	    SpeechGUI.eline5=Esend;	//this changes the message in the SpeechGUI   
	    System.out.println(SpeechGUI.eline5);
	    System.out.println(SpeechGUI.line5);
  	}         
          if(SpeechGUI.line1.length()>100 | SpeechGUI.line2.length()>100 | SpeechGUI.line3.length()>100 | SpeechGUI.line4.length()>100 
        		  | SpeechGUI.line5.length()>100	|  SpeechGUI.eline1.length()>100 | SpeechGUI.eline2.length()>100 
        		  | SpeechGUI.eline3.length()>100 | SpeechGUI.eline4.length()>100 | SpeechGUI.eline5.length()>100  )	
        	  
        	  //finds total length of msg from API, if its super long lower font size.
          {
        	  SpeechGUI.FontTest=1;
          }
          else {SpeechGUI.FontTest=0;}
          SpeechGUI.checkers.repaint();	//REPAINT WITH NEW MESSAGE!
      }
      
      
      // THE REST OF THE FILE IS UNCHANGED FROM MICROSOFT's API         ----------------------------------------------------------------
      
      public SpeechTranslationSessionSocket(String inputAudioFileName) {
            this.inputAudioFileName = inputAudioFileName;
            this.closeLatch = new CountDownLatch(1);
      }

      public boolean awaitClose(int duration, TimeUnit unit) throws InterruptedException {
            return this.closeLatch.await(duration, unit);
      }

      @OnWebSocketClose
      public void onClose(int statusCode, String reason) {
            System.out.printf("[%s] Connection closed: %d - %s%n", LocalDateTime.now().toString(), statusCode, reason);
            this.session = null;
            this.closeLatch.countDown();
      }

      @OnWebSocketConnect
      public void onConnect(Session session) {
            System.out.printf("[%s] Connection Success: %s%n", LocalDateTime.now().toString(), session);
            this.session = session;
            try {
                  // start streaming the audio
                  sendData(session, inputAudioFileName);
                  System.out.printf("%n[%s] Done streaming audio.%n", LocalDateTime.now().toString());
            } catch (Throwable t) {
                  t.printStackTrace();
            }
      }
      

      private void sendData(Session session, String fileName) {
    	  
            try {
                  try (FileInputStream fi = new FileInputStream(new File(fileName))) {
                        byte[] buffer = new byte[32000];
                        int counter = 0;
                        System.out.printf("[%s] Sending Bytes: ", LocalDateTime.now().toString());
                        while ((counter = fi.read(buffer, 0, buffer.length)) != -1) {
                              if (counter > 0) {
                                    System.out.printf(".");
                                    session.getRemote().sendBytes(ByteBuffer.wrap(buffer));
                                    Thread.sleep(100);
                              }
                        }
                  }

                  // Now send some silence bytes
                  for (int i = 0; i < 10; i++) {
                        session.getRemote().sendBytes(ByteBuffer.wrap(silenceBytes));
                        Thread.sleep(100);
                  }
            } catch (Exception e) {
                  e.printStackTrace();
                  System.exit(1);
            }
      }
}
