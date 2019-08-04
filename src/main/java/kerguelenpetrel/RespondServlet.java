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
import java.io.PrintWriter;
import java.util.Random;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.StatusUpdate;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.KeyFactory;

import com.rometools.rome.io.FeedException;

@SuppressWarnings("serial")

public class RespondServlet extends HttpServlet
 {
 private static DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
 private static Entity lastPostIdEntity;
 private static long lastPostId = 0;
 public static final String feedsFile = "WEB-INF/StaticFiles/feeds";
 public static final String[] separator = new String[] { "?","!",",","-"," " };
 public static final Random r = new Random();
 
 @Override
 public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException 
   {
   StringBuilder builder = new StringBuilder();   
   PrintWriter out = resp.getWriter();
   resp.setContentType("text/plain; charset=UTF-8");
   
   try 
     {    
     //Get the Twitter object
     Twitter twit = TwitterFactory.getSingleton();
     ResponseList<Status> mentions = twit.getMentionsTimeline();

     lastPostIdEntity = datastore.get(KeyFactory.createKey("lastPostIDEntity", "ID"));
     lastPostId = Long.parseLong(lastPostIdEntity.getProperty("lastPostID").toString());

     if(mentions.size() == 0)
        {
       out.println("No mentions so far...");
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
               builder.setLength(0);
               builder.append("@");
               builder.append(mention.getUser().getScreenName());
               builder.append(" Bye");
               }
            else
               {
               builder.setLength(0);
               //Add the screen name of the person we are responding to
               builder.append("@"); 
               builder.append(mention.getUser().getScreenName() + " ");
               
               //Get feed title as content
               GetFeed feed = new GetFeed(feedsFile); 
               builder.append(feed.title());
           
               /* Tweets are maximum 280 characters */
               if(builder.length() > 280)
                  builder.setLength(280);
               }
               
            //Set the status
            StatusUpdate status = new StatusUpdate(builder.toString());
            
            //Post the status
            twit.updateStatus(status);
            out.println("Tweet posted: "+ status.getStatus());
            }
         }
       //Save last post ID
       lastPostIdEntity.setProperty("lastPostID", (Long.toString(mentions.get(0).getId())));
       datastore.put(lastPostIdEntity);
       }
     catch (EntityNotFoundException e) 
       {
       // Make new ResponseIDentity
       lastPostIdEntity = new Entity("lastPostIDEntity", "ID");
       lastPostIdEntity.setProperty("lastPostID", "0");
       datastore.put(lastPostIdEntity);
       out.println("Made new lastPostId " +lastPostIdEntity.getProperty("lastPostID").toString());
       }
    catch(TwitterException e)
      {
      out.println("Problem with Twitter \n");
      e.printStackTrace(out);
      }
   catch(FeedException e)
      {
      resp.getWriter().println("Problem with RSS Feed \n");
      e.printStackTrace(out);
      }
   
   finally 
      {
      out.close();  // Always close the output writer
      } 
    }
  }