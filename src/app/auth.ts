import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class Auth {
private apiUrl = 'http://localhost:1155/auth/login';

  constructor(private http: HttpClient) {}

  login(data: any): Observable<any> {
    return this.http.post(this.apiUrl, data);
  }

  register(data: any): Observable<any> {
  return this.http.post('http://localhost:1155/auth/registerNewUser', data);
}


  extractUserRole(token: string): string {
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
    const role = payload.role || '';
          return role.startsWith('ROLE_') ? role.slice(5) : role;
    } catch {
      return '';
    }
  }

  getRole(): string {
  const token = this.getToken();
  return token ? this.extractUserRole(token) : '';
}


  getToken(): string | null {
    return localStorage.getItem('token');
  }

  logout() {
    localStorage.removeItem('token');
  }
}
