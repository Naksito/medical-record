import {Component, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {NgIf} from '@angular/common';
import {LocalStorageService} from '../../services/local-storage.service';
import {AuthService} from '../../services/auth.service';
import {StatusService, ServiceVersions} from '../../services/status.service';

@Component({
  selector: 'app-home',
  imports: [
    NgIf
  ],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent implements OnInit {
  currentUser: string = '';
  versions: ServiceVersions | null = null;

  constructor(
    private router: Router,
    private authService: AuthService,
    private localStorageService: LocalStorageService,
    private statusService: StatusService,
  ) {
    const name = this.localStorageService.getCurrentUser();
    this.currentUser = name ? name : 'User';
  }

  ngOnInit(): void {
    this.statusService.getVersions()
      .then(v => this.versions = v)
      .catch(() => this.versions = null);
  }

  redirectToLogIn(): void {
    this.router.navigate(['/login'])
      .catch(err => {
        console.log(err);
      })
  }

  redirectToRegister(): void {
    this.router.navigate(['/register'])
      .catch(err => {
        console.log(err);
      })
  }

  isUserLogged() {
    return this.localStorageService.isUserLoggedIn()
  }

  isUserAdmin() {
    return this.localStorageService.getUserRole() === 'admin';
  }

  redirectToAdminDashboard() {
    this.router.navigate(['/dashboard'])
      .catch(err => {
        console.log(err);
      })
  }

  redirectToMenu() {
    this.router.navigate(['/menu'])
      .catch(err => {
        console.log(err);
      })
  }

  logOut() {
    this.authService.logOut();
  }
}
