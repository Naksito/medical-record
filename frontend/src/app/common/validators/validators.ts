import {AbstractControl, ValidationErrors, ValidatorFn} from '@angular/forms';

export function isoDateValidator(): ValidatorFn {
  const isoDateRegex = /^\d{4}-\d{2}-\d{2}$/;

  return (control: AbstractControl): ValidationErrors | null => {
    const value = control.value;

    if (!value) return null;

    if (!isoDateRegex.test(value)) {
      return { isoDate: { value } };
    }

    const date = new Date(value);
    const isValidDate = !isNaN(date.getTime()) && value === date.toISOString().split('T')[0];

    return isValidDate ? null : { isoDate: { value } };
  };
}

export function yearValidator(control: AbstractControl): ValidationErrors | null {
  const year = control.value;
  const yearRegex = /^\d{4}$/;

  if (!yearRegex.test(year)) {
    return { invalidYear: true };
  }

  const numericYear = parseInt(year, 10);
  if (numericYear < 1000 || numericYear > 9999) {
    return { invalidYear: true };
  }

  return null;
}

export function timeValidator(): ValidatorFn {
  const timeRegex = /^([01]\d|2[0-3]):([0-5]\d)$/;

  return (control: AbstractControl): ValidationErrors | null => {
    const value = control.value;

    if (!value) return null;

    return timeRegex.test(value) ? null : { time: { value } };
  };
}

export function optionalPairValidator(formGroup: AbstractControl): ValidationErrors | null {
  const leaveDate = formGroup.get('leaveDate')?.value;
  const leaveDays = formGroup.get('leaveDays')?.value;

  const leaveDateFilled = leaveDate !== null && leaveDate !== '' && formGroup.get('leaveDate')?.touched;
  const leaveDaysFilled = leaveDays !== null && leaveDays !== '' && formGroup.get('leaveDays')?.touched;

  const isoDateRegex = /^\d{4}-\d{2}-\d{2}$/;
  const integerRegex = /^[1-9]\d*$/;

  if (leaveDateFilled) {
    if (!isoDateRegex.test(leaveDate)) {
      return { leaveDate: true, leaveDateMsg: 'Invalid date format!' };
    }
    if (!leaveDaysFilled) {
      return { leaveDays: true, leaveDaysMsg: 'Please fill in the number of days!' };
    }
  }

  if (leaveDaysFilled) {
    if (!integerRegex.test(leaveDays)) {
      return { leaveDays: true, leaveDaysMsg: 'Please fill in a positive integer!' };
    }
    if (!leaveDateFilled) {
      return { leaveDate: true, leaveDateMsg: 'Please fill in the date!' };
    }
  }

  return null;
}
