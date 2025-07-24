import { TestBed } from '@angular/core/testing';

import { PlocaService } from './ploca.service';

describe('PlocaService', () => {
  let service: PlocaService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(PlocaService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
