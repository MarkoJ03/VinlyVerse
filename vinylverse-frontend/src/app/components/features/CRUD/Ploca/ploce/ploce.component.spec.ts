import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PloceComponent } from './ploce.component';

describe('PloceComponent', () => {
  let component: PloceComponent;
  let fixture: ComponentFixture<PloceComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PloceComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PloceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
