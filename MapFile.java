/**
* 三种读取方式
* RO
* RW
* COW
*/

package com.tom.ecity_clawer;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class MapFile {

	public static void main(String[] args) throws IOException {
		File tempFile = File.createTempFile("mmaptest", null);
		RandomAccessFile rf = new RandomAccessFile(tempFile, "rw");

		FileChannel channel = rf.getChannel();
		ByteBuffer temp = ByteBuffer.allocate(100);
		temp.put("this is the file content".getBytes());

		temp.flip();

		channel.write(temp, 8192);

		temp.clear();

		temp.put("this is more file content".getBytes());

		temp.flip();

		channel.write(temp, 0);

		MappedByteBuffer ro = channel.map(FileChannel.MapMode.READ_ONLY, 0,
				channel.size());

		MappedByteBuffer rw = channel.map(FileChannel.MapMode.READ_WRITE, 0,
				channel.size());

		MappedByteBuffer cow = channel.map(FileChannel.MapMode.PRIVATE, 0,
				channel.size());

		System.out.println("begin");

		showBuffers(ro, rw, cow);

		cow.position(8);

		cow.put("COW".getBytes());

		System.out.println("Change to COW buffer");

		showBuffers(ro, rw, cow);

		rw.position(9);

		rw.put("R/W".getBytes());

		System.out.println("limit:"+rw.limit());;
		rw.position(8194);

		rw.put("R/W".getBytes());

		rw.force();

		System.out.println("Change to R/W buffer");

		showBuffers(ro, rw, cow);

		temp.clear();

		temp.put("channel write".getBytes());

		temp.flip();

		channel.write(temp, 0);

		temp.rewind();

		channel.write(temp, 8202);

		System.out.println("Write on channel");

		showBuffers(ro, rw, cow);

		cow.position(8207);

		cow.put("Cow".getBytes());

		System.out.println("Second change to COW buffer");

		showBuffers(ro, rw, cow);

		rw.position(0);

		rw.put("rw".getBytes());

		rw.position(8210);
		rw.put("rw".getBytes());
		rw.force();

		System.out.println("Second change to R/W buffer");

		showBuffers(ro, rw, cow);
		channel.close();
		rf.close();
		tempFile.delete();

	}

	private static void showBuffers(MappedByteBuffer ro, MappedByteBuffer rw,
			MappedByteBuffer cow) {
		dumpBuffer("R/O",ro);
		dumpBuffer("R/W",rw);
		dumpBuffer("COW",cow);

	}

	private static void dumpBuffer(String prefix, MappedByteBuffer buffer) {
		System.out.println(prefix+":'");
		int nulls = 0;
		int limits = buffer.limit();
		
		for(int i=0;i<limits;i++){
			char c = (char) buffer.get(i);
			if(c =='\u0000'){
				nulls++;
				continue;
			}
			if(nulls !=0){
				System.out.println("|["+nulls+"nulls]|");
				nulls =0;
			}
			System.out.print(c);
			
		}
		System.out.println("'");
		
		
	}

}
