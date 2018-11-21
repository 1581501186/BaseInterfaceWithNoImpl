package com.fuping.noclassImpl;

public class MainTest {
	
	public static void main(String[] args) {
		Computor newInstance = (Computor)AllHandlerImpl.newInstance(Computor.class);
		newInstance.seePicture("123");
//		System.out.println(seeMovie + "return");
	}
	
}
