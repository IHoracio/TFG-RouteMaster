import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GasStationsPreferences } from './gas-stations-preferences';

describe('GasStationsPreferences', () => {
  let component: GasStationsPreferences;
  let fixture: ComponentFixture<GasStationsPreferences>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [GasStationsPreferences]
    })
    .compileComponents();

    fixture = TestBed.createComponent(GasStationsPreferences);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
