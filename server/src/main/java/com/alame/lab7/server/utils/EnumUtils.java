package com.alame.lab7.server.utils;


public class EnumUtils {
	public static String name(Enum<?> e){
		return e==null?null:e.name();
	}
}
