import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IKeyValue } from '../key-value.model';
import { KeyValueService } from '../service/key-value.service';
import { KeyValueFormGroup, KeyValueFormService } from './key-value-form.service';

@Component({
  standalone: true,
  selector: 'jhi-key-value-update',
  templateUrl: './key-value-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class KeyValueUpdateComponent implements OnInit {
  isSaving = false;
  keyValue: IKeyValue | null = null;

  protected keyValueService = inject(KeyValueService);
  protected keyValueFormService = inject(KeyValueFormService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: KeyValueFormGroup = this.keyValueFormService.createKeyValueFormGroup();

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ keyValue }) => {
      this.keyValue = keyValue;
      if (keyValue) {
        this.updateForm(keyValue);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const keyValue = this.keyValueFormService.getKeyValue(this.editForm);
    if (keyValue.id !== null) {
      this.subscribeToSaveResponse(this.keyValueService.update(keyValue));
    } else {
      this.subscribeToSaveResponse(this.keyValueService.create(keyValue));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IKeyValue>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(keyValue: IKeyValue): void {
    this.keyValue = keyValue;
    this.keyValueFormService.resetForm(this.editForm, keyValue);
  }
}
