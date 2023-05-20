package com.github.sondaehyeon.comcigan.data;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@RequiredArgsConstructor(staticName = "of")
public final class School {

	private final int code;
	private final String name;
	private final String location;
}
