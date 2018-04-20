import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ToastrService } from 'ngx-toastr';
import { Observable } from 'rxjs/Observable';
import { catchError, tap } from 'rxjs/operators';

import { AuthenticationRequest } from '../../shared/model/authentication-request';
import { AuthenticationResponse } from '../../shared/model/authentication-response';
import { RestService } from './rest.service';

const authenticatedUserKey = 'authenticatedUser';

@Injectable()
export class AuthService extends RestService<AuthenticationResponse> {

  constructor(protected http: HttpClient,
    toastr: ToastrService) {
    super(http, '/api/auth', toastr);
  }

  signup(body: string): Observable<string> {
    return this.http.post(`${this.baseUrl}/signup`, body, {
      headers: new HttpHeaders({
        'Content-Type': 'application/xml',
        'Accept': '*/*, application/xml, application/json'
      }),
      responseType: 'text'
    }).pipe(
      catchError(this.handleError<string>())
      );
  }

  signin(body: AuthenticationRequest): Observable<AuthenticationResponse> {
    return this.http.post<AuthenticationResponse>(`${this.baseUrl}/signin`, body).pipe(
      tap(res => {
        localStorage.setItem(authenticatedUserKey, JSON.stringify({
          username: res.username,
          role: res.role
        }));
      }),
      catchError(this.handleError<AuthenticationResponse>())
    );
  }

  signout(): Observable<void> {
    this.clearStorage();
    return this.http.post<void>(`${this.baseUrl}/signout`, null).pipe(
      catchError(this.handleError<void>())
    );
  }

  clearStorage(): void {
    localStorage.removeItem(authenticatedUserKey);
  }

  getAuthenticated(): Observable<AuthenticationResponse> {
    return this.http.get<AuthenticationResponse>('api/auth/me').pipe(
      tap(res => {
        localStorage.setItem(authenticatedUserKey, JSON.stringify({
          username: res.username,
          role: res.role
        }));
      }),
      catchError(this.handleError<AuthenticationResponse>())
    );
  }

  getAuthenticatedUser() {
    return JSON.parse(localStorage.getItem(authenticatedUserKey));
  }

  getAuthenticatedUsername() {
    const user = this.getAuthenticatedUser();
    return user ? user.username : '';
  }

  isAuthenticated(): boolean {
    return this.getAuthenticatedUser() != null;
  }

  isAuthor(): boolean {
    const user = this.getAuthenticatedUser();
    return user && user.role === 'AUTHOR';
  }

  isEditor(): boolean {
    const user = this.getAuthenticatedUser();
    return user && user.role === 'EDITOR';
  }
}
