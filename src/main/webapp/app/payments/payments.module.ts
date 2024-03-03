import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import PaymentsUploadPaymentsComponent from './upload-payments/upload-payments.component';
import paymentsRoutes from './payments.route';

@NgModule({
  imports: [SharedModule, RouterModule.forChild(paymentsRoutes)],
  declarations: [PaymentsUploadPaymentsComponent],
})
export class PaymentsModule {}
