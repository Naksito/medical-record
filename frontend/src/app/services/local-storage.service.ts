import {Injectable} from '@angular/core';
import {AuthResponse} from '../common/interfaces/auth.response';
import {first} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class LocalStorageService {
  constructor() {}

  public setUserAuth(userAuth: AuthResponse): void {
    localStorage.setItem('token', userAuth.token);
    localStorage.setItem('id', userAuth.id.toString())
    localStorage.setItem('email', userAuth.email);
    localStorage.setItem('firstName', userAuth.firstName);
    localStorage.setItem('lastName', userAuth.lastName);
    localStorage.setItem('role', userAuth.role);
  }

  public isUserLoggedIn(): boolean {
    return localStorage.getItem('token') !== null;
  }

  public getUserToken(): string | null {
    return localStorage.getItem('token')
  }

  public getUserRole(): string | null {
    return localStorage.getItem('role')
  }

  public getCurrentUserId(): number {
    return Number(localStorage.getItem('id'));
  }

  public removeUserAuth(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('role');
    localStorage.removeItem('id');
    localStorage.removeItem('email');
    localStorage.removeItem('firstName');
    localStorage.removeItem('lastName');
  }

  public getCurrentUser(): string | null {
    return `${localStorage.getItem('firstName')} ${localStorage.getItem('lastName') }`;
  }
}
