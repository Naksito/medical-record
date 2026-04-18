import { Component } from '@angular/core';
import {JsonPipe, NgIf} from '@angular/common';
import {QueryRunnerService} from '../../services/query-runner.service';
import {PatientCount} from '../../common/dtos/queries.dto';

@Component({
  selector: 'app-count-patients-gp-doctors-query',
  imports: [
    JsonPipe,
    NgIf
  ],
  templateUrl: './count-patients-gp-doctors-query.component.html',
  styleUrl: '../pretty-json.css'
})
export class CountPatientsGpDoctorsQueryComponent {
  success: string = '';
  error: string = '';

  patientCount: PatientCount[] | null = null;

  constructor(
    private readonly queryRunnerService: QueryRunnerService,
  ) {}


  runQuery() {
    this.queryRunnerService.countOfPatientsForDoctors().subscribe({
      next: data => {
        this.patientCount = data.body;
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
