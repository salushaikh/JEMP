import React from 'react';
import { ShieldCheck, AlertOctagon, MessageSquare, CheckCircle, XCircle, Users } from 'lucide-react';

export const CommunityStandardsSection: React.FC = () => {
  const whoFor = [
    'Software developers & systems engineers',
    'UI/UX designers & design system leads',
    'Product thinkers, researchers & writers',
    'AI/ML builders & automation specialists',
    'Independent creators between roles',
    'People actively job searching who refuse to sit idle'
  ];

  const whoNotFor = [
    'Anyone seeking guaranteed employment or salary',
    'Passive income seekers or get-rich-quick mindsets',
    'Spammers promoting unrelated commercial services',
    'People looking to browse without contributing',
    'Anyone unwilling to document project decisions'
  ];

  return (
    <section id="community-standards-section" className="py-16 sm:py-24 border-b border-[#1A1E29] bg-[#0B0D13]">
      <div className="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8 space-y-20">
        
        {/* 1. COMMUNITY STANDARDS */}
        <div className="space-y-8">
          <div className="text-center max-w-3xl mx-auto space-y-4">
            <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-[#181B24] border border-[#232736] text-xs font-mono text-[#FFB020]">
              <span>11 • COMMUNITY STANDARDS</span>
            </div>
            <h2 className="text-3xl sm:text-5xl font-black text-white font-mono tracking-tight uppercase">
              PROFESSIONAL WORK REQUIRES PROFESSIONAL BEHAVIOR.
            </h2>
            <p className="text-base sm:text-lg text-[#DBDEE1]">
              A collaborative builder network relies on mutual respect, honest attribution, and clear accountability.
            </p>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            {/* Anti-Scam Zero Tolerance */}
            <div className="bg-[#11131A] border border-[#F43F5E]/30 rounded-2xl p-6 space-y-4">
              <div className="flex items-center gap-2 text-[#F43F5E] font-bold font-mono text-base">
                <AlertOctagon className="w-5 h-5" />
                <span>ZERO TOLERANCE FOR SCAMS & FRAUD</span>
              </div>
              <p className="text-xs sm:text-sm text-[#DBDEE1] leading-relaxed">
                We enforce strict penalties for fraudulent financial evidence, fake client claims, stolen code, impersonation, or payment manipulation. Disciplinary actions include project removal, suspension, and permanent ban.
              </p>
              <div className="text-xs font-mono text-[#949BA4] bg-[#181B24] p-3 rounded-lg border border-[#232736]">
                All disputes undergo human investigation and preserved audit logs.
              </div>
            </div>

            {/* Public Channels / No Undocumented DMs */}
            <div className="bg-[#11131A] border border-[#FFB020]/30 rounded-2xl p-6 space-y-4">
              <div className="flex items-center gap-2 text-[#FFB020] font-bold font-mono text-base">
                <MessageSquare className="w-5 h-5" />
                <span>COMMUNICATION EXPECTATIONS</span>
              </div>
              <p className="text-xs sm:text-sm text-[#DBDEE1] leading-relaxed">
                Important project agreements, scope definitions, and milestone acceptances must stay in dedicated project threads.
              </p>
              <div className="text-xs font-mono text-[#FFB020] bg-[#181B24] p-3 rounded-lg border border-[#FFB020]/20 font-bold">
                “Don’t let important work disappear into undocumented DMs.”
              </div>
            </div>
          </div>
        </div>

        {/* 2. NO GUARANTEE DISCLAIMER */}
        <div className="bg-[#11131A] border border-[#232736] rounded-2xl p-6 sm:p-8 space-y-6">
          <div className="text-center max-w-2xl mx-auto space-y-2">
            <span className="text-xs font-mono font-bold text-[#F43F5E] uppercase tracking-wider">
              EXPLICIT CLARIFICATION
            </span>
            <h3 className="text-2xl font-black text-white font-mono uppercase">
              WHAT JOBLESS EMPIRE DOES NOT PROMISE
            </h3>
          </div>

          <div className="grid grid-cols-2 sm:grid-cols-4 gap-3 text-center text-xs font-mono">
            <div className="p-3 rounded-lg bg-[#181B24] border border-[#F43F5E]/20 text-[#DBDEE1]">
              ✕ No Guaranteed Job
            </div>
            <div className="p-3 rounded-lg bg-[#181B24] border border-[#F43F5E]/20 text-[#DBDEE1]">
              ✕ No Guaranteed Income
            </div>
            <div className="p-3 rounded-lg bg-[#181B24] border border-[#F43F5E]/20 text-[#DBDEE1]">
              ✕ No Guaranteed Projects
            </div>
            <div className="p-3 rounded-lg bg-[#181B24] border border-[#F43F5E]/20 text-[#DBDEE1]">
              ✕ No Guaranteed Clients
            </div>
          </div>

          <div className="p-4 rounded-xl bg-[#181B24] border border-[#232736] text-xs sm:text-sm text-[#DBDEE1] text-center max-w-3xl mx-auto leading-relaxed">
            “We provide the environment, systems, structure, and opportunities to keep building. What you do with them depends on your contribution, availability, skills, and the opportunities available.”
          </div>
        </div>

        {/* 3. WHO SHOULD JOIN VS WHO SHOULD NOT */}
        <div className="space-y-8">
          <div className="text-center max-w-2xl mx-auto space-y-2">
            <span className="text-xs font-mono font-bold text-[#10B981] uppercase tracking-wider">
              ALIGNMENT CHECK
            </span>
            <h3 className="text-2xl sm:text-4xl font-black text-white font-mono uppercase">
              WHO IS THIS FOR?
            </h3>
            <p className="text-xs sm:text-sm text-[#949BA4]">
              The common characteristic: <strong className="text-white">You want to build, not just browse.</strong>
            </p>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            {/* Who is it for */}
            <div className="bg-[#11131A] border border-[#10B981]/30 rounded-2xl p-6 space-y-4">
              <div className="flex items-center gap-2 text-[#10B981] font-bold font-mono text-sm">
                <CheckCircle className="w-4 h-4" />
                <span>WHO SHOULD APPLY</span>
              </div>
              <div className="space-y-2.5">
                {whoFor.map((item, i) => (
                  <div key={i} className="flex items-start gap-2 text-xs text-[#DBDEE1]">
                    <CheckCircle className="w-3.5 h-3.5 text-[#10B981] shrink-0 mt-0.5" />
                    <span>{item}</span>
                  </div>
                ))}
              </div>
            </div>

            {/* Who is it not for */}
            <div className="bg-[#11131A] border border-[#F43F5E]/30 rounded-2xl p-6 space-y-4">
              <div className="flex items-center gap-2 text-[#F43F5E] font-bold font-mono text-sm">
                <XCircle className="w-4 h-4" />
                <span>WHO SHOULD NOT APPLY</span>
              </div>
              <div className="space-y-2.5">
                {whoNotFor.map((item, i) => (
                  <div key={i} className="flex items-start gap-2 text-xs text-[#949BA4]">
                    <XCircle className="w-3.5 h-3.5 text-[#F43F5E] shrink-0 mt-0.5" />
                    <span>{item}</span>
                  </div>
                ))}
              </div>
            </div>
          </div>

          {/* Strong Closing Quote */}
          <div className="text-center max-w-2xl mx-auto p-4 rounded-xl bg-[#181B24] border border-[#232736]">
            <p className="text-xs sm:text-sm text-[#DBDEE1] italic">
              “If you’re only looking for what Jobless Empire can give you, this probably isn’t the place. If you’re interested in what we can build together, keep reading.”
            </p>
          </div>
        </div>

      </div>
    </section>
  );
};
