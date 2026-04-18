import { Component } from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {AppointmentDto} from '../../common/dtos/appointment.dto';
import {isoDateValidator} from '../../common/validators/validators';
import {JsonPipe, NgIf} from '@angular/common';
import {TextInputComponent} from '../../components/text-input/text-input.component';
import {QueryRunnerService} from '../../services/query-runner.service';

@Component({
  selector: 'app-list-appointments-by-time-period-query',
  imports: [
    JsonPipe,
    NgIf,
    ReactiveFormsModule,
    TextInputComponent
  ],
  templateUrl: './list-appointments-by-time-period-query.component.html',
  styleUrl: '../pretty-json.css'
})
export class ListAppointmentsByTimePeriodQueryComponent {
  form: FormGroup;

  error: string = '';
  success: string = '';

  appointments: AppointmentDto[] | null = null;

  constructor(
    private fb: FormBuilder,
    private queryRunnerService: QueryRunnerService,
  ) {
    this.form = this.fb.group({
      startDate: ['', [Validators.required, isoDateValidator()]],
      endDate: ['', [Validators.required, isoDateValidator()]]
    });
  }

  onSubmit() {
    if (this.form.valid) {
      const formValue = this.form.value;

      const startDate = formValue.startDate;
      const endDate = formValue.endDate;

      this.runQuery(startDate, endDate);
    }
  }

  private runQuery(startDate: string, endDate: string) {
    this.queryRunnerService.getAppointmentsForTimePeriod(startDate, endDate).subscribe({
      next: data => {
        this.appointments = data.body;
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
