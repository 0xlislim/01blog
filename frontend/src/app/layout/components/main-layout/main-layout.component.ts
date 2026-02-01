import { Component, OnInit, OnDestroy, ViewChild } from '@angular/core';
import { FormControl } from '@angular/forms';
import { MatSidenav } from '@angular/material/sidenav';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { Observable, Subject, interval, of } from 'rxjs';
import { map, shareReplay, takeUntil, debounceTime, distinctUntilChanged, switchMap } from 'rxjs/operators';
import { AuthService } from '../../../core/services/auth.service';
import { NotificationService } from '../../../core/services/notification.service';
import { UserService } from '../../../core/services/user.service';
import { FileService } from '../../../core/services/file.service';
import { UserProfile } from '../../../core/models';
import { Router } from '@angular/router';

@Component({
  selector: 'app-main-layout',
  templateUrl: './main-layout.component.html',
  styleUrls: ['./main-layout.component.scss']
})
export class MainLayoutComponent implements OnInit, OnDestroy {
  @ViewChild('sidenav') sidenav!: MatSidenav;

  private destroy$ = new Subject<void>();

  isHandset$: Observable<boolean>;
  currentUser$ = this.authService.currentUser$;
  unreadCount$ = this.notificationService.unreadCount$;

  searchControl = new FormControl('');
  searchResults: UserProfile[] = [];
  isSearching = false;

  navItems = [
    { icon: 'home', label: 'Feed', route: '/feed' },
    { icon: 'person', label: 'My Profile', route: '/users/profile' },
    { icon: 'notifications', label: 'Notifications', route: '/notifications' },
    { icon: 'search', label: 'Discover', route: '/users/search' }
  ];

  adminNavItems = [
    { icon: 'admin_panel_settings', label: 'Admin Dashboard', route: '/admin' }
  ];

  constructor(
    private breakpointObserver: BreakpointObserver,
    private authService: AuthService,
    private notificationService: NotificationService,
    private userService: UserService,
    private fileService: FileService,
    private router: Router
  ) {
    this.isHandset$ = this.breakpointObserver.observe(Breakpoints.Handset)
      .pipe(
        map(result => result.matches),
        shareReplay()
      );
  }

  ngOnInit(): void {
    // Fetch initial unread count
    this.notificationService.refreshUnreadCount();

    // Poll for new notifications every 30 seconds
    interval(30000)
      .pipe(takeUntil(this.destroy$))
      .subscribe(() => {
        this.notificationService.refreshUnreadCount();
      });

    // Setup search autocomplete
    this.searchControl.valueChanges.pipe(
      debounceTime(300),
      distinctUntilChanged(),
      takeUntil(this.destroy$),
      switchMap(query => {
        if (!query || query.trim().length < 2) {
          this.searchResults = [];
          return of([]);
        }
        this.isSearching = true;
        return this.userService.searchUsers(query.trim());
      })
    ).subscribe({
      next: (users) => {
        this.searchResults = users.slice(0, 5);
        this.isSearching = false;
      },
      error: () => {
        this.searchResults = [];
        this.isSearching = false;
      }
    });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  isAdmin(): boolean {
    return this.authService.isAdmin();
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/auth/login']);
  }

  closeSidenavOnMobile(): void {
    this.isHandset$.pipe(takeUntil(this.destroy$)).subscribe(isHandset => {
      if (isHandset && this.sidenav) {
        this.sidenav.close();
      }
    });
  }

  selectUser(user: UserProfile): void {
    this.searchControl.setValue('');
    this.searchResults = [];
    this.router.navigate(['/users', user.id]);
  }

  getAvatarUrl(avatarUrl: string | undefined): string {
    if (!avatarUrl) return '';
    return this.fileService.getFullMediaUrl(avatarUrl);
  }

  onSearchEnter(): void {
    const query = this.searchControl.value?.trim();
    if (query && query.length >= 2) {
      this.router.navigate(['/users/search'], { queryParams: { q: query } });
      this.searchControl.setValue('');
      this.searchResults = [];
    }
  }

  clearSearch(): void {
    this.searchControl.setValue('');
    this.searchResults = [];
  }
}
