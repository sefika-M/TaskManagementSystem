import { Routes } from '@angular/router';
import { Login } from './login/login';
import { Dashboard } from './dashboard/dashboard';
import { Register } from './register/register';

export const routes: Routes = [
     { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: 'login', component: Login },
   { path: 'register', component: Register },
  { path: 'dashboard', component: Dashboard },
  {
  path: 'add-task',
loadComponent: () => import('./add-task/add-task').then(m => m.AddTask)
},
{
  path: 'edit-task/:id',
  loadComponent: () => import('./edit-task/edit-task').then(m => m.EditTask)
}


];
