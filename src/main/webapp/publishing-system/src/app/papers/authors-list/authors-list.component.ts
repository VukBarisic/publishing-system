import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

import { PaperService } from '../../core/http/paper.service';
import { UserResponse } from '../../shared/model/user-response';

@Component({
  selector: 'ps-authors-list',
  templateUrl: './authors-list.component.html',
  styleUrls: ['./authors-list.component.css']
})
export class AuthorsListComponent implements OnInit {

  authors: UserResponse[];

  constructor(private paperService: PaperService,
    private router: Router) { }

  ngOnInit() {
    this.getAuthors();
  }

  getAuthors() {
    this.paperService.findAllAuthors().subscribe(
      res => {
        this.authors = res;
      },
      err => { });
  }

  showDetails(author: UserResponse): void {
    this.router.navigate(['authors', author.username, 'papers']);
  }
}
