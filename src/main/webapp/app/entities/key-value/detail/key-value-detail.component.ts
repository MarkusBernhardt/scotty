import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { DurationPipe, FormatMediumDatePipe, FormatMediumDatetimePipe } from 'app/shared/date';
import { IKeyValue } from '../key-value.model';

@Component({
  standalone: true,
  selector: 'jhi-key-value-detail',
  templateUrl: './key-value-detail.component.html',
  imports: [SharedModule, RouterModule, DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe],
})
export class KeyValueDetailComponent {
  keyValue = input<IKeyValue | null>(null);

  previousState(): void {
    window.history.back();
  }
}
