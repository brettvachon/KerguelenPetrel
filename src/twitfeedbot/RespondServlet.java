/*
 * TwitFeedBot is licenced under the The MIT License (MIT)
 * 
 * Copyright (c) 2014–2015 Daloonik daloonik@gmail.com
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
import net.jeremybrooks.knicker.dto.Example;
import net.jeremybrooks.knicker.dto.Word;

@SuppressWarnings("serial")

public class RespondServlet extends HttpServlet
 {
 @Override
 public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException 
   {
   ResponseList<Status> mentions;
   Twitter twit;
   String reply = "";
   StringBuffer buffer = new StringBuffer();
   long lastPostId = 0;
   Random r = new Random();
   String[] end = { "?","!"," :-)","...","?!"};
   
   DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
   Entity lastPostIdEntity;
   
   resp.setContentType("text/html");
   resp.getWriter().println("<html>");
   resp.getWriter().println("<body>");
   try 
     {    
     ConfigurationBuilder twitterConfigBuilder = new ConfigurationBuilder();		
     twitterConfigBuilder.setDebugEnabled(false);

     //Replace these values with your own
     System.setProperty("WORDNIK_API_KEY", "YourWordnikKey");
     twitterConfigBuilder.setOAuthConsumerKey("ConsumerKey");
     twitterConfigBuilder.setOAuthConsumerSecret("ConsumerSecret");
     twitterConfigBuilder.setOAuthAccessToken("AccessToken");
     twitterConfigBuilder.setOAuthAccessTokenSecret("AccessTokenSecret");

     twit = new TwitterFactory(twitterConfigBuilder.build()).getInstance();
     mentions = twit.getMentionsTimeline();

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
            if(mention.getUser().getId() == twit.getId()); //don't respond to myself
            else if(mention.isRetweeted()); //don't respond to retweet
            else if(mention.getText().toLowerCase().contains("bye")) 
              reply = "@" + mention.getUser().getScreenName() + " Ok. Bye";
            else
               {
               buffer.setLength(0);
               buffer.insert(0, "@" + mention.getUser().getScreenName() + " ");
               Word random = WordsApi.randomWord();
               Example ex = WordApi.topExample(random.getWord());;
               buffer.append(ex.getText());
               if(buffer.length() > 140)
                  {
                  buffer.setLength(buffer.lastIndexOf(" ", 120));  //Tweets are maximum 140 characters
                  buffer.append(end[(r.nextInt(end.length))]);
                  }
               reply = buffer.toString();
               }
           twit.updateStatus(reply);
           resp.getWriter().println("Tweet posted: "+reply +"<br>");
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
