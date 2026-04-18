import {Component} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {AppointmentsService} from '../../services/appointments.service';
import {IcdDto} from '../../common/dtos/icd.dto';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {TextInputComponent} from '../text-input/text-input.component';
import {DropdownComponent} from '../dropdown/dropdown.component';
import {NgIf} from '@angular/common';
import {CheckboxComponent} from '../checkbox/checkbox.component';
import {isoDateValidator} from '../../common/validators/validators';
import {LocalStorageService} from '../../services/local-storage.service';

@Component({
  selector: 'app-update-diagnosis',
  imports: [
    ReactiveFormsModule,
    TextInputComponent,
    DropdownComponent,
    NgIf,
    CheckboxComponent
  ],
  templateUrl: './update-diagnosis.component.html',
  styleUrl: './update-diagnosis.component.css'
})
export class UpdateDiagnosisComponent {
  private appointmentId: number;
  updateDiagnosis: FormGroup;

  protected includeSickLeave: boolean = false;

  protected icdEntities: IcdDto[] = [];
  protected icdFetchError: string = '';

  protected successMsg: string = '';
  protected errorMsg: string = '';

  private sickLeaveFields: string[] = ['sickLeaveDate', 'sickLeaveDays'];

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private appointmentsService: AppointmentsService,
    private localStorageService: LocalStorageService
  ) {
    this.updateDiagnosis = this.fb.group({
      treatmentDescription: [null, [Validators.required]],
      icd: [null, [Validators.required]],

      sickLeaveDate: [null],
      sickLeaveDays: [null],

      includeSickLeave: [false]
    })

    this.updateDiagnosis.get('includeSickLeave')?.valueChanges.subscribe(sickLeave => {
      this.includeSickLeave = sickLeave;
      sickLeave ? this.addSickLeaveValidators() : this.removeSickLeaveValidators()
    })

    const appointmentId = this.route.snapshot.paramMap.get('id');
    if (appointmentId !== null) {
      this.appointmentId = Number(appointmentId);
    } else {
      this.appointmentId = -1;
      this.errorMsg = 'Invalid appointment id';
    }

    if (this.appointmentId !== -1) {
      const doctorId = this.localStorageService.getCurrentUserId();
      this.appointmentsService.startAppointment(this.appointmentId, doctorId).subscribe({
        next: data => {
          const entities = data.body;
          if (entities) {
            this.icdEntities = entities;
          }
        },
        error: err => {
          this.errorMsg = err.error.message;
          this.successMsg = ''
        }
      })
    }
  }

  private addSickLeaveValidators(): void {
    this.sickLeaveFields.forEach(field => {
      const control = this.updateDiagnosis.get(field);
      if (control) {
        switch (field) {
          case 'sickLeaveDate': {
            control.setValidators([Validators.required, isoDateValidator()]);
            control.updateValueAndValidity();
            break;
          }
          case 'sickLeaveDays': {
            control.setValidators([Validators.required, Validators.min(1), Validators.pattern("^[0-9]*$")]);
            control.updateValueAndValidity();
            break;
          }
        }
      }
    })
  }

  private removeSickLeaveValidators(): void {
    this.sickLeaveFields.forEach(field => {
      const control = this.updateDiagnosis.get(field);
      if (control) {
        control.clearValidators();
        control.setValue(null);
        control.updateValueAndValidity();
      }
    })
  }

  onSubmit(): void {
    if (this.updateDiagnosis.valid) {
      const formValue = this.updateDiagnosis.value;

      const treatmentDescription = formValue.treatmentDescription;
      const icdId = formValue.icd;
      const sickLeaveDate = formValue.sickLeaveDate;
      const sickLeaveDays = formValue.sickLeaveDays;

      this.appointmentsService.finishAppointment(
        this.appointmentId,
        treatmentDescription,
        icdId,
        sickLeaveDate,
        sickLeaveDays
      ).subscribe({
        next: data => {
          this.successMsg = 'Successfully finished appointment! Returning to main menu...';
          this.errorMsg = '';
          setTimeout(() => {
              this.router.navigate(['/menu']).catch(err => {console.log(err);});
            }, 1000);
        },
        error: err => {
          this.errorMsg = err.error.message;
          this.successMsg = '';
        }
      })
    }
  }
}
