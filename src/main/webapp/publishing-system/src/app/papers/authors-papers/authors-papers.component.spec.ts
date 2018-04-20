import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AuthorsPapersComponent } from './authors-papers.component';

describe('AuthorsPapersComponent', () => {
  let component: AuthorsPapersComponent;
  let fixture: ComponentFixture<AuthorsPapersComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AuthorsPapersComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AuthorsPapersComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
