package com.fuping.aio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.ExecutionException;

public class AIOServer {
	
	
	public AIOServer(int port) throws IOException {
		//��ʼ��AIO
		final AsynchronousServerSocketChannel assc = AsynchronousServerSocketChannel.open().bind(new InetSocketAddress(port));
		//��BIOͬ���Ĳ����������ڸо������첽����
		assc.accept(null, new CompletionHandler<AsynchronousSocketChannel, Void>() {

			@Override
			public void completed(AsynchronousSocketChannel result, Void attachment) {
				//��һ������
				assc.accept(null, this);
				try {
					//�����߼�
					handle(result);
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				}
			}
			
			@Override
			public void failed(Throwable exc, Void attachment) {
				System.out.println("this is a fail io");
				
			}
		});
	}

	private void handle(AsynchronousSocketChannel result) throws InterruptedException, ExecutionException {
		ByteBuffer buffer = ByteBuffer.allocate(32);
		Integer integer = result.read(buffer).get();
		buffer.flip();
		System.out.println("server receive" + buffer.get() + " " + integer);
	}

	public static void main(String[] args) throws Exception {
		AIOServer aio = new AIOServer(8888);
		System.out.println("�����˿�");
	}


}
