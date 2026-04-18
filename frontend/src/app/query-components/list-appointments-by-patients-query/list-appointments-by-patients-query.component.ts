import { Component } from '@angular/core';
import {AppointmentsByPatientDto, DoctorAppointmentsCount} from '../../common/dtos/appointment.dto';
import {QueryRunnerService} from '../../services/query-runner.service';
import {JsonPipe, NgIf} from '@angular/common';

@Component({
  selector: 'app-list-appointments-by-patients-query',
  imports: [
    JsonPipe,
    NgIf
  ],
  templateUrl: './list-appointments-by-patients-query.component.html',
  styleUrl: '../pretty-json.css'
})
export class ListAppointmentsByPatientsQueryComponent {
  success: string = '';
  error: string = '';

  appointmentsByPatients: AppointmentsByPatientDto[] | null = null;

  constructor(
    private readonly queryRunnerService: QueryRunnerService,
  ) {}

  runQuery() {
    this.queryRunnerService.getAppointmentsByPatients().subscribe({
      next: data => {
        this.appointmentsByPatients = data.body;
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
