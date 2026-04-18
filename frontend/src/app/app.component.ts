import {Component} from '@angular/core';
import {ReactiveFormsModule} from '@angular/forms';
import {RouterOutlet} from '@angular/router';
import {AgGridModule} from 'ag-grid-angular';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    RouterOutlet,
    ReactiveFormsModule,
    AgGridModule
  ],
  template: `
    <div class="app-container">
        <router-outlet></router-outlet>
    </div>
  `,
  styles: [``]
})
export class AppComponent {
  title = 'Medical Records App';
}
