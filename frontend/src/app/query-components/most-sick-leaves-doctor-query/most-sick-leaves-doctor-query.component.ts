import { Component } from '@angular/core';
import {DropdownComponent} from '../../components/dropdown/dropdown.component';
import {JsonPipe, NgIf} from '@angular/common';
import {FrequencyLimitType} from '../../common/util/util';
import {QueryRunnerService} from '../../services/query-runner.service';
import {MostSickLeavesByDoctorDto} from '../../common/dtos/sick-leave.dto';

@Component({
  selector: 'app-most-sick-leaves-doctor-query',
  imports: [
    DropdownComponent,
    JsonPipe,
    NgIf
  ],
  templateUrl: './most-sick-leaves-doctor-query.component.html',
  styleUrl: '../pretty-json.css'
})
export class MostSickLeavesDoctorQueryComponent {
  limitOptions: FrequencyLimitType[] = [{ text: '1', value: 1}, { text: '2', value: 2}, { text: '3', value: 3}, { text: '4', value: 4}, { text: '5', value: 5}];
  selectedLimit  = { text: '1', value: 1};

  success: string = '';
  error: string = '';

  sickLeaves: MostSickLeavesByDoctorDto[] | null = null;

  constructor(
    private readonly queryRunnerService: QueryRunnerService,
  ) {}

  runQuery() {
    this.queryRunnerService.getDoctorsBySickLeaveCount(this.selectedLimit.value).subscribe({
      next: data => {
        this.sickLeaves = data.body;
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

  onLimitChange($event: FrequencyLimitType) {
    this.selectedLimit = $event;
  }
}
