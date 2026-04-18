import {Injectable} from '@angular/core';
import {
  ActivatedRouteSnapshot,
  CanActivate, Router,
  RouterStateSnapshot, UrlTree
} from '@angular/router';
import {LocalStorageService} from '../services/local-storage.service';
import {environment} from '../../environments/environment';
import {Observable, of} from 'rxjs';
import {AuthService} from '../services/auth.service';

@Injectable({ providedIn: 'root' })
export class AuthGuard implements CanActivate {
  private api: string = environment.apiGateway

  constructor(
    private readonly authService: AuthService,
    private readonly localStorageService: LocalStorageService,
    private readonly router: Router
  ) {}

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean | UrlTree> {
    const page = route.data['page'];

    const tokenExists = this.localStorageService.isUserLoggedIn()

    if (page === 'login' || page === 'register') {
      if (tokenExists) {
        this.localStorageService.removeUserAuth();
        return of(this.router.parseUrl(''));
      } else {
        return of(true);
      }
    }

    return this.authService.fetchPage(page);
  }
}
