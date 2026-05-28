import { inject } from '@angular/core';
import { signalStore, withState, withMethods, patchState } from '@ngrx/signals';
import { BadgeService } from '../services/badge';

export interface BadgeState {
  loading: boolean;
  error: string | null;
  success: string | null;
  scanOutcome: { success: boolean; message: string } | null;
}

const initialState: BadgeState = {
  loading: false,
  error: null,
  success: null,
  scanOutcome: null,
};

export const BadgeStore = signalStore(
  { providedIn: 'root' },
  withState(initialState),
  withMethods((store, badgeService = inject(BadgeService)) => {
    const clearMessages = () => {
      setTimeout(() => {
        patchState(store, { success: null, error: null });
      }, 3000);
    };

    return {
      setError(message: string | null) {
        patchState(store, { error: message });
      },
      setScanOutcome(outcome: { success: boolean; message: string } | null) {
        patchState(store, { scanOutcome: outcome });
      },
      linkBadge(payload: { memberId: string, badgeNumber: string }, onSuccess: () => void) {
        patchState(store, { loading: true, error: null });
        badgeService.associateBadge(payload).subscribe({
          next: () => {
            patchState(store, { success: 'RFID Badge successfully associated to member!', loading: false });
            onSuccess();
            clearMessages();
          },
          error: (err) => {
            console.error(err);
            patchState(store, { error: err.error?.message || 'Failed to associate badge.', loading: false });
          }
        });
      },

      unlinkBadge(memberId: string, onSuccess: () => void) {
        patchState(store, { loading: true, error: null });
        badgeService.dissociateBadge(memberId).subscribe({
          next: () => {
            patchState(store, { success: 'Badge successfully dissociated.', loading: false });
            onSuccess();
            clearMessages();
          },
          error: (err) => {
            console.error(err);
            patchState(store, { error: 'Failed to dissociate badge.', loading: false });
          }
        });
      },

      scanBadge(payload: { badgeNumber: string, lessonId: string }, onSuccess: () => void) {
        patchState(store, { scanOutcome: null });
        badgeService.scanBadge(payload).subscribe({
          next: (res) => {
            patchState(store, {
              scanOutcome: {
                success: true,
                message: `Swipe Successful! Attendance logged for Student ID: ${res.memberId} at ${new Date(res.timestamp).toLocaleTimeString()}`
              }
            });
            onSuccess();
          },
          error: (err) => {
            console.error(err);
            patchState(store, {
              scanOutcome: {
                success: false,
                message: err.error?.message || 'Swipe Failed! Invalid badge number or student is not eligible for this course level.'
              }
            });
          }
        });
      }
    };
  })
);
