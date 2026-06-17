package com.odoru.statsservice.service;

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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StatsService {

    private final MemberClient memberClient;
    private final LessonClient lessonClient;
    private final CompetitionClient competitionClient;
    private final BadgeClient badgeClient;

    public CourseSummaryDto getCourseSummary() {
        final List<LessonDto> lessons = lessonClient.getAllLessons();
        if (lessons.isEmpty()) {
            return CourseSummaryDto.builder()
                .totalCourses(0)
                .averageAttendance(0.0)
                .build();
        }

        int totalAttendance = 0;
        for (final LessonDto lesson : lessons) {
            totalAttendance += badgeClient
                .getLessonAttendees(lesson.getId())
                .size();
        }

        final double average = (double) totalAttendance / lessons.size();

        return CourseSummaryDto.builder()
            .totalCourses(lessons.size())
            .averageAttendance(average)
            .build();
    }

    public LessonAttendanceDto getLessonAttendance(final String lessonId) {

        lessonClient.getLessonById(lessonId);

        final List<String> studentIds = badgeClient.getLessonAttendees(
            lessonId
        );

        final List<MemberDto> students = new ArrayList<>();
        for (final String id : studentIds) {
            try {
                students.add(memberClient.getMemberById(id));
            } catch (Exception ex) {

            }
        }

        return LessonAttendanceDto.builder()
            .presentCount(students.size())
            .presentStudents(students)
            .build();
    }

    public List<StudentCoursePresenceDto> getStudentCoursePresence(
        final String studentId,
        final LocalDateTime start,
        final LocalDateTime end
    ) {

        final MemberDto student = memberClient.getMemberById(studentId);
        final int targetLevel = student.getExpertiseLevel();

        List<LessonDto> lessons = lessonClient
            .getAllLessons()
            .stream()
            .filter(lesson -> lesson.getTargetLevel() == targetLevel)
            .collect(Collectors.toList());

        if (start != null) {
            lessons = lessons
                .stream()
                .filter(l -> !l.getDateTime().isBefore(start))
                .collect(Collectors.toList());
        }
        if (end != null) {
            lessons = lessons
                .stream()
                .filter(l -> !l.getDateTime().isAfter(end))
                .collect(Collectors.toList());
        }

        final List<LessonDto> attended = badgeClient.getStudentAttendedLessons(
            studentId
        );
        final Set<String> attendedIds = attended
            .stream()
            .map(LessonDto::getId)
            .collect(Collectors.toSet());

        final List<StudentCoursePresenceDto> result = new ArrayList<>();
        for (final LessonDto lesson : lessons) {
            result.add(
                StudentCoursePresenceDto.builder()
                    .lesson(lesson)
                    .present(attendedIds.contains(lesson.getId()))
                    .build()
            );
        }

        return result;
    }

    public long getCompetitionsCountByLevel(final int level) {
        return competitionClient
            .getAllCompetitions()
            .stream()
            .filter(comp -> comp.getTargetLevel() == level)
            .count();
    }

    public List<StudentCompetitionResultDto> getStudentCompetitionResults(
        final String studentId,
        final LocalDateTime start,
        final LocalDateTime end
    ) {

        final MemberDto student = memberClient.getMemberById(studentId);
        final int level = student.getExpertiseLevel();

        List<CompetitionDto> competitions = competitionClient
            .getAllCompetitions()
            .stream()
            .filter(comp -> comp.getTargetLevel() == level)
            .collect(Collectors.toList());

        if (start != null) {
            competitions = competitions
                .stream()
                .filter(c -> !c.getDateTime().isBefore(start))
                .collect(Collectors.toList());
        }
        if (end != null) {
            competitions = competitions
                .stream()
                .filter(c -> !c.getDateTime().isAfter(end))
                .collect(Collectors.toList());
        }

        final List<CompetitionResultDto> results =
            competitionClient.getStudentResults(studentId);
        final Map<String, Double> scoreMap = results
            .stream()
            .collect(
                Collectors.toMap(
                    CompetitionResultDto::getCompetitionId,
                    CompetitionResultDto::getScore,
                    (score1, score2) -> score1
                )
            );

        final List<StudentCompetitionResultDto> list = new ArrayList<>();
        for (final CompetitionDto comp : competitions) {
            list.add(
                StudentCompetitionResultDto.builder()
                    .competition(comp)
                    .score(scoreMap.get(comp.getId()))
                    .build()
            );
        }

        return list;
    }
}
