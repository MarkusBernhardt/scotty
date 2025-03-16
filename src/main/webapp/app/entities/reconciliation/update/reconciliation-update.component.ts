import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IPayment } from 'app/entities/payment/payment.model';
import { PaymentService } from 'app/entities/payment/service/payment.service';
import { Gateway } from 'app/entities/enumerations/gateway.model';
import { ReconciliationService } from '../service/reconciliation.service';
import { IReconciliation } from '../reconciliation.model';
import { ReconciliationFormGroup, ReconciliationFormService } from './reconciliation-form.service';

@Component({
  standalone: true,
  selector: 'jhi-reconciliation-update',
  templateUrl: './reconciliation-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class ReconciliationUpdateComponent implements OnInit {
  isSaving = false;
  reconciliation: IReconciliation | null = null;
  gatewayValues = Object.keys(Gateway);

  paymentsSharedCollection: IPayment[] = [];

  protected reconciliationService = inject(ReconciliationService);
  protected reconciliationFormService = inject(ReconciliationFormService);
  protected paymentService = inject(PaymentService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: ReconciliationFormGroup = this.reconciliationFormService.createReconciliationFormGroup();

  comparePayment = (o1: IPayment | null, o2: IPayment | null): boolean => this.paymentService.comparePayment(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ reconciliation }) => {
      this.reconciliation = reconciliation;
      if (reconciliation) {
        this.updateForm(reconciliation);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const reconciliation = this.reconciliationFormService.getReconciliation(this.editForm);
    if (reconciliation.id !== null) {
      this.subscribeToSaveResponse(this.reconciliationService.update(reconciliation));
    } else {
      this.subscribeToSaveResponse(this.reconciliationService.create(reconciliation));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IReconciliation>>): void {
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

  protected updateForm(reconciliation: IReconciliation): void {
    this.reconciliation = reconciliation;
    this.reconciliationFormService.resetForm(this.editForm, reconciliation);

    this.paymentsSharedCollection = this.paymentService.addPaymentToCollectionIfMissing<IPayment>(
      this.paymentsSharedCollection,
      reconciliation.scottyPayment,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.paymentService
      .query()
      .pipe(map((res: HttpResponse<IPayment[]>) => res.body ?? []))
      .pipe(
        map((payments: IPayment[]) =>
          this.paymentService.addPaymentToCollectionIfMissing<IPayment>(payments, this.reconciliation?.scottyPayment),
        ),
      )
      .subscribe((payments: IPayment[]) => (this.paymentsSharedCollection = payments));
  }
}
