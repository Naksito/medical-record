export interface SickLeaveDto {
  id: number,
  date: string,
  numberOfDays: number,
}

export interface CreateSickLeaveDto {
  date: string,
  numberOfDays: number,
}

export interface UpdateSickLeaveDto {
  id: number,
  date: string,
  numberOfDays: number,
}

export interface MostSickLeavesByMonthDto {
  date: string,
  count: number
}

export interface MostSickLeavesByDoctorDto {
  doctorId: number,
  count: number
}
