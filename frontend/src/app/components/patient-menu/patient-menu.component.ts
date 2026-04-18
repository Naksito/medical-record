import { Component } from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {LocalStorageService} from '../../services/local-storage.service';
import {DoctorDto, DropdownDoctorDto} from '../../common/dtos/doctor.dto';
import {TextInputComponent} from '../text-input/text-input.component';
import {DropdownComponent} from '../dropdown/dropdown.component';
import {NgForOf, NgIf} from '@angular/common';
import {isoDateValidator, timeValidator} from '../../common/validators/validators';
import {AppointmentsService} from '../../services/appointments.service';
import {AppointmentDetailedDto} from '../../common/dtos/appointment.dto';
import {GeneralWorkService} from '../../services/general-work.service';
import {getDoctorDropdownNames} from '../../common/util/util';

@Component({
  selector: 'app-patient-menu',
  imports: [
    ReactiveFormsModule,
    TextInputComponent,
    DropdownComponent,
    NgIf,
    NgForOf
  ],
  templateUrl: './patient-menu.component.html',
  styleUrl: './patient-menu.component.css'
})
export class PatientMenuComponent {
  createAppointmentForm: FormGroup;
  private currentPatientId: number;

  protected doctors: DropdownDoctorDto[] = [];
  doctorsFetchError: string = '';

  appointmentCreateError: string = '';
  appointmentCreateSuccess: string = '';

  protected appointments: AppointmentDetailedDto[] = [];

  constructor(
    private fb: FormBuilder,
    private localStorageService: LocalStorageService,
    private generalWorkService: GeneralWorkService,
    private appointmentsService: AppointmentsService,
  ) {
    this.currentPatientId = this.localStorageService.getCurrentUserId();
    this.getData();
    this.createAppointmentForm = this.fb.group({
      date: [null, [Validators.required, isoDateValidator()]],
      time: [null, [Validators.required, timeValidator()]],
      doctorId: [null, [Validators.required]]
    })
  }

  private getData(): void {
    this.generalWorkService.getPatientMenuData(this.currentPatientId).subscribe({
      next: data => {
        if (data.body?.doctors) {
          this.doctors = getDoctorDropdownNames(data.body.doctors);
        } else {
          this.doctors = [];
        }
        this.appointments = data.body?.appointments ? data.body.appointments : [];
      },
      error: err => {
        console.log(err)
      }
    })
  }

  onSubmit() {
    if (this.createAppointmentForm.valid) {
      const formValue = this.createAppointmentForm.value;

      const date = formValue.date;
      const time = formValue.time;
      const doctorId = formValue.doctorId;

      this.appointmentsService.createAppointment(
        date,
        time,
        this.currentPatientId,
        doctorId
      ).subscribe({
        next: value => {
          this.appointmentCreateSuccess = 'Successfully created new appointment'
          this.appointmentCreateError = '';
        },
        error: err => {
          this.appointmentCreateError = err.error.message;
          this.appointmentCreateSuccess = '';
        }
      })
    }
  }
}
