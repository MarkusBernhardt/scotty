import { Routes } from '@angular/router';

import downloadPaymentsRoute from './download-payments/download-payments.route';
import downloadReconciliationsRoute from './download-reconciliations/download-reconciliations.route';
import uploadPaymentsRoute from './upload-payments/upload-payments.route';

const paymentsRoutes: Routes = [uploadPaymentsRoute, downloadPaymentsRoute, downloadReconciliationsRoute];

export default paymentsRoutes;
