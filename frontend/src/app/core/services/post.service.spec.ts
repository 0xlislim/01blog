import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { PostService } from './post.service';
import { environment } from '../../../environments/environment';
import { Post, PostRequest, Comment, CommentRequest } from '../models';

describe('PostService', () => {
  let service: PostService;
  let httpMock: HttpTestingController;

  const mockPost: Post = {
    id: 1,
    content: 'Test post content',
    mediaUrl: undefined,
    mediaType: undefined,
    createdAt: '2024-01-01T00:00:00Z',
    updatedAt: '2024-01-01T00:00:00Z',
    userId: 1,
    username: 'testuser',
    userDisplayName: 'Test User',
    likeCount: 5,
    commentCount: 2,
    likedByCurrentUser: false
  };

  const mockComment: Comment = {
    id: 1,
    content: 'Test comment',
    createdAt: '2024-01-01T00:00:00Z',
    userId: 2,
    username: 'commenter',
    userDisplayName: 'Commenter User'
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [PostService]
    });

    service = TestBed.inject(PostService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('getFeed', () => {
    it('should return feed posts', () => {
      const mockPosts: Post[] = [mockPost];

      service.getFeed().subscribe(posts => {
        expect(posts).toEqual(mockPosts);
        expect(posts.length).toBe(1);
      });

      const req = httpMock.expectOne(`${environment.apiUrl}/posts/feed`);
      expect(req.request.method).toBe('GET');
      req.flush(mockPosts);
    });
  });

  describe('getPost', () => {
    it('should return a single post by ID', () => {
      service.getPost(1).subscribe(post => {
        expect(post).toEqual(mockPost);
      });

      const req = httpMock.expectOne(`${environment.apiUrl}/posts/1`);
      expect(req.request.method).toBe('GET');
      req.flush(mockPost);
    });
  });

  describe('getUserPosts', () => {
    it('should return posts for a specific user', () => {
      const mockPosts: Post[] = [mockPost];

      service.getUserPosts(1).subscribe(posts => {
        expect(posts).toEqual(mockPosts);
      });

      const req = httpMock.expectOne(`${environment.apiUrl}/posts/user/1`);
      expect(req.request.method).toBe('GET');
      req.flush(mockPosts);
    });
  });

  describe('createPost', () => {
    it('should create a new post', () => {
      const postRequest: PostRequest = {
        content: 'New post content'
      };

      service.createPost(postRequest).subscribe(post => {
        expect(post).toEqual(mockPost);
      });

      const req = httpMock.expectOne(`${environment.apiUrl}/posts`);
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual(postRequest);
      req.flush(mockPost);
    });

    it('should create a post with media', () => {
      const postRequest: PostRequest = {
        content: 'Post with image',
        mediaUrl: 'http://example.com/image.jpg',
        mediaType: 'IMAGE'
      };

      service.createPost(postRequest).subscribe(post => {
        expect(post).toBeTruthy();
      });

      const req = httpMock.expectOne(`${environment.apiUrl}/posts`);
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual(postRequest);
      req.flush({ ...mockPost, ...postRequest });
    });
  });

  describe('updatePost', () => {
    it('should update an existing post', () => {
      const postRequest: PostRequest = {
        content: 'Updated content'
      };
      const updatedPost = { ...mockPost, content: 'Updated content' };

      service.updatePost(1, postRequest).subscribe(post => {
        expect(post.content).toBe('Updated content');
      });

      const req = httpMock.expectOne(`${environment.apiUrl}/posts/1`);
      expect(req.request.method).toBe('PUT');
      expect(req.request.body).toEqual(postRequest);
      req.flush(updatedPost);
    });
  });

  describe('deletePost', () => {
    it('should delete a post', () => {
      service.deletePost(1).subscribe(response => {
        expect(response).toBeNull();
      });

      const req = httpMock.expectOne(`${environment.apiUrl}/posts/1`);
      expect(req.request.method).toBe('DELETE');
      req.flush(null);
    });
  });

  describe('toggleLike', () => {
    it('should toggle like on a post', () => {
      const likedPost = { ...mockPost, likedByCurrentUser: true, likeCount: 6 };

      service.toggleLike(1).subscribe(post => {
        expect(post.likedByCurrentUser).toBeTrue();
        expect(post.likeCount).toBe(6);
      });

      const req = httpMock.expectOne(`${environment.apiUrl}/posts/1/like`);
      expect(req.request.method).toBe('POST');
      req.flush(likedPost);
    });
  });

  describe('getComments', () => {
    it('should return comments for a post', () => {
      const mockComments: Comment[] = [mockComment];

      service.getComments(1).subscribe(comments => {
        expect(comments).toEqual(mockComments);
        expect(comments.length).toBe(1);
      });

      const req = httpMock.expectOne(`${environment.apiUrl}/posts/1/comments`);
      expect(req.request.method).toBe('GET');
      req.flush(mockComments);
    });
  });

  describe('addComment', () => {
    it('should add a comment to a post', () => {
      const commentRequest: CommentRequest = {
        content: 'New comment'
      };

      service.addComment(1, commentRequest).subscribe(comment => {
        expect(comment).toEqual(mockComment);
      });

      const req = httpMock.expectOne(`${environment.apiUrl}/posts/1/comments`);
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual(commentRequest);
      req.flush(mockComment);
    });
  });

  describe('deleteComment', () => {
    it('should delete a comment', () => {
      service.deleteComment(1).subscribe(response => {
        expect(response).toBeNull();
      });

      const req = httpMock.expectOne(`${environment.apiUrl}/posts/comments/1`);
      expect(req.request.method).toBe('DELETE');
      req.flush(null);
    });
  });
});
