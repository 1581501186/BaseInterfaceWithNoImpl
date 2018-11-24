package com.fuping.aio;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.Future;

public class AIOClient {
	private AsynchronousSocketChannel client = null;

	public AIOClient(String addr, int port) throws Exception {
		client = AsynchronousSocketChannel.open();
		Future<Void> connect = client.connect(new InetSocketAddress(addr, port));
		System.out.println(connect.get());
	}
	
	public void write(byte b) {
		ByteBuffer buffer = ByteBuffer.allocate(32);
		buffer.put(b);
		buffer.flip();
		client.write(buffer);
	}
	
	public static void main(String[] args) throws Exception {
		//模式两次链接
		for (int i = 0; i < 2; i++) {
			AIOClient ac = new AIOClient("127.0.0.1", 8888);
			System.out.println(i);
			ac.write((byte)29);
		}
		
		
	}
	
}
