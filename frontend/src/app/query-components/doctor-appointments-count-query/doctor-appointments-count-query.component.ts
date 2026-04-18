import { Component } from '@angular/core';
import {DoctorAppointmentsCount} from '../../common/dtos/appointment.dto';
import {QueryRunnerService} from '../../services/query-runner.service';
import {JsonPipe, NgIf} from '@angular/common';

@Component({
  selector: 'app-doctor-appointments-count-query',
  imports: [
    JsonPipe,
    NgIf
  ],
  templateUrl: './doctor-appointments-count-query.component.html',
  styleUrl: '../pretty-json.css'
})
export class DoctorAppointmentsCountQueryComponent {
  success: string = '';
  error: string = '';

  doctorAppointmentsCount: DoctorAppointmentsCount[] | null = null;

  constructor(
    private readonly queryRunnerService: QueryRunnerService,
  ) {}

  runQuery() {
    this.queryRunnerService.getDoctorAppointmentsCount().subscribe({
      next: data => {
        this.doctorAppointmentsCount = data.body;
        this.setSuccess('Query ran successfully');
      },
      error: err => {
        this.setError('The query run was not successful');
      }
    })
  }

  private setSuccess(msg: string): void {
    this.success = msg;
    this.error = '';
  }

  private setError(msg: string): void {
    this.error = msg;
    this.success = '';
  }
}
