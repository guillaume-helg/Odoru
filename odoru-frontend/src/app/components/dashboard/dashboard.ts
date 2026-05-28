import { Component, inject, OnInit } from '@angular/core';
import { PercentPipe, DecimalPipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth';
import { StatsStore } from '../../stores/stats';
import { UserStore } from '../../stores/user';

@Component({
  selector: 'app-dashboard',
  imports: [RouterLink, PercentPipe, DecimalPipe],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css'
})
export class Dashboard implements OnInit {
  readonly auth = inject(AuthService);
  readonly store = inject(UserStore);
  readonly statsStore = inject(StatsStore);

  ngOnInit() {
    this.loadDashboardData();
  }

  loadDashboardData() {
    const role = this.store.role();
    const username = this.store.username();

    if (role && username) {
      this.statsStore.loadStats(role, username);
    }
  }
}
