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
   *  �ͻ��˴���
 * @author thefirstcomputer
 *
 */
public class NIOClient {
	private static int flag = 0;
	private static int capacity = 4096;
	private static ByteBuffer sendBuffer = ByteBuffer.allocate(capacity);
	private static ByteBuffer receiveBuffer = ByteBuffer.allocate(capacity);
	//��Ϊ�Ǳ��������Ե�ַ�Ǳ��ص�
	private static final InetSocketAddress address = new InetSocketAddress("127.0.0.1", 8888);
	public static void main(String[] args) throws IOException {
		//�ͷ����һ���ķ�ʽ��ֻ��������socketchannel
		SocketChannel socketChannel = SocketChannel.open();
		socketChannel.configureBlocking(false);
		Selector selector = Selector.open();
		//�ͻ��˸�������
		socketChannel.register(selector, SelectionKey.OP_CONNECT);
		socketChannel.connect(address);
		//�ͻ���Ҳ��Ҫ��ѵע����¼��Ƿ����
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
					//û��̫��˵�ģ���������д�ĺܺ���
					if (client.isConnectionPending()) {
						client.finishConnect();
						System.out.println("connect finish");
						sendBuffer.clear();
						sendBuffer.put("connect server".getBytes());
						sendBuffer.flip();
						client.write(sendBuffer);
						//����֮�����Ҫ��ȡ����˷��ص�������
						client.register(selector, SelectionKey.OP_READ);
					}
					
				} else if (key.isReadable()) {
					receiveBuffer.clear();
					int count = client.read(receiveBuffer);
					if (count > 0) {
						System.out.printf("client receive message is ��%s",new String(receiveBuffer.array(),0,count));
						System.out.println();
						//�������ע���ˣ�������key��д��ɵ�ifҲҪ���ϣ������Ϳ������޶�д��
//						client.register(selector, SelectionKey.OP_WRITE);
					}
					
				}  
//				else if (key.isWritable()) {
//					//����ջ���
//					sendBuffer.clear();
//					String sendText = "hello server " + flag ++ ;
//					sendBuffer.put(sendText.getBytes());
//					//ָ���λ
//					sendBuffer.flip();
//					client.write(sendBuffer);
//					System.out.printf("client send message is ��%s",sendText);
//					System.out.println();
//					client.register(selector, SelectionKey.OP_READ);
//				}
			}
			//ÿ����ɼǵ����������ظ����в���
			selectedKeys.clear();	
		}
	}
}
