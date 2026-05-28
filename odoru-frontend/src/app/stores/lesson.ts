import { inject } from '@angular/core';
import { signalStore, withState, withMethods, patchState } from '@ngrx/signals';
import { LessonService } from '../services/lesson';

export interface LessonState {
  lessons: any[];
  loading: boolean;
  error: string | null;
  success: string | null;
}

const initialState: LessonState = {
  lessons: [],
  loading: false,
  error: null,
  success: null,
};

export const LessonStore = signalStore(
  { providedIn: 'root' },
  withState(initialState),
  withMethods((store, lessonService = inject(LessonService)) => {
    const clearMessages = () => {
      setTimeout(() => {
        patchState(store, { success: null, error: null });
      }, 3000);
    };

    return {
      setError(message: string | null) {
        patchState(store, { error: message });
      },
      loadLessons() {
        patchState(store, { loading: true, error: null });
        lessonService.getLessons().subscribe({
          next: (data) => patchState(store, { lessons: data, loading: false }),
          error: (err) => {
            console.error(err);
            patchState(store, { error: 'Failed to retrieve club lessons.', loading: false });
          }
        });
      },

      createLesson(payload: any) {
        patchState(store, { loading: true, error: null });
        lessonService.createLesson(payload).subscribe({
          next: () => {
            patchState(store, { success: 'New course slot scheduled successfully!' });
            this.loadLessons();
            clearMessages();
          },
          error: (err) => {
            console.error(err);
            patchState(store, { error: 'Failed to schedule course.', loading: false });
          }
        });
      }
    };
  })
);
