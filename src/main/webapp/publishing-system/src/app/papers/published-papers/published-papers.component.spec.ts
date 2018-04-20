import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { PublishedPapersComponent } from './published-papers.component';

describe('PublishedPapersComponent', () => {
  let component: PublishedPapersComponent;
  let fixture: ComponentFixture<PublishedPapersComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ PublishedPapersComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PublishedPapersComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
