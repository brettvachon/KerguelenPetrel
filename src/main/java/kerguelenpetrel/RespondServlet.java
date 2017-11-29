/*
 * Copyright (c) 2017 daloonik
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package kerguelenpetrel;

import java.io.IOException;
import java.io.InputStream;
import java.io.FileNotFoundException;
import java.util.Properties;
import java.util.Random;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
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
import net.jeremybrooks.knicker.WordsApi;


@SuppressWarnings("serial")

public class RespondServlet extends HttpServlet
 {
 private static DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
 private static Entity lastPostIdEntity;
 private static long lastPostId = 0;
 public static final String feedsFile = "WEB-INF/StaticFiles/feeds";
 private static String[] end = { "?","!"," :-)","...","?!"};
 
 @Override
 public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException 
   {
   Random r = new Random();
   resp.setContentType("text/plain; charset=UTF-8");
   
   try 
     {    
     //Get the Twitter object
     Twitter twit = TwitterFactory.getSingleton();
     ResponseList<Status> mentions = twit.getMentionsTimeline();
     StringBuilder builder = new StringBuilder(280);   //Tweets are 280 characters

     lastPostIdEntity = datastore.get(KeyFactory.createKey("lastPostIDEntity", "ID"));
     lastPostId = Long.parseLong(lastPostIdEntity.getProperty("lastPostID").toString());

     if(mentions.size() == 0)
        {
       resp.getWriter().println("No mentions so far...\n");
       return;
        }
     
     for (Status mention : mentions) 
         {
         if (lastPostId < mention.getId()) 
            {
            if(mention.getUser().getId() == twit.getId()); //don't respond to myself
            
            else if(mention.isRetweeted())
               mention = twit.createFavorite(mention.getId()); //mark the retweet as a favourite
            else if(mention.getText().toLowerCase().contains("bye")) 
               {
               builder.insert(0, "@");
               builder.append(mention.getUser().getScreenName());
               builder.append(" Bye");
               }
            else
               {
               builder.setLength(0);
               //Add the screen name of the person we are responding to
               builder.insert(0, "@" + mention.getUser().getScreenName() + " ");
               
               //Append feed contents
               GetFeed feed = new GetFeed(feedsFile); 
               builder.append(feed.title());
               
               //Append random word from Wordnik as hashtag
               Properties p = new Properties();
               InputStream in = RespondServlet.class.getResourceAsStream("wordnik.properties");
               p.load(in);
               System.setProperty("WORDNIK_API_KEY", p.getProperty("WORDNIK_API_KEY"));
               
               // Make up a trend by combining two words
               builder.append(" #" + WordsApi.randomWord().getWord() + WordsApi.randomWord().getWord());
               if(builder.length() > 280)
                  {
                   /* Tweets are maximum 280 characters */
                  builder.setLength(builder.lastIndexOf(" ", 270));  
                  builder.append(end[(r.nextInt(end.length))]);
                  }
               }
           twit.updateStatus(builder.toString());
           resp.getWriter().println("Tweet posted: \n");
           resp.getWriter().println(builder.toString());
            }
         }
       //Save last post ID
       lastPostIdEntity.setProperty("lastPostID", (Long.toString(mentions.get(0).getId())));
       datastore.put(lastPostIdEntity);
       }
   catch(FileNotFoundException e)
      {
      resp.getWriter().println("Input file(s) not found \n");
      e.printStackTrace(resp.getWriter());
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
     resp.getWriter().println("Problem with Twitter \n");
     e.printStackTrace(resp.getWriter());
     }
   catch(KnickerException e)
      {
      resp.getWriter().println("Problem with Wordnik \n");
      e.printStackTrace(resp.getWriter());
      }
   catch(Exception e)
     {
     resp.getWriter().println("Problem! \n");
     e.printStackTrace(resp.getWriter());
     }
   }
 }
