import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { TaskService } from '../task-service';
import { Task } from '../task';
import { MatCardModule } from '@angular/material/card';

@Component({
  selector: 'app-edit-task',
  imports: [CommonModule,FormsModule, RouterModule,
    ReactiveFormsModule, MatCardModule,
    MatInputModule,
    MatFormFieldModule,
    MatSelectModule,
    MatButtonModule],
  templateUrl: './edit-task.html',
  styleUrl: './edit-task.css'
})
export class EditTask implements OnInit {
  taskForm!: FormGroup;
  taskId!: number;

  constructor(
    private route: ActivatedRoute,
    private fb: FormBuilder,
    private taskService: TaskService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.taskId = Number(this.route.snapshot.paramMap.get('id'));
    this.taskForm = this.fb.group({
      title: ['', Validators.required],
      description: [''],
      dueDate: ['', Validators.required],
      priority: ['MEDIUM', Validators.required],
      status: ['PENDING', Validators.required]
    });

    this.taskService.getTaskById(this.taskId).subscribe({
      next: (task: Task) => this.taskForm.patchValue(task),
      error: () => alert('❌ Failed to load task data.')
    });
  }

  onSubmit() {
    if (this.taskForm.valid) {
      this.taskService.updateTask(this.taskId, this.taskForm.value).subscribe({
        next: () => {
          alert('✅ Task updated successfully!');
          this.router.navigate(['/dashboard']);
        },
        error: () => alert('❌ Failed to update task.')
      });
    }
  }

}
