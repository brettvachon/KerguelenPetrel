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
import java.io.FileNotFoundException;

import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.EnumSet; 

import twitter4j.StatusUpdate;
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

import net.jeremybrooks.knicker.KnickerException;
import net.jeremybrooks.knicker.WordApi;
import net.jeremybrooks.knicker.WordsApi;
import net.jeremybrooks.knicker.dto.Word;
import net.jeremybrooks.knicker.Knicker.PartOfSpeech; 

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
    
    PrintWriter out = resp.getWriter();
   
    resp.setContentType("text/html; charset=UTF-8");
    
    try 
      {
      //Append feed title
      out.println("Updating status...<br />");
      GetFeed feed = new GetFeed(feedsFile); 
      builder.append(feed.title());
      
      //Add separator at the end of the feed title and before the Wordnik sentence
      builder.append(separator[(r.nextInt(separator.length))] + " ");
      }
    
    catch(FeedException e)
       {
       out.println("Problem generating Feed Title with RSS Feed <br />");
       e.printStackTrace(out);
       }
          
     try
        {     
        //Append Wordnik example sentence
        Properties p = new Properties();
        InputStream in = UpdateStatusServlet.class.getResourceAsStream("wordnik.properties");
        p.load(in);
        System.setProperty("WORDNIK_API_KEY", p.getProperty("WORDNIK_API_KEY"));
        
        builder.append(WordApi.topExample(WordsApi.randomWord().getWord()).getText());
        }
     catch(FileNotFoundException e)
        {
        out.println("Wordnik property file not found <br />");
        e.printStackTrace(out);
        }
     catch(KnickerException e)
        {
       out.println("Problem appending Wordnik example sentence <br />");
       e.printStackTrace(out);
       } 
     
      /* Tweets are maximum 280 characters, so trim our sentence appropriately */
   if(builder.length() > 280) 
         {
         if(builder.lastIndexOf(";",220) > 0)
            builder.setLength(builder.lastIndexOf(";",220)); 
         else if(builder.lastIndexOf(":",220) > 0)
            builder.setLength(builder.lastIndexOf(":", 220));  
         else if(builder.lastIndexOf(",",220) > 0)
            builder.setLength(builder.lastIndexOf(",", 220));
         else builder.setLength(220);
         }
     
    try
         {
         //Set up Twitter
         Twitter twit = TwitterFactory.getSingleton();

         //Append a trend from Twitter
         BuildTrend bt = new BuildTrend(resp, twit);
         builder.append(" ").append(bt.twitterTrend());
          
         // Append a Wordnik trend
         builder.append(" ").append(bt.wordnikTrend()); 
      
       /* Tweets are maximum 280 characters */
        if(builder.length() > 280)
          {
          builder.setLength(builder.lastIndexOf(" ", 270));  
          builder.append(end[(r.nextInt(end.length))]);
          }

       //Set the status
       StatusUpdate status = new StatusUpdate(builder.toString());
     
       // Add an image from Flickr for small status 
       if(builder.length() < 120)
          status.setMediaIds(addFlickrImg(twit,resp));   

       twit.updateStatus(status);
       out.println("Tweet posted! <br /> Tweet : ");
       out.println(status.getStatus());
       }
 
      catch(TwitterException e)
        {
       out.println("Problem with Twitter \n");
        e.printStackTrace(out);
        }
 
      finally 
      {
      out.close();  // Always close the output writer
      } 
    }
  
    public static long[] addFlickrImg(Twitter twit, HttpServletResponse resp) throws IOException, TwitterException
      {
      long[] mediaID = new long[1];
      StringBuilder imageFeedUrl = new StringBuilder();
      String tag;
      
      imageFeedUrl.append("http://api.flickr.com/services/feeds/photos_public.gne");
      
      try
         {
         Properties p = new Properties();
         InputStream in = UpdateStatusServlet.class.getResourceAsStream("wordnik.properties");
         p.load(in);
         System.setProperty("WORDNIK_API_KEY", p.getProperty("WORDNIK_API_KEY"));
         
         boolean hasDictionaryDef = false; 
         EnumSet<PartOfSpeech> includePartOfSpeech = null; 
         EnumSet<PartOfSpeech> excludePartOfSpeech = null; 
         int minCorpusCount = 0; 
         int maxCorpusCount = 0; 
         int minDictionaryCount = 0; 
         int maxDictionaryCount = 0; 
         int minLength = 3; 
         int maxLength = 6; 
        
         Word result = WordsApi.randomWord(hasDictionaryDef, includePartOfSpeech, excludePartOfSpeech, minCorpusCount, maxCorpusCount, minDictionaryCount, maxDictionaryCount, minLength, maxLength); 
         tag = result.getWord();
         imageFeedUrl.append("?tag=").append(tag);
         }
      
      catch(FileNotFoundException e)
         {
         resp.getWriter().println("Wordnik property file not found \n");
         e.printStackTrace(resp.getWriter());
         }
      catch(KnickerException e)
         {
         resp.getWriter().println("Problem with Wordnik \n");
         e.printStackTrace(resp.getWriter());
         }  
            
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
