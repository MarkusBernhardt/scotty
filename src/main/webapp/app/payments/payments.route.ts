import { Routes } from '@angular/router';

import downloadPaymentsRoute from './download-payments/download-payments.route';
import uploadPaymentsRoute from './upload-payments/upload-payments.route';

const paymentsRoutes: Routes = [uploadPaymentsRoute, downloadPaymentsRoute];

export default paymentsRoutes;
