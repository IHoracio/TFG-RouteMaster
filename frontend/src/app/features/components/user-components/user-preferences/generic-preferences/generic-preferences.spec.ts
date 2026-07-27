import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GenericPreferencesComponent } from './generic-preferences';

describe('GenericPreferencesComponent', () => {
  let component: GenericPreferencesComponent;
  let fixture: ComponentFixture<GenericPreferencesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [GenericPreferencesComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(GenericPreferencesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
