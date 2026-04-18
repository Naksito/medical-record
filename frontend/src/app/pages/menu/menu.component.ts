import { Component } from '@angular/core';
import {RouterLink} from '@angular/router';
import {NgIf} from '@angular/common';
import {LocalStorageService} from '../../services/local-storage.service';
import {PatientMenuComponent} from '../../components/patient-menu/patient-menu.component';
import {DoctorMenuComponent} from '../../components/doctor-menu/doctor-menu.component';

@Component({
  selector: 'app-menu',
  imports: [
    RouterLink,
    NgIf,
    PatientMenuComponent,
    DoctorMenuComponent
  ],
  templateUrl: './menu.component.html',
  styleUrl: './menu.component.css'
})
export class MenuComponent {
  constructor(
    private localStorageService: LocalStorageService,
  ) {}

  protected getUserRole() {
    return this.localStorageService.getUserRole();
  }
}
