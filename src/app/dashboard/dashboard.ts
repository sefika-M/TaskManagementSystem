import { Component, OnInit } from '@angular/core';
import { TaskService } from '../task-service';
import { Auth } from '../auth';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { Task } from '../task';
import { MatSelectModule } from '@angular/material/select';
import { MatTableDataSource } from '@angular/material/table';
import { MatSort, MatSortModule } from '@angular/material/sort';
import { MatPaginator, MatPaginatorModule } from '@angular/material/paginator';
import { ViewChild } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';

@Component({
  selector: 'app-dashboard',
  imports: [CommonModule,
FormsModule, RouterModule, MatTableModule,
    MatButtonModule, MatSelectModule, MatSortModule, MatPaginatorModule , MatIconModule, MatInputModule],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css'
})
export class Dashboard implements OnInit {
    userRole: string = '';
dataSource = new MatTableDataSource<Task>();

get displayedColumns(): string[] {
  const base = ['title', 'description', 'dueDate', 'priority', 'status'];
  if (this.auth.getRole() === 'ADMIN') {
    return [...base, 'actions']; 
  }
  return base;
}
@ViewChild(MatSort) sort!: MatSort;
@ViewChild(MatPaginator) paginator!: MatPaginator;

  constructor(private taskService: TaskService, private auth: Auth) {}

  ngOnInit() {
        this.userRole = this.auth.getRole();
console.log(" Logged-in role:", this.userRole);
console.log(" Raw token:", this.auth.getToken());
  console.log(" Decoded payload:", JSON.parse(atob(this.auth.getToken()?.split('.')[1] || ''))); 

  this.taskService.getTasks().subscribe({
    next: (data) => {
      console.log("Tasks fetched:", data);
        this.dataSource.data = data;
      this.dataSource.sort = this.sort;
      this.dataSource.paginator = this.paginator;
    },
    error: (err) => {
      console.error("❌ Failed to load tasks:", err);
      alert('Failed to load tasks. Check login or token.');
    }
  });
}

applyFilter(event: Event) {
  const filterValue = (event.target as HTMLInputElement).value;
  this.dataSource.filter = filterValue.trim().toLowerCase();
}


updateStatus(taskId: number, newStatus: string) {
  this.taskService.updateTaskStatus(taskId, newStatus).subscribe({
    next: () => {
      alert('✅ Status updated successfully!');
    },
    error: (err) => {
      console.error('❌ Failed to update status:', err);
      alert('❌ Error updating status. Try again.');
    }
  });
}

deleteTask(taskId: number) {
  if (confirm("Are you sure you want to delete this task?")) {
    this.taskService.deleteTask(taskId).subscribe({
      next: () => {
        alert("✅ Task deleted");
          this.dataSource.data = this.dataSource.data.filter(t => t.taskId !== taskId);
      },
      error: (err) => {
                console.error("❌ Failed to delete task:", err);
alert("❌ Failed to delete task.")
      }
    });
  }
}


  logout() {
    this.auth.logout();
    window.location.href = '/login';
  }

}
