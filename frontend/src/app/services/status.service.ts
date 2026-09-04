import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {environment} from '../../environments/environment';
import {catchError, firstValueFrom, of} from 'rxjs';

export interface ServiceVersions {
  api: string;
  auth: string;
  users: string;
  appointments: string;
  frontend: string;
}

interface FrontendVersion {
  version: string;
}

@Injectable({
  providedIn: 'root'
})
export class StatusService {
  private readonly statusUrl: string = `${environment.apiGateway}/status`;

  constructor(private httpClient: HttpClient) {}

  public async getVersions(): Promise<ServiceVersions> {
    const serviceVersions = await firstValueFrom(
      this.httpClient.get<Omit<ServiceVersions, 'frontend'>>(this.statusUrl).pipe(
        catchError(() => of({api: 'unknown', auth: 'unknown', users: 'unknown', appointments: 'unknown'}))
      )
    );
    const frontendVersion = await firstValueFrom(
      this.httpClient.get<FrontendVersion>('/version.json', {
        headers: {'Cache-Control': 'no-cache'}
      }).pipe(catchError(() => of({version: 'unknown'})))
    );
    return {...serviceVersions, frontend: frontendVersion.version};
  }
}
