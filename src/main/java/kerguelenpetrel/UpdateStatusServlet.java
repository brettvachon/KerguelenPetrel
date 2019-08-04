/*
 * Copyright (c) 2019 daloonik
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
import java.io.PrintWriter;
import java.net.URL;

import java.util.List;
import java.util.Random;

import twitter4j.StatusUpdate;
import twitter4j.Trends;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.feed.synd.SyndLink;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;

@SuppressWarnings("serial")

public class UpdateStatusServlet extends HttpServlet 
  {
  public static final String feedsFile = "WEB-INF/StaticFiles/feeds";
  public static final String[] separator = new String[] { "?","!",",","-"," " };
  private static String[] end = { "?","!"," :-)","...","?!"};
  public static final Random r = new Random();
 
  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException 
    {
    StringBuilder builder = new StringBuilder();  
    String tweet = new String();
    
    PrintWriter out = resp.getWriter();
    resp.setContentType("text/html; charset=UTF-8");
    out.println("Updating status...<br />");

    
    try 
      {

      //Append feed title
      GetFeed feed = new GetFeed(feedsFile); 
      builder.append(feed.title());
      
      //Add separator at the end of the feed title
      builder.append(separator[(r.nextInt(separator.length))] + " ");
      
      feed = new GetFeed(feedsFile); 
      builder.append(feed.description());
      }
    
    catch(FeedException e)
       {
       out.println("Problem with RSS Feed <br />");
       e.printStackTrace(out);
       }       
    
      /* Tweets are maximum 280 characters, so trim our tweet appropriately */
      if(builder.length() > 280) 
         {
         if(builder.lastIndexOf(";",260) > 0)
            builder.setLength(builder.lastIndexOf(";",260)); 
         else if(builder.lastIndexOf(":",260) > 0)
            builder.setLength(builder.lastIndexOf(":", 260));  
         else if(builder.lastIndexOf(",",260) > 0)
            builder.setLength(builder.lastIndexOf(",", 260));
         else builder.setLength(260);
         }
     
      try
         {
         //Set up Twitter
         Twitter twit = TwitterFactory.getSingleton();

         //Get a trend using a WOEID from the list (change this list to match language of your tweet)
         int[] woeids = new int[] { 44418,23424775,2459115 };
         
         Trends t = twit.getPlaceTrends(woeids[r.nextInt(woeids.length)]);
         
         //Append two trends from Twitter
         for(int i=0; i<2; i++)
            {
            builder.append(" ");
            builder.append(t.getTrends()[r.nextInt(t.getTrends().length)].getName());   
            }
  
         /* Tweets are maximum 280 characters */
        if(builder.length() > 280)
          {
          builder.setLength(builder.lastIndexOf(" ", 270));  
          builder.append(end[(r.nextInt(end.length))]);
          }

       //Set the status
       StatusUpdate status = new StatusUpdate(builder.toString());
     
       // Add an image from Flickr for small status 
       if(builder.length() < 60)
          status.setMediaIds(addFlickrImg(twit,resp));   

       twit.updateStatus(status);
       
       tweet = status.getStatus();
       }
 
      catch(TwitterException e)
        {
       out.println("Problem with Twitter \n");
        e.printStackTrace(out);
        }
      finally 
      {
      if(tweet.length() > 0)
         out.println("Tweet posted! <br /> Tweet : " +tweet);
      out.close();  // Always close the output writer
      } 
    }
  
    public static long[] addFlickrImg(Twitter twit, HttpServletResponse resp) throws IOException, TwitterException
      {
      long[] mediaID = new long[1];
      StringBuilder imageFeedUrl = new StringBuilder();
      
      imageFeedUrl.append("http://api.flickr.com/services/feeds/photos_public.gne");
                
      try 
         {
         XmlReader reader = new XmlReader(new URL(imageFeedUrl.toString()));
         SyndFeed feed = new SyndFeedInput().build(reader);
      
         int ind = r.nextInt(feed.getEntries().size()); 
         SyndEntry entry =  (SyndEntry)feed.getEntries().get(ind); //Get a random entry
      
         //Get our links from the feed
         List<SyndLink> links = entry.getLinks();
      
         //get the URL for the image
         String mediaURL = links.get(1).getHref();
         InputStream imageStream = new URL(mediaURL).openConnection().getInputStream(); 
         mediaID[0] = twit.uploadMedia(mediaURL, imageStream).getMediaId();
         }
      catch(FeedException e)
         {
         resp.getWriter().println("Problem with Flickr RSS Feed \n");
         e.printStackTrace(resp.getWriter());
         }
      return mediaID;
       }
   }