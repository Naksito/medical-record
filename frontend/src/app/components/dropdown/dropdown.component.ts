import {NgFor, NgIf} from '@angular/common';
import {Component, EventEmitter, forwardRef, Input, Output} from '@angular/core';
import {ControlValueAccessor, FormsModule, NG_VALUE_ACCESSOR} from '@angular/forms';

@Component({
  selector: 'app-dropdown',
  imports: [
    NgIf,
    NgFor,
    FormsModule
  ],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => DropdownComponent),
      multi: true
    }
  ],
  templateUrl: './dropdown.component.html',
  styleUrl: './dropdown.component.css'
})
export class DropdownComponent<T> implements ControlValueAccessor {
  @Input() items: T[] = [];
  @Input() label = '';
  @Input() valueField = 'id';
  @Input() displayField = 'name';
  @Input() disabled = false;
  @Input() errorMessage = '';

  @Output() selectionChanged = new EventEmitter<T>();

  private _value: any = '';
  private onChange: any = () => {};
  private onTouch: any = () => {};

  get value(): any {
    return this._value;
  }

  set value(val: any) {
    this._value = val;
    this.onChange(val);
    this.onTouch();
  }

  getItemValue(item: T): any {
    if (typeof item === 'object' && item !== null) {
      const value = (item as any)[this.valueField];
      return value !== undefined && value !== null ? String(value) : null;
    }
    return item !== undefined && item !== null ? String(item) : null;
  }

  getItemLabel(item: T): string {
    if (typeof item === 'object' && item !== null) {
      return (item as any)[this.displayField];
    }
    return String(item);
  }

  onSelectionChange(value: any) {
    const newValue = value === '' || value === undefined ? null : value;
    this.value = newValue;
    const selectedItem = this.items.find(item => this.getItemValue(item) === newValue);
    if (selectedItem) {
      this.selectionChanged.emit(selectedItem);
    }
  }

  registerOnChange(fn: any): void {
    this.onChange = fn;
  }

  registerOnTouched(fn: any): void {
    this.onTouch = fn;
  }

  setDisabledState(isDisabled: boolean): void {
    this.disabled = isDisabled;
  }

  writeValue(obj: any): void {
    this._value = obj === undefined ? null : obj;
  }
}
