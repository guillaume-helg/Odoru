package com.odoru.statsservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import com.odoru.statsservice.client.BadgeClient;
import com.odoru.statsservice.client.CompetitionClient;
import com.odoru.statsservice.client.LessonClient;
import com.odoru.statsservice.client.MemberClient;
import com.odoru.statsservice.dto.CompetitionDto;
import com.odoru.statsservice.dto.CompetitionResultDto;
import com.odoru.statsservice.dto.CourseSummaryDto;
import com.odoru.statsservice.dto.LessonAttendanceDto;
import com.odoru.statsservice.dto.LessonDto;
import com.odoru.statsservice.dto.MemberDto;
import com.odoru.statsservice.dto.StudentCompetitionResultDto;
import com.odoru.statsservice.dto.StudentCoursePresenceDto;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class StatsServiceTest {

    @Mock
    private MemberClient memberClient;

    @Mock
    private LessonClient lessonClient;

    @Mock
    private CompetitionClient competitionClient;

    @Mock
    private BadgeClient badgeClient;

    @InjectMocks
    private StatsService statsService;

    @Test
    public void testGetCourseSummary() {
        final LessonDto lesson1 = LessonDto.builder().id("l-1").build();
        final LessonDto lesson2 = LessonDto.builder().id("l-2").build();

        when(lessonClient.getAllLessons()).thenReturn(
            List.of(lesson1, lesson2)
        );
        when(badgeClient.getLessonAttendees("l-1")).thenReturn(List.of("s-1"));
        when(badgeClient.getLessonAttendees("l-2")).thenReturn(
            List.of("s-1", "s-2")
        );

        final CourseSummaryDto summary = statsService.getCourseSummary();

        assertNotNull(summary);
        assertEquals(2, summary.getTotalCourses());
        assertEquals(1.5, summary.getAverageAttendance());
    }

    @Test
    public void testGetLessonAttendance() {
        final MemberDto student = MemberDto.builder()
            .id("s-1")
            .firstName("John")
            .build();

        when(lessonClient.getLessonById("l-1")).thenReturn(new LessonDto());
        when(badgeClient.getLessonAttendees("l-1")).thenReturn(List.of("s-1"));
        when(memberClient.getMemberById("s-1")).thenReturn(student);

        final LessonAttendanceDto attendance = statsService.getLessonAttendance(
            "l-1"
        );

        assertNotNull(attendance);
        assertEquals(1, attendance.getPresentCount());
        assertEquals(
            "John",
            attendance.getPresentStudents().get(0).getFirstName()
        );
    }

    @Test
    public void testGetStudentCoursePresence() {
        final MemberDto student = MemberDto.builder()
            .id("s-1")
            .expertiseLevel(3)
            .build();

        final LessonDto lesson1 = LessonDto.builder()
            .id("l-1")
            .targetLevel(3)
            .dateTime(LocalDateTime.now())
            .build();

        when(memberClient.getMemberById("s-1")).thenReturn(student);
        when(lessonClient.getAllLessons()).thenReturn(List.of(lesson1));
        when(badgeClient.getStudentAttendedLessons("s-1")).thenReturn(
            List.of()
        );

        final List<StudentCoursePresenceDto> list =
            statsService.getStudentCoursePresence("s-1", null, null);

        assertNotNull(list);
        assertEquals(1, list.size());
        assertFalse(list.get(0).isPresent());
    }

    @Test
    public void testGetStudentCompetitionResults() {
        final MemberDto student = MemberDto.builder()
            .id("s-1")
            .expertiseLevel(3)
            .build();

        final CompetitionDto comp1 = CompetitionDto.builder()
            .id("c-1")
            .targetLevel(3)
            .dateTime(LocalDateTime.now())
            .build();

        final CompetitionResultDto result = CompetitionResultDto.builder()
            .competitionId("c-1")
            .score(8.2)
            .build();

        when(memberClient.getMemberById("s-1")).thenReturn(student);
        when(competitionClient.getAllCompetitions()).thenReturn(List.of(comp1));
        when(competitionClient.getStudentResults("s-1")).thenReturn(
            Collections.singletonList(result)
        );

        final List<StudentCompetitionResultDto> list =
            statsService.getStudentCompetitionResults("s-1", null, null);

        assertNotNull(list);
        assertEquals(1, list.size());
        assertEquals(8.2, list.get(0).getScore());
    }

    @Test
    public void testGetCompetitionsCountByLevel() {
        final CompetitionDto comp1 = CompetitionDto.builder()
            .id("c-1")
            .targetLevel(3)
            .build();
        final CompetitionDto comp2 = CompetitionDto.builder()
            .id("c-2")
            .targetLevel(4)
            .build();

        when(competitionClient.getAllCompetitions()).thenReturn(
            List.of(comp1, comp2)
        );

        final long count = statsService.getCompetitionsCountByLevel(3);

        assertEquals(1, count);
    }
}
