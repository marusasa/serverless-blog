package ssg.serverlessblog.testdata;

import java.util.Optional;

import ssg.serverlessblog.util.PasswordUtil;

public class TestPassword {

	public static void main(String[] args) {
		try {
			Optional<String> password = PasswordUtil.hashPassword("pass123", "98z7y18daeeAHCSbVXXyLcMo5Ek8cpz4sMmTbfZI");
			password.ifPresent(System.out::println);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

}
