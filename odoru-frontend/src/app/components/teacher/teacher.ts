import { Component, inject, signal, OnInit, computed } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MemberStore } from '../../stores/member';
import { LessonStore } from '../../stores/lesson';
import { CompetitionStore } from '../../stores/competition';
import { UserStore } from '../../stores/user';
import { Router } from '@angular/router';

@Component({
  selector: 'app-teacher',
  imports: [FormsModule],
  templateUrl: './teacher.html',
  styleUrl: './teacher.css'
})
export class Teacher implements OnInit {
  readonly memberStore = inject(MemberStore);
  readonly lessonStore = inject(LessonStore);
  readonly competitionStore = inject(CompetitionStore);
  readonly store = inject(UserStore);
  private readonly router = inject(Router);

  // Computed states combining stores
  readonly loading = computed(() => this.memberStore.loading() || this.lessonStore.loading() || this.competitionStore.loading());
  readonly error = computed(() => this.memberStore.error() || this.lessonStore.error() || this.competitionStore.error());
  readonly success = computed(() => this.memberStore.success() || this.lessonStore.success() || this.competitionStore.success());

  // Resolve active teacher's internal ID reactively
  readonly teacherId = computed(() => {
    const activeUser = this.memberStore.members().find(m => m.username === this.store.username());
    return activeUser ? activeUser.id : 'teacher';
  });

  // Filter lists based on store states
  readonly students = computed(() => this.memberStore.students());
  readonly lessons = computed(() => this.lessonStore.lessons().filter(l => l.teacherId === this.teacherId()));
  readonly competitions = computed(() => this.competitionStore.competitions());

  // Lesson Form Fields
  lessonTitle = '';
  lessonLevel = 1;
  lessonDay = 'MONDAY';
  lessonTime = '18:00 - 19:30';
  lessonDuration = 90;
  lessonLocation = 'Studio Alpha';

  // Competition Form Fields
  compTitle = '';
  compLevel = 1;
  compDate = '2026-06-15T10:00';
  compLocation = 'Club Arena';

  // Grade Form Fields
  selectedCompId = '';
  selectedStudentId = '';
  studentScore = 15.0;

  ngOnInit() {
    if (this.store.role() !== 'TEACHER') {
      this.router.navigate(['/dashboard']);
      return;
    }
    this.loadData();
  }

  loadData() {
    this.memberStore.loadMembers();
    this.lessonStore.loadLessons();
    this.competitionStore.loadCompetitions();
  }

  onCreateLesson() {
    if (!this.lessonTitle.trim()) {
      this.lessonStore.setError('Course title is required.');
      return;
    }

    const payload = {
      title: this.lessonTitle,
      targetLevel: Number(this.lessonLevel),
      duration: Number(this.lessonDuration),
      teacherId: this.teacherId(),
      location: this.lessonLocation,
      dateTime: new Date().toISOString().substring(0, 19) // default datetime slot
    };

    this.lessonStore.createLesson(payload);
    this.lessonTitle = '';
  }

  onCreateCompetition() {
    if (!this.compTitle.trim()) {
      this.competitionStore.setError('Competition title is required.');
      return;
    }

    const payload = {
      title: this.compTitle,
      targetLevel: Number(this.compLevel),
      dateTime: this.compDate + ':00', // pad seconds for backend LocalDateTime parser
      duration: 120, // default competition duration to satisfy @Min(1) validator
      location: this.compLocation,
      teacherId: this.teacherId()
    };

    this.competitionStore.createCompetition(payload);
    this.compTitle = '';
  }

  onSubmitScore() {
    if (!this.selectedCompId || !this.selectedStudentId) {
      this.competitionStore.setError('Please select both competition and student.');
      return;
    }

    const scoreNum = Number(this.studentScore);
    if (isNaN(scoreNum) || scoreNum < 0 || scoreNum > 20) {
      this.competitionStore.setError('Score must be a number between 0.0 and 20.0.');
      return;
    }

    this.competitionStore.submitScore(this.selectedCompId, this.selectedStudentId, {
      score: scoreNum,
      teacherId: this.teacherId()
    }, () => {
      this.selectedStudentId = '';
    });
  }
}
