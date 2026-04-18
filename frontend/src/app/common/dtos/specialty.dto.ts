export interface SpecialtyDto {
  id: number,
  name: string,
  description: string,
}

export interface CreateSpecialtyDto {
  name: string,
  description: string,
}

export interface UpdateSpecialtyDto {
  id: number,
  name: string,
  description: string,
}
