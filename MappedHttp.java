/*利用通道读写文件

*/

package com.tom.ecity_clawer;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;

/**
 * @author lWX193785
 *
 */
public class MappedHttp {

	private static final String OUT_FILE = "E:\\file.out";

	private static final String LINE_SEQ = "\r\n";

	private static final String SERVER_ID = "Server :Ronsoft Dummy Server";

	private static final String HTTP_HDR = "HTTP/1.0 200 OK" + LINE_SEQ
			+ SERVER_ID + LINE_SEQ;

	private static final String HTTP_404_HDR = "HTTP/1.0 404 Not Found"
			+ LINE_SEQ + SERVER_ID + LINE_SEQ;

	private static final String MSG_404 = "Couldn't not open file";

	public static void main(String[] args) throws IOException {
		args = new String[1];
		args[0] = "e:\\param.xml";
		if (args.length < 1) {
			System.err.println("Usage:filename");
			return;
		}
		String file = args[0];
		ByteBuffer header = ByteBuffer.wrap(bytes(HTTP_HDR));
		ByteBuffer dyhdrs = ByteBuffer.allocate(128);
		ByteBuffer[] gather = { header, dyhdrs, null };

		String contentType = "unknow/unknown";

		long contentLenth = -1;
		
		try{
			FileInputStream fis = new FileInputStream(file);
			FileChannel fc = fis.getChannel();
			MappedByteBuffer filedata = fc.map(MapMode.READ_ONLY, 0, fc.size());
			gather[2] = filedata;
			
			contentLenth = fc.size();
			
			contentType = URLConnection.guessContentTypeFromName(file);
		}catch(Exception e){
			ByteBuffer buf = ByteBuffer.allocate(128);
			String msg = MSG_404+e+LINE_SEQ;
			buf.put(bytes(msg));
			buf.flip();
			gather[0] = ByteBuffer.wrap(bytes(HTTP_404_HDR));
			gather[2] = buf;
			contentLenth = msg.length();
			contentType ="text/plain";
		}
		StringBuffer sb = new StringBuffer();
		sb.append("Content-Length:"+contentLenth);
		sb.append(LINE_SEQ);
		sb.append("Content-Type:"+contentType);
		sb.append(LINE_SEQ).append(LINE_SEQ);
		dyhdrs.put(bytes(sb.toString()));
		dyhdrs.put(bytes(sb.toString()));
		dyhdrs.flip();
		FileOutputStream fos = new FileOutputStream(OUT_FILE);
		FileChannel out  = fos.getChannel();
		while(out.write(gather)>0){
			
		}
		out.close();
		System.out.println("output written to"+OUT_FILE);
	}

	private static byte[] bytes(String httpHdr) throws UnsupportedEncodingException {
		// FIXME
		return (httpHdr.getBytes("US-ASCII"));

	}

}
