import { TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness, RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { KeyValueDetailComponent } from './key-value-detail.component';

describe('KeyValue Management Detail Component', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [KeyValueDetailComponent, RouterTestingModule.withRoutes([], { bindToComponentInputs: true })],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              component: KeyValueDetailComponent,
              resolve: { keyValue: () => of({ id: 123 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(KeyValueDetailComponent, '')
      .compileComponents();
  });

  describe('OnInit', () => {
    it('Should load keyValue on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', KeyValueDetailComponent);

      // THEN
      expect(instance.keyValue).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
