import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { AccountService } from '../../../core/services/account.service';
import { AccountRequest } from '../../../models';

@Component({
  selector: 'app-account-form',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatCheckboxModule
  ],
  templateUrl: './account-form.component.html',
  styleUrl: './account-form.component.scss'
})
export class AccountFormComponent implements OnInit {
  accountForm: FormGroup;
  isEditMode = false;
  accountId: number | null = null;
  isSubmitting = false;

  constructor(
    private fb: FormBuilder,
    private accountService: AccountService,
    private router: Router,
    private route: ActivatedRoute
  ) {
    this.accountForm = this.fb.group({
      desc: ['', [Validators.required, Validators.maxLength(15)]],
      num: [null, [Validators.max(9223372036854775807)]],
      date: [new Date(), Validators.required],
      clientId: ['', [Validators.required, Validators.maxLength(10)]],
      amount: [0, [Validators.required, Validators.min(0)]],
      tagged: [false]
    });
  }

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.isEditMode = true;
      this.accountId = +id;
      this.loadAccount(this.accountId);
    }
  }

  loadAccount(id: number): void {
    this.accountService.getAccountById(id).subscribe({
      next: (account) => {
        this.accountForm.patchValue({
          desc: account.desc,
          num: account.num,
          date: new Date(account.date),
          clientId: account.clientId,
          amount: account.currentBalance || 0,
          tagged: account.tagged
        });
      },
      error: (error) => {
        console.error('Error loading account:', error);
        alert('Failed to load account');
        this.router.navigate(['/accounts']);
      }
    });
  }

  onSubmit(): void {
    if (this.accountForm.valid && !this.isSubmitting) {
      this.isSubmitting = true;
      const formValue = this.accountForm.value;
      
      const request: AccountRequest = {
        desc: formValue.desc,
        num: formValue.num,
        date: this.formatDate(formValue.date),
        clientId: formValue.clientId,
        amount: formValue.amount,
        tagged: formValue.tagged
      };

      const operation = this.isEditMode && this.accountId
        ? this.accountService.updateAccount(this.accountId, request)
        : this.accountService.createAccount(request);

      operation.subscribe({
        next: () => {
          this.router.navigate(['/accounts']);
        },
        error: (error) => {
          console.error('Error saving account:', error);
          const errorMessage = error.message || 'Failed to save account';
          alert(errorMessage);
          this.isSubmitting = false;
        }
      });
    }
  }

  onCancel(): void {
    this.router.navigate(['/accounts']);
  }

  private formatDate(date: Date): string {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  }
}

// Made with Bob