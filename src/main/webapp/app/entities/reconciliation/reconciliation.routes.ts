import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import { ReconciliationComponent } from './list/reconciliation.component';
import { ReconciliationDetailComponent } from './detail/reconciliation-detail.component';
import { ReconciliationUpdateComponent } from './update/reconciliation-update.component';
import ReconciliationResolve from './route/reconciliation-routing-resolve.service';

const reconciliationRoute: Routes = [
  {
    path: '',
    component: ReconciliationComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: ReconciliationDetailComponent,
    resolve: {
      reconciliation: ReconciliationResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: ReconciliationUpdateComponent,
    resolve: {
      reconciliation: ReconciliationResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: ReconciliationUpdateComponent,
    resolve: {
      reconciliation: ReconciliationResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default reconciliationRoute;
