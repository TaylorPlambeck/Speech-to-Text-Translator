import java.net.URI;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;

import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;


//Run the project with this .java.  The SpeechGUI makes the interface.  The Capture.java starts and stops audio recording.
//The SpeechTransSessionSocket.java grabs the message sent back from Microsoft and manipulates it before it is sent to the GUI.
//The other .java files were not changed and are originally from the API.
public class SpeechWebSocketClient 
{
      private static final String speechTranslateUriTemplate = "wss://dev.microsofttranslator.com/speech/translate?from=%1s&to=%2s&api-version=1.0";
      private static String generateWsUrl(String from, String to)  {    return String.format(speechTranslateUriTemplate, from, to);  }
      static String FileNameOnly; 
      static int x=0;
      static int Cliptemp=0;
//static int ClipCount=3; Changed on 4/4/17. If doesn't work,remove commented out while below, and take out the clipcount=temp after files,+next line
      static int ClipCount;
      public static void main(String[] args) 
      {    
	     //YOU CAN CONTINUALLY ADD FILES, JUST PUT IN Cliptemp++ in the array like the ones below, change the filename at the end
    	  //DONT FORGET TO CHANGE THE TOTAL CLIPCOUNT ABOVE, ClipCount.
    	  
    	//  while(Cliptemp<ClipCount)
    	//  {
   //DR LIN FILE PATH CAN BE ADDED BELOW------------------------------------------------------------------------------------------------------
    			SpeechGUI.PathArray[Cliptemp++]="C:\\Users\\Taylor\\Desktop\\API SON\\Java-Speech-Translate-master\\myFile.wav";
    		    SpeechGUI.PathArray[Cliptemp++]="C:\\Users\\Taylor\\Desktop\\API SON\\Java-Speech-Translate-master\\Life.wav";
    		    SpeechGUI.PathArray[Cliptemp++]="C:\\Users\\Taylor\\Desktop\\API SON\\Java-Speech-Translate-master\\HelloWorld.wav";
    		    SpeechGUI.PathArray[Cliptemp++]="C:\\Users\\Taylor\\Desktop\\API SON\\Java-Speech-Translate-master\\dodont.wav";
    		    SpeechGUI.PathArray[Cliptemp++]="C:\\Users\\Taylor\\Desktop\\API SON\\Java-Speech-Translate-master\\douspeak.wav";
    		  //SpeechGUI.PathArray[Cliptemp++]="C:\\Users\\Taylor\\Desktop\\API SON\\Java-Speech-Translate-master\\tastyburger.wav";
    		 // SpeechGUI.PathArray[Cliptemp++]="C:\\Users\\Taylor\\Desktop\\API SON\\Java-Speech-Translate-master\\yourfather.wav";
    		 // SpeechGUI.PathArray[Cliptemp++]="C:\\Users\\Taylor\\Desktop\\API SON\\Java-Speech-Translate-master\\punk.wav";
    		//  SpeechGUI.PathArray[Cliptemp++]="C:\\Users\\Taylor\\Desktop\\API SON\\Java-Speech-Translate-master\\speeding.wav";
    	//	    SpeechGUI.PathArray[Cliptemp++]="C:\\Users\\Taylor\\Desktop\\API SON\\Java-Speech-Translate-master\\EnterNameHere.wav";
    	 // }
    	  ClipCount=Cliptemp;
    	  fileMaker(x++);	//this creates the filename out of the above paths, given to the API
    	  
	     JFrame window = new SpeechGUI();	//Create GUI window  
		 window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	     window.setTitle("Speech to Text Translator");
	     window.setSize(1250,650);
	     window.setVisible(true);	
  /*	     	// ALL THIS MAKES IT SO THE OUTPUT CONSOLE GOES TO A SEPARATE FRAME. COULDNt FIGURE IT OUT THOUGH.
	         JFrame aFrame = new JFrame();         
	         aFrame.setTitle("Status Window");
	        aFrame.setLocationRelativeTo(window);
	       JTextArea jta = new JTextArea(11,108);
	       TextAreaOutputStream taos = TextAreaOutputStream.getInstance(jta);
	       aFrame.setBounds(3,555,10,10);
	       aFrame.getContentPane().add(jta);
	       aFrame.pack();
	       aFrame.show();
   */       
}      

public static void trans(String from, String to)
{
		String ClientID="taplambeck";
		String ClientSecret="taylorplambecksecret";
		 String FileName=FileNameOnly;	//Filename is now figured out above in MAIN
		 System.out.println(FileName);	//output just to test
	
		AdmAccessToken accessToken = AdmAccessToken.getAccessToken(ClientID,ClientSecret);
		SslContextFactory sslContextFactory = new SslContextFactory();
		WebSocketClient client = new WebSocketClient(sslContextFactory);
		SpeechTranslationSessionSocket socket = new SpeechTranslationSessionSocket(FileName);	//pass in the filename here
		
   try {	
         String traceId = UUID.randomUUID().toString();
         ClientUpgradeRequest request = new ClientUpgradeRequest();
         request.setHeader("Authorization", "Bearer " + accessToken.access_token);
         request.setHeader("X-ClientTraceId", traceId);
        
    //THIS (from,to) PASSES THE LANGUAGES YOU WANT TO USE. LIST IS PASTED AT END OF THIS .JAVA     -------------------------------------------------
         String speechTranslateUri = generateWsUrl(from, to);
         client.start();
         URI echoUri = new URI(speechTranslateUri);
         client.connect(socket, echoUri, request);
         System.out.printf("[%s] Connecting to : %s%n", LocalDateTime.now().toString(), echoUri);
         System.out.println("ClientTraceId: " + traceId);
         // wait enough so the server has ample time to respond, THIS DECIDES WHEN TO CLOSE THE CONNECTION. GUI UPDATES WHEN CONNECTION IS CLOSED.
         socket.awaitClose(10, TimeUnit.SECONDS);//lowering this number too much will not give it time to work with server and it will fail during run
   		}
   catch (Throwable t) {t.printStackTrace();}
   finally {   try { client.stop();} 
   				catch (Exception e) { e.printStackTrace();}
   		   }
}

public static void fileMaker(int x)	//this is just to make it easier to add more files. I used to have a separate array for filenames,
									//but this string search was already created for the API recognition/translation finder.
									//so this is more efficient because you can add more files VERY easily at the top of this .java
{
	  //THIS SEGMENT USES THE STRING SEARCH TO FIND THE NAME OF OUR AUDIO FILE FOR USE IN THE API------------------------------------------
			 int FilePathLength=SpeechGUI.PathArray[x].length();	//finds length of filepath
	 	     int start = 0;	//start of audio file name, indexed from the full
	 	     int end=0;
	 	     String wordToFind = "master";	//this searches for the word 'master', because our filename will always be one away
	          Pattern word = Pattern.compile(wordToFind);	//word matcher, gives the index of a match with our wordtoFind
	          Matcher match = word.matcher(SpeechGUI.PathArray[x]);
	          while (match.find()) 
	          {
	               end=(FilePathLength);	//this gives the end of string
	               start=(match.end()+1);	//this uses the end of the word master, to find the beginning of the filename.
	          }
	          FileNameOnly=SpeechGUI.PathArray[x].substring(start, end);	//sets you up with just the filename.        
}
//end of class
}

/*
Supported TTS Languages:
LanguageVoice: ar-EG-Hoda LanguageName: Arabic Locale: ar-EG DisplayName: Hoda Region: Egypt
LanguageVoice: ca-ES-Herena LanguageName: Catalan Locale: ca-ES DisplayName: Herena Region: Spain
LanguageVoice: da-DK-Helle LanguageName: Danish Locale: da-DK DisplayName: Helle Region: Denmark
LanguageVoice: de-DE-Hedda LanguageName: German Locale: de-DE DisplayName: Hedda Region: Germany
LanguageVoice: de-DE-Katja LanguageName: German Locale: de-DE DisplayName: Katja Region: Germany
LanguageVoice: de-DE-Stefan LanguageName: German Locale: de-DE DisplayName: Stefan Region: Germany
LanguageVoice: en-AU-Catherine LanguageName: English Locale: en-AU DisplayName: Catherine Region: Australia
LanguageVoice: en-AU-James LanguageName: English Locale: en-AU DisplayName: James Region: Australia
LanguageVoice: en-AU-Matilda LanguageName: English Locale: en-AU DisplayName: Matilda Region: Australia
LanguageVoice: en-AU-MatildaRUS LanguageName: English Locale: en-AU DisplayName: MatildaRUS Region: Australia
LanguageVoice: en-CA-Linda LanguageName: English Locale: en-CA DisplayName: Linda Region: Canada
LanguageVoice: en-CA-Richard LanguageName: English Locale: en-CA DisplayName: Richard Region: Canada
LanguageVoice: en-GB-George LanguageName: English Locale: en-GB DisplayName: George Region: United Kingdom
LanguageVoice: en-GB-Susan LanguageName: English Locale: en-GB DisplayName: Susan Region: United Kingdom
LanguageVoice: en-IN-Heera LanguageName: English Locale: en-IN DisplayName: Heera Region: India
LanguageVoice: en-IN-Koyal LanguageName: English Locale: en-IN DisplayName: Koyal Region: India
LanguageVoice: en-IN-KoyalRUS LanguageName: English Locale: en-IN DisplayName: KoyalRUS Region: India
LanguageVoice: en-IN-Ravi LanguageName: English Locale: en-IN DisplayName: Ravi Region: India
LanguageVoice: en-US-BenjaminRUS LanguageName: English Locale: en-US DisplayName: BenjaminRUS Region: United States
LanguageVoice: en-US-JessaRUS LanguageName: English Locale: en-US DisplayName: JessaRUS Region: United States
LanguageVoice: en-US-Mark LanguageName: English Locale: en-US DisplayName: Mark Region: United States
LanguageVoice: en-US-Zira LanguageName: English Locale: en-US DisplayName: Zira Region: United States
LanguageVoice: en-US-ZiraRUS LanguageName: English Locale: en-US DisplayName: ZiraRUS Region: United States
LanguageVoice: es-ES-Laura LanguageName: Spanish Locale: es-ES DisplayName: Laura Region: Spain
LanguageVoice: es-ES-Pablo LanguageName: Spanish Locale: es-ES DisplayName: Pablo Region: Spain
LanguageVoice: es-MX-Mila LanguageName: Spanish Locale: es-MX DisplayName: Mila Region: Mexico
LanguageVoice: es-MX-MilaRUS LanguageName: Spanish Locale: es-MX DisplayName: MilaRUS Region: Mexico
LanguageVoice: es-MX-Raul LanguageName: Spanish Locale: es-MX DisplayName: Raul Region: Mexico
LanguageVoice: es-MX-Sabina LanguageName: Spanish Locale: es-MX DisplayName: Sabina Region: Mexico
LanguageVoice: fi-FI-Heidi LanguageName: Finnish Locale: fi-FI DisplayName: Heidi Region: Finland
LanguageVoice: fr-CA-Caroline LanguageName: French Locale: fr-CA DisplayName: Caroline Region: Canada
LanguageVoice: fr-CA-Claude LanguageName: French Locale: fr-CA DisplayName: Claude Region: Canada
LanguageVoice: fr-FR-Julie LanguageName: French Locale: fr-FR DisplayName: Julie Region: France
LanguageVoice: fr-FR-Paul LanguageName: French Locale: fr-FR DisplayName: Paul Region: France
LanguageVoice: it-IT-Cosimo LanguageName: Italian Locale: it-IT DisplayName: Cosimo Region: Italy
LanguageVoice: it-IT-Elsa LanguageName: Italian Locale: it-IT DisplayName: Elsa Region: Italy
LanguageVoice: ja-JP-Ayumi LanguageName: Japanese Locale: ja-JP DisplayName: Ayumi Region: Japan
LanguageVoice: ja-JP-HarukaRUS LanguageName: Japanese Locale: ja-JP DisplayName: HarukaRUS Region: Japan
LanguageVoice: ja-JP-Ichiro LanguageName: Japanese Locale: ja-JP DisplayName: Ichiro Region: Japan
LanguageVoice: ja-JP-Sayaka LanguageName: Japanese Locale: ja-JP DisplayName: Sayaka Region: Japan
LanguageVoice: ja-JP-SayakaRUS LanguageName: Japanese Locale: ja-JP DisplayName: SayakaRUS Region: Japan
LanguageVoice: ko-KR-Minjoon LanguageName: Korean Locale: ko-KR DisplayName: Minjoon Region: Korea
LanguageVoice: ko-KR-Seohyun LanguageName: Korean Locale: ko-KR DisplayName: Seohyun Region: Korea
LanguageVoice: nb-NO-Jon LanguageName: Norwegian Locale: nb-NO DisplayName: Jon Region: Norway
LanguageVoice: nb-NO-Nina LanguageName: Norwegian Locale: nb-NO DisplayName: Nina Region: Norway
LanguageVoice: nl-NL-Frank LanguageName: Dutch Locale: nl-NL DisplayName: Frank Region: Netherlands
LanguageVoice: nl-NL-Marijke LanguageName: Dutch Locale: nl-NL DisplayName: Marijke Region: Netherlands
LanguageVoice: pl-PL-Adam LanguageName: Polish Locale: pl-PL DisplayName: Adam Region: Poland
LanguageVoice: pl-PL-Paulina LanguageName: Polish Locale: pl-PL DisplayName: Paulina Region: Poland
LanguageVoice: pt-BR-Daniel LanguageName: Portuguese Locale: pt-BR DisplayName: Daniel Region: Brazil
LanguageVoice: pt-BR-Maria LanguageName: Portuguese Locale: pt-BR DisplayName: Maria Region: Brazil
LanguageVoice: pt-PT-Helia LanguageName: Portuguese Locale: pt-PT DisplayName: Helia Region: Portugal
LanguageVoice: ru-RU-Irina LanguageName: Russian Locale: ru-RU DisplayName: Irina Region: Russia
LanguageVoice: ru-RU-Pavel LanguageName: Russian Locale: ru-RU DisplayName: Pavel Region: Russia
LanguageVoice: sv-SE-Bengt LanguageName: Swedish Locale: sv-SE DisplayName: Bengt Region: Sweden
LanguageVoice: sv-SE-Karin LanguageName: Swedish Locale: sv-SE DisplayName: Karin Region: Sweden
LanguageVoice: tr-TR-Seda LanguageName: Turkish Locale: tr-TR DisplayName: Seda Region: Turkey
LanguageVoice: tr-TR-Tolga LanguageName: Turkish Locale: tr-TR DisplayName: Tolga Region: Turkey
LanguageVoice: zh-CN-HuihuiRUS LanguageName: Chinese Simplified Locale: zh-CN DisplayName: HuihuiRUS Region: People's Republic of China
LanguageVoice: zh-CN-Kangkang LanguageName: Chinese Simplified Locale: zh-CN DisplayName: Kangkang Region: People's Republic of China
LanguageVoice: zh-CN-Yaoyao LanguageName: Chinese Simplified Locale: zh-CN DisplayName: Yaoyao Region: People's Republic of China
LanguageVoice: zh-HK-Danny LanguageName: Cantonese (Traditional) Locale: zh-HK DisplayName: Danny Region: Hong Kong S.A.R.
LanguageVoice: zh-HK-Tracy LanguageName: Cantonese (Traditional) Locale: zh-HK DisplayName: Tracy Region: Hong Kong S.A.R.
LanguageVoice: zh-TW-Yating LanguageName: Chinese Traditional Locale: zh-TW DisplayName: Yating Region: Taiwan
LanguageVoice: zh-TW-Zhiwei LanguageName: Chinese Traditional Locale: zh-TW DisplayName: Zhiwei Region: Taiwan

*/