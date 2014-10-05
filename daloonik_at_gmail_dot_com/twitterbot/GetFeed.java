package daloonik_at_gmail_dot_com.twitterbot;
/**
 * GetFeed - get title from a XML feed
 * Working on doing something with the contents
 * This code is open source, use at your own risk, blah blah blah...
 * Made in 2014
 */

import java.util.Random;

import java.net.URL;

import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

//import org.jdom.Document;

public class GetFeed
 {
 private String feedURLfile;
 
 /**Returns a random title from an XML feed
  * @return title Random title selected
  */
 public String title()
   {
   XmlReader reader = null;
   SyndFeed feed = null;
   String feedtitle = "";
   GetLine feedURL = new GetLine(feedURLfile);
   Random rand = new Random();
   try 
 	  {
      reader = new XmlReader(new URL(feedURL.line()));
      feed = new SyndFeedInput().build(reader);
      int ind = rand.nextInt(feed.getEntries().size()); 
      SyndEntry entry = (SyndEntry)feed.getEntries().get(ind);
      feedtitle = entry.getTitle();
      }  
   catch (Exception ex) 
      {
      ex.printStackTrace();
      System.out.println("ERROR: "+ex.getMessage());
      }

   return feedtitle;
   }
 
 /** Work in progress
  * @return feedcontent Doesn't return anything meaningful yet
  */
 public String contents()
    {
    XmlReader reader = null;
    SyndFeed feed = null;
    String feedcontent = "";
    GetLine feedURL = new GetLine(feedURLfile);
    Random rand = new Random();
    System.out.println("geeddd Line is"+feedURL.line());
    try 
      {
      reader = new XmlReader(new URL(feedURL.line()));
      feed = new SyndFeedInput().build(reader);
      SyndEntry entry = (SyndEntry)feed.getEntries().get(rand.nextInt(feed.getEntries().size()));
      if(entry.getContents() != null)
        {
        SyndContent content = (SyndContent)entry.getContents().get(rand.nextInt(entry.getContents().size()));
        feedcontent = content.getValue();
        }
      }  
    catch (Exception ex) 
      {
      ex.printStackTrace();
      System.out.println("ERROR: "+ex.getMessage());
      }
    return feedcontent;
    }

 /**Constructor - sets local values
  * @param urlfile File with XML URLs
  */ 
 public GetFeed(String urlfile)
   {
   this.feedURLfile = urlfile;
   }
 }

    
   

 

