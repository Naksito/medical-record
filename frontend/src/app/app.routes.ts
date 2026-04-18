import {Routes} from '@angular/router';
import {LogInComponent} from './pages/log-in/log-in.component';
import {RegisterComponent} from './pages/register/register.component';
import {HomeComponent} from './pages/home/home.component';
import {DashboardComponent} from './pages/dashboard/dashboard.component';
import {MenuComponent} from './pages/menu/menu.component';
import {UpdateDiagnosisComponent} from './components/update-diagnosis/update-diagnosis.component';
import {AdminQueryComponent} from './pages/admin-query/admin-query.component';
import {AuthGuard} from './guards/auth.guard';

export const routes: Routes = [
  {
    path: '',
    component: HomeComponent,
    canActivate: [AuthGuard],
    data: { 'page': 'home' }
  },
  {
    path: 'login',
    component: LogInComponent,
    canActivate: [AuthGuard],
    data: { 'page': 'login' }
  },
  {
    path: 'register',
    component: RegisterComponent,
    canActivate: [AuthGuard],
    data: { 'page': 'register' }
  },
  {
    path: 'dashboard',
    component: DashboardComponent,
    canActivate: [AuthGuard],
    data: { 'page': 'dashboard' }
  },
  {
    path: 'menu',
    component: MenuComponent,
    canActivate: [AuthGuard],
    data: {'page': 'menu'}
  },
  {
    path: 'add-diagnosis/:id',
    component: UpdateDiagnosisComponent
  },
  {
    path: 'admin-query',
    component: AdminQueryComponent,
    canActivate: [AuthGuard],
    data: { 'page': 'admin-query' }
  }
];
