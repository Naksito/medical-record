import { Injectable } from '@angular/core';
import {environment} from '../../environments/environment';
import {HttpClient, HttpResponse} from '@angular/common/http';
import {Observable} from 'rxjs';
import {PatientDto} from '../common/dtos/patient.dto';
import {PatientCount} from '../common/dtos/queries.dto';
import {IcdOccurrenceDto} from '../common/dtos/icd.dto';
import {AppointmentDto, AppointmentsByPatientDto, DoctorAppointmentsCount} from '../common/dtos/appointment.dto';
import {MostSickLeavesByDoctorDto, MostSickLeavesByMonthDto} from '../common/dtos/sick-leave.dto';

@Injectable({
  providedIn: 'root'
})
export class QueryRunnerService {
  private readonly apiUrl: string = `${environment.apiGateway}/query`

  constructor(
    private httpClient: HttpClient,
  ) {}

  public getAllPatientsByGpId(gpId: number): Observable<HttpResponse<PatientDto[]>> {
    return this.httpClient.get<PatientDto[]>(`${this.apiUrl}/get-all-patients-for-gp/${gpId}`, {observe: 'response'})
  }

  public countOfPatientsForDoctors(): Observable<HttpResponse<PatientCount[]>> {
    return this.httpClient.get<PatientCount[]>(`${this.apiUrl}/get-patients-count-for-gp-doctors`, {observe: 'response'})
  }

  public getPatientsByIcd(icdId: number): Observable<HttpResponse<PatientDto[]>> {
    return this.httpClient.get<PatientDto[]>(`${this.apiUrl}/get-patients-by-icd/${icdId}`, {observe: 'response'})
  }

  public getIcdOccurrences(limit: number): Observable<HttpResponse<IcdOccurrenceDto[]>> {
    return this.httpClient.get<IcdOccurrenceDto[]>(`${this.apiUrl}/get-most-frequent-icds/${limit}`, {observe: 'response'})
  }

  public getDoctorAppointmentsCount(): Observable<HttpResponse<DoctorAppointmentsCount[]>> {
    return this.httpClient.get<DoctorAppointmentsCount[]>(`${this.apiUrl}/get-doctor-appointments-count`, {observe: 'response'})
  }

  public getAppointmentsByPatients(): Observable<HttpResponse<AppointmentsByPatientDto[]>> {
    return this.httpClient.get<AppointmentsByPatientDto[]>(`${this.apiUrl}/list-appointments-by-patient`, {observe: 'response'})
  }

  public getAppointmentsForTimePeriod(startDate: string, endDate: string): Observable<HttpResponse<AppointmentDto[]>> {
    return this.httpClient.get<AppointmentDto[]>(
      `${this.apiUrl}/list-appointments-for-time-period/${startDate}/${endDate}`,
      {observe: 'response'}
    )
  }

  public getAppointmentsForTimePeriodByDoctor(startDate: string, endDate: string, doctorId: number): Observable<HttpResponse<AppointmentDto[]>> {
  return this.httpClient.get<AppointmentDto[]>(
      `${this.apiUrl}/list-appointments-for-time-period/${startDate}/${endDate}?doctorId=${doctorId}`,
      {observe: 'response'}
    )
  }

  public getMonthWithMostSickLeaves(year: string): Observable<HttpResponse<MostSickLeavesByMonthDto>> {
    return this.httpClient.get<MostSickLeavesByMonthDto>(
      `${this.apiUrl}/get-month-with-most-sick-leaves/${year}`,
      {observe: 'response'}
    )
  }

  public getDoctorsBySickLeaveCount(limit: number): Observable<HttpResponse<MostSickLeavesByDoctorDto[]>> {
    return this.httpClient.get<MostSickLeavesByDoctorDto[]>(
      `${this.apiUrl}/get-doctors-by-sick-leave-count/${limit}`,
      {observe: 'response'}
    )
  }
}
