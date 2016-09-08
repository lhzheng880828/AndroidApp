package com.base.module.pack.update;
/**
 * Copyright (c) 2009-2011 by Grandstream Networks
 */
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;
import android.util.Log;
import android.util.Xml;
import com.base.module.pack.bean.Pack;
public class PackParser {
    protected static final String TAG = "PullPackParser";

    public List<Pack> parse(InputStream is) throws Exception {
        XmlPullParser parser = Xml.newPullParser();
        try {
            parser.setInput(is, "UTF-8");
            int eventType = parser.getEventType();
            List<Pack> packs = null;
            Pack pack = null;
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                case XmlPullParser.START_DOCUMENT:
                    packs = new ArrayList<Pack>();
                    break;
                case XmlPullParser.START_TAG:
                    String name = parser.getName();
                    if (name.equals("pack")) {
                        pack = new Pack();
                        pack.setPackLName(parser.getAttributeValue(null, "pack_name"));
                    } else if (pack != null && name.equals("downloadurl")) {
                        pack.setPackDownRoute(parser.nextText());
                    }
                    break;
                case XmlPullParser.END_TAG:
                    if (parser.getName().equals("pack") && pack != null) {
                        packs.add(pack);
                        pack = null;
                    }
                    break;
                }
                eventType = parser.next();
            }
            is.close();
            return packs;
        } catch (Exception e) {
            Log.e(TAG, "eeeeee" + e.toString());
            e.printStackTrace();
        }
        return null;
    }

    public String serialize(List<Pack> packs) throws Exception {
        XmlSerializer serializer = Xml.newSerializer();
        StringWriter writer = new StringWriter();
        serializer.setOutput(writer);
        serializer.startDocument("UTF-8", true);
        serializer.startTag("", "packs");
        String appCode;
        for (Pack pack : packs) {
            serializer.startTag("", "pack");
            serializer.attribute("", "pack_name", pack.getPackLName());
            //			Log.d(TAG, "===========writer==============pack--" + pack);
            appCode = pack.getPackSerial();
            //appCode = appCode.substring(0, appCode.lastIndexOf("_"));
            serializer.startTag("", "apk_code");
            serializer.text(appCode);
            serializer.endTag("", "apk_code");
            serializer.startTag("", "version");
            serializer.text(pack.getPackVersion() + "");
            //			serializer.text("1.0" + "");
            serializer.endTag("", "version");
            serializer.startTag("", "versioncode");
            serializer.text(pack.getPackVersionCode() + "");
            //			serializer.text("1.0" + "");
            serializer.endTag("", "versioncode");

            serializer.endTag("", "pack");
        }
        serializer.endTag("", "packs");
        serializer.endDocument();
        return writer.toString();
    }
}