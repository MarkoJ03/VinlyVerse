import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ZanroviComponent } from './zanrovi.component';

describe('ZanroviComponent', () => {
  let component: ZanroviComponent;
  let fixture: ComponentFixture<ZanroviComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ZanroviComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ZanroviComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
