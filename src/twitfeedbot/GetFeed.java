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
/**
 * GetFeed - get title from a XML feed
 */

import java.util.Random;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.net.URL;

import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;
import com.sun.syndication.io.FeedException;

//import org.jdom.Document;

public class GetFeed
 {
 private String feedURLfile;
 
 /**Returns a random title from an XML feed
  * @return title Random title selected
  */
 public String title() throws FileNotFoundException, IOException, FeedException
   {
   GetLine feedURL = new GetLine(feedURLfile);
   XmlReader reader = new XmlReader(new URL(feedURL.line()));;
   SyndFeed feed = new SyndFeedInput().build(reader);;
   String feedtitle = "";
   
   Random rand = new Random();
 
      int ind = rand.nextInt(feed.getEntries().size()); 
      SyndEntry entry = (SyndEntry)feed.getEntries().get(ind);
      feedtitle = entry.getTitle();
   return feedtitle;
   }
 
 /** Work in progress
  * @return feedcontent Doesn't return anything meaningful yet
  */
 public String contents() throws FileNotFoundException
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

    
   

 

