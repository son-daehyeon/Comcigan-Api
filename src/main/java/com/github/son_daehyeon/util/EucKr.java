package com.github.son_daehyeon.util;

import java.nio.charset.Charset;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class EucKr {

	public static String convert(String text) {
		StringBuilder hexString = new StringBuilder();
		byte[] bytes = text.getBytes(Charset.forName("euc-kr"));

		for (byte b : bytes) {
			hexString.append("%").append(String.format("%02x", b));
		}

		return hexString.toString();
	}
}
