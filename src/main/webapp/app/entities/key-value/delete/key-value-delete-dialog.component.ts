import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IKeyValue } from '../key-value.model';
import { KeyValueService } from '../service/key-value.service';

@Component({
  standalone: true,
  templateUrl: './key-value-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class KeyValueDeleteDialogComponent {
  keyValue?: IKeyValue;

  constructor(
    protected keyValueService: KeyValueService,
    protected activeModal: NgbActiveModal,
  ) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.keyValueService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
