import { Routes } from '@angular/router';

const routes: Routes = [
  {
    path: 'authority',
    data: { pageTitle: 'scottyApp.adminAuthority.home.title' },
    loadChildren: () => import('./admin/authority/authority.routes'),
  },
  {
    path: 'reconciliation',
    data: { pageTitle: 'scottyApp.reconciliation.home.title' },
    loadChildren: () => import('./reconciliation/reconciliation.routes'),
  },
  {
    path: 'key-value',
    data: { pageTitle: 'scottyApp.keyValue.home.title' },
    loadChildren: () => import('./key-value/key-value.routes'),
  },
  {
    path: 'payment',
    data: { pageTitle: 'scottyApp.payment.home.title' },
    loadChildren: () => import('./payment/payment.routes'),
  },
  /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
];

export default routes;
