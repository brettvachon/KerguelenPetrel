package baconbot_daloonik;

import java.io.IOException;
import java.io.FileNotFoundException;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;
import twitter4j.ResponseList;
import twitter4j.Status;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.KeyFactory;

@SuppressWarnings("serial")

public class BaconbotResponseServlet extends HttpServlet
 {
 public static final String dialogueFileEnglish = "WEB-INF/StaticFiles/dialogue.english.txt";
 public static final String dialogueFileFrench = "WEB-INF/StaticFiles/dialogue.french.txt";

 @Override
 public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException 
   {
   ResponseList<Status> mentions;
   GetLine enline, frline;
   Twitter bacon;
   String reply = "";
   long lastPostId = 0;
   
   DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
   Entity lastPostIdEntity;
   
   resp.setContentType("text/html");
   resp.getWriter().println("<html>");
   resp.getWriter().println("<body>");
   try 
     {
     enline = new GetLine(dialogueFileEnglish);
     frline = new GetLine(dialogueFileFrench);
     
     ConfigurationBuilder twitterConfigBuilder = new ConfigurationBuilder();		
     twitterConfigBuilder.setDebugEnabled(false);
     //Replace these values with your own
     twitterConfigBuilder.setOAuthConsumerKey("ConsumerKey");
     twitterConfigBuilder.setOAuthConsumerSecret("ConsumerSecret");
     twitterConfigBuilder.setOAuthAccessToken("AccessToken");
     twitterConfigBuilder.setOAuthAccessTokenSecret("AccessTokenSecret");
		
     bacon = new TwitterFactory(twitterConfigBuilder.build()).getInstance();
     mentions = bacon.getMentionsTimeline();

     lastPostIdEntity = datastore.get(KeyFactory.createKey("lastPostIDEntity", "ID"));
     lastPostId = Long.parseLong(lastPostIdEntity.getProperty("lastPostID").toString());

     if(mentions.size() == 0)
       resp.getWriter().println("No mentions so far...<br>");
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
             do
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
               } while (reply.length() > 140);
             bacon.updateStatus(reply);
             resp.getWriter().println("Tweet posted: "+reply +"<br>");
             }
           }
         }
       //Save last post ID
       lastPostIdEntity.setProperty("lastPostID", (Long.toString(mentions.get(0).getId())));
       datastore.put(lastPostIdEntity);
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
   catch (EntityNotFoundException e) 
     {
     // Make new ResponseIDentity
     lastPostIdEntity = new Entity("lastPostIDEntity", "ID");
     lastPostIdEntity.setProperty("lastPostID", "533739405236649984");
     datastore.put(lastPostIdEntity);
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
