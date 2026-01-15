import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-post-card-skeleton',
  templateUrl: './post-card-skeleton.component.html',
  styleUrls: ['./post-card-skeleton.component.scss']
})
export class PostCardSkeletonComponent {
  @Input() count = 3;

  get skeletons(): number[] {
    return Array(this.count).fill(0);
  }
}
