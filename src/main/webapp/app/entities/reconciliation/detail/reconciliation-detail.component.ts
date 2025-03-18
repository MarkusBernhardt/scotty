import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { DurationPipe, FormatMediumDatePipe, FormatMediumDatetimePipe } from 'app/shared/date';
import { IReconciliation } from '../reconciliation.model';

@Component({
  standalone: true,
  selector: 'jhi-reconciliation-detail',
  templateUrl: './reconciliation-detail.component.html',
  imports: [SharedModule, RouterModule, DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe],
})
export class ReconciliationDetailComponent {
  reconciliation = input<IReconciliation | null>(null);

  previousState(): void {
    window.history.back();
  }
}
