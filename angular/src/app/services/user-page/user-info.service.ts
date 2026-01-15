import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class UserInfoService {
  private baseUrl = 'http://localhost:8080';

  private userSignal = signal<any>({});
  private routesSignal = signal<any[]>([]);

  constructor(private http: HttpClient) { }

  getUserSignal() { return this.userSignal; }
  setUser(data: any) { this.userSignal.set(data); }
  getRoutesSignal() { return this.routesSignal; }
  setRoutes(data: any[]) { this.routesSignal.set(data); }

  getUserInfo(mail: string): Observable<any> {
    return this.http.get(`${this.baseUrl}/api/users/get`, {
      params: { mail }
    });
  }

  getUserRoutes(email: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/api/ruta`, {
      params: { email }
    });
  }

  executeRoute(id: number): Observable<any> {
    return this.http.get(`${this.baseUrl}/api/ruta/execute`, {
      params: { id: id.toString() }
    });
  }

  deleteRoute(id: number, email: string): Observable<any> {
    return this.http.delete(`${this.baseUrl}/api/ruta/delete/${id}`, {
      params: { email }
    });
  }
}