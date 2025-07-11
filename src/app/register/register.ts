import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { Router, RouterModule } from '@angular/router';
import { Auth } from '../auth';

@Component({
  selector: 'app-register',
  imports: [CommonModule,FormsModule,RouterModule,
    ReactiveFormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatSelectModule],
  templateUrl: './register.html',
  styleUrl: './register.css'
})
export class Register {
  registerForm: FormGroup;

  constructor(private fb: FormBuilder, private auth: Auth, private router: Router) {
    this.registerForm = this.fb.group({
      username: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      password: ['', Validators.required],
      role: ['USER', Validators.required]
    });
  }

  onRegister() {
  if (this.registerForm.valid) {
        const formData = this.registerForm.value;
            formData.role = 'ROLE_' + formData.role;
    this.auth.register(this.registerForm.value).subscribe({
      next: (res: any) => {
        console.log('Register response:', res);
        alert('Registration successful. Please login.');
        this.router.navigate(['/login']);
      },
      error: (err) => {
        console.error('Register failed:', err);
        alert('Registration failed. Try again.');
      }
    });
  }
}
}
