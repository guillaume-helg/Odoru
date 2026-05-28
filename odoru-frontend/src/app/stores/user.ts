import { signalStore, withState, withComputed, withMethods, patchState } from '@ngrx/signals';
import { computed } from '@angular/core';

export interface UserState {
  token: string | null;
  username: string | null;
}

const initialState: UserState = {
  token: localStorage.getItem('odoru_token'),
  username: localStorage.getItem('odoru_username'),
};

export const UserStore = signalStore(
  { providedIn: 'root' },
  withState(initialState),
  withComputed((store) => {
    const decodedToken = computed(() => {
      const currentToken = store.token();
      if (!currentToken) return null;
      return parseJwt(currentToken);
    });

    const role = computed<string | null>(() => {
      const decoded = decodedToken();
      if (!decoded) return null;
      const roles = decoded.realm_access?.roles || [];
      if (roles.includes('PRESIDENT')) return 'PRESIDENT';
      if (roles.includes('SECRETARY')) return 'SECRETARY';
      if (roles.includes('TEACHER')) return 'TEACHER';
      if (roles.includes('STUDENT')) return 'STUDENT';
      return null;
    });

    const isAuthenticated = computed(() => !!store.token());

    return {
      decodedToken,
      role,
      isAuthenticated,
    };
  }),
  withMethods((store) => ({
    setCredentials(token: string, username: string) {
      localStorage.setItem('odoru_token', token);
      localStorage.setItem('odoru_username', username);
      patchState(store, { token, username });
    },
    clear() {
      localStorage.removeItem('odoru_token');
      localStorage.removeItem('odoru_username');
      patchState(store, { token: null, username: null });
    }
  }))
);

function parseJwt(token: string): any {
  try {
    const base64Url = token.split('.')[1];
    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
    const jsonPayload = decodeURIComponent(
      window
        .atob(base64)
        .split('')
        .map((c) => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
        .join('')
    );
    return JSON.parse(jsonPayload);
  } catch (e) {
    return null;
  }
}
