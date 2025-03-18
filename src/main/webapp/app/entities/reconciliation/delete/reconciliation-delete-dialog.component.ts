import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IReconciliation } from '../reconciliation.model';
import { ReconciliationService } from '../service/reconciliation.service';

@Component({
  standalone: true,
  templateUrl: './reconciliation-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class ReconciliationDeleteDialogComponent {
  reconciliation?: IReconciliation;

  protected reconciliationService = inject(ReconciliationService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.reconciliationService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
