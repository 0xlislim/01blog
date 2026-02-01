import { Component, OnInit } from '@angular/core';
import { FormControl } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { debounceTime, distinctUntilChanged, switchMap, of } from 'rxjs';
import { UserService } from '../../../../core/services/user.service';
import { FileService } from '../../../../core/services/file.service';
import { UserProfile } from '../../../../core/models';

@Component({
  selector: 'app-user-search',
  templateUrl: './user-search.component.html',
  styleUrls: ['./user-search.component.scss']
})
export class UserSearchComponent implements OnInit {
  searchControl = new FormControl('');
  users: UserProfile[] = [];
  isLoading = false;
  hasSearched = false;

  constructor(
    private userService: UserService,
    private fileService: FileService,
    private router: Router,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    // Check for query parameter from navbar search
    this.route.queryParams.subscribe(params => {
      if (params['q']) {
        this.searchControl.setValue(params['q']);
      }
    });

    this.searchControl.valueChanges.pipe(
      debounceTime(300),
      distinctUntilChanged(),
      switchMap(query => {
        if (!query || query.trim().length < 2) {
          this.hasSearched = false;
          return of([]);
        }
        this.isLoading = true;
        this.hasSearched = true;
        return this.userService.searchUsers(query.trim());
      })
    ).subscribe({
      next: (users) => {
        this.users = users;
        this.isLoading = false;
      },
      error: () => {
        this.users = [];
        this.isLoading = false;
      }
    });
  }

  getAvatarUrl(avatarUrl: string | undefined): string {
    if (!avatarUrl) return '';
    return this.fileService.getFullMediaUrl(avatarUrl);
  }

  viewProfile(userId: number): void {
    this.router.navigate(['/users', userId]);
  }

  clearSearch(): void {
    this.searchControl.setValue('');
    this.users = [];
    this.hasSearched = false;
  }
}
