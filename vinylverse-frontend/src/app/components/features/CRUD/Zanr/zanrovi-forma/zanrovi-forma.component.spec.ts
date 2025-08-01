import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ZanroviFormaComponent } from './zanrovi-forma.component';

describe('ZanroviFormaComponent', () => {
  let component: ZanroviFormaComponent;
  let fixture: ComponentFixture<ZanroviFormaComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ZanroviFormaComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ZanroviFormaComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
