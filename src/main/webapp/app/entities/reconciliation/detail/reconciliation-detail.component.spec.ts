import { TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness, RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { ReconciliationDetailComponent } from './reconciliation-detail.component';

describe('Reconciliation Management Detail Component', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReconciliationDetailComponent, RouterTestingModule.withRoutes([], { bindToComponentInputs: true })],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              component: ReconciliationDetailComponent,
              resolve: { reconciliation: () => of({ id: 123 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(ReconciliationDetailComponent, '')
      .compileComponents();
  });

  describe('OnInit', () => {
    it('Should load reconciliation on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', ReconciliationDetailComponent);

      // THEN
      expect(instance.reconciliation).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
