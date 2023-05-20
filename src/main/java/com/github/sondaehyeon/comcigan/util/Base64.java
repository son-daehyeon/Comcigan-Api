package com.github.sondaehyeon.comcigan.util;

import java.nio.charset.StandardCharsets;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Base64 {

	public static String encode(String text) {
		return new String(java.util.Base64.getEncoder().encode(text.getBytes(StandardCharsets.UTF_8)));
	}

	public static String decode(String text) {
		return new String(java.util.Base64.getDecoder().decode(text));
	}
}
