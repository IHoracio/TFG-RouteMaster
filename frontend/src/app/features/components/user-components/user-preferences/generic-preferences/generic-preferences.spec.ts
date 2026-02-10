import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GenericPreferences } from './generic-preferences';

describe('GenericPreferences', () => {
  let component: GenericPreferences;
  let fixture: ComponentFixture<GenericPreferences>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [GenericPreferences]
    })
    .compileComponents();

    fixture = TestBed.createComponent(GenericPreferences);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
