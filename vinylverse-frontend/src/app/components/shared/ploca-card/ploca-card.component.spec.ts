import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PlocaCardComponent } from './ploca-card.component';

describe('PlocaCardComponent', () => {
  let component: PlocaCardComponent;
  let fixture: ComponentFixture<PlocaCardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PlocaCardComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PlocaCardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
