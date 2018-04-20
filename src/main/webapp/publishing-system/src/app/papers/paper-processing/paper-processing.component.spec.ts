import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { PaperProcessingComponent } from './paper-processing.component';

describe('PaperProcessingComponent', () => {
  let component: PaperProcessingComponent;
  let fixture: ComponentFixture<PaperProcessingComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ PaperProcessingComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PaperProcessingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
