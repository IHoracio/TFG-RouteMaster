import { inject, Injectable } from "@angular/core";
import { BehaviorSubject, Observable } from "rxjs";
import { environment } from "../../../environments/environment";
import { HttpClient } from "@angular/common/http";
import { User, UserLoginDTO } from "../../Dto/user-dtos";

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private authUrl = '/auth';
  private apiUrl = environment.apiUrl;

  // Inicializamos el Subject con el valor real del storage
  private userSessionSubject = new BehaviorSubject<boolean>(
    localStorage.getItem('isLoggedIn') === 'true'
  );

  private http = inject(HttpClient);

  saveUser(user: User): Observable<User> {
    return this.http.post<User>(environment.apiUrl + this.authUrl + "/register", user, { withCredentials: true });
  }


  loginUser(user: UserLoginDTO) {
    return this.http.post<User>(environment.apiUrl + this.authUrl + "/login", user, { withCredentials: true });
  }

  logout(): Observable<any> {
    return this.http.post(`${this.apiUrl}${this.authUrl}/logout`, {}, { withCredentials: true });
  }

  sendUserSession(isLoggedIn: boolean) {
    if (isLoggedIn) {
      localStorage.setItem('isLoggedIn', 'true');
    } else {
      localStorage.removeItem('isLoggedIn');
    }
    this.userSessionSubject.next(isLoggedIn);
  }

  // Método síncrono útil para Guards
  get isLoggedIn(): boolean {
    return this.userSessionSubject.value;
  }

  getUserSession(): Observable<boolean> {
    return this.userSessionSubject.asObservable();
  }
}