import {
  BrainstormIdea,
  OngoingProject,
  ClientProject,
  MemberProfile,
  FinancialStageDetail,
  ApplicationSubmission,
  FaqItem
} from '../types';

export const DEMO_BRAINSTORMS: BrainstormIdea[] = [
  {
    id: 'B#027',
    title: 'AI Resume & Portfolio Proof Analyzer',
    champion: 'JEMP#027',
    description: 'An open-source tool that scans engineering portfolios and maps git commits and PRs directly into tangible milestone summaries instead of generic bullet points.',
    problem: 'Traditional resumes reduce 6 months of intense building into invisible 1-line bullet points that recruiters scan past.',
    proposedDirection: 'Build a Next.js + AST parsing engine that extracts verified project milestones, architecture choices, and benchmark results into a recruiter-ready interactive summary.',
    audience: 'Engineers, product builders, and designers between roles who want to showcase tangible evidence.',
    desiredSkills: ['TypeScript', 'Next.js', 'LLM Prompt Engineering', 'UX Design', 'Automated Testing'],
    tags: ['AI/ML', 'Open-Source', 'Developer Tool', 'Portfolio'],
    expectedCommitment: '4–6 hours/week (async)',
    status: 'ALIVE',
    statusReason: 'Promoted to active collaborative project P#014 after squad consensus.',
    targetDeliverable: 'Working CLI tool + Web visualizer',
    activeSquadMembers: ['JEMP#027', 'JEMP#014', 'JEMP#008'],
    applicants: [
      {
        id: 'app-01',
        memberId: 'JEMP#014',
        memberName: 'Elena Rostova',
        role: 'Full-Stack Engineer',
        reason: 'Have built AST analyzers before; want to help design the parsing core.',
        skills: ['Node.js', 'TypeScript', 'AST'],
        appliedAt: 'September 2, 2026',
        status: 'ACCEPTED'
      },
      {
        id: 'app-02',
        memberId: 'JEMP#008',
        memberName: 'Marcus Chen',
        role: 'UI/UX Designer',
        reason: 'Want to craft the dark minimal visualizer theme and export layouts.',
        skills: ['Figma', 'Tailwind CSS', 'Design Systems'],
        appliedAt: 'September 3, 2026',
        status: 'ACCEPTED'
      }
    ],
    tasks: [
      { id: 't-1', title: 'Define AST schema for git commit messages', assignee: 'JEMP#014', completed: true },
      { id: 't-2', title: 'Design exportable Markdown & JSON layout', assignee: 'JEMP#008', completed: true },
      { id: 't-3', title: 'Build CLI test runner & sample dataset', assignee: 'JEMP#027', completed: true }
    ],
    resources: [
      { id: 'r-1', title: 'PRD & Architecture Spec v1.2', url: '#', category: 'DOC' },
      { id: 'r-2', title: 'Interactive Wireframe Prototypes', url: '#', category: 'DESIGN' }
    ],
    decisions: [
      'Decided on standard JSON export schema for maximum interoperability.',
      'Selected TypeScript with zero heavy frontend dependencies for the CLI engine.'
    ],
    createdAt: 'September 2, 2026',
    votes: 42,
    userVoted: false
  },
  {
    id: 'B#031',
    title: 'Decentralized Milestone Attestation Protocol',
    champion: 'JEMP#009',
    description: 'A lightweight cryptographic hashing protocol that signs milestone deliverables and stores immutable proof packages for client handoffs.',
    problem: 'Clients and contractors often dispute what was delivered at each milestone stage when scopes evolve.',
    proposedDirection: 'Generate SHA-256 deliverable bundles with cryptographic reviewer signatures that both parties confirm.',
    audience: 'Independent builders and client project teams.',
    desiredSkills: ['Cryptography', 'Go', 'API Design', 'Security'],
    tags: ['Security', 'Cryptography', 'Milestones'],
    expectedCommitment: '3–5 hours/week',
    status: 'COOKING',
    targetDeliverable: 'Lightweight Go SDK + verification endpoint',
    activeSquadMembers: ['JEMP#009', 'JEMP#019'],
    applicants: [],
    tasks: [
      { id: 't-4', title: 'Draft hashing spec for multi-file bundles', completed: true },
      { id: 't-5', title: 'Implement benchmark testing for large repos', completed: false }
    ],
    resources: [],
    decisions: ['All hashes use salted SHA-256 with timestamp verification.'],
    createdAt: 'September 8, 2026',
    votes: 28,
    userVoted: false
  },
  {
    id: 'B#034',
    title: 'Async Standup & Milestone Bot for Discord',
    champion: 'JEMP#003',
    description: 'A bot that captures async sprint updates, compiles weekly changelogs, and updates contributor timeline records automatically.',
    problem: 'Synchronous standups are hard across timezones and leave no searchable structured trail.',
    proposedDirection: 'Discord slash commands that structure daily blockers and pull directly into project threads.',
    audience: 'Jobless Empire project squads and open-source groups.',
    desiredSkills: ['Discord.js', 'PostgreSQL', 'TypeScript'],
    tags: ['Automation', 'Discord Bot', 'Productivity'],
    expectedCommitment: '2–4 hours/week',
    status: 'BRAINSTORMING',
    activeSquadMembers: ['JEMP#003'],
    applicants: [],
    tasks: [],
    resources: [],
    decisions: [],
    createdAt: 'September 12, 2026',
    votes: 19,
    userVoted: false
  }
];

export const DEMO_PROJECTS: OngoingProject[] = [
  {
    id: 'P#014',
    brainstormSourceId: 'B#027',
    title: 'AI Resume & Proof-of-Work Analyzer Core',
    champion: 'JEMP#027',
    description: 'Full-stack application and CLI tool that converts raw GitHub repository activity and design deliverables into verifiable portfolio proof summaries.',
    status: 'IN_PROGRESS',
    progressPercentage: 75,
    contributors: ['JEMP#027 (Architect)', 'JEMP#014 (Full-stack)', 'JEMP#008 (UI/UX)'],
    squadMembers: ['JEMP#027', 'JEMP#014', 'JEMP#008'],
    activeSprint: 'Sprint 3: Export Engine & Verification API',
    originType: 'BRAINSTORM',
    startedAt: 'September 5, 2026',
    milestones: [
      {
        id: 'm-1',
        title: 'AST Parser & Git Activity Ingestion Engine',
        completed: true,
        completedAt: 'September 10, 2026',
        verification: { verifiedBy: 'Squad Consensus', timestamp: '2026-09-10T18:00:00Z', proofHash: '0x9a8f2c1d4e6b' }
      },
      {
        id: 'm-2',
        title: 'Minimalist Dark UI & Interactive Proof Viewer',
        completed: true,
        completedAt: 'September 18, 2026',
        verification: { verifiedBy: 'Squad Consensus', timestamp: '2026-09-18T14:30:00Z', proofHash: '0x44b1c900e12f' }
      },
      {
        id: 'm-3',
        title: 'Multi-Format Export Engine (PDF, DOCX, Markdown, JSON)',
        completed: false,
        notes: 'In active implementation by JEMP#014.'
      },
      {
        id: 'm-4',
        title: 'Open-Source Release & Public Documentation',
        completed: false,
        notes: 'Scheduled for Sprint 4.'
      }
    ]
  },
  {
    id: 'P#011',
    brainstormSourceId: 'HC#003',
    title: 'AI Customer Support Automation Pipeline',
    champion: 'JEMP#005',
    description: 'Verified client project delivering an intelligent triage and ticket routing pipeline with automated test suites and human-in-the-loop escalation.',
    status: 'IN_PROGRESS',
    progressPercentage: 85,
    contributors: ['JEMP#005 (Lead)', 'JEMP#012 (Backend)', 'JEMP#022 (QA/Docs)'],
    squadMembers: ['JEMP#005', 'JEMP#012', 'JEMP#022'],
    activeSprint: 'Sprint 4: Client Acceptance Testing & Stage 2 Handoff',
    originType: 'EXTERNAL_OPPORTUNITY',
    startedAt: 'August 24, 2026',
    milestones: [
      {
        id: 'm-11',
        title: 'Scope Lock & Architecture Specification',
        completed: true,
        completedAt: 'August 28, 2026',
        verification: { verifiedBy: 'Client & Admin Review', timestamp: '2026-08-28T16:00:00Z', proofHash: '0x3c7e91a0b5f2' }
      },
      {
        id: 'm-12',
        title: 'Core Routing Pipeline & Model Integration',
        completed: true,
        completedAt: 'September 12, 2026',
        verification: { verifiedBy: 'Client Acceptance', timestamp: '2026-09-12T19:00:00Z', proofHash: '0x77d2a891e4c0' }
      },
      {
        id: 'm-13',
        title: 'Milestone 2 Acceptance & Escrow Disbursement (PAY#001)',
        completed: true,
        completedAt: 'September 20, 2026',
        verification: { verifiedBy: 'Admin & Contributor Quorum', timestamp: '2026-09-20T12:00:00Z', proofHash: '0x10b981f4a990' }
      },
      {
        id: 'm-14',
        title: 'Final Deployment & Maintenance Playbook',
        completed: false
      }
    ]
  }
];

export const DEMO_CLIENT_OPPORTUNITIES: ClientProject[] = [
  {
    id: 'HC#003',
    title: 'AI Customer Support Automation & Ticket Triage',
    clientType: 'B2B SaaS Enterprise',
    description: 'Build a production triage engine integrating webhook events, sentiment scoring, and automated responses with 99.9% uptime requirement.',
    budgetEstimated: '₹100,000 INR (~$1,200 USD equivalent)',
    paymentStructure: '40% Advance / 30% Milestone / 30% Final Delivery',
    expectedTimeline: '4 Weeks',
    status: 'DEVELOPING',
    requiredSkills: ['Python', 'FastAPI', 'LLM APIs', 'Docker', 'PostgreSQL'],
    assignedSquad: ['JEMP#005', 'JEMP#012', 'JEMP#022'],
    internalReviewPassed: true,
    scopeLocked: true,
    clientName: 'SaaS Partner (Direct Client)'
  },
  {
    id: 'HC#005',
    title: 'Fintech Dashboard Redesign & Design System',
    clientType: 'Growth-Stage Fintech',
    description: 'High-density transactional dashboard revamp with comprehensive component library, accessibility compliance, and dark-mode tokens.',
    budgetEstimated: '₹150,000 INR (~$1,800 USD equivalent)',
    paymentStructure: '50% Upfront / 50% Final Acceptance',
    expectedTimeline: '3 Weeks',
    status: 'SCOPING',
    requiredSkills: ['Figma', 'Design Systems', 'React', 'Tailwind CSS', 'Accessibility'],
    assignedSquad: ['JEMP#008', 'JEMP#019'],
    internalReviewPassed: true,
    scopeLocked: false,
    clientName: 'Fintech Client'
  },
  {
    id: 'HC#007',
    title: 'E-Commerce Logistics Event Pipeline & Monitoring',
    clientType: 'Regional Logistics Hub',
    description: 'Real-time order tracking event bridge connecting courier webhooks with merchant notification queues.',
    budgetEstimated: '₹220,000 INR (~$2,650 USD equivalent)',
    paymentStructure: '30% Advance / 40% Milestone / 30% Handoff',
    expectedTimeline: '6 Weeks',
    status: 'SCOPING',
    requiredSkills: ['Go', 'Kafka / Redis Streams', 'Node.js', 'AWS'],
    assignedSquad: [],
    internalReviewPassed: true,
    scopeLocked: false,
    clientName: 'Logistics Provider'
  }
];

export const DEMO_MEMBER_PROFILE: MemberProfile = {
  id: 'JEMP#027',
  name: 'Alex Vance',
  handle: '@alexvance',
  avatarUrl: 'https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150&auto=format&fit=crop&q=80',
  role: 'Systems Architect & Full-Stack Builder',
  joinedDate: 'September 2026',
  status: 'ACTIVE',
  reputationScore: 98,
  contributedProjectsCount: 3,
  totalClientEarnedUsd: 1140,
  timeline: [
    {
      id: 'tl-01',
      memberId: 'JEMP#027',
      timestamp: 'September 2, 2026',
      date: 'Sept 2, 2026',
      title: 'Created Brainstorm Proposal B#027',
      category: 'BRAINSTORM',
      projectRef: 'B#027',
      description: 'Drafted and published the problem specification for the AI Resume & Proof Analyzer.',
      verificationHash: '0x7e29b104ad5c91f0',
      verifiedBy: 'Community Peer Review'
    },
    {
      id: 'tl-02',
      memberId: 'JEMP#027',
      timestamp: 'September 5, 2026',
      date: 'Sept 5, 2026',
      title: 'Formed Squad & Promoted B#027 to Project P#014',
      category: 'PROJECT',
      projectRef: 'P#014',
      description: 'Assembled 3-person squad with JEMP#014 and JEMP#008; initialized core Git repository and architecture RFC.',
      verificationHash: '0x9a8f2c1d4e6b3301',
      verifiedBy: 'Squad Consensus'
    },
    {
      id: 'tl-03',
      memberId: 'JEMP#027',
      timestamp: 'September 12, 2026',
      date: 'Sept 12, 2026',
      title: 'Completed AST Parser & Extraction Milestone',
      category: 'MILESTONE',
      projectRef: 'P#014',
      description: 'Engineered TypeScript AST analyzer that scans codebases and structures milestone proof packages with unit test coverage.',
      verificationHash: '0x44b1c900e12f0091',
      verifiedBy: 'Squad Quorum'
    },
    {
      id: 'tl-04',
      memberId: 'JEMP#027',
      timestamp: 'September 20, 2026',
      date: 'Sept 20, 2026',
      title: 'Completed Stage 2 Milestone Delivery for HC#003',
      category: 'CLIENT_WORK',
      projectRef: 'HC#003 / P#011',
      description: 'Delivered customer support ticket routing engine; client accepted milestone with zero blockers.',
      verificationHash: '0x10b981f4a99077de',
      verifiedBy: 'Client Acceptance & Admin Review'
    }
  ]
};

export const DEMO_FINANCIAL_STAGES: FinancialStageDetail[] = [
  {
    stageId: 'PAY#001',
    clientProjectId: 'HC#003',
    projectTitle: 'AI Customer Support Automation Pipeline',
    clientName: 'SaaS Enterprise Client',
    totalBudgetUsd: 1200, // equivalent to ₹100,000 INR
    adminFeePercentage: 5,
    adminFeeUsd: 60, // 5% fee = ₹5,000 INR
    contributorPoolUsd: 1140, // 95% pool = ₹95,000 INR
    status: 'PAYOUT_EXECUTED',
    proofPackage: {
      invoiceHash: '0x8849b2f1c90011ae',
      utrTransactionReference: 'HDFC-UTR-9920194821',
      bankConfirmationReceipt: 'PROOF-REC-001.pdf',
      reviewerSignatures: ['JEMP#005 (Lead)', 'JEMP#012 (Contributor)', 'ADMIN#001 (Auditor)'],
      quorumReached: true
    },
    payoutDistribution: [
      {
        memberId: 'JEMP#005',
        memberName: 'Tariq Malik',
        role: 'Lead Architect & Pipeline Developer',
        percentage: 45,
        amountUsd: 513,
        status: 'DISBURSED',
        payoutTxHash: 'WIRE-CONF-44910'
      },
      {
        memberId: 'JEMP#012',
        memberName: 'Sarah Jenkins',
        role: 'FastAPI & Integration Engineer',
        percentage: 35,
        amountUsd: 399,
        status: 'DISBURSED',
        payoutTxHash: 'WIRE-CONF-44911'
      },
      {
        memberId: 'JEMP#022',
        memberName: 'Alex Vance',
        role: 'QA Automation & Documentation',
        percentage: 20,
        amountUsd: 228,
        status: 'DISBURSED',
        payoutTxHash: 'WIRE-CONF-44912'
      }
    ],
    lastUpdated: 'September 20, 2026'
  },
  {
    stageId: 'PAY#002',
    clientProjectId: 'HC#005',
    projectTitle: 'Fintech Dashboard Redesign & Tokens',
    clientName: 'Fintech Partner Client',
    totalBudgetUsd: 1800, // ₹150,000 INR
    adminFeePercentage: 5,
    adminFeeUsd: 90,
    contributorPoolUsd: 1710,
    status: 'ESCROW_RECEIVED',
    proofPackage: {
      invoiceHash: '0x12c4b901a8ef33d2',
      utrTransactionReference: 'ICICI-UTR-8830192831',
      bankConfirmationReceipt: 'PROOF-REC-002.pdf',
      reviewerSignatures: ['JEMP#008', 'ADMIN#001'],
      quorumReached: true
    },
    payoutDistribution: [
      {
        memberId: 'JEMP#008',
        memberName: 'Elena Rostova',
        role: 'UI/UX & Design Tokens Lead',
        percentage: 60,
        amountUsd: 1026,
        status: 'ESCROWED'
      },
      {
        memberId: 'JEMP#019',
        memberName: 'David Miller',
        role: 'React Component Engineer',
        percentage: 40,
        amountUsd: 684,
        status: 'ESCROWED'
      }
    ],
    lastUpdated: 'September 22, 2026'
  }
];

export const INITIAL_FINANCIAL_STAGES = DEMO_FINANCIAL_STAGES;
export const INITIAL_MEMBERS: Record<string, MemberProfile> = {
  'JEMP#027': DEMO_MEMBER_PROFILE,
  'JEMP#001': DEMO_MEMBER_PROFILE
};

export const FAQ_LIST: FaqItem[] = [
  {
    id: 'faq-1',
    question: 'Is Jobless Empire a job board?',
    answer: 'No. Jobless Empire is a private builder community for people between jobs or seeking opportunities while continuing to build, collaborate, learn, create, and produce tangible proof-of-work. It is not a recruitment agency, employment agency, or job listing board.',
    category: 'GENERAL'
  },
  {
    id: 'faq-2',
    question: 'Will I get a job if I join?',
    answer: 'No guarantee. Jobless Empire creates an environment where members can keep building, collaborating, and documenting real work. It does not guarantee employment, income, client placement, or career outcomes.',
    category: 'GENERAL'
  },
  {
    id: 'faq-3',
    question: 'Do I need to be unemployed to join?',
    answer: 'The community is designed around people who are jobless, between jobs, job seeking, or continuing to build despite not having a traditional role. Eligibility is determined during the manual application review process.',
    category: 'APPLICATION'
  },
  {
    id: 'faq-4',
    question: 'Do I need to be a developer?',
    answer: 'No. Useful contributors come from many disciplines including product thinking, UI/UX design, writing, research, marketing, AI/ML, operations, and strategy. We value tangible contributions over titles.',
    category: 'BUILDING'
  },
  {
    id: 'faq-5',
    question: 'Can I create my own idea?',
    answer: 'Yes. Any member can create a proposal in the Brainstorming Club (B#), specify the problem and required skills, and invite interested community members to collaborate.',
    category: 'BUILDING'
  },
  {
    id: 'faq-6',
    question: 'Can I join someone else\'s idea?',
    answer: 'Yes. Members can discover active brainstorms (B#) and ongoing projects (P#) and apply to contribute based on their skills and availability.',
    category: 'BUILDING'
  },
  {
    id: 'faq-7',
    question: 'Are external projects guaranteed?',
    answer: 'No. External client opportunities (HC#) depend on what opportunities become available and how teams are selected. Applying to an opportunity does not guarantee selection.',
    category: 'BUILDING'
  },
  {
    id: 'faq-8',
    question: 'How are project payments handled?',
    answer: 'When an external client project generates revenue, it follows a transparent workflow: client payment -> payment proof submission -> team verification -> distribution proposal -> team approval -> transfer to admin -> payroll disbursement -> contributor confirmation.',
    category: 'PAYMENTS'
  },
  {
    id: 'faq-9',
    question: 'Who decides how project money is divided?',
    answer: 'The project owner proposes a distribution breakdown based on actual contributions, but affected contributors review and approve it. The bot verifies the mathematics, while people evaluate the fairness. If even one required contributor objects with a reason, the proposal must be reviewed.',
    category: 'PAYMENTS'
  },
  {
    id: 'faq-10',
    question: 'What is the 5% admin fee?',
    answer: 'The 5% administrative fee supports running the community platform, infrastructure, and coordination. It is strictly separate from compensation for actual project work. 95% of applicable revenue goes directly to project contributors.',
    category: 'PAYMENTS'
  },
  {
    id: 'faq-11',
    question: 'Does the admin get another share if they work on the project?',
    answer: 'If an administrator performs actual project work as an active contributor, they may receive their legitimate project-work allocation in addition to the separate administrative fee. Work compensation depends strictly on actual contribution.',
    category: 'PAYMENTS'
  },
  {
    id: 'faq-12',
    question: 'What happens if someone disagrees with the payment proof?',
    answer: 'Any team reviewer can mark the proof as "Not Satisfied" and provide a reason. The project owner must then submit additional evidence. The financial workflow cannot proceed past required unresolved objections.',
    category: 'PAYMENTS'
  },
  {
    id: 'faq-13',
    question: 'What happens if someone thinks the distribution is unfair?',
    answer: 'They can mark the distribution proposal as "Not Satisfied" with a clear reason. The owner must re-evaluate the allocation with the squad and submit a revised proposal. Escalation to administrative arbitration is available if consensus cannot be reached.',
    category: 'PAYMENTS'
  },
  {
    id: 'faq-14',
    question: 'Are payment screenshots automatically verified?',
    answer: 'No. The bot manages the evidence submission and approval workflow, but human reviewers (squad members and administrators) carefully evaluate the actual documentation and reference numbers.',
    category: 'PAYMENTS'
  },
  {
    id: 'faq-15',
    question: 'What happens if a bank transfer takes time?',
    answer: 'Normal settlement delays are supported through explicit statuses (processing, pending, partially received, confirmed). Settlement delays are not treated as misconduct.',
    category: 'PAYMENTS'
  },
  {
    id: 'faq-16',
    question: 'What is a JEMP#?',
    answer: 'JEMP# is your permanent Jobless Empire member identifier (e.g., JEMP#027) assigned upon successful verification and onboarding. It serves as an organizational reference for projects, contributions, and your private Timeline.',
    category: 'GENERAL'
  },
  {
    id: 'faq-17',
    question: 'What is B#?',
    answer: 'B# denotes a Brainstorming Club proposal or experiment (e.g., B#027) where members explore concepts, recruit collaborators, and validate ideas.',
    category: 'BUILDING'
  },
  {
    id: 'faq-18',
    question: 'What is P#?',
    answer: 'P# denotes an active Ongoing Project (e.g., P#014) with defined contributors, milestones, responsibilities, and deliverables.',
    category: 'BUILDING'
  },
  {
    id: 'faq-19',
    question: 'What is HC#?',
    answer: 'HC# denotes an external client or hiring opportunity (e.g., HC#003) submitted by businesses or clients for internal review and team selection.',
    category: 'BUILDING'
  },
  {
    id: 'faq-20',
    question: 'Is my Timeline public?',
    answer: 'No. Your Proof-of-Work Timeline is private by default. You have full control over whether, when, and what portion of your eligible verified records you choose to export or share.',
    category: 'TIMELINE'
  },
  {
    id: 'faq-21',
    question: 'Can I use my Timeline for my portfolio?',
    answer: 'Yes. Members can export verified milestones and contribution summaries in Markdown, JSON, DOCX, or PDF formats to include in resumes, portfolio sites, or interview discussions.',
    category: 'TIMELINE'
  },
  {
    id: 'faq-22',
    question: 'Can I leave the community?',
    answer: 'Yes, members can leave at any time subject to standard community policies. Historical records of completed milestones remain preserved in the audit log.',
    category: 'GENERAL'
  },
  {
    id: 'faq-23',
    question: 'Can I join if I don\'t have much experience?',
    answer: 'Potentially. Applications are evaluated individually on intent, honesty, and willingness to contribute meaningfully. We value curiosity and genuine desire to build over senior corporate titles.',
    category: 'APPLICATION'
  }
];

export const FAQ_ITEMS = FAQ_LIST;
