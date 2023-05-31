package com.github.ioloolo.comcigan.data.timetable;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(staticName = "of")
public final class TeacherTimeTable {

    private final int period;
    private final String lecture;
    private final String location;
    private final boolean isModify;
}

