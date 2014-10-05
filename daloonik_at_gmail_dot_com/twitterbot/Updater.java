package daloonik_at_gmail_dot_com.twitterbot;
/**
 * Updater
 */

import twitter4j.Twitter;
import twitter4j.TwitterException;

public class Updater extends Thread
 {
 private GetLine sentences;
 private String feedfile;
 private Twitter twitterbacon;
 private int sleeptime;
 
 /**
  * status - make an update with format sentence - feed title - sentence.	
  * Or instead make an update format feed title - sentence if sentence
  * starts with Lowercase letter
  * @return update Sentence with update
  */
 public String status()
   {
   String update = "";
   StringBuffer buffer = new StringBuffer();  
   GetFeed feed = new GetFeed(feedfile);
   do
	 {	
	 buffer.setLength(0);
     buffer.append(sentences.line());
	 
	 if(Character.isUpperCase(buffer.charAt(0)))
	   {
	   buffer.append(" - ").append(feed.title());
	   String temp = sentences.line();
	
	   //Now find sentence starting with lowercase letter and append.
	   while(Character.isUpperCase(temp.charAt(0)))
	   temp = sentences.line();    
	   buffer.append(" - " + temp + ".");
	   }
		
	     
	 else if(Character.isLowerCase(buffer.charAt(0)))
	   {
	   buffer.insert(0,(feed.title() + ", "));
	   buffer.append(".");
	   }
	     					
	 update = buffer.toString();
     } while (update.length() > 140);

   return update;
   /*if(update.length() < 120)  */ //do something with hashtags
   }	
 
	/** Updater sets local variables
	 * @param sentencefile file with sentences
	 * @param feedfile file containing links to xml feeds
	 * @param t Twitter4j instance
	 * 
	 */
  public Updater(String sentencefile, String feedfile, Twitter t, int time) 
     {
	 this.feedfile = feedfile;
	 this.sentences = new GetLine(sentencefile);
	 this.twitterbacon = t;
	 this.sleeptime = time;
	 }
 
 public void run()
    {
	String tweet = "";
	while(true)
	   {
	   tweet = this.status();
	      try 
          {
          twitterbacon.updateStatus(tweet);
          System.out.println("Tweet posted: "+tweet);
          } 
       catch (TwitterException e) 
          {
          e.printStackTrace();
          }
	   try
	      {
	      Thread.sleep(sleeptime);   //wait 
	      }
	   catch(InterruptedException e)
	      {
	      System.out.println("Bot interrupted! Exiting...");
	      System.exit(1);
	      }
	   }
     }
 }
