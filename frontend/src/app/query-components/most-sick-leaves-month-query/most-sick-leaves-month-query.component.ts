import { Component } from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {QueryRunnerService} from '../../services/query-runner.service';
import {yearValidator} from '../../common/validators/validators';
import {MostSickLeavesByMonthDto} from '../../common/dtos/sick-leave.dto';
import {JsonPipe, NgIf} from '@angular/common';
import {TextInputComponent} from '../../components/text-input/text-input.component';

@Component({
  selector: 'app-most-sick-leaves-month-query',
  imports: [
    JsonPipe,
    NgIf,
    ReactiveFormsModule,
    TextInputComponent
  ],
  templateUrl: './most-sick-leaves-month-query.component.html',
  styleUrl: '../pretty-json.css'
})
export class MostSickLeavesMonthQueryComponent {
form: FormGroup;

  error: string = '';
  success: string = '';

  monthCount: MostSickLeavesByMonthDto | null = null;

  constructor(
    private fb: FormBuilder,
    private queryRunnerService: QueryRunnerService,
  ) {
    this.form = this.fb.group({
      year: ['', [Validators.required, yearValidator]],
    });
  }

  onSubmit() {
    if (this.form.valid) {
      const formValue = this.form.value;

      const year = formValue.year;

      this.runQuery(year);
    }
  }

  private runQuery(year: string) {
    this.queryRunnerService.getMonthWithMostSickLeaves(year).subscribe({
      next: data => {
        this.monthCount = data.body;
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
