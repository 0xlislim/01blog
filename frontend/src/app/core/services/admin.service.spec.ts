import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { AdminService } from './admin.service';
import { environment } from '../../../environments/environment';
import { AdminUserResponse, Post, Report } from '../models';

describe('AdminService', () => {
  let service: AdminService;
  let httpMock: HttpTestingController;

  const mockUser: AdminUserResponse = {
    id: 1,
    username: 'testuser',
    email: 'test@example.com',
    displayName: 'Test User',
    role: 'USER',
    banned: false,
    createdAt: '2024-01-01T00:00:00Z',
    postCount: 5,
    subscriberCount: 10
  };

  const mockPost: Post = {
    id: 1,
    content: 'Test post',
    createdAt: '2024-01-01T00:00:00Z',
    updatedAt: '2024-01-01T00:00:00Z',
    userId: 1,
    username: 'testuser',
    userDisplayName: 'Test User',
    likeCount: 5,
    commentCount: 2,
    likedByCurrentUser: false
  };

  const mockReport: Report = {
    id: 1,
    reason: 'Inappropriate content',
    createdAt: '2024-01-01T00:00:00Z',
    reporterId: 2,
    reporterUsername: 'reporter',
    reportedUserId: 1,
    reportedUsername: 'testuser'
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [AdminService]
    });

    service = TestBed.inject(AdminService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('Users Management', () => {
    describe('getAllUsers', () => {
      it('should return all users', () => {
        const mockUsers: AdminUserResponse[] = [mockUser];

        service.getAllUsers().subscribe(users => {
          expect(users).toEqual(mockUsers);
          expect(users.length).toBe(1);
        });

        const req = httpMock.expectOne(`${environment.apiUrl}/admin/users`);
        expect(req.request.method).toBe('GET');
        req.flush(mockUsers);
      });
    });

    describe('banUser', () => {
      it('should ban a user', () => {
        service.banUser(1).subscribe(response => {
          expect(response).toBeNull();
        });

        const req = httpMock.expectOne(`${environment.apiUrl}/admin/users/1/ban`);
        expect(req.request.method).toBe('POST');
        req.flush(null);
      });
    });

    describe('unbanUser', () => {
      it('should unban a user', () => {
        service.unbanUser(1).subscribe(response => {
          expect(response).toBeNull();
        });

        const req = httpMock.expectOne(`${environment.apiUrl}/admin/users/1/unban`);
        expect(req.request.method).toBe('POST');
        req.flush(null);
      });
    });

    describe('deleteUser', () => {
      it('should delete a user', () => {
        service.deleteUser(1).subscribe(response => {
          expect(response).toBeNull();
        });

        const req = httpMock.expectOne(`${environment.apiUrl}/admin/users/1`);
        expect(req.request.method).toBe('DELETE');
        req.flush(null);
      });
    });
  });

  describe('Posts Management', () => {
    describe('getAllPosts', () => {
      it('should return all posts', () => {
        const mockPosts: Post[] = [mockPost];

        service.getAllPosts().subscribe(posts => {
          expect(posts).toEqual(mockPosts);
          expect(posts.length).toBe(1);
        });

        const req = httpMock.expectOne(`${environment.apiUrl}/admin/posts`);
        expect(req.request.method).toBe('GET');
        req.flush(mockPosts);
      });
    });

    describe('deletePost', () => {
      it('should delete a post', () => {
        service.deletePost(1).subscribe(response => {
          expect(response).toBeNull();
        });

        const req = httpMock.expectOne(`${environment.apiUrl}/admin/posts/1`);
        expect(req.request.method).toBe('DELETE');
        req.flush(null);
      });
    });
  });

  describe('Reports Management', () => {
    describe('getAllReports', () => {
      it('should return all reports', () => {
        const mockReports: Report[] = [mockReport];

        service.getAllReports().subscribe(reports => {
          expect(reports).toEqual(mockReports);
          expect(reports.length).toBe(1);
        });

        const req = httpMock.expectOne(`${environment.apiUrl}/admin/reports`);
        expect(req.request.method).toBe('GET');
        req.flush(mockReports);
      });
    });

    describe('dismissReport', () => {
      it('should dismiss a report', () => {
        service.dismissReport(1).subscribe(response => {
          expect(response).toBeNull();
        });

        const req = httpMock.expectOne(`${environment.apiUrl}/admin/reports/1`);
        expect(req.request.method).toBe('DELETE');
        req.flush(null);
      });
    });
  });
});
