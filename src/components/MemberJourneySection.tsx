import React from 'react';
import { ShieldCheck, Mail, Key, UserCheck, Terminal, Rocket, FileCheck, Award, Layers } from 'lucide-react';

export const MemberJourneySection: React.FC = () => {
  const steps = [
    { num: '01', title: 'APPLY', desc: 'Submit name, email, resume, and unemployment status.' },
    { num: '02', title: 'MANUAL REVIEW', desc: 'Human administrator audits experience and alignment.' },
    { num: '03', title: 'PRIVATE INVITE', desc: 'Personalized, single-use, expiring invitation sent by email.' },
    { num: '04', title: 'VERIFY & ONBOARD', desc: 'Enter Discord, verify email, and accept standards.' },
    { num: '05', title: 'JEMP# ASSIGNED', desc: 'Receive your permanent builder identifier.' },
    { num: '06', title: 'ENTER THE EMPIRE', desc: 'Access active channels, workspaces, and squads.' },
    { num: '07', title: 'BRAINSTORM / JOIN', desc: 'Propose a B# idea or apply to an ongoing P# project.' },
    { num: '08', title: 'BUILD & DELIVER', desc: 'Collaborate with squad members on verified milestones.' },
    { num: '09', title: 'DOCUMENT', desc: 'Attest deliverables, PRs, and decisions to the log.' },
    { num: '10', title: 'GROW PROOF-OF-WORK', desc: 'Export verifiable evidence to share with recruiters.' }
  ];

  const pillars = [
    {
      title: 'Structured',
      desc: 'Ideas, projects, opportunities, finances, and membership follow defined workflows rather than chaotic chat channels.'
    },
    {
      title: 'Collaborative',
      desc: 'People with complementary skills (design, full-stack, AI, product) form agile squads to build real software.'
    },
    {
      title: 'Documented',
      desc: 'Every meaningful contribution leaves a verified cryptographic proof trail on your private timeline.'
    },
    {
      title: 'Transparent',
      desc: 'External project revenue is verified with receipts and 95% distributed through an auditable payroll ledger.'
    }
  ];

  return (
    <section id="member-journey-section" className="py-16 sm:py-24 border-b border-[#1A1E29] bg-[#0E1017]">
      <div className="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8 space-y-20">
        
        {/* 1. MEMBER JOURNEY */}
        <div className="space-y-12">
          <div className="text-center max-w-3xl mx-auto space-y-4">
            <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-[#181B24] border border-[#232736] text-xs font-mono text-[#10B981]">
              <span>09 • THE MEMBER JOURNEY</span>
            </div>
            <h2 className="text-3xl sm:text-5xl font-black text-white font-mono tracking-tight uppercase">
              HOW YOU ENTER AND BUILD.
            </h2>
            <p className="text-base sm:text-lg text-[#DBDEE1]">
              From application review to permanent JEMP# identity and verifiable milestone delivery.
            </p>
          </div>

          {/* 10 Step Grid */}
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-5 gap-3">
            {steps.map((step) => (
              <div
                key={step.num}
                className="bg-[#11131A] border border-[#232736] rounded-xl p-4 space-y-2 hover:border-[#10B981]/50 transition-all flex flex-col justify-between"
              >
                <div className="space-y-1.5">
                  <div className="text-xs font-mono font-black text-[#10B981]">{step.num}</div>
                  <h3 className="text-sm font-bold text-white font-mono">{step.title}</h3>
                  <p className="text-xs text-[#949BA4] leading-relaxed">{step.desc}</p>
                </div>
              </div>
            ))}
          </div>

          <div className="p-4 rounded-xl bg-[#181B24] border border-[#232736] text-xs text-[#949BA4] text-center max-w-2xl mx-auto">
            <strong className="text-white font-mono">Personalized Invitations Only:</strong> We do not publish open Discord links. Approved applicants receive a private, single-use invite to protect community quality.
          </div>
        </div>

        {/* 2. OFFICE, NOT CHAT ROOM */}
        <div className="pt-8 border-t border-[#1A1E29] space-y-12">
          <div className="text-center max-w-3xl mx-auto space-y-4">
            <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-[#181B24] border border-[#232736] text-xs font-mono text-[#FFB020]">
              <span>10 • NOT ANOTHER DISCORD SERVER</span>
            </div>
            <h2 className="text-3xl sm:text-4xl font-black text-white font-mono tracking-tight uppercase">
              THINK LESS “DISCORD SERVER.” MORE “PRIVATE DIGITAL OFFICE.”
            </h2>
            <p className="text-sm sm:text-base text-[#949BA4]">
              Discord is merely our communication medium. The actual system includes membership verification, brainstorm management, agile squads, escrow auditing, and proof-of-work timelines.
            </p>
          </div>

          {/* The 4 Pillars */}
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
            {pillars.map((p, idx) => (
              <div
                key={idx}
                className="bg-[#11131A] border border-[#232736] rounded-xl p-5 space-y-2 hover:border-[#FFB020]/40 transition-all"
              >
                <div className="text-xs font-mono font-bold text-[#FFB020] uppercase">
                  0{idx + 1} • {p.title}
                </div>
                <h3 className="text-base font-bold text-white">{p.title} Workflows</h3>
                <p className="text-xs text-[#949BA4] leading-relaxed">{p.desc}</p>
              </div>
            ))}
          </div>

          {/* System Identifiers Guide */}
          <div className="bg-[#11131A] border border-[#232736] rounded-2xl p-6 sm:p-8 space-y-4">
            <h3 className="text-base font-bold text-white font-mono uppercase">
              JOBLESS EMPIRE SYSTEM IDENTIFIERS
            </h3>
            <div className="grid grid-cols-2 sm:grid-cols-4 gap-3 text-xs font-mono">
              <div className="bg-[#181B24] p-3 rounded-lg border border-[#232736]">
                <div className="text-[#FFB020] font-black text-sm">JEMP#027</div>
                <div className="text-[#949BA4] mt-1">Permanent Member Identity</div>
              </div>
              <div className="bg-[#181B24] p-3 rounded-lg border border-[#232736]">
                <div className="text-[#FFB020] font-black text-sm">B#027</div>
                <div className="text-[#949BA4] mt-1">Brainstorm Idea Proposal</div>
              </div>
              <div className="bg-[#181B24] p-3 rounded-lg border border-[#232736]">
                <div className="text-[#10B981] font-black text-sm">P#014</div>
                <div className="text-[#949BA4] mt-1">Active Ongoing Project</div>
              </div>
              <div className="bg-[#181B24] p-3 rounded-lg border border-[#232736]">
                <div className="text-[#38BDF8] font-black text-sm">HC#003</div>
                <div className="text-[#949BA4] mt-1">External Client Opportunity</div>
              </div>
            </div>
          </div>
        </div>

      </div>
    </section>
  );
};
