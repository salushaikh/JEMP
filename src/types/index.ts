export enum NavSection {
  HOW_IT_WORKS = 'HOW_IT_WORKS',
  WORKSPACES = 'WORKSPACES',
  PROOF_OF_WORK = 'PROOF_OF_WORK',
  TRANSPARENCY = 'TRANSPARENCY',
  FAQ = 'FAQ',
  APPLY = 'APPLY',
  // Internal sections
  OVERVIEW = 'OVERVIEW',
  PHILOSOPHY = 'PHILOSOPHY',
  SAFETY = 'SAFETY'
}

export type EmploymentStatusOption = 'YES' | 'NO' | 'PREFER_NOT_TO_SAY';

export interface ApplicationSubmission {
  id: string; // e.g. JEMP-APP-9821
  fullName: string;
  email: string;
  employmentStatus: EmploymentStatusOption;
  resumeFileName: string;
  resumeFileSize: string;
  consentAgreed: boolean;
  submittedAt: string;
  status: 'PENDING' | 'UNDER_REVIEW' | 'APPROVED' | 'WAITLISTED' | 'REJECTED';
  assignedMemberId?: string;
  reviewerNotes?: string;
  isGenuinelyJobless?: boolean;
}

export type BrainstormStatus = 'DRAFT' | 'BRAINSTORMING' | 'COOKING' | 'PAUSED' | 'ALIVE' | 'CANCELLED' | 'PROMOTED';

export interface SquadApplicant {
  id: string;
  memberId: string;
  memberName: string;
  avatarUrl?: string;
  role: string;
  reason: string;
  skills: string[];
  appliedAt: string;
  status: 'PENDING' | 'ACCEPTED' | 'REJECTED';
}

export interface BrainstormTask {
  id: string;
  title: string;
  assignee?: string;
  completed: boolean;
}

export interface BrainstormResource {
  id: string;
  title: string;
  url: string;
  category: 'DOC' | 'DESIGN' | 'CODE' | 'RESEARCH';
}

export interface BrainstormIdea {
  id: string; // e.g. B#027
  title: string;
  champion: string;
  description: string;
  problem: string;
  proposedDirection: string;
  audience: string;
  desiredSkills: string[];
  tags: string[];
  expectedCommitment: string;
  status: BrainstormStatus;
  statusReason?: string;
  targetDeliverable?: string;
  requiredRoles?: string[];
  activeSquadMembers: string[];
  applicants: SquadApplicant[];
  tasks: BrainstormTask[];
  resources: BrainstormResource[];
  decisions: string[];
  createdAt: string;
  votes: number;
  userVoted?: boolean;
}

export type ProjectStatus = 'COOKING' | 'IN_PROGRESS' | 'REVIEW' | 'COMPLETED' | 'PAUSED';

export interface MilestoneVerification {
  verifiedBy: string;
  timestamp: string;
  proofHash?: string;
}

export interface ProjectMilestone {
  id: string;
  title: string;
  completed: boolean;
  completedAt?: string;
  verification?: MilestoneVerification;
  notes?: string;
}

export interface OngoingProject {
  id: string; // e.g. P#014
  brainstormSourceId?: string; // e.g. B#027 or HC#003
  title: string;
  champion: string;
  description: string;
  status: ProjectStatus;
  statusReason?: string;
  progressPercentage: number;
  contributors?: string[];
  squadMembers?: string[];
  activeSprint?: string;
  milestones: ProjectMilestone[];
  repoUrl?: string;
  liveUrl?: string;
  startedAt?: string;
  originType: 'BRAINSTORM' | 'EXTERNAL_OPPORTUNITY' | 'INTERNAL_INITIATIVE';
}

export type ClientProjectStatus = 'SCOPING' | 'ESCROW_FUNDED' | 'DEVELOPING' | 'DELIVERED' | 'PAID_OUT';

export interface ClientProject {
  id: string; // e.g. HC#003
  title: string;
  clientType: string;
  description: string;
  budgetEstimated: string;
  paymentStructure: string;
  expectedTimeline: string;
  status: ClientProjectStatus;
  requiredSkills: string[];
  assignedSquad: string[];
  internalReviewPassed: boolean;
  scopeLocked: boolean;
  clientName?: string;
  budgetUsd?: number;
}

export type TimelineCategory = 
  | 'ONBOARDING'
  | 'BRAINSTORM'
  | 'PROJECT'
  | 'CLIENT_WORK'
  | 'MILESTONE'
  | 'GOVERNANCE'
  | 'CONTRIBUTION';

export interface TimelineEntry {
  id: string;
  memberId: string;
  timestamp: string;
  date?: string;
  title: string;
  category: TimelineCategory;
  projectRef?: string; // e.g. P#014, B#027, HC#003
  relatedCode?: string;
  description: string;
  details?: string[];
  verificationHash?: string;
  proofHash?: string;
  verifiedBy?: string;
}

export interface MemberProfile {
  id: string; // e.g. JEMP#027
  name: string;
  handle: string;
  avatarUrl: string;
  role: string;
  joinedDate: string;
  status: 'ACTIVE' | 'ONBOARDING' | 'APPLICANT' | 'SUSPENDED';
  reputationScore: number;
  contributedProjectsCount: number;
  totalClientEarnedUsd: number;
  timeline: TimelineEntry[];
}

export type PayrollStageStatus = 
  | 'QUOTATION_VERIFIED'
  | 'ESCROW_RECEIVED'
  | 'MILESTONE_ACCEPTED'
  | 'PAYOUT_EXECUTED'
  | 'RECONCILED';

export interface ProofPackage {
  invoiceHash: string;
  utrTransactionReference: string;
  bankConfirmationReceipt: string;
  reviewerSignatures: string[];
  quorumReached: boolean;
}

export interface ContributorPayoutShare {
  memberId: string;
  memberName: string;
  role: string;
  percentage: number;
  amountUsd: number;
  amountInr?: number;
  status: 'PENDING' | 'ESCROWED' | 'DISBURSED';
  payoutTxHash?: string;
}

export interface FinancialStageDetail {
  stageId: string; // e.g. PAY#001
  clientProjectId: string; // e.g. HC#003
  projectTitle: string;
  clientName: string;
  totalBudgetUsd: number;
  adminFeePercentage: number;
  adminFeeUsd: number;
  charityPercentage?: number;
  charityUsd?: number;
  contributorPoolUsd: number;
  status: PayrollStageStatus;
  proofPackage: ProofPackage;
  payoutDistribution: ContributorPayoutShare[];
  lastUpdated: string;
}

export type TimelineMilestone = TimelineEntry;
export type PayrollStage = FinancialStageDetail;

export interface FaqItem {
  id: string;
  question: string;
  answer: string;
  category: 'GENERAL' | 'APPLICATION' | 'BUILDING' | 'TIMELINE' | 'PAYMENTS' | 'SAFETY';
}
