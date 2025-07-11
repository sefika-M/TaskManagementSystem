import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Auth } from './auth';
import { Observable, throwError } from 'rxjs';
import { Task } from './task';

@Injectable({
  providedIn: 'root'
})
export class TaskService {
  private apiUrl = 'http://localhost:1155/tasks';

  constructor(private http: HttpClient) {}

  getTasks(): Observable<Task[]> {
    const token = localStorage.getItem('token');
  if (!token || token.trim() === '') {
  console.error('❌ No token found in localStorage!');
      return throwError(() => new Error('No token found'));
}

    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
  return this.http.get<Task[]>(`${this.apiUrl}/showAll`, { headers });
 
}

updateTaskStatus(taskId: number, status: string) {
  const token = localStorage.getItem('token');
  const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
  return this.http.patch(`${this.apiUrl}/updateTask/${taskId}/status?status=${status}`, null, { headers });
}

addTask(task: any) {
  const token = localStorage.getItem('token');
  const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
  return this.http.post(`${this.apiUrl}/addTask`, task, { headers });
}

getTaskById(id: number) {
  const token = localStorage.getItem('token');
  const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
  return this.http.get<Task>(`${this.apiUrl}/getById/${id}`, { headers });
}

updateTask(id: number, taskData: Task) {
  const token = localStorage.getItem('token');
  const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
  return this.http.put(`${this.apiUrl}/updateTask/${id}`, taskData, { headers });
}

deleteTask(id: number) {
  const token = localStorage.getItem('token');
  if (!token || token.trim() === '') {
    console.error('❌ No token found in localStorage!');
    return throwError(() => new Error('No token found'));
  }

  const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
  return this.http.delete(`${this.apiUrl}/deleteTask/${id}`, { headers });
}


}
