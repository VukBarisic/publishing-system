import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ToastrService } from 'ngx-toastr';
import { Observable } from 'rxjs/Observable';

@Injectable()
export class RestService<T> {

  constructor(protected http: HttpClient,
    protected baseUrl: string,
    private toastr: ToastrService) { }

  protected handleError<E>(operation = 'operation', result?: E) {
    return (response: any): Observable<E> => {
      console.error(response);
      if (response.error) {
        this.toastr.error(response.error);
      } else {
        this.toastr.error('Client side error!');
      }
      return Observable.throw(result as E);
    };
  }
}
