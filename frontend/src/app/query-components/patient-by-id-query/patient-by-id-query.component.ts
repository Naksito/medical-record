import { Component } from '@angular/core';
import {DoctorSummary} from '../../common/dtos/doctor.dto';
import {UsersService} from '../../services/users.service';
import {DropdownComponent} from '../../components/dropdown/dropdown.component';
import {ReactiveFormsModule} from '@angular/forms';
import {QueryRunnerService} from '../../services/query-runner.service';
import {PatientDto} from '../../common/dtos/patient.dto';
import {JsonPipe, NgIf} from '@angular/common';

@Component({
  selector: 'app-patient-by-id-query',
  imports: [
    DropdownComponent,
    ReactiveFormsModule,
    NgIf,
    JsonPipe
  ],
  templateUrl: './patient-by-id-query.component.html',
  styleUrl: '../pretty-json.css'
})
export class PatientByIdQueryComponent {
  gpDoctors: DoctorSummary[] = [];
  selectedDoctor: DoctorSummary | null = null;
  gpError: string = '';

  patients: PatientDto[] | null = null;

  success: string = '';
  error: string = '';

  constructor(
    private readonly usersService: UsersService,
    private readonly queryRunnerService: QueryRunnerService
  ) {
    this.getGpDoctors();
  }

  runQuery() {
    if (this.selectedDoctor === null) {
      this.setError('No doctor selected')
      return
    }

    this.queryRunnerService.getAllPatientsByGpId(this.selectedDoctor.id).subscribe({
      next: data => {
        this.patients = data.body;
        this.setSuccess('Query ran successfully')
      },
      error: err => {
        this.setError('The query run was not successful')
      }
    })
  }

  private async getGpDoctors() {
    this.gpDoctors = await this.usersService.getGpDoctors()
      .then(data => {
        this.gpError = '';
        return data;
      })
      .catch(error => {
        this.gpError = 'Error retrieving GP doctors';
        return [];
      });
  }

  private setSuccess(msg: string): void {
    this.success = msg;
    this.error = '';
  }

  private setError(msg: string): void {
    this.error = msg;
    this.success = '';
  }

  onDoctorSelected($event: DoctorSummary) {
    this.selectedDoctor = $event;
  }
}
