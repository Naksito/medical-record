import {Component} from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {TextInputComponent} from "../../components/text-input/text-input.component";
import {NgIf} from '@angular/common';
import {RadioComponent} from "../../components/radio/radio.component";
import {AuthService} from '../../services/auth.service';
import {AuthResponse} from '../../common/interfaces/auth.response';
import {LocalStorageService} from '../../services/local-storage.service';
import {Router} from '@angular/router';

@Component({
  selector: 'app-log-in',
  imports: [
    TextInputComponent,
    ReactiveFormsModule,
    RadioComponent,
    NgIf,
    RadioComponent
  ],
  templateUrl: './log-in.component.html',
  styleUrl: './log-in.component.css',
})
export class LogInComponent implements ReactiveFormsModule {
  loginForm: FormGroup;
  label: string = 'Log In';

  logInError: string = '';
  logInSuccess: string = '';

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private localStorageService: LocalStorageService,
    private router: Router,
  ) {
    this.loginForm = this.fb.group({
      email: [''],
      password: [''],
      userType: ['patient']
    });

    this.loginForm.get('userType')?.valueChanges.subscribe(userType => {
      this.updateValidation();
    })

    this.updateValidation();
  }

  updateValidation(): void {
    const userType = this.loginForm.get('userType')?.value;
    if (userType !== 'admin') {
      this.loginForm.get('email')?.setValidators([Validators.required, Validators.email]);
      this.loginForm.get('password')?.setValidators([Validators.required, Validators.minLength(6)]);
    } else {
      this.loginForm.get('email')?.clearValidators();
      this.loginForm.get('password')?.clearValidators();
    }
    this.loginForm.get('email')?.updateValueAndValidity();
  }

  onSubmit(): void {
    if (this.loginForm.valid) {
      const userType: 'patient' | 'doctor' | 'admin' = this.loginForm.value?.userType;
      const email: string = this.loginForm.value?.email;
      const password: string = this.loginForm.value?.password;
      this.authService.logIn(userType, email, password)
        .subscribe({
          next: (auth: AuthResponse) => {
            this.localStorageService.setUserAuth(auth);
            this.logInSuccess = 'Logged in successfully! Redirecting to home page...';
            this.logInError = '';
            setTimeout(() => {
              this.router.navigate(['/']).catch(err => {console.log(err);});
            }, 1000);
          },
          error: err => {
            console.log('Error occurred: ', err)
            this.logInError = err.error.message;
          }
      });
    }
  }
}
