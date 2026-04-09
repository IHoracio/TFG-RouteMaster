import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class UserInfoService {
  private baseUrl = environment.apiUrl;

  private userSignal = signal<any>(this.getInitial('user', {}));
  private routesSignal = signal<any[]>(this.getInitial('routes', []));

  private getInitial(key: string, defaultValue: any) {
    const saved = localStorage.getItem(key);
    return saved ? JSON.parse(saved) : defaultValue;
  }

  constructor(private http: HttpClient) { }

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
      params: { id }, withCredentials: true
    });
  }

  renameRoute(routeId: number, newName: string): Observable<any> {
    return this.http.post(`${this.baseUrl}/api/savedRoute/rename`, null, {
      params: { routeId: routeId.toString(), newName },
      withCredentials: true
    });
  }

  clearUserData() {
    // 1. Resetear Signals a sus valores iniciales
    this.userSignal.set({});
    this.routesSignal.set([]);

    // 2. Limpiar el almacenamiento físico
    localStorage.clear();
  }
}