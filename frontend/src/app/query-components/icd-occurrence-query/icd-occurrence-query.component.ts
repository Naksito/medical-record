import { Component } from '@angular/core';
import {QueryRunnerService} from '../../services/query-runner.service';
import {IcdOccurrenceDto} from '../../common/dtos/icd.dto';
import {JsonPipe, NgIf} from '@angular/common';
import {DropdownComponent} from '../../components/dropdown/dropdown.component';
import {FrequencyLimitType} from '../../common/util/util';

@Component({
  selector: 'app-icd-occurrence-query',
  imports: [
    JsonPipe,
    NgIf,
    DropdownComponent
  ],
  templateUrl: './icd-occurrence-query.component.html',
  styleUrl: '../pretty-json.css'
})
export class IcdOccurrenceQueryComponent {
  limitOptions: FrequencyLimitType[] = [{ text: '1', value: 1}, { text: '2', value: 2}, { text: '3', value: 3}, { text: '4', value: 4}, { text: '5', value: 5}];
  selectedLimit  = { text: '1', value: 1};

  success: string = '';
  error: string = '';

  icdOccurrences: IcdOccurrenceDto[] | null = null;

  constructor(
    private readonly queryRunnerService: QueryRunnerService,
  ) {}

  runQuery() {
    this.queryRunnerService.getIcdOccurrences(this.selectedLimit.value).subscribe({
      next: data => {
        this.icdOccurrences = data.body;
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
