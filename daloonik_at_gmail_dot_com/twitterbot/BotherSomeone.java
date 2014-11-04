package daloonik_at_gmail_dot_com.twitterbot;
/**BotherSomeone - tweet a random twitter user
 * This code is open source. Use at your own risk, blah blah blah...,
 */

import java.util.Random;

import twitter4j.Trends;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;

public class BotherSomeone extends Thread
 {
 public static final String dialogueFileEnglish = "dialogue.en.txt";
 public static final String dialogueFileFrench = "dialogue.fr.txt";
 
 private Twitter bacon;
 private int sleeptime;
 private static GetLine en = new GetLine(dialogueFileEnglish);
 private static GetLine fr = new GetLine(dialogueFileFrench);
 
 /**Bothers a random follower of one of my followers
  * 
  * @return msg Message for twitter user
  */
 public String bother() 
    {
    User victim = null;  
    long cursor = -1;
    long[] friendIDs, victimIDs;
    Random r = new Random();
    String msg = "";
   
    try
      {
      String buf = "";
      Trends t = bacon.getPlaceTrends(1);  //global trends
      friendIDs = bacon.getFollowersIDs(bacon.getId(), cursor).getIDs();
      if(friendIDs.length == 0)
        return msg;       //skip if no followers
	 
      victimIDs = bacon.getFollowersIDs(friendIDs[r.nextInt(friendIDs.length)], cursor).getIDs();
      if(victimIDs.length == 0)
         return msg;       //skip if no followers

      victim = bacon.showUser(victimIDs[r.nextInt(victimIDs.length)]);
      if (victim == null)
        return msg;   
      else
        {
        switch (victim.getLang())
           {
           case "en":
              buf = en.line();
              break;
           case "fr":
              buf = fr.line();
              break;
           default:
              buf = en.line();
           }
        msg = "@" + victim.getScreenName() +" "+ buf + " "
           +t.getTrends()[r.nextInt(t.getTrends().length)].getName();
        }
     }
     catch (TwitterException e) 
        {
        e.printStackTrace();
        }
     return msg;
     }

 /** Overrides java.lang.Thread.run
  *
  */
 public void run()
    {
	String tweet = "";
	while(true)
	   {
           tweet = "";
	   tweet = this.bother();
	   if (tweet.length() > 140 || tweet.length() == 0); //discard tweet
	   else
	      {
              try 
                {
                bacon.updateStatus(tweet);
                System.out.println("Tweet posted: "+tweet);
             } 
          catch (TwitterException e) 
             {
             e.printStackTrace();
             }
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
 /** Bothers a random twitter user
  * @param t Twitter4j instance
  * @param time Time to wait before bothering someone else
  */
 public BotherSomeone(Twitter t, int time)
    {
    this.bacon = t;
    this.sleeptime = time;
    }
    
 }
