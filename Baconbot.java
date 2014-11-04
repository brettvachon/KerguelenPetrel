import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

import daloonik_at_gmail_dot_com.twitterbot.*;

public class Baconbot 
 {
 private static final String postIDFile = "baconbot.postIDlog.txt";
 private static long lastPostId;
 
 public static final String sentenceFileEnglish = "sentence.english.txt";
 public static final String feedsFileEnglish = "feeds.english.txt";
 public static final String dialogueFileEnglish = "dialogue.en.txt";

 public static final String sentenceFileFrench = "sentence.french.txt";
 public static final String feedsFileFrench = "feeds.french.txt";
 public static final String dialogueFileFrench = "dialogue.fr.txt";
 
 /** Checks for replies and responds in the user's language
  * 
  * @param bacon Twitter instance
  */
 public void checkreply(Twitter bacon)
    {
    ResponseList<Status> mentions;
	GetLine enline = new GetLine(dialogueFileEnglish);
	GetLine frline = new GetLine(dialogueFileFrench);
	
	try (BufferedReader br = new BufferedReader(new FileReader(postIDFile)))
      {
      lastPostId = Long.parseLong(br.readLine());
      }
    catch (IOException e) 
      {
      System.out.println("Problem with post ID file");
      e.printStackTrace();
      System.exit(1);
      } 
   // get mention list from twitter
   try 
     {
     mentions = bacon.getMentionsTimeline();
     String reply = "";
     if(mentions.size() == 0)
       System.out.println("No mentions so far...");
     else
       {
       for (Status mention : mentions) 
         {
         if (lastPostId < mention.getId()) 
            {
            if(mention.getUser().getId() == bacon.getId()); //don't respond to myself
            else if(mention.isRetweeted()); //don't respond to retweet
            else
               {
                if(mention.getText().toLowerCase().contains("bye")) 
             	   reply = "@" + mention.getUser().getScreenName() + " Ok. Bye";
                else
                  {
         	      switch (mention.getUser().getLang())
                     {
                     case "en":
                       reply = "@" + mention.getUser().getScreenName() + " "
                          + enline.line();
                       break;
                     case "fr":
                          reply = "@" + mention.getUser().getScreenName() + " "
                             + frline.line();
                             break;
                      default:
                          reply = "@" + mention.getUser().getScreenName() + " "
                            + enline.line();
                      }
                  }
               bacon.updateStatus(reply);
               System.out.println("Tweet posted: "+reply);
               }
            }
         }
         // write last post id to the file
         try(FileWriter fw = new FileWriter(new File(postIDFile)))
            {
            fw.write(Long.toString(mentions.get(0).getId()));
            } 
         catch (IOException e) 
            {
            System.out.println("Trouble writing to postID file");
             e.printStackTrace();
            } 
         }
     }
     catch(TwitterException e)
        {
        e.printStackTrace();
        }
    }
 
 public Baconbot(Twitter bacon) 
   {
   BotherSomeone bother = new BotherSomeone(bacon, 154020); 

   Updater en = new Updater(sentenceFileEnglish, feedsFileEnglish, bacon, 350040);
   Updater fr = new Updater(sentenceFileFrench, feedsFileFrench, bacon, 360010);
   
   en.start();
   fr.start();
   bother.start();
   
   while(en.isAlive() || fr.isAlive() || bother.isAlive())
      {
      System.out.println("Check for replies...");
      checkreply(bacon);
      try 
         { 
         Thread.sleep(300000);   //5 minutes
         }
      catch(InterruptedException e)
          {
          System.out.println("Interrupted. Exiting...");
          System.exit(1);
          }
       }
    }

 public static void main(String[] args)
    {
    try 
      {
      System.out.println("Starting Bacon Bot...");
      ConfigurationBuilder twitterConfigBuilder = new ConfigurationBuilder();		
      twitterConfigBuilder.setDebugEnabled(false);
      twitterConfigBuilder.setOAuthConsumerKey("ConsumerKey");
      twitterConfigBuilder.setOAuthConsumerSecret("ConsumerSecret");
      twitterConfigBuilder.setOAuthAccessToken("AccessToken");
      twitterConfigBuilder.setOAuthAccessTokenSecret("AccesTokenSecret");
		
      Twitter bacon = new TwitterFactory(twitterConfigBuilder.build()).getInstance();
      new Baconbot(bacon);
      }
    catch(Exception e)
      {
      System.out.println(" Unexpected error");
      e.printStackTrace();
      }
   }
	 
 }
