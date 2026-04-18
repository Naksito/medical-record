import {Injectable} from '@angular/core';
import {HttpClient, HttpResponse} from '@angular/common/http';
import {environment} from '../../environments/environment';

import {DoctorDto, DoctorSummary} from '../common/dtos/doctor.dto';
import {firstValueFrom, Observable} from 'rxjs';
import {PatientDto} from '../common/dtos/patient.dto';
import {SpecialtyDto} from '../common/dtos/specialty.dto';
import {ApiResponse} from '../common/interfaces/api.response';

@Injectable({
  providedIn: 'root'
})
export class UsersService {
  private readonly apiUrl: string = `${environment.apiGateway}/users`

  constructor(
    private httpClient: HttpClient
  ) {}

  public async getGpDoctors(): Promise<DoctorSummary[]> {
    return await firstValueFrom(this.httpClient.get<DoctorSummary[]>(`${this.apiUrl}/doctors/list/gp`));
  }

  public async getAllPatients(): Promise<PatientDto[]> {
    return await firstValueFrom(this.httpClient.get<PatientDto[]>(`${this.apiUrl}/patients/list/all`));
  }

  public async getAllDoctors(): Promise<DoctorDto[]> {
    return await firstValueFrom(this.httpClient.get<DoctorDto[]>(`${this.apiUrl}/doctors/list/all`));
  }

  public async getAllSpecialties(): Promise<SpecialtyDto[]> {
    return await firstValueFrom(this.httpClient.get<SpecialtyDto[]>(`${this.apiUrl}/specialty/list/all`));
  }

  public updatePatient(id: number, firstName: string, lastName: string, gpId: number, insured: boolean): Observable<HttpResponse<ApiResponse>> {
    let body: any = {};

    body.id = id;

    if (firstName) {
      body.firstName = firstName;
    }

    if (lastName) {
      body.lastName = lastName;
    }

    if (gpId) {
      body.gpId = gpId;
    }

    body.insured = insured;

    return this.httpClient.put<ApiResponse>(`${this.apiUrl}/patients/update`, body, {observe: 'response'});
  }

  public deletePatient(id: number): Observable<HttpResponse<ApiResponse>> {
    return this.httpClient.delete<ApiResponse>(`${this.apiUrl}/patients/delete?id=${id}`, {observe: 'response'});
  }

  public updateDoctor(id: number, firstName: string, lastName: string, specialtyId: number, gp: boolean): Observable<HttpResponse<ApiResponse>> {
    let body: any = {};

    body.id = id;

    if (firstName) {
      body.firstName = firstName;
    }

    if (lastName) {
      body.lastName = lastName;
    }

    if (specialtyId) {
      body.specialtyId = specialtyId;
    }

    body.generalPractitioner = gp;

    console.log(body)

    return this.httpClient.put<ApiResponse>(`${this.apiUrl}/doctors/update`, body, {observe: 'response'});
  }

  public deleteDoctor(id: number): Observable<HttpResponse<ApiResponse>> {
    return this.httpClient.delete<ApiResponse>(`${this.apiUrl}/doctors/delete?id=${id}`, {observe: 'response'});
  }

  public createSpecialty(name: string, description: string): Observable<HttpResponse<ApiResponse>> {
    return this.httpClient.post<ApiResponse>(`${this.apiUrl}/specialty/create`, {
      name: name,
      description: description
    }, {observe: 'response'});
  }

  public updateSpecialty(id: number, name: string, description: string): Observable<HttpResponse<ApiResponse>> {
    let body: any = {};

    body.id = id;

    if (name) {
      body.name = name;
    }

    if (description) {
      body.description = description;
    }

    return this.httpClient.put<ApiResponse>(`${this.apiUrl}/specialty/update`, body, {observe: 'response'});
  }

  public deleteSpecialty(id: number): Observable<HttpResponse<ApiResponse>> {
    return this.httpClient.delete<ApiResponse>(`${this.apiUrl}/specialty/delete?id=${id}`, {observe: 'response'});
  }
}
