import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';

import { AuthService } from '../../core/http/auth.service';
import { AuthenticationRequest } from '../../shared/model/authentication-request';

@Component({
  selector: 'ps-signin',
  templateUrl: './signin.component.html',
  styleUrls: ['./signin.component.css']
})
export class SigninComponent implements OnInit {

  authRequest: AuthenticationRequest;

  constructor(private authService: AuthService,
    private router: Router,
    private toastr: ToastrService) { }

  ngOnInit() {
    this.authRequest = new AuthenticationRequest();
  }

  signin() {
    this.authService.signin(this.authRequest).subscribe(
      res => {
        this.toastr.success(`Welcome ${this.authRequest.username}!`);
        this.router.navigateByUrl('papers');
      },
      err => { });
  }
}
