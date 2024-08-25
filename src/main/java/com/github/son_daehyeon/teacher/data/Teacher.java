package com.github.son_daehyeon.teacher.data;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@RequiredArgsConstructor(staticName = "of")
public final class Teacher {

	private final int id;
	private final String name;
}
