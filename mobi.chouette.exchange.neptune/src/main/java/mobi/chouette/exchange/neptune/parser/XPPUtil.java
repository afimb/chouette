package mobi.chouette.exchange.neptune.parser;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.util.XmlPullUtil;

public class XPPUtil
{
   public static void nextStartTag(XmlPullParser xpp, String name)
         throws XmlPullParserException, IOException
   {
      int eventType = xpp.getEventType();
      while (eventType != XmlPullParser.END_DOCUMENT)
      {
         if (eventType == XmlPullParser.START_TAG)
         {
            if (xpp.getName().equals(name))
            {
               break;
            }
         }
         eventType = xpp.next();
      }
   }

   public static void skipSubTree(Logger log, XmlPullParser xpp) throws XmlPullParserException, IOException
   {
      log.debug("[DSU] skip " + xpp.getName());
      XmlPullUtil.skipSubTree(xpp);
   }
}
