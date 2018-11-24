package com.fuping.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
/**
 * NIO服务端代码
 * @author thefirstcomputer
 *
 */
public class NIOServer {
	//发送的消息计数
	private int flag = 0;
	//缓存容量
	private int capacity = 4096;
	//数据发送缓存
	private ByteBuffer sendBuffer = ByteBuffer.allocate(capacity);
	//数据接收缓存
	private ByteBuffer receiveBuffer = ByteBuffer.allocate(capacity);
	//多路复用选择器
	private Selector selector;
	
	public NIOServer(int port) throws IOException {
//		ServerSocketChannel.open().socket().bind(new InetSocketAddress(port));
		//服务端通道
		ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
		//设置成非阻塞，NIO也可以设置为阻塞的
		serverSocketChannel.configureBlocking(false);
		//这里就可以看做是平常BIO的socket编程的开始了
		ServerSocket socket = serverSocketChannel.socket();
		//监听端口
		socket.bind(new InetSocketAddress(port));
		//拿到多路复用选择器
		selector = Selector.open();
		//注册接收的key，后续会调用accept方法，所以这个注册信息可以理解成就是用户提前告知下一步调用的方法
		//这里注册的也可以是多个，使用 | 也求并集  原理就是几个int，通过位运算记录信息
		serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
		//这两行其实一行可以，我是闲得无聊
		System.out.printf("server start -> %d",port);
		System.out.println();

	}
	//循环获取选择器用的注册的key，通过程序进行下一步的操作
	public void listen() throws IOException {
		while(true) {
			//拿到所以的key
			selector.select();
			//后面就是循环遍历注册的信息了
			Set<SelectionKey> selectedKeys = selector.selectedKeys();
			System.out.println(selectedKeys);
			Iterator<SelectionKey> iterator = selectedKeys.iterator();
			while(iterator.hasNext()) {
				SelectionKey key = iterator.next();
				//必须要remove，不然会导致重复操作
				iterator.remove();
				//开始处理
				handle(key);
			}
			
		}		
	}

	private void handle(SelectionKey key) throws IOException {
		//每次拿到key都要进行判断是否可以进行，也就是看看这个动作是否完成。
		if (key.isAcceptable()) {
			//只用accept是这个channel，其他的都是不加server的
			ServerSocketChannel server = (ServerSocketChannel) key.channel();
			//是不是很熟悉
			SocketChannel client = server.accept();
			//同样这里也必须设置为未阻塞的
			client.configureBlocking(false);
			client.register(selector, SelectionKey.OP_READ);	
		} else if (key.isReadable()) {
			SocketChannel client = (SocketChannel) key.channel();
			//缓存清理
			receiveBuffer.clear();
			int count = client.read(receiveBuffer);
			//缓存读取从头开始
			receiveBuffer.flip();
			if(count > 0){
				System.out.printf("receive from client: %s",new String(receiveBuffer.array(),0,count));
				System.out.println();
				//读完之后就需要给客户端会写消息了
				client.register(selector, SelectionKey.OP_WRITE);
			}
			
		} else if (key.isWritable()) {
			//这些清理操作必须有，不然后续的buffer填充一定会出问题，专治各种不服
			sendBuffer.clear();
			SocketChannel client = (SocketChannel) key.channel();
			String sendText = "hello client" + flag ++;
			sendBuffer.put(sendText.getBytes());
			//同样，这个方法有例子，写的很好，就是使用out回写的时候，从头开始回写
			sendBuffer.flip();
			client.write(sendBuffer);
			System.out.println("send to client is :" + sendText);
			client.register(selector, SelectionKey.OP_READ);	
		}
		
	}
	
	public static void main(String[] args) throws IOException {
		NIOServer server = new NIOServer(8888);
		server.listen();
	}
	
	
	
	
}
