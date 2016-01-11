package com.shekoofeh.example.parser;

import com.shekoofeh.example.model.Flower;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by SHEKOOFEH on 11/11/2015.
 */
public class GeoIpParser {

    public static String parseFeed(String content) {

        try {

            boolean inDataItemTag = false;
            String currentTagName = "";
            String CountryName="";

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(new StringReader(content));

            int eventType = parser.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {

                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        currentTagName = parser.getName();
                        if (currentTagName.equals("CountryName")) {
                            inDataItemTag = true;

                        }
                        break;

                    case XmlPullParser.END_TAG:
                        if (parser.getName().equals("CountryName")) {
                            inDataItemTag = false;
                        }
                        currentTagName = "";
                        break;

                    case XmlPullParser.TEXT:
                        if (inDataItemTag ) {
                            switch (currentTagName) {
                                case "CountryName":
                                    CountryName=parser.getText();

                                default:
                                    break;
                            }
                        }
                        break;
                }

                eventType = parser.next();

            }

            return CountryName;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }


    }

}
