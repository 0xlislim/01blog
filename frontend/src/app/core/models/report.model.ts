export interface Report {
  id: number;
  reason: string;
  createdAt: string;
  reporterId: number;
  reporterUsername: string;
  reportedUserId: number;
  reportedUsername: string;
}

export interface CreateReportRequest {
  reportedUserId: number;
  reason: string;
}
