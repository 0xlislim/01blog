import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { AuthService } from './auth.service';
import { environment } from '../../../environments/environment';
import { JwtResponse, LoginRequest, RegisterRequest } from '../models';

describe('AuthService', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;

  const mockJwtResponse: JwtResponse = {
    accessToken: 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwiZXhwIjo5OTk5OTk5OTk5fQ.signature',
    tokenType: 'Bearer',
    userId: 1,
    username: 'testuser',
    role: 'USER'
  };

  const mockAdminJwtResponse: JwtResponse = {
    ...mockJwtResponse,
    role: 'ADMIN'
  };

  beforeEach(() => {
    // Clear localStorage before each test
    localStorage.clear();

    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [AuthService]
    });

    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
    localStorage.clear();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('login', () => {
    it('should send login request and store auth data', () => {
      const credentials: LoginRequest = {
        usernameOrEmail: 'testuser',
        password: 'password123'
      };

      service.login(credentials).subscribe(response => {
        expect(response).toEqual(mockJwtResponse);
        expect(localStorage.getItem('auth_token')).toBe(mockJwtResponse.accessToken);
        expect(localStorage.getItem('auth_user')).toBe(JSON.stringify(mockJwtResponse));
      });

      const req = httpMock.expectOne(`${environment.apiUrl}/auth/login`);
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual(credentials);
      req.flush(mockJwtResponse);
    });
  });

  describe('register', () => {
    it('should send register request', () => {
      const userData: RegisterRequest = {
        username: 'newuser',
        email: 'newuser@example.com',
        password: 'password123',
        displayName: 'New User'
      };

      const mockResponse = { message: 'User registered successfully' };

      service.register(userData).subscribe(response => {
        expect(response).toEqual(mockResponse);
      });

      const req = httpMock.expectOne(`${environment.apiUrl}/auth/register`);
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual(userData);
      req.flush(mockResponse);
    });
  });

  describe('logout', () => {
    it('should clear auth data from localStorage', () => {
      localStorage.setItem('auth_token', 'test_token');
      localStorage.setItem('auth_user', JSON.stringify(mockJwtResponse));

      service.logout();

      expect(localStorage.getItem('auth_token')).toBeNull();
      expect(localStorage.getItem('auth_user')).toBeNull();
    });

    it('should emit null for currentUser$', (done) => {
      service.logout();

      service.currentUser$.subscribe(user => {
        expect(user).toBeNull();
        done();
      });
    });
  });

  describe('getToken', () => {
    it('should return token from localStorage', () => {
      localStorage.setItem('auth_token', 'test_token');
      expect(service.getToken()).toBe('test_token');
    });

    it('should return null if no token exists', () => {
      expect(service.getToken()).toBeNull();
    });
  });

  describe('isLoggedIn', () => {
    it('should return false if no token exists', () => {
      expect(service.isLoggedIn()).toBeFalse();
    });

    it('should return false for invalid token', () => {
      localStorage.setItem('auth_token', 'invalid_token');
      expect(service.isLoggedIn()).toBeFalse();
    });

    it('should return true for valid non-expired token', () => {
      // Create a token with exp far in the future
      const futureExp = Math.floor(Date.now() / 1000) + 3600; // 1 hour from now
      const payload = btoa(JSON.stringify({ sub: '1', exp: futureExp }));
      const validToken = `header.${payload}.signature`;
      localStorage.setItem('auth_token', validToken);

      expect(service.isLoggedIn()).toBeTrue();
    });

    it('should return false for expired token', () => {
      // Create a token with exp in the past
      const pastExp = Math.floor(Date.now() / 1000) - 3600; // 1 hour ago
      const payload = btoa(JSON.stringify({ sub: '1', exp: pastExp }));
      const expiredToken = `header.${payload}.signature`;
      localStorage.setItem('auth_token', expiredToken);

      expect(service.isLoggedIn()).toBeFalse();
    });
  });

  describe('isAdmin', () => {
    it('should return false when no user is logged in', () => {
      expect(service.isAdmin()).toBeFalse();
    });

    it('should return false for regular user', () => {
      // Login as regular user
      const credentials: LoginRequest = { usernameOrEmail: 'user', password: 'pass' };
      service.login(credentials).subscribe();

      const req = httpMock.expectOne(`${environment.apiUrl}/auth/login`);
      req.flush(mockJwtResponse);

      expect(service.isAdmin()).toBeFalse();
    });

    it('should return true for admin user', () => {
      // Login as admin
      const credentials: LoginRequest = { usernameOrEmail: 'admin', password: 'pass' };
      service.login(credentials).subscribe();

      const req = httpMock.expectOne(`${environment.apiUrl}/auth/login`);
      req.flush(mockAdminJwtResponse);

      expect(service.isAdmin()).toBeTrue();
    });
  });

  describe('getCurrentUser', () => {
    it('should return null when no user is logged in', () => {
      expect(service.getCurrentUser()).toBeNull();
    });

    it('should return current user after login', () => {
      const credentials: LoginRequest = { usernameOrEmail: 'user', password: 'pass' };
      service.login(credentials).subscribe();

      const req = httpMock.expectOne(`${environment.apiUrl}/auth/login`);
      req.flush(mockJwtResponse);

      expect(service.getCurrentUser()).toEqual(mockJwtResponse);
    });
  });

  describe('getCurrentUserId', () => {
    it('should return null when no user is logged in', () => {
      expect(service.getCurrentUserId()).toBeNull();
    });

    it('should return user ID after login', () => {
      const credentials: LoginRequest = { usernameOrEmail: 'user', password: 'pass' };
      service.login(credentials).subscribe();

      const req = httpMock.expectOne(`${environment.apiUrl}/auth/login`);
      req.flush(mockJwtResponse);

      expect(service.getCurrentUserId()).toBe(1);
    });
  });
});
