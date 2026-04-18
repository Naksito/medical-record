export interface IcdDto {
  id: number,
  code: string,
  description: string,
}

export interface CreateIcdDto {
  code: string,
  description: string,
}

export interface UpdateIcdDto {
  id: number,
  code: string,
  description: string,
}

export interface IcdOccurrenceDto {
  icdId: number,
  icdCode: string,
  occurrence: number
}
