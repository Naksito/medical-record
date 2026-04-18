import {DiagnosisDto} from './diagnosis.dto';
import {PatientDto} from './patient.dto';
import {DoctorDto} from './doctor.dto';

export interface AppointmentDto {
  id: number,
  date: string,
  time: string,
  patientId: number,
  doctorId: number,
  status: string,
  diagnosis: DiagnosisDto | null,
}

export interface AppointmentDetailedDto {
  id: number,
  date: string,
  time: string,
  patient: PatientDto | null,
  doctor: DoctorDto | null,
  status: string,
  diagnosis: DiagnosisDto | null,
  performedByGp: boolean
}

export interface AppointmentSummary {
  id: number,
  date: string,
  time: string,
  patientId: number,
  doctorId: number,
  status: string,
  diagnosisId: number | null,
}

export interface CreateAppointmentDto {
  date: string,
  time: string,
  patientId: number,
  doctorId: number,
}

export interface UpdateAppointmentDto {
  id: number,
  status: string | null,
  diagnosisId: number | null,
}

export interface AppointmentStatus {
  status: 'upcoming' | 'started' | 'finished'
}

export interface DoctorAppointmentsCount {
  doctorId: number,
  appointmentCount: number
}

export interface AppointmentsByPatientDto {
  patientId: number,
  appointments: AppointmentDto[]
}
