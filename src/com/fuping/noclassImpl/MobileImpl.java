package com.fuping.noclassImpl;

public class MobileImpl implements Mobile {

	@Override
	public String seeMovie(String name) {
		System.out.println(name);
		return name;
	}

	@Override
	public void seePicture(String name) {
		System.out.println(name);
	}

}
