import { Route } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { DESC } from 'app/config/navigation.constants';
import DownloadReconciliationsComponent from './download-reconciliations.component';

const downloadReconciliationsRoute: Route = {
  path: 'download-reconciliations',
  component: DownloadReconciliationsComponent,
  title: 'payments.downloadReconciliations.title',
  canActivate: [UserRouteAccessService],
  data: {
    defaultSort: 'fileName,' + DESC,
  },
};

export default downloadReconciliationsRoute;
