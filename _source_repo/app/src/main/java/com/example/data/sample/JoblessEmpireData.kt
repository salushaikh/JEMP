package com.example.data.sample

import com.example.data.model.*

object JoblessEmpireData {

    val builderRoles = listOf(
        BuilderRoleInfo("Developers", "Frontend, backend, mobile, AI engineering, smart contracts, systems architecture.", "code", listOf("Kotlin", "React", "Python", "Rust", "TypeScript")),
        BuilderRoleInfo("Designers", "Product UI/UX, brand systems, motion design, design systems, visual mockups.", "palette", listOf("Figma", "Design Systems", "Prototyping", "3D")),
        BuilderRoleInfo("Writers", "Technical documentation, copy, content strategy, newsletters, narrative design.", "edit_note", listOf("Tech Writing", "Copywriting", "Storytelling")),
        BuilderRoleInfo("Marketers", "Growth hacking, distribution, SEO, community building, launch campaigns.", "campaign", listOf("Growth", "SEO", "Analytics", "Launches")),
        BuilderRoleInfo("Analysts", "Data pipelines, metrics evaluation, market research, business intelligence.", "analytics", listOf("SQL", "Data Models", "BI Dashboards")),
        BuilderRoleInfo("Product People", "Roadmapping, feature prioritization, user stories, cross-team sprint sync.", "view_kanban", listOf("PRDs", "User Research", "Agile")),
        BuilderRoleInfo("Creators", "Video production, social assets, visual storytelling, educational guides.", "movie", listOf("Video", "Podcasting", "Short-form")),
        BuilderRoleInfo("Founders", "Zero-to-one problem solving, unit economics, lean validation, pitching.", "rocket_launch", listOf("0 to 1", "Strategy", "Execution")),
        BuilderRoleInfo("Students & Grads", "Eager builders bridging academic foundations with production proof-of-work.", "school", listOf("Junior Dev", "Design Intern", "Fast Learner")),
        BuilderRoleInfo("Career Switchers", "Translating domain expertise into fresh technical and creative disciplines.", "transform", listOf("Domain Mastery", "Tech Transition")),
        BuilderRoleInfo("Independent Pros", "Contractors and consultants between gigs keeping toolchains razor-sharp.", "badge", listOf("Consulting", "Architecture", "Auditing")),
        BuilderRoleInfo("Anyone Between Jobs", "Driven humans who refuse to let idle time erode their technical edge.", "bolt", listOf("Ambition", "Consistency", "Collaboration"))
    )

    val sampleBrainstorms = listOf(
        BrainstormSession(
            id = "B#007",
            title = "AI-Powered Resume Intelligence",
            description = "A client-side neural parser that extracts real proof-of-work tokens and portfolio commits rather than bullet buzzwords.",
            category = "AI / Tooling",
            ownerId = "JEMP#014",
            status = BrainstormStatus.COOKING,
            contributorsCount = 4,
            applicationsCount = 9,
            linkedProjectId = "P#003",
            recentActivity = "Spec finalized with 4 accepted contributors; promoted to Active Project P#003.",
            tags = listOf("AI", "LLM Parser", "Proof of Work")
        ),
        BrainstormSession(
            id = "B#014",
            title = "Decentralized Proof-of-Work Verifier",
            description = "Permanent cryptographic attestation for open-source contributions across GitHub, Figma, and Discord meetings.",
            category = "Identity / Crypto",
            ownerId = "JEMP#001",
            status = BrainstormStatus.COOKING,
            contributorsCount = 3,
            applicationsCount = 6,
            linkedProjectId = "P#008",
            recentActivity = "Architecture blueprint presented in voice room sync.",
            tags = listOf("Verification", "Zero-Knowledge", "Portfolio")
        ),
        BrainstormSession(
            id = "B#022",
            title = "Micro-SaaS Invoicing & Split-Payout Engine",
            description = "Automated escrow and multi-contributor split disbursement for collaborative freelance squads.",
            category = "Fintech / SaaS",
            ownerId = "JEMP#038",
            status = BrainstormStatus.BRAINSTORMING,
            contributorsCount = 2,
            applicationsCount = 4,
            recentActivity = "Wireframes posted for community feedback.",
            tags = listOf("Fintech", "Escrow", "Revenue Split")
        ),
        BrainstormSession(
            id = "B#031",
            title = "Local Community Hardware & Device Swapper",
            description = "P2P local exchange network for testing devices, spare testphones, and micro-controllers.",
            category = "Community / Hardware",
            ownerId = "JEMP#052",
            status = BrainstormStatus.IT_S_ALIVE,
            contributorsCount = 5,
            applicationsCount = 11,
            recentActivity = "Beta testing successfully concluded across 3 regional hubs.",
            tags = listOf("P2P", "Hardware", "Local")
        )
    )

    val sampleProjects = listOf(
        OngoingProject(
            id = "P#003",
            title = "Resume Intelligence Platform",
            description = "High-speed document parsing engine that turns resumes and portfolios into structured skill verification graphs.",
            ownerId = "JEMP#014",
            contributors = listOf("JEMP#014 (Lead)", "JEMP#001 (Frontend)", "JEMP#027 (ML)", "JEMP#045 (Design)"),
            linkedBrainstormId = "B#007",
            status = ProjectStatus.IN_PROGRESS,
            statusReason = "Sprint 3: Implementing local OCR parser & vector embeddings.",
            milestones = listOf(
                ProjectMilestone("PRD & API Specification", true, "12 AUG 2026", "JEMP#014"),
                ProjectMilestone("Compose UI & Design Tokens", true, "20 AUG 2026", "JEMP#045"),
                ProjectMilestone("Core Inference Engine Integration", false, "05 SEP 2026", "JEMP#027"),
                ProjectMilestone("Beta Release & Benchmarks", false, "15 SEP 2026", "JEMP#001")
            ),
            latestMeeting = "Sprint 3 Sync — 45 mins voice channel participation recorded."
        ),
        OngoingProject(
            id = "P#008",
            title = "Decentralized Proof-of-Work Verifier",
            description = "Immutable timeline event logging engine linking Discord sprint milestones to verified GitHub pull requests.",
            ownerId = "JEMP#001",
            contributors = listOf("JEMP#001 (Lead)", "JEMP#018 (Backend)", "JEMP#032 (QA)"),
            linkedBrainstormId = "B#014",
            status = ProjectStatus.PAUSED,
            statusReason = "Waiting for client API access & third-party auth key rotation.",
            milestones = listOf(
                ProjectMilestone("Smart Contract Architecture", true, "15 AUG 2026", "JEMP#001"),
                ProjectMilestone("Backend Relay Service", true, "25 AUG 2026", "JEMP#018"),
                ProjectMilestone("Discord Bot Webhook Integration", false, "TBD", "JEMP#032")
            ),
            latestMeeting = "Paused blocker review on 28 AUG 2026."
        ),
        OngoingProject(
            id = "P#002",
            title = "E-commerce Growth Analytics Suite",
            description = "Production dashboard analyzing multi-store conversion funnels, churn velocity, and automated re-engagement triggers.",
            ownerId = "JEMP#009",
            contributors = listOf("JEMP#009 (Lead)", "JEMP#001 (UI)", "JEMP#061 (Data Eng)"),
            linkedClientId = "HC#004",
            status = ProjectStatus.IN_PROGRESS,
            statusReason = "Milestone 2 delivering on schedule.",
            milestones = listOf(
                ProjectMilestone("Data Pipeline Ingestion Setup", true, "18 AUG 2026", "JEMP#061"),
                ProjectMilestone("Real-time Charts & Funnel UI", true, "29 AUG 2026", "JEMP#001"),
                ProjectMilestone("Client UAT Acceptance & Final Payout", false, "10 SEP 2026", "JEMP#009")
            ),
            latestMeeting = "Client check-in recorded on 30 AUG 2026."
        )
    )

    val sampleClientProjects = listOf(
        ClientProject(
            id = "HC#004",
            title = "E-commerce Growth Dashboard",
            description = "High-growth direct-to-consumer apparel brand requires custom analytics portal for multi-store attribution and cohort retention.",
            clientRequirements = "Interactive charts, sub-second query response, exportable PDF reports, OAuth2 team login.",
            budgetUsd = 4500,
            expectedTimeline = "3 Weeks",
            requiredSkills = listOf("Kotlin/Compose", "Vico Charts", "REST API", "State Management"),
            revShareSplit = "5% Admin ($225) • 95% Contributor Distribution ($4,275)",
            ownerId = "JEMP#009",
            status = ClientProjectStatus.CONVERTED_TO_PROJECT,
            linkedProjectId = "P#002"
        ),
        ClientProject(
            id = "HC#009",
            title = "Fintech Sandbox & API Documentation Portal",
            description = "Series-A payment infrastructure startup needs modern interactive developer documentation with copyable curl examples and response explorer.",
            clientRequirements = "Next-gen design, dark mode, interactive sandbox emulator, search indexation.",
            budgetUsd = 6000,
            expectedTimeline = "4 Weeks",
            requiredSkills = listOf("Technical Writing", "Frontend UI", "API Design", "Security"),
            revShareSplit = "5% Admin ($300) • 95% Contributor Distribution ($5,700)",
            ownerId = "JEMP#014",
            status = ClientProjectStatus.OPEN_FOR_APPLICATIONS
        ),
        ClientProject(
            id = "HC#012",
            title = "Multi-tenant Brand Asset Management UI",
            description = "B2B creative agency seeks a streamlined asset reviewer where internal clients can markup video frames and brand guides.",
            clientRequirements = "Canvas annotation, real-time collaboration indicators, video playback scrubber.",
            budgetUsd = 3200,
            expectedTimeline = "2 Weeks",
            requiredSkills = listOf("UI/UX Design", "Compose Canvas", "Media Player"),
            revShareSplit = "5% Admin ($160) • 95% Contributor Distribution ($3,040)",
            ownerId = "JEMP#022",
            status = ClientProjectStatus.REVIEWING_APPLICANTS
        )
    )

    val jemp001Timeline = listOf(
        TimelineEntry(
            id = "TL-001",
            memberId = "JEMP#001",
            date = "02 SEP 2026",
            title = "Joined Jobless Empire",
            details = listOf(
                "Completed onboarding",
                "Accepted community rules",
                "Verified communication email",
                "Selected timezone (UTC-7)"
            ),
            isSystemGenerated = true,
            category = TimelineCategory.ONBOARDING
        ),
        TimelineEntry(
            id = "TL-002",
            memberId = "JEMP#001",
            date = "04 SEP 2026",
            title = "Joined Brainstorming Session B#014",
            linkedReferenceId = "B#014",
            details = listOf(
                "Contributed product research on verifiable credentials",
                "Proposed streamlined member onboarding flow"
            ),
            isSystemGenerated = true,
            category = TimelineCategory.BRAINSTORM
        ),
        TimelineEntry(
            id = "TL-003",
            memberId = "JEMP#001",
            date = "09 SEP 2026",
            title = "Project P#008 — Contributor",
            linkedReferenceId = "P#008",
            details = listOf(
                "Designed landing page architecture",
                "Participated in team planning meeting"
            ),
            isSystemGenerated = true,
            category = TimelineCategory.PROJECT
        ),
        TimelineEntry(
            id = "TL-004",
            memberId = "JEMP#001",
            date = "15 SEP 2026",
            title = "HC#004 — Client Project",
            linkedReferenceId = "HC#004",
            details = listOf(
                "Accepted as frontend developer",
                "Completed assigned milestone on time",
                "Received transparent payout via administrator"
            ),
            isSystemGenerated = true,
            category = TimelineCategory.CLIENT_WORK
        )
    )

    val jemp014Timeline = listOf(
        TimelineEntry(
            id = "TL-101",
            memberId = "JEMP#014",
            date = "10 AUG 2026",
            title = "Onboarding & Empire Verification",
            details = listOf(
                "Completed manual application review",
                "Assigned Member Identifier JEMP#014",
                "Passed security and anti-scam orientation"
            ),
            isSystemGenerated = true,
            category = TimelineCategory.ONBOARDING
        ),
        TimelineEntry(
            id = "TL-102",
            memberId = "JEMP#014",
            date = "14 AUG 2026",
            title = "Created Brainstorm Session B#007",
            linkedReferenceId = "B#007",
            details = listOf(
                "Authored proposal for AI Resume Intelligence",
                "Evaluated 9 contributor applications",
                "Selected 4 core squad members"
            ),
            isSystemGenerated = true,
            category = TimelineCategory.BRAINSTORM
        ),
        TimelineEntry(
            id = "TL-103",
            memberId = "JEMP#014",
            date = "22 AUG 2026",
            title = "Promoted B#007 → Project P#003",
            linkedReferenceId = "P#003",
            details = listOf(
                "Lead architect on parser pipeline",
                "Completed 2 sprint milestones",
                "Conducted 3 Discord voice standups"
            ),
            isSystemGenerated = true,
            category = TimelineCategory.PROJECT
        ),
        TimelineEntry(
            id = "TL-104",
            memberId = "JEMP#014",
            date = "29 AUG 2026",
            title = "Initiated Client Opportunity HC#009",
            linkedReferenceId = "HC#009",
            details = listOf(
                "Posted $6,000 Fintech API documentation scope",
                "Defined transparent 95% contributor pool structure",
                "Documented payment escrow terms in Discord thread"
            ),
            isSystemGenerated = true,
            category = TimelineCategory.CLIENT_WORK
        )
    )

    val memberProfiles = mapOf(
        "JEMP#001" to MemberProfile(
            id = "JEMP#001",
            name = "Alex Vance",
            specialty = "Full-Stack & Systems Developer",
            joinedDate = "02 SEP 2026",
            timezone = "UTC-7 (Pacific)",
            timeline = jemp001Timeline,
            isPublic24hShared = true,
            shareExpiresInSeconds = 64200L
        ),
        "JEMP#014" to MemberProfile(
            id = "JEMP#014",
            name = "Marcus Chen",
            specialty = "AI Architect & Product Lead",
            joinedDate = "10 AUG 2026",
            timezone = "UTC-5 (Eastern)",
            timeline = jemp014Timeline,
            isPublic24hShared = false,
            shareExpiresInSeconds = 86400L
        )
    )

    val faqList = listOf(
        FaqItem(
            question = "Is Jobless Empire a recruitment agency?",
            answer = "No. It is a private community for people currently without traditional employment who want to build, collaborate and create proof of work."
        ),
        FaqItem(
            question = "Do I get guaranteed work?",
            answer = "No. There are no guarantees that members will receive work, project acceptance, or specific assignments."
        ),
        FaqItem(
            question = "Do I get guaranteed income?",
            answer = "No. Jobless Empire is not a wage-paying company or income entitlement program. Earning is only possible when you are selected for and deliver on paid client projects."
        ),
        FaqItem(
            question = "Is every project paid?",
            answer = "No. Some projects may be brainstorming, experimental, portfolio-focused or otherwise unpaid."
        ),
        FaqItem(
            question = "How do paid projects work?",
            answer = "Project owners define the proposed revenue distribution and accepted contributors participate according to the agreed structure."
        ),
        FaqItem(
            question = "What is the admin fee?",
            answer = "Currently 5% of project revenue under the community's proposed fee structure (95% remains for contributor payouts)."
        ),
        FaqItem(
            question = "Can I apply to multiple projects?",
            answer = "Yes, subject to each project owner's requirements and your available capacity."
        ),
        FaqItem(
            question = "Can I create my own project?",
            answer = "Yes. Members can initiate brainstorming sessions or post legitimate client/hiring projects directly into the appropriate channels."
        ),
        FaqItem(
            question = "Can I start a project from a brainstorming session?",
            answer = "Yes. A brainstorming session (B#) can be converted into an ongoing project (P#) while maintaining the permanent connection between the two."
        ),
        FaqItem(
            question = "Can I show my timeline to employers?",
            answer = "Yes. Members can choose to temporarily make their timeline publicly accessible (with a 24-hour expiration link) and can export their timeline as a professional proof-of-work document."
        ),
        FaqItem(
            question = "Is my timeline public?",
            answer = "No. It is private by default. Only you control when and how it is shared."
        ),
        FaqItem(
            question = "What happens if someone scams another member?",
            answer = "Report it to the administrator with evidence. Fraudulent, manipulative, or malicious behavior results in immediate suspension, investigation, and permanent ban under our zero-tolerance policy."
        ),
        FaqItem(
            question = "Why can't I just DM people?",
            answer = "Important project work, milestones, agreements, and payout commitments must remain in the appropriate Discord project thread. If it isn't in the project thread, it didn't happen — this creates transparent accountability and protect all contributors."
        )
    )
}
