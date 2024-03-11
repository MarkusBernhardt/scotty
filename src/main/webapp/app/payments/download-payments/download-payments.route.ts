import { Route } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { DESC } from 'app/config/navigation.constants';
import DownloadPaymentsComponent from './download-payments.component';

const downloadPaymentsRoute: Route = {
  path: 'download-payments',
  component: DownloadPaymentsComponent,
  title: 'payments.downloadPayments.title',
  canActivate: [UserRouteAccessService],
  data: {
    defaultSort: 'fileName,' + DESC,
  },
};

export default downloadPaymentsRoute;
