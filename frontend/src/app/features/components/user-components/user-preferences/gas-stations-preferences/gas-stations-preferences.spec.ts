import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GasStationsPreferencesComponent } from './gas-stations-preferences';

describe('GasStationsPreferencesComponent', () => {
  let component: GasStationsPreferencesComponent;
  let fixture: ComponentFixture<GasStationsPreferencesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [GasStationsPreferencesComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(GasStationsPreferencesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
