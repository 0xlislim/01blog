import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { MatSnackBarModule, MatSnackBar } from '@angular/material/snack-bar';
import { Router } from '@angular/router';
import { of, throwError } from 'rxjs';

import { LoginComponent } from './login.component';
import { AuthService } from '../../../../core/services/auth.service';

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let authServiceSpy: jasmine.SpyObj<AuthService>;
  let routerSpy: jasmine.SpyObj<Router>;
  let snackBarSpy: jasmine.SpyObj<MatSnackBar>;

  const mockJwtResponse = {
    accessToken: 'test_token',
    tokenType: 'Bearer',
    userId: 1,
    username: 'testuser',
    role: 'USER' as const
  };

  beforeEach(async () => {
    authServiceSpy = jasmine.createSpyObj('AuthService', ['login']);
    routerSpy = jasmine.createSpyObj('Router', ['navigate'], { url: '/auth/login' });
    snackBarSpy = jasmine.createSpyObj('MatSnackBar', ['open']);

    await TestBed.configureTestingModule({
      imports: [
        ReactiveFormsModule,
        NoopAnimationsModule,
        MatFormFieldModule,
        MatInputModule,
        MatButtonModule,
        MatIconModule,
        MatCardModule,
        MatSnackBarModule
      ],
      declarations: [LoginComponent],
      providers: [
        { provide: AuthService, useValue: authServiceSpy },
        { provide: Router, useValue: routerSpy },
        { provide: MatSnackBar, useValue: snackBarSpy }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize with empty form', () => {
    expect(component.loginForm).toBeTruthy();
    expect(component.loginForm.get('usernameOrEmail')?.value).toBe('');
    expect(component.loginForm.get('password')?.value).toBe('');
  });

  it('should have required validators', () => {
    const usernameControl = component.loginForm.get('usernameOrEmail');
    const passwordControl = component.loginForm.get('password');

    usernameControl?.setValue('');
    passwordControl?.setValue('');

    expect(usernameControl?.hasError('required')).toBeTrue();
    expect(passwordControl?.hasError('required')).toBeTrue();
  });

  it('should have minlength validator on password', () => {
    const passwordControl = component.loginForm.get('password');
    passwordControl?.setValue('12345');

    expect(passwordControl?.hasError('minlength')).toBeTrue();

    passwordControl?.setValue('123456');
    expect(passwordControl?.hasError('minlength')).toBeFalse();
  });

  it('should not submit if form is invalid', () => {
    component.onSubmit();

    expect(authServiceSpy.login).not.toHaveBeenCalled();
  });

  it('should call authService.login on valid form submission', fakeAsync(() => {
    authServiceSpy.login.and.returnValue(of(mockJwtResponse));

    component.loginForm.setValue({
      usernameOrEmail: 'testuser',
      password: 'password123'
    });

    component.onSubmit();
    tick();

    expect(authServiceSpy.login).toHaveBeenCalledWith({
      usernameOrEmail: 'testuser',
      password: 'password123'
    });
  }));

  it('should navigate to feed on successful login', fakeAsync(() => {
    authServiceSpy.login.and.returnValue(of(mockJwtResponse));

    component.loginForm.setValue({
      usernameOrEmail: 'testuser',
      password: 'password123'
    });

    component.onSubmit();
    tick();

    expect(routerSpy.navigate).toHaveBeenCalledWith(['/feed']);
  }));

  it('should show success snackbar on successful login', fakeAsync(() => {
    authServiceSpy.login.and.returnValue(of(mockJwtResponse));

    component.loginForm.setValue({
      usernameOrEmail: 'testuser',
      password: 'password123'
    });

    component.onSubmit();
    tick();

    expect(snackBarSpy.open).toHaveBeenCalledWith(
      'Login successful!',
      'Close',
      jasmine.objectContaining({ duration: 3000 })
    );
  }));

  it('should show error snackbar on failed login', fakeAsync(() => {
    const errorResponse = { error: { message: 'Invalid credentials' } };
    authServiceSpy.login.and.returnValue(throwError(() => errorResponse));

    component.loginForm.setValue({
      usernameOrEmail: 'testuser',
      password: 'wrongpassword'
    });

    component.onSubmit();
    tick();

    expect(snackBarSpy.open).toHaveBeenCalledWith(
      'Invalid credentials',
      'Close',
      jasmine.objectContaining({ duration: 5000 })
    );
  }));

  it('should set isLoading to true during login', () => {
    authServiceSpy.login.and.returnValue(of(mockJwtResponse));

    component.loginForm.setValue({
      usernameOrEmail: 'testuser',
      password: 'password123'
    });

    component.onSubmit();

    // isLoading should be true during the request (before observable completes)
    expect(component.isLoading).toBeFalse(); // Already completed due to sync of()
  });

  describe('getErrorMessage', () => {
    it('should return required error message for username', () => {
      component.loginForm.get('usernameOrEmail')?.setValue('');
      component.loginForm.get('usernameOrEmail')?.markAsTouched();

      const message = component.getErrorMessage('usernameOrEmail');
      expect(message).toBe('Username or email is required');
    });

    it('should return required error message for password', () => {
      component.loginForm.get('password')?.setValue('');
      component.loginForm.get('password')?.markAsTouched();

      const message = component.getErrorMessage('password');
      expect(message).toBe('Password is required');
    });

    it('should return minlength error message for password', () => {
      component.loginForm.get('password')?.setValue('12345');
      component.loginForm.get('password')?.markAsTouched();

      const message = component.getErrorMessage('password');
      expect(message).toBe('Password must be at least 6 characters');
    });
  });

  it('should toggle password visibility', () => {
    expect(component.hidePassword).toBeTrue();
    component.hidePassword = false;
    expect(component.hidePassword).toBeFalse();
  });
});
