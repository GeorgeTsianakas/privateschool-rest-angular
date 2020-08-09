import {Injectable} from '@angular/core';
import {User} from '../models/user';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {Observable} from 'rxjs';

let API_URL = 'http://localhost:8080/api/manager/';

@Injectable({
  providedIn: 'root'
})
export class ManagerService {

  currentUser: User;
  headers: HttpHeaders;

  constructor(private http: HttpClient) {
    this.currentUser = JSON.parse(localStorage.getItem('currentUser'));
    this.headers = new HttpHeaders({
      authorization: 'Bearer ' + this.currentUser.token,
      'Content-Type': 'application/json; charset=UTF-8'
    });
  }

  findAllEnrollments(): Observable<any> {
    return this.http.get(API_URL + 'enrollments', {headers: this.headers});
  }

}
