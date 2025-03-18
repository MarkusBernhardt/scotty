import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import KeyValueResolve from './route/key-value-routing-resolve.service';

const keyValueRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/key-value.component').then(m => m.KeyValueComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/key-value-detail.component').then(m => m.KeyValueDetailComponent),
    resolve: {
      keyValue: KeyValueResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/key-value-update.component').then(m => m.KeyValueUpdateComponent),
    resolve: {
      keyValue: KeyValueResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/key-value-update.component').then(m => m.KeyValueUpdateComponent),
    resolve: {
      keyValue: KeyValueResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default keyValueRoute;
