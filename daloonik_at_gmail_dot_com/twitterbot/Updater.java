package daloonik_at_gmail_dot_com.twitterbot;
/**
 * Updater
 */

import java.util.Random;

import twitter4j.Trends;
import twitter4j.Twitter;
import twitter4j.TwitterException;


public class Updater extends Thread
 {
 private GetLine sentences;
 private String feedfile;
 private Twitter bacon;
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
   Random r = new Random();
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
	      {
          temp = sentences.line();   
          }    
	   buffer.append(" - " + temp + ".");
	   }
	     
	 else if(Character.isLowerCase(buffer.charAt(0)))
	   {
	   buffer.insert(0,(feed.title() + ", "));
	   buffer.append(".");
	   }    	
	 /*add hashtag to short tweets */
	 if(buffer.length() < 120)
       {
       try
         {
          Trends t = bacon.getPlaceTrends(1);  //global trends
   	      buffer.append(" "+t.getTrends()[r.nextInt(t.getTrends().length)].getName());
          }
       catch(TwitterException e)
          {
          System.out.println("Failed to add trend to tweet");
          e.printStackTrace();
          }		 
       }
	 
	 update = buffer.toString();
     } while (update.length() > 140);

   return update;
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
	 this.bacon = t;
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
          bacon.updateStatus(tweet);
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
