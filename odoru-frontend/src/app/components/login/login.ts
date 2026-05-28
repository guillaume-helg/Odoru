import { Component, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../services/auth';
import { MemberStore } from '../../stores/member';
import { UserStore } from '../../stores/user';

@Component({
  selector: 'app-login',
  imports: [FormsModule],
  templateUrl: './login.html',
  styleUrl: './login.css'
})
export class Login {
  private readonly auth = inject(AuthService);
  private readonly memberStore = inject(MemberStore);
  private readonly router = inject(Router);
  private readonly store = inject(UserStore);

  // Toggle state between 'signin' and 'signup'
  readonly mode = signal<'signin' | 'signup'>('signin');

  // Signin fields
  signinUsername = '';
  signinPassword = '';

  // Signup fields
  signupUsername = '';
  signupEmail = '';
  signupFirstName = '';
  signupLastName = '';
  signupExpertiseLevel = 1;
  signupStreet = '';
  signupCity = '';
  signupPostalCode = '';

  // Feedback states
  readonly errorMsg = signal<string | null>(null);
  readonly successMsg = signal<string | null>(null);
  readonly loading = signal(false);

  constructor() {
    // Redirect if already authenticated
    if (this.store.isAuthenticated()) {
      this.router.navigate(['/dashboard']);
    }
  }

  toggleMode() {
    this.mode.update(m => m === 'signin' ? 'signup' : 'signin');
    this.errorMsg.set(null);
    this.successMsg.set(null);
  }

  handleSignin() {
    if (!this.signinUsername || !this.signinPassword) {
      this.errorMsg.set('Please enter both username and password.');
      return;
    }

    this.loading.set(true);
    this.errorMsg.set(null);

    this.auth.login(this.signinUsername, this.signinPassword).subscribe({
      next: () => {
        this.loading.set(false);
        this.router.navigate(['/dashboard']);
      },
      error: (err) => {
        this.loading.set(false);
        console.error(err);
        if (err.status === 401) {
          this.errorMsg.set('Invalid credentials or account setup required.');
        } else {
          this.errorMsg.set('Failed to connect to Keycloak. Make sure containers are running.');
        }
      }
    });
  }

  handleSignup() {
    if (!this.signupUsername || !this.signupEmail || !this.signupFirstName || !this.signupLastName) {
      this.errorMsg.set('Please fill out all required fields.');
      return;
    }

    this.loading.set(true);
    this.errorMsg.set(null);
    this.successMsg.set(null);

    const payload = {
      username: this.signupUsername,
      email: this.signupEmail,
      firstName: this.signupFirstName,
      lastName: this.signupLastName,
      expertiseLevel: Number(this.signupExpertiseLevel),
      address: {
        street: this.signupStreet,
        city: this.signupCity,
        postalCode: this.signupPostalCode
      }
    };

    this.memberStore.signup(payload).subscribe({
      next: () => {
        this.loading.set(false);
        this.successMsg.set('Registration submitted successfully! Wait for validation from Secretary.');
        // Reset signup form
        this.signupUsername = '';
        this.signupEmail = '';
        this.signupFirstName = '';
        this.signupLastName = '';
        this.signupStreet = '';
        this.signupCity = '';
        this.signupPostalCode = '';
        // Shift back to sign in
        setTimeout(() => {
          this.mode.set('signin');
          this.successMsg.set(null);
        }, 3000);
      },
      error: (err) => {
        this.loading.set(false);
        console.error(err);
        this.errorMsg.set(err.error?.message || 'Failed to sign up. Username or email may already exist.');
      }
    });
  }

  quickLogin(role: string) {
    this.signinUsername = role.toLowerCase();
    this.signinPassword = 'admin';
    this.handleSignin();
  }
}
