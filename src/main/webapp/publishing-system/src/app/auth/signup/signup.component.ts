import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';

import { AuthService } from '../../core/http/auth.service';

@Component({
  selector: 'ps-signup',
  templateUrl: './signup.component.html',
  styleUrls: ['./signup.component.css']
})
export class SignupComponent implements OnInit {

  username: string;
  email: string;
  password: string;

  constructor(private authService: AuthService,
    private router: Router,
    private toastr: ToastrService) { }

  ngOnInit() {
  }

  signup() {
    // tslint:disable-next-line:max-line-length
    const signupXml = `<?xml version="1.0" encoding="UTF-8"?><user xmlns="http://ftn.uns.ac.rs/code10/user"><username>${this.username}</username><email>${this.email}</email><password>${this.password}</password></user>`;

    this.authService.signup(signupXml).subscribe(
      res => {
        this.toastr.success('Successful sign up!');
        this.router.navigateByUrl('signin');
      },
      err => { }
    );
  }

}
