import {SpecialtyDto} from './specialty.dto';

export interface DoctorDto {
  id: number,
  firstName: string,
  lastName: string,
  email: string,
  password: string,
  specialty: SpecialtyDto,
  gp: boolean
}

export interface DoctorMenuDoctorDto {
  firstName: string,
  lastName: string,
  email: string,
  specialty: string,
  gp: boolean
}

export interface DoctorSummary {
  id: number,
  firstName: string,
  lastName: string,
  email: string,
  password: string,
  specialtyId: number
  gp: boolean,
}

export interface RegisterDoctorDto {
  firstName: string,
  lastName: string,
  email: string,
  password: string,
  specialtyId: number,
  generalPractitioner: boolean
}

export interface UpdateDoctorDto {
  id: number,
  firstName: string,
  lastName: string,
  specialtyId: number,
  generalPractitioner: boolean
}

export interface DropdownDoctorDto {
  id: number,
  fullName: string
}
