import { TestBed, inject } from '@angular/core/testing';

import { EvaluationFormService } from './evaluation-form.service';

describe('EvaluationFormService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [EvaluationFormService]
    });
  });

  it('should be created', inject([EvaluationFormService], (service: EvaluationFormService) => {
    expect(service).toBeTruthy();
  }));
});
