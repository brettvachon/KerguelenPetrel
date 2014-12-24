package baconbot_daloonik;

import java.io.IOException;
import java.io.FileNotFoundException;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;
import twitter4j.Trends;
import twitter4j.User;

import java.util.Random;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")

public class BaconbotBotherSomeoneServlet extends HttpServlet
 {
 public static final String dialogueFileEnglish = "WEB-INF/StaticFiles/dialogue.english.txt";
 public static final String dialogueFileFrench = "WEB-INF/StaticFiles/dialogue.french.txt";

 @Override
 public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException 
   {
   GetLine en, fr;
   Twitter bacon;
   String botherSomeoneMsg, buf;
   User victim = null;  
   long cursor = -1;
   long[] friendIDs, victimIDs;
   Random r = new Random();   

   resp.setContentType("text/html");
   resp.getWriter().println("<html>");
   resp.getWriter().println("<body>");
   try 
     {
     en = new GetLine(dialogueFileEnglish);
     fr = new GetLine(dialogueFileFrench);
     
     ConfigurationBuilder twitterConfigBuilder = new ConfigurationBuilder();		
     twitterConfigBuilder.setDebugEnabled(false);
     //Replace these values with your own
     twitterConfigBuilder.setOAuthConsumerKey("ConsumerKey");
     twitterConfigBuilder.setOAuthConsumerSecret("ConsumerSecret");
     twitterConfigBuilder.setOAuthAccessToken("AccessToken");
     twitterConfigBuilder.setOAuthAccessTokenSecret("AccessTokenSecret");
		
     bacon = new TwitterFactory(twitterConfigBuilder.build()).getInstance();

     Trends t = bacon.getPlaceTrends(1);  //global trends
     friendIDs = bacon.getFollowersIDs(bacon.getId(), cursor).getIDs();
     if(friendIDs.length == 0)
        resp.getWriter().println("Cannot find any followers to bother <br>");
     else
       {  
       victimIDs = bacon.getFollowersIDs(friendIDs[r.nextInt(friendIDs.length)], cursor).getIDs();
       if(victimIDs.length == 0)
         resp.getWriter().println("Cannot find any followers to bother <br>");
       else
         {
         victim = bacon.showUser(victimIDs[r.nextInt(victimIDs.length)]);
         do
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
           botherSomeoneMsg = "@" + victim.getScreenName() +" "+ buf + " " +
                t.getTrends()[r.nextInt(t.getTrends().length)].getName();
           } while (botherSomeoneMsg.length() > 140);
         bacon.updateStatus(botherSomeoneMsg);
         resp.getWriter().println("Tweet posted: "+botherSomeoneMsg +"<br>"); 
         }
       } 
     }
   catch(FileNotFoundException e)
     {
     e.printStackTrace(System.err);
     resp.getWriter().println("Input file(s) not found<br>");
     resp.getWriter().println("<pre>");
     e.printStackTrace(resp.getWriter());
     resp.getWriter().println("</pre>");
     }
   catch(TwitterException e)
     {
     resp.getWriter().println("Problem with Twitter <br>");
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


