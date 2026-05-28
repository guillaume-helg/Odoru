import { inject, computed } from '@angular/core';
import { signalStore, withState, withComputed, withMethods, patchState } from '@ngrx/signals';
import { MemberService } from '../services/member';
import { Observable } from 'rxjs';

export interface MemberState {
  members: any[];
  loading: boolean;
  error: string | null;
  success: string | null;
}

const initialState: MemberState = {
  members: [],
  loading: false,
  error: null,
  success: null,
};

export const MemberStore = signalStore(
  { providedIn: 'root' },
  withState(initialState),
  withComputed(({ members }) => ({
    students: computed(() => members().filter((m: any) => m.role === 'STUDENT')),
  })),
  withMethods((store, memberService = inject(MemberService)) => {
    const clearMessages = () => {
      setTimeout(() => {
        patchState(store, { success: null, error: null });
      }, 3000);
    };

    return {
      loadMembers() {
        patchState(store, { loading: true, error: null });
        memberService.getMembers().subscribe({
          next: (data) => patchState(store, { members: data, loading: false }),
          error: (err) => {
            console.error(err);
            patchState(store, { error: 'Failed to retrieve club members.', loading: false });
          }
        });
      },

      signup(memberData: any): Observable<any> {
        return memberService.signup(memberData);
      },

      updateRole(memberId: string, newRole: string) {
        memberService.patchMemberRole(memberId, newRole).subscribe({
          next: () => {
            patchState(store, { success: 'Member role updated successfully!' });
            this.loadMembers();
            clearMessages();
          },
          error: (err) => {
            console.error(err);
            patchState(store, { error: 'Failed to update member role.' });
          }
        });
      },

      updateExpertise(memberId: string, newLevel: number) {
        memberService.patchMemberExpertise(memberId, newLevel).subscribe({
          next: () => {
            patchState(store, { success: 'Member expertise level updated!' });
            this.loadMembers();
            clearMessages();
          },
          error: (err) => {
            console.error(err);
            patchState(store, { error: 'Failed to update expertise level.' });
          }
        });
      },

      updateRegistrationStatus(memberId: string, statusData: {
        registrationValidated?: boolean;
        feePaid?: boolean;
        medicalCertificateProvided?: boolean;
      }) {
        memberService.patchMemberRegistrationStatus(memberId, statusData).subscribe({
          next: () => {
            patchState(store, { success: 'Member compliance checklist updated.' });
            this.loadMembers();
            clearMessages();
          },
          error: (err) => {
            console.error(err);
            patchState(store, { error: 'Failed to update compliance status.' });
          }
        });
      },

      deleteMember(memberId: string) {
        memberService.deleteMember(memberId).subscribe({
          next: () => {
            patchState(store, { success: 'Member removed successfully.' });
            this.loadMembers();
            clearMessages();
          },
          error: (err) => {
            console.error(err);
            patchState(store, { error: 'Failed to delete member.' });
          }
        });
      }
    };
  })
);
