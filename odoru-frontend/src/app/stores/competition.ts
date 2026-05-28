import { inject } from '@angular/core';
import { signalStore, withState, withMethods, patchState } from '@ngrx/signals';
import { CompetitionService } from '../services/competition';

export interface CompetitionState {
  competitions: any[];
  loading: boolean;
  error: string | null;
  success: string | null;
}

const initialState: CompetitionState = {
  competitions: [],
  loading: false,
  error: null,
  success: null,
};

export const CompetitionStore = signalStore(
  { providedIn: 'root' },
  withState(initialState),
  withMethods((store, competitionService = inject(CompetitionService)) => {
    const clearMessages = () => {
      setTimeout(() => {
        patchState(store, { success: null, error: null });
      }, 3000);
    };

    return {
      setError(message: string | null) {
        patchState(store, { error: message });
      },
      loadCompetitions() {
        patchState(store, { loading: true, error: null });
        competitionService.getCompetitions().subscribe({
          next: (data) => patchState(store, { competitions: data, loading: false }),
          error: (err) => {
            console.error(err);
            patchState(store, { error: 'Failed to retrieve competitions.', loading: false });
          }
        });
      },

      createCompetition(payload: any) {
        patchState(store, { loading: true, error: null });
        competitionService.createCompetition(payload).subscribe({
          next: () => {
            patchState(store, { success: 'Competition scheduled successfully!' });
            this.loadCompetitions();
            clearMessages();
          },
          error: (err) => {
            console.error(err);
            patchState(store, { error: 'Failed to schedule competition.', loading: false });
          }
        });
      },

      submitScore(competitionId: string, studentId: string, payload: { score: number, teacherId: string }, onSuccess: () => void) {
        patchState(store, { loading: true, error: null });
        competitionService.submitCompetitionResult(competitionId, studentId, payload).subscribe({
          next: () => {
            patchState(store, { success: 'Student score submitted successfully!', loading: false });
            onSuccess();
            clearMessages();
          },
          error: (err) => {
            console.error(err);
            patchState(store, { error: err.error?.message || 'Failed to submit score.', loading: false });
          }
        });
      }
    };
  })
);
