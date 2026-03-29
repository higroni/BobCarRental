import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatCardModule } from '@angular/material/card';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatChipsModule } from '@angular/material/chips';
import { FormsModule } from '@angular/forms';
import { UserService } from '../../../core/services/user.service';
import { UserResponse } from '../../../models/user.model';

@Component({
  selector: 'app-user-list',
  standalone: true,
  imports: [
    CommonModule,
    MatTableModule,
    MatButtonModule,
    MatIconModule,
    MatTooltipModule,
    MatCardModule,
    MatInputModule,
    MatFormFieldModule,
    MatChipsModule,
    FormsModule
  ],
  templateUrl: './user-list.component.html',
  styleUrl: './user-list.component.css'
})
export class UserListComponent implements OnInit {
  users: UserResponse[] = [];
  filteredUsers: UserResponse[] = [];
  displayedColumns: string[] = ['username', 'email', 'fullName', 'roles', 'enabled', 'actions'];
  searchTerm: string = '';
  isLoading: boolean = false;
  error: string | null = null;

  constructor(
    private userService: UserService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadUsers();
  }

  loadUsers(): void {
    this.isLoading = true;
    this.error = null;
    
    this.userService.getAllUsersNoPagination().subscribe({
      next: (users) => {
        this.users = users;
        this.filteredUsers = users;
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error loading users:', error);
        this.error = 'Failed to load users. Please try again.';
        this.isLoading = false;
      }
    });
  }

  applyFilter(): void {
    const term = this.searchTerm.toLowerCase().trim();
    
    if (!term) {
      this.filteredUsers = this.users;
      return;
    }

    this.filteredUsers = this.users.filter(user =>
      user.username?.toLowerCase().includes(term) ||
      user.email?.toLowerCase().includes(term) ||
      user.fullName?.toLowerCase().includes(term) ||
      user.roles?.some(role => role.toLowerCase().includes(term))
    );
  }

  editUser(id: number): void {
    this.router.navigate(['/users', id, 'edit']);
  }

  deleteUser(id: number, username: string): void {
    if (confirm(`Are you sure you want to delete user "${username}"?`)) {
      this.userService.deleteUser(id).subscribe({
        next: () => {
          this.loadUsers();
        },
        error: (error) => {
          console.error('Error deleting user:', error);
          if (error.error?.message) {
            alert(error.error.message);
          } else {
            alert('Failed to delete user. Please try again.');
          }
        }
      });
    }
  }

  createUser(): void {
    this.router.navigate(['/users/new']);
  }

  getRoleBadgeClass(role: string): string {
    return role === 'ROLE_ADMIN' ? 'role-admin' : 'role-user';
  }

  getRoleDisplay(role: string): string {
    return role.replace('ROLE_', '');
  }
}

// Made with Bob