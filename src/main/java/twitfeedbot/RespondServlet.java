/*
 * TwitFeedBot is licenced under the The MIT License (MIT)
 * 
 * Copyright (c) 2017 Brett daloonik@gmail.com
 * 
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package twitfeedbot;

import java.io.IOException;
import java.io.FileNotFoundException;

import java.util.Random;

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

import net.jeremybrooks.knicker.KnickerException;
import net.jeremybrooks.knicker.WordApi;
import net.jeremybrooks.knicker.WordsApi;

@SuppressWarnings("serial")

public class RespondServlet extends HttpServlet
 {
 public static final String feedsFile = "WEB-INF/StaticFiles/feeds";
 public static final String CONSUMER_KEY = "your consumer key";
 public static final String CONSUMER_SECRET = "your consumer secret";
 public static final String ACCESS_TOKEN = "your access code";
 public static final String ACCESS_SECRET = "your access secret";
 public static final String WORDNIK_KEY = "your wordnik key";
    
 @Override
 public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException 
   {
   ResponseList<Status> mentions;
   Twitter twit;
   StringBuilder builder = new StringBuilder();
   long lastPostId = 0;
   
   DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
   Entity lastPostIdEntity;
   
   resp.setContentType("text/html");
   resp.getWriter().println("<html>");
   resp.getWriter().println("<body>");
   try 
     {    
     ConfigurationBuilder twitterConfigBuilder = new ConfigurationBuilder();		
     twitterConfigBuilder.setDebugEnabled(false);

     System.setProperty("WORDNIK_API_KEY", WORDNIK_KEY);
     twitterConfigBuilder.setOAuthConsumerKey(CONSUMER_KEY);
     twitterConfigBuilder.setOAuthConsumerSecret(CONSUMER_SECRET);
     twitterConfigBuilder.setOAuthAccessToken(ACCESS_TOKEN);
     twitterConfigBuilder.setOAuthAccessTokenSecret(ACCESS_SECRET);
     
     twit = new TwitterFactory(twitterConfigBuilder.build()).getInstance();
     mentions = twit.getMentionsTimeline();

     lastPostIdEntity = datastore.get(KeyFactory.createKey("lastPostIDEntity", "ID"));
     lastPostId = Long.parseLong(lastPostIdEntity.getProperty("lastPostID").toString());

     if(mentions.size() == 0 || mentions.get(0).getId() == lastPostId)
       resp.getWriter().println("No mentions so far...<br>");
     else
       {
       resp.getWriter().println("Responding to mentions...<br>");   
       for (Status mention : mentions) 
         {
         builder.setLength(0); // Clear the String Builder
         if (lastPostId < mention.getId()) 
            {
               //Figure out how to like a reply with certain words (best fuck etc)
            if(mention.getUser().getId() == twit.getId());//don't respond to myself
            else if(mention.isRetweeted()); //don't respond to retweet
            else if(mention.getText().toLowerCase().contains("bye")) // Say goodbye
              {
              builder.setLength(0); // Clear the String Builder 
              builder.append("@").append(mention.getUser().getScreenName())
                     .append(" Ok. Bye");
               }
            else
               {
               builder.setLength(0); // Clear the String Builder
               builder.append("@").append(mention.getUser().getScreenName());
               builder.append(" ");
               
               //Append Wordnik example sentence
               builder.append(WordApi.topExample(WordsApi.randomWord().getWord()).getText());
               if(builder.length() > 140)
                  {
                     if(builder.lastIndexOf(";",110) > 0)
                        builder.setLength(builder.lastIndexOf(";",110)); 
                     else if(builder.lastIndexOf(":",110) > 0)
                        builder.setLength(builder.lastIndexOf(":", 110));  
                     else if(builder.lastIndexOf(",",110) > 0)
                        builder.setLength(builder.lastIndexOf(",", 110));
                     else builder.setLength(110);
                  }
               }
           twit.updateStatus(builder.toString());
           resp.getWriter().println("Reply posted: "+ builder.toString() +"<br>");
           builder.delete(0,builder.length());  //Clear the builder
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
     resp.getWriter().println("lastPostID not found. Creating...<br>");
     lastPostIdEntity = new Entity("lastPostIDEntity", "ID");
     //lastPostIdEntity.setProperty("lastPostID", 0);
     lastPostIdEntity.setProperty("lastPostID", "883352596160946176");
     datastore.put(lastPostIdEntity);
     
     }
   catch(TwitterException e)
     {
     resp.getWriter().println("Problem with Twitter <br>");
     resp.getWriter().println("<pre>");
     e.printStackTrace(resp.getWriter());
     resp.getWriter().println("</pre>");
     }
   catch(KnickerException e)
      {
      e.printStackTrace(System.err);
      resp.getWriter().println("Problem with Wordnik <br>");
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
