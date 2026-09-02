import React from 'react';
import { ShieldCheck, Lock, FileSpreadsheet, Users, ArrowDown, AlertTriangle, CheckCircle } from 'lucide-react';
import { DEMO_CLIENT_OPPORTUNITIES } from '../data/joblessData';

export const ExternalWorkFlowSection: React.FC = () => {
  const externalSteps = [
    'CLIENT / EMPLOYER',
    'HC# OPPORTUNITY',
    'INTERNAL REVIEW',
    'MEMBER APPLICATIONS',
    'SHORTLIST',
    'TEAM FORMATION',
    'QUOTE / SCOPE',
    'CLIENT AGREEMENT',
    'P# PROJECT'
  ];

  return (
    <section id="external-work-section" className="py-16 sm:py-24 border-b border-[#1A1E29] bg-[#0E1017]">
      <div className="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8 space-y-16">
        
        {/* Section Header */}
        <div className="text-center max-w-3xl mx-auto space-y-4">
          <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-[#181B24] border border-[#232736] text-xs font-mono text-[#38BDF8]">
            <span>05 • EXTERNAL CLIENT WORK</span>
          </div>
          <h2 className="text-3xl sm:text-5xl font-black text-white font-mono tracking-tight uppercase">
            SOMETIMES THE WORK COMES FROM OUTSIDE.
          </h2>
          <p className="text-base sm:text-lg text-[#DBDEE1]">
            Legitimate external opportunities (HC#) from businesses, startups, and clients seeking verified builder squads for software, AI automation, design, and research.
          </p>
        </div>

        {/* 1. External Flow Stepper */}
        <div className="bg-[#11131A] border border-[#232736] rounded-2xl p-6 sm:p-8 space-y-6">
          <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-2 pb-4 border-b border-[#232736]">
            <h3 className="text-base sm:text-lg font-bold text-white font-mono uppercase">
              THE EXTERNAL OPPORTUNITY LIFECYCLE
            </h3>
            <span className="text-xs font-mono text-[#38BDF8]">From Discovery to Verified Delivery</span>
          </div>

          <div className="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-9 gap-2">
            {externalSteps.map((step, idx) => (
              <div
                key={step}
                className="bg-[#181B24] border border-[#232736] rounded-lg p-2.5 text-center flex flex-col justify-center items-center"
              >
                <div className="text-[10px] font-mono text-[#38BDF8] font-bold">0{idx + 1}</div>
                <div className="text-[11px] font-mono font-bold text-white leading-tight mt-1">
                  {step}
                </div>
              </div>
            ))}
          </div>

          <div className="p-4 rounded-xl bg-[#181B24]/70 border border-[#232736] flex flex-col sm:flex-row items-start sm:items-center justify-between gap-3 text-xs text-[#949BA4]">
            <div className="flex items-center gap-2">
              <ShieldCheck className="w-4 h-4 text-[#10B981] shrink-0" />
              <span>Internal review filters spam and non-viable requirements before member listing.</span>
            </div>
            <span className="text-[#FFB020] font-mono font-bold">Applying does not guarantee selection.</span>
          </div>
        </div>

        {/* 2. Client Safety & Human Selection Cards */}
        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
          {/* Client Safety */}
          <div className="bg-[#11131A] border border-[#232736] rounded-2xl p-6 space-y-4">
            <div className="flex items-center gap-2 text-white font-bold text-base font-mono">
              <ShieldCheck className="w-5 h-5 text-[#10B981]" />
              <span>CLIENT SAFETY & REVIEW PROTOCOL</span>
            </div>
            <p className="text-xs sm:text-sm text-[#949BA4] leading-relaxed">
              We perform reasonable internal checks and maintain structured records of client identity, scope clarity, budget sanity, milestone schedules, and communication history before opening opportunities to members.
            </p>
            <div className="p-3.5 rounded-xl bg-[#181B24] border border-[#232736] flex items-start gap-2.5 text-xs text-[#DBDEE1]">
              <AlertTriangle className="w-4 h-4 text-[#FFB020] shrink-0 mt-0.5" />
              <span>
                <strong>Honest Real-World Disclaimer:</strong> We perform structured internal checks, but no screening process can eliminate all real-world risk.
              </span>
            </div>
          </div>

          {/* Human Team Selection */}
          <div className="bg-[#11131A] border border-[#232736] rounded-2xl p-6 space-y-4">
            <div className="flex items-center gap-2 text-white font-bold text-base font-mono">
              <Users className="w-5 h-5 text-[#38BDF8]" />
              <span>HUMAN SQUAD SELECTION</span>
            </div>
            <p className="text-xs sm:text-sm text-[#949BA4] leading-relaxed">
              Selection is a thoughtful human decision made by opportunity owners and administrators based on relevant skills, past work, availability, and verified proof-of-work.
            </p>
            <div className="p-3.5 rounded-xl bg-[#181B24] border border-[#232736] text-xs text-[#DBDEE1] space-y-1">
              <div className="font-bold text-white">No AI Black-Box Selection:</div>
              <p className="text-[#949BA4]">
                We do not use automated algorithms to assign spots. Humans review portfolios and communication to form balanced, capable squads.
              </p>
            </div>
          </div>
        </div>

        {/* 3. Scope Protection & Change Request Flow */}
        <div className="bg-[#11131A] border border-[#232736] rounded-2xl p-6 sm:p-8 space-y-6">
          <div className="flex items-center gap-2 text-white font-bold text-lg font-mono">
            <Lock className="w-5 h-5 text-[#FFB020]" />
            <span>VERSIONED QUOTATIONS & SCOPE PROTECTION</span>
          </div>
          <p className="text-xs sm:text-sm text-[#949BA4] max-w-3xl">
            Once an external project is agreed, the commercial terms and functional scope are formally locked to protect both contributors and clients.
          </p>

          {/* Change Request Flow Box */}
          <div className="bg-[#181B24] p-5 rounded-xl border border-[#232736] space-y-3">
            <span className="text-xs font-mono font-bold text-[#FFB020] uppercase">
              STRUCTURED CHANGE REQUEST PROTOCOL:
            </span>
            <div className="grid grid-cols-1 sm:grid-cols-4 gap-3 text-xs">
              <div className="bg-[#11131A] p-3 rounded-lg border border-[#232736]">
                <div className="text-[10px] font-mono text-[#949BA4]">01. REQUEST</div>
                <div className="font-bold text-white mt-1">Client Requests Extra Feature</div>
              </div>
              <div className="bg-[#11131A] p-3 rounded-lg border border-[#232736]">
                <div className="text-[10px] font-mono text-[#949BA4]">02. IMPACT</div>
                <div className="font-bold text-white mt-1">Squad Evaluates Timeline & Cost</div>
              </div>
              <div className="bg-[#11131A] p-3 rounded-lg border border-[#232736]">
                <div className="text-[10px] font-mono text-[#949BA4]">03. APPROVAL</div>
                <div className="font-bold text-white mt-1">Client Formally Approves Delta</div>
              </div>
              <div className="bg-[#11131A] p-3 rounded-lg border border-[#10B981]/30">
                <div className="text-[10px] font-mono text-[#10B981]">04. LOCKED</div>
                <div className="font-bold text-[#10B981] mt-1">Scope v2 Added to Milestones</div>
              </div>
            </div>
          </div>

          {/* Payment Schedules Examples */}
          <div className="space-y-2">
            <span className="text-xs font-mono font-bold text-[#949BA4] uppercase">
              FLEXIBLE PAYMENT SCHEDULE MODELS:
            </span>
            <div className="grid grid-cols-1 sm:grid-cols-3 gap-3 text-xs">
              <div className="bg-[#181B24] p-3.5 rounded-lg border border-[#232736]">
                <div className="font-bold text-white">40% Advance / 30% / 30%</div>
                <div className="text-[#949BA4] text-[11px] mt-1">Standard milestone delivery for web/mobile apps.</div>
              </div>
              <div className="bg-[#181B24] p-3.5 rounded-lg border border-[#232736]">
                <div className="font-bold text-white">50% Advance / 50% Final</div>
                <div className="text-[#949BA4] text-[11px] mt-1">Short sprints, branding & design systems.</div>
              </div>
              <div className="bg-[#181B24] p-3.5 rounded-lg border border-[#232736]">
                <div className="font-bold text-white">Monthly Retainers</div>
                <div className="text-[#949BA4] text-[11px] mt-1">Ongoing maintenance, DevOps & AI tuning.</div>
              </div>
            </div>
          </div>
        </div>

      </div>
    </section>
  );
};
