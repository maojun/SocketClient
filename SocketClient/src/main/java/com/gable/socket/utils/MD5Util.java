package com.gable.socket.utils;

import org.springframework.security.authentication.encoding.Md5PasswordEncoder;



public class MD5Util {
	
	public final static String MD5_SALT = "gable!@#$%^";
	
    public final static String md5(String s) {
        return new Md5PasswordEncoder().encodePassword(s,MD5_SALT);
    }
}