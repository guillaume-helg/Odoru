package com.odoru.competitionservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.odoru.competitionservice.client.MemberClient;
import com.odoru.competitionservice.dto.MemberDto;
import com.odoru.competitionservice.model.Competition;
import com.odoru.competitionservice.model.CompetitionResult;
import com.odoru.competitionservice.model.MemberRole;
import com.odoru.competitionservice.repository.CompetitionRepository;
import com.odoru.competitionservice.repository.CompetitionResultRepository;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CompetitionServiceTest {

    @Mock
    private CompetitionRepository competitionRepository;

    @Mock
    private CompetitionResultRepository competitionResultRepository;

    @Mock
    private MemberClient memberClient;

    @InjectMocks
    private CompetitionService competitionService;

    @Test
    public void testCreateCompetition_Success() {
        final Competition competition = Competition.builder()
            .title("Grand Prix")
            .targetLevel(3)
            .dateTime(LocalDateTime.now().plusDays(8))
            .duration(120)
            .teacherId("teacher-1")
            .location("Main Hall")
            .build();

        final MemberDto teacher = MemberDto.builder()
            .id("teacher-1")
            .role(MemberRole.TEACHER)
            .expertiseLevel(4)
            .build();

        when(memberClient.getMemberById("teacher-1")).thenReturn(teacher);
        when(competitionRepository.save(any(Competition.class))).thenReturn(
            competition
        );

        final Competition created = competitionService.createCompetition(
            competition
        );

        assertNotNull(created);
        assertEquals("Grand Prix", created.getTitle());
        verify(competitionRepository).save(competition);
    }

    @Test
    public void testCreateCompetition_InvalidDate() {
        final Competition competition = Competition.builder()
            .title("Grand Prix")
            .targetLevel(3)
            .dateTime(LocalDateTime.now().plusDays(6))
            .duration(120)
            .teacherId("teacher-1")
            .location("Main Hall")
            .build();

        assertThrows(IllegalArgumentException.class, () ->
            competitionService.createCompetition(competition)
        );

        verify(memberClient, never()).getMemberById(any());
        verify(competitionRepository, never()).save(any());
    }

    @Test
    public void testAddOrUpdateResult_Success() {
        final String competitionId = "comp-123";
        final String studentId = "student-456";
        final String teacherId = "teacher-789";
        final Double score = 8.5;

        final MemberDto teacher = MemberDto.builder()
            .id(teacherId)
            .role(MemberRole.TEACHER)
            .build();

        final Competition competition = Competition.builder()
            .id(competitionId)
            .targetLevel(3)
            .build();

        final MemberDto student = MemberDto.builder()
            .id(studentId)
            .role(MemberRole.MEMBER)
            .expertiseLevel(3)
            .build();

        final CompetitionResult result = CompetitionResult.builder()
            .competitionId(competitionId)
            .studentId(studentId)
            .score(score)
            .teacherId(teacherId)
            .build();

        when(memberClient.getMemberById(teacherId)).thenReturn(teacher);
        when(competitionRepository.findById(competitionId)).thenReturn(
            Optional.of(competition)
        );
        when(memberClient.getMemberById(studentId)).thenReturn(student);
        when(
            competitionResultRepository.findByCompetitionIdAndStudentId(
                competitionId,
                studentId
            )
        ).thenReturn(Optional.empty());
        when(
            competitionResultRepository.save(any(CompetitionResult.class))
        ).thenReturn(result);

        final CompetitionResult saved = competitionService.addOrUpdateResult(
            competitionId,
            studentId,
            score,
            teacherId
        );

        assertNotNull(saved);
        assertEquals(score, saved.getScore());
        verify(competitionResultRepository).save(any(CompetitionResult.class));
    }

    @Test
    public void testAddOrUpdateResult_InvalidScoreRange() {
        assertThrows(IllegalArgumentException.class, () ->
            competitionService.addOrUpdateResult(
                "comp-1",
                "student-1",
                10.5,
                "teacher-1"
            )
        );

        assertThrows(IllegalArgumentException.class, () ->
            competitionService.addOrUpdateResult(
                "comp-1",
                "student-1",
                -0.5,
                "teacher-1"
            )
        );
    }

    @Test
    public void testAddOrUpdateResult_InvalidScorePrecision() {
        assertThrows(IllegalArgumentException.class, () ->
            competitionService.addOrUpdateResult(
                "comp-1",
                "student-1",
                8.55,
                "teacher-1"
            )
        );
    }

    @Test
    public void testAddOrUpdateResult_ExpertiseMismatch() {
        final String competitionId = "comp-123";
        final String studentId = "student-456";
        final String teacherId = "teacher-789";

        final MemberDto teacher = MemberDto.builder()
            .id(teacherId)
            .role(MemberRole.TEACHER)
            .build();

        final Competition competition = Competition.builder()
            .id(competitionId)
            .targetLevel(3)
            .build();

        final MemberDto student = MemberDto.builder()
            .id(studentId)
            .role(MemberRole.MEMBER)
            .expertiseLevel(2)
            .build();

        when(memberClient.getMemberById(teacherId)).thenReturn(teacher);
        when(competitionRepository.findById(competitionId)).thenReturn(
            Optional.of(competition)
        );
        when(memberClient.getMemberById(studentId)).thenReturn(student);

        assertThrows(IllegalArgumentException.class, () ->
            competitionService.addOrUpdateResult(
                competitionId,
                studentId,
                8.0,
                teacherId
            )
        );

        verify(competitionResultRepository, never()).save(any());
    }

    @Test
    public void testGetStudentResults_Success() {
        final String studentId = "student-123";
        final MemberDto student = MemberDto.builder()
            .id(studentId)
            .role(MemberRole.MEMBER)
            .build();

        final CompetitionResult result = CompetitionResult.builder()
            .studentId(studentId)
            .score(9.2)
            .build();

        when(memberClient.getMemberById(studentId)).thenReturn(student);
        when(competitionResultRepository.findByStudentId(studentId)).thenReturn(
            Collections.singletonList(result)
        );

        final List<CompetitionResult> results =
            competitionService.getStudentResults(studentId);

        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(9.2, results.get(0).getScore());
    }
}
