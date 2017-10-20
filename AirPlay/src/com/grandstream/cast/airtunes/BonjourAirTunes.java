
package com.grandstream.cast.airtunes;


import java.util.HashMap;
import java.util.Map;

import com.grandstream.cast.airplay.AirPlay;
import com.grandstream.cast.util.Bonjour;

import javax.jmdns.ServiceInfo;

public class BonjourAirTunes extends Bonjour {
	
	public BonjourAirTunes(String name, int port) {
	    super(name, port);
    }

	protected ServiceInfo onCreateServiceInfo(byte[] hwAddr, String name, int port) {
		String identifier = getStringHardwareAdress(hwAddr);
		Map<String, String> txt = new HashMap<String, String>();
		txt.put("tp", "UDP"); /*传输方式*/
		txt.put("sm", "false");
		txt.put("sv", "false");/*不清楚*/
		txt.put("ek", "1");
		txt.put("et", "0,1"); /*supported encryption types, 0, no encryption; 1,RSA (AirPort Express);3, FairPlay;4,MFiSAP (3rd-party devices); 5,FairPlay SAPv2.5*/
		txt.put("md", "0,1,2"); /*metadata: text, artwork, progress*/
		txt.put("cn", "0,1");/*audio codecs 0	PCM，1	Apple Lossless (ALAC)， 2	AAC， 3	AAC ELD (Enhanced Low Delay) */
		txt.put("sr", "44100");/*audio sample rate: 44100 Hz*/
		txt.put("ch", "2"); /*audio channels: stereo*/
		txt.put("ss", "16"); /*audio sample size: 16-bit*/
		txt.put("pw", "false");  	/*does the speaker require a password?*/
		txt.put("vn", "3");  
		txt.put("txtvers", "1");  /*TXT record version 1*/
		txt.put("vs", AirPlay.SRCVERS);  /*server version 130.14*/
		txt.put("am",AirPlay.MODEL);  /*device model*/
		//txt.put("pk", AirTunes.PKSTR);  还不知道这个字段的用处
		//txt.put("da", "true");还不知道怎么用
		//txt.put("ft", AirPlay.FEATURES);  support function
		txt.put("w", "1");  //unknown
		//txt.put("vn", "65537");  //unknown
		//txt.put("sf", "0x4");  //unknown
		
		return ServiceInfo
        .create(AirTunes.AIR_TUNES_SERVICE_TYPE,
        		identifier + "@" + name ,
                port, 0, 0, txt);
	}

	private static String getStringHardwareAdress(byte[] hwAddr) {
		StringBuilder sb = new StringBuilder();
		for (byte b : hwAddr) {
			sb.append(String.format("%02x", b));
		}
		return sb.toString();
	}
}
