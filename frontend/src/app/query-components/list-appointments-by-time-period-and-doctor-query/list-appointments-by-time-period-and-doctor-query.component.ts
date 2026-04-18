import { Component } from '@angular/core';
import {AppointmentDto} from '../../common/dtos/appointment.dto';
import {QueryRunnerService} from '../../services/query-runner.service';
import {JsonPipe, NgIf} from "@angular/common";
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from "@angular/forms";
import {TextInputComponent} from "../../components/text-input/text-input.component";
import {isoDateValidator} from '../../common/validators/validators';
import {DropdownComponent} from '../../components/dropdown/dropdown.component';
import {DropdownDoctorDto} from '../../common/dtos/doctor.dto';
import {UsersService} from '../../services/users.service';

@Component({
  selector: 'app-list-appointments-by-time-period-and-doctor-query',
  imports: [
    JsonPipe,
    NgIf,
    ReactiveFormsModule,
    TextInputComponent,
    DropdownComponent
  ],
  templateUrl: './list-appointments-by-time-period-and-doctor-query.component.html',
  styleUrl: '../pretty-json.css'
})
export class ListAppointmentsByTimePeriodAndDoctorQueryComponent {
  form: FormGroup;

  success: string = '';
  error: string = '';

  appointments: AppointmentDto[] | null = null;

  doctors: DropdownDoctorDto[] = []
  selectedDoctor: DropdownDoctorDto | null = null;
  doctorError: string = '';

  constructor(
    private fb: FormBuilder,
    private readonly queryRunnerService: QueryRunnerService,
    private readonly usersService: UsersService,
  ) {
    this.fetchDoctors();

    this.form = this.fb.group({
      startDate: ['', [Validators.required, isoDateValidator()]],
      endDate: ['', [Validators.required, isoDateValidator()]],
      doctorId: [null, [Validators.required]]
    });
  }

  runQuery(startDate: string, endDate: string, doctorId: number) {
    this.queryRunnerService.getAppointmentsForTimePeriodByDoctor(startDate, endDate, doctorId).subscribe({
      next: data => {
        this.appointments = data.body;
        this.setSuccess('Query ran successfully');
      },
      error: err => {
        this.setError('The query run was not successful');
      }
    })
  }

  private fetchDoctors(): void {
    this.usersService.getAllDoctors().then(data => {
      const summaryData: DropdownDoctorDto[] = []
      data.forEach(doctor => {
        const doctorSummary: DropdownDoctorDto = {
          id: doctor.id,
          fullName: `${doctor.firstName} ${doctor.lastName}`
        }
        summaryData.push(doctorSummary)
      })

      this.doctors = [...summaryData];
    }).catch(error => {
      this.error = error.error;
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

  onSubmit() {
    if (this.form.valid) {
      const formValue = this.form.value;

      const startDate = formValue.startDate;
      const endDate = formValue.endDate;
      const doctorId = formValue.doctorId;

      this.runQuery(startDate, endDate, doctorId);
    }
  }

  onDoctorSelected($event: DropdownDoctorDto) {
    this.selectedDoctor = $event;
  }
}
