package com.grandstream.cast.airtunes.raop;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.dd.plist.BinaryPropertyListParser;
import com.dd.plist.BinaryPropertyListWriter;
import com.dd.plist.NSArray;
import com.dd.plist.NSData;
import com.dd.plist.NSDictionary;
import com.dd.plist.NSNumber;
import com.dd.plist.NSObject;
import com.dd.plist.PropertyListFormatException;
import com.grandstream.cast.airplay.AirPlay;
import com.grandstream.cast.airtunes.AirTunes;
import com.grandstream.cast.control.PlaybackControl;
import com.grandstream.cast.util.FairPlay;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.security.PrivateKey;
import java.sql.SQLClientInfoException;
import java.util.Arrays;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Cipher;


/**
 * An primitive RTSP responder for replying iTunes
 * 
 * @author bencall
 */
public class RTSPResponder extends Thread{
	
	
	private static final String TAG = RTSPResponder.class.getSimpleName();
	
	private static final byte[] CRLFCRLF = {
	        '\r', '\n', '\r', '\n'
	};

	private Socket socket;					// Connected socket
	private PrivateKey mKey;

	private AudioServer mAudioServer; 				// Audio listener
	private AudioSession mAudioSession;
	
	final byte[] hwAddr;
	final byte[] ip;
	private InputStream mInput;
//	private BufferedReader in;
	
	public RTSPResponder(Socket socket, PrivateKey key) throws IOException {
		this.socket = socket;
		mInput = socket.getInputStream();
//		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		InetAddress addr = socket.getLocalAddress();
		ip =  addr.getAddress();
		hwAddr = NetworkInterface.getByInetAddress(addr).getHardwareAddress();
		mKey = key;
	}


	public RTSPResponse handlePacket(RTSPPacket packet){
		// We init the response holder
		RTSPResponse response = new RTSPResponse("RTSP/1.0 200 OK");

		// Apple Challenge-Response field if needed
    	String challenge;
    	if( (challenge = packet.valueOfHeader("Apple-Challenge")) != null){
    		response.append("Audio-Jack-Status", "connected; type=analog");
    		response.append("CSeq", packet.valueOfHeader("CSeq"));
    		
    		// BASE64 DECODE
    		byte[] decoded = Base64.decode(challenge, Base64.DEFAULT);

    		ByteArrayOutputStream out = new ByteArrayOutputStream();
    		// Challenge
    		try {
				out.write(decoded);
				// IP-Address
				out.write(ip);
				// HW-Addr
				out.write(hwAddr);

				// Pad to 32 Bytes
				int padLen = 32 - out.size();
				for(int i = 0; i < padLen; ++i) {
					out.write(0x00);
				}

			} catch (IOException e) {
				e.printStackTrace();
			}

    		// RSA
    		byte[] crypted = this.encryptRSA(out.toByteArray());
    		
    		// Encode64
    		String ret = Base64.encodeToString(crypted, Base64.DEFAULT);
    		
    		// On retire les ==
	        ret = ret.replace("=", "").replace("\r", "").replace("\n", "");

    		// Write
        	response.append("Apple-Response", ret);
    	} 
    	
		// Paquet request
		String REQ = packet.getReq();
		Log.d(TAG, " REQ is "+REQ);
        if(REQ.contentEquals("OPTIONS")){
        	response.append("Audio-Jack-Status", "connected; type=analog");
    		response.append("CSeq", packet.valueOfHeader("CSeq"));
        	
        	// The response field
        	response.append("Public", "ANNOUNCE, SETUP, RECORD, PAUSE, FLUSH, TEARDOWN, OPTIONS, GET_PARAMETER, SET_PARAMETER");

        } else if (REQ.contentEquals("ANNOUNCE")){
        	response.append("Audio-Jack-Status", "connected; type=analog");
    		response.append("CSeq", packet.valueOfHeader("CSeq"));
        	
    		String payload = packet.getContentString();
    		mAudioSession = AudioSession.parse(payload, mKey);
        	
        } else if (REQ.startsWith("SETUP")){
        	PlaybackControl.getInstance().stopAudioSession();
        	
        	response.append("Audio-Jack-Status", "connected; type=analog");
    		response.append("CSeq", packet.valueOfHeader("CSeq"));
    		
        	
    		try {
	        	int controlPort = 0;
	        	int timingPort = 0;
	        	InetAddress iaddr = null;
	        	
		        	String value = packet.valueOfHeader("Transport");        	
		        	// Control port
		        	Pattern p = Pattern.compile(";control_port=(\\d+)");
		        	if(!TextUtils.isEmpty(value)){
		        		Matcher m = p.matcher(value);
			        	if(m.find()){
			        		controlPort =  Integer.valueOf(m.group(1));
			        	}
			        	// Timing port
			        	p = Pattern.compile(";timing_port=(\\d+)");
			        	
			        	m = p.matcher(value);
			        	if(m.find()){
			        		timingPort =  Integer.valueOf(m.group(1));
			        	}
			        
		        	}
		        	
		        	// Address
		        	p = Pattern.compile("SETUP\\s(rtsp://.*?)\\s");
		        	Matcher m = p.matcher(packet.getRawPacket());
		        	if ( m.find() && mAudioSession != null) {
		        		String uri = m.group(1);
		        		Log.d(TAG, "[SETUP] uri is "+uri +", timingPort is "+timingPort+", controlPort is "+controlPort);
		        		iaddr = InetAddress.getByName(Uri.parse(uri).getHost());
		        		// Creaet Audio Server
		        		mAudioServer = AudioServer.create(mAudioSession, iaddr, controlPort, timingPort);
		        	}
		     
            } catch (SocketException e) {
	            e.printStackTrace();
            } catch (UnknownHostException e) {
	            e.printStackTrace();
            } catch (Exception e) {
	            e.printStackTrace();
            }
    		if (mAudioServer != null) {
	            String transport = String.format(Locale.US, "RTP/AVP/UDP;unicast;mode=record;server_port=%d;control_port=%d;timing_port=%d",
	            		mAudioServer.getServerPort(), mAudioServer.getControlPort(), mAudioServer.getTimingPort());
	            response.append("Transport", transport);
	            
	            response.append("Session", "DEADBEEF");
    		} else {
    			// TODO: response not okay
    		}
    		
    		
    		
    		
    		
    		//{for Mirror SETUP message}
         // parse setup request
        	/*SETUP rtsp://192.168.123.47/6108990559123033009 RTSP/1.0
        		Content-Length: 425
        		Content-Type: application/x-apple-binary-plist
        		CSeq: 5
        		DACP-ID: E28CCF9054EDE3B9
        		Active-Remote: 3016615115
        		User-Agent: AirPlay/320.20

        		bplist00..........
        		..
        		.........RetTname]sourceVersionZtimingPortXdeviceIDUmodelZmacAddress^osBuildVersion[sessionUUIDTekeySeiv. _..GrandStream-6plusV320.20..&_..D8:BB:2C:1F:28:94YiPhone7,1_..D8:BB:2C:1F:28:92U14G60_.$54C77E8B-F4BE-4BB1-A9D7-D246D6672D8BO.HFPLY.......<....?z\.(...K`.....}.....'......W...B...6j..w.{^..d...,...!.O..Ds*..i#6...D.T.!.....".'.5.@.I.O.Z.i.u.z.~...................H...............................[
        	*/
        	
        	/*request string like this
        	 * <plist version="1.0">
            <dict>
            	<key>et</key>
            	<integer>32</integer>
            	<key>name</key>
            	<string>GrandStream-6plus</string>
            	<key>sourceVersion</key>
            	<string>320.20</string>
            	<key>timingPort</key>
            	<integer>57330</integer>
            	<key>deviceID</key>
            	<string>D8:BB:2C:1F:28:94</string>
            	<key>model</key>
            	<string>iPhone7,1</string>
            	<key>macAddress</key>
            	<string>D8:BB:2C:1F:28:92</string>
            	<key>osBuildVersion</key>
            	<string>14G60</string>
            	<key>sessionUUID</key>
            	<string>8708F9F2-CF6F-47B6-9F30-AB8006A531A1</string>
            	<key>ekey</key>
            	<data>
            		RlBMWQECAQAAAAA8AAAAANHJ7v4IfkSnCYtn6odFJ/cAAAAQl1HFACyv3A6IYceOU0T+lX1WsxyxG71s3Sdxwa4k+SJaqs5R
            	</data>
            	<key>eiv</key>
            	<data>
            		i2XW7d2LuYAbCJ2ub4qS/g==
            	</data>
            </dict>
            </plist>*/
        	
        	String contentType = packet.valueOfHeader("Content-Type");
        	Log.d(TAG,"[SETUP], content type is "+contentType);
	        if(contentType.equals("application/x-apple-binary-plist")){
	        	try {
					NSDictionary dict = (NSDictionary) BinaryPropertyListParser.parse(packet.getContent());
					Log.d(TAG,"[SETUP], dict  is "+dict.toXMLPropertyList());
					//process ekey && eiv
					NSData ekey =  (NSData)dict.get("ekey");
					NSData eiv = (NSData) dict.get("eiv");
					mAudioSession = AudioSession.parse(ekey.toString(), eiv.toString(), mKey);
					
					NSNumber timingportObj = (NSNumber)dict.get("timingPort");
					Log.d(TAG,"[SETUP], timingportObj  is "+timingportObj.toString());
						if(timingportObj != null){
						int  timingPort =timingportObj.intValue();
						
						//timingPort = Integer.valueOf(timingPortStr);
						//response should like below
						/*RTSP/1.0 200 OK
						Date: Thu, 19 Oct 2017 02:16:37 GMT
						CSeq: 5
						Server: AirTunes/220.68
						Content-Length: 284
	
						<?xml version="1.0" encoding="UTF-8"?>
						<!DOCTYPE plist PUBLIC "-//Apple//DTD PLIST 1.0//EN" "http://www.apple.com/DTDs/PropertyList-1.0.dtd">
						<plist version="1.0">
						<dict>
						.<key>eventPort</key>
						.<integer>12854</integer>
						.<key>timingPort</key>
						.<integer>22897</integer>
						</dict>
						</plist>*/
							if(mAudioServer != null ){
								NSDictionary reponsedict = new NSDictionary();
								Log.d(TAG,"local event Port is "+mAudioServer.getControlPort()+
										",  time port is "+mAudioServer.getTimingPort());
					        	reponsedict.put("eventPort",  mAudioServer.getControlPort());
					        	reponsedict.put("timingPort", mAudioServer.getTimingPort());
					        	Log.d(TAG,"[SETUP]response content is "+reponsedict.toXMLPropertyList());
								byte[] contents = BinaryPropertyListWriter.writeToArray(reponsedict);
								response.append("Content-Length", contents.length+"");
					        	response.setContent(contents, 0, contents.length);
							}
						}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (PropertyListFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
	        }
    	
        } else if (REQ.contentEquals("RECORD")){
        	/*session like below
        	 * RECORD rtsp://192.168.123.47/6108990559123033009 RTSP/1.0
        		CSeq: 8
        		DACP-ID: E28CCF9054EDE3B9
        		Active-Remote: 3016615115
        		User-Agent: AirPlay/320.20

        		RTSP/1.0 200 OK
        		Date: Thu, 19 Oct 2017 02:16:37 GMT
        		CSeq: 8
        		Session: DEADBEEF
        		Audio-Jack-Status: connected; type=digital
        		Server: AirTunes/220.68
        		Content-Length: 0*/
        	response.append("Session", "DEADBEEF");
        	response.append("Audio-Jack-Status", "connected; type=digital");
    		response.append("CSeq", packet.valueOfHeader("CSeq"));
    		response.append("Content-Length", "0");
        	
    		if (mAudioServer != null) {
    			PlaybackControl.getInstance().startAudioSession(this);
    			
	        	/* Headers
	        	 * Range: ntp=0-
	        	 * RTP-Info: seq={Note 1};rtptime={Note 2}
	        	 * Note 1: Initial value for the RTP Sequence Number, random 16 bit value
	        	 * Note 2: Initial value for the RTP Timestamps, random 32 bit value
	        	 */
    			String value = packet.getRawPacket();
    			
    			// Control port
    			Pattern p = Pattern.compile(";rtptime=(\\d+)");
    			Matcher m = p.matcher(value);
    			long timestamp = 0;
    			if(m.find()){
    				timestamp =  Long.parseLong(m.group(1));
    			}
    			mAudioServer.start(timestamp);
    		}

        } else if (REQ.contentEquals("FLUSH")){
        	response.append("Audio-Jack-Status", "connected; type=analog");
    		response.append("CSeq", packet.valueOfHeader("CSeq"));
        	
        	mAudioServer.flush();
        
        } else if (REQ.contentEquals("TEARDOWN")){
        	response.append("Audio-Jack-Status", "connected; type=analog");
    		response.append("CSeq", packet.valueOfHeader("CSeq"));
        	
        	if (mAudioServer != null) {
        		mAudioServer.close();
        		mAudioServer = null;
        	}
        	if (mAudioSession != null) {
        		mAudioSession.close();
        		mAudioSession = null;
        	}
        	response.append("Connection", "close");
        	
        } else if (REQ.contentEquals("SET_PARAMETER")){
        	//response.append("Audio-Jack-Status", "connected; type=analog");
    		response.append("CSeq", packet.valueOfHeader("CSeq"));
        	
        	// Timing port
        	String contentOfType = packet.valueOfHeader("Content-Type");
        	if ("text/parameters".equals(contentOfType)) {
        		String content = packet.getContentString();
        		// Volume control
        		Pattern p = Pattern.compile("volume:\\s?(.+)");
        		Matcher m = p.matcher(content);
        		if(m.find()){
        			double volume = Double.parseDouble(m.group(1));
        			mAudioServer.setVolume(volume);
        		} else {
        			p = Pattern.compile("progress:\\s?([0-9]+)/([0-9]+)/([0-9]+)");
        			m = p.matcher(content);
        			if (m.find()) {
        				long rtpTime = 0;
        				long start = Long.parseLong(m.group(1));
        				long curr = Long.parseLong(m.group(2));
        				long end = Long.parseLong(m.group(3));
        				PlaybackControl.getInstance().updateProgress(0, start, curr, end);
        			}
        		}
        		
        	// Cover Art
        	} else if ("image/jpeg".equals(contentOfType)) {
        		long rtpTime = 0;
        		PlaybackControl.getInstance().updateCoverArt(rtpTime, packet.getContent(), 0, packet.getContentLength());
        		
        	// Track info
        	} else if ("application/x-dmap-tagged".equals(contentOfType)) {
        		long rtpTime = 0;
        		String str = packet.getContentString();
        		PlaybackControl.getInstance().updateTrackInfo(rtpTime, packet.getContent(), 0, packet.getContentLength());
        	}
        	
        } else if (REQ.contains("POST")) {
        	String head = packet.getRawPacket();
        	if (head.contains("/fp-setup")) {
        		byte[] content = packet.getContent();
        		int seqno = content[6];
        		String cseq = packet.valueOfHeader("CSeq");
        		Log.d("ShairPort", "header cq is "+cseq);
        		//Log.d("ShairPort","contenter seqno is "+seqno);
        		if (cseq.equals("0")) {
        			response.append("Content-Type", "application/octet-stream");
        			response.append("X-Apple-ET", "32");
        			response.append("Content-Length", "142");
        			response.finalizeHeader();
        			
        			//payload
        			response.setContent(FairPlay.PACKET2, 0, FairPlay.PACKET2.length);
        			
        		} else if (cseq.equals("1")) {
        			response.append("Content-Type", "application/octet-stream");
        			response.append("X-Apple-ET", "32");
        			response.append("Content-Length", "32");
        			response.finalizeHeader();
        			
        			//payload
        			byte[] payload = Arrays.copyOf(FairPlay.PACKET4, FairPlay.PACKET4.length);
        			System.arraycopy(content, content.length - 20, payload, payload.length - 20, 20);
        			response.setContent(payload, 0, payload.length);
        		}
        		/*RTSP/1.0 200 OK
        		Date: Thu, 19 Oct 2017 02:16:35 GMT
        		CSeq: 0
        		Content-Type: application/octet-stream
        		Server: AirTunes/220.68
        		Content-Length: 32

        		8.5...p.*.....A.%.Yn.....w
        		...$.*/
        	}else if(head.contains("pair-setup")){
        		byte[] content = packet.getContent();
        		int seqno = content[6];
        		response.append("CSeq", packet.valueOfHeader("CSeq"));
        		response.append("Content-Type", "application/octet-stream");
        		response.append("Content-Length", "32");
        		response.finalizeHeader();
        		
        		//payload, 根据client发过来的32位长度的字串根据算法转化成32位发给client
        		//return pk base 64
        		byte[] encodePk = Base64.encode(AirTunes.PKSTR.getBytes(), Base64.DEFAULT);
        		response.setContent(encodePk, 0, encodePk.length);
        		
        	}else if(head.contains("pair-verify")){
        		byte[] content = packet.getContent();
        		int seqno = content[6];
        		/*client->server
        		 * POST /pair-verify RTSP/1.0
        		X-Apple-PD: 1
        		X-Apple-AbsoluteTime: 530072180
        		Content-Length: 68
        		Content-Type: application/octet-stream
        		CSeq: 1
        		DACP-ID: E28CCF9054EDE3B9
        		Active-Remote: 3016615115
        		User-Agent: AirPlay/320.20

        		..............k......)oWL..t.%s..(.b1t<..*....i1"m,.. g.|h.Y...t..M.
        		server-client
        		
        		RTSP/1.0 200 OK
        		Date: Thu, 19 Oct 2017 02:16:36 GMT
        		CSeq: 1
        		Content-Type: application/octet-stream
        		Server: AirTunes/220.68
        		Content-Length: 96

        		.^{ ...O. j..Xa+..y....+.^.!...p...nF.!.g*.W..... .U...L...b[bz.tf.........sm..".....,A.=....g..
*/        		if (seqno == 1) {
	response.append("CSeq", packet.valueOfHeader("CSeq"));
	response.append("Content-Type", "application/octet-stream");
	response.append("Content-Length", "96");
	response.finalizeHeader();
	
	//payload,根据请求数据返回的结果
	
	
	/*client->server
	 * POST /pair-verify RTSP/1.0
	X-Apple-PD: 1
	X-Apple-AbsoluteTime: 530072180
	Content-Length: 68
	Content-Type: application/octet-stream
	CSeq: 2
	DACP-ID: E28CCF9054EDE3B9
	Active-Remote: 3016615115
	User-Agent: AirPlay/320.20

	......H.u6.Fq`.(V?..+6.4.7...^..&.....K.L.............T1Wv7I.*ke.m..
	
	server->client
	RTSP/1.0 200 OK
	Date: Thu, 19 Oct 2017 02:16:36 GMT
	CSeq: 2
	Content-Type: application/octet-stream
	Server: AirTunes/220.68
	Content-Length: 0*/
        		}else if(seqno==2){
        			response.append("CSeq", packet.valueOfHeader("CSeq"));
        			response.append("Content-Type", "application/octet-stream");
        			response.append("Content-Length", "96");
        			response.finalizeHeader();
        			
        			//payload, 没有数据返回
        		}
        	}
        	
        } else if(REQ.contentEquals("GET")){
        	/*request like this
        	 * GET /info RTSP/1.0
        	X-Apple-ProtocolVersion: 0
        	CSeq: 6
        	DACP-ID: E28CCF9054EDE3B9
        	Active-Remote: 3016615115
        	User-Agent: AirPlay/320.20*/
        	
        	String directry = packet.getDirectory();
        	Log.d(TAG,"directry is "+directry);
        	/*response like this
        	 * RTSP/1.0 200 OK
        	Date: Thu, 19 Oct 2017 02:16:37 GMT
        	Session: DEADBEEF
        	CSeq: 6
        	Audio-Jack-Status: connected; type=digital
        	Server: AirTunes/220.68
        	Content-Length: 2230

        	<?xml version="1.0" encoding="UTF-8"?>
        	<!DOCTYPE plist PUBLIC "-//Apple//DTD PLIST 1.0//EN" "http://www.apple.com/DTDs/PropertyList-1.0.dtd">
        	<plist version="1.0">
        	<dict>
        	.<key>audioFormats</key>
        	.<array>
        	..<dict>
        	...<key>audioInputFormats</key>
        	...<integer>67108860</integer>
        	...<key>audioOutputFormats</key>
        	...<integer>67108860</integer>
        	...<key>type</key>
        	...<integer>100</integer>
        	..</dict>*/
        	if(directry.equals("info")){
        		Log.d(TAG," [GET], directry is "+directry);
        	}
        	response.append("Session", "DEADBEEF");
        	response.append("CSeq", packet.valueOfHeader("CSeq"));
        	response.append("Audio-Jack-Status","connected; type=digital");
        	
        	NSDictionary responseDic = new NSDictionary();
        	
        	//audioFormats
        	NSDictionary audioFormat1 = new NSDictionary();
        	audioFormat1.put("audioInputFormats", 67108860);
        	audioFormat1.put("audioOutputFormats", 67108860);
        	audioFormat1.put("type", 100);
        	NSDictionary audioFormat2 = new NSDictionary();
        	audioFormat2.put("audioInputFormats", 67108860);
        	audioFormat2.put("audioOutputFormats", 67108860);
        	audioFormat2.put("type", 101);
        	responseDic.put("audioFormats", NSArray.wrap(new NSObject[]{audioFormat1,audioFormat2}));
        	
        	//audioLatencies
        	NSDictionary audioLatencies1 = new NSDictionary();
        	audioLatencies1.put("audioType", "default");
        	audioLatencies1.put("inputLatencyMicros", false);
        	audioLatencies1.put("type", 100);
        	NSDictionary audioLatencies2 = new NSDictionary();
        	audioLatencies2.put("audioType", "default");
        	audioLatencies2.put("inputLatencyMicros", false);
        	audioLatencies2.put("type", 101);
        	responseDic.put("audioLatencies", NSArray.wrap(new NSObject[]{audioLatencies1,audioLatencies2}));
        	
        	//deviceID
        	responseDic.put("deviceID", "000B828B571E");//ugle
        	
        	//displays
        	NSDictionary displayFeature = new NSDictionary();
        	displayFeature.put("features", 14);
        	displayFeature.put("height", 720);
        	displayFeature.put("heightPhysical", false);
        	displayFeature.put("heightPixels", 720);
        	displayFeature.put("overscanned", false);
        	displayFeature.put("refreshRate", 60);
        	displayFeature.put("rotation", true);
        	displayFeature.put("uuid", "e5f7a68d-7b0f-4305-984b-974f677a150b");
        	displayFeature.put("width", 1280);
        	displayFeature.put("widthPhysical", false);
        	displayFeature.put("widthPixels", 1280);
        	responseDic.put("displays", NSArray.wrap(new NSObject[]{displayFeature}));
        	
        	//features
        	NSDictionary feature = new NSDictionary();
        	feature.put("features", AirPlay.FEATURES);
        	
        	//keepAliveLowPower
        	NSDictionary keepAliveLowPower = new NSDictionary();
        	keepAliveLowPower.put("keepAliveLowPower", 1);
        	
        	//keepAliveLowPower
        	NSDictionary keepAliveSendStatsAsBody = new NSDictionary();
        	keepAliveSendStatsAsBody.put("keepAliveSendStatsAsBody", 1);
        	
        	//macAddress
        	NSDictionary macAddress = new NSDictionary();
        	macAddress.put("macAddress", "000B828B571E");
        	
        	
        	//model
        	NSDictionary model = new NSDictionary();
        	model.put("model", "AppleTV3,2");
        	
        	//name
        	NSDictionary name = new NSDictionary();
        	name.put("name", "Apple TV");
        	
        	//pi
        	NSDictionary pi = new NSDictionary();
        	pi.put("pi", "b08f5a79-db29-4384-b456-a4784d9e6055");
        	
        	//pk
        	NSDictionary pk = new NSDictionary();
        	pk.put("pk", "373d07e5fc40608f0533fc21daac96e97b6dc59d71e87163e3d8cec6700dce29");
        	
        	//sourceVersion
        	NSDictionary sourceVersion = new NSDictionary();
        	sourceVersion.put("sourceVersion", AirPlay.SRCVERS);
        	
        	//statusFlags
        	NSDictionary statusFlags = new NSDictionary();
        	statusFlags.put("statusFlags", 68);
        	
        	//statusFlags
        	NSDictionary vv = new NSDictionary();
        	vv.put("vv", 2);
        	
        	Log.d(TAG, "[GET/info], response string  is "+responseDic.toXMLPropertyList());
        	byte[] contents = responseDic.toXMLPropertyList().getBytes();
        	response.setContent(contents, 0, contents.length);
        } else if(REQ.contentEquals("GET_PARAMETER")){
        String requestStr =	packet.getContentString();
        if(requestStr.contains("volume")){
        	/*RTSP/1.0 200 OK
        	Date: Thu, 19 Oct 2017 02:16:37 GMT
        	CSeq: 7
        	Audio-Jack-Status: connected; type=digital
        	Server: AirTunes/220.68
        	volume: 1.000000
        	Content-Length: 18

        	volume: 1.000000*/
        	response.append("CSeq", packet.valueOfHeader("CSeq"));
        	response.append("Audio-Jack-Status", "connected; type=digital");
        	response.append("volume", "1.000000");
        	byte[] respContent = "volume: 1.000000".getBytes();
        	response.setContent(respContent, 0, respContent.length);
        }
        	
        } else {
        	Log.d("ShairPort", "REQUEST(" + REQ + "): Not Supported Yet!");
        	Log.d("ShairPort", packet.getRawPacket());
        }
        
    	// We close the response
    	response.ensureHeaderFinialized();
    	return response;
	}
	
	private static final byte[] FP_REQ = {
	        0x46, 0x50, 0x4C, 0x59, 0x03, 0x01, 0x01, 0x00, 0x00, 0x00, 0x00, 0x04, 0x02, 0x00,
	        0x01, (byte) (0xBB & 0xFF)
	};
	
	private static final byte[] FP_RES = {
	        0x46, 0x50, 0x4C, 0x59, 0x03, 0x01, 0x02, 0x00, 0x00, 0x00, 0x00, (byte) (0x82 & 0xFF),
	        0x02, 0x01
	        
	};
	
	/**
	 * Crypts with private key
	 * @param array	data to encrypt
	 * @return encrypted data
	 */
	public byte[] encryptRSA(byte[] array){
		try{

	        // Encrypt
	        Cipher cipher = Cipher.getInstance("RSA/NONE/PKCS1Padding"); 
	        cipher.init(Cipher.ENCRYPT_MODE, mKey);
	        return cipher.doFinal(array);

		}catch(Exception e){
			e.printStackTrace();
		}

		return null;
	}

    /**
     * Thread to listen packets
     */
	public void run() {
		try {
//			char[] buffer = new char[4096];
			ByteBuffer buffer = ByteBuffer.allocate(4096);
			do {
//				StringBuffer packet = new StringBuffer();
				Log.d("ShairPort", "listening packets ... ");
//				// feed buffer until packet completed
//				
//				int ret = 0;
//				do {
//					ret = in.read(buffer);
//					packet.append(new String(buffer));
//				} while (ret!=-1 && !completedPacket.matcher(packet.toString()).find());
				buffer.rewind();
				int ret = readSegment(buffer, mInput, CRLFCRLF);
				
				
				if (ret!=-1) {
					// We handle the packet
					String packet = new String(buffer.array(), 0, ret);
					RTSPPacket request = new RTSPPacket(packet.toString());
					request.readContent(mInput);
					
					RTSPResponse response = this.handlePacket(request);		
					Log.d("ShairPort", request.toString());	
					Log.d("ShairPort", response.toString());
		
			    	// Write the response to the wire
			    	try {			
//			    		BufferedWriter oStream = new BufferedWriter(new OutputStreamWriter());
//			    		oStream.write(response.getRawPacket());
//			    		oStream.flush();
			    		OutputStream os = socket.getOutputStream();
			    		os.write(response.getRawPacket());
			    		os.flush();
					} catch (IOException e) {
						e.printStackTrace();
					}
					
		    		if("TEARDOWN".equals(request.getReq())){
		    			socket.close();
		    			socket = null;
		    		}
				} else {
	    			socket.close();
	    			socket = null;
				}
			} while (socket!=null);
			
		} catch (IOException e) {
			e.printStackTrace();
			
		} finally {
			try {
				if (mInput != null) mInput.close();
			} catch (IOException e) {
			}
			try {
				if (socket != null) socket.close();
			} catch (IOException e) {
			}
			if (mAudioServer != null) {
				mAudioServer.close();
			}
		}
		Log.d("ShairPort", "connection ended.");
	}
	
	public void close() {
		if (mAudioServer != null) {
			mAudioServer.close();
		}
		if (socket != null) {
			try {
				socket.close();
			} catch (IOException e) {
			}
		}
    }
	
	private static int readSegment(ByteBuffer dst, InputStream is, byte[] endToken) throws IOException {
		int read = 0;
		int idx = 0;
		while(true) {
			int r = is.read();
			if (r < 0) {
				return r;
			}
			read++;
			byte b = (byte) (r & 0xFF);
			dst.put(b);
			if (b == endToken[idx]) {
				idx++;
			} else {
				idx = 0;
			}
			if (idx == endToken.length) {
				break;
			}
		}
		return read;
	}

}