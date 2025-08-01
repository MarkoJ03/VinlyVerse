import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PloceFormaComponent } from './ploce-forma.component';

describe('PloceFormaComponent', () => {
  let component: PloceFormaComponent;
  let fixture: ComponentFixture<PloceFormaComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PloceFormaComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PloceFormaComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
