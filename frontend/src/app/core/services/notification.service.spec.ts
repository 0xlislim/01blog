import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { NotificationService } from './notification.service';
import { environment } from '../../../environments/environment';
import { Notification } from '../models';

describe('NotificationService', () => {
  let service: NotificationService;
  let httpMock: HttpTestingController;

  const mockNotification: Notification = {
    id: 1,
    type: 'NEW_POST',
    message: 'User posted something new',
    read: false,
    createdAt: '2024-01-01T00:00:00Z',
    relatedUserId: 2,
    relatedUsername: 'otheruser'
  };

  const mockReadNotification: Notification = {
    ...mockNotification,
    id: 2,
    read: true
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [NotificationService]
    });

    service = TestBed.inject(NotificationService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('getNotifications', () => {
    it('should return all notifications', () => {
      const mockNotifications: Notification[] = [mockNotification, mockReadNotification];

      service.getNotifications().subscribe(notifications => {
        expect(notifications).toEqual(mockNotifications);
        expect(notifications.length).toBe(2);
      });

      const req = httpMock.expectOne(`${environment.apiUrl}/notifications`);
      expect(req.request.method).toBe('GET');
      req.flush(mockNotifications);
    });
  });

  describe('getUnreadNotifications', () => {
    it('should return only unread notifications', () => {
      const mockUnread: Notification[] = [mockNotification];

      service.getUnreadNotifications().subscribe(notifications => {
        expect(notifications).toEqual(mockUnread);
        expect(notifications.every(n => !n.read)).toBeTrue();
      });

      const req = httpMock.expectOne(`${environment.apiUrl}/notifications/unread`);
      expect(req.request.method).toBe('GET');
      req.flush(mockUnread);
    });
  });

  describe('getUnreadCount', () => {
    it('should return unread count and update subject', () => {
      const mockResponse = { count: 5 };

      service.getUnreadCount().subscribe(response => {
        expect(response.count).toBe(5);
      });

      const req = httpMock.expectOne(`${environment.apiUrl}/notifications/unread-count`);
      expect(req.request.method).toBe('GET');
      req.flush(mockResponse);

      // Check that the subject was updated
      service.unreadCount$.subscribe(count => {
        expect(count).toBe(5);
      });
    });
  });

  describe('markAsRead', () => {
    it('should mark notification as read and decrement count', () => {
      // First set up the unread count
      service.getUnreadCount().subscribe();
      const countReq = httpMock.expectOne(`${environment.apiUrl}/notifications/unread-count`);
      countReq.flush({ count: 3 });

      // Now mark as read
      const readNotification = { ...mockNotification, read: true };

      service.markAsRead(1).subscribe(notification => {
        expect(notification.read).toBeTrue();
      });

      const req = httpMock.expectOne(`${environment.apiUrl}/notifications/1/read`);
      expect(req.request.method).toBe('PUT');
      req.flush(readNotification);

      // Check count was decremented
      service.unreadCount$.subscribe(count => {
        expect(count).toBe(2);
      });
    });
  });

  describe('markAllAsRead', () => {
    it('should mark all notifications as read and reset count to 0', () => {
      // First set up the unread count
      service.getUnreadCount().subscribe();
      const countReq = httpMock.expectOne(`${environment.apiUrl}/notifications/unread-count`);
      countReq.flush({ count: 5 });

      // Mark all as read
      service.markAllAsRead().subscribe();

      const req = httpMock.expectOne(`${environment.apiUrl}/notifications/read-all`);
      expect(req.request.method).toBe('PUT');
      req.flush(null);

      // Check count is now 0
      service.unreadCount$.subscribe(count => {
        expect(count).toBe(0);
      });
    });
  });

  describe('deleteNotification', () => {
    it('should delete a notification', () => {
      service.deleteNotification(1).subscribe(response => {
        expect(response).toBeNull();
      });

      const req = httpMock.expectOne(`${environment.apiUrl}/notifications/1`);
      expect(req.request.method).toBe('DELETE');
      req.flush(null);
    });
  });

  describe('unreadCount$', () => {
    it('should start with 0', (done) => {
      service.unreadCount$.subscribe(count => {
        expect(count).toBe(0);
        done();
      });
    });
  });
});
