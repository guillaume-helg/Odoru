import { inject } from '@angular/core';
import { signalStore, withState, withMethods, patchState } from '@ngrx/signals';
import { MemberService } from '../services/member';
import { LessonService } from '../services/lesson';
import { BadgeService } from '../services/badge';
import { CompetitionService } from '../services/competition';

export interface StudentState {
  loading: boolean;
  error: string | null;
  studentProfile: any | null;
  myLessons: any[];
  myAttendance: any[];
  myCompetitionResults: any[];
}

const initialState: StudentState = {
  loading: false,
  error: null,
  studentProfile: null,
  myLessons: [],
  myAttendance: [],
  myCompetitionResults: [],
};

export const StudentStore = signalStore(
  { providedIn: 'root' },
  withState(initialState),
  withMethods((
    store,
    memberService = inject(MemberService),
    lessonService = inject(LessonService),
    badgeService = inject(BadgeService),
    competitionService = inject(CompetitionService)
  ) => {
    return {
      loadStudentPortal(username: string) {
        if (!username) return;

        patchState(store, { loading: true, error: null });

        memberService.getMembers().subscribe({
          next: (members) => {
            const student = members.find(m => m.username === username);
            if (!student) {
              patchState(store, { error: 'No student profile found associated with this username.', loading: false });
              return;
            }

            patchState(store, { studentProfile: student });
            const studentId = student.id;

            // Fetch lessons
            lessonService.getLessonsByStudent(studentId).subscribe({
              next: (lessons) => {
                patchState(store, { myLessons: lessons });
              }
            });

            // Fetch attendance logs
            badgeService.getBadgeAttendanceByStudent(studentId).subscribe({
              next: (attLogs) => {
                patchState(store, { myAttendance: attLogs });
              }
            });

            // Fetch competition results
            competitionService.getCompetitionResultsByStudent(studentId).subscribe({
              next: (results) => {
                patchState(store, { myCompetitionResults: results, loading: false });
              },
              error: () => patchState(store, { loading: false })
            });
          },
          error: (err) => {
            console.error(err);
            patchState(store, { error: 'Failed to connect to microservices. Please verify services are up.', loading: false });
          }
        });
      }
    };
  })
);
