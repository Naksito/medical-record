import {Component} from '@angular/core';
import {RouterLink} from '@angular/router';
import {QueryType} from '../../common/util/util';
import {DropdownComponent} from '../../components/dropdown/dropdown.component';
import {ReactiveFormsModule} from '@angular/forms';
import {PatientByIdQueryComponent} from '../../query-components/patient-by-id-query/patient-by-id-query.component';
import {
  CountPatientsGpDoctorsQueryComponent
} from '../../query-components/count-patients-gp-doctors-query/count-patients-gp-doctors-query.component';
import {
  PatientsByIcdQueryComponent
} from '../../query-components/patients-by-icd-query/patients-by-icd-query.component';
import {IcdOccurrenceQueryComponent} from '../../query-components/icd-occurrence-query/icd-occurrence-query.component';
import {
  DoctorAppointmentsCountQueryComponent
} from '../../query-components/doctor-appointments-count-query/doctor-appointments-count-query.component';
import {
  ListAppointmentsByPatientsQueryComponent
} from '../../query-components/list-appointments-by-patients-query/list-appointments-by-patients-query.component';
import {
  ListAppointmentsByTimePeriodQueryComponent
} from '../../query-components/list-appointments-by-time-period-query/list-appointments-by-time-period-query.component';
import {
  ListAppointmentsByTimePeriodAndDoctorQueryComponent
} from '../../query-components/list-appointments-by-time-period-and-doctor-query/list-appointments-by-time-period-and-doctor-query.component';
import {
  MostSickLeavesMonthQueryComponent
} from '../../query-components/most-sick-leaves-month-query/most-sick-leaves-month-query.component';
import {
  MostSickLeavesDoctorQueryComponent
} from '../../query-components/most-sick-leaves-doctor-query/most-sick-leaves-doctor-query.component';
import {AuthService} from '../../services/auth.service';

@Component({
  selector: 'app-admin-query',
  imports: [
    RouterLink,
    DropdownComponent,
    ReactiveFormsModule,
    PatientByIdQueryComponent,
    CountPatientsGpDoctorsQueryComponent,
    PatientsByIcdQueryComponent,
    IcdOccurrenceQueryComponent,
    DoctorAppointmentsCountQueryComponent,
    ListAppointmentsByPatientsQueryComponent,
    ListAppointmentsByTimePeriodQueryComponent,
    ListAppointmentsByTimePeriodAndDoctorQueryComponent,
    MostSickLeavesMonthQueryComponent,
    MostSickLeavesDoctorQueryComponent
  ],
  templateUrl: './admin-query.component.html',
  styleUrl: './admin-query.component.css'
})
export class AdminQueryComponent {
  queryOptions: QueryType[] = [
    'Patients By GP',
    'Occurrence Of ICD Diagnoses',
    'Patients By ICD Diagnosis',
    'Patients Count For All GP Doctors',
    'Doctor Appointments Count',
    'List Appointments By Patients',
    'List Appointments By Time Period',
    'List Appointments By Time Period And Doctor',
    'Most Sick Leaves - Month',
    'Most Sick Leaves - Doctor'
  ];
  selectedQuery: QueryType | null = null;

  selectQuery(value: QueryType) {
    this.selectedQuery = value;
  }

  constructor() {}
}
