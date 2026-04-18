import {DoctorDto} from './doctor.dto';

export interface PatientDto {
  id: number,
  firstName: string,
  lastName: string,
  email: string,
  password: string,
  pin: string,
  gp: DoctorDto,
  insured: boolean
}

export interface DoctorMenuPatientDto {
  firstName: string,
  lastName: string,
  email: string,
  gpFirstName: string,
  gpLastName: string,
  gpEmail: string,
  insured: boolean
}

export interface PatientSummary {
  id: number,
  firstName: string,
  lastName: string,
  email: string,
  password: string,
  pin: string,
  gpId: number,
  insured: boolean
}

export interface RegisterPatientDto {
  firstName: string,
  lastName: string,
  email: string,
  password: string,
  pin: string,
  gpId: number,
  isInsured: boolean
}

export interface UpdatePatientDto {
  id: number,
  firstName: string,
  lastName: string,
  gpId: number,
  isInsured: boolean
}
