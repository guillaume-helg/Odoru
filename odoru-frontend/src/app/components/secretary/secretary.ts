import { Component, inject, signal, OnInit, computed } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MemberStore } from '../../stores/member';
import { LessonStore } from '../../stores/lesson';
import { BadgeStore } from '../../stores/badge';
import { UserStore } from '../../stores/user';
import { Router } from '@angular/router';

@Component({
  selector: 'app-secretary',
  imports: [FormsModule],
  templateUrl: './secretary.html',
  styleUrl: './secretary.css'
})
export class Secretary implements OnInit {
  readonly memberStore = inject(MemberStore);
  readonly lessonStore = inject(LessonStore);
  readonly badgeStore = inject(BadgeStore);
  readonly store = inject(UserStore);
  private readonly router = inject(Router);

  // Combined Loading/Error/Success states via computed signals
  readonly loading = computed(() => this.memberStore.loading() || this.lessonStore.loading() || this.badgeStore.loading());
  readonly error = computed(() => this.memberStore.error() || this.lessonStore.error() || this.badgeStore.error());
  readonly success = computed(() => this.memberStore.success() || this.badgeStore.success());
  readonly scanOutcome = computed(() => this.badgeStore.scanOutcome());

  // Badge association state
  selectedMemberId = '';
  badgeNumberInput = '';

  // Scan simulation state
  selectedLessonId = '';
  scanBadgeNumber = '';

  ngOnInit() {
    if (this.store.role() !== 'SECRETARY') {
      this.router.navigate(['/dashboard']);
      return;
    }
    this.loadData();
  }

  loadData() {
    this.memberStore.loadMembers();
    this.lessonStore.loadLessons();
  }

  toggleRegistration(member: any) {
    const nextVal = !member.registrationValidated;
    this.memberStore.updateRegistrationStatus(member.id, { registrationValidated: nextVal });
  }

  toggleFee(member: any) {
    const nextVal = !member.feePaid;
    this.memberStore.updateRegistrationStatus(member.id, { feePaid: nextVal });
  }

  toggleMedical(member: any) {
    const nextVal = !member.medicalCertificateProvided;
    this.memberStore.updateRegistrationStatus(member.id, { medicalCertificateProvided: nextVal });
  }

  linkBadge() {
    if (!this.selectedMemberId || !this.badgeNumberInput.trim()) {
      this.badgeStore.setError('Please select a member and enter a badge number.');
      return;
    }

    this.badgeStore.linkBadge({
      memberId: this.selectedMemberId,
      badgeNumber: this.badgeNumberInput.trim()
    }, () => {
      this.badgeNumberInput = '';
      this.selectedMemberId = '';
      this.loadData();
    });
  }

  unlinkBadge(memberId: string) {
    if (!confirm('Are you sure you want to dissociate the badge from this member?')) {
      return;
    }

    this.badgeStore.unlinkBadge(memberId, () => {
      this.loadData();
    });
  }

  simulateScan() {
    if (!this.selectedLessonId || !this.scanBadgeNumber.trim()) {
      this.badgeStore.setScanOutcome({ success: false, message: 'Please select a lesson and provide badge number.' });
      return;
    }

    this.badgeStore.scanBadge({
      badgeNumber: this.scanBadgeNumber.trim(),
      lessonId: this.selectedLessonId
    }, () => {
      this.scanBadgeNumber = '';
    });
  }
}
