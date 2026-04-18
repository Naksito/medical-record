import {Component} from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {UsersService} from '../../services/users.service';
import {NgIf} from '@angular/common';
import {RadioComponent} from "../../components/radio/radio.component";
import {TextInputComponent} from '../../components/text-input/text-input.component';
import {CheckboxComponent} from "../../components/checkbox/checkbox.component";
import {DropdownComponent} from '../../components/dropdown/dropdown.component';
import {DoctorSummary} from '../../common/dtos/doctor.dto';
import {AuthService} from '../../services/auth.service';
import {AuthResponse} from '../../common/interfaces/auth.response';
import {LocalStorageService} from '../../services/local-storage.service';
import {SpecialtyDto} from '../../common/dtos/specialty.dto';
import {Router} from '@angular/router';

@Component({
  selector: 'app-register',
  imports: [
    ReactiveFormsModule,
    NgIf,
    RadioComponent,
    TextInputComponent,
    CheckboxComponent,
    DropdownComponent,
],
  templateUrl: './register.component.html',
  styleUrl: './register.component.css'
})
export class RegisterComponent implements ReactiveFormsModule{
  registerForm: FormGroup;
  label: string = 'Register';
  selectedUserType: 'patient' | 'doctor' = 'patient';

  registerError: string = '';
  registerSuccess: string = '';

  specialties: SpecialtyDto[] = []
  selectedSpecialty: SpecialtyDto | null = null;
  specialtyError: string = ''

  gpDoctors: DoctorSummary[] = []
  selectedDoctor: DoctorSummary | null = null;
  gpError: string = '';


  constructor(
    private fb: FormBuilder,
    private usersService: UsersService,
    private authService: AuthService,
    private localStorageService: LocalStorageService,
    private router: Router,
  ) {
    this.getGpDoctors();
    this.getSpecialties();

    this.registerForm = this.fb.group({
      userType: ['patient'],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      firstName: ['', Validators.required],
      lastName: ['', Validators.required],

      // Patient specific
      gpId: [null],
      pin: [null],
      isHealthInsured: [null],

      // Doctor specific
      specialtyId: [null],
      isGp: [null]
    })

    this.registerForm.get('userType')?.valueChanges.subscribe(userType => {
      this.selectedUserType = userType;
      this.updateFormValidation();
    })

    this.updateFormValidation();
  }

  private async getGpDoctors() {
    this.gpDoctors = await this.usersService.getGpDoctors()
      .then(data => {
        return data;
      })
      .catch(error => {
        this.gpError = 'Error retrieving doctors';
        return [];
      });
    this.gpError = '';
  }

  private async getSpecialties() {
    this.specialties = await this.usersService.getAllSpecialties()
      .then(data => {
        return data;
      })
      .catch(error => {
        this.specialtyError = 'Error retrieving specialties';
        return [];
      });
    this.specialtyError = '';
  }

  onDoctorSelected(doctor: DoctorSummary) {
    this.selectedDoctor = doctor;
  }

  onSpecialtySelected(specialty: SpecialtyDto) {
    this.selectedSpecialty = specialty;
  }

  private updateFormValidation(): void {
    const patientFields = ['gpId', 'pin',];
    const doctorFields = ['specialtyId'];

    if (this.selectedUserType === 'patient') {
      patientFields.forEach(field => {
        const control = this.registerForm.get(field);
        if (control) {
          if (field === 'pin') {
            control.setValidators([Validators.required, Validators.pattern('[0-9]{10}')]);
            control.updateValueAndValidity();
          } else {
            control.setValidators([Validators.required]);
            control.updateValueAndValidity();
          }
        }
      });

      doctorFields.forEach(field => {
        const control = this.registerForm.get(field);
        if (control) {
          control.clearValidators();
          control.updateValueAndValidity();
          control.setValue(null);
        }
      });
    } else {
      doctorFields.forEach(field => {
        const control = this.registerForm.get(field);
        if (control) {
          control.setValidators([Validators.required]);
          control.updateValueAndValidity();
        }
      });

      patientFields.forEach(field => {
        const control = this.registerForm.get(field);
        if (control) {
          control.clearValidators();
          control.updateValueAndValidity();
          control.setValue(null);
        }
      });
    }
  }

  onSubmit(): void {
    if (this.registerForm.valid) {
      const formValue = this.registerForm.value;

      switch (formValue.userType) {
        case 'patient': {
          this.authService.registerPatient(
            formValue.firstName,
            formValue.lastName,
            formValue.email,
            formValue.password,
            formValue.pin,
            formValue.gpId,
            formValue.isHealthInsured
          ).subscribe({
            next: (authResponse: AuthResponse) => {
              this.localStorageService.setUserAuth(authResponse);
              this.registerSuccess = 'Registered successfully! Redirecting to home page...';
              this.registerError = '';
              setTimeout(() => {
                this.router.navigate(['/']).catch(err => {console.log(err);});
              }, 1000);
            },
            error: (error) => {
              this.registerError = error.error.message;
              this.registerSuccess = '';
            }
          });
          break;
        }
        case 'doctor': {
          this.authService.registerDoctor(
            formValue.firstName,
            formValue.lastName,
            formValue.email,
            formValue.password,
            formValue.isGp,
            formValue.specialtyId
          ).subscribe({
            next: (authResponse: AuthResponse) => {
              this.localStorageService.setUserAuth(authResponse);
              this.registerSuccess = 'Registered successfully! Redirecting to home page...';
              this.registerError = '';
              setTimeout(() => {
                this.router.navigate(['/']).catch(err => {console.log(err);});
              }, 1000);
            },
            error: (error) => {
              this.registerError = error.error.message;
              this.registerSuccess = '';
            }
          });
          break;
        }
      }
    }
  }
}

