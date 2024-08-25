package com.github.son_daehyeon.student.timetable;

import lombok.*;

@Data
@RequiredArgsConstructor(staticName = "of")
public final class PeriodTimeTable {

    private final int period;
    private final String lecture;
    private final String teacher;
    private boolean isModify;
    private OriginalTimeTable original;

    @Data
    @RequiredArgsConstructor(staticName = "of")
    public final static class OriginalTimeTable {
        private final int period;
        private final String lecture;
        private final String teacher;

        public static OriginalTimeTable of(PeriodTimeTable timeTable) {
            return OriginalTimeTable.of(timeTable.period, timeTable.lecture, timeTable.teacher);
        }
    }
}

