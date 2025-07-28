import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PlocaDetailsComponent } from './ploca-details.component';

describe('PlocaDetailsComponent', () => {
  let component: PlocaDetailsComponent;
  let fixture: ComponentFixture<PlocaDetailsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PlocaDetailsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PlocaDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
