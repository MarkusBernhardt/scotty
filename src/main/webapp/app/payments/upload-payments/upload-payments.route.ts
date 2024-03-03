import { Route } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import PaymentsUploadPaymentsComponent from './upload-payments.component';

const uploadPaymentsRoute: Route = {
  path: 'upload-payments',
  component: PaymentsUploadPaymentsComponent,
  title: 'payments.uploadPayments.title',
  canActivate: [UserRouteAccessService],
};

export default uploadPaymentsRoute;
