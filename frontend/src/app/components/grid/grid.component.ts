import {Component, Input, OnInit} from '@angular/core';
import {AgGridAngular } from 'ag-grid-angular';
import {ColDef, GridReadyEvent, GridApi, ModuleRegistry, ClientSideRowModelModule} from 'ag-grid-community';

ModuleRegistry.registerModules([ClientSideRowModelModule]);

@Component({
  selector: 'app-grid',
  standalone: true,
  imports: [AgGridAngular],
  template: `
    <div class="grid-container">
      <ag-grid-angular
        class="ag-theme-alpine"
        style="height: 500px; width: 100%;"
        [columnDefs]="columnDefs"
        [rowData]="rowData"
        [defaultColDef]="defaultColDef"
        [rowSelection]="'single'"
        (gridReady)="onGridReady($event)"
      >
      </ag-grid-angular>
    </div>
  `,
  styles: [`
    :host { display: block; height: 100%; }
    .ag-theme-alpine { height: 100%; width: 100%; }
  `],
})
export class GridComponent implements OnInit {
  @Input() columnDefs: ColDef[] = [];
  @Input() rowData: any[] = [];

  private gridApi!: GridApi;

  defaultColDef: ColDef = {
    sortable: true,
    filter: true,
    resizable: true,
    flex: 1,
    minWidth: 100,
  };

  constructor() {}

  ngOnInit(): void {}

  onGridReady(params: GridReadyEvent) {
    this.gridApi = params.api;
  }
}
