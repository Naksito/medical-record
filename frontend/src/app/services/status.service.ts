import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {environment} from '../../environments/environment';
import {firstValueFrom} from 'rxjs';

export interface ServiceVersions {
  api: string;
  auth: string;
  users: string;
  appointments: string;
}

@Injectable({
  providedIn: 'root'
})
export class StatusService {
  private readonly statusUrl: string = `${environment.apiGateway}/status`;

  constructor(private httpClient: HttpClient) {}

  public async getVersions(): Promise<ServiceVersions> {
    return await firstValueFrom(this.httpClient.get<ServiceVersions>(this.statusUrl));
  }
}
