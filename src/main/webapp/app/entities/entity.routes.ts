import { Routes } from '@angular/router';

const routes: Routes = [
  {
    path: 'payment',
    data: { pageTitle: 'scottyApp.payment.home.title' },
    loadChildren: () => import('./payment/payment.routes'),
  },
  /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
];

export default routes;
