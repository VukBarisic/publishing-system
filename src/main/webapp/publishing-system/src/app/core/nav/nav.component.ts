import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

import { AuthService } from '../http/auth.service';

@Component({
  selector: 'ps-nav',
  templateUrl: './nav.component.html',
  styleUrls: ['./nav.component.css']
})
export class NavComponent implements OnInit {

  isCollapsed = true;

  constructor(
    private authService: AuthService,
    private router: Router) { }

  ngOnInit() {
  }

  isAuthenticated() {
    return this.authService.isAuthenticated();
  }

  isEditor() {
    return this.authService.isEditor();
  }

  isAuthor() {
    return this.authService.isAuthor();
  }

  me() {
    this.router.navigate(['authors', this.authService.getAuthenticatedUsername(), 'papers']);
  }

  signout() {
    this.authService.signout().subscribe(
      res => {
        this.router.navigateByUrl('signin');
      }, err => { });
  }
}
