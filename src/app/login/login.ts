import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { Router, RouterModule } from '@angular/router';
import { Auth } from '../auth';

@Component({
  selector: 'app-login',
  imports: [ CommonModule, FormsModule, RouterModule,
    ReactiveFormsModule , MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule],
  templateUrl: './login.html',
  styleUrl: './login.css'
})
export class Login {
  loginForm: FormGroup;

  constructor(private fb: FormBuilder, private auth: Auth, private router: Router) {
    this.loginForm = this.fb.group({
  username: ['', Validators.required],
      password: ['', [Validators.required]]
    });
  }

  onLogin() {
  if (this.loginForm.valid) {
    console.log("Login form submitted:", this.loginForm.value); 
    this.auth.login(this.loginForm.value).subscribe({
      next: (res: any) => {
        console.log("Login success response:", res); 
        localStorage.setItem('token', res.token);
       if (res.role === 'ADMIN' || res.role === 'USER' || res.role === 'ROLE_USER' || res.role === 'ROLE_ADMIN') {
                        console.log("Role extracted:", res.role);
  this.router.navigate(['/dashboard']);
} else {
  alert("Unknown role. Access denied.");
}

      },
      error: (err) => {
        console.error("Login error:", err);
        alert("Login failed. Please check credentials.");
      }
    });
  } else {
    console.warn("Form is invalid:", this.loginForm.value);
  }
}

}
