import { ColDef } from 'ag-grid-community';
import {PatientSummary} from '../../common/dtos/patient.dto';

export const patientsColumnDefs: ColDef<PatientSummary>[] = [
  {
    field: 'id',
    cellDataType: 'text',
  },
  {
    field: 'firstName',
    cellDataType: 'text',
  },
  {
    field: 'lastName',
    cellDataType: 'text',
  },
  {
    field: 'email',
    cellDataType: 'text',
  },
  {
    field: 'password',
    cellDataType: 'text',
  },
  {
    field: 'pin',
    headerName: 'PIN',
    cellDataType: 'text',
  },
  {
    field: 'gpId',
    headerName: 'General Practitioner Id',
    cellDataType: 'text',
  },
  {
    field: 'insured',
    headerName: 'Is Insured',
    cellDataType: 'text',
    valueFormatter: params => params.value ? 'Yes' : 'No'
  },
]

export const doctorColumnDefs: ColDef[] = [
  {
    field: 'id',
    cellDataType: 'text',
  },
  {
    field: 'firstName',
    cellDataType: 'text',
  },
  {
    field: 'lastName',
    cellDataType: 'text',
  },
  {
    field: 'email',
    cellDataType: 'text',
  },
  {
    field: 'password',
    cellDataType: 'text',
  },
  {
    field: 'specialtyId',
    cellDataType: 'text',
  },
  {
    field: 'gp',
    cellDataType: 'text',
    headerName: 'Is General Practitioner',
    valueFormatter: params => params.value ? 'Yes' : 'No'
  },
]

export const specialtyColumnDefs: ColDef[] = [
  {
    field: 'id',
    cellDataType: 'text',
  },
  {
    field: 'name',
    cellDataType: 'text',
  },
  {
    field: 'description',
    cellDataType: 'text',
  },
]

export const appointmentsColumnDefs: ColDef[] = [
  {
    field: 'id',
    cellDataType: 'text',
  },
  {
    field: 'patientId',
    headerName: 'Patient Id',
    cellDataType: 'text',
  },
  {
    field: 'doctorId',
    headerName: 'Doctor Id',
    cellDataType: 'text',
  },
  {
    field: 'date',
    cellDataType: 'text',
  },
  {
    field: 'time',
    cellDataType: 'text',
  },
  {
    field: 'status',
    cellDataType: 'text',
  },
  {
    field: 'diagnosisId',
    headerName: 'Diagnosis Id',
    cellDataType: 'text',
    valueFormatter: params => params.value ? params.value : 'NO DIAGNOSIS'
  }
]

export const diagnosisColumnDefs: ColDef[] = [
  {
    field: 'id',
    cellDataType: 'text',
  },
  {
    field: 'treatmentDescription',
    headerName: 'Treatment Description',
    cellDataType: 'text',
  },
  {
    field: 'icdId',
    headerName: 'ICD Id',
    cellDataType: 'text',
  },
  {
    field: 'sickLeaveId',
    headerName: 'Sick Leave Id',
    cellDataType: 'text',
    valueFormatter: params => params.value ? params.value : 'NO SICK LEAVE'
  }
]

export const icdColumnDefs: ColDef[] = [
  {
    field: 'id',
    cellDataType: 'text',
  },
  {
    field: 'code',
    cellDataType: 'text',
  },
  {
    field: 'description',
    cellDataType: 'text',
  },
]

export const sickLeaveColumnDefs: ColDef[] = [
  {
    field: 'id',
    cellDataType: 'text',
  },
  {
    field: 'date',
    headerName: 'First Day of Leave',
    cellDataType: 'text',
  },
  {
    field: 'numberOfDays',
    headerName: 'Number of Days of Leave',
    cellDataType: 'text',
  },
]
