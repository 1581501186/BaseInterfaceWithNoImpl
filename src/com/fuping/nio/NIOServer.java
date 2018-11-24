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
 * NIO����˴���
 * @author thefirstcomputer
 *
 */
public class NIOServer {
	//���͵���Ϣ����
	private int flag = 0;
	//��������
	private int capacity = 4096;
	//���ݷ��ͻ���
	private ByteBuffer sendBuffer = ByteBuffer.allocate(capacity);
	//���ݽ��ջ���
	private ByteBuffer receiveBuffer = ByteBuffer.allocate(capacity);
	//��·����ѡ����
	private Selector selector;
	
	public NIOServer(int port) throws IOException {
//		ServerSocketChannel.open().socket().bind(new InetSocketAddress(port));
		//�����ͨ��
		ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
		//���óɷ�������NIOҲ��������Ϊ������
		serverSocketChannel.configureBlocking(false);
		//����Ϳ��Կ�����ƽ��BIO��socket��̵Ŀ�ʼ��
		ServerSocket socket = serverSocketChannel.socket();
		//�����˿�
		socket.bind(new InetSocketAddress(port));
		//�õ���·����ѡ����
		selector = Selector.open();
		//ע����յ�key�����������accept�������������ע����Ϣ�������ɾ����û���ǰ��֪��һ�����õķ���
		//����ע���Ҳ�����Ƕ����ʹ�� | Ҳ�󲢼�  ԭ����Ǽ���int��ͨ��λ�����¼��Ϣ
		serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
		//��������ʵһ�п��ԣ������е�����
		System.out.printf("server start -> %d",port);
		System.out.println();

	}
	//ѭ����ȡѡ�����õ�ע���key��ͨ�����������һ���Ĳ���
	public void listen() throws IOException {
		while(true) {
			//�õ����Ե�key
			selector.select();
			//�������ѭ������ע�����Ϣ��
			Set<SelectionKey> selectedKeys = selector.selectedKeys();
			System.out.println(selectedKeys);
			Iterator<SelectionKey> iterator = selectedKeys.iterator();
			while(iterator.hasNext()) {
				SelectionKey key = iterator.next();
				//����Ҫremove����Ȼ�ᵼ���ظ�����
				iterator.remove();
				//��ʼ����
				handle(key);
			}
			
		}		
	}

	private void handle(SelectionKey key) throws IOException {
		//ÿ���õ�key��Ҫ�����ж��Ƿ���Խ��У�Ҳ���ǿ�����������Ƿ���ɡ�
		if (key.isAcceptable()) {
			//ֻ��accept�����channel�������Ķ��ǲ���server��
			ServerSocketChannel server = (ServerSocketChannel) key.channel();
			//�ǲ��Ǻ���Ϥ
			SocketChannel client = server.accept();
			//ͬ������Ҳ��������Ϊδ������
			client.configureBlocking(false);
			client.register(selector, SelectionKey.OP_READ);	
		} else if (key.isReadable()) {
			SocketChannel client = (SocketChannel) key.channel();
			//��������
			receiveBuffer.clear();
			int count = client.read(receiveBuffer);
			//�����ȡ��ͷ��ʼ
			receiveBuffer.flip();
			if(count > 0){
				System.out.printf("receive from client: %s",new String(receiveBuffer.array(),0,count));
				System.out.println();
				//����֮�����Ҫ���ͻ��˻�д��Ϣ��
				client.register(selector, SelectionKey.OP_WRITE);
			}
			
		} else if (key.isWritable()) {
			//��Щ������������У���Ȼ������buffer���һ��������⣬ר�θ��ֲ���
			sendBuffer.clear();
			SocketChannel client = (SocketChannel) key.channel();
			String sendText = "hello client" + flag ++;
			sendBuffer.put(sendText.getBytes());
			//ͬ����������������ӣ�д�ĺܺã�����ʹ��out��д��ʱ�򣬴�ͷ��ʼ��д
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
