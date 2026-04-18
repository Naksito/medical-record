import {ColDef} from 'ag-grid-community';

export const doctorColumnDefs: ColDef[] = [
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
    field: 'specialty',
    cellDataType: 'text',
  },
  {
    field: 'gp',
    headerName: 'Is General Practitioner',
    cellDataType: 'text',
    valueFormatter: params => params.value ? 'Yes' : 'No'
  },
]

export const patientColumnDef: ColDef[] = [
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
    field: 'gpFirstName',
    headerName: 'GP First Name',
    cellDataType: 'text',
  },
  {
    field: 'gpLastName',
    headerName: 'GP Last Name',
    cellDataType: 'text',
  },
  {
    field: 'gpEmail',
    headerName: 'GP Email Address',
    cellDataType: 'text',
  },
  {
    field: 'insured',
    headerName: 'Is Insured',
    cellDataType: 'text',
    valueFormatter: params => params.value ? 'Yes' : 'No'
  },
]
