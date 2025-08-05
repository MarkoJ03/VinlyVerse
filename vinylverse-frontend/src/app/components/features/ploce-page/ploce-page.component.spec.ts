import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PlocePageComponent } from './ploce-page.component';

describe('PlocePageComponent', () => {
  let component: PlocePageComponent;
  let fixture: ComponentFixture<PlocePageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PlocePageComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PlocePageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
