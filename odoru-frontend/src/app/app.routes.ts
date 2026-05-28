import { Routes } from '@angular/router';

export const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: 'login', loadComponent: () => import('./components/login/login').then((m) => m.Login) },
  { path: 'dashboard', loadComponent: () => import('./components/dashboard/dashboard').then((m) => m.Dashboard) },
  { path: 'president', loadComponent: () => import('./components/president/president').then((m) => m.President) },
  { path: 'secretary', loadComponent: () => import('./components/secretary/secretary').then((m) => m.Secretary) },
  { path: 'teacher', loadComponent: () => import('./components/teacher/teacher').then((m) => m.Teacher) },
  { path: 'student', loadComponent: () => import('./components/student/student').then((m) => m.Student) }
];
