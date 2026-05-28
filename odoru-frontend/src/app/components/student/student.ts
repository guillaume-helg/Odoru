import { Component, inject, OnInit } from '@angular/core';
import { DatePipe, DecimalPipe } from '@angular/common';
import { StudentStore } from '../../stores/student';
import { UserStore } from '../../stores/user';
import { Router } from '@angular/router';

@Component({
  selector: 'app-student',
  imports: [DatePipe, DecimalPipe],
  templateUrl: './student.html',
  styleUrl: './student.css'
})
export class Student implements OnInit {
  readonly studentStore = inject(StudentStore);
  readonly store = inject(UserStore);
  private readonly router = inject(Router);

  ngOnInit() {
    if (this.store.role() !== 'STUDENT') {
      this.router.navigate(['/dashboard']);
      return;
    }
    this.loadStudentPortal();
  }

  loadStudentPortal() {
    const username = this.store.username();
    if (username) {
      this.studentStore.loadStudentPortal(username);
    }
  }
}
