package com.fuping.noclassImpl;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

public class AllHandlerImpl implements InvocationHandler {
	
	private Map<String, Object> map;
	
	public static Object newInstance(Class clazz) {
		return Proxy.newProxyInstance(clazz.getClassLoader(), new Class[] {clazz}, new AllHandlerImpl());
	}
	
	public AllHandlerImpl() {
		this.map = new HashMap<>();
		map.put("mobile", new MobileImpl());
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		
		Class<?> declaringClass = method.getDeclaringClass();
		
		if (Object.class.equals(declaringClass)) {
			System.out.println("do nothing");
		} else {
			//����Ϳ����������ʲô��
			String methodName = method.getName();
			//ͨ���ӿڵ����ƶ�Ϊʵ����
			//����ӿڱ��
			String interfaceName = declaringClass.getSimpleName();
			String s = (char)(interfaceName.charAt(0) + 32) + interfaceName.substring(1);
			Object mobile = map.get(s);
			if (null != mobile) {
				//����ʵ��������
				Object invoke = method.invoke(mobile, args);
				return invoke;
			} else {
				//������ʵ����ֱ��invoke��ʵ�ֵķ���
				System.out.println("this is " + interfaceName  + " " + methodName);
			}
			
		}
		return null;
	}

}
