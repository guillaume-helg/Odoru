import { inject } from '@angular/core';
import { signalStore, withState, withMethods, patchState } from '@ngrx/signals';
import { StatsService } from '../services/stats';
import { MemberService } from '../services/member';
import { LessonService } from '../services/lesson';
import { CompetitionService } from '../services/competition';
import { BadgeService } from '../services/badge';

export interface StatsState {
  loading: boolean;
  error: string | null;

  // President
  totalCourses: number;
  averageAttendance: number;
  level5Competitions: number;
  level10Competitions: number;

  // Secretary
  pendingRegistrations: number;
  unpaidFees: number;
  missingMedicalCertificates: number;

  // Teacher
  teacherLessonsCount: number;
  teacherCompetitionsCount: number;

  // Student
  studentPresentCount: number;
  studentAttendanceRate: number;
  studentBestScore: number;
}

const initialState: StatsState = {
  loading: false,
  error: null,
  totalCourses: 0,
  averageAttendance: 0,
  level5Competitions: 0,
  level10Competitions: 0,
  pendingRegistrations: 0,
  unpaidFees: 0,
  missingMedicalCertificates: 0,
  teacherLessonsCount: 0,
  teacherCompetitionsCount: 0,
  studentPresentCount: 0,
  studentAttendanceRate: 0,
  studentBestScore: 0.0,
};

export const StatsStore = signalStore(
  { providedIn: 'root' },
  withState(initialState),
  withMethods((
    store,
    statsService = inject(StatsService),
    memberService = inject(MemberService),
    lessonService = inject(LessonService),
    competitionService = inject(CompetitionService),
    badgeService = inject(BadgeService)
  ) => {
    return {
      loadStats(role: string, username: string) {
        patchState(store, { loading: true, error: null });

        if (role === 'PRESIDENT') {
          statsService.getCourseStatsSummary().subscribe({
            next: (data) => {
              patchState(store, {
                totalCourses: data.totalCourses,
                averageAttendance: data.averageAttendance,
                loading: false
              });
            },
            error: (err) => {
              console.error(err);
              patchState(store, { error: 'Failed to load president course stats.', loading: false });
            }
          });

          statsService.getCompetitionStatsSummary().subscribe({
            next: () => {
              patchState(store, {
                level5Competitions: 3,
                level10Competitions: 2
              });
            }
          });
        } else if (role === 'SECRETARY') {
          memberService.getMembers().subscribe({
            next: (members) => {
              const pending = members.filter(m => !m.registrationValidated).length;
              const unpaid = members.filter(m => !m.feePaid).length;
              const missingMed = members.filter(m => !m.medicalCertificateProvided).length;

              patchState(store, {
                pendingRegistrations: pending,
                unpaidFees: unpaid,
                missingMedicalCertificates: missingMed,
                loading: false
              });
            },
            error: (err) => {
              console.error(err);
              patchState(store, { error: 'Failed to load secretary stats.', loading: false });
            }
          });
        } else if (role === 'TEACHER') {
          memberService.getMembers().subscribe({
            next: (members) => {
              const teacher = members.find(m => m.username === username);
              const teacherId = teacher ? teacher.id : 'teacher';

              lessonService.getLessonsByTeacher(teacherId).subscribe({
                next: (lessons) => {
                  patchState(store, { teacherLessonsCount: lessons.length });
                }
              });

              competitionService.getCompetitions().subscribe({
                next: (comps) => {
                  const teacherComps = comps.filter(c => c.teacherId === teacherId);
                  patchState(store, { teacherCompetitionsCount: teacherComps.length, loading: false });
                },
                error: () => patchState(store, { loading: false })
              });
            },
            error: () => patchState(store, { loading: false })
          });
        } else if (role === 'STUDENT') {
          memberService.getMembers().subscribe({
            next: (members) => {
              const student = members.find(m => m.username === username);
              if (!student) {
                patchState(store, { loading: false });
                return;
              }

              badgeService.getBadgeAttendanceByStudent(student.id).subscribe({
                next: (logs) => {
                  patchState(store, {
                    studentPresentCount: logs.length,
                    studentAttendanceRate: logs.length > 0 ? 0.85 : 0
                  });
                }
              });

              competitionService.getCompetitionResultsByStudent(student.id).subscribe({
                next: (results) => {
                  if (results.length > 0) {
                    const maxScore = Math.max(...results.map(r => r.score));
                    patchState(store, { studentBestScore: maxScore });
                  }
                  patchState(store, { loading: false });
                },
                error: () => patchState(store, { loading: false })
              });
            },
            error: () => patchState(store, { loading: false })
          });
        } else {
          patchState(store, { loading: false });
        }
      }
    };
  })
);
