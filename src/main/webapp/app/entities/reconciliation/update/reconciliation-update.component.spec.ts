import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { IPayment } from 'app/entities/payment/payment.model';
import { PaymentService } from 'app/entities/payment/service/payment.service';
import { ReconciliationService } from '../service/reconciliation.service';
import { IReconciliation } from '../reconciliation.model';
import { ReconciliationFormService } from './reconciliation-form.service';

import { ReconciliationUpdateComponent } from './reconciliation-update.component';

describe('Reconciliation Management Update Component', () => {
  let comp: ReconciliationUpdateComponent;
  let fixture: ComponentFixture<ReconciliationUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let reconciliationFormService: ReconciliationFormService;
  let reconciliationService: ReconciliationService;
  let paymentService: PaymentService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [ReconciliationUpdateComponent],
      providers: [
        provideHttpClient(),
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(ReconciliationUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ReconciliationUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    reconciliationFormService = TestBed.inject(ReconciliationFormService);
    reconciliationService = TestBed.inject(ReconciliationService);
    paymentService = TestBed.inject(PaymentService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Payment query and add missing value', () => {
      const reconciliation: IReconciliation = { id: 456 };
      const scottyPayment: IPayment = { id: 22811 };
      reconciliation.scottyPayment = scottyPayment;

      const paymentCollection: IPayment[] = [{ id: 2812 }];
      jest.spyOn(paymentService, 'query').mockReturnValue(of(new HttpResponse({ body: paymentCollection })));
      const additionalPayments = [scottyPayment];
      const expectedCollection: IPayment[] = [...additionalPayments, ...paymentCollection];
      jest.spyOn(paymentService, 'addPaymentToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ reconciliation });
      comp.ngOnInit();

      expect(paymentService.query).toHaveBeenCalled();
      expect(paymentService.addPaymentToCollectionIfMissing).toHaveBeenCalledWith(
        paymentCollection,
        ...additionalPayments.map(expect.objectContaining),
      );
      expect(comp.paymentsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const reconciliation: IReconciliation = { id: 456 };
      const scottyPayment: IPayment = { id: 24976 };
      reconciliation.scottyPayment = scottyPayment;

      activatedRoute.data = of({ reconciliation });
      comp.ngOnInit();

      expect(comp.paymentsSharedCollection).toContain(scottyPayment);
      expect(comp.reconciliation).toEqual(reconciliation);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IReconciliation>>();
      const reconciliation = { id: 123 };
      jest.spyOn(reconciliationFormService, 'getReconciliation').mockReturnValue(reconciliation);
      jest.spyOn(reconciliationService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ reconciliation });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: reconciliation }));
      saveSubject.complete();

      // THEN
      expect(reconciliationFormService.getReconciliation).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(reconciliationService.update).toHaveBeenCalledWith(expect.objectContaining(reconciliation));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IReconciliation>>();
      const reconciliation = { id: 123 };
      jest.spyOn(reconciliationFormService, 'getReconciliation').mockReturnValue({ id: null });
      jest.spyOn(reconciliationService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ reconciliation: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: reconciliation }));
      saveSubject.complete();

      // THEN
      expect(reconciliationFormService.getReconciliation).toHaveBeenCalled();
      expect(reconciliationService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IReconciliation>>();
      const reconciliation = { id: 123 };
      jest.spyOn(reconciliationService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ reconciliation });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(reconciliationService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('comparePayment', () => {
      it('Should forward to paymentService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(paymentService, 'comparePayment');
        comp.comparePayment(entity, entity2);
        expect(paymentService.comparePayment).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
