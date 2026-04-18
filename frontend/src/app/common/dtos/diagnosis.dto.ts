import {IcdDto} from './icd.dto';
import {SickLeaveDto} from './sick-leave.dto';

export interface DiagnosisDto {
  id: number,
  treatmentDescription: string,
  icd: IcdDto,
  sickLeave: SickLeaveDto | null,
}

export interface DiagnosisSummary {
  id: number,
  treatmentDescription: string,
  icdId: number,
  sickLeaveId: number | undefined,
}

export interface CreateDiagnosisDto {
  treatmentDescription: string,
  icdId: number,
  sickLeaveDate: number | null,
  sickLeaveDays: number | null,
}

export interface UpdateDiagnosisDto {
  id: number,
  treatmentDescription: string,
  icdId: number,
  sickLeaveDate: number | null,
  sickLeaveDays: number | null,
}
