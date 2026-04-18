import { Component } from '@angular/core';
import {IcdDto} from '../../common/dtos/icd.dto';
import {PatientDto} from '../../common/dtos/patient.dto';
import {AppointmentsService} from '../../services/appointments.service';
import {QueryRunnerService} from '../../services/query-runner.service';
import {DropdownComponent} from '../../components/dropdown/dropdown.component';
import {JsonPipe, NgIf} from '@angular/common';

@Component({
  selector: 'app-patients-by-icd-query',
  imports: [
    DropdownComponent,
    JsonPipe,
    NgIf
  ],
  templateUrl: './patients-by-icd-query.component.html',
  styleUrl: '../pretty-json.css'
})
export class PatientsByIcdQueryComponent {
  icdEntities: IcdDto[] = [];
  selectedIcd: IcdDto | null = null;
  icdError: string = '';

  patients: PatientDto[] | null = null;

  success: string = '';
  error: string = '';

  constructor(
    private readonly appointmentsService: AppointmentsService,
    private readonly queryRunnerService: QueryRunnerService
  ) {
    this.getIcdEntities();
  }

  runQuery() {
    if (this.selectedIcd === null) {
      this.setError('No ICD selected')
      return
    }

    this.queryRunnerService.getPatientsByIcd(this.selectedIcd.id).subscribe({
      next: value => {
        this.patients = value.body;
        this.setSuccess('Query ran successfully');
      },
      error: err => {
        this.setError('The query run was not successful')
      }
    })
  }

  private getIcdEntities() {
    this.appointmentsService.getAllIcdEntries()
      .then((data: IcdDto[]) => {
        this.icdEntities = data;
        this.icdError = '';
      })
      .catch(error => {
        this.icdError = error.error.message;
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

  onIcdSelected($event: IcdDto) {
    this.selectedIcd = $event;
  }
}
