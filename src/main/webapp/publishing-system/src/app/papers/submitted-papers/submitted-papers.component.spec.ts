import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { SubmittedPapersComponent } from './submitted-papers.component';

describe('SubmittedPapersComponent', () => {
  let component: SubmittedPapersComponent;
  let fixture: ComponentFixture<SubmittedPapersComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ SubmittedPapersComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SubmittedPapersComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
