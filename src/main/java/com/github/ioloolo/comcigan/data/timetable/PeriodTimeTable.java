package com.github.ioloolo.comcigan.data.timetable;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(staticName = "of")
public final class PeriodTimeTable {

    private final int period;
    private final String lecture;
    private final String teacher;

    private boolean isModify;
    private OriginalTimeTable original;

    @RequiredArgsConstructor
    public enum TimeTableType {

        TODAY("자료147"),
        ORIGINAL("자료481"),
        ;

        @Getter
        private final String jsonKey;
    }
}

