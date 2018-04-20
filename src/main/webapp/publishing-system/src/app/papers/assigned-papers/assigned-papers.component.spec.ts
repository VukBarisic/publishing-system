import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AssignedPapersComponent } from './assigned-papers.component';

describe('AssignedPapersComponent', () => {
  let component: AssignedPapersComponent;
  let fixture: ComponentFixture<AssignedPapersComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AssignedPapersComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AssignedPapersComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
