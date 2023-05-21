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
public final class OriginalTimeTable {

    private final String lecture;
    private final String teacher;

    public static OriginalTimeTable from(PeriodTimeTable timeTable) {
        return new OriginalTimeTable(timeTable.getLecture(), timeTable.getTeacher());
    }
}
