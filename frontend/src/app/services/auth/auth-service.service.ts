import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { User, UserLoginDTO } from '../../Dto/user-dtos';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private authUrl = '/auth'
  private userSessionSubject = new BehaviorSubject<boolean>(false);
  constructor(private http: HttpClient) { }

  saveUser(user: User): Observable<User> {
    return this.http.post<User>(environment.apiUrl + this.authUrl + "/register", user, { withCredentials: true });
  }

  loginUser(user: UserLoginDTO){
    return this.http.post<User>(environment.apiUrl + this.authUrl + "/login", user, { withCredentials: true });
  }

  logout() {
  return this.http.post(environment.apiUrl +
    this.authUrl + "/logout",
    {},
    { withCredentials: true }
  );
  } 

  sendUserSession(isLoggedIn: boolean){
    if (isLoggedIn) {
      localStorage.setItem('isLoggedIn', 'true');
    } else {
      localStorage.removeItem('isLoggedIn');
    }
    this.userSessionSubject.next(isLoggedIn);
  }

  getUserSession(): Observable<boolean>{
    return this.userSessionSubject.asObservable()
  }
}
