import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import ReconciliationResolve from './route/reconciliation-routing-resolve.service';

const reconciliationRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/reconciliation.component').then(m => m.ReconciliationComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/reconciliation-detail.component').then(m => m.ReconciliationDetailComponent),
    resolve: {
      reconciliation: ReconciliationResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/reconciliation-update.component').then(m => m.ReconciliationUpdateComponent),
    resolve: {
      reconciliation: ReconciliationResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/reconciliation-update.component').then(m => m.ReconciliationUpdateComponent),
    resolve: {
      reconciliation: ReconciliationResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default reconciliationRoute;
