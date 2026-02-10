import { ComponentFixture, TestBed } from '@angular/core/testing';

import { VehiclePreferences } from './vehicle-preferences';

describe('VehiclePreferences', () => {
  let component: VehiclePreferences;
  let fixture: ComponentFixture<VehiclePreferences>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [VehiclePreferences]
    })
    .compileComponents();

    fixture = TestBed.createComponent(VehiclePreferences);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
