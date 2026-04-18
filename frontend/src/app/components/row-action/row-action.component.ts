import {Component, Input, OnChanges} from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {DropdownComponent} from '../dropdown/dropdown.component';
import {NgIf} from '@angular/common';
import {TextInputComponent} from '../text-input/text-input.component';
import {CheckboxComponent} from '../checkbox/checkbox.component';
import {UsersService} from '../../services/users.service';
import {RegisterPatientDto, UpdatePatientDto} from '../../common/dtos/patient.dto';
import {AuthService} from '../../services/auth.service';
import {RegisterDoctorDto, UpdateDoctorDto} from '../../common/dtos/doctor.dto';
import {CreateSpecialtyDto, UpdateSpecialtyDto} from '../../common/dtos/specialty.dto';
import {isoDateValidator, optionalPairValidator, timeValidator} from '../../common/validators/validators';
import {AppointmentStatus, CreateAppointmentDto, UpdateAppointmentDto} from '../../common/dtos/appointment.dto';
import {AppointmentsService} from '../../services/appointments.service';
import {CreateDiagnosisDto, UpdateDiagnosisDto} from '../../common/dtos/diagnosis.dto';
import {CreateIcdDto, UpdateIcdDto} from '../../common/dtos/icd.dto';
import {CreateSickLeaveDto, UpdateSickLeaveDto} from '../../common/dtos/sick-leave.dto';

interface Action {
  name: 'Create' | 'Update' | 'Delete';
}

@Component({
  selector: 'app-row-action',
  imports: [
    ReactiveFormsModule,
    DropdownComponent,
    NgIf,
    TextInputComponent,
    CheckboxComponent
  ],
  templateUrl: './row-action.component.html',
  styleUrl: './row-action.component.css'
})
export class RowActionComponent implements ReactiveFormsModule, OnChanges {
  rowActionForm: FormGroup;

  @Input() selectedEntity: string = '';

  @Input() entitiesList: any[] = [];

  appointmentStatusList: AppointmentStatus[] = [
    { status: 'upcoming' },
    { status: 'started' },
    { status: 'finished' }
  ];

  rowActionError: string = '';
  rowActionSuccess: string = '';

  actions: Action[] = [
    { name: 'Create' },
    { name: 'Update' },
    { name: 'Delete' }
  ];
  selectedAction: Action =  { name: 'Create' };

  private readonly createPatientFields: string[] = ['firstName', 'lastName', 'email', 'password', 'pin', 'gpId'];
  private readonly createDoctorFields: string[] = ['firstName', 'lastName', 'email', 'password', 'specialtyId'];
  private readonly createSpecialtyFields: string[] = ['specialtyName', 'specialtyDescription'];
  private readonly createAppointmentFields: string[] = ['date', 'time', 'doctorId', 'patientId'];
  private readonly createIcdFields: string[] = ['icdCode', 'icdDescription'];
  private readonly createSickLeaveFields: string[] = ['sickLeaveDate', 'numberOfDays'];
  private readonly createDiagnosisFields: string[] = ['treatmentDescription', 'icdId'];

  private readonly updateAppointmentFields: string[] = ['entityId'];

  private readonly entityIdField: string[] = ['entityId'];

  private readonly allFields: string[] = [
    'entityId',
    'email',
    'password',
    'firstName',
    'lastName',
    'gpId',
    'pin',
    'specialtyId',
    'specialtyName',
    'specialtyDescription',
    'date',
    'time',
    'doctorId',
    'patientId',
    'status',
    'diagnosisId',
    'icdCode',
    'icdDescription',
    'sickLeaveDate',
    'numberOfDays',
    'treatmentDescription',
    'icdId',
    'leaveDate',
    'leaveDays'
  ]

  constructor(
    private formBuilder: FormBuilder,
    private usersService: UsersService,
    private appointmentsService: AppointmentsService,
    private authService: AuthService,
  ) {
    this.rowActionForm = this.formBuilder.group({
      action: [null, [Validators.required]],

      entityId: [null],

      email: [null],
      password: [null],
      firstName: [null],
      lastName: [null],

      gpId: [null],
      pin: [null],
      isHealthInsured: [true],

      specialtyId: [null],
      isGp: [null],

      specialtyName: [null],
      specialtyDescription: [null],

      date: [null],
      time: [null],
      doctorId: [null],
      patientId: [null],
      status: [null],
      diagnosisId: [null],

      icdCode: [null],
      icdDescription: [null],

      sickLeaveDate: [null],
      numberOfDays: [null],

      treatmentDescription: [null],
      icdId: [null],
      leaveDate: [null],
      leaveDays: [null]
    }, {
      validators: [optionalPairValidator]
    });

    this.rowActionForm.get('action')?.valueChanges.subscribe(action => {
      this.updateFormValidation();
    })

    this.updateFormValidation();
  }

  clearLabels() {
    this.rowActionError = '';
    this.rowActionSuccess = '';
  }

  ngOnChanges(): void {
    this.clearLabels();
  }

  onActionSelected($event: Action) {
    this.selectedAction = $event;
  }

  updateFormValidation() {
    const action = this.rowActionForm.get('action')?.value;
    const entity = this.selectedEntity;

    this.resetAllFields();
    if (action === 'Create') {
      switch (entity) {
        case 'Patients': {
          this.setFormValidation(this.createPatientFields);
          break;
        }
        case 'Doctors': {
          this.setFormValidation(this.createDoctorFields);
          break;
        }
        case 'Specialties': {
          this.setFormValidation(this.createSpecialtyFields);
          break;
        }
        case 'Appointments': {
          this.setFormValidation(this.createAppointmentFields);
          break;
        }
        case 'Diagnosis': {
          this.setFormValidation(this.createDiagnosisFields);
          break;
        }
        case 'ICD': {
          this.setFormValidation(this.createIcdFields);
          break;
        }
        case 'Sick Leave': {
          this.setFormValidation(this.createSickLeaveFields);
          break;
        }
      }
    } else {
      if (entity === 'Appointments') {
        this.setFormValidation(this.updateAppointmentFields);
      } else {
        this.setFormValidation(this.entityIdField);
      }
    }
  }

  private setFormValidation(fields: string[]) {
    fields.forEach(field => {
      const control = this.rowActionForm.get(field);
      if (control) {
        switch (field) {
          case 'email': {
            control.setValidators([Validators.required, Validators.email]);
            control.updateValueAndValidity();
            break;
          }
          case 'password': {
            control.setValidators([Validators.required, Validators.minLength(6)]);
            control.updateValueAndValidity();
            break;
          }
          case 'pin': {
            control.setValidators([Validators.required, Validators.pattern('[0-9]{10}')]);
            control.updateValueAndValidity();
            break;
          }
          case 'specialtyId': {
            control.setValidators([Validators.required, Validators.min(1), Validators.pattern("^[0-9]*$")]);
            control.updateValueAndValidity();
            break;
          }
          case 'gpId': {
            control.setValidators([Validators.required, Validators.min(1), Validators.pattern("^[0-9]*$")]);
            control.updateValueAndValidity();
            break;
          }
          case 'date': {
            control.setValidators([Validators.required, isoDateValidator()]);
            control.updateValueAndValidity();
            break;
          }
          case 'time': {
            control.setValidators([Validators.required, timeValidator()]);
            control.updateValueAndValidity();
            break;
          }
          case 'doctorId': {
            control.setValidators([Validators.required, Validators.min(1), Validators.pattern("^[0-9]*$")]);
            control.updateValueAndValidity();
            break;
          }
          case 'patientId': {
            control.setValidators([Validators.required, Validators.min(1), Validators.pattern("^[0-9]*$")]);
            control.updateValueAndValidity();
            break;
          }
          case 'sickLeaveDate': {
            control.setValidators([Validators.required, isoDateValidator()]);
            control.updateValueAndValidity();
            break;
          }
          case 'numberOfDays': {
            control.setValidators([Validators.required, Validators.min(1), Validators.pattern("^[0-9]*$")]);
            control.updateValueAndValidity();
            break;
          }
          default: {
            control.setValidators([Validators.required]);
            control.updateValueAndValidity();
          }
        }
      }
    })
  }

  private resetAllFields(): void {
    this.allFields.forEach(field => {
      const control = this.rowActionForm.get(field);
      if (control) {
        control.clearValidators();
        control.setValue(null);
        control.updateValueAndValidity();
      }
    });
  }

  onSubmit() {
    if (this.rowActionForm.valid) {
      const formValue = this.rowActionForm.value;

      const entityId = formValue.entityId;

      const firstName = formValue.firstName;
      const lastName = formValue.lastName;

      const email = formValue.email;
      const password = formValue.password;

      const pin = formValue.pin;
      const gpId = formValue.gpId;
      const isHealthInsured = formValue.isHealthInsured;
      const isGp = formValue.isGp;
      const specialtyId = formValue.specialtyId;

      const date = formValue.date;
      const time = formValue.time;
      const doctorId = formValue.doctorId;
      const patientId = formValue.patientId;
      const status = formValue.status;
      const diagnosisId = formValue.diagnosisId;

      const treatmentDescription = formValue.treatmentDescription;
      const icdId = formValue.icdId;
      const leaveDate = formValue.leaveDate;
      const leaveDays = formValue.leaveDays;

      const icdCode = formValue.icdCode;
      const icdDescription = formValue.icdDescription;

      const sickLeaveDate = formValue.sickLeaveDate;
      const numberOfDays = formValue.numberOfDays;

      switch (this.selectedEntity) {
        case 'Patients': {
          switch (this.selectedAction.name) {
            case 'Create': {
              const dto: RegisterPatientDto = {
                firstName: firstName,
                lastName: lastName,
                email: email,
                password: password,
                pin: pin,
                gpId: gpId,
                isInsured: isHealthInsured
              }

              this.handlePatientCreate(dto);
              break;
            }

            case 'Update': {
              const dto: UpdatePatientDto = {
                id: entityId,
                firstName: firstName,
                lastName: lastName,
                gpId: gpId,
                isInsured: isHealthInsured
              }

              this.handlePatientUpdate(dto);
              break;
            }

            case 'Delete': {
              this.handlePatientDelete(entityId);
              break;
            }
          }

          break;
        }
        case 'Doctors': {
          switch (this.selectedAction.name) {
            case 'Create': {
              const dto: RegisterDoctorDto = {
                firstName: firstName,
                lastName: lastName,
                email: email,
                password: password,
                generalPractitioner: isGp,
                specialtyId: specialtyId
              }

              this.handleDoctorCreate(dto);
              break;
            }

            case 'Update': {
              const dto: UpdateDoctorDto = {
                id: entityId,
                firstName: firstName,
                lastName: lastName,
                specialtyId: specialtyId,
                generalPractitioner: isGp
              }

              this.handleDoctorUpdate(dto);
              break;
            }

            case 'Delete': {
              this.handleDoctorDelete(entityId);
              break;
            }
          }

          break;
        }

        case 'Specialties': {
          switch (this.selectedAction.name) {
            case 'Create': {
              const dto: CreateSpecialtyDto = {
                name: formValue.specialtyName,
                description: formValue.specialtyDescription
              }

              this.handleCreateSpecialty(dto);
              break;
            }

            case 'Update': {
              const dto: UpdateSpecialtyDto = {
                id: entityId,
                name: formValue.specialtyName,
                description: formValue.specialtyDescription
              }

              this.handleUpdateSpecialty(dto);
              break;
            }

            case 'Delete': {
              this.handleDeleteSpecialty(entityId);
              break;
            }
          }

          break;
        }

        case 'Appointments': {
          switch (this.selectedAction.name) {
            case "Create": {
              const dto: CreateAppointmentDto = {
                date: date,
                time: time,
                patientId: patientId,
                doctorId: doctorId,
              }

              this.handleAppointmentCreate(dto);
              break;
            }

            case "Update": {
              const dto: UpdateAppointmentDto = {
                id: entityId,
                status: status,
                diagnosisId: diagnosisId
              }

              this.handleAppointmentUpdate(dto);
              break;
            }

            case "Delete": {
              this.handleAppointmentDelete(entityId);
              break;
            }
          }
          break;
        }

        case 'Diagnosis': {
          switch (this.selectedAction.name) {
            case "Create": {
              const dto: CreateDiagnosisDto = {
                treatmentDescription: treatmentDescription,
                icdId: icdId,
                sickLeaveDate: leaveDate,
                sickLeaveDays: leaveDays
              }

              this.handleDiagnosisCreate(dto);
              break;
            }

            case "Update": {
              const dto: UpdateDiagnosisDto = {
                id: entityId,
                treatmentDescription: treatmentDescription,
                icdId: icdId,
                sickLeaveDate: leaveDate,
                sickLeaveDays: leaveDays
              }

              this.handleDiagnosisUpdate(dto);
              break;
            }

            case "Delete": {
              this.handleDiagnosisDelete(entityId);
              break;
            }
          }
          break;
        }

        case 'ICD': {
          switch (this.selectedAction.name) {
            case "Create": {
              const dto: CreateIcdDto = {
                code: icdCode,
                description: icdDescription
              }

              this.handleIcdCreate(dto);
              break;
            }

            case "Update": {
              const dto: UpdateIcdDto = {
                id: entityId,
                code: icdCode,
                description: icdDescription
              }

              this.handleIcdUpdate(dto);
              break;
            }

            case "Delete": {
              this.handleIcdDelete(entityId);
              break;
            }
          }
          break;
        }

        case 'Sick Leave': {
          switch (this.selectedAction.name) {
            case "Create": {
              const dto: CreateSickLeaveDto = {
                date: sickLeaveDate,
                numberOfDays: numberOfDays
              }

              this.handleSickLeaveCreate(dto);
              break;
            }

            case "Update": {
              const dto: UpdateSickLeaveDto = {
                id: entityId,
                date: sickLeaveDate,
                numberOfDays: numberOfDays
              }

              this.handleSickLeaveUpdate(dto);
              break;
            }

            case "Delete": {
              this.handleSickLeaveDelete(entityId);
              break;
            }
          }
        }
      }
    }
  }

  private handlePatientCreate(dto: RegisterPatientDto): void {
    this.authService.registerPatient(
      dto.firstName,
      dto.lastName,
      dto.email,
      dto.password,
      dto.pin,
      dto.gpId,
      dto.isInsured
    ).subscribe({
      next: () => {
        this.rowActionSuccess = 'Patient created successfully';
        this.rowActionError = '';
      },
      error: (error) => {
        this.rowActionError = error.error.message;
        this.rowActionSuccess = '';
      }
    })
  }

  private handlePatientUpdate(dto: UpdatePatientDto): void {
    this.usersService.updatePatient(
      dto.id,
      dto.firstName,
      dto.lastName,
      dto.gpId,
      dto.isInsured,
    ).subscribe({
      next: () => {
        this.rowActionSuccess = 'Patient updated successfully';
        this.rowActionError = '';
      },
      error: (error) => {
        this.rowActionError = error.error.message;
        this.rowActionSuccess = '';
      }
    })
  }

  private handlePatientDelete(id: number): void {
    this.usersService.deletePatient(id).subscribe({
      next: value => {
        this.rowActionSuccess = 'Patient deleted successfully';
        this.rowActionError = '';
      },
      error: err => {
        this.rowActionError = err.error.message;
        this.rowActionSuccess = '';
      }
    })
  }

  private handleDoctorCreate(dto: RegisterDoctorDto): void {
    this.authService.registerDoctor(
      dto.firstName,
      dto.lastName,
      dto.email,
      dto.password,
      dto.generalPractitioner,
      dto.specialtyId,
    ).subscribe({
      next: () => {
        this.rowActionSuccess = 'Doctor created successfully';
        this.rowActionError = '';
      },
      error: (error) => {
        this.rowActionError = error.error.message;
        this.rowActionSuccess = '';
      }
    })
  }

  private handleDoctorUpdate(dto: UpdateDoctorDto): void {
    this.usersService.updateDoctor(
      dto.id,
      dto.firstName,
      dto.lastName,
      dto.specialtyId,
      dto.generalPractitioner,
    ).subscribe({
      next: () => {
        this.rowActionSuccess = 'Doctor updated successfully';
        this.rowActionError = '';
      },
      error: (error) => {
        this.rowActionError = error.error.message;
        this.rowActionSuccess = '';
      }
    })
  }

  private handleDoctorDelete(id: number): void {
    this.usersService.deleteDoctor(id).subscribe({
      next: value => {
        this.rowActionSuccess = 'Doctor deleted successfully';
        this.rowActionError = '';
      },
      error: err => {
        this.rowActionError = err.error.message;
        this.rowActionSuccess = '';
      }
    })
  }

  private handleCreateSpecialty(dto: CreateSpecialtyDto): void {
    this.usersService.createSpecialty(dto.name, dto.description).subscribe({
      next: value => {
        this.rowActionSuccess = 'Specialty created successfully';
        this.rowActionError = '';
      },
      error: err => {
        this.rowActionError = err.error.message;
        this.rowActionSuccess = '';
      }
    })
  }

  private handleUpdateSpecialty(dto: UpdateSpecialtyDto): void {
    this.usersService.updateSpecialty(dto.id, dto.name, dto.description).subscribe({
      next: value => {
        this.rowActionSuccess = 'Specialty updated successfully';
        this.rowActionError = '';
      },
      error: err => {
        this.rowActionError = err.error.message;
        this.rowActionSuccess = '';
      }
    })
  }

  private handleDeleteSpecialty(id: number): void {
    this.usersService.deleteSpecialty(id).subscribe({
      next: value => {
        this.rowActionSuccess = 'Specialty deleted successfully';
        this.rowActionError = '';
      },
      error: err => {
        this.rowActionError = err.error.message;
        this.rowActionSuccess = '';
      }
    })
  }

  private handleAppointmentCreate(dto: CreateAppointmentDto): void {
    this.appointmentsService.createAppointment(
      dto.date,
      dto.time,
      dto.patientId,
      dto.doctorId,
    ).subscribe({
      next: value => {
        this.rowActionSuccess = 'Appointment created successfully';
        this.rowActionError = '';
      },
      error: err => {
        this.rowActionError = err.error.message;
        this.rowActionSuccess = '';
      }
    })
  }

  private handleAppointmentUpdate(dto: UpdateAppointmentDto): void {
    this.appointmentsService.updateAppointment(
      dto.id,
      dto.status,
      dto.diagnosisId,
    ).subscribe({
      next: value => {
        this.rowActionSuccess = 'Appointment updated successfully';
        this.rowActionError = '';
      },
      error: err => {
        this.rowActionError = err.error.message;
        this.rowActionSuccess = '';
      }
    })
  }

  private handleAppointmentDelete(id: number): void {
    this.appointmentsService.deleteAppointment(id).subscribe({
      next: value => {
        this.rowActionSuccess = 'Appointment deleted successfully';
        this.rowActionError = '';
      },
      error: err => {
        this.rowActionError = err.error.message;
        this.rowActionSuccess = '';
      }
    })
  }

  private handleDiagnosisCreate(dto: CreateDiagnosisDto): void {
    this.appointmentsService.createDiagnosis(
      dto.treatmentDescription,
      dto.icdId,
      dto.sickLeaveDate,
      dto.sickLeaveDays
    ).subscribe({
      next: value => {
        this.rowActionSuccess = 'Diagnosis created successfully';
        this.rowActionError = '';
      },
      error: err => {
        this.rowActionError = err.error.message;
        this.rowActionSuccess = '';
      }
    })
  }

  private handleDiagnosisUpdate(dto: UpdateDiagnosisDto): void {
    this.appointmentsService.updateDiagnosis(
      dto.id,
      dto.treatmentDescription,
      dto.icdId,
      dto.sickLeaveDate,
      dto.sickLeaveDays
    ).subscribe({
      next: value => {
        this.rowActionSuccess = 'Diagnosis updated successfully';
        this.rowActionError = '';
      },
      error: err => {
        this.rowActionError = err.error.message;
        this.rowActionSuccess = '';
      }
    })
  }

  private handleDiagnosisDelete(id: number): void {
    this.appointmentsService.deleteDiagnosis(id).subscribe({
      next: value => {
        this.rowActionSuccess = 'Diagnosis deleted successfully';
        this.rowActionError = '';
      },
      error: err => {
        this.rowActionError = err.error.message;
        this.rowActionSuccess = '';
      }
    })
  }

  private handleIcdCreate(dto: CreateIcdDto): void {
    this.appointmentsService.createIcd(dto.code, dto.description).subscribe({
      next: value => {
        this.rowActionSuccess = 'ICD created successfully';
        this.rowActionError = '';
      },
      error: err => {
        this.rowActionError = err.error.message;
        this.rowActionSuccess = '';
      }
    })
  }

  private handleIcdUpdate(dto: UpdateIcdDto): void {
    this.appointmentsService.updateIcd(dto.id, dto.code, dto.description).subscribe({
      next: value => {
        this.rowActionSuccess = 'ICD updated successfully';
        this.rowActionError = '';
      },
      error: err => {
        this.rowActionError = err.error.message;
        this.rowActionSuccess = '';
      }
    })
  }

  private handleIcdDelete(id: number): void {
    this.appointmentsService.deleteIcd(id).subscribe({
      next: value => {
        this.rowActionSuccess = 'ICD deleted successfully';
        this.rowActionError = '';
      },
      error: err => {
        this.rowActionError = err.error.message;
        this.rowActionSuccess = '';
      }
    })
  }

  private handleSickLeaveCreate(dto: CreateSickLeaveDto) {
    this.appointmentsService.createSickLeave(dto.date, dto.numberOfDays).subscribe({
      next: value => {
        this.rowActionSuccess = 'Sick Leave created successfully';
        this.rowActionError = '';
      },
      error: err => {
        this.rowActionError = err.error.message;
        this.rowActionSuccess = '';
      }
    })
  }

  private handleSickLeaveUpdate(dto: UpdateSickLeaveDto) {
    this.appointmentsService.updateSickLeave(dto.id, dto.date, dto.numberOfDays).subscribe({
      next: value => {
        this.rowActionSuccess = 'Sick Leave updated successfully';
        this.rowActionError = '';
      },
      error: err => {
        this.rowActionError = err.error.message;
        this.rowActionSuccess = '';
      }
    })
  }

  private handleSickLeaveDelete(id: number) {
    this.appointmentsService.deleteSickLeave(id).subscribe({
      next: value => {
        this.rowActionSuccess = 'Sick Leave updated successfully';
        this.rowActionError = '';
      },
      error: err => {
        this.rowActionError = err.error.message;
        this.rowActionSuccess = '';
      }
    })
  }
}
