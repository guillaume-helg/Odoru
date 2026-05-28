import { Component, inject, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MemberStore } from '../../stores/member';
import { UserStore } from '../../stores/user';
import { Router } from '@angular/router';

@Component({
  selector: 'app-president',
  imports: [FormsModule],
  templateUrl: './president.html',
  styleUrl: './president.css'
})
export class President implements OnInit {
  readonly memberStore = inject(MemberStore);
  readonly store = inject(UserStore);
  private readonly router = inject(Router);

  ngOnInit() {
    // Only allow President role
    if (this.store.role() !== 'PRESIDENT') {
      this.router.navigate(['/dashboard']);
      return;
    }
    this.memberStore.loadMembers();
  }

  updateRole(memberId: string, newRole: string) {
    this.memberStore.updateRole(memberId, newRole);
  }

  updateExpertise(memberId: string, event: Event) {
    const select = event.target as HTMLSelectElement;
    const newLevel = Number(select.value);
    this.memberStore.updateExpertise(memberId, newLevel);
  }

  removeMember(memberId: string) {
    if (!confirm('Are you sure you want to remove this member from the club? This action is irreversible.')) {
      return;
    }
    this.memberStore.deleteMember(memberId);
  }
}
