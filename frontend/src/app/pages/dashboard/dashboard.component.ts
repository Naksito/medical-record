import {Component} from '@angular/core';
import { ColDef } from 'ag-grid-community';
import {GridComponent} from '../../components/grid/grid.component';
import {NgForOf, NgIf} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {
  appointmentsColumnDefs,
  diagnosisColumnDefs,
  doctorColumnDefs, icdColumnDefs,
  patientsColumnDefs, sickLeaveColumnDefs,
  specialtyColumnDefs
} from './entity.def';
import {UsersService} from '../../services/users.service';
import { PatientSummary} from '../../common/dtos/patient.dto';
import {DoctorSummary} from '../../common/dtos/doctor.dto';
import {SpecialtyDto} from '../../common/dtos/specialty.dto';
import {RowActionComponent} from '../../components/row-action/row-action.component';
import {AppointmentsService} from '../../services/appointments.service';
import {AppointmentSummary} from '../../common/dtos/appointment.dto';
import {EntityType} from '../../common/util/util';
import {RouterLink} from '@angular/router';
import {DiagnosisSummary} from '../../common/dtos/diagnosis.dto';
import {IcdDto} from '../../common/dtos/icd.dto';
import {SickLeaveDto} from '../../common/dtos/sick-leave.dto';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    GridComponent,
    RowActionComponent,
    NgForOf,
    FormsModule,
    NgIf,
    RouterLink,
  ],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css'
})
export class DashboardComponent {
  entityOptions: EntityType[] = ['Patients', 'Doctors', 'Specialties', 'Appointments', 'Diagnosis', 'ICD', 'Sick Leave'];
  selectedEntity: EntityType = 'Patients';

  error: string = '';

  selectEntity(entity: EntityType) {
    this.selectedEntity = entity;
    this.error = '';
  }

  patientColumnDefs: ColDef[] = patientsColumnDefs;
  doctorColumnDefs: ColDef[] = doctorColumnDefs;
  specialtyColumnDefs: ColDef[] = specialtyColumnDefs;
  appointmentsColumnDefs: ColDef[] = appointmentsColumnDefs;
  diagnosisColumnDefs: ColDef[] = diagnosisColumnDefs;
  icdColumnDefs: ColDef[] = icdColumnDefs;
  sickLeaveColumnDefs: ColDef[] = sickLeaveColumnDefs;


  patientRowData: PatientSummary[] = [];
  doctorRowData: DoctorSummary[] = [];
  specialtyRowData: SpecialtyDto[] = [];
  appointmentsRowData: AppointmentSummary[] = [];
  diagnosisRowData: DiagnosisSummary[] = [];
  icdRowData: IcdDto[] = [];
  sickLeaveRowData: SickLeaveDto[] = [];

  constructor(
    private usersService: UsersService,
    private appointmentsService: AppointmentsService,
  ) {}

  get currentColumnDefs(): ColDef[] {
    switch (this.selectedEntity) {
      case 'Patients':
        return this.patientColumnDefs;
      case 'Doctors':
        return this.doctorColumnDefs;
      case 'Specialties':
        return this.specialtyColumnDefs;
      case 'Appointments':
        return this.appointmentsColumnDefs;
      case 'Diagnosis':
        return this.diagnosisColumnDefs;
      case 'ICD':
        return this.icdColumnDefs;
      case "Sick Leave":
        return this.sickLeaveColumnDefs;
      default:
        return [];
    }
  }

  get currentRowData(): any[] {
    switch (this.selectedEntity) {
      case 'Patients':
        return this.patientRowData;
      case 'Doctors':
        return this.doctorRowData;
      case 'Specialties':
        return this.specialtyRowData;
      case 'Appointments':
        return this.appointmentsRowData;
      case "Diagnosis":
        return this.diagnosisRowData;
      case "ICD":
        return this.icdRowData;
      case "Sick Leave":
        return this.sickLeaveRowData;
      default:
        return [];
    }
  }

  loadGridData() {
    switch (this.selectedEntity) {
      case 'Patients':
        this.handlePatientsFetch();
        break;
      case "Doctors":
        this.handleDoctorsFetch();
        break;
      case "Specialties":
        this.handleSpecialtiesFetch();
        break;
      case "Appointments":
        this.handleAppointmentsFetch();
        break;
      case "Diagnosis":
        this.handleDiagnosisFetch();
        break
      case "ICD":
        this.handleIcdFetch();
        break;
      case "Sick Leave":
        this.handleSickLeaveFetch();
        break;
    }
  }

  private handlePatientsFetch(): void {
    this.usersService.getAllPatients().then(data => {
      const summaryData: PatientSummary[] = []
      data.forEach(patient => {
        const patientSummary: PatientSummary = {
          id: patient.id,
          firstName: patient.firstName,
          lastName: patient.lastName,
          email: patient.email,
          password: patient.password,
          pin: patient.pin,
          gpId: patient.gp.id,
          insured: patient.insured,
        }
        summaryData.push(patientSummary)
      })

      this.patientRowData = [...summaryData];
    }).catch(error => {
      this.error = error.error;
    })
  }

  private handleDoctorsFetch(): void {
    this.usersService.getAllDoctors().then(data => {
      const summaryData: DoctorSummary[] = []
      data.forEach(doctor => {
        const doctorSummary: DoctorSummary = {
          id: doctor.id,
          firstName: doctor.firstName,
          lastName: doctor.lastName,
          email: doctor.email,
          password: doctor.password,
          specialtyId: doctor.specialty.id,
          gp: doctor.gp,
        }
        summaryData.push(doctorSummary)
      })

      this.doctorRowData = [...summaryData];
    }).catch(error => {
      this.error = error.error;
    })
  }

  private handleSpecialtiesFetch(): void {
    this.usersService.getAllSpecialties().then(data => {
      this.specialtyRowData = [...data];
    }).catch(error => {
      this.error = error.error;
    })
  }

  private handleAppointmentsFetch(): void {
    this.appointmentsService.getAllAppointments().then(data => {
      const summaryData: AppointmentSummary[] = []
      data.forEach(appointment => {
        const summaryDto: AppointmentSummary = {
          id: appointment.id,
          date: appointment.date,
          time: appointment.time,
          patientId: appointment.patientId,
          doctorId: appointment.doctorId,
          status: appointment.status,
          diagnosisId: appointment.diagnosis?.id ? appointment.diagnosis.id : null,
        }
        summaryData.push(summaryDto)
      })

      this.appointmentsRowData = [...summaryData];
    }).catch(error => {
      this.error = error.error;
    })
  }

  private handleDiagnosisFetch(): void {
    this.appointmentsService.getAllDiagnoses().then(data => {
      const summaryData: DiagnosisSummary[] = []
      data.forEach(diagnosis => {
        const summaryDto: DiagnosisSummary = {
          id: diagnosis.id,
          treatmentDescription: diagnosis.treatmentDescription,
          icdId: diagnosis.icd.id,
          sickLeaveId: diagnosis.sickLeave?.id,
        }
        summaryData.push(summaryDto)
      })

      this.diagnosisRowData = [...summaryData];
    }).catch(error => {
      this.error = error.error;
    })
  }

  private handleIcdFetch(): void {
    this.appointmentsService.getAllIcdEntries().then(data => {
      this.icdRowData = [...data];
    }).catch(error => {
      this.error = error.error;
    })
  }

  private handleSickLeaveFetch(): void {
    this.appointmentsService.getAllSickLeaveEntities().then(data => {
      this.sickLeaveRowData = [...data];
    }).catch(error => {
      this.error = error.error;
    })
  }
}
