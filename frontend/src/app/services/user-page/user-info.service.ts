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

  constructor(private http: HttpClient) {
    const savedUser = localStorage.getItem('user');
    if (savedUser) {
      this.userSignal.set(JSON.parse(savedUser));
    }
    const savedRoutes = localStorage.getItem('routes');
    if (savedRoutes) {
      this.routesSignal.set(JSON.parse(savedRoutes));
    }
  }

  getUserSignal() { return this.userSignal; }
  setUser(data: any) { 
    this.userSignal.set(data); 
    localStorage.setItem('user', JSON.stringify(data));
  }
  getRoutesSignal() { return this.routesSignal; }
  setRoutes(data: any[]) { 
    this.routesSignal.set(data); 
    localStorage.setItem('routes', JSON.stringify(data));
  }

  getUserInfo(): Observable<any> {
    return this.http.get(`${this.baseUrl}/api/users/get`, { withCredentials: true });
  }

  getUserRoutes(): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/api/savedRoute`, { withCredentials: true });
  }

  executeRoute(id: number): Observable<any> {
    return this.http.get(`${this.baseUrl}/api/savedRoute/execute`, {
      params: { id: id.toString() },
      withCredentials: true
    });
  }

  deleteRoute(id: number): Observable<any> {
    return this.http.delete(`${this.baseUrl}/api/savedRoute/delete/${id}`, {
      params: { id }, withCredentials: true });
  }

  renameRoute(routeId: number, newName: string): Observable<any> {
    return this.http.post(`${this.baseUrl}/api/savedRoute/rename`, null, {
      params: { routeId: routeId.toString(), newName },
      withCredentials: true
    });
  }
}