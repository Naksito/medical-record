import { Component } from '@angular/core';
import {DoctorMenuDoctorDto} from '../../common/dtos/doctor.dto';
import {DoctorMenuPatientDto} from '../../common/dtos/patient.dto';
import {AppointmentDetailedDto} from '../../common/dtos/appointment.dto';
import {LocalStorageService} from '../../services/local-storage.service';
import {GeneralWorkService} from '../../services/general-work.service';
import {EntityType} from '../../common/util/util';
import {NgForOf} from '@angular/common';
import {GridComponent} from '../grid/grid.component';
import {ColDef} from 'ag-grid-community';
import {doctorColumnDefs, patientColumnDef} from './doctor-menu.columns';
import {RouterLink} from '@angular/router';

@Component({
  selector: 'app-doctor-menu',
  imports: [
    NgForOf,
    GridComponent,
    RouterLink
  ],
  templateUrl: './doctor-menu.component.html',
  styleUrl: './doctor-menu.component.css'
})
export class DoctorMenuComponent {
  private currentDoctorId: number;

  entityOptions: EntityType[] = ['Patients', 'Doctors'];
  selectedEntity: EntityType | null = null;

  error: string = '';

  selectEntity(entity: EntityType) {
    this.selectedEntity = entity;
    this.error = '';
  }

  protected doctorColumnDefs: ColDef[] = doctorColumnDefs;
  protected patientsColumnDefs: ColDef[] = patientColumnDef;

  protected doctors: DoctorMenuDoctorDto[] = [];
  protected patients: DoctorMenuPatientDto[] = [];
  protected appointments: AppointmentDetailedDto[] = [];

  constructor(
    private localStorageService: LocalStorageService,
    private generalWorkService: GeneralWorkService,
  ) {
    this.currentDoctorId = this.localStorageService.getCurrentUserId();
    this.getData();
  }

  get currentColumnDefs(): ColDef[] {
    switch (this.selectedEntity) {
      case "Doctors": {
        return this.doctorColumnDefs;
      }
      case "Patients": {
        return this.patientsColumnDefs;
      }
      default: return []
    }
  }

  get currentRowData(): any {
    switch (this.selectedEntity) {
      case "Doctors": {
        return this.doctors;
      }
      case "Patients": {
        return this.patients;
      }
      default: return []
    }
  }

  private getData(): void {
    this.generalWorkService.getDoctorMenuData(this.currentDoctorId).subscribe({
      next: data => {
        this.appointments = data.body?.appointments ? data.body?.appointments : [];

        data.body?.doctors.forEach(doctor => {
          const dto: DoctorMenuDoctorDto = {
            firstName: doctor.firstName,
            lastName: doctor.lastName,
            email: doctor.email,
            specialty: doctor.specialty.name,
            gp: doctor.gp
          };

          this.doctors.push(dto);
        })


        data.body?.patients.forEach(patient => {
          const dto: DoctorMenuPatientDto = {
            firstName: patient.firstName,
            lastName: patient.lastName,
            email: patient.email,
            gpFirstName: patient.gp.firstName,
            gpLastName: patient.gp.lastName,
            gpEmail: patient.gp.email,
            insured: patient.insured
          };

          this.patients.push(dto);
        })
      },
      error: err => {
        console.log(err)
      }
    })
  }
}
