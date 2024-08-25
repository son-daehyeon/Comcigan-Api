package com.github.son_daehyeon.teacher.timetable;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor(staticName = "of")
public final class PeriodTimeTable {

    private final int period;
    private final String lecture;
    private final String location;
    private final boolean isModify;
}

