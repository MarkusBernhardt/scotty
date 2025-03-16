import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import { KeyValueComponent } from './list/key-value.component';
import { KeyValueDetailComponent } from './detail/key-value-detail.component';
import { KeyValueUpdateComponent } from './update/key-value-update.component';
import KeyValueResolve from './route/key-value-routing-resolve.service';

const keyValueRoute: Routes = [
  {
    path: '',
    component: KeyValueComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: KeyValueDetailComponent,
    resolve: {
      keyValue: KeyValueResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: KeyValueUpdateComponent,
    resolve: {
      keyValue: KeyValueResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: KeyValueUpdateComponent,
    resolve: {
      keyValue: KeyValueResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default keyValueRoute;
