import { ComponentFixture, TestBed } from '@angular/core/testing';

import { VehiclePreferencesComponent } from './vehicle-preferences';

describe('VehiclePreferencesComponent', () => {
  let component: VehiclePreferencesComponent;
  let fixture: ComponentFixture<VehiclePreferencesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [VehiclePreferencesComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(VehiclePreferencesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
