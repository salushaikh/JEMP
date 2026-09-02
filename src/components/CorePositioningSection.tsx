import React from 'react';
import { ShieldAlert, FileText, CheckCircle, Code2, Users, Compass, Award, GitBranch } from 'lucide-react';

export const CorePositioningSection: React.FC = () => {
  const activities = [
    { icon: Compass, title: 'Exchange Ideas', desc: 'Propose and debate early-stage concepts with other skilled builders.' },
    { icon: Users, title: 'Find Collaborators', desc: 'Form agile squads with complementary skills across code, design, and product.' },
    { icon: Code2, title: 'Build Real Projects', desc: 'Turn abstract concepts into working software, tools, and prototypes.' },
    { icon: Award, title: 'Strengthen Portfolios', desc: 'Replace passive gap months with verifiable project deliverables.' },
    { icon: GitBranch, title: 'Learn by Doing', desc: 'Tackle real architectures, code reviews, and production deployments.' },
    { icon: FileText, title: 'Document Contributions', desc: 'Create an immutable record of every milestone and architectural decision.' }
  ];

  return (
    <section id="core-positioning-section" className="py-16 sm:py-24 border-b border-[#1A1E29] bg-[#0E1017]">
      <div className="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8 space-y-20">
        
        {/* 1. WHAT IS JOBLESS EMPIRE */}
        <div className="space-y-6 text-center max-w-3xl mx-auto">
          <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-[#181B24] border border-[#232736] text-xs font-mono text-[#10B981]">
            <span>01 • THE MISSION</span>
          </div>
          <h2 className="text-3xl sm:text-5xl font-black text-white font-mono tracking-tight uppercase">
            JOBLESS DOESN’T MEAN USELESS.
          </h2>
          <p className="text-base sm:text-lg text-[#DBDEE1] leading-relaxed">
            Jobless Empire is a private community built for people who are unemployed, between opportunities, job searching, building independently, or simply unwilling to stop creating because they currently don’t have a traditional job.
          </p>
        </div>

        {/* Member Capabilities Grid */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
          {activities.map((item, idx) => {
            const Icon = item.icon;
            return (
              <div
                key={idx}
                className="bg-[#11131A] border border-[#232736] rounded-xl p-5 hover:border-[#FFB020]/40 transition-all space-y-2"
              >
                <div className="w-9 h-9 rounded-lg bg-[#181B24] border border-[#232736] flex items-center justify-center text-[#FFB020]">
                  <Icon className="w-5 h-5" />
                </div>
                <h3 className="text-base font-bold text-white">{item.title}</h3>
                <p className="text-xs sm:text-sm text-[#949BA4] leading-relaxed">{item.desc}</p>
              </div>
            );
          })}
        </div>

        {/* Critical Disclaimer Banner */}
        <div className="p-4 sm:p-5 rounded-xl bg-[#181B24]/90 border border-[#232736] flex flex-col sm:flex-row items-start sm:items-center gap-3">
          <ShieldAlert className="w-5 h-5 text-[#FFB020] shrink-0 mt-0.5 sm:mt-0" />
          <div className="text-xs sm:text-sm text-[#DBDEE1]">
            <strong className="text-white">Core Community Principle:</strong> Membership does not guarantee employment, projects, income, or selection for any opportunity. Jobless Empire creates the environment to keep building; outcomes depend on active contribution and capability.
          </div>
        </div>

        {/* 2. THE PROBLEM: THE GAP BETWEEN JOBS IS OFTEN INVISIBLE */}
        <div className="pt-8 border-t border-[#1A1E29] space-y-8">
          <div className="text-center max-w-2xl mx-auto space-y-3">
            <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-[#181B24] border border-[#232736] text-xs font-mono text-[#FFB020]">
              <span>02 • THE PROBLEM</span>
            </div>
            <h2 className="text-2xl sm:text-4xl font-black text-white font-mono tracking-tight uppercase">
              THE GAP BETWEEN JOBS IS OFTEN INVISIBLE.
            </h2>
            <p className="text-sm sm:text-base text-[#949BA4]">
              Someone can spend three months learning, designing, coding, researching, building, and experimenting — yet have little tangible evidence to show for it.
            </p>
          </div>

          {/* Contrast Visualizer: Resume vs Reality */}
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6 max-w-4xl mx-auto">
            {/* The Resume Void */}
            <div className="bg-[#11131A] border border-[#F43F5E]/30 rounded-2xl p-6 space-y-4">
              <div className="flex items-center justify-between pb-3 border-b border-[#232736]">
                <span className="text-xs font-mono font-bold text-[#F43F5E] uppercase tracking-wider">
                  Traditional Resume
                </span>
                <span className="text-[10px] font-mono text-[#949BA4]">The Black Hole</span>
              </div>
              <div className="bg-[#181B24] p-4 rounded-xl border border-[#232736] space-y-2">
                <div className="text-xs font-mono text-[#949BA4]">March 2026 – June 2026</div>
                <div className="text-base font-bold text-white">Unemployed / Career Break</div>
                <p className="text-xs text-[#949BA4] italic">
                  “No verifiable artifacts. Recruiters see a blank gap.”
                </p>
              </div>
            </div>

            {/* The Jobless Empire Reality */}
            <div className="bg-[#11131A] border border-[#10B981]/40 rounded-2xl p-6 space-y-4">
              <div className="flex items-center justify-between pb-3 border-b border-[#232736]">
                <span className="text-xs font-mono font-bold text-[#10B981] uppercase tracking-wider">
                  Jobless Empire Proof-of-Work
                </span>
                <span className="text-[10px] font-mono text-[#10B981]">Documented Reality</span>
              </div>
              <div className="bg-[#181B24] p-4 rounded-xl border border-[#10B981]/30 space-y-2">
                <div className="text-xs font-mono text-[#10B981]">March 2026 – June 2026</div>
                <div className="text-sm font-bold text-white space-y-1">
                  <div className="flex items-center gap-1.5">
                    <CheckCircle className="w-3.5 h-3.5 text-[#10B981]" />
                    <span>Built 3 working prototypes in squads</span>
                  </div>
                  <div className="flex items-center gap-1.5">
                    <CheckCircle className="w-3.5 h-3.5 text-[#10B981]" />
                    <span>Collaborated with 5 skilled builders</span>
                  </div>
                  <div className="flex items-center gap-1.5">
                    <CheckCircle className="w-3.5 h-3.5 text-[#10B981]" />
                    <span>Contributed to open-source developer tool</span>
                  </div>
                  <div className="flex items-center gap-1.5">
                    <CheckCircle className="w-3.5 h-3.5 text-[#10B981]" />
                    <span>Helped deliver real client project milestone</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        {/* 3. PHILOSOPHY: YOUR WORK SHOULD LEAVE A TRAIL */}
        <div className="pt-8 border-t border-[#1A1E29] bg-[#11131A] border border-[#232736] rounded-2xl p-6 sm:p-8 space-y-4 text-center max-w-3xl mx-auto">
          <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-[#181B24] border border-[#232736] text-xs font-mono text-[#FFB020]">
            <span>03 • THE PHILOSOPHY</span>
          </div>
          <h2 className="text-2xl sm:text-3xl font-black text-white font-mono tracking-tight uppercase">
            YOUR WORK SHOULD LEAVE A TRAIL.
          </h2>
          <p className="text-sm sm:text-base text-[#DBDEE1] leading-relaxed">
            Every meaningful contribution can become part of a member’s private proof-of-work Timeline — documenting projects joined, ideas created, brainstorms contributed to, milestones completed, architectural decisions, and external project participation.
          </p>
          <p className="text-xs sm:text-sm text-[#949BA4] max-w-xl mx-auto italic">
            It is not surveillance. It is designed to help members build a credible record of what they have actually done.
          </p>
        </div>

      </div>
    </section>
  );
};
