package com.fuping.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
/**
   *  客户端代码
 * @author thefirstcomputer
 *
 */
public class NIOClient {
	private static int flag = 0;
	private static int capacity = 4096;
	private static ByteBuffer sendBuffer = ByteBuffer.allocate(capacity);
	private static ByteBuffer receiveBuffer = ByteBuffer.allocate(capacity);
	//因为是本机，所以地址是本地的
	private static final InetSocketAddress address = new InetSocketAddress("127.0.0.1", 8888);
	public static void main(String[] args) throws IOException {
		//和服务端一样的方式，只是这里是socketchannel
		SocketChannel socketChannel = SocketChannel.open();
		socketChannel.configureBlocking(false);
		Selector selector = Selector.open();
		//客户端负责连接
		socketChannel.register(selector, SelectionKey.OP_CONNECT);
		socketChannel.connect(address);
		//客户端也需要轮训注册的事件是否完成
		while(true) {
			selector.select();
			Set<SelectionKey> selectedKeys = selector.selectedKeys();
			System.out.println(selectedKeys.size());
			Iterator<SelectionKey> iteratorKeys = selectedKeys.iterator();
			while(iteratorKeys.hasNext()) {
				SelectionKey key = iteratorKeys.next();
				SocketChannel client = (SocketChannel) key.channel();
				if (key.isConnectable()) {
					System.out.println("client connect");
					//没有太多说的，方法名字写的很好了
					if (client.isConnectionPending()) {
						client.finishConnect();
						System.out.println("connect finish");
						sendBuffer.clear();
						sendBuffer.put("connect server".getBytes());
						sendBuffer.flip();
						client.write(sendBuffer);
						//连接之后就需要读取服务端返回的内容了
						client.register(selector, SelectionKey.OP_READ);
					}
					
				} else if (key.isReadable()) {
					receiveBuffer.clear();
					int count = client.read(receiveBuffer);
					if (count > 0) {
						System.out.printf("client receive message is ：%s",new String(receiveBuffer.array(),0,count));
						System.out.println();
						//这里如果注册了，后续的key的写完成的if也要加上，这样就可以无限读写了
//						client.register(selector, SelectionKey.OP_WRITE);
					}
					
				}  
//				else if (key.isWritable()) {
//					//先清空缓存
//					sendBuffer.clear();
//					String sendText = "hello server " + flag ++ ;
//					sendBuffer.put(sendText.getBytes());
//					//指针回位
//					sendBuffer.flip();
//					client.write(sendBuffer);
//					System.out.printf("client send message is ：%s",sendText);
//					System.out.println();
//					client.register(selector, SelectionKey.OP_READ);
//				}
			}
			//每次完成记得清理，以免重复进行操作
			selectedKeys.clear();	
		}
	}
}
