import { Component, inject, signal } from '@angular/core';
import { RouterOutlet, RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from './services/auth';
import { UserStore } from './stores/user';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  readonly auth = inject(AuthService);
  readonly store = inject(UserStore);
  
  readonly isSidebarOpen = signal(true);

  toggleSidebar() {
    this.isSidebarOpen.update(val => !val);
  }
}
