import { Injectable } from '@angular/core';
import {HttpClient, HttpResponse} from '@angular/common/http';
import {environment} from '../../environments/environment';
import {Observable} from 'rxjs';
import {DoctorMenuDataDto, PatientMenuDataDto} from '../common/dtos/general.dto';

@Injectable({
  providedIn: 'root'
})
export class GeneralWorkService {
  private readonly apiUrl: string = environment.apiGateway

  constructor(
    private httpClient: HttpClient
  ) { }

  public getPatientMenuData(patientId: number): Observable<HttpResponse<PatientMenuDataDto>> {
    return this.httpClient.get<PatientMenuDataDto>(
      `${this.apiUrl}/general-work/patient-menu-data?id=${patientId}`,
      {observe: 'response'}
    )
  }

  public getDoctorMenuData(doctorId: number): Observable<HttpResponse<DoctorMenuDataDto>> {
    return this.httpClient.get<DoctorMenuDataDto>(
      `${this.apiUrl}/general-work/doctor-menu-data?id=${doctorId}`,
      {observe: 'response'}
    )
  }
}
