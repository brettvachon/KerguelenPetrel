package baconbot_daloonik;

import java.io.IOException;
import java.io.FileNotFoundException;

import com.sun.syndication.io.FeedException;

import java.util.Random;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.Trends;
import twitter4j.conf.ConfigurationBuilder;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")

public class BaconbotUpdateEnglishServlet extends HttpServlet 
 {
 public static final String sentenceFileEnglish = "WEB-INF/StaticFiles/sentence.english.txt";
 public static final String feedsFileEnglish = "WEB-INF/StaticFiles/feeds.english.txt";
 
 public Twitter bacon;
 
 /**
  * status - make an update with format sentence - feed title - sentence.    
  * Or instead make an update format feed title - sentence if sentence
  * starts with Lowercase letter
  * @return update Sentence with update
  */
 public String status() throws FileNotFoundException
   {
   String update = "";
   StringBuffer buffer = new StringBuffer();  
   GetFeed feed = new GetFeed(feedsFileEnglish);
   GetLine sentences = new GetLine(sentenceFileEnglish);
   Random r = new Random();
   do
     {
     buffer.setLength(0);
     buffer.append(sentences.line());
     if(Character.isUpperCase(buffer.charAt(0)))
       {
       try
         {
         buffer.append(" - ").append(feed.title());
         String temp = sentences.line();
         while(Character.isUpperCase(temp.charAt(0)))
           {
           temp = sentences.line();   
           }    
         buffer.append(" - " + temp + ".");
         }
       catch(IOException | FeedException e)
         {
         buffer.append("!");    		  
         }
       }        
     else if(Character.isLowerCase(buffer.charAt(0)))
       {
       try
         {
         buffer.insert(0,(feed.title() + ", "));
         buffer.append(".");		 
    	 }
       catch(IOException | FeedException e)
         {
         buffer.append("?");
         }
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
         buffer.append("#bacon");        
         }         
       }
     update = buffer.toString();
     } while (update.length() > 140);

   return update;
   }    
   
  @Override
 public void doGet(HttpServletRequest req, HttpServletResponse resp)
    throws IOException 
   {
   String tweet = "";
   resp.setContentType("text/html");
   resp.getWriter().println("<html>");
   resp.getWriter().println("<body>");
    try 
     {
     resp.getWriter().println("Updating status...<br>");
     
     ConfigurationBuilder twitterConfigBuilder = new ConfigurationBuilder();		
     twitterConfigBuilder.setDebugEnabled(false);
     //Replace these values with your own
     twitterConfigBuilder.setOAuthConsumerKey("ConsumerKey");
     twitterConfigBuilder.setOAuthConsumerSecret("ConsumerSecret");
     twitterConfigBuilder.setOAuthAccessToken("AccessToken");
     twitterConfigBuilder.setOAuthAccessTokenSecret("AccessTokenSecret");
	
     bacon = new TwitterFactory(twitterConfigBuilder.build()).getInstance();
     tweet = status();
     bacon.updateStatus(tweet);
     resp.getWriter().println("Tweet posted: "+ tweet);
     }
   catch(FileNotFoundException e)
     {
     e.printStackTrace(System.err);
     resp.getWriter().println("Input file(s) not found<br>");
     resp.getWriter().println("<pre>");
     e.printStackTrace(resp.getWriter());
     resp.getWriter().println("</pre>");
     }
   catch(Exception e)
     {
     e.printStackTrace(System.err);
     resp.getWriter().println("<pre>");
     e.printStackTrace(resp.getWriter());
     resp.getWriter().println("</pre>");
     }
    
    }
}
