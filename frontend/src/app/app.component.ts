import {Component} from '@angular/core';
import {NgIf} from '@angular/common';
import {ReactiveFormsModule} from '@angular/forms';
import {RouterOutlet} from '@angular/router';
import {AgGridModule} from 'ag-grid-angular';
import {StatusService, ServiceVersions} from './services/status.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    RouterOutlet,
    NgIf,
    ReactiveFormsModule,
    AgGridModule
  ],
  template: `
    <div class="app-container">
        <router-outlet></router-outlet>
        <footer class="version-bar" *ngIf="versions">
          <span>API v{{ versions.api }}</span>
          <span>Auth v{{ versions.auth }}</span>
          <span>Users v{{ versions.users }}</span>
          <span>Appointments v{{ versions.appointments }}</span>
          <span>Frontend v{{ versions.frontend }}</span>
        </footer>
    </div>
  `,
  styles: [`
    .version-bar {
      display: flex;
      flex-wrap: wrap;
      gap: 1rem;
      justify-content: center;
      padding: 0.75rem 1rem;
      color: #6c757d;
      font-family: monospace;
      font-size: 0.8rem;
    }
  `]
})
export class AppComponent {
  title = 'Medical Records App';
  versions: ServiceVersions | null = null;

  constructor(private readonly statusService: StatusService) {
    this.statusService.getVersions()
      .then(versions => this.versions = versions)
      .catch(() => this.versions = null);
  }
}
