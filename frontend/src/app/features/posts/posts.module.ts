import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { SharedModule } from '../../shared/shared.module';
import { TextFieldModule } from '@angular/cdk/text-field';

// Pages
import { FeedComponent } from './pages/feed/feed.component';

// Components
import { PostCardComponent } from './components/post-card/post-card.component';
import { CommentsSectionComponent } from './components/comments-section/comments-section.component';
import { CreatePostDialogComponent } from './components/create-post-dialog/create-post-dialog.component';
import { EditPostDialogComponent } from './components/edit-post-dialog/edit-post-dialog.component';
import { UserPostsListComponent } from './components/user-posts-list/user-posts-list.component';

const routes: Routes = [
  { path: '', component: FeedComponent }
];

@NgModule({
  declarations: [
    FeedComponent,
    PostCardComponent,
    CommentsSectionComponent,
    CreatePostDialogComponent,
    EditPostDialogComponent,
    UserPostsListComponent
  ],
  imports: [
    SharedModule,
    TextFieldModule,
    RouterModule.forChild(routes)
  ],
  exports: [
    PostCardComponent,
    UserPostsListComponent
  ]
})
export class PostsModule { }
